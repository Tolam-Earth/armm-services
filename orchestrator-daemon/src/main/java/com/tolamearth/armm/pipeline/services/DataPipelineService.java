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

package com.tolamearth.armm.pipeline.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tolamearth.armm.pipeline.client.PricingClient;
import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.pipeline.dtos.TransactionTime;
import com.tolamearth.armm.pipeline.dtos.prices.PricingRequestDTO;
import com.tolamearth.armm.pipeline.dtos.prices.PricingResponse;
import com.tolamearth.armm.pipeline.dtos.prices.TokenId;
import com.tolamearth.armm.pipeline.entities.Summary;
import com.tolamearth.armm.pipeline.entities.TokenAttributes;
import com.tolamearth.armm.pipeline.entities.TokenAttributesSummary;
import com.tolamearth.armm.pipeline.exceptions.ArmmMissingMintedTokenException;
import com.tolamearth.armm.pipeline.exceptions.ArmmPricingClientException;
import com.tolamearth.armm.pipeline.grpc.listener.DataPipelinePublisher;
import com.tolamearth.armm.pipeline.repository.ClassificationDataRepository;
import com.tolamearth.armm.pipeline.repository.SummaryRepository;
import com.tolamearth.armm.trader.MarketplaceState;
import com.tolamearth.integration.armm.ArmmEvent;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Singleton
public class DataPipelineService {
    public static final String TOKEN_ATTRIBUTES_SUMMARY_NAME = "token-attributes";
    final Logger log = LoggerFactory.getLogger(DataPipelineService.class);

    private final ObjectMapper om = new ObjectMapper();

    private final TokenHistoryClient historyClient;
    private final DataTransformerClient transformerClient;
    private final ClassificationServiceClient classificationServiceClient;
    private final ClassificationDataRepository classificationDataRepository;
    private final DataPipelinePublisher dataPipelinePublisher;
    private final PricingClient pricingClient;
    private final SummaryRepository summaryRepository;

    public DataPipelineService(TokenHistoryClient historyClient,
                               DataTransformerClient transformerClient,
                               ClassificationServiceClient classificationServiceClient,
                               ClassificationDataRepository classificationDataRepository,
                               DataPipelinePublisher dataPipelinePublisher,
                               PricingClient pricingClient,
                               SummaryRepository summaryRepository) {
        this.historyClient = historyClient;
        this.transformerClient = transformerClient;
        this.classificationServiceClient = classificationServiceClient;
        this.classificationDataRepository = classificationDataRepository;
        this.dataPipelinePublisher = dataPipelinePublisher;
        this.pricingClient = pricingClient;
        this.summaryRepository = summaryRepository;
    }

    public void handleTokensPipeline(ArmmEvent.Transaction transaction) {
        AttributesDTO attributesDTO = executePipelineLogic(transaction);
        saveSummaryData(attributesDTO.dbTokenAttribute, attributesDTO.preparedTokenAttributes);
    }

    @Transactional
    protected AttributesDTO executePipelineLogic(ArmmEvent.Transaction transaction) {
        String assetId = transaction.getNftId().getTokenId() + "-" + transaction.getNftId().getSerialNumber();
        TokenHistoryClient.TokenHistoryResponse tokenHistoryResponse;
        tokenHistoryResponse = historyClient.getTokensHistory(
                transaction.getNftId().getTokenId(),
                Long.valueOf(transaction.getNftId().getSerialNumber()),
                transaction.getEventType().toString()
        );
        if (tokenHistoryResponse == null) {
            tokenHistoryResponse = new TokenHistoryClient.TokenHistoryResponse(
                    new NftId(transaction.getNftId().getTokenId(), Long.valueOf(transaction.getNftId().getSerialNumber())), Collections.emptyList());
        }
        log.debug("History response: \n{}", List.of(tokenHistoryResponse));
        TokenAttributes tokenAttributes = getTokenAttributes(transaction, assetId);
        DataTransformerClient.TransformationRequest transformationRequest = getTransformationRequest(transaction, tokenHistoryResponse, tokenAttributes);
        log.debug("Transformed request: \n{}", List.of(transformationRequest));
        DataTransformerClient.TransformationResponse transformedData = transformerClient.transformData(List.of(transformationRequest));
        log.debug("Transformed response: \n{}", transformedData);

        ClassificationServiceClient.ClassificationResponse classificationResponse = null;
        if (ArmmEvent.EventType.MINTED.equals(transaction.getEventType())) {
            ClassificationServiceClient.ClassificationRequest classificationRequest = prepareRequest(transformedData, transaction, tokenAttributes);
            log.debug("Classification request: \n{}", classificationRequest);
            classificationResponse = classificationServiceClient.classify(classificationRequest);
            log.debug("Classification response: \n{}", classificationResponse);
        }

        TokenAttributes preparedTokenAttributes = prepareTokenAttributes(assetId,
                tokenAttributes,
                transformedData,
                transaction,
                classificationResponse
        );

        classificationDataRepository.save(preparedTokenAttributes);
        sendInfoToTraderAgent(transaction, tokenAttributes);
        return new AttributesDTO(tokenAttributes, preparedTokenAttributes);
    }

    private record AttributesDTO(
            TokenAttributes dbTokenAttribute,
            TokenAttributes preparedTokenAttributes
    ) {}


    protected void saveSummaryData(TokenAttributes tokenAttributes, TokenAttributes pta) {
        TokenAttributes ta;
        if (tokenAttributes == null) {
            ta = pta;
        } else {
            ta = tokenAttributes;
        }
        boolean summarySaved = false;
        int loopCounter = 0;
        while (!summarySaved && loopCounter < 10) {
            try {
                Summary summary = summaryRepository.findBySummaryName(TOKEN_ATTRIBUTES_SUMMARY_NAME, LockModeType.OPTIMISTIC);
                if (summary != null) {
                    TokenAttributesSummary as = checkExistingSummaries(pta, ta, summary);
                    if (as != null) {
                        summary.setSummary(om.writeValueAsString(as));
                        summaryRepository.update(summary);
                    }
                    summarySaved = true;
                } else {
                    log.error("Summary record for {} must exist", TOKEN_ATTRIBUTES_SUMMARY_NAME);
                }
            } catch (JsonProcessingException e) {
                log.error("", e);
            } catch (OptimisticLockException l) {
            }
            loopCounter++;
        }
    }

    private TokenAttributesSummary checkExistingSummaries(TokenAttributes pta, TokenAttributes ta, Summary summary) throws JsonProcessingException {
        boolean saveSummary = false;
        TokenAttributesSummary as = om.readValue(summary.getSummary(), TokenAttributesSummary.class);
        if (!as.getCountries().contains(ta.getCountry())) {
            as.getCountries().add(ta.getCountry());
            saveSummary = true;
        }
        if (!as.getFirstSubdivisions().contains(ta.getFirstSubdivision())) {
            as.getFirstSubdivisions().add(ta.getFirstSubdivision());
            saveSummary = true;
        }
        if (!as.getProjectCategories().contains(ta.getProjectCategory())) {
            as.getProjectCategories().add(ta.getProjectCategory());
            saveSummary = true;
        }
        if (!as.getProjectTypes().contains(ta.getProjectType())) {
            as.getProjectTypes().add(ta.getProjectType());
            saveSummary = true;
        }
        if (!as.getNftStates().contains(ta.getNftState())) {
            as.getNftStates().add(ta.getNftState());
            saveSummary = true;
        }
        TokenAttributesSummary.NumPair np = exceedsTheRange(as.getVintageYears(), pta.getVintageYear());
        if (np != null) {
            as.setVintageYears(np);
            saveSummary = true;
        }
        np = exceedsTheRange(as.getNumOwners(), pta.getNumOwners().longValue());
        if (np != null) {
            as.setNumOwners(np);
            saveSummary = true;
        }
        np = exceedsTheRange(as.getNftAge(), pta.getNftAge());
        if (np != null) {
            as.setNftAge(np);
            saveSummary = true;
        }

        np = exceedsTheRange(as.getNftAge(), pta.getNftAge());
        if (np != null) {
            as.setNftAge(np);
            saveSummary = true;
        }

        TokenAttributesSummary.NumPairBigDecimal npbd = exceedsTheRange(as.getAvgPrice(), pta.getAvgPrice());
        if (npbd != null) {
            as.setAvgPrice(npbd);
            saveSummary = true;
        }

        np = exceedsTheRange(as.getLastPrice(), pta.getLastPrice().longValue());
        if (np != null) {
            as.setLastPrice(np);
            saveSummary = true;
        }

        np = exceedsTheRange(as.getNumPriceChg(), pta.getNumPriceChg().longValue());
        if (np != null) {
            as.setNumPriceChg(np);
            saveSummary = true;
        }

        npbd = exceedsTheRange(as.getLatitude(), pta.getLatitude());
        if (npbd != null) {
            as.setLatitude(npbd);
            saveSummary = true;
        }

        npbd = exceedsTheRange(as.getLatitude(), pta.getLatitude());
        if (npbd != null) {
            as.setLatitude(npbd);
            saveSummary = true;
        }

        npbd = exceedsTheRange(as.getLongitude(), pta.getLongitude());
        if (npbd != null) {
            as.setLongitude(npbd);
            saveSummary = true;
        }

        return saveSummary ? as : null;
    }

    private TokenAttributesSummary.NumPair exceedsTheRange(TokenAttributesSummary.NumPair minMax, Long value) {
        if (value < minMax.getMinValue() && value > minMax.getMaxValue()) return new TokenAttributesSummary.NumPair(value, value);
        if (value < minMax.getMinValue()) return new TokenAttributesSummary.NumPair(value, minMax.getMaxValue());
        if (value > minMax.getMaxValue()) return new TokenAttributesSummary.NumPair(minMax.getMinValue(), value);
        return null;
    }

    private TokenAttributesSummary.NumPairBigDecimal exceedsTheRange(TokenAttributesSummary.NumPairBigDecimal minMax, BigDecimal value) {
        if (value.compareTo(minMax.getMinValue()) == -1 && value.compareTo(minMax.getMaxValue()) == 1) return new TokenAttributesSummary.NumPairBigDecimal(value, value);
        if (value.compareTo(minMax.getMinValue()) == -1) return new TokenAttributesSummary.NumPairBigDecimal(value, minMax.getMaxValue());
        if (value.compareTo(minMax.getMaxValue()) == 1) return new TokenAttributesSummary.NumPairBigDecimal(minMax.getMinValue(), value);
        return null;
    }


    @NotNull
    private static DataTransformerClient.TransformationRequest getTransformationRequest(ArmmEvent.Transaction transaction, TokenHistoryClient.TokenHistoryResponse tokenHistoryResponse, TokenAttributes tokenAttributes) {
        NftId nftId = new NftId(transaction.getNftId().getTokenId(), Long.valueOf(transaction.getNftId().getSerialNumber()));
        if (Objects.equals(ArmmEvent.EventType.MINTED, transaction.getEventType())) {
            return new DataTransformerClient.TransformationRequest(
                    nftId,
                    List.of(transaction.getEventType().toString()),
                    transaction.getTokenDetail().getCountry(),
                    transaction.getTokenDetail().getFirstSubdivision(),
                    List.of(transaction.getTransactionId()),
                    List.of(new TransactionTime(transaction.getTransactionTime().getSeconds(), (long) transaction.getTransactionTime().getNanos())),
                    List.of(transaction.getTokenDetail().getOwner()),
                    List.of(0L));
        } else {
            return new DataTransformerClient.TransformationRequest(
                    nftId,
                    tokenHistoryResponse.transactions().stream().map(TokenHistoryClient.HistoryDetail::msgType).toList(),
                    tokenAttributes.getCountry(),
                    tokenAttributes.getFirstSubdivision(),
                    tokenHistoryResponse.transactions().stream().map(TokenHistoryClient.HistoryDetail::transactionId).toList(),
                    tokenHistoryResponse.transactions().stream().map(DataPipelineService::getTransactionTime)
                            .toList(),
                    tokenHistoryResponse.transactions().stream().map(TokenHistoryClient.HistoryDetail::owner).toList(),
                    tokenHistoryResponse.transactions().stream().map(TokenHistoryClient.HistoryDetail::price).toList());
        }
    }

    @NotNull
    private static TransactionTime getTransactionTime(TokenHistoryClient.HistoryDetail hd) {
        if (hd.transactionTime() == null || hd.transactionTime().isEmpty()) {
            return new TransactionTime(0L, 0L);
        }
        String[] timeValues = hd.transactionTime().split("\\.");
        return new TransactionTime(Long.parseLong(timeValues[0]), timeValues.length>1?Long.parseLong(timeValues[1]):0L);
    }

    private void sendInfoToTraderAgent(
            ArmmEvent.Transaction transaction,
            TokenAttributes tokenAttributes
    ) throws ArmmPricingClientException {
        if (!ArmmEvent.EventType.LISTED.equals(transaction.getEventType())) return;
        log.info("Prepare for trader: {}", transaction);
        PricingResponse prices = pricingClient.getPrices(
                new PricingRequestDTO(
                        List.of(new TokenId(new NftId(transaction.getNftId().getTokenId(), Long.valueOf(transaction.getNftId().getSerialNumber()))))
                )
        );

        if (null == prices || prices.getPrices().isEmpty() || null != prices.getPrices().get(0).code()) {
            throw new ArmmPricingClientException("Cannot get prices for " + transaction.getNftId().getTokenId() + "-" + transaction.getNftId().getSerialNumber(), null);
        }

        MarketplaceState marketplaceState = MarketplaceState.newBuilder()
                .setEventType(MarketplaceState.EventType.valueOf(transaction.getEventType().toString()))
                .setNftId(
                        MarketplaceState.NftId.newBuilder()
                                .setTokenId(transaction.getNftId().getTokenId())
                                .setSerialNumber(Long.parseLong(transaction.getNftId().getSerialNumber()))
                                .build())
                .setTransactionId(transaction.getTransactionId())
                .setTransactionTime(transaction.getTransactionTime())
                .setOwner(tokenAttributes.getOwner())
                .setListingPrice(transaction.getTokenState().getListingPrice())
                .setMinPrice(prices.getPrices().get(0).minPrice())
                .setMaxPrice(prices.getPrices().get(0).maxPrice())
                .setProjectCategory(tokenAttributes.getProjectCategory())
                .setCountry(tokenAttributes.getCountry())
                .setFirstSubdivision(tokenAttributes.getFirstSubdivision())
                .setLatitude(tokenAttributes.getLatitude().floatValue())
                .setLongitude(tokenAttributes.getLongitude().floatValue())

                .build();
        byte[] stateBinary = marketplaceState.toByteArray();
        log.info("Trader invocation: {}", marketplaceState);
        dataPipelinePublisher.sendToMarketplaceState(stateBinary);
    }

    private TokenAttributes getTokenAttributes(ArmmEvent.Transaction transaction, String assetId) {
        TokenAttributes tClass = null;
        if (!ArmmEvent.EventType.MINTED.equals(transaction.getEventType())) {
            tClass = classificationDataRepository.findByNftId(assetId).orElseThrow(
                    () -> new ArmmMissingMintedTokenException("Minted token should exists " + assetId, new IllegalArgumentException())
            );
        }
        return tClass;
    }


    private TokenAttributes prepareTokenAttributes(String assetId,
                                                   TokenAttributes tokenAttributes,
                                                   DataTransformerClient.TransformationResponse transformationResponse,
                                                   ArmmEvent.Transaction transaction,
                                                   ClassificationServiceClient.ClassificationResponse classResponse) {
        if (ArmmEvent.EventType.MINTED.equals(transaction.getEventType())) {
            TokenAttributes newTokenAttributes = new TokenAttributes(
                    null,
                    assetId,
                    transaction.getNftId().getTokenId(),
                    Long.parseLong(transaction.getNftId().getSerialNumber()),
                    transaction.getTransactionId(),
                    transaction.getTransactionTime().getSeconds(),
                    transaction.getTokenDetail().getOwner(),
                    transaction.getTokenDetail().getOwner(),
                    transaction.getTokenDetail().getCountry(),
                    transaction.getTokenDetail().getFirstSubdivision(),
                    transaction.getTokenDetail().getProjectCategory(),
                    transaction.getTokenDetail().getProjectType(),
                    transaction.getTokenDetail().getVintageYear(),
                    transformationResponse.nftAges().get(0) == 0 ? 1 : transformationResponse.nftAges().get(0),
                    null,
                    null,
                    null,
                    null,
                    transaction.getEventType().toString(),
                    classResponse.tokenPoolIds().get(0),
                    classResponse.namePools().get(0),
                    classResponse.poolingVersion(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
            setTransformedData(newTokenAttributes, transformationResponse);
            //log.debug("Save data: \n{}", tokenAttributes);
            return newTokenAttributes;
        }
        tokenAttributes.setTransactionId(transaction.getTransactionId());
        tokenAttributes.setTransactionTime(transaction.getTransactionTime().getSeconds());
        setTransformedData(tokenAttributes, transformationResponse);

        //log.debug("Save data: \n{}", tAttr);
        return tokenAttributes;
    }

    private static void setTransformedData(TokenAttributes tokenAttributes, DataTransformerClient.TransformationResponse transformationResponse) {
        if (transformationResponse.nftAges() != null) tokenAttributes.setNftAge(transformationResponse.nftAges().get(0));
        if (transformationResponse.numOwners() != null) tokenAttributes.setNumOwners(transformationResponse.numOwners().get(0).intValue());
        if (transformationResponse.avgPrices() != null) tokenAttributes.setAvgPrice(transformationResponse.avgPrices().get(0));
        if (transformationResponse.lastPrices() != null) tokenAttributes.setLastPrice(BigDecimal.valueOf(transformationResponse.lastPrices().get(0)));
        if (transformationResponse.numPriceChgs() != null) tokenAttributes.setNumPriceChg(transformationResponse.numPriceChgs().get(0).intValue());
        if (transformationResponse.latitudes() != null && transformationResponse.latitudes().get(0) != null) tokenAttributes.setLatitude(transformationResponse.latitudes().get(0));
        if (transformationResponse.longitudes() != null && transformationResponse.longitudes().get(0) != null)
            tokenAttributes.setLongitude(transformationResponse.longitudes().get(0));
        if (transformationResponse.nftStates() != null) tokenAttributes.setNftState(transformationResponse.nftStates().get(0));
    }

    private ClassificationServiceClient.ClassificationRequest prepareRequest(DataTransformerClient.TransformationResponse transformationResponse,
                                                                             ArmmEvent.Transaction transaction,
                                                                             TokenAttributes tokenAttributes) {

        if (Objects.equals(ArmmEvent.EventType.MINTED, transaction.getEventType())) {
            return new ClassificationServiceClient.ClassificationRequest(
                    List.of(new NftId(transaction.getNftId().getTokenId(), Long.valueOf(transaction.getNftId().getSerialNumber()))),
                    List.of(transaction.getEventType().toString()),
                    List.of(transaction.getTokenDetail().getOwner()),
                    List.of(transaction.getTransactionId()),
                    List.of(new ClassificationServiceClient.HederaTimestamp(transaction.getTransactionTime().getSeconds(), (long) transaction.getTransactionTime().getNanos())),
                    List.of(transformationResponse.nftAges().get(0)),
                    List.of(transaction.getTokenDetail().getOwner()),
                    List.of(transaction.getTokenDetail().getCountry()),
                    List.of(transaction.getTokenDetail().getFirstSubdivision()),
                    transformationResponse.latitudes().get(0) == null ? List.of(BigDecimal.ZERO) : List.of(transformationResponse.latitudes().get(0)),
                    transformationResponse.longitudes().get(0) == null ? List.of(BigDecimal.ZERO) : List.of(transformationResponse.longitudes().get(0)),
                    List.of(transaction.getTokenDetail().getProjectCategory()),
                    List.of(transaction.getTokenDetail().getProjectType()),
                    List.of(transaction.getTokenDetail().getVintageYear()),
                    List.of(1),
                    List.of(BigDecimal.valueOf(1)),
                    List.of(1L),
                    List.of(1)
            );
        } else {
            return new ClassificationServiceClient.ClassificationRequest(
                    List.of(new NftId(transaction.getNftId().getTokenId(), Long.valueOf(transaction.getNftId().getSerialNumber()))),
                    List.of(transaction.getEventType().toString()),
                    List.of(tokenAttributes.getMintingOwner()),
                    List.of(transaction.getTransactionId()),
                    List.of(new ClassificationServiceClient.HederaTimestamp(transaction.getTransactionTime().getSeconds(), (long) transaction.getTransactionTime().getNanos())),
                    transformationResponse.nftAges(),
                    List.of(tokenAttributes.getOwner()),
                    List.of(tokenAttributes.getCountry()),
                    List.of(tokenAttributes.getFirstSubdivision()),
                    tokenAttributes.getLatitude() != null ? List.of(tokenAttributes.getLatitude()) : List.of(BigDecimal.ZERO),
                    tokenAttributes.getLongitude() != null ? List.of(tokenAttributes.getLongitude()) : List.of(BigDecimal.ZERO),
                    List.of(tokenAttributes.getProjectCategory()),
                    List.of(tokenAttributes.getProjectType()),
                    List.of(tokenAttributes.getVintageYear()),
                    transformationResponse.numOwners().stream().mapToInt(Long::intValue).boxed().toList(),
                    transformationResponse.avgPrices(),
                    transformationResponse.lastPrices(),
                    transformationResponse.numPriceChgs() != null ? transformationResponse.numPriceChgs().stream().mapToInt(Long::intValue).boxed().toList() : List.of()
            );
        }
    }
}
