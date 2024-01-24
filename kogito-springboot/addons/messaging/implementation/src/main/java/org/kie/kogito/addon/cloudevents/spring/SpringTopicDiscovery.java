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
package org.kie.kogito.addon.cloudevents.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.kogito.addon.cloudevents.AbstractTopicDiscovery;
import org.kie.kogito.event.ChannelType;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.event.Topic;
import org.kie.kogito.event.cloudevents.CloudEventMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component("springTopics")
public class SpringTopicDiscovery extends AbstractTopicDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(SpringTopicDiscovery.class);
    private static final String KAFKA_PREFIX = "kogito.addon.cloudevents.kafka.";
    private static final String INCOMING_PREFIX = KAFKA_PREFIX + KogitoEventStreams.INCOMING;
    private static final String OUTGOING_PREFIX = KAFKA_PREFIX + KogitoEventStreams.OUTGOING;

    @Autowired
    private Environment env;

    @Autowired(required = false)
    private List<CloudEventMeta> cloudEventMetaList = Collections.emptyList();

    public Set<String> getIncomingTopics() {
        return getTopics(INCOMING_PREFIX, KogitoEventStreams.INCOMING, EventKind.CONSUMED);
    }

    public Set<String> getOutgoingTopics() {
        return getTopics(OUTGOING_PREFIX, KogitoEventStreams.OUTGOING, EventKind.PRODUCED);
    }

    @Override
    protected List<Topic> getTopics() {
        List<Topic> topics = new ArrayList<>();
        for (String topic : getIncomingTopics()) {
            topics.add(new Topic(topic, ChannelType.INCOMING));
        }
        for (String topic : getOutgoingTopics()) {
            topics.add(new Topic(topic, ChannelType.OUTGOING));
        }
        logger.debug("Using this list of topics {}", topics);
        return topics;
    }

    private Set<String> getTopics(String prefix, String defaultChannel, EventKind eventKind) {
        final String defaultChannelName = env.getProperty(prefix, defaultChannel);
        Set<String> topics = cloudEventMetaList.stream().filter(c -> c.getKind().equals(eventKind)).map(CloudEventMeta::getType).map(this::parserTopicType)
                .map(t -> env.getProperty(prefix + "." + t, defaultChannelName))
                .collect(Collectors.toSet());
        if (topics.isEmpty()) {
            logger.debug("Using default channel name {}", defaultChannelName);
            topics.add(defaultChannelName);
        }
        return topics;
    }

    private String parserTopicType(String topic) {
        int indexOf = topic.lastIndexOf('.');
        return indexOf == -1 ? topic : topic.substring(indexOf + 1);
    }
}
