/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.repository.infinispan;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InfinispanConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanConfiguration.class);
    public static final String PERSISTENCE_CONFIG_KEY = "kogito.job-service.persistence";
    private final Configuration config = new ConfigurationBuilder().build();

    /**
     * Constants for Caches
     */
    public static class Caches {

        public static final String SCHEDULED_JOBS = "SCHEDULED_JOBS";

        public static String[] ALL() {
            return new String[]{SCHEDULED_JOBS};
        }
    }

    private Optional<RemoteCacheManager> cacheManager;

    @Inject
    public InfinispanConfiguration(Instance<RemoteCacheManager> cacheManagerInstance,
                                   @ConfigProperty(name = PERSISTENCE_CONFIG_KEY)
                                           Optional<String> persistence) {

        LOGGER.info("Persistence config {}", persistence);
        this.cacheManager = persistence
                .filter("infinispan"::equals)
                .map(p -> cacheManagerInstance.get());
    }

    CompletionStage<Void> onStart(@Observes StartupEvent startupEvent) {
        return ReactiveStreams.of(Caches.ALL())
                .forEach(name -> cacheManager
                        .map(RemoteCacheManager::administration)
                        .ifPresent(adm -> adm.getOrCreateCache(name, config)))
                .run()
                .thenAccept(c -> LOGGER.info("Executed Infinispan configuration"));
    }
}