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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.DevServicesLauncherConfigResultBuildItem;

import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.HibernateOrmDatabaseGeneration;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.KafkaBootstrapServers;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.QuarkusDataSourceDbKind;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.QuarkusDataSourceJdbcUrl;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.QuarkusDataSourcePassword;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.QuarkusDataSourceUserName;

/**
 * Extracts DevServices properties.
 */
public class DevServicesConfigProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevServicesConfigProcessor.class);

    @SuppressWarnings("unused")
    @BuildStep(onlyIf = IsDevelopment.class)
    public void extractDevServicesDefaultDataSourceConfiguration(final DevServicesLauncherConfigResultBuildItem configurationItems,
            final BuildProducer<DevServicesConfig> devServicesPostgreSQLConfigBuilder) {

        final DevServicesConfig devServicesConfig = new DevServicesConfig();

        LOGGER.debug("Extracting Quarkus DevService configurations...");
        final Map<String, String> quarkusConfig = configurationItems.getConfig();
        devServicesConfig.setDataSourceKind(quarkusConfig.get(QuarkusDataSourceDbKind.getPropertyName()));
        devServicesConfig.setDataSourceUserName(quarkusConfig.get(QuarkusDataSourceUserName.getPropertyName()));
        devServicesConfig.setDataSourcePassword(quarkusConfig.get(QuarkusDataSourcePassword.getPropertyName()));
        devServicesConfig.setDataSourceUrl(quarkusConfig.get(QuarkusDataSourceJdbcUrl.getPropertyName()));
        devServicesConfig.setKafkaBootstrapServer(quarkusConfig.get(KafkaBootstrapServers.getPropertyName()));
        devServicesConfig.setHibernateOrmDatabaseGeneration(quarkusConfig.get(HibernateOrmDatabaseGeneration.getPropertyName()));

        LOGGER.debug(String.format("DevServices default DataSource Kind: %s", devServicesConfig.getDataSourceKind()));
        LOGGER.debug(String.format("DevServices default DataSource Username: %s", devServicesConfig.getDataSourceUserName()));
        LOGGER.debug(String.format("DevServices default DataSource Password: %s", devServicesConfig.getDataSourcePassword()));
        LOGGER.debug(String.format("DevServices default DataSource URL: %s", devServicesConfig.getDataSourceUrl()));
        LOGGER.debug(String.format("DevServices Kafka Bootstrap Server: %s", devServicesConfig.getKafkaBootstrapServer()));
        LOGGER.debug(String.format("DevServices Hibernate ORM Database Generation: %s", devServicesConfig.getHibernateOrmDatabaseGeneration()));

        devServicesPostgreSQLConfigBuilder.produce(devServicesConfig);
    }

}
