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

package com.tolamearth.armm.trader.controller.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.trader.controller.client.IntegrationBuyClient.BuyRequest;
import com.tolamearth.armm.trader.controller.client.IntegrationBuyClient.IntegrationBuyResponse;
import com.tolamearth.armm.trader.controller.client.IntegrationBuyClient.Asset;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class IntegrationBuyClientTest {

    @Inject
    private ObjectMapper objectMapper;

    @Test
    void testBuyRequestFormatIsCorrect() throws JsonProcessingException {
        // create request object
        BuyRequest buyRequest = mockBuyRequest();

        // verify format
        String jsonString = objectMapper.writeValueAsString(buyRequest);
        String expectedString = "{\"account_id\":\"myAccountId\",\"asset\":{\"nft_id\":{\"token_id\":\"myNftId\",\"serial_number\":100},\"price\":199}}";
        assertEquals(expectedString, jsonString);
    }

    @Test
    void testIntegrationBuyResponseFormatIsCorrect() throws JsonProcessingException {
        // create response object
        IntegrationBuyResponse integrationBuyResponse = new IntegrationBuyResponse("SOMETHING");

        // verify format
        String jsonString = objectMapper.writeValueAsString(integrationBuyResponse);
        String expectedString = "{\"status\":\"SOMETHING\"}";
        assertEquals(expectedString, jsonString);
    }

    private BuyRequest mockBuyRequest() {
        NftId nftId = new NftId("myNftId", 100L);
        Asset asset = new Asset(nftId, 199L);
        BuyRequest buyRequest = new BuyRequest("myAccountId", asset);
        return buyRequest;
    }
}
