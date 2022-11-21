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

package com.tolamearth.armm.pricing.controller.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Client("pricemodel-node")
public interface PriceModelClient {

    @Post("/arm/v1/price-range")
    PriceResponse getPriceFromModel(@Body PriceRequest assets);

    record PriceRequest(
            @JsonProperty("endpoint_id") Long endpointId,
            @JsonProperty("pool_id") UUID poolId,
            @JsonProperty("n_nft") Integer nNft,
            @JsonProperty("pools") List<Pool> pools
    ) {
    }

    record Pool(
            @JsonProperty("id") String id,
            @JsonProperty("version_pool") String versionPool,
            @JsonProperty("dt_pool") Long dtPool,
            @JsonProperty("name_pool") String namePool,
            @JsonProperty("attributes_pool") List<String> attributesPool,
            @JsonProperty("category_pool") List<String> categoryPool,
            @JsonProperty("mean_pool") List<BigDecimal> meanPool,
            @JsonProperty("median_pool") List<BigDecimal> medianPool,
            @JsonProperty("var_pool") List<BigDecimal> varPool,
            @JsonProperty("stdev_pool") List<BigDecimal> stdevPool,
            @JsonProperty("n_pool") Long nPool,
            @JsonProperty("weight") double pool
    ) {
    }

    record PriceResponse(
            @JsonProperty("min_price_usd_cents") Long minPrice,
            @JsonProperty("max_price_usd_cents") Long maxPrice
    ) {
    }
}
/*
{
    "endpoint_id": 12345,
    "pool_id": 2,
    "n_nft": 20,
    "pools": [
        { "id": 0,
          "name_pool": "pool_0",
          ...
        },
        ...,
        { "id": 7,
          "name_pool": "pool_7",
          ...
        }
    ]
}
 */
