package org.drools.quarkus.ruleunit.examples.reactive;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySource;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class RuntimeTest {

    @Inject
    @Connector(value = "smallrye-in-memory")
    InMemoryConnector connector;

    @Test
    public void sendEvents() {
        InMemorySource<Event> incomingEvents = connector.source("events");
        InMemorySink<Alert> outgoingAlerts = connector.sink("alerts");

        incomingEvents.send(new Event("temperature", 20));
        incomingEvents.send(new Event("temperature", 40));

        assertThat(outgoingAlerts.received().size()).isEqualTo(1);
        assertThat(outgoingAlerts.received().get(0).getPayload().getSeverity()).isEqualTo("warning");

    }
}
