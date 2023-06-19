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

package org.kie.kogito.quarkus.workflow.deployment.devservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.devservices.common.ConfigureUtil;

/**
 * This container wraps Data Index Service container
 */
public class DataIndexInMemoryContainer extends GenericContainer<DataIndexInMemoryContainer> {

    public static final int PORT = 8080;
    /**
     * This allows other applications to discover the running service and use it instead of starting a new instance.
     */
    public static final String DEV_SERVICE_LABEL = "kogito-dev-service-data-index";
    public static final String LATEST = "latest";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataIndexInMemoryContainer.class);

    private final int fixedExposedPort;
    private final boolean useSharedNetwork;
    private String hostName = null;

    public DataIndexInMemoryContainer(DockerImageName dockerImageName, int fixedExposedPort, String serviceName, boolean useSharedNetwork) {
        super(dockerImageName);
        this.fixedExposedPort = fixedExposedPort;
        this.useSharedNetwork = useSharedNetwork;

        if (serviceName != null) { // Only adds the label in dev mode.
            withLabel(DEV_SERVICE_LABEL, serviceName);
        }
        withPrivilegedMode(true);
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        withImagePullPolicy(LATEST.equalsIgnoreCase(dockerImageName.getVersionPart()) ? PullPolicy.alwaysPull() : PullPolicy.defaultPolicy());
        withEnv("KOGITO_DATAINDEX_GATEWAY_URL", "host.testcontainers.internal");
        withEnv("KOGITO_DATA_INDEX_VERTX_GRAPHQL_UI_PATH", "/q/graphql-ui");
        withEnv("KOGITO_DATA_INDEX_QUARKUS_PROFILE", "http-events-support");
        withExposedPorts(PORT);
        waitingFor(Wait.forHttp("/q/health/ready").forStatusCode(200));
    }

    @Override
    protected void configure() {
        super.configure();

        if (useSharedNetwork) {
            hostName = ConfigureUtil.configureSharedNetwork(this, "data-index");
            return;
        }

        if (fixedExposedPort > 0) {
            addFixedExposedPort(fixedExposedPort, PORT);
        }
    }

    public String getUrl() {
        return String.format("http://%s:%s", getHostToUse(), getPortToUse());
    }

    private String getHostToUse() {
        return useSharedNetwork ? hostName : getHost();
    }

    private int getPortToUse() {
        return useSharedNetwork ? PORT : getMappedPort(PORT);
    }

}
