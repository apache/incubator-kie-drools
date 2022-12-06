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

@Schema(description = "Recipient definition that delivers a kafka message that contains the configured \"payload\".", allOf = { Recipient.class })
public class KafkaRecipient extends Recipient<byte[]> {

    @Schema(description = "A comma-separated list of host:port to use to establish the connection to the kafka cluster.", required = true)
    private String bootstrapServers;
    @Schema(description = "Topic name for the message delivery.", required = true)
    private String topicName;
    @Schema(description = "Headers to send with the kafka message.")
    private Map<String, String> headers;

    public KafkaRecipient() {
        // marshalling constructor.
        this.headers = new HashMap<>();
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

    public KafkaRecipient addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        return "KafkaRecipient{" +
                "bootstrapServers='" + bootstrapServers + '\'' +
                ", topicName='" + topicName + '\'' +
                ", headers=" + headers +
                "} " + super.toString();
    }

    public static Builder builder() {
        return new Builder(new KafkaRecipient());
    }

    public static class Builder {

        private final KafkaRecipient recipient;

        private Builder(KafkaRecipient recipient) {
            this.recipient = recipient;
        }

        public Builder payload(byte[] payload) {
            recipient.setPayload(payload);
            return this;
        }

        public Builder bootstrapServers(String bootstrapServers) {
            recipient.setBootstrapServers(bootstrapServers);
            return this;
        }

        public Builder topicName(String topicName) {
            recipient.setTopicName(topicName);
            return this;
        }

        public Builder header(String name, String value) {
            recipient.addHeader(name, value);
            return this;
        }

        public KafkaRecipient build() {
            return recipient;
        }
    }
}
