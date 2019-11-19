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

package org.kie.kogito.jobs.service.resource;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.Index;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.server.core.admin.embeddedserver.EmbeddedServerAdminOperationHandler;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.configuration.HotRodServerConfiguration;
import org.infinispan.server.hotrod.configuration.HotRodServerConfigurationBuilder;
import org.kie.kogito.jobs.service.repository.infinispan.InfinispanConfiguration;

public class InfinispanServerTestResource implements QuarkusTestResourceLifecycleManager {

    private HotRodServer hotRodServer;
    private EmbeddedCacheManager cacheManager;
    private static final Integer PORT = 11232;

    @Override
    public Map<String, String> start() {
        Configuration configuration = new ConfigurationBuilder()
                .encoding()
                .key()
                .mediaType(MediaType.APPLICATION_PROTOSTREAM_TYPE)
                .encoding()
                .value()
                .mediaType(MediaType.APPLICATION_PROTOSTREAM_TYPE)
                .indexing().index(Index.PRIMARY_OWNER).addProperty("default.directory_provider", "local-heap")
                .build();

        GlobalConfiguration globalConfig = new GlobalConfigurationBuilder()
                .defaultCacheName("default")
                .serialization()
                .build();

        cacheManager = new DefaultCacheManager(globalConfig, configuration);

        hotRodServer = new HotRodServer();
        HotRodServerConfiguration cfg = new HotRodServerConfigurationBuilder()
                .host("localhost")
                .proxyHost("localhost")
                .port(PORT)
                .proxyPort(PORT)
                .adminOperationsHandler(new EmbeddedServerAdminOperationHandler())
                .build();
        hotRodServer.start(cfg, cacheManager);

        Stream.of(InfinispanConfiguration.Caches.SCHEDULED_JOBS)
                .forEach(name -> cacheManager.administration().getOrCreateCache(name, configuration));

        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        Optional.ofNullable(hotRodServer)
                .ifPresent(HotRodServer::stop);
    }
}
