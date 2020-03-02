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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.kie.remote.RemoteFactHandle;
import org.kie.remote.RemoteWorkingMemory;
import org.kie.remote.TopicsConfig;
import org.kie.remote.command.AbstractCommand;
import org.kie.remote.command.FactCountCommand;
import org.kie.remote.command.GetObjectCommand;
import org.kie.remote.command.ListObjectsCommand;
import org.kie.remote.command.ListObjectsCommandClassType;
import org.kie.remote.command.ListObjectsCommandNamedQuery;
import org.kie.remote.impl.producer.Sender;

public abstract class AbstractRemoteEntryPoint implements RemoteWorkingMemory {

    protected final Sender sender;
    protected final String entryPoint;
    protected TopicsConfig topicsConfig;

    public AbstractRemoteEntryPoint(Sender sender, String entryPoint, TopicsConfig topicsConfig) {
        this.sender = sender;
        this.entryPoint = entryPoint;
        this.topicsConfig = topicsConfig;
    }

    @Override
    public String getEntryPointId() {
        return entryPoint;
    }

    @Override
    public CompletableFuture<Collection> getObjects() {
        ListObjectsCommand command = new ListObjectsCommand(entryPoint);
        return executeCommand(command);
    }

    @Override
    public <T> CompletableFuture<Collection<T>> getObjects(Class<T> clazztype) {
        ListObjectsCommand command = new ListObjectsCommandClassType(entryPoint, clazztype);
        return executeCommand(command);
    }

    @Override
    public CompletableFuture<Collection> getObjects(String namedQuery, String objectName, Object... params) {
        ListObjectsCommand command = new ListObjectsCommandNamedQuery(entryPoint, namedQuery, objectName, params);
        return executeCommand(command);
    }

    @Override
    public CompletableFuture<Long> getFactCount() {
        FactCountCommand command = new FactCountCommand(entryPoint);
        return executeCommand(command);
    }

    @Override
    public <T> CompletableFuture<T> getObject(RemoteFactHandle<T> remoteFactHandle) {
        GetObjectCommand command = new GetObjectCommand(remoteFactHandle);
        return executeCommand(command);
    }

    protected <T> CompletableFuture<T> executeCommand(AbstractCommand command) {
        CompletableFuture callback = new CompletableFuture<>();
        getRequestsStore().put(command.getId(), callback);
        sender.sendCommand(command, topicsConfig.getEventsTopicName());
        return callback;
    }

    protected abstract Map<String, CompletableFuture<Object>> getRequestsStore();
}
