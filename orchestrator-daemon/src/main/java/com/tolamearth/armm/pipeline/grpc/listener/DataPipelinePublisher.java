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

import io.micronaut.gcp.pubsub.annotation.PubSubClient;
import io.micronaut.gcp.pubsub.annotation.Topic;
import reactor.core.publisher.Mono;

@PubSubClient
public interface DataPipelinePublisher {

    @Topic("${pipeline.pubsub.topics.unlisted.name}")
    void sendUnlisted(byte[] data);
    @Topic("${pipeline.pubsub.topics.classifier.name}")
    Mono<String> sendToClassifier(byte[] data);

    @Topic("${pipeline.pubsub.topics.marketplace.name}")
    void sendToMarketplaceState(byte[] data);

}
