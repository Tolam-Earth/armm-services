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
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;

import java.util.List;

@Client("history-node")
public interface TokenHistoryClient {

    @Get("/integration/v1/offsets/{token_id},{serial_num}/transactions")
    TokenHistoryResponse getTokensHistory(
            @PathVariable("token_id") String tokenId,
            @PathVariable("serial_num") Long serialNum,
            @Header("x-msg-type") String msgType);


    record TokenHistoryResponse(
            @JsonProperty("nft_id") NftId nftId,
            List<HistoryDetail> transactions
    ) {
    }
    record HistoryDetail(
            @JsonProperty("transaction_id") String transactionId,
            @JsonProperty("transaction_time") String transactionTime, // Hedera Timestamp
            @JsonProperty("msg_type") String msgType,
            @JsonProperty("owner") String owner,
            @JsonProperty("price") Long price
    ){}

}
