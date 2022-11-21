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
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Client("classification-node")
public interface ClassificationServiceClient {

    @Post("/arm/v1/classification/scheduled_pool")
    ClassificationResponse classify(@Body ClassificationRequest classificationRequest);


    record ClassificationRequest(
            @JsonProperty("nft_id") List<NftId> nftIds,
            @JsonProperty("nft_state") List<String> nftStates,
            @JsonProperty("minting_owner") List<String> mintingOwners,
            @JsonProperty("transaction_id") List<String> transactionIds,
            @JsonProperty("transaction_time") List<HederaTimestamp> transactionsTime,
            @JsonProperty("nft_age") List<Long> nftAges,
            @JsonProperty("owner") List<String> owners,
            @JsonProperty("country") List<String> countries,
            @JsonProperty("first_subdivision") List<String> firstSubdivisions,
            @JsonProperty("latitude") List<BigDecimal> latitudes,
            @JsonProperty("longitude") List<BigDecimal> longitudes,
            @JsonProperty("project_category") List<String> projectCategories,
            @JsonProperty("project_type") List<String> projectTypes,
            @JsonProperty("vintage_year") List<Long> vintageYears,
            @JsonProperty("num_owners") List<Integer> numOwners,
            @JsonProperty("avg_price") List<BigDecimal> avgPrices,
            @JsonProperty("last_price") List<Long> lastPrices,
            @JsonProperty("num_price_chg") List<Integer> numPriceChgs
    ) {
    }

    record HederaTimestamp(
            @JsonProperty("seconds") Long seconds,
            @JsonProperty("nanos") Long nanos
    ){}

    record ClassificationResponse(
            @JsonProperty("nft_id") List<NftId> nftIds,
            @JsonProperty("transaction_id") List<String> transactionIds,
            @JsonProperty("transaction_time") List<HederaTimestamp> transactionsTime,
            @JsonProperty("token_pool_id") List<UUID> tokenPoolIds,
            @JsonProperty("name_pool") List<String> namePools,
            @JsonProperty("pooling_version") String poolingVersion
    ) {
    }
}

