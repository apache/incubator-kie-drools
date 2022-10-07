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

package org.kie.kogito.tracing.decision.quarkus.devservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.devservices.common.ConfigureUtil;

/**
 * This container wraps the TrustyService container
 */
public class TrustyServiceInMemoryContainer extends GenericContainer<TrustyServiceInMemoryContainer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrustyServiceInMemoryContainer.class);

    public static final int PORT = 8080;

    /**
     * This allows other applications to discover the running service and use it instead of starting a new instance.
     */
    public static final String DEV_SERVICE_LABEL = "kogito-dev-service-trusty-service";
    private final int fixedExposedPort;
    private final boolean useSharedNetwork;

    private final int portUsedByTest;

    private String hostName = null;

    public TrustyServiceInMemoryContainer(final DockerImageName dockerImageName,
            final int fixedExposedPort,
            final String serviceName,
            final boolean useSharedNetwork,
            final int portUsedByTest) {
        super(dockerImageName);
        this.fixedExposedPort = fixedExposedPort;
        this.useSharedNetwork = useSharedNetwork;
        this.portUsedByTest = portUsedByTest;

        // Only adds the label in dev mode.
        if (serviceName != null) {
            withLabel(DEV_SERVICE_LABEL, serviceName);
        }

        withPrivilegedMode(true);
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        withImagePullPolicy("latest".equalsIgnoreCase(dockerImageName.getVersionPart()) ? PullPolicy.alwaysPull() : PullPolicy.defaultPolicy());
    }

    @Override
    protected void configure() {
        super.configure();

        if (useSharedNetwork) {
            hostName = ConfigureUtil.configureSharedNetwork(this, "trusty-service");
            return;
        }

        if (fixedExposedPort > 0) {
            addFixedExposedPort(fixedExposedPort, PORT);
        } else {
            addExposedPort(PORT);
        }
    }

    public String getUrl() {
        return String.format("http://%s:%s", getHostToUse(), getPortToUse());
    }

    private String getHostToUse() {
        return useSharedNetwork ? hostName : getHost();
    }

    private int getPortToUse() {
        LOGGER.debug("portUsedByTest " + portUsedByTest);
        if (portUsedByTest > 0) {
            return useSharedNetwork ? portUsedByTest : getMappedPort(portUsedByTest);
        } else {
            return useSharedNetwork ? PORT : getMappedPort(PORT);
        }
    }
}
