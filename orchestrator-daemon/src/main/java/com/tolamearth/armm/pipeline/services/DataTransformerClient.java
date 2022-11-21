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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.pipeline.dtos.TransactionTime;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

import java.math.BigDecimal;
import java.util.List;

@Client("transformer-node")
public interface DataTransformerClient {

    @Post("/arm/v1/data_transformer")
    TransformationResponse transformData(@Body List<TransformationRequest> assets);

    record TransformationRequest(
            @JsonProperty("nft_id") NftId nftId,
            @JsonProperty("msg_type") List<String> msgTypes,
            @JsonProperty("country") String country,
            @JsonProperty("first_subdivision") String firstSubdivision,
            @JsonProperty("transaction_id") List<String> transactionIds,
            @JsonProperty("transaction_time") List<TransactionTime> transactionTimes,
            @JsonProperty("owner") List<String> owners,
            @JsonProperty("price") List<Long> prices
    ) {}

    record TransformationResponse(
            @JsonProperty("nft_id") List<NftId> nftIds,
            @JsonProperty("current_owner") List<String> currentOwners,
            @JsonProperty("nft_age") List<Long> nftAges,
            @JsonProperty("num_owners") List<Long> numOwners,
            @JsonProperty("avg_price") List<BigDecimal> avgPrices,
            @JsonProperty("last_price") List<Long> lastPrices,
            @JsonProperty("num_price_chg") List<Long> numPriceChgs,
            @JsonProperty("nft_state") List<String> nftStates,
            @JsonProperty("latitude") List<BigDecimal> latitudes,
            @JsonProperty("longitude") List<BigDecimal> longitudes
    ) {
    }
}
