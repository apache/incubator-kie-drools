/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.infinispan;

import java.util.Collections;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public abstract class InfinispanServerTestResource implements QuarkusTestResourceWithCleanupLifecycleManager {

    private static final String LOCALHOST = "127.0.0.1";
    private static final int PORT = 11232;
    private static final String SASL_DIGEST_MD5 = "DIGEST-MD5";
    private static final String SERVER_NAME_INFINISPAN = "infinispan";
    private static final String REALM_DEFAULT = "default";
    private static final String ADMIN = "admin";
    private static final String INFINISPAN_IMAGE = System.getProperty("container.image.infinispan");
    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanServerTestResource.class);
    private GenericContainer infinispan;
    private RemoteCacheManager cacheManager;

    @Override
    public Map<String, String> start() {
        if (INFINISPAN_IMAGE == null) {
            // Workaround for the well known issue in quarkus https://github.com/quarkusio/quarkus/issues/9854
            LOGGER.warn("System property container.image.infinispan is not set. The test is started without the infinispan container.");
            return Collections.emptyMap();
        }

        LOGGER.info("Using Infinispan image: {}", INFINISPAN_IMAGE);
        infinispan = new FixedHostPortGenericContainer(INFINISPAN_IMAGE)
                .withFixedExposedPort(PORT, 11222)
                .withReuse(false)
                .withEnv("USER", ADMIN)
                .withEnv("PASS", ADMIN)
                .withLogConsumer(new Slf4jLogConsumer(LOGGER))
                .waitingFor(Wait.forLogMessage(".*ISPN080001.*", 1));
        infinispan.start();
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        if(cacheManager!=null) {
            cacheManager.close();
        }
        infinispan.stop();
    }

    private RemoteCacheManager getCacheManager() {
        if (cacheManager == null) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder
                    .addServer()
                    .host(LOCALHOST)
                    .port(PORT)
                    .security()
                    .authentication()
                    .username(ADMIN)
                    .password(ADMIN)
                    .realm(REALM_DEFAULT)
                    .serverName(SERVER_NAME_INFINISPAN)
                    .saslMechanism(SASL_DIGEST_MD5)
                    .clientIntelligence(ClientIntelligence.BASIC);

            cacheManager = new RemoteCacheManager(builder.build());
        }
        return cacheManager;
    }

    abstract public boolean shouldCleanCache(String cacheName);

    @Override
    public void cleanup() {
        if (!infinispan.isRunning()) {
            return;
        }

        for (String cacheName : getCacheManager().getCacheNames()) {
            if(shouldCleanCache(cacheName)){
                LOGGER.debug("Cleaning cache " + cacheName);
                getCacheManager().getCache(cacheName).clear();
            }
        }

    }
}