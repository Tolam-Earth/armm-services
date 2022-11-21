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

package com.tolamearth.armm.trader.controller;

import com.tolamearth.armm.pipeline.entities.TraderUserConfig;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static com.tolamearth.armm.pipeline.enums.TraderUserConfigName.BUY_ON;
import static com.tolamearth.armm.pipeline.enums.TraderUserConfigValue.FALSE;
import static com.tolamearth.armm.pipeline.enums.TraderUserConfigValue.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class TraderUserConfigControllerTest {

    @Inject
    @Client("/")
    private HttpClient httpClient;

    @Test
    void testTraderUserConfigBuyOnThenOff() {
        // buy on when no row exists
        HttpRequest requestOn = HttpRequest.POST("/armm/v1/trader/buyOn",null);
        HttpResponse<TraderUserConfig> response = httpClient.toBlocking().exchange(requestOn, TraderUserConfig.class);
        TraderUserConfig traderUserConfig = response.body();
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(BUY_ON, traderUserConfig.getConfigName());
        assertEquals(TRUE, traderUserConfig.getConfigValue());

        // buy off when a row already exists
        HttpRequest requestOff = HttpRequest.POST("/armm/v1/trader/buyOff",null);
        response = httpClient.toBlocking().exchange(requestOff, TraderUserConfig.class);
        TraderUserConfig traderUserConfig2 = response.body();
        // should be same id retrieved again
        assertEquals(traderUserConfig.getId(), traderUserConfig2.getId());
        assertEquals(BUY_ON, traderUserConfig2.getConfigName());
        assertEquals(FALSE, traderUserConfig2.getConfigValue());
    }

    @Test
    void testTraderUserConfigBuyOffThenOn() {
        // Buy Off when no row exists
        HttpRequest requestOff = HttpRequest.POST("/armm/v1/trader/buyOff",null);
        HttpResponse<TraderUserConfig> response = httpClient.toBlocking().exchange(requestOff, TraderUserConfig.class);
        TraderUserConfig traderUserConfig = response.body();
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(BUY_ON, traderUserConfig.getConfigName());
        assertEquals(FALSE, traderUserConfig.getConfigValue());

        // Buy on when a row already exists
        HttpRequest requestOn = HttpRequest.POST("/armm/v1/trader/buyOn",null);
        response = httpClient.toBlocking().exchange(requestOn, TraderUserConfig.class);
        TraderUserConfig traderUserConfig2 = response.body();
        // should be same id retrieved again
        assertEquals(traderUserConfig.getId(), traderUserConfig2.getId());
        assertEquals(BUY_ON, traderUserConfig2.getConfigName());
        assertEquals(TRUE, traderUserConfig2.getConfigValue());
    }
}

