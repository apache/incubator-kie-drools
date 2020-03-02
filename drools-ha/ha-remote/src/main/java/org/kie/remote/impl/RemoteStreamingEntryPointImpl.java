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

import org.kie.remote.RemoteStreamingEntryPoint;
import org.kie.remote.TopicsConfig;
import org.kie.remote.command.EventInsertCommand;
import org.kie.remote.impl.consumer.Listener;
import org.kie.remote.impl.producer.Sender;

public class RemoteStreamingEntryPointImpl extends AbstractRemoteEntryPoint implements RemoteStreamingEntryPoint {

    protected final RemoteStatefulSessionImpl delegate;

    protected RemoteStreamingEntryPointImpl(Sender sender, String entryPoint, TopicsConfig topicsConfig, Listener listener) {
        super(sender, entryPoint, topicsConfig);
        delegate = new RemoteStatefulSessionImpl( sender, listener, topicsConfig );
    }

    protected RemoteStreamingEntryPointImpl(Sender sender, String entryPoint, TopicsConfig topicsConfig, RemoteStatefulSessionImpl delegate) {
        super(sender, entryPoint, topicsConfig);
        this.delegate = delegate;
    }

    @Override
    public void insert(Serializable object) {
        EventInsertCommand command = new EventInsertCommand(object, entryPoint);
        sender.sendCommand(command, topicsConfig.getEventsTopicName());
    }

    protected Map<String, CompletableFuture<Object>> getRequestsStore() {
        return delegate.getRequestsStore();
    }
}