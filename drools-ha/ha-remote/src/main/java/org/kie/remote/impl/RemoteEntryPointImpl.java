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

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.kie.remote.RemoteEntryPoint;
import org.kie.remote.RemoteFactHandle;
import org.kie.remote.TopicsConfig;
import org.kie.remote.command.DeleteCommand;
import org.kie.remote.command.InsertCommand;
import org.kie.remote.command.UpdateCommand;
import org.kie.remote.impl.consumer.Listener;
import org.kie.remote.impl.producer.Sender;

public class RemoteEntryPointImpl extends AbstractRemoteEntryPoint implements RemoteEntryPoint {

    protected final RemoteStatefulSessionImpl delegate;

    protected RemoteEntryPointImpl(Sender sender, String entryPoint, TopicsConfig topicsConfig, Listener listener) {
        super(sender, entryPoint, topicsConfig);
        delegate = new RemoteStatefulSessionImpl( sender, listener, topicsConfig );
    }

    protected RemoteEntryPointImpl(Sender sender, String entryPoint, TopicsConfig topicsConfig, RemoteStatefulSessionImpl delegate) {
        super(sender, entryPoint, topicsConfig);
        this.delegate = delegate;
    }

    @Override
    public <T> RemoteFactHandle<T> insert(T obj) {
        RemoteFactHandle factHandle = new RemoteFactHandleImpl( (Serializable) obj );
        InsertCommand command = new InsertCommand( factHandle, entryPoint );
        sender.sendCommand(command, topicsConfig.getEventsTopicName());
        return factHandle;
    }

    @Override
    public <T> void delete( RemoteFactHandle<T> handle ) {
        DeleteCommand command = new DeleteCommand( handle, entryPoint );
        sender.sendCommand(command, topicsConfig.getEventsTopicName());
    }

    @Override
    public <T> void update( RemoteFactHandle<T> handle, T object ) {
        UpdateCommand command = new UpdateCommand( handle, object, entryPoint );
        sender.sendCommand(command, topicsConfig.getEventsTopicName());
    }

    protected Map<String, CompletableFuture<Object>> getRequestsStore() {
        return delegate.getRequestsStore();
    }
}