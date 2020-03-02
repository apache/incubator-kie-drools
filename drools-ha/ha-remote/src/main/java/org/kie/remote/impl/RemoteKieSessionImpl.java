/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.remote.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.kie.remote.RemoteEntryPoint;
import org.kie.remote.RemoteKieSession;
import org.kie.remote.TopicsConfig;
import org.kie.remote.command.GetKJarGAVCommand;
import org.kie.remote.command.UpdateKJarCommand;
import org.kie.remote.impl.consumer.Listener;
import org.kie.remote.impl.producer.Producer;
import org.kie.remote.impl.producer.Sender;

public class RemoteKieSessionImpl extends RemoteEntryPointImpl implements RemoteKieSession {

    private final Map<String, RemoteEntryPoint> entryPoints = new HashMap<>();

    public RemoteKieSessionImpl(Properties configuration, Listener listener, Producer producer) {
        this(configuration, TopicsConfig.getDefaultTopicsConfig(), listener, producer);
    }

    public RemoteKieSessionImpl(Properties configuration, TopicsConfig envConfig, Listener listener, Producer producer) {
        super(new Sender(configuration, producer), EntryPointUtil.DEFAULT_ENTRY_POINT, envConfig, listener);
        sender.start();
    }

    @Override
    public void close() {
        sender.stop();
        delegate.stop();
    }

    @Override
    public RemoteEntryPoint getEntryPoint(String name) {
        return entryPoints.computeIfAbsent(name,
                                           k -> new RemoteEntryPointImpl(sender,
                                                                         k,
                                                                         topicsConfig,
                                                                         delegate));
    }

    @Override
    public CompletableFuture<Long> fireAllRules() {
        return delegate.fireAllRules();
    }

    @Override
    public void fireUntilHalt() {
        delegate.fireUntilHalt();
    }

    @Override
    public void halt() {
        delegate.halt();
    }

    @Override
    public CompletableFuture<Boolean> updateKJarGAV(String kJar) {
        UpdateKJarCommand command = new UpdateKJarCommand(kJar);
        return executeCommand(command);
    }

    @Override
    public CompletableFuture<String> getKJarGAV() {
        GetKJarGAVCommand command = new GetKJarGAVCommand(entryPoint);
        return executeCommand(command);
    }
}