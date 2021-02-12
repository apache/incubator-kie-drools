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
package org.kie.kogito.jobs.service.repository.infinispan;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.interceptor.Interceptor;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.kie.kogito.infinispan.health.InfinispanHealthCheck;

import static org.kie.kogito.jobs.service.repository.infinispan.InfinispanConfiguration.Caches.JOB_DETAILS;

@ApplicationScoped
public class InfinispanConfiguration {

    public static final String PERSISTENCE_CONFIG_KEY = "kogito.jobs-service.persistence";
    public static final String CACHE_TEMPLATE_XML = "META-INF/kogito-cache-default.xml";
    private AtomicBoolean initialized = new AtomicBoolean(Boolean.FALSE);

    /**
     * Constants for Caches
     */
    public static class Caches {

        private Caches() {

        }

        public static final String JOB_DETAILS = "JOB_DETAILS";
    }

    @Produces
    @Readiness
    public HealthCheck infinispanHealthCheck(@ConfigProperty(name = PERSISTENCE_CONFIG_KEY) Optional<String> persistence,
                                             Instance<RemoteCacheManager> cacheManagerInstance) {
        return isEnabled(persistence)
                .<HealthCheck>map(p -> new InfinispanHealthCheck(cacheManagerInstance))
                .orElse(() -> HealthCheckResponse.up("In Memory Persistence"));
    }

    private Optional<String> isEnabled(Optional<String> persistence) {
        return persistence
                .filter("infinispan"::equals);
    }

    void initializeCaches(@Observes @Priority(Interceptor.Priority.PLATFORM_BEFORE) StartupEvent startupEvent,
                          @ConfigProperty(name = PERSISTENCE_CONFIG_KEY) Optional<String> persistence,
                          Instance<RemoteCacheManager> remoteCacheManager,
                          Event<InfinispanInitialized> initializedEvent) {
        isEnabled(persistence)
                .map(c -> remoteCacheManager.get().administration().getOrCreateCache(JOB_DETAILS, getCacheTemplate()))
                .ifPresent(c -> {
                    initializedEvent.fire(new InfinispanInitialized());
                    initialized.set(Boolean.TRUE);
                });
    }

    protected Boolean isInitialized(){
        return initialized.get();
    }

    private XMLStringConfiguration getCacheTemplate() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(CACHE_TEMPLATE_XML);
        String xml = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        return new XMLStringConfiguration(xml);
    }
}