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

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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
public class JobServiceContainer extends GenericContainer<JobServiceContainer> implements TestResource {

    public static final String NAME = "jobs-service";
    public static final int PORT = 8080;
    public static final String IMAGE = "container.image." + NAME;
    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceContainer.class);

    public JobServiceContainer() {
        addExposedPort(PORT);
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        waitingFor(Wait.forLogMessage(".*Listening on:.*", 1));
        setDockerImageName(getImageName());
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(PORT);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }

    private String getImageName() {
        return Optional.ofNullable(System.getProperty(IMAGE)).filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new IllegalArgumentException(IMAGE + " property should be set in pom.xml"));
    }
}
