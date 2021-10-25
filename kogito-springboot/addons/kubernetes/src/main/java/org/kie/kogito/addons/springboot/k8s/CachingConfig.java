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
package org.kie.kogito.addons.springboot.k8s;

import org.kie.kogito.addons.k8s.CacheNames;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CachingConfig {

    public static final String CACHE_MANAGER = "caffeineCacheManager";

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder();
    }

    @Primary //marking as primary to not clash with Infinispan Persistence. Could be removed in the future. See: https://issues.redhat.com/browse/KOGITO-6111
    @Bean(CACHE_MANAGER)
    public CaffeineCacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        final CaffeineCacheManager cacheManager = new CaffeineCacheManager(CacheNames.CACHE_BY_NAME, CacheNames.CACHE_BY_LABELS);
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

}
