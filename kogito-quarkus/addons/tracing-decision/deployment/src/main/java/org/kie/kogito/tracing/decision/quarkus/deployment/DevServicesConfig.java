/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.tracing.decision.quarkus.deployment;

import java.util.Objects;

import io.quarkus.builder.item.SimpleBuildItem;

public final class DevServicesConfig extends SimpleBuildItem {

    public enum Property {

        QuarkusDataSourceDbKind("quarkus.datasource.db-kind") {
            @Override
            public String getEnvironmentVariableName() {
                return "QUARKUS_DATASOURCE_DB_KIND";
            }
        },
        QuarkusDataSourceUserName("quarkus.datasource.username") {
            @Override
            public String getEnvironmentVariableName() {
                return "QUARKUS_DATASOURCE_USERNAME";
            }
        },
        QuarkusDataSourcePassword("quarkus.datasource.password") {
            @Override
            public String getEnvironmentVariableName() {
                return "QUARKUS_DATASOURCE_PASSWORD";
            }
        },
        QuarkusDataSourceJdbcUrl("quarkus.datasource.jdbc.url") {
            @Override
            public String getEnvironmentVariableName() {
                return "QUARKUS_DATASOURCE_JDBC_URL";
            }
        },
        KafkaBootstrapServers("kafka.bootstrap.servers") {
            @Override
            public String getEnvironmentVariableName() {
                return "KAFKA_BOOTSTRAP_SERVERS";
            }
        },
        HibernateOrmDatabaseGeneration("quarkus.hibernate-orm.database.generation") {
            @Override
            public String getEnvironmentVariableName() {
                return "QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION";
            }
        };

        private final String propertyName;

        Property(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public abstract String getEnvironmentVariableName();

    }

    private String dataSourceUserName;
    private String dataSourcePassword;
    private String dataSourceKind;
    private String dataSourceUrl;
    private String kafkaBootstrapServer;
    private String hibernateOrmDatabaseGeneration;

    public String getDataSourceUserName() {
        return dataSourceUserName;
    }

    public void setDataSourceUserName(String dataSourceUserName) {
        this.dataSourceUserName = dataSourceUserName;
    }

    public String getDataSourcePassword() {
        return dataSourcePassword;
    }

    public void setDataSourcePassword(String dataSourcePassword) {
        this.dataSourcePassword = dataSourcePassword;
    }

    public String getDataSourceKind() {
        return dataSourceKind;
    }

    public void setDataSourceKind(String dataSourceKind) {
        this.dataSourceKind = dataSourceKind;
    }

    public String getDataSourceUrl() {
        return dataSourceUrl;
    }

    public void setDataSourceUrl(String dataSourceUrl) {
        this.dataSourceUrl = dataSourceUrl;
    }

    public String getKafkaBootstrapServer() {
        return kafkaBootstrapServer;
    }

    public void setKafkaBootstrapServer(String kafkaBootstrapServer) {
        this.kafkaBootstrapServer = kafkaBootstrapServer;
    }

    public String getHibernateOrmDatabaseGeneration() {
        return hibernateOrmDatabaseGeneration;
    }

    public void setHibernateOrmDatabaseGeneration(String hibernateOrmDatabaseGeneration) {
        this.hibernateOrmDatabaseGeneration = hibernateOrmDatabaseGeneration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DevServicesConfig that = (DevServicesConfig) o;
        return Objects.equals(dataSourceUserName, that.dataSourceUserName)
                && Objects.equals(dataSourcePassword, that.dataSourcePassword)
                && Objects.equals(dataSourceKind, that.dataSourceKind)
                && Objects.equals(dataSourceUrl, that.dataSourceUrl)
                && Objects.equals(kafkaBootstrapServer, that.kafkaBootstrapServer)
                && Objects.equals(hibernateOrmDatabaseGeneration, that.hibernateOrmDatabaseGeneration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSourceUserName,
                dataSourcePassword,
                dataSourceKind,
                dataSourceUrl,
                kafkaBootstrapServer,
                hibernateOrmDatabaseGeneration);
    }
}
