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

package org.kie.kogito.quarkus.processes.devservices;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.Base58;
import org.testcontainers.utility.DockerImageName;

/**
 * This container wraps Data Index Service container
 */
public class DataIndexInMemoryContainer extends GenericContainer<DataIndexInMemoryContainer> {

    public static final int PORT = 8080;
    /**
     * This allows other applications to discover the running service and use it instead of starting a new instance.
     */
    public static final String DEV_SERVICE_LABEL = "kogito-dev-service-data-index";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataIndexInMemoryContainer.class);
    private final int port;
    private final boolean useSharedNetwork;
    private String hostName = null;

    public DataIndexInMemoryContainer(DockerImageName dockerImageName, int fixedExposedPort, String serviceName, boolean useSharedNetwork) {
        super(dockerImageName);
        this.port = fixedExposedPort;
        this.useSharedNetwork = useSharedNetwork;
        withPrivilegedMode(true);
        withNetwork(Network.SHARED);
        if (useSharedNetwork) {
            hostName = "data-index-" + Base58.randomString(5);
            setNetworkAliases(Collections.singletonList(hostName));
        } else {
            withExposedPorts(PORT);
        }
        if (serviceName != null) { // Only adds the label in dev mode.
            withLabel(DEV_SERVICE_LABEL, serviceName);
        }
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
    }

    @Override
    protected void configure() {
        super.configure();
        if ((port > 0) && !useSharedNetwork) {
            addFixedExposedPort(port, PORT);
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
