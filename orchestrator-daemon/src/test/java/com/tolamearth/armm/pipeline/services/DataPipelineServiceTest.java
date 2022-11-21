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
import com.google.protobuf.Timestamp;
import com.tolamearth.armm.pipeline.client.PricingClient;
import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.pipeline.dtos.prices.PriceInfo;
import com.tolamearth.armm.pipeline.dtos.prices.PricingResponse;
import com.tolamearth.armm.pipeline.dtos.prices.TokenId;
import com.tolamearth.armm.pipeline.entities.Summary;
import com.tolamearth.armm.pipeline.entities.TokenAttributes;
import com.tolamearth.armm.pipeline.entities.TokenAttributesSummary;
import com.tolamearth.armm.pipeline.exceptions.ArmmPricingClientException;
import com.tolamearth.armm.pipeline.grpc.listener.DataPipelinePublisher;
import com.tolamearth.armm.pipeline.repository.ClassificationDataRepository;
import com.tolamearth.armm.pipeline.repository.SummaryRepository;
import com.tolamearth.integration.armm.ArmmEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataPipelineServiceTest {

    private final ObjectMapper om = new ObjectMapper();
    private TokenHistoryClient historyClient;
    private DataTransformerClient dataTransformerClient;
    private ClassificationServiceClient classificationServiceClient;
    private ClassificationDataRepository classificationDataRepository;
    private DataPipelinePublisher dataPipelinePublisher;

    private DataPipelineService dataPipelineService;
    private PricingClient pricingClient;
    private SummaryRepository summaryRepository;

    @BeforeEach
    void setup() {
        historyClient = mock(TokenHistoryClient.class);
        dataTransformerClient = mock(DataTransformerClient.class);
        classificationDataRepository = mock(ClassificationDataRepository.class);
        classificationServiceClient = mock(ClassificationServiceClient.class);
        dataPipelinePublisher = mock(DataPipelinePublisher.class);
        pricingClient = mock(PricingClient.class);
        summaryRepository = mock(SummaryRepository.class);
        dataPipelineService = new DataPipelineService(historyClient, dataTransformerClient, classificationServiceClient, classificationDataRepository, dataPipelinePublisher, pricingClient, summaryRepository);
    }

    @Test
    public void testSaveStateMint() throws JsonProcessingException {
        ArmmEvent.Transaction transaction = prepareMocks(ArmmEvent.EventType.MINTED);
        when(summaryRepository.findBySummaryName(any(), any())).thenReturn(new Summary(
                UUID.randomUUID(),
                0,
                "token-attributes",
                om.writeValueAsString(new TokenAttributesSummary())
        ));
        dataPipelineService.handleTokensPipeline(transaction);
        verify(historyClient, times(1)).getTokensHistory(any(), any(), any());
        verify(dataTransformerClient, times(1)).transformData(any());
        verify(classificationDataRepository, times(1)).save(any());
        verify(classificationServiceClient, times(1)).classify(any());
        verify(summaryRepository, times(1)).findBySummaryName(any(), any());
        verify(summaryRepository, times(1)).update(any());
    }

    @Test
    public void testSaveStateListedSuccessful() throws JsonProcessingException {
        ArmmEvent.Transaction transaction = prepareMocks(ArmmEvent.EventType.LISTED);
        when(classificationDataRepository.findByNftId(any())).thenReturn(Optional.of(new TokenAttributes(null, "1.1.1-1", "1.1.1", 1L, "12343", getTransactionTime(), "me", "I", "USA", "1", "2", "3", 1966L, 20L, 3, BigDecimal.TEN, BigDecimal.TEN, 1, "MINT", UUID.randomUUID(), null, null, BigDecimal.valueOf(12.55555), BigDecimal.valueOf(15.6666))));
        when(pricingClient.getPrices(any())).thenReturn(
                new PricingResponse(
                        List.of(new TokenId(new NftId("1.1.1", 1L))),
                        List.of(new PriceInfo("1.1.1-1", "1.1.1", 1L, 10L, 15L, null, null))
                ));
        when(summaryRepository.findBySummaryName(any(), any())).thenReturn(new Summary(
                UUID.randomUUID(),
                0,
                "token-attributes",
                om.writeValueAsString(new TokenAttributesSummary())
        ));

        dataPipelineService.handleTokensPipeline(transaction);

        verify(historyClient, times(1)).getTokensHistory(any(), any(), any());
        verify(dataTransformerClient, times(1)).transformData(any());
        verify(classificationDataRepository, times(1)).findByNftId(any());
        verify(classificationDataRepository, times(1)).save(any());
        verify(pricingClient, times(1)).getPrices(any());
        verify(dataPipelinePublisher, times(1)).sendToMarketplaceState(any());
        verify(classificationServiceClient, times(0)).classify(any());
        verify(summaryRepository, times(1)).findBySummaryName(any(), any());
        verify(summaryRepository, times(1)).update(any());
    }

    @Test
    public void testSaveStateListedWithError() throws JsonProcessingException {
        ArmmEvent.Transaction transaction = prepareMocks(ArmmEvent.EventType.LISTED);
        when(classificationDataRepository.findByNftId(any())).thenReturn(Optional.of(new TokenAttributes(null, "1.1.1-1", "1.1.1", 1L, "12343", getTransactionTime(), "me", "I", "USA", "1", "2", "3", 1966L, 20L, 3, BigDecimal.TEN, BigDecimal.TEN, 1, "MINT", UUID.randomUUID(), null, null, BigDecimal.valueOf(12.55555), BigDecimal.valueOf(15.6666))));
        when(pricingClient.getPrices(any())).thenReturn(
                new PricingResponse(
                        List.of(new TokenId(new NftId("1.1.1", 1L))),
                        List.of(new PriceInfo("1.1.1-1", "1.1.1", 1L, null, null, "POOL_NOT_FOUND", "Pool not found"))
                ));
        when(summaryRepository.findBySummaryName(any(), any())).thenReturn(new Summary(
                UUID.randomUUID(),
                0,
                "token-attributes",
                om.writeValueAsString(new TokenAttributesSummary())
        ));

        assertThrows(ArmmPricingClientException.class, () -> dataPipelineService.handleTokensPipeline(transaction));

        verify(historyClient, times(1)).getTokensHistory(any(), any(), any());
        verify(dataTransformerClient, times(1)).transformData(any());
        verify(classificationDataRepository, times(1)).findByNftId(any());
        verify(classificationDataRepository, times(1)).save(any());
        verify(pricingClient, times(1)).getPrices(any());
        verify(dataPipelinePublisher, times(0)).sendToMarketplaceState(any());
        verify(classificationServiceClient, times(0)).classify(any());
        verify(summaryRepository, times(0)).findBySummaryName(any(), any());
        verify(summaryRepository, times(0)).update(any());
    }

    @Test
    public void testSaveStatePurchase() throws JsonProcessingException {
        ArmmEvent.Transaction transaction = prepareMocks(ArmmEvent.EventType.PURCHASED);
        when(classificationDataRepository.findByNftId(any())).thenReturn(Optional.of(new TokenAttributes(null, "1.1.1-1", "1.1.1", 1L, "12343", getTransactionTime(), "me", "I", "USA", "1", "2", "3", 1966L, 20L, 3, BigDecimal.TEN, BigDecimal.TEN, 1, "MINTED", UUID.randomUUID(), null, null, BigDecimal.valueOf(12.55555), BigDecimal.valueOf(15.6666))));
        when(summaryRepository.findBySummaryName(any(), any())).thenReturn(new Summary(
                UUID.randomUUID(),
                0,
                "token-attributes",
                om.writeValueAsString(new TokenAttributesSummary())
        ));

        dataPipelineService.handleTokensPipeline(transaction);
        verify(historyClient, times(1)).getTokensHistory(any(), any(), any());
        verify(dataTransformerClient, times(1)).transformData(any());
        verify(classificationDataRepository, times(1)).findByNftId(any());
        verify(classificationDataRepository, times(1)).save(any());
        verify(classificationServiceClient, times(0)).classify(any());
        verify(summaryRepository, times(1)).findBySummaryName(any(), any());
        verify(summaryRepository, times(1)).update(any());
    }

    @Test
    public void testSaveNewSummary() throws JsonProcessingException {
        ArmmEvent.Transaction transaction = prepareMocks(ArmmEvent.EventType.MINTED);
        when(summaryRepository.findBySummaryName(any(), any())).thenReturn(null);
        when(summaryRepository.findBySummaryName(any(), any())).thenReturn(new Summary(
                UUID.randomUUID(),
                0,
                "token-attributes",
                om.writeValueAsString(new TokenAttributesSummary())
        ));

        dataPipelineService.handleTokensPipeline(transaction);

        verify(historyClient, times(1)).getTokensHistory(any(), any(), any());
        verify(dataTransformerClient, times(1)).transformData(any());
        verify(classificationDataRepository, times(0)).findByNftId(any());
        verify(classificationDataRepository, times(1)).save(any());
        verify(classificationServiceClient, times(1)).classify(any());
        verify(summaryRepository, times(1)).findBySummaryName(any(), any());
        verify(summaryRepository, times(1)).update(any());

        ArgumentCaptor<Summary> argument = ArgumentCaptor.forClass(Summary.class);
        verify(summaryRepository).update(argument.capture());
        assertEquals("token-attributes", argument.getValue().getSummaryName());
        TokenAttributesSummary attributesSummary = om.readValue(argument.getValue().getSummary(), TokenAttributesSummary.class);
        assertTrue(attributesSummary.getCountries().contains("USA"));
        assertTrue(attributesSummary.getFirstSubdivisions().contains("fd1"));
        assertTrue(attributesSummary.getProjectCategories().contains("pc1"));
        assertTrue(attributesSummary.getProjectTypes().contains("pt1"));
    }

    private Long getTransactionTime() {
        return Instant.now().toEpochMilli();
    }

    private ArmmEvent.Transaction prepareMocks(ArmmEvent.EventType eventType) {
        Instant time = Instant.now();
        ArmmEvent.Transaction transaction = ArmmEvent.Transaction.newBuilder()
                .setNftId(
                        ArmmEvent.NftId.newBuilder().setTokenId("0.0.0").setSerialNumber("1").build()
                )
                .setEventType(eventType)
                .setTransactionId(UUID.randomUUID().toString())
                .setTransactionTime(Timestamp.newBuilder().setSeconds(time.getEpochSecond()).build())
                .setTokenDetail(
                        ArmmEvent.TokenDetail.newBuilder()
                                .setCountry("USA")
                                .setFirstSubdivision("fd1")
                                .setProjectCategory("pc1")
                                .setProjectType("pt1")
                                .build()
                )
                .build();
        when(historyClient.getTokensHistory(any(), any(), any())).thenReturn(new TokenHistoryClient.TokenHistoryResponse(
                new NftId("0.0.0", 1L),
                new ArrayList<>()));
        when(dataTransformerClient.transformData(any())).thenReturn(new DataTransformerClient.TransformationResponse(
                List.of(new NftId("0.0.0", 1L)),
                List.of("1.0.0.1", "1.0.0.2"),
                List.of(1L, 2L),
                List.of(1L, 2L),
                List.of(BigDecimal.valueOf(2.15), BigDecimal.valueOf(5.1)),
                List.of(10L, 12L),
                List.of(2L, 3L),
                List.of(ArmmEvent.EventType.LISTED.toString(), ArmmEvent.EventType.PURCHASED.toString()),
                List.of(BigDecimal.valueOf(2.151212), BigDecimal.valueOf(5.1232323)),
                List.of(BigDecimal.valueOf(2.151212), BigDecimal.valueOf(5.1232323))
        ));
        when(classificationDataRepository.findByNftId(any())).thenReturn(Optional.of(new TokenAttributes(
                UUID.randomUUID(), "0.0.0-1", "0.0.0", 1L, "12343", 123L, "O1", "O1", "USA", "MD", "1", "1", 1900L, 0L,
                1, BigDecimal.ZERO, BigDecimal.ZERO, 1, "MINTED", UUID.randomUUID(), "1", "1", BigDecimal.ZERO, BigDecimal.ZERO)
        ));
        when(classificationDataRepository.save(any())).thenReturn(new TokenAttributes());
        when(classificationServiceClient.classify(any())).thenReturn(
                new ClassificationServiceClient.ClassificationResponse(
                        List.of(new NftId("0.0.0", 1L)),
                        List.of("1"),
                        List.of(new ClassificationServiceClient.HederaTimestamp(time.getEpochSecond(), 0L)),
                        List.of(UUID.randomUUID()),
                        List.of("RENEW_ENERGY-SOLAR-CHN-bb898c7c-45f4-4e9a-a35f-d7dcb0f684cd"),
                        "v1.1.1-202208311301"
                ));
        return transaction;
    }

}
