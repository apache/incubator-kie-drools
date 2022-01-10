/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.addon.cloudevents;

import java.util.List;
import java.util.stream.Collectors;

import org.kie.kogito.event.ChannelType;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.event.Topic;
import org.kie.kogito.event.TopicDiscovery;
import org.kie.kogito.event.cloudevents.CloudEventMeta;

/**
 * Base class for events Topic Discovery
 */
public abstract class AbstractTopicDiscovery implements TopicDiscovery {

    public static final Topic DEFAULT_INCOMING_CHANNEL = new Topic(KogitoEventStreams.INCOMING, ChannelType.INCOMING);
    public static final Topic DEFAULT_OUTGOING_CHANNEL = new Topic(KogitoEventStreams.OUTGOING, ChannelType.OUTGOING);

    protected abstract List<Topic> getTopics();

    @Override
    public List<Topic> getTopics(final List<CloudEventMeta> events) {
        final List<Topic> topics = getTopics();
        if (events == null || events.isEmpty()) {
            return topics;
        }
        // guarantees that we have CONSUMED or PRODUCED events with respective channels
        events.forEach(e -> {
            if (e.getKind() == EventKind.CONSUMED && topics.stream().noneMatch(t -> t.getType() == ChannelType.INCOMING)) {
                topics.add(DEFAULT_INCOMING_CHANNEL);
            } else if (e.getKind() == EventKind.PRODUCED && topics.stream().noneMatch(t -> t.getType() == ChannelType.OUTGOING)) {
                topics.add(DEFAULT_OUTGOING_CHANNEL);
            }
        });
        topics.forEach(t -> {
            if (t.getType() == ChannelType.INCOMING) {
                t.setEventsMeta(events.stream().filter(e -> e.getKind() == EventKind.CONSUMED).collect(Collectors.toList()));
            } else {
                t.setEventsMeta(events.stream().filter(e -> e.getKind() == EventKind.PRODUCED).collect(Collectors.toList()));
            }
        });
        return topics;
    }
}
