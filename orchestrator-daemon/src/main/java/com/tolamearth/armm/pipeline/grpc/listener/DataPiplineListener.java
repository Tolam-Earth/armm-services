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

package com.tolamearth.armm.pipeline.grpc.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tolamearth.armm.pipeline.entities.ErrorLog;
import com.tolamearth.armm.pipeline.entities.Summary;
import com.tolamearth.armm.pipeline.entities.TokenAttributesSummary;
import com.tolamearth.armm.pipeline.repository.ErrorLogRepository;
import com.tolamearth.armm.pipeline.repository.SummaryRepository;
import com.tolamearth.armm.pipeline.services.DataPipelineService;
import com.tolamearth.integration.armm.ArmmEvent;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import io.micronaut.gcp.pubsub.annotation.PubSubListener;
import io.micronaut.gcp.pubsub.annotation.Subscription;
import io.micronaut.gcp.pubsub.exception.PubSubMessageReceiverException;
import io.micronaut.gcp.pubsub.exception.PubSubMessageReceiverExceptionHandler;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.persistence.LockModeType;

@PubSubListener
@Primary
public class DataPiplineListener implements PubSubMessageReceiverExceptionHandler {
    public static final String TOKEN_ATTRIBUTES_SUMMARY_NAME = "token-attributes";

    private final Logger log = LoggerFactory.getLogger(DataPiplineListener.class);
    private final DataPipelineService dataPipelineService;
    private final DataPipelinePublisher publisher;
    private final ErrorLogRepository errorLogRepository;
    private final SummaryRepository summaryRepository;
    private final String unlistedListener;

    public DataPiplineListener(DataPipelineService dataPipelineService, DataPipelinePublisher publisher,
                               ErrorLogRepository errorLogRepository,
                               SummaryRepository summaryRepository,
                               @Value("${pipeline.pubsub.topics.unlisted.listener}") String unlistedListener) {
        this.dataPipelineService = dataPipelineService;
        this.publisher = publisher;
        this.errorLogRepository = errorLogRepository;
        this.summaryRepository = summaryRepository;
        this.unlistedListener = unlistedListener;
    }

    @PostConstruct
    public void init() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        TokenAttributesSummary ta = new TokenAttributesSummary();
            Summary summary = summaryRepository.findBySummaryName(TOKEN_ATTRIBUTES_SUMMARY_NAME, LockModeType.OPTIMISTIC);
            if (summary == null) {
                summary = new Summary(null, null, TOKEN_ATTRIBUTES_SUMMARY_NAME, om.writeValueAsString(ta));
                summaryRepository.save(summary);
            }
    }

    /**
     * The method receives tokens, decompose tokens and send each token as a separate message
     * to the claassification topic
     *
     * @param msg {@link com.tolamearth.integration.armm.ArmmEvent}
     * @throws InvalidProtocolBufferException
     *
     */
    @Subscription(value = "${pipeline.pubsub.topics.unlisted.listener}", configuration = "custom") //
    public void onTokenInfoMessage(byte[] msg) throws InvalidProtocolBufferException {
        ArmmEvent armmEvents = ArmmEvent.parseFrom(msg);
        for (ArmmEvent.Transaction event : armmEvents.getTransactionsList()) {
            log.info("**************** {}-{}", event.getNftId().getTokenId(), event.getNftId().getSerialNumber());
            Mono<String> s = publisher.sendToClassifier(event.toByteArray());
            s.subscribe();
        }
    }

    @Subscription(value = "${pipeline.pubsub.topics.classifier.listener}", configuration = "custom") //
    public void onMessageClassifier(byte[] msg) throws InvalidProtocolBufferException {
        ArmmEvent.Transaction transaction = ArmmEvent.Transaction.parseFrom(msg);
        log.info("--------------------{}-{}", transaction.getNftId().getTokenId(), transaction.getNftId().getSerialNumber());
        dataPipelineService.handleTokensPipeline(transaction);
    }

    @Override
    public void handle(PubSubMessageReceiverException exception) {
        byte[] msg;
        String nftId = null;
        if (!(exception.getCause() instanceof InvalidProtocolBufferException)) {
            msg = exception.getState().getPubsubMessage().getData().toByteArray();
            if (exception.getState().getSubscriptionName().getSubscription().equals(unlistedListener)) {
                nftId = "armmEvents";
            } else {
                try {
                    ArmmEvent.Transaction transaction = ArmmEvent.Transaction.parseFrom(msg);
                    nftId = transaction.getNftId().getTokenId() + "-" + transaction.getNftId().getSerialNumber();
                } catch (InvalidProtocolBufferException e) {
                    // should never happen because whole message is already validated
                }
            }
        } else {
            msg = exception.getState().getPubsubMessage().getData().toByteArray();
            log.error("Invalid protobuf format", exception);
        }

        ErrorLog errorLog = new ErrorLog(null, nftId, exception.getMessage(), msg);
        try {
            errorLogRepository.save(errorLog);
        } catch (Exception e) {
            log.error("Error saving errors", e);
        }
        log.error("Original exception", exception);
        exception.getState().getAckReplyConsumer().ack();
    }

}
