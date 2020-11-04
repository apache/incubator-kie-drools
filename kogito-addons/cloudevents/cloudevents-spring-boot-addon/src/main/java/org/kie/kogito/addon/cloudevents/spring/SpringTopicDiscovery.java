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
