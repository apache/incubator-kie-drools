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

import java.net.SocketAddress;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.event.impl.ClientListenerNotifier;
import org.infinispan.client.hotrod.impl.operations.OperationsFactory;
import org.infinispan.client.hotrod.impl.operations.PingOperation;
import org.infinispan.client.hotrod.impl.operations.PingResponse;
import org.infinispan.client.hotrod.impl.transport.netty.ChannelFactory;

import jakarta.enterprise.inject.Instance;

/**
 * This is a health check implementation for Infinispan Hot Rod Server, based on client and
 * {@link RemoteCacheManager}. Basically it executes a ping operation to all nodes and if all are down it responds as
 * Down, otherwise it responds as Up.
 */
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

            final ChannelFactory channelFactory = cacheManager.getChannelFactory();
            final Configuration configuration = cacheManager.getConfiguration();
            final ClientListenerNotifier listenerNotifier = new ClientListenerNotifier(
                    cacheManager.getMarshaller(),
                    channelFactory,
                    configuration);
            final OperationsFactory operationsFactory = new OperationsFactory(channelFactory,
                    listenerNotifier,
                    configuration);

            return Optional.of(channelFactory
                    .getServers()
                    .stream()
                    .map(server -> invokePingOperation(channelFactory, operationsFactory, server)
                            .thenApply(PingResponse::isSuccess)
                            .exceptionally(ex -> false))
                    .map(op -> {
                        try {
                            return op.get(500, TimeUnit.MILLISECONDS);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .allMatch(Boolean.FALSE::equals))
                    .map(allDown -> buildResponse(channelFactory, !allDown))
                    .orElse(buildResponse(channelFactory, false));
        }).orElse(null);
    }

    private HealthCheckResponse buildResponse(ChannelFactory channelFactory, boolean state) {
        return HealthCheckResponse.builder()
                .withData("nodes", Optional.ofNullable(channelFactory.getServers())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")))
                .name(state ? "Infinispan is Up" : "Infinispan is Down")
                .status(state)
                .build();
    }

    private PingOperation invokePingOperation(ChannelFactory channelFactory, OperationsFactory operationsFactory, SocketAddress server) {
        return channelFactory.fetchChannelAndInvoke(server, operationsFactory.newPingOperation(true));
    }
}
