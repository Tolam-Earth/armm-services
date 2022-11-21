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

import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.pricing.controller.client.PriceModelClient;
import com.tolamearth.armm.pipeline.dtos.prices.PriceInfo;
import com.tolamearth.armm.pipeline.dtos.prices.TokenId;
import com.tolamearth.armm.pricing.repository.ClassificationDataRepository;
import com.tolamearth.armm.pricing.repository.ModelRepository;
import com.tolamearth.armm.pricing.repository.PoolMetaRepository;
import com.tolamearth.armm.pricing.repository.PricingRequestRepository;
import com.tolamearth.armm.pipeline.entities.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PricingServiceTest {

    public static final long ENDPOINT_ID = 1L;
    private PricingService pricingService;
    private PriceModelClient priceModelClient;
    private PoolMetaRepository poolMetaRepository;
    private ClassificationDataRepository classificationDataRepository;
    private PricingRequestRepository pricingRequestRepository;
    private ModelRepository modelRepository;

    @BeforeEach
    void setup() {
        priceModelClient = mock(PriceModelClient.class);
        poolMetaRepository = mock(PoolMetaRepository.class);
        classificationDataRepository = mock(ClassificationDataRepository.class);
        pricingRequestRepository = mock(PricingRequestRepository.class);
        modelRepository = mock(ModelRepository.class);
        pricingService = new PricingServiceImpl(priceModelClient, poolMetaRepository, classificationDataRepository, pricingRequestRepository, modelRepository);
    }

    @AfterEach
    void cleanup() {
        pricingService = null;
    }

    /**
     * Positive scenario.
     * Three tokens
     * all tokens in classification table
     * grouped in one group
     */
    @Test
    void tokensInOneGroup() {
        List<TokenId> tokenIds = prepareTokenIds();
        UUID poolId = UUID.randomUUID();
        PoolMeta poolMeta = new PoolMeta(
                poolId,
                "1",
                2L,
                "pool1",
                List.of("4"),
                List.of("cp1"),
                List.of(BigDecimal.valueOf(100)),
                List.of(BigDecimal.valueOf(50)),
                List.of(BigDecimal.valueOf(25)),
                List.of(BigDecimal.valueOf(22)),
                11L,
                0.5
        );
        createMocksForOneGroup(tokenIds, poolId, poolMeta);
        List<PriceInfo> tokenPrices = pricingService.getTokenPrices(tokenIds);
        assertEquals(tokenPrices.size(), 3);
        verify(poolMetaRepository, times(1)).findAll();
        verify(classificationDataRepository, times(1)).findByNftIdInList(any());
        verify(priceModelClient, times(1)).getPriceFromModel(any());
        verify(modelRepository, times(1)).findByEndpointId(any());
        verify(pricingRequestRepository, times(1)).save(any());
    }

    /**
     * Positive scenario.
     * Three tokens
     * all tokens in classification table
     * grouped in two groups
     */
    @Test
    void tokensInTwoGroups() {
        List<TokenId> parameters = prepareTokenIds();
        UUID poolId1 = UUID.randomUUID();
        UUID poolId2 = UUID.randomUUID();
        List<PoolMeta> poolMetas = createTwoPoolMetas(poolId1, poolId2);
        createMocks(parameters, poolId1, poolId2, poolMetas, true);
        List<PriceInfo> tokenPrices = pricingService.getTokenPrices(parameters);
        assertEquals(tokenPrices.size(), 3);

        verify(poolMetaRepository, times(1)).findAll();
        verify(classificationDataRepository, times(1)).findByNftIdInList(any());
        verify(priceModelClient, times(2)).getPriceFromModel(any());
        verify(modelRepository, times(2)).findByEndpointId(any());
        verify(pricingRequestRepository, times(1)).save(any());
    }

    /**
     * Negative scenario.
     * Three tokens
     * no tokens in classification table
     * grouped in two groups
     */
    @Test
    void noTokensFound() {
        List<TokenId> parameters = prepareTokenIds();
        UUID poolId1 = UUID.randomUUID();
        UUID poolId2 = UUID.randomUUID();
        List<PoolMeta> poolMetas = createTwoPoolMetas(poolId1, poolId2);
        //createMocks(parameters, poolId1, poolId2, poolMetas);
        when(poolMetaRepository.findAll()).thenReturn(poolMetas);
        when(poolMetaRepository.findById(any())).thenReturn(Optional.of(poolMetas.get(0)));
        List<String> stringifyTokenIds = stringifyTokenIds(parameters);
        when(classificationDataRepository.findByNftIdInList(stringifyTokenIds))
                .thenReturn(List.of());
        List<PriceInfo> tokenPrices = pricingService.getTokenPrices(parameters);
        assertEquals(tokenPrices.size(), 3);
        assertEquals(tokenPrices.stream().filter(tp -> "BAD_NFT_ID".equals(tp.code())).toList().size(), 3);

        verify(poolMetaRepository, times(1)).findAll();
        verify(classificationDataRepository, times(1)).findByNftIdInList(any());
        verify(priceModelClient, times(0)).getPriceFromModel(any());
    }

    /**
     * Negative scenario.
     * Three tokens
     * some tokens in classification table
     * grouped in two groups
     */
    @Test
    void someTokensFound() {
        List<TokenId> parameters = prepareTokenIds();
        UUID poolId1 = UUID.randomUUID();
        UUID poolId2 = UUID.randomUUID();
        List<PoolMeta> poolMetas = createTwoPoolMetas(poolId1, poolId2);
        createMocks(parameters, poolId1, poolId2, poolMetas, false);
        List<PriceInfo> tokenPrices = pricingService.getTokenPrices(parameters);
        assertEquals(tokenPrices.size(), 3);
        assertEquals((int) tokenPrices.stream().filter(tp -> "BAD_NFT_ID".equals(tp.code())).count(), 2);
        assertEquals((int) tokenPrices.stream().filter(tp -> (tp.minPrice() != null && tp.minPrice() > 0)).count(), 1);

        verify(poolMetaRepository, times(1)).findAll();
        verify(classificationDataRepository, times(1)).findByNftIdInList(any());
        verify(modelRepository, times(1)).findByEndpointId(any());
        verify(priceModelClient, times(1)).getPriceFromModel(any());
    }


    private void createMocksForOneGroup(List<TokenId> tokenIds, UUID poolId, PoolMeta poolMeta) {
        when(poolMetaRepository.findAll()).thenReturn(List.of(poolMeta));
        when(poolMetaRepository.findById(any())).thenReturn(Optional.of(poolMeta));
        List<String> stringifyTokenIds = stringifyTokenIds(tokenIds);
        when(classificationDataRepository.findByNftIdInList(stringifyTokenIds))
                .thenReturn(prepareReturnTokenAttributes(stringifyTokenIds, poolId));
        when(priceModelClient.getPriceFromModel(any())).thenReturn(new PriceModelClient.PriceResponse(10L, 20L));
        when(pricingRequestRepository.save(any())).thenReturn(new PricingRequest());
        when(modelRepository.findByEndpointId(any())).thenReturn(List.of(
                new ModelVersion(
                        null, "model version 1", true, true, ENDPOINT_ID, new Date().getTime(), new Date().getTime(),
                        new Model(
                                null, "model 1", "type 1", new Date().getTime()
                        )
                )));
    }

    @NotNull
    private static List<TokenId> prepareTokenIds() {
        return Arrays.asList(
                new TokenId(new NftId("0.0.1", 1L)),
                new TokenId(new NftId("0.0.1", 2L)),
                new TokenId(new NftId("0.0.1", 3L))
        );
    }


    private void createMocks(List<TokenId> tokenIds, UUID poolId1, UUID poolId2, List<PoolMeta> poolMetas, boolean returnForTwoGroups) {
        when(poolMetaRepository.findAll()).thenReturn(poolMetas);
        when(poolMetaRepository.findById(any())).thenReturn(Optional.of(poolMetas.get(0)));
        List<String> stringifyTokenIds = stringifyTokenIds(tokenIds);
        when(classificationDataRepository.findByNftIdInList(stringifyTokenIds))
                .thenReturn(
                        returnForTwoGroups ?
                                prepareReturnTokenAttributesForTwoGroups(stringifyTokenIds, poolId1, poolId2) :
                                prepareReturnTokenAttributesSomeFound(stringifyTokenIds, poolId1, poolId2));
        when(priceModelClient.getPriceFromModel(
                new PriceModelClient.PriceRequest(ENDPOINT_ID, poolId1, 1, getPoolsFromPoolMeta(poolMetas))))
                .thenReturn(new PriceModelClient.PriceResponse(10L, 20L));
        when(priceModelClient.getPriceFromModel(
                new PriceModelClient.PriceRequest(ENDPOINT_ID, poolId2, 2, getPoolsFromPoolMeta(poolMetas))))
                .thenReturn(new PriceModelClient.PriceResponse(30L, 40L));
        when(pricingRequestRepository.save(any())).thenReturn(new PricingRequest());
        when(modelRepository.findByEndpointId(any())).thenReturn(List.of(
                new ModelVersion(
                        null, "model version 1", true, true, ENDPOINT_ID, new Date().getTime(), new Date().getTime(),
                        new Model(
                                null, "model 1", "type 1", new Date().getTime()
                        )
                )));
    }

    @NotNull
    private static List<PoolMeta> createTwoPoolMetas(UUID poolId1, UUID poolId2) {
        return Arrays.asList(new PoolMeta(
                        poolId1,
                        "1",
                        2L,
                        "pool1",
                        List.of("4"),
                        List.of("cp1"),
                        List.of(BigDecimal.valueOf(100)),
                        List.of(BigDecimal.valueOf(50)),
                        List.of(BigDecimal.valueOf(25)),
                        List.of(BigDecimal.valueOf(22)),
                        11L,
                        0.5

                ),
                new PoolMeta(
                        poolId2,
                        "1",
                        2L,
                        "pool1",
                        List.of("4"),
                        List.of("cp1"),
                        List.of(BigDecimal.valueOf(100)),
                        List.of(BigDecimal.valueOf(50)),
                        List.of(BigDecimal.valueOf(25)),
                        List.of(BigDecimal.valueOf(22)),
                        11L,
                        0.5

                ));
    }



    private List<TokenAttributes> prepareReturnTokenAttributesSomeFound(List<String> stringifyTokenIds, UUID poolId1, UUID poolId2) {
        List<TokenAttributes> tokenAttributes = new ArrayList<>();
        stringifyTokenIds = stringifyTokenIds.subList(1, stringifyTokenIds.size() - 1);
        int i = 0;
        for (String stringifyTokenId : stringifyTokenIds) {
            tokenAttributes.add(
                    getTokenClassification(i == 0 ? poolId1 : poolId2, stringifyTokenId)
            );
            i++;
        }
        return tokenAttributes;
    }


    private List<PriceModelClient.Pool> getPoolsFromPoolMeta(List<PoolMeta> poolMetas) {
        List<PriceModelClient.Pool> pool = new ArrayList<>();
        poolMetas.forEach(p -> pool.add(new PriceModelClient.Pool(
                p.getId().toString(), p.getVersionPool(), p.getDtPool(), p.getNamePool(), p.getAttributesPool(), p.getCategoryPool(),
                p.getMeanPool(), p.getMedianPool(), p.getVarPool(), p.getStdevPool(), p.getnPool(), p.getWeight()
        )));
        return pool;
    }

    private List<TokenAttributes> prepareReturnTokenAttributesForTwoGroups(List<String> stringifyTokenIds, UUID poolId1, UUID poolId2) {
        List<TokenAttributes> tokenAttributes = new ArrayList<>();
        int i = 0;
        for (String stringifyTokenId : stringifyTokenIds) {
            tokenAttributes.add(
                    getTokenClassification(i == 0 ? poolId1 : poolId2, stringifyTokenId)
            );
            i++;
        }
        return tokenAttributes;
    }

    @NotNull
    private static TokenAttributes getTokenClassification(UUID poolId, String stringifyTokenId) {
        return new TokenAttributes(
                UUID.randomUUID(),
                stringifyTokenId,
                "fakeTokenId",
                88L,
                "12343",
                getTransactionTime(),
                null, null, null,
                null, null, null,
                1900L, 100L, 1,
                BigDecimal.TEN, BigDecimal.TEN, 1, null,
                poolId, null, null, BigDecimal.valueOf(12.5555), BigDecimal.valueOf(15.6666)
        );
    }

    private static Long getTransactionTime() {
        return Instant.now().toEpochMilli();
    }

    private List<TokenAttributes> prepareReturnTokenAttributes(List<String> stringifyTokenIds, UUID poolId) {
        List<TokenAttributes> tokenAttributes = new ArrayList<>();
        for (String stringifyTokenId : stringifyTokenIds) {
            tokenAttributes.add(
                    new TokenAttributes(
                            UUID.randomUUID(),
                            stringifyTokenId,
                            "fakeTokenId",
                            99L,
                            "12343",
                            getTransactionTime(),
                            null, null, null,
                            null, null, null,
                            1900L, 100L, 1,
                            BigDecimal.TEN, BigDecimal.TEN, 1, null,
                            poolId, null, null, BigDecimal.valueOf(12.5555), BigDecimal.valueOf(15.6666)
                    )
            );
        }
        return tokenAttributes;
    }

    private List<String> stringifyTokenIds(List<TokenId> parameters) {
        return parameters.stream().map(PricingServiceImpl::getConcatenatedTokenIdAndSerialNumber).collect(Collectors.toList());
    }


}
