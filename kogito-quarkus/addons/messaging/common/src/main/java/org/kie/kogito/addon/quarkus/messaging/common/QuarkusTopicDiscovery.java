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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;
import org.kie.kogito.addon.cloudevents.AbstractTopicDiscovery;
import org.kie.kogito.event.ChannelType;
import org.kie.kogito.event.Topic;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Priority(0)
public class QuarkusTopicDiscovery extends AbstractTopicDiscovery {

    private static final String OUTGOING_PREFIX = "mp.messaging.outgoing.";
    private static final String INCOMING_PREFIX = "mp.messaging.incoming.";
    private static final String TOPIC_SUFFIX = ".topic";

    @Override
    protected List<Topic> getTopics() {
        final List<Topic> topics = new ArrayList<>();
        ConfigProvider.getConfig().getPropertyNames().forEach(n -> {
            if (n.startsWith(OUTGOING_PREFIX)) {
                final String topicName = this.extractChannelName(n, OUTGOING_PREFIX);
                if (topics.stream().noneMatch(t -> t.getName().equals(topicName) && t.getType() == ChannelType.OUTGOING)) {
                    final Topic topic = new Topic();
                    topic.setType(ChannelType.OUTGOING);
                    topic.setName(topicName);
                    topics.add(topic);
                }
            } else if (n.startsWith(INCOMING_PREFIX)) {
                final String topicName = this.extractChannelName(n, INCOMING_PREFIX);
                if (topics.stream().noneMatch(t -> t.getName().equals(topicName) && t.getType() == ChannelType.INCOMING)) {
                    final Topic topic = new Topic();
                    topic.setType(ChannelType.INCOMING);
                    topic.setName(topicName);
                    topics.add(topic);
                }
            }
        });
        return topics;
    }

    private String extractChannelName(String property, String prefix) {
        String channelName = property.substring(prefix.length());
        if (channelName.contains(".")) {
            channelName = channelName.substring(0, channelName.indexOf("."));
        }
        final Optional<String> topicName = ConfigProvider.getConfig().getOptionalValue(prefix + channelName + TOPIC_SUFFIX, String.class);
        return topicName.orElse(channelName);
    }
}
