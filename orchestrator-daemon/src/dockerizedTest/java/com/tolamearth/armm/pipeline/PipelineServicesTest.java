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

package com.tolamearth.armm.pipeline;

import com.tolamearth.armm.pipeline.controller.TokenDetailDTO;
import com.tolamearth.armm.pipeline.controller.TokenDetailDTOBuilder;
import com.tolamearth.armm.pipeline.controller.TokenDetailsDTO;
import com.tolamearth.armm.pipeline.controller.mock.TokenTransactionHistoryController;
import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.pipeline.dtos.prices.PriceInfo;
import com.tolamearth.armm.pipeline.dtos.prices.PricingResponse;
import com.tolamearth.armm.pipeline.dtos.prices.TokenId;
import com.tolamearth.armm.pipeline.services.ClassificationServiceClient;
import com.tolamearth.armm.pipeline.services.DataTransformerClient;
import com.tolamearth.armm.pipeline.client.PricingClient;
import com.tolamearth.armm.pipeline.services.TokenHistoryClient;
import com.tolamearth.integration.armm.ArmmEvent;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.h2.tools.Server;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
class PipelineServicesTest extends PubSubSpec {
    public static final String TOKEN_ID_1 = "0.0.0";
    public static final String TOKEN_ID_2 = "1.1.1";
    public static final Long SERIAL_NUMBER_1 = 1L;
    public static final Long SERIAL_NUMBER_2 = 2L;
    public static final String CONCAT_NFT_ID_1 = TOKEN_ID_1 + "-" + SERIAL_NUMBER_1;
    public static final String CONCAT_NFT_ID_2 = TOKEN_ID_2 + "-" + SERIAL_NUMBER_2;
    private static final Logger log = LoggerFactory.getLogger(PipelineServicesTest.class);
    @Inject
    TokenHistoryClient historyClient;

    @Inject
    TokenTransactionHistoryController historyController;

    @Inject
    DataTransformerClient dataTransformerClient;

    @Inject
    ClassificationServiceClient classificationServiceClient;
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
    void testPublishMintedMessage(TestInfo testInfo) {
        System.out.println("************  Execute test: " + testInfo.getDisplayName());
        generateTestData("2.2.2-11");
        prepareMocks("2.2.2", 11L);

        TokenDetailDTO detailDTO = prepareTokenDetailRequest(ArmmEvent.EventType.MINTED.toString());

        publishMessage(detailDTO);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<TokenDetailDTO> result = getTokenDetailDTOHttpResponse("2.2.2-11");
        Assertions.assertEquals("2.2.2-11", result.getBody().get().getTokenId());
    }

    @Test
    void testPublishListedMessage(TestInfo testInfo) {
        System.out.println("************  Execute test: " + testInfo.getDisplayName());
        generateTestData(CONCAT_NFT_ID_1);
        prepareMocks(TOKEN_ID_1, SERIAL_NUMBER_1);

        TokenDetailDTO detailDTO = prepareListedPurchaseRequest(ArmmEvent.EventType.LISTED.toString(), Boolean.FALSE);

        publishMessage(detailDTO);
        //Thread.sleep(100000000);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<TokenDetailDTO> result = getTokenDetailDTOHttpResponse(CONCAT_NFT_ID_1);
        Assertions.assertEquals(CONCAT_NFT_ID_1, result.getBody().get().getTokenId());
    }

    @Test
    void testPublishPurchasedMessage(TestInfo testInfo) {
        System.out.println("************  Execute test: " + testInfo.getDisplayName());
        generateTestData(CONCAT_NFT_ID_2);

        prepareMocks(TOKEN_ID_2, SERIAL_NUMBER_2);

        TokenDetailDTO detailDTO = prepareListedPurchaseRequest(ArmmEvent.EventType.PURCHASED.toString(), Boolean.TRUE);

        publishMessage(detailDTO);
        //Thread.sleep(100000000);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<TokenDetailDTO> result = getTokenDetailDTOHttpResponse(CONCAT_NFT_ID_2);
        Assertions.assertEquals(CONCAT_NFT_ID_2, result.getBody().get().getTokenId());
    }

    private HttpResponse<TokenDetailDTO> getTokenDetailDTOHttpResponse(String nftId) {
        var getRequest = HttpRequest.GET("/pubsub/" + nftId);
        return blockingClient.exchange(getRequest, TokenDetailDTO.class);
    }

    private void publishMessage(TokenDetailDTO detailDTO) {
        List<TokenDetailDTO> tokenDetailDTOS = List.of(detailDTO);
        TokenDetailsDTO details = new TokenDetailsDTO(tokenDetailDTOS);
        HttpRequest<TokenDetailsDTO> request = HttpRequest.POST("/pubsub/publish", details);
        blockingClient.exchange(request);
    }

    private static TokenDetailDTO prepareTokenDetailRequest(String msgType) {
        return new TokenDetailDTOBuilder()
                .setMsgType(msgType)
                .setTokenId("2.2.2")
                .setSerialNumber(11L)
                .setTransactionId(UUID.randomUUID().toString())
                .setTransactionTime(LocalDateTime.of(2022, 8, 1, 0, 0))
                .setCountry("USA")
                .setDeviceId("Solar Cell")
                .setFirstSubdivision("1")
                .setGuardianId("24234234")
                .setMintingOwner("New Owner 1")
                .setVintageYear(2000L)
                .setQuality(7L)
                .setProjectCategory("Kategory super")
                .setTransactionMemo("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi lacus ex, euismod vitae cursus vel, congue quis massa. Nullam auctor ante ac mattis maximus. Phasellus tincidunt maximus elit sit amet malesuada. In hac habitasse platea dictumst. Proin imperdiet ultrices eros nec fringilla. Proin interdum mauris a dui tempus, quis rutrum dolor porta. Quisque sed eros sed mauris gravida vehicula eget commodo arcu. Duis dui ex, efficitur at ultricies ac, molestie non enim. Vivamus fermentum elit et pharetra tempus. Praesent consectetur vulputate dolor id ornare.")
                .setProjectType("Type 1")
                .createTokenDetailDTO();
    }

    private static TokenDetailDTO prepareListedPurchaseRequest(String msgType, boolean isPurchase) {
        return new TokenDetailDTOBuilder()
                .setMsgType(msgType)
                .setTokenId(isPurchase ? TOKEN_ID_2 : TOKEN_ID_1)
                .setSerialNumber(isPurchase ? SERIAL_NUMBER_2 : SERIAL_NUMBER_1)
                .setTransactionId(UUID.randomUUID().toString())
                .setTransactionTime(LocalDateTime.of(2022, 8, 1, 0, 0))
                .setOwner("New Owner 1")
                .setNewOwner("New Owner 1")
                .setListingPrice(100L)
                .setPurchasePrice(isPurchase ? 120L : null)
                .createTokenDetailDTO();
    }

    private void prepareMocks(String tokenId, Long serialNumber) {
        when(pricingClient.getPrices(any())).thenReturn(
                new PricingResponse(
                        List.of(new TokenId(new NftId(tokenId, serialNumber))),
                        List.of(new PriceInfo(CONCAT_NFT_ID_1, TOKEN_ID_1, SERIAL_NUMBER_1, 10L, 15L, null, null))
                ));
        when(historyClient.getTokensHistory(tokenId, serialNumber, "TEST"))
                .thenReturn(historyController.getTokensHistory(tokenId, serialNumber, "TEST"));
        when(dataTransformerClient.transformData(any())).thenReturn(new DataTransformerClient.TransformationResponse(
                List.of(new NftId(tokenId, serialNumber)),
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

        when(classificationServiceClient.classify(any())).thenReturn(
                new ClassificationServiceClient.ClassificationResponse(
                        List.of(new NftId(tokenId, serialNumber)),
                        List.of("0.0.9401@1602138343.335616988"),
                        List.of(new ClassificationServiceClient.HederaTimestamp(1602138343L, 335616988L)),
                        List.of(UUID.randomUUID()),
                        List.of("RENEW_ENERGY-SOLAR-CHN-bb898c7c-45f4-4e9a-a35f-d7dcb0f684cd"),
                        "v1.1.1-202208311301"
                )
        );

    }


    public void generateTestData(String nftId) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:mem:armmdb;DB_CLOSE_ON_EXIT=TRUE;MODE=PostgreSQL", "sa", "");
            Instant instant = LocalDateTime.of(2022, 8, 1, 0, 0)
                    .toInstant(ZoneOffset.UTC);
            Statement s = connection.createStatement();
            s.execute("delete from token_attributes where nft_id = '" + nftId + "'");
            s.execute("insert into token_attributes (nft_id, avg_price, country, first_subdivision, last_price, " +
                    "latitude, longitude, minting_owner, name_pool, nft_age, nft_state, num_owners, " +
                    "num_price_chg, owner, pooling_version, project_category, project_type, token_pool_id, " +
                    "vintage_year, id, transaction_id, transaction_time) values ('" + nftId + "', 10.2, 'USA', 'USA', 10, 12.555, 15.666, 'USA', 'USA', 5, 'MINTED', 2, 2, " +
                    "'USA', 'USA', 'USA', 'USA', 11111, 1998, random_uuid(), random_uuid(), "+instant.toEpochMilli()+")");
            s.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @MockBean(PricingClient.class)
    PricingClient getPricingClient() {
        return mock(PricingClient.class);
    }

    @MockBean(TokenHistoryClient.class)
    TokenHistoryClient tokenHistoryClient() {
        return mock(TokenHistoryClient.class);
    }

    @MockBean(DataTransformerClient.class)
    DataTransformerClient dataTransformerClient() {
        return mock(DataTransformerClient.class);
    }

    @MockBean(ClassificationServiceClient.class)
    ClassificationServiceClient classificationServiceClient() {
        return mock(ClassificationServiceClient.class);
    }
}
