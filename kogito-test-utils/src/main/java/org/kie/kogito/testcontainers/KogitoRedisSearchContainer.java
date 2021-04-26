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
package org.kie.kogito.testcontainers;

import java.time.Duration;

import org.kie.kogito.resources.TestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class KogitoRedisSearchContainer extends GenericContainer<KogitoRedisSearchContainer> implements TestResource {

    public static final String NAME = "redis";
    public static final String REDIS_PROPERTY = "container.image." + NAME;
    public static final int PORT = 6379;

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoRedisSearchContainer.class);

    public KogitoRedisSearchContainer() {
        addExposedPort(PORT);
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        withLogConsumer(f -> System.out.println(f.getUtf8String()));
        waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\s", 1).withStartupTimeout(Duration.ofMinutes(5)));
        setDockerImageName(System.getProperty(REDIS_PROPERTY));
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