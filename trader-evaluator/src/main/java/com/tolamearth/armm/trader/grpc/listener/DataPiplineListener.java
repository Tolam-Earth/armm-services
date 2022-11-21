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

package com.tolamearth.armm.trader.grpc.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tolamearth.armm.trader.service.TraderEvaluator;
import com.tolamearth.armm.trader.service.TraderUserConfigService;
import com.tolamearth.armm.trader.MarketplaceState;
import io.micronaut.context.annotation.Primary;
import io.micronaut.gcp.pubsub.annotation.PubSubListener;
import io.micronaut.gcp.pubsub.annotation.Subscription;
import io.micronaut.gcp.pubsub.exception.PubSubMessageReceiverException;
import io.micronaut.gcp.pubsub.exception.PubSubMessageReceiverExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PubSubListener
@Primary
public class DataPiplineListener implements PubSubMessageReceiverExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(DataPiplineListener.class);
    private final TraderEvaluator traderEvaluator;
    private final TraderUserConfigService traderUserConfigService;

    public DataPiplineListener(TraderEvaluator traderEvaluator, TraderUserConfigService traderUserConfigService) {
        this.traderEvaluator = traderEvaluator;
        this.traderUserConfigService = traderUserConfigService;
    }

    @Subscription("${trader.pubsub.topics.marketplace.listener}")
    public void onMarketplaceMessage(byte[] msg) throws InvalidProtocolBufferException {
        MarketplaceState marketplaceState = MarketplaceState.parseFrom(msg);
        if (traderUserConfigService.isBuyOnActivated()) {
            traderEvaluator.executePurchaseOrder(marketplaceState);
        } else {
            log.info("marketplaceState not processed - BUY_ON is FALSE: " + marketplaceState);
        }
    }


    @Override
    public void handle(PubSubMessageReceiverException exception) {
        log.error("Cannot handle msg: " + exception.getMessage(), exception);
        exception.getState().getAckReplyConsumer().ack();
    }
}
