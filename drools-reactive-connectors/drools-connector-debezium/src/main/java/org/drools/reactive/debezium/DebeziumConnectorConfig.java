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
package org.drools.reactive.debezium;

import java.util.Objects;
import java.util.Properties;

import org.drools.reactive.api.ConnectorConfig;

/**
 * Debezium-specific configuration extending the base {@link ConnectorConfig}.
 * Wraps a Debezium {@link Properties} object plus connector-level settings.
 */
public class DebeziumConnectorConfig extends ConnectorConfig {

    private final Properties debeziumProperties;
    private final String connectorName;

    private DebeziumConnectorConfig(Builder builder) {
        super(builder);
        this.debeziumProperties = builder.debeziumProperties;
        this.connectorName = builder.connectorName;
    }

    /** The full Debezium engine properties (connector class, database host, etc.). */
    public Properties getDebeziumProperties() {
        return debeziumProperties;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ConnectorConfig.Builder<Builder> {

        private Properties debeziumProperties = new Properties();
        private String connectorName = "drools-cdc";

        public Builder debeziumProperties(Properties props) {
            this.debeziumProperties = Objects.requireNonNull(props, "debeziumProperties must not be null");
            return this;
        }

        /** Set an individual Debezium property. */
        public Builder debeziumProperty(String key, String value) {
            this.debeziumProperties.setProperty(key, value);
            return this;
        }

        public Builder connectorName(String name) {
            this.connectorName = Objects.requireNonNull(name, "connectorName must not be null");
            return this;
        }

        @Override
        public DebeziumConnectorConfig build() {
            return new DebeziumConnectorConfig(this);
        }
    }
}
