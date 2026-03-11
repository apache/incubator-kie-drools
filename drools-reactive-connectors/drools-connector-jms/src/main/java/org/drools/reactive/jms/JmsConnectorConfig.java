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
package org.drools.reactive.jms;

import java.util.Objects;

import org.drools.reactive.api.ConnectorConfig;

/**
 * JMS-specific configuration extending the base {@link ConnectorConfig}.
 *
 * <p>The connector requires a {@link jakarta.jms.ConnectionFactory} to be
 * provided at construction time (since JMS connection factories are
 * broker-specific and typically obtained via JNDI or CDI injection).
 */
public class JmsConnectorConfig extends ConnectorConfig {

    private final String destinationName;
    private final JmsDestinationType destinationType;
    private final String messageSelector;
    private final boolean sessionTransacted;
    private final String durableSubscriptionName;
    private final String clientId;

    private JmsConnectorConfig(Builder builder) {
        super(builder);
        this.destinationName = builder.destinationName;
        this.destinationType = builder.destinationType;
        this.messageSelector = builder.messageSelector;
        this.sessionTransacted = builder.sessionTransacted;
        this.durableSubscriptionName = builder.durableSubscriptionName;
        this.clientId = builder.clientId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public JmsDestinationType getDestinationType() {
        return destinationType;
    }

    /** Optional JMS message selector expression, or {@code null} for all messages. */
    public String getMessageSelector() {
        return messageSelector;
    }

    public boolean isSessionTransacted() {
        return sessionTransacted;
    }

    /** If set, creates a durable topic subscription with this name. Only valid for TOPIC destinations. */
    public String getDurableSubscriptionName() {
        return durableSubscriptionName;
    }

    /** JMS client ID, required for durable subscriptions. */
    public String getClientId() {
        return clientId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ConnectorConfig.Builder<Builder> {

        private String destinationName;
        private JmsDestinationType destinationType = JmsDestinationType.QUEUE;
        private String messageSelector;
        private boolean sessionTransacted = false;
        private String durableSubscriptionName;
        private String clientId;

        public Builder destinationName(String name) {
            this.destinationName = Objects.requireNonNull(name, "destinationName must not be null");
            return this;
        }

        public Builder destinationType(JmsDestinationType type) {
            this.destinationType = Objects.requireNonNull(type, "destinationType must not be null");
            return this;
        }

        public Builder messageSelector(String selector) {
            this.messageSelector = selector;
            return this;
        }

        public Builder sessionTransacted(boolean transacted) {
            this.sessionTransacted = transacted;
            return this;
        }

        public Builder durableSubscription(String subscriptionName, String clientId) {
            this.durableSubscriptionName = subscriptionName;
            this.clientId = clientId;
            return this;
        }

        @Override
        public JmsConnectorConfig build() {
            if (destinationName == null || destinationName.isEmpty()) {
                throw new IllegalArgumentException("destinationName must be specified");
            }
            if (durableSubscriptionName != null && destinationType != JmsDestinationType.TOPIC) {
                throw new IllegalArgumentException("Durable subscriptions are only valid for TOPIC destinations");
            }
            return new JmsConnectorConfig(this);
        }
    }
}
