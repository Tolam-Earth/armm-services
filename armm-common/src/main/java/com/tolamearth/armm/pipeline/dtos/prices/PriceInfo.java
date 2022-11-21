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

package com.tolamearth.armm.pipeline.dtos.prices;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PriceInfo(
        @JsonProperty("nft_id") String nftId,
        @JsonProperty("token_id") String tokenId,
        @JsonProperty("serial_number") Long serial_number,
        @JsonProperty("min_price") Long minPrice,
        @JsonProperty("max_price") Long maxPrice,
        @JsonProperty("code") String code,
        @JsonProperty("message") String message

) {
}
