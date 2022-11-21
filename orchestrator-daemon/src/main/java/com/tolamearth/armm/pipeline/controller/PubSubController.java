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

package com.tolamearth.armm.pipeline.controller;

import com.google.protobuf.Timestamp;
import com.tolamearth.armm.pipeline.controller.mock.HistoryDetailEntity;
import com.tolamearth.armm.pipeline.controller.mock.HistoryRepository;
import com.tolamearth.armm.pipeline.entities.TokenAttributes;
import com.tolamearth.armm.pipeline.grpc.listener.DataPipelinePublisher;
import com.tolamearth.armm.pipeline.repository.ClassificationDataRepository;
import com.tolamearth.integration.armm.ArmmEvent;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Controller("/pubsub")
public class PubSubController {
    private static final Logger log = LoggerFactory.getLogger(PubSubController.class);

    private final DataPipelinePublisher publisher;
    private final ClassificationDataRepository dataRepository;
    private final HistoryRepository historyRepository;

    public PubSubController(DataPipelinePublisher publisher,
                            ClassificationDataRepository dataRepository,
                            HistoryRepository historyRepository) {
        this.publisher = publisher;
        this.dataRepository = dataRepository;
        this.historyRepository = historyRepository;
    }

    @Post("/publish")
    void publishMsg(@Body TokenDetailsDTO tokenDetailsDTO) {
        log.info("********************************** \n{}", tokenDetailsDTO);
        ArmmEvent.Builder transactionsBuilder = ArmmEvent.newBuilder();
        for (TokenDetailDTO detail : tokenDetailsDTO.getDetails()) {
            Instant instant = detail.getTransactionTime().toInstant(ZoneOffset.UTC);
            Timestamp transactionTime = Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                    .build();

            ArmmEvent.Transaction.Builder transactionBuilder = ArmmEvent.Transaction.newBuilder()
                    .setNftId(
                            ArmmEvent.NftId.newBuilder()
                                    .setTokenId(detail.getTokenId())
                                    .setSerialNumber(detail.getSerialNumber().toString())
                                    .build()
                    )
                    .setEventType(ArmmEvent.EventType.valueOf(detail.getMsgType()))
                    .setTransactionTime(transactionTime)
                    .setTransactionId(detail.getTransactionId());

            if (ArmmEvent.EventType.MINTED.toString().equalsIgnoreCase(detail.getMsgType())) {
                ArmmEvent.TokenDetail tokenDetail = ArmmEvent.TokenDetail.newBuilder()
                        .setCountry(detail.getCountry())
                        .setDeviceId(detail.getDeviceId())
                        .setFirstSubdivision(detail.getFirstSubdivision())
                        .setGuardianId(detail.getGuardianId())
                        .setOwner(detail.getMintingOwner())
                        .setQuality(detail.getQuality()!=null?detail.getQuality():0L)
                        .setVintageYear(detail.getVintageYear())
                        .setProjectCategory(detail.getProjectCategory())
                        .setTransactionMemo(detail.getTransactionMemo())
                        .setProjectType(detail.getProjectType())
                        .build();
                transactionBuilder.setTokenDetail(tokenDetail);
            } else if (ArmmEvent.EventType.LISTED.toString().equalsIgnoreCase(detail.getMsgType())) {
                ArmmEvent.TokenState tokenState = ArmmEvent.TokenState.newBuilder()
                        .setOwner(detail.getOwner())
                        .setListingPrice(detail.getListingPrice())
                        .build();
                transactionBuilder.setTokenState(tokenState);
            } else if (ArmmEvent.EventType.PURCHASED.toString().equalsIgnoreCase(detail.getMsgType())) {
                ArmmEvent.TokenState tokenState = ArmmEvent.TokenState.newBuilder()
                        .setOwner(detail.getNewOwner())
                        .setListingPrice(detail.getListingPrice())
                        .setPurchasePrice(detail.getPurchasePrice())
                        .build();
                transactionBuilder.setTokenState(tokenState);
            }
            transactionsBuilder.addTransactions(transactionBuilder.build());
        }
        ArmmEvent armmEvent = transactionsBuilder.build();
        byte[] msg = armmEvent.toByteArray();

        saveHistory(armmEvent);
        publisher.sendUnlisted(msg);
    }

    @Transactional
    protected void saveHistory(ArmmEvent armmEvent) {
        for (ArmmEvent.Transaction transaction : armmEvent.getTransactionsList()) {
            long price = 0L;
            String owner = null;
            if (ArmmEvent.EventType.MINTED.equals(transaction.getEventType())) {
                List<HistoryDetailEntity> details = historyRepository.findByTokenIdAndSerialNumber(transaction.getNftId().getTokenId(), Long.parseLong(transaction.getNftId().getSerialNumber()));
                if (details.stream().anyMatch(hd -> ArmmEvent.EventType.MINTED.toString().equals(hd.getMsgType()))) {
                    return;
                }
                owner = transaction.getTokenDetail().getOwner();
            } else if (ArmmEvent.EventType.LISTED.equals(transaction.getEventType())) {
                price = transaction.getTokenState().getListingPrice();
                owner = transaction.getTokenState().getOwner();
            } else if (ArmmEvent.EventType.PURCHASED.equals(transaction.getEventType())) {
                price = transaction.getTokenState().getPurchasePrice();
                owner = transaction.getTokenState().getOwner();
            }

            HistoryDetailEntity historyDetailEntity = new HistoryDetailEntity(
                    null,
                    transaction.getNftId().getTokenId(),
                    Long.parseLong(transaction.getNftId().getSerialNumber()),
                    "0.0.1234" + "@" + (1000),
                    transaction.getTransactionTime().getSeconds(),
                    transaction.getEventType().toString(),
                    owner,
                    price
            );
            historyRepository.save(historyDetailEntity);
        }
    }

    @Get("/{id}")
    HttpResponse<TokenDetailDTO> getById(@PathVariable("id") String nftId) {
        Optional<TokenAttributes> classification = dataRepository.findByNftId(nftId);
        if (classification.isPresent()) {
            return HttpResponse.ok(new TokenDetailDTOBuilder()
                    .setId(classification.get().getId().toString())
                    .setTokenId(classification.get().getNftId())
                    .createTokenDetailDTO());
        }
        return HttpResponse.notFound();
    }
}
