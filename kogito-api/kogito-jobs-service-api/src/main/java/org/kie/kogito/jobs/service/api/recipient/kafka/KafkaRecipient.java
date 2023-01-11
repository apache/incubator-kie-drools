/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.api.recipient.kafka;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.kie.kogito.jobs.service.api.Recipient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static org.kie.kogito.jobs.service.api.recipient.kafka.KafkaRecipient.BOOTSTRAP_SERVERS_PROPERTY;
import static org.kie.kogito.jobs.service.api.recipient.kafka.KafkaRecipient.HEADERS_PROPERTY;
import static org.kie.kogito.jobs.service.api.recipient.kafka.KafkaRecipient.PAYLOAD_PROPERTY;
import static org.kie.kogito.jobs.service.api.recipient.kafka.KafkaRecipient.TOPIC_NAME_PROPERTY;

@Schema(description = "Recipient definition that delivers a kafka message that contains the configured \"payload\".",
        allOf = { Recipient.class },
        requiredProperties = { BOOTSTRAP_SERVERS_PROPERTY, TOPIC_NAME_PROPERTY, PAYLOAD_PROPERTY })
@JsonPropertyOrder({ BOOTSTRAP_SERVERS_PROPERTY, TOPIC_NAME_PROPERTY, HEADERS_PROPERTY, PAYLOAD_PROPERTY })
public class KafkaRecipient<T extends KafkaRecipientPayloadData<?>> extends Recipient<T> {

    static final String BOOTSTRAP_SERVERS_PROPERTY = "bootstrapServers";
    static final String TOPIC_NAME_PROPERTY = "topicName";
    static final String HEADERS_PROPERTY = "headers";

    @Schema(description = "A comma-separated list of host:port to use to establish the connection to the kafka cluster.")
    private String bootstrapServers;
    @Schema(description = "Topic name for the message delivery.")
    private String topicName;
    @Schema(description = "Headers to send with the kafka message.")
    private Map<String, String> headers;
    @JsonProperty("payload")
    private T payload;

    public KafkaRecipient() {
        // Marshalling constructor.
        this.headers = new HashMap<>();
    }

    @Override
    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers != null ? headers : new HashMap<>();
    }

    public KafkaRecipient<T> addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        return "KafkaRecipient{" +
                "bootstrapServers='" + bootstrapServers + '\'' +
                ", topicName='" + topicName + '\'' +
                ", headers=" + headers +
                ", payload=" + payload +
                "} " + super.toString();
    }

    public static BuilderSelector builder() {
        return new BuilderSelector();
    }

    public static class BuilderSelector {

        private BuilderSelector() {

        }

        public Builder<KafkaRecipientStringPayloadData> forStringPayload() {
            return new Builder<>(new KafkaRecipient<>());
        }

        public Builder<KafkaRecipientBinaryPayloadData> forBinaryPayload() {
            return new Builder<>(new KafkaRecipient<>());
        }
    }

    public static class Builder<P extends KafkaRecipientPayloadData<?>> {

        private final KafkaRecipient<P> recipient;

        private Builder(KafkaRecipient<P> recipient) {
            this.recipient = recipient;
        }

        public Builder<P> payload(P payload) {
            recipient.setPayload(payload);
            return this;
        }

        public Builder<P> bootstrapServers(String bootstrapServers) {
            recipient.setBootstrapServers(bootstrapServers);
            return this;
        }

        public Builder<P> topicName(String topicName) {
            recipient.setTopicName(topicName);
            return this;
        }

        public Builder<P> header(String name, String value) {
            recipient.addHeader(name, value);
            return this;
        }

        public KafkaRecipient<P> build() {
            return recipient;
        }
    }
}
