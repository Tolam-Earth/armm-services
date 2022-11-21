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

package com.tolamearth.armm.pipeline.controller.mock;

import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.pipeline.services.TokenHistoryClient;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class TokenTransactionHistoryController {

    private final HistoryRepository historyRepository;

    public TokenTransactionHistoryController(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Get("/integration/v1/offsets/{token_id},{serial_num}/transactions")
    public TokenHistoryClient.TokenHistoryResponse getTokensHistory(
            @PathVariable("token_id") String tokenId,
            @PathVariable("serial_num") Long serialNum,
            @Header("x-msg-type") String msgType) {

        List<TokenHistoryClient.HistoryDetail> historyDetails = new ArrayList<>();
        for (HistoryDetailEntity d : historyRepository.findByTokenIdAndSerialNumber(tokenId, serialNum)) {
           addDetail(historyDetails, 1, d.getTransactionTime(), d.getMsgType(), d.getOwner(), d.getPrice());
        }
        return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), historyDetails);


/*
        //////////////////      A
        if ("0.0.48119366".equals(tokenId) && serialNum == 1) {
            List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
            if (ArmmEvent.EventType.MINTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380,ArmmEvent.EventType.MINTED.toString(), "0.0.48116496", 0L);
            }
            if (ArmmEvent.EventType.LISTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.48116496", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.48116496", 2000L);
            }
            if (ArmmEvent.EventType.PURCHASED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.48116496", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.48116496", 2000L);
                addDetail(details, 1, 1659736382,ArmmEvent.EventType.PURCHASED.toString(), "0.0.23", 2001L);
            }
            return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
        }
        if ("0.0.48119366".equals(tokenId) && serialNum == 2) {
            List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
            if (ArmmEvent.EventType.MINTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380,ArmmEvent.EventType.MINTED.toString(), "0.0.48116496", 0L);
            }
            if (ArmmEvent.EventType.LISTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.48116496", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.48116496", 2000L);
            }
            if (ArmmEvent.EventType.PURCHASED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.48116496", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.48116496", 2000L);
                addDetail(details, 1, 1659736382,ArmmEvent.EventType.PURCHASED.toString(), "0.0.23", 2001L);
            }
            return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
        }
        if ("0.0.48119366".equals(tokenId) && serialNum == 3) {
            List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
            if (ArmmEvent.EventType.MINTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380,ArmmEvent.EventType.MINTED.toString(), "0.0.48116496", 0L);
            }
            if (ArmmEvent.EventType.LISTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.48116496", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.48116496", 2000L);
            }
            if (ArmmEvent.EventType.PURCHASED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.48116496", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.48116496", 2000L);
                addDetail(details, 1, 1659736382,ArmmEvent.EventType.PURCHASED.toString(), "0.0.48116498", 2001L);
            }
            return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
        }

        //////////////////      B
        if ("0.0.66260016".equals(tokenId) && serialNum == 1) {
            List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
            if (ArmmEvent.EventType.MINTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380,ArmmEvent.EventType.MINTED.toString(), "0.0.63157514", 0L);
            }
            if (ArmmEvent.EventType.LISTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.63157514", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.63157514", 2000L);
            }
            if (ArmmEvent.EventType.PURCHASED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.63157514", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.63157514", 2000L);
                addDetail(details, 1, 1659736382,ArmmEvent.EventType.PURCHASED.toString(), "0.0.63157514", 2001L);
            }
            return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
        }
        if ("0.0.66260016".equals(tokenId) && serialNum == 2) {
            List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
            if (ArmmEvent.EventType.MINTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380,ArmmEvent.EventType.MINTED.toString(), "0.0.63157514", 0L);
            }
            if (ArmmEvent.EventType.LISTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.63157514", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.63157514", 2000L);
            }
            if (ArmmEvent.EventType.PURCHASED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.63157514", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.63157514", 2000L);
                addDetail(details, 1, 1659736382,ArmmEvent.EventType.PURCHASED.toString(), "0.0.63157514", 2001L);
            }
            return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
        }
        if ("0.0.66260016".equals(tokenId) && serialNum == 3) {
            List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
            if (ArmmEvent.EventType.MINTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380,ArmmEvent.EventType.MINTED.toString(), "0.0.63157514", 0L);
            }
            if (ArmmEvent.EventType.LISTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.63157514", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.63157514", 2000L);
            }
            if (ArmmEvent.EventType.PURCHASED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.63157514", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.63157514", 2000L);
                addDetail(details, 1, 1659736382,ArmmEvent.EventType.PURCHASED.toString(), "0.0.63157514", 2001L);
            }
            return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
        }

        //////////////////      Z
        if ("0.0.86100112".equals(tokenId) && serialNum == 1) {
            List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
            if (ArmmEvent.EventType.MINTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380,ArmmEvent.EventType.MINTED.toString(), "0.0.89346948", 0L);
            }
            if (ArmmEvent.EventType.LISTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.89346948", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.89346948", 1930L);
            }
            if (ArmmEvent.EventType.PURCHASED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.89346948", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.89346948", 1930L);
                addDetail(details, 1, 1659736382,ArmmEvent.EventType.PURCHASED.toString(), "0.0.89346948", 1932L);
            }
            return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
        }
        if ("0.0.86100112".equals(tokenId) && serialNum == 2) {
            List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
            if (ArmmEvent.EventType.MINTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380,ArmmEvent.EventType.MINTED.toString(), "0.0.89346948", 0L);
            }
            if (ArmmEvent.EventType.LISTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.89346948", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.89346948", 1930L);
            }
            if (ArmmEvent.EventType.PURCHASED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.89346948", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.89346948", 1930L);
                addDetail(details, 1, 1659736382,ArmmEvent.EventType.PURCHASED.toString(), "0.0.89346948", 1932L);
            }
            return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
        }
        if ("0.0.86100112".equals(tokenId) && serialNum == 3) {
            List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
            if (ArmmEvent.EventType.MINTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380,ArmmEvent.EventType.MINTED.toString(), "0.0.89346948", 0L);
            }
            if (ArmmEvent.EventType.LISTED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.89346948", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.89346948", 1930L);
            }
            if (ArmmEvent.EventType.PURCHASED.toString().equals(msgType)) {
                addDetail(details, 1, 1659736380, ArmmEvent.EventType.MINTED.toString(), "0.0.89346948", 0L);
                addDetail(details, 1, 1659736381,ArmmEvent.EventType.LISTED.toString(), "0.0.89346948", 1930L);
                addDetail(details, 1, 1659736382,ArmmEvent.EventType.PURCHASED.toString(), "0.0.89346948", 1932L);
            }
            return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
        }


        List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
        Random random = new Random();
        int count = random.ints(1, 21).findFirst().getAsInt();
        for (int i = 0; i < count; i++) {
            addDetail(details, i, generateTransactionTimes(), generateMsgTypes(), generateOwners(), generatePrices());
        }
        return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
*/
    }

    private void addDetail(List<TokenHistoryClient.HistoryDetail> details, int i, long transactionTime, String msgType, String owner, long price) {
        details.add(
                new TokenHistoryClient.HistoryDetail(
                        generateTransactionId(i),
                        ""+transactionTime,
                        msgType,
                        owner,
                        price
                )
        );
    }

/*
    @Get("/integration/v1/offsets/{token_id},{serial_num}/transactions")
    public TokenHistoryClient.TokenHistoryResponse getTokensHistory(@PathVariable("token_id") String tokenId, @PathVariable("serial_num") Long serialNum) {
        List<TokenHistoryClient.HistoryDetail> details = new ArrayList<>();
        Random random = new Random();
        int count = random.ints(1, 21).findFirst().getAsInt();
        for (int i = 0; i < count; i++) {
            details.add(
                    new TokenHistoryClient.HistoryDetail(
                            generateTransactionId(i),
                            generateTransactionTimes(),
                            generateMsgTypes(),
                            generateOwners(),
                            generatePrices()
                    )
            );
        }
        return new TokenHistoryClient.TokenHistoryResponse(new NftId(tokenId, serialNum), details);
    }
*/

    private String generateTransactionId(int i) {
        return "0.0.1234" + "@" + (i * 1000);
    }

    private Long generateTransactionTimes() {
        // start at a random point in time and increment from there
        Random random = new Random();
        return random.nextLong(0, 1660918973);
    }

    private String generateMsgTypes() {
        Random random = new Random();
        return random.nextBoolean() ? "LISTED" : "PURCHASED";
    }

    private String generateOwners() {
        Random random = new Random();
        return "0.0." + random.ints(1000, 10000).findFirst().getAsInt();
    }

    private Long generatePrices() {
        Random random = new Random();
        return random.longs(100, 2000).findFirst().getAsLong();
    }
}
