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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.kie.kogito.testcontainers.Constants.CONTAINER_START_TIMEOUT;

public abstract class KogitoGenericContainer<T extends GenericContainer<T>> extends GenericContainer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoGenericContainer.class);

    public KogitoGenericContainer(String containerName) {
        super(getImageName(containerName));
        withStartupTimeout(CONTAINER_START_TIMEOUT);
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        withLogConsumer(f -> System.out.print(f.getUtf8String()));
    }

    public static String getImageName(String name) {
        return System.getProperty(Constants.CONTAINER_NAME_PREFIX + name,
                TestcontainersConfiguration.getInstance().getClasspathProperties().getProperty(Constants.CONTAINER_NAME_PREFIX + name));
    }
}
