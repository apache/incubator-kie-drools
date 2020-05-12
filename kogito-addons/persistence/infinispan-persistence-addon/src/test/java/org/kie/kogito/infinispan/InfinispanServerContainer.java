/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.infinispan;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import org.testcontainers.lifecycle.Startable;

public class InfinispanServerContainer implements Startable {

    private static final String INFINISPAN_IMAGE = System.getProperty("container.image.infinispan");
    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanServerContainer.class);
    private GenericContainer infinispan;

    @Override
    public void start() {
        if (INFINISPAN_IMAGE == null) {
            throw new RuntimeException("Please define a valid Infinispan image in system property container.image.infinispan");
        }
        LOGGER.info("Using Infinispan image: {}", INFINISPAN_IMAGE);
        infinispan = new FixedHostPortGenericContainer(INFINISPAN_IMAGE)
                .withFixedExposedPort(11222, 11222)
                //wait for the server to be  fully started
                .waitingFor(Wait.forLogMessage(".*\\bstarted\\b.*", 1))
                .withEnv("USER", "admin")
                .withEnv("PASS", "admin")
                .withLogConsumer(new Slf4jLogConsumer(LOGGER));
        infinispan.start();
    }

    @Override
    public void stop() {
        infinispan.stop();
    }
}
