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

package com.tolamearth.armm.trader;

import com.google.protobuf.Timestamp;
import com.tolamearth.armm.pipeline.client.PricingClient;
import com.tolamearth.armm.trader.controller.client.IntegrationBuyClient;
import com.tolamearth.armm.trader.entities.Rules;
import com.tolamearth.armm.trader.publisher.DataPipelinePublisher;
import com.tolamearth.armm.trader.repository.RulesRepository;
import io.micronaut.http.*;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.h2.tools.Server;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static io.micronaut.http.MediaType.TEXT_PLAIN_TYPE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
class TriggerTraderRulesTest extends PubSubSpec {

    @Inject
    private RulesRepository rulesRepository;
    @Inject
    DataPipelinePublisher publisher;
    @Inject
    IntegrationBuyClient integrationBuyClient;
    @Inject
    PricingClient pricingClient;
    @Inject
    EmbeddedApplication<?> application;
    @Inject
    @Client("/")
    private HttpClient httpClient;

    BlockingHttpClient blockingClient = null;

    @BeforeAll
    static void beforeAll() throws SQLException {
        Server.createWebServer().start();
    }


    @BeforeEach
    public void setup() {
        blockingClient = httpClient.toBlocking();
    }

    @Test
    void testSaveRuleFile(TestInfo testInfo) throws IOException {
        System.out.println("************  Execute test: " + testInfo.getDisplayName());
        MultipartBody requestBody = createRuleRequestBody();
        MutableHttpRequest<MultipartBody> request = HttpRequest.POST("/armm/v1/trader/rules/upload?ruleName=test", requestBody).contentType(MediaType.MULTIPART_FORM_DATA_TYPE);
        HttpResponse<String> response = blockingClient.exchange(request, String.class);

        Iterable<Rules> rules = rulesRepository.findAll();
        assertTrue(rules.spliterator().getExactSizeIfKnown() > 0);
        assertEquals(response.getStatus(), HttpStatus.OK);
        assertEquals(MediaType.TEXT_PLAIN, response.getContentType().get().getName());
        assertEquals("Pending", response.getBody().get());
    }

    @NotNull
    private static MultipartBody createRuleRequestBody() throws IOException {
        File tempFile = File.createTempFile("rules", ".xls");
        byte[] data = ArmmPubsubConfig.class.getClassLoader().getResourceAsStream("TraderRulesDemo.xls").readAllBytes();
        Files.write(Path.of(tempFile.getPath()), data, WRITE);
        return MultipartBody.builder()
                .addPart(
                        "file",
                        tempFile.getName(),
                        TEXT_PLAIN_TYPE,
                        tempFile
                ).build();
    }

    @Test
    void testConfigurationRulesFail(TestInfo testInfo) throws IOException {
        System.out.println("************  Execute test: " + testInfo.getDisplayName());
        triggerExecutePurchaseOrder(35L, 15L);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        verify(integrationBuyClient, times(0)).executeBuy(any());
    }

    @Test
    void testConfigurationRulesPass_ButMaxPriceFails(TestInfo testInfo) throws IOException {
        System.out.println("************  Execute test: " + testInfo.getDisplayName());
        triggerExecutePurchaseOrder(12L, 10);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        verify(integrationBuyClient, times(0)).executeBuy(any());
    }

    @Test
    void testConfigurationRulesAndMaxPriceSuccess_Once(TestInfo testInfo) throws IOException {
        System.out.println("************  Execute test: " + testInfo.getDisplayName());
        triggerExecutePurchaseOrder(12L, 15L);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        verify(integrationBuyClient, times(1)).executeBuy(any());
    }

    @Test
    void testConfigurationRulesAndMaxPriceSuccess_Twice(TestInfo testInfo) throws IOException {
        System.out.println("************  Execute test: " + testInfo.getDisplayName());
        triggerExecutePurchaseOrder(12L, 15L);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        triggerExecutePurchaseOrder(35L, 15L);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        verify(integrationBuyClient, times(1)).executeBuy(any());
    }

    private void triggerExecutePurchaseOrder(long listingPrice, long maxPrice) throws IOException {
        when(integrationBuyClient.executeBuy(any())).thenReturn(new IntegrationBuyClient.IntegrationBuyResponse("PENDING"));

        // upload rule file
        MultipartBody requestBody = createRuleRequestBody();
        MutableHttpRequest<MultipartBody> request = HttpRequest.POST("/armm/v1/trader/rules/upload?ruleName=test", requestBody).contentType(MediaType.MULTIPART_FORM_DATA_TYPE);
        HttpResponse<String> response = blockingClient.exchange(request, String.class);
        //Thread.sleep(1000);
        Instant instant = LocalDateTime.of(2022, 8, 1, 0, 0)
                .toInstant(ZoneOffset.UTC);
        Timestamp transactionTime = Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();

        MarketplaceState marketplaceState = MarketplaceState.newBuilder()
                .setTransactionId(UUID.randomUUID().toString())
                .setOwner("0.0.0.1111")
                .setTransactionTime(transactionTime)
                .setEventType(MarketplaceState.EventType.LISTED)
                .setNftId(MarketplaceState.NftId.newBuilder().setTokenId("0.0.0").setSerialNumber(1L).build())
                .setMaxPrice(maxPrice)
                .setMinPrice(15L)
                .setListingPrice(listingPrice)
                .setProjectCategory("TEST")
                .build();
        byte[] mpsByteArray = marketplaceState.toByteArray();
        publisher.sendToMarketplaceState(mpsByteArray);
    }

    @MockBean(IntegrationBuyClient.class)
    IntegrationBuyClient getIntegrationBuyClient() {
        return mock(IntegrationBuyClient.class);
    }

    @MockBean(PricingClient.class)
    PricingClient getPricingClient() {
        return mock(PricingClient.class);
    }
}
