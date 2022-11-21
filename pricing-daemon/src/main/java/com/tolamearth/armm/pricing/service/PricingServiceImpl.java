/*
 * Copyright 2022 Tolam Earth
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tolamearth.armm.pricing.service;

import com.tolamearth.armm.pricing.controller.client.PriceModelClient;
import com.tolamearth.armm.pipeline.dtos.prices.PriceInfo;
import com.tolamearth.armm.pipeline.dtos.prices.TokenId;
import com.tolamearth.armm.pricing.repository.ClassificationDataRepository;
import com.tolamearth.armm.pricing.repository.ModelRepository;
import com.tolamearth.armm.pricing.repository.PoolMetaRepository;
import com.tolamearth.armm.pricing.repository.PricingRequestRepository;
import com.tolamearth.armm.pipeline.entities.*;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.groupingBy;

@Singleton
public class PricingServiceImpl implements PricingService {
    private static final Logger log = LoggerFactory.getLogger(PricingServiceImpl.class);
    public static final long ENDPOINT_ID = 1;

    private final PriceModelClient priceModelClient;
    private final PoolMetaRepository poolMetaRepository;
    private final ClassificationDataRepository classificationDataRepository;
    private final PricingRequestRepository pricingRequestRepository;
    private final ModelRepository modelRepository;

    public PricingServiceImpl(PriceModelClient priceModelClient,
                              PoolMetaRepository poolMetaRepository,
                              ClassificationDataRepository classificationDataRepository,
                              PricingRequestRepository pricingRequestRepository,
                              ModelRepository modelRepository) {
        this.priceModelClient = priceModelClient;
        this.poolMetaRepository = poolMetaRepository;
        this.classificationDataRepository = classificationDataRepository;
        this.pricingRequestRepository = pricingRequestRepository;
        this.modelRepository = modelRepository;
    }

    @Override
    @Transactional
    public List<PriceInfo> getTokenPrices(List<TokenId> tokenIds) {
        Map<String, TokenId> tokensIdMap = tokenIds.stream().collect(Collectors.toMap(PricingServiceImpl::getConcatenatedTokenIdAndSerialNumber, Function.identity()));
        List<PriceModelClient.Pool> pools = getPools();
        List<TokenAttributes> tokenAttributes = getTokenAttributes(tokenIds);
        Map<UUID, List<TokenAttributes>> groupedTokenAttributes = getGroupedTokenAttributes(tokenAttributes);

        PricingRequest pricingRequest = new PricingRequest();
        // get prices
        List<PriceInfo> result = new ArrayList<>();
        Set<String> tokensWithClassifications = new HashSet<>(); // used for finding
        for (Map.Entry<UUID, List<TokenAttributes>> groupedTokenEntry : groupedTokenAttributes.entrySet()) {
            PoolMeta poolMeta = poolMetaRepository.findById(groupedTokenEntry.getKey()).orElseThrow(RuntimeException::new); // TODO define exception
            PriceModelClient.PriceRequest priceRequest = new PriceModelClient.PriceRequest(ENDPOINT_ID, groupedTokenEntry.getKey(), groupedTokenEntry.getValue().size(), pools);
            log.debug("priceModelClient.getPriceFromModel request\n{}", priceRequest);
            PriceModelClient.PriceResponse priceFromModel = priceModelClient.getPriceFromModel(priceRequest);
            log.debug("priceModelClient.getPriceFromModel response\n{}", priceFromModel);
            List<ModelVersion> modelVersions = modelRepository.findByEndpointId(ENDPOINT_ID);
            if (modelVersions.isEmpty()) throw new IllegalArgumentException("Missing model version");
            // TODO get pool group data from API
            PoolGroup poolGroup = null;
            if (priceFromModel != null) {
                poolGroup = new PoolGroup(null, poolMeta, new ModelResult(
                        null, priceFromModel.minPrice(), priceFromModel.maxPrice(), new Date().getTime(), modelVersions.get(0)
                ), true);
            }

            for (TokenAttributes tokenAttributeWasGrouped : groupedTokenEntry.getValue()) {
                tokensWithClassifications.add(tokenAttributeWasGrouped.getNftId());
                String errorCode = null;
                String errorMessage = null;
                Long minPrice = priceFromModel != null ? priceFromModel.minPrice() : null;
                Long maxPrice = priceFromModel != null ? priceFromModel.maxPrice() : null;
                if (poolGroup != null) {
                    RequestNft requestNft = new RequestNft(
                            null, tokensIdMap.get(tokenAttributeWasGrouped.getNftId()).getNftId().tokenId(),
                            tokensIdMap.get(tokenAttributeWasGrouped.getNftId()).getNftId().serialNumber()
                    );
                    pricingRequest.getNfts().add(requestNft);
                    requestNft.getPoolGroups().add(poolGroup);
                }
                if (minPrice == null || maxPrice == null) {
                    errorCode = "POOL_NOT_FOUND";
                    errorMessage = "Pool not found";
                }
                result.add(
                        new PriceInfo(tokenAttributeWasGrouped.getNftId(),
                                tokenAttributeWasGrouped.getTokenId(),
                                tokenAttributeWasGrouped.getSerialNumber(),
                                minPrice,
                                maxPrice,
                                errorCode,
                                errorMessage)
                );
            }
        }
        result.addAll(findBadTokenId(tokenIds, tokensWithClassifications));
        if (pricingRequest.getNfts() != null && !pricingRequest.getNfts().isEmpty()) {
            pricingRequestRepository.save(pricingRequest);
        }
        return result;
    }

    private List<PriceInfo> findBadTokenId(List<TokenId> tokenIds, Set<String> tokensWithClassifications) {
        List<PriceInfo> priceInfoWithBadId = new ArrayList<>();
        for (TokenId tokenId : tokenIds) {
            String stringTokenId = getConcatenatedTokenIdAndSerialNumber(tokenId);
            if (!tokensWithClassifications.contains(stringTokenId)) {
                priceInfoWithBadId.add(
                        new PriceInfo(stringTokenId,
                                tokenId.getNftId().tokenId(),
                                tokenId.getNftId().serialNumber(),
                                null,
                                null,
                                "BAD_NFT_ID",
                                "Invalid NFT ID")
                );
            }
        }
        return priceInfoWithBadId;
    }

    private static Map<UUID, List<TokenAttributes>> getGroupedTokenAttributes
            (List<TokenAttributes> tokenAttributes) {
        return tokenAttributes.stream()
                .collect(groupingBy(TokenAttributes::getTokenPoolId));
    }

    private List<TokenAttributes> getTokenAttributes(List<TokenId> tokenIds) {
        List<String> tokens = tokenIds.stream().map(PricingServiceImpl::getConcatenatedTokenIdAndSerialNumber).collect(Collectors.toList());
        return classificationDataRepository.findByNftIdInList(tokens);
    }

    @NotNull
    public static String getConcatenatedTokenIdAndSerialNumber(TokenId t) {
        return t.getNftId().tokenId() + "-" + t.getNftId().serialNumber();
    }

    @NotNull
    private List<PriceModelClient.Pool> getPools() {
        List<PriceModelClient.Pool> pool = new ArrayList<>();
        List<PoolMeta> metadata =
                StreamSupport.stream(poolMetaRepository.findAll().spliterator(), false).toList();
        metadata.forEach(p -> pool.add(new PriceModelClient.Pool(
                p.getId().toString(), p.getVersionPool(), p.getDtPool(), p.getNamePool(), p.getAttributesPool(), p.getCategoryPool(),
                p.getMeanPool(), p.getMedianPool(), p.getVarPool(), p.getStdevPool(), p.getnPool(), p.getWeight()
        )));
        return pool;
    }

}
