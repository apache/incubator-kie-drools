/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.addon.quarkus.messaging.common;

import java.util.*;

import org.junit.jupiter.api.*;
import org.kie.kogito.addon.cloudevents.AbstractTopicDiscovery;
import org.kie.kogito.event.ChannelType;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.Topic;
import org.kie.kogito.event.TopicDiscovery;
import org.kie.kogito.event.cloudevents.CloudEventMeta;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

class QuarkusTopicDiscoveryTest {

    @Test
    void verifyTopicsWithPropertiesSet() {
        final List<Topic> expectedTopics = new ArrayList<>();
        expectedTopics.add(new Topic("processedtravellers", ChannelType.OUTGOING));
        expectedTopics.add(new Topic("mycooltopic", ChannelType.INCOMING));

        final TopicDiscovery discovery = getTopicDiscovery(entry("mp.messaging.outgoing.processedtravellers.connector", "quarkus-http"),
                entry("mp.messaging.outgoing.processedtravellers.url", "http://localhost:8080/"),
                entry("mp.messaging.incoming.kogito_incoming_stream.connector", "smallrye-kafka"),
                entry("mp.messaging.incoming.kogito_incoming_stream.topic", "mycooltopic"));

        final List<Topic> topics = discovery.getTopics(Collections.emptyList());
        assertThat(topics).hasSize(2);
        expectedTopics.forEach(e -> assertThat(topics.stream().anyMatch(t -> t.getName().equals(e.getName()) && t.getType() == e.getType())).isTrue());
    }

    @Test
    void verifyTopicsWithPropertiesSameTopic() {
        final List<Topic> expectedTopics = new ArrayList<>();
        expectedTopics.add(new Topic("mycooltopic", ChannelType.OUTGOING));
        expectedTopics.add(new Topic("mycooltopic", ChannelType.INCOMING));

        final TopicDiscovery discovery = getTopicDiscovery(entry("mp.messaging.outgoing.processedtravellers.connector", "quarkus-http"),
                entry("mp.messaging.outgoing.processedtravellers.url", "http://localhost:8080/"),
                entry("mp.messaging.outgoing.processedtravellers.topic", "mycooltopic"),
                entry("mp.messaging.incoming.kogito_incoming_stream.connector", "smallrye-kafka"),
                entry("mp.messaging.incoming.kogito_incoming_stream.topic", "mycooltopic"));

        final List<Topic> topics = discovery.getTopics(Collections.emptyList());
        assertThat(topics).hasSize(2);
        expectedTopics.forEach(e -> assertThat(topics.stream().anyMatch(t -> t.getName().equals(e.getName()) && t.getType() == e.getType())).isTrue());
    }

    @Test
    void verifyTopicsWithNoPropertiesSet() {
        final List<Topic> expectedTopics = new ArrayList<>();
        expectedTopics.add(AbstractTopicDiscovery.DEFAULT_OUTGOING_CHANNEL);
        expectedTopics.add(AbstractTopicDiscovery.DEFAULT_INCOMING_CHANNEL);
        final List<CloudEventMeta> eventsMeta = new ArrayList<>();
        eventsMeta.add(new CloudEventMeta("event1", "", EventKind.CONSUMED));
        eventsMeta.add(new CloudEventMeta("event2", "", EventKind.PRODUCED));

        final TopicDiscovery discovery = new QuarkusTopicDiscovery() {
            Iterable<String> getPropertyNames() {
                return new HashSet<>();
            }

            Optional<String> getOptionalValue(String key) {
                return Optional.empty();
            }
        };
        final List<Topic> topics = discovery.getTopics(eventsMeta);
        assertThat(topics).hasSize(2);
        expectedTopics.forEach(e -> assertThat(topics.stream().anyMatch(t -> t.getName().equals(e.getName()) && t.getType() == e.getType())).isTrue());
    }

    @Test
    void verifyTopicsWithPropertiesAndChannels() {
        final TopicDiscovery discovery = new QuarkusTopicDiscovery() {
            Iterable<String> getPropertyNames() {
                return new HashSet<>();
            }

            Optional<String> getOptionalValue(String key) {
                return Optional.empty();
            }
        };
        final List<Topic> topics = discovery.getTopics(Collections.emptyList());
        assertThat(topics).isEmpty();
    }

    private TopicDiscovery getTopicDiscovery(Map.Entry<String, String>... entries) {
        return new QuarkusTopicDiscovery() {
            final Map<String, String> properties = Map.ofEntries(entries);

            @Override
            Iterable<String> getPropertyNames() {
                return properties.keySet();
            }

            @Override
            Optional<String> getOptionalValue(String key) {
                return Optional.ofNullable(properties.get(key));
            }
        };
    }

}
