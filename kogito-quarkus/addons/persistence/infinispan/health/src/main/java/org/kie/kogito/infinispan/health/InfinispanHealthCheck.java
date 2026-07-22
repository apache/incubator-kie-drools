/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.infinispan.health;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.infinispan.client.hotrod.RemoteCacheManager;

import jakarta.enterprise.inject.Instance;

/**
 * This is a health check implementation for Infinispan Hot Rod Server, based on client and
 * {@link RemoteCacheManager}. Performs a remote roundtrip against the cluster: if it succeeds the
 * server responds as Up, otherwise as Down.
 */
// The "cluster reachable" probe uses RemoteCacheManager#getCacheNames() because Infinispan's
// public hotrod API no longer exposes a per-server probe. Restore per-server probes if Infinispan
// reintroduces them.
public class InfinispanHealthCheck implements HealthCheck {

    private Optional<RemoteCacheManager> cacheManagerOptional;

    public InfinispanHealthCheck(Instance<RemoteCacheManager> cacheManagerInstance) {
        this.cacheManagerOptional = Optional.of(cacheManagerInstance)
                .filter(Instance::isResolvable)
                .map(Instance::get);
    }

    @Override
    public HealthCheckResponse call() {
        return cacheManagerOptional.map(cacheManager -> {
            boolean up;
            try {
                cacheManager.getCacheNames();
                up = true;
            } catch (Exception ex) {
                up = false;
            }
            return buildResponse(cacheManager, up);
        }).orElse(null);
    }

    private HealthCheckResponse buildResponse(RemoteCacheManager cacheManager, boolean state) {
        return HealthCheckResponse.builder()
                .withData("nodes", Optional.ofNullable(cacheManager.getServers())
                        .map(Stream::of)
                        .orElseGet(Stream::empty)
                        .collect(Collectors.joining(",")))
                .name(state ? "Infinispan is Up" : "Infinispan is Down")
                .status(state)
                .build();
    }
}
