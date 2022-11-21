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

package com.tolamearth.armm.pricing.controller;

import com.tolamearth.armm.pricing.controller.exceptions.MaxRequestLengthExceededException;
import com.tolamearth.armm.pricing.service.PricingService;
import com.tolamearth.armm.pricing.service.PricingServiceImpl;
import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.pipeline.dtos.prices.*;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@MicronautTest
public class PricingControllerTest {


    @Inject
    private PricingServiceImpl pricingService;

    @Inject
    @Client("/")
    private HttpClient httpClient;

    BlockingHttpClient blockingClient = null;

    @BeforeEach
    public void setup() {
        blockingClient = httpClient.toBlocking();
    }


    @Test
    void getTokenPriceInfo() {
        when(pricingService.getTokenPrices(any()))
                .thenReturn(List.of(new PriceInfo("123", "123", 99L, 100L, 200L, null, null)));

        HttpRequest<?> request = HttpRequest.POST("/armm/v1/price",
                new PricingRequestDTO(
                        Collections.singletonList(new TokenId(new NftId("0.0.0.1", 123L)))
                ));
        HttpResponse<PricingResponse> exchange = blockingClient.exchange(request, PricingResponse.class);
        Assertions.assertEquals(200, exchange.getStatus().getCode());
        verify(pricingService, times(1)).getTokenPrices(any());
    }

    // TODO exceptions tests
    @Test
    void getTokenPriceInfoErrors207() {
        when(pricingService.getTokenPrices(any()))
                .thenReturn(Arrays.asList(
                        new PriceInfo("123", "123", 99L, 100L, 200L, null, null),
                        new PriceInfo("123", "123", 88L, 101L, 201L, "NOT OK", null)
                ));

        HttpRequest<?> request = HttpRequest.POST("/armm/v1/price",
                new PricingRequestDTO(
                        Collections.singletonList(new TokenId(new NftId("0.0.0.1", 123L)))
                ));
        HttpResponse<PricingResponse> exchange = blockingClient.exchange(request, PricingResponse.class);
        Assertions.assertEquals(207, exchange.getStatus().getCode());

    }

    @Test
    void getTokenPriceInfoErrors1001() {
        when(pricingService.getTokenPrices(List.of(new TokenId(new NftId("0.0.0.1", null)))))
                .thenReturn(Arrays.asList(
                        new PriceInfo("123", "123", 99L, 100L, 200L, null, null),
                        new PriceInfo("123", "123", 88L, 101L, 201L, "NOT OK", null)
                ));
        HttpRequest<?> request = HttpRequest.POST("/armm/v1/price",
                new PricingRequestDTO(
                        Collections.singletonList(new TokenId(new NftId("0.0.0.1", null)))
                ));
        ErrorResponse errorResponse = new ErrorResponse(new ErrorResponse.ErrorInfo("1001", "Missing required field"));
        try {
            blockingClient.exchange(request);
        } catch (HttpClientResponseException e) {
            Assertions.assertEquals(400, e.getStatus().getCode());
            Assertions.assertEquals(errorResponse, e.getResponse().getBody(ErrorResponse.class).get());
        }

        when(pricingService.getTokenPrices(List.of(new TokenId(new NftId(null, 123L)))))
                .thenReturn(Arrays.asList(
                        new PriceInfo("123", "123", 99L, 100L, 200L, null, null),
                        new PriceInfo("123", "123", 88L, 101L, 201L, "NOT OK", null)
                ));
        request = HttpRequest.POST("/armm/v1/price",
                new PricingRequestDTO(
                        Collections.singletonList(new TokenId(new NftId(null, 123L)))
                ));
        errorResponse = new ErrorResponse(new ErrorResponse.ErrorInfo("1001", "Missing required field"));
        try {
            blockingClient.exchange(request);
        } catch (HttpClientResponseException e) {
            Assertions.assertEquals(400, e.getStatus().getCode());
            Assertions.assertEquals(errorResponse, e.getResponse().getBody(ErrorResponse.class).get());
        }

        when(pricingService.getTokenPrices(List.of(new TokenId(new NftId(null, null)))))
                .thenReturn(Arrays.asList(
                        new PriceInfo("123", "123", 99L, 100L, 200L, null, null),
                        new PriceInfo("123", "123", 88L,101L, 201L, "NOT OK", null)
                ));
        request = HttpRequest.POST("/armm/v1/price",
                new PricingRequestDTO(
                        Collections.singletonList(new TokenId(new NftId(null, null)))
                ));
        errorResponse = new ErrorResponse(new ErrorResponse.ErrorInfo("1001", "Missing required field"));
        try {
            blockingClient.exchange(request);
        } catch (HttpClientResponseException e) {
            Assertions.assertEquals(400, e.getStatus().getCode());
            Assertions.assertEquals(errorResponse, e.getResponse().getBody(ErrorResponse.class).get());
        }
    }

    @Test
    void getTokenPriceInfoErrors1005() {
        when(pricingService.getTokenPrices(any()))
                .thenThrow(new MaxRequestLengthExceededException());
        HttpRequest<?> request = HttpRequest.POST("/armm/v1/price",
                new PricingRequestDTO(
                        Collections.singletonList(new TokenId(new NftId("0.0.0.1", 123L)))
                ));
        ErrorResponse errorResponse = new ErrorResponse(new ErrorResponse.ErrorInfo("1005", "Requested more than maximum"));
        try {
            blockingClient.exchange(request);
        } catch (HttpClientResponseException e) {
            Assertions.assertEquals(400, e.getStatus().getCode());
            Assertions.assertEquals(errorResponse, e.getResponse().getBody(ErrorResponse.class).get());
        }
    }

    @MockBean(PricingService.class)
    PricingServiceImpl pricingService() {
        return mock(PricingServiceImpl.class);
    }

}
