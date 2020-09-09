/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.testcontainers;

import org.kie.kogito.resources.TestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * This container wraps Infinispan container
 *
 */
public class KogitoInfinispanContainer extends GenericContainer<KogitoInfinispanContainer> implements TestResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoInfinispanContainer.class);

    public static final String NAME = "infinispan";
    public static final int PORT = 11222;
    public static final String INFINISPAN_PROPERTY = "container.image." + NAME;
    public static final String USER = "admin";
    public static final String PASS = "admin";

    public KogitoInfinispanContainer() {
        addExposedPort(PORT);
        withEnv("USER", USER);
        withEnv("PASS", PASS);
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        waitingFor(Wait.forHttp("/"));
        setDockerImageName(System.getProperty(INFINISPAN_PROPERTY));
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(PORT);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }

}
