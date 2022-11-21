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

package com.tolamearth.armm.pricing;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class PubSubSpec implements TestPropertyProvider {
    static final String cloudSdkName = "gcr.io/google.com/cloudsdktool/cloud-sdk:emulators";
    static final DockerImageName cloudSdkImage = DockerImageName.parse(cloudSdkName);
    static final PubSubEmulatorContainer pubsubEmulator = new PubSubEmulatorContainer(cloudSdkImage);

    static {
        ArmmPubsubConfig props = null;
        try {
            props = loadProperties();
        } catch (IOException e) {
            System.out.println("Invalid YAML");
            System.exit(1);
        }
        pubsubEmulator.start();
        ManagedChannel channel = ManagedChannelBuilder.forTarget(pubsubEmulator.getEmulatorEndpoint())
                .usePlaintext().build();
        try {
            TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
            CredentialsProvider credentialsProvider = NoCredentialsProvider.create();
            TopicAdminClient topicClient = TopicAdminClient.create(
                    TopicAdminSettings.newBuilder()
                            .setTransportChannelProvider(channelProvider)
                            .setCredentialsProvider(credentialsProvider)
                            .build()
            );
            SubscriptionAdminClient subscriptionAdminClient =
                    SubscriptionAdminClient.create(
                            SubscriptionAdminSettings.newBuilder()
                                    .setTransportChannelProvider(channelProvider)
                                    .setCredentialsProvider(NoCredentialsProvider.create())
                                    .build());

            // unlisted
            TopicName topicName = TopicName.of(props.pricing.pubsub.project.name, props.pricing.pubsub.topics.classificator.name);
            topicClient.createTopic(topicName);
            SubscriptionName subscriptionName = SubscriptionName.of(props.pricing.pubsub.project.name, props.pricing.pubsub.topics.classificator.listener);
            subscriptionAdminClient.createSubscription(subscriptionName, topicName, PushConfig.getDefaultInstance(), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            channel.shutdown();
        }
    }

    private static ArmmPubsubConfig loadProperties() throws IOException {
        Constructor constructor = new Constructor(ArmmPubsubConfig.class);
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(constructor, representer);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader
                .getResourceAsStream("application.yml");
        return yaml.load(inputStream);
    }

    @Override
    @NonNull
    public Map<String, String> getProperties() {
        return Collections.singletonMap("pubsub.emulator.host", pubsubEmulator.getEmulatorEndpoint());
    }

    @AfterAll
    public void shutdown() {
        pubsubEmulator.stop();
    }

}
