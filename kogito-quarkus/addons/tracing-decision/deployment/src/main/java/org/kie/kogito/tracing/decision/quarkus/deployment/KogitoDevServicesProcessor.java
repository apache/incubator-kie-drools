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

import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kie.kogito.quarkus.extensions.spi.deployment.TrustyServiceAvailableBuildItem;
import org.kie.kogito.tracing.decision.quarkus.devservices.TrustyServiceInMemoryContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.utility.DockerImageName;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.ContainerNetworkSettings;
import com.github.dockerjava.api.model.ContainerPort;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.IsDockerWorking;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CuratedApplicationShutdownBuildItem;
import io.quarkus.deployment.builditem.DevServicesSharedNetworkBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;
import io.quarkus.deployment.console.ConsoleInstalledBuildItem;
import io.quarkus.deployment.console.StartupLogCompressor;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import io.quarkus.deployment.logging.LoggingSetupBuildItem;
import io.quarkus.devservices.common.ContainerAddress;
import io.quarkus.devservices.common.ContainerLocator;
import io.quarkus.runtime.LaunchMode;

import static org.kie.kogito.tracing.decision.TrustyConstants.KOGITO_TRUSTY_SERVICE;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.HibernateOrmDatabaseGeneration;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.KafkaBootstrapServers;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.QuarkusDataSourceDbKind;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.QuarkusDataSourceJdbcUrl;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.QuarkusDataSourcePassword;
import static org.kie.kogito.tracing.decision.quarkus.deployment.DevServicesConfig.Property.QuarkusDataSourceUserName;

/**
 * Start a TrustyService instance with PostgreSQL storage as dev service if needed.
 */
public class KogitoDevServicesProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoDevServicesProcessor.class);
    private static final ContainerLocator LOCATOR = new ContainerLocator(TrustyServiceInMemoryContainer.DEV_SERVICE_LABEL,
            TrustyServiceInMemoryContainer.PORT);

    static volatile Closeable closeable;
    static volatile TrustyServiceDevServiceConfig cfg;
    static volatile boolean first = true;

    private final IsDockerWorking isDockerWorking = new IsDockerWorking(true);

    @SuppressWarnings("unused")
    @BuildStep(onlyIf = { GlobalDevServicesConfig.Enabled.class, IsDevelopment.class })
    public void startTrustyServiceDevService(
            final DevServicesConfig devServicesConfig,
            final BuildProducer<SystemPropertyBuildItem> systemProperties,
            final BuildProducer<TrustyServiceAvailableBuildItem> trustyServiceAvailableBuildItemBuildProducer,
            final LaunchModeBuildItem launchMode,
            final KogitoBuildTimeConfig buildTimeConfig,
            final List<DevServicesSharedNetworkBuildItem> devServicesSharedNetwork,
            final Optional<ConsoleInstalledBuildItem> consoleInstalled,
            final CuratedApplicationShutdownBuildItem applicationShutdown,
            final LoggingSetupBuildItem loggingSetup) {

        LOGGER.info("Docker Containers configuration...");
        DockerClientFactory.lazyClient().listContainersCmd().exec()
                .forEach(c -> {
                    LOGGER.debug("----> Image: " + c.getImage());
                    if (Objects.nonNull(c.getNames())) {
                        Arrays.stream(c.getNames()).forEach(n -> LOGGER.debug(String.format("----> Name: %s", n)));
                    }
                    if (Objects.nonNull(c.getLabels())) {
                        c.getLabels().forEach((key, value) -> LOGGER.debug(String.format("----> Label: [%s]=[%s]", key, value)));
                    }
                    LOGGER.debug("----> Ports: " + Arrays.stream(c.getPorts()).map(p -> p.getPrivatePort() + ">>" + p.getPublicPort()).collect(Collectors.joining(", ")));
                    LOGGER.debug("----> Network: "
                            + (Objects.isNull(c.getNetworkSettings()) ? ""
                                    : c.getNetworkSettings()
                                            .getNetworks()
                                            .entrySet()
                                            .stream()
                                            .map(n -> String.format("%s=%s [%s]", n.getKey(), n.getValue(), n.getValue().getIpAddress())).collect(Collectors.joining(", "))));
                });

        final TrustyServiceDevServiceConfig configuration = getConfiguration(buildTimeConfig);

        if (closeable != null) {
            boolean shouldShutdown = !configuration.equals(cfg);
            if (!shouldShutdown) {
                // Signal the service is (still) available when DevServices may have restarted but the service not
                trustyServiceAvailableBuildItemBuildProducer.produce(new TrustyServiceAvailableBuildItem());
                return;
            }
            shutdownTrustyService();
            cfg = null;
        }

        final StartupLogCompressor compressor = new StartupLogCompressor(
                (launchMode.isTest() ? "(test) " : "") + "Kogito TrustyService DevService starting:",
                consoleInstalled,
                loggingSetup);

        TrustyServiceInstance trustyService = null;
        try {
            trustyService = startTrustyService(configuration,
                    devServicesConfig,
                    launchMode,
                    !devServicesSharedNetwork.isEmpty());
            if (trustyService != null) {
                // Signal the service is available
                trustyServiceAvailableBuildItemBuildProducer.produce(new TrustyServiceAvailableBuildItem());
                closeable = trustyService.getCloseable();
            }
            compressor.close();
        } catch (Exception t) {
            compressor.closeAndDumpCaptured();
            throw new RuntimeException("Failed to start Kogito TrustyService DevServices", t);
        }

        //Discover TrustyService container
        LOGGER.info("Discovering TrustyService instance...");
        DockerClientFactory.lazyClient().listContainersCmd().exec()
                .stream().filter(container -> isTrustyServiceImage(container, configuration))
                .findFirst()
                .ifPresent(container -> {
                    Optional<Integer> port = Optional.empty();
                    Optional<String> ipAddress = Optional.empty();
                    final ContainerPort[] containerPorts = container.getPorts();
                    final ContainerNetworkSettings networkSettings = container.getNetworkSettings();
                    if (Objects.nonNull(containerPorts)) {
                        port = Arrays.stream(containerPorts)
                                .map(ContainerPort::getPrivatePort)
                                .filter(Objects::nonNull)
                                .findFirst();
                    }
                    if (Objects.nonNull(networkSettings)) {
                        ipAddress = networkSettings.getNetworks().values().stream()
                                .map(ContainerNetwork::getIpAddress)
                                .filter(Objects::nonNull)
                                .findFirst();
                    }
                    LOGGER.debug(String.format("[TrustyService] Private Port: %s", port.orElse(0)));
                    LOGGER.debug(String.format("[TrustyService] IP Address: %s", ipAddress.orElse("<None>")));
                    if (ipAddress.isPresent() && port.isPresent()) {
                        final String trustyServiceServer = String.format("http://%s:%s", ipAddress.get(), port.get());
                        LOGGER.debug(String.format("Setting System Property '%s' to '%s'", KOGITO_TRUSTY_SERVICE, trustyServiceServer));
                        systemProperties.produce(new SystemPropertyBuildItem(KOGITO_TRUSTY_SERVICE, trustyServiceServer));
                    }
                });

        // Configure the watch dog
        if (first) {
            first = false;
            final Runnable closeTask = () -> {
                if (closeable != null) {
                    shutdownTrustyService();
                }
                first = true;
                closeable = null;
                cfg = null;
            };
            applicationShutdown.addCloseTask(closeTask, true);
        }
        cfg = configuration;

        if (trustyService != null && trustyService.isOwner()) {
            LOGGER.info(
                    "DevServices for Kogito TrustyService started at {}",
                    trustyService.getUrl());
        }
    }

    private boolean isTrustyServiceImage(final Container container,
            final TrustyServiceDevServiceConfig trustyServiceDevServiceConfig) {
        final String name = container.getImage();
        return Objects.equals(trustyServiceDevServiceConfig.imageName, name);
    }

    private void shutdownTrustyService() {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOGGER.error("Failed to stop Kogito Data Index", e);
            } finally {
                closeable = null;
            }
        }
    }

    private TrustyServiceInstance startTrustyService(final TrustyServiceDevServiceConfig config,
            final DevServicesConfig devServicesConfig,
            final LaunchModeBuildItem launchMode,
            final boolean useSharedNetwork) {
        if (!config.devServicesEnabled) {
            // explicitly disabled
            LOGGER.info("Not starting DevServices for Kogito, as it has been disabled in the config.");
            return null;
        }

        if (!isDockerWorking.getAsBoolean()) {
            LOGGER.warn("Docker isn't working, unable to start TrustyService image.");
            return null;
        }

        final Optional<ContainerAddress> maybeContainerAddress = LOCATOR.locateContainer(config.serviceName,
                config.shared,
                launchMode.getLaunchMode());

        // Starting TrustyService
        final Supplier<TrustyServiceInstance> trustyServiceSupplier = () -> {
            try {
                TrustyServiceInMemoryContainer container = new TrustyServiceInMemoryContainer(
                        DockerImageName.parse(config.imageName),
                        config.fixedExposedPort,
                        launchMode.getLaunchMode() == LaunchMode.DEVELOPMENT ? config.serviceName : null,
                        useSharedNetwork,
                        config.portUsedByTest);

                LOGGER.debug(String.format("TrustyService DataSource Kind: %s", devServicesConfig.getDataSourceKind()));
                LOGGER.debug(String.format("TrustyService DataSource Username: %s", devServicesConfig.getDataSourceUserName()));
                LOGGER.debug(String.format("TrustyService DataSource Password: %s", devServicesConfig.getDataSourcePassword()));
                LOGGER.debug(String.format("TrustyService DataSource URL: %s", devServicesConfig.getDataSourceUrl()));
                LOGGER.debug(String.format("TrustyService Kafka Bootstrap Server: %s", devServicesConfig.getKafkaBootstrapServer()));
                LOGGER.debug(String.format("TrustyService Hibernate ORM Database Generation: %s", devServicesConfig.getHibernateOrmDatabaseGeneration()));

                //Environment variables used by kogito-images when launching the TrustyService container
                container.addEnv("SCRIPT_DEBUG", "false");
                container.addEnv("EXPLAINABILITY_ENABLED", "false");

                //Environment variables used by TrustyService to integrate with other services
                container.addEnv(QuarkusDataSourceDbKind.getEnvironmentVariableName(), devServicesConfig.getDataSourceKind());
                container.addEnv(QuarkusDataSourceUserName.getEnvironmentVariableName(), devServicesConfig.getDataSourceUserName());
                container.addEnv(QuarkusDataSourcePassword.getEnvironmentVariableName(), devServicesConfig.getDataSourcePassword());
                container.addEnv(QuarkusDataSourceJdbcUrl.getEnvironmentVariableName(), devServicesConfig.getDataSourceUrl());
                container.addEnv(KafkaBootstrapServers.getEnvironmentVariableName(), devServicesConfig.getKafkaBootstrapServer());
                container.addEnv(HibernateOrmDatabaseGeneration.getEnvironmentVariableName(), devServicesConfig.getHibernateOrmDatabaseGeneration());

                container.start();

                return new TrustyServiceInstance(container.getUrl(), container::close);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };

        return maybeContainerAddress.map(containerAddress -> new TrustyServiceInstance(containerAddress.getUrl(), null))
                .orElseGet(trustyServiceSupplier);
    }

    private TrustyServiceDevServiceConfig getConfiguration(final KogitoBuildTimeConfig cfg) {
        KogitoDevServicesBuildTimeConfig devServicesConfig = cfg.devServicesTrusty;
        return new TrustyServiceDevServiceConfig(devServicesConfig);
    }

    private static class TrustyServiceInstance {

        private final String url;
        private final Closeable closeable;

        public TrustyServiceInstance(final String url,
                final Closeable closeable) {
            this.url = url;
            this.closeable = closeable;
        }

        public boolean isOwner() {
            return closeable != null;
        }

        public String getUrl() {
            return url;
        }

        public Closeable getCloseable() {
            return closeable;
        }
    }

    private static final class TrustyServiceDevServiceConfig {

        private final boolean devServicesEnabled;
        private final String imageName;
        private final Integer fixedExposedPort;
        private final boolean shared;
        private final String serviceName;

        /**
         * In test mode, pick a random port
         */
        private final int portUsedByTest;

        public TrustyServiceDevServiceConfig(final KogitoDevServicesBuildTimeConfig config) {
            this.devServicesEnabled = config.enabled.orElse(true);
            this.imageName = config.imageName;
            this.fixedExposedPort = config.port.orElse(0);
            this.shared = config.shared;
            this.serviceName = config.serviceName;
            this.portUsedByTest = config.portUsedByTest;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TrustyServiceDevServiceConfig that = (TrustyServiceDevServiceConfig) o;
            return devServicesEnabled == that.devServicesEnabled
                    && Objects.equals(imageName, that.imageName)
                    && Objects.equals(fixedExposedPort, that.fixedExposedPort);
        }

        @Override
        public int hashCode() {
            return Objects.hash(devServicesEnabled, imageName, fixedExposedPort);
        }
    }
}
