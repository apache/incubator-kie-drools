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

package org.kie.kogito.index;

import java.util.Collections;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.infinispan.configuration.cache.CacheMode;
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

import static org.infinispan.commons.dataconversion.MediaType.APPLICATION_PROTOSTREAM_TYPE;

public class InfinispanServerTestResource implements QuarkusTestResourceLifecycleManager {

    private EmbeddedCacheManager cacheManager;
    private HotRodServer hotRodServer;

    @Override
    public Map<String, String> start() {
        Configuration config = new ConfigurationBuilder()
                .template(true)
                .clustering().cacheMode(CacheMode.LOCAL)
                .indexing().index(Index.PRIMARY_OWNER).addProperty("default.directory_provider", "local-heap")
                .encoding().key().mediaType(APPLICATION_PROTOSTREAM_TYPE)
                .encoding().value().mediaType(APPLICATION_PROTOSTREAM_TYPE)
                .build();

        GlobalConfiguration globalConfig = new GlobalConfigurationBuilder()
                .defaultCacheName("default")
                .nonClusteredDefault()
                .build();

        cacheManager = new DefaultCacheManager(globalConfig, new ConfigurationBuilder().build());
        cacheManager.defineConfiguration("kogito-template", config);

        hotRodServer = new HotRodServer();
        HotRodServerConfiguration cfg = new HotRodServerConfigurationBuilder()
                .host("localhost")
                .proxyHost("localhost")
                .port(11232)
                .proxyPort(11232)
                .adminOperationsHandler(new EmbeddedServerAdminOperationHandler())
                .build();
        hotRodServer.start(cfg, cacheManager);
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        if (hotRodServer != null) {
            hotRodServer.stop();
        }
        if (cacheManager != null) {
            cacheManager.stop();
        }
    }
}
