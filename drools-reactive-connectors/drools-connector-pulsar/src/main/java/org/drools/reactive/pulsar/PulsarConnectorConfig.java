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
package org.drools.reactive.pulsar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.drools.reactive.api.ConnectorConfig;

/**
 * Pulsar-specific configuration extending the base {@link ConnectorConfig}.
 */
public class PulsarConnectorConfig extends ConnectorConfig {

    private final String serviceUrl;
    private final List<String> topics;
    private final String subscriptionName;
    private final PulsarSubscriptionType subscriptionType;

    private PulsarConnectorConfig(Builder builder) {
        super(builder);
        this.serviceUrl = builder.serviceUrl;
        this.topics = Collections.unmodifiableList(builder.topics);
        this.subscriptionName = builder.subscriptionName;
        this.subscriptionType = builder.subscriptionType;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public PulsarSubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ConnectorConfig.Builder<Builder> {

        private String serviceUrl = "pulsar://localhost:6650";
        private List<String> topics = Collections.emptyList();
        private String subscriptionName = "drools-reactive";
        private PulsarSubscriptionType subscriptionType = PulsarSubscriptionType.EXCLUSIVE;

        public Builder serviceUrl(String url) {
            this.serviceUrl = Objects.requireNonNull(url, "serviceUrl must not be null");
            return this;
        }

        public Builder topics(String... topics) {
            this.topics = Arrays.asList(topics);
            return this;
        }

        public Builder topics(List<String> topics) {
            this.topics = Objects.requireNonNull(topics, "topics must not be null");
            return this;
        }

        public Builder subscriptionName(String name) {
            this.subscriptionName = Objects.requireNonNull(name, "subscriptionName must not be null");
            return this;
        }

        public Builder subscriptionType(PulsarSubscriptionType type) {
            this.subscriptionType = Objects.requireNonNull(type, "subscriptionType must not be null");
            return this;
        }

        @Override
        public PulsarConnectorConfig build() {
            if (topics.isEmpty()) {
                throw new IllegalArgumentException("At least one topic must be specified");
            }
            return new PulsarConnectorConfig(this);
        }
    }
}
