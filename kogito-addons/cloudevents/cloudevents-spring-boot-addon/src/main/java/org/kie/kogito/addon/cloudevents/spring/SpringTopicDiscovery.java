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
package org.kie.kogito.addon.cloudevents.spring;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.addon.cloudevents.AbstractTopicDiscovery;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.event.Topic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpringTopicDiscovery extends AbstractTopicDiscovery {

    // in the future we should be implementation agnostic
    @Value(value = "${kogito.addon.cloudevents.kafka." + KogitoEventStreams.INCOMING + "}")
    String incomingStreamTopic;

    @Value(value = "${kogito.addon.cloudevents.kafka." + KogitoEventStreams.OUTGOING + "}")
    String outgoingStreamTopic;

    @Override
    protected List<Topic> getTopics() {
        final List<Topic> topics = new ArrayList<>();

        if (incomingStreamTopic != null && !incomingStreamTopic.isEmpty()) {
            final Topic incoming = DEFAULT_INCOMING_CHANNEL;
            incoming.setName(incomingStreamTopic);
            topics.add(incoming);
        }

        if (outgoingStreamTopic != null && !outgoingStreamTopic.isEmpty()) {
            final Topic outgoing = DEFAULT_OUTGOING_CHANNEL;
            outgoing.setName(outgoingStreamTopic);
            topics.add(outgoing);
        }

        return topics;
    }
}
