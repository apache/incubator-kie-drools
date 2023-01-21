/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.testcontainers;

import org.kie.kogito.test.resources.TestResource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.kie.kogito.testcontainers.Constants.CONTAINER_START_TIMEOUT;

/**
 * This container wraps Infinispan container
 */
public class KogitoInfinispanContainer extends KogitoGenericContainer<KogitoInfinispanContainer> implements TestResource {

    public static final String NAME = "infinispan";
    public static final int PORT = 11222;
    public static final String CONF_PATH = "/opt/infinispan/server/conf/";

    public KogitoInfinispanContainer() {
        super(NAME);
        addExposedPort(PORT);
        waitingFor(new HttpWaitStrategy().forPort(PORT).forStatusCodeMatching(response -> response == HTTP_OK || response == HTTP_UNAUTHORIZED)).withStartupTimeout(CONTAINER_START_TIMEOUT);
        withClasspathResourceMapping("testcontainers/infinispan/infinispan-local.xml", CONF_PATH + "infinispan-local.xml", BindMode.READ_ONLY);
        withCommand("-c infinispan-local.xml");
        withEnv("USER", "admin");
        withEnv("PASS", "admin");
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
