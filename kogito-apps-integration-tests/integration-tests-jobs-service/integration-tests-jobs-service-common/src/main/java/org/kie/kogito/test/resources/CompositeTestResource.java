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
package org.kie.kogito.test.resources;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.testcontainers.Constants;
import org.kie.kogito.testcontainers.KogitoGenericContainer;
import org.kie.kogito.testcontainers.KogitoKafkaContainer;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

public class CompositeTestResource implements TestResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeTestResource.class);
    public static final String MAIN_SERVICE_ID = "main-service";
    private final Map<String, GenericContainer<?>> sharedDependencyContainers;
    private final Map<String, KogitoGenericContainer<?>> serviceContainers;

    private final Map<String, List<GenericContainer<?>>> dependencyContainers;

    private final KogitoGenericContainer<?> mainContainer;
    private final Map<String, String> properties;

    public CompositeTestResource(KogitoGenericContainer<?> mainContainer) {
        this.sharedDependencyContainers = new HashMap<>();
        this.dependencyContainers = new HashMap<>();
        this.serviceContainers = new HashMap<>();
        this.properties = new HashMap<>();
        this.mainContainer = mainContainer;
        withServiceContainer(MAIN_SERVICE_ID, mainContainer);
    }

    public CompositeTestResource withServiceContainer(String id, KogitoGenericContainer<?> container, GenericContainer<?>... dependency) {
        serviceContainers.put(id, container);
        return withDependencyToService(id, dependency);
    }

    public CompositeTestResource withDependencyToService(String serviceId, GenericContainer<?>... dependency) {
        List<GenericContainer<?>> containers = dependencyContainers.getOrDefault(serviceId, new ArrayList<>());
        containers.addAll(Arrays.asList(dependency));
        dependencyContainers.put(serviceId, containers);
        return this;
    }

    public CompositeTestResource withSharedDependencyContainer(String prefix, GenericContainer<?> container) {
        sharedDependencyContainers.put(prefix, container);
        return this;
    }

    public <T> List<T> getServiceContainers(Class<T> type) {
        return serviceContainers.values().stream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
    }

    public <T extends KogitoGenericContainer<?>> T getServiceContainer(String id) {
        return (T) serviceContainers.get(id);
    }

    private String hostName(GenericContainer<?> container) {
        return container.getContainerInfo().getConfig().getHostName();
    }

    @Override
    public void start() {
        LOGGER.info("Starting {} Test Resource", mainContainer);
        final Network network = Network.newNetwork();
        sharedDependencyContainers.values().stream()
                .map(c -> c.withNetwork(network))
                .map(c -> c.waitingFor(Wait.forListeningPort()).withStartupTimeout(Constants.CONTAINER_START_TIMEOUT))
                .forEach(GenericContainer::start);
        configureKafkaToService(sharedDependencyContainers.values(), serviceContainers.values().toArray(GenericContainer[]::new));
        startServices(network);
    }

    protected void startServices(Network network) {
        serviceContainers.entrySet()
                .stream()
                .map(entry -> {
                    List<GenericContainer<?>> dependencies = dependencyContainers.getOrDefault(entry.getKey(), Collections.emptyList())
                            .stream()
                            .map(container -> container.withNetwork(network))
                            .map(container -> {
                                if (!container.isRunning()) {
                                    container.start();
                                }
                                return container;
                            })
                            .collect(Collectors.toList());
                    configurePostgreSQLToService(dependencies, serviceContainers.get(entry.getKey()));
                    configureKafkaToService(dependencies, serviceContainers.get(entry.getKey()));
                    return entry;
                })
                .map(Map.Entry::getValue)
                .forEach(service -> {
                    service.withNetwork(network);
                    service.start();
                    LOGGER.info("Test resource started");
                });
    }

    protected void configureKafkaToService(Collection<GenericContainer<?>> containers, GenericContainer<?>... services) {
        containers.stream()
                .filter(KogitoKafkaContainer.class::isInstance)
                .map(KogitoKafkaContainer.class::cast)
                .findFirst()
                .ifPresent(kafka -> {
                    // external access url
                    String kafkaURL = kafka.getBootstrapServers();
                    properties.put("kafka.bootstrap.servers", kafkaURL);
                    properties.put("spring.kafka.bootstrap-servers", kafkaURL);

                    //internal access
                    final String kafkaInternalUrl = hostName(kafka) + ":29092";
                    Stream.of(services).forEach(service -> service.addEnv("KAFKA_BOOTSTRAP_SERVERS", kafkaInternalUrl));
                });
    }

    protected void configurePostgreSQLToService(Collection<GenericContainer<?>> containers, GenericContainer<?>... services) {
        containers.stream()
                .filter(KogitoPostgreSqlContainer.class::isInstance)
                .map(KogitoPostgreSqlContainer.class::cast)
                .findFirst()
                .ifPresent(postgreSql -> {
                    final String connectionTemplate = "postgresql://{0}:{1}/{2}";
                    final String jdbcConnectionTemplate = "jdbc:postgresql://{0}:{1}/{2}";
                    final String server = hostName(postgreSql);
                    final String port = "5432";
                    final String username = postgreSql.getUsername();
                    final String password = postgreSql.getPassword();
                    final String database = postgreSql.getDatabaseName();
                    final String reactiveUrl = MessageFormat.format(connectionTemplate, server, port, database);
                    final String jdbcUrl = MessageFormat.format(jdbcConnectionTemplate, server, port, database);
                    Stream.of(services)
                            .forEach(service -> {
                                service.addEnv("QUARKUS_DATASOURCE_JDBC_URL", jdbcUrl);
                                service.addEnv("QUARKUS_DATASOURCE_REACTIVE_URL", reactiveUrl);
                                service.addEnv("QUARKUS_DATASOURCE_USERNAME", username);
                                service.addEnv("QUARKUS_DATASOURCE_PASSWORD", password);
                                service.addEnv("QUARKUS_DATASOURCE_DB-KIND", "postgresql");
                                service.addEnv("QUARKUS_FLYWAY_MIGRATE_AT_START", "true");
                                service.addEnv("QUARKUS_FLYWAY_BASELINE_ON_MIGRATE", "true");
                                service.addEnv("QUARKUS_FLYWAY_CLEAN_AT_START", "false");
                            });
                });
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping test resource");
        serviceContainers.values().forEach(GenericContainer::stop);
        sharedDependencyContainers.values().forEach(GenericContainer::stop);
        dependencyContainers.values().stream().flatMap(List::stream).forEach(GenericContainer::stop);
    }

    @Override
    public String getResourceName() {
        return mainContainer.getContainerName();
    }

    @Override
    public int getMappedPort() {
        return mainContainer.getMappedPort(8080);
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
