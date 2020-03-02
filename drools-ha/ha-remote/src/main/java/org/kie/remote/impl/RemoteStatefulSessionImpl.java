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

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.kie.remote.RemoteStatefulSession;
import org.kie.remote.TopicsConfig;
import org.kie.remote.command.FireAllRulesCommand;
import org.kie.remote.command.FireUntilHaltCommand;
import org.kie.remote.command.HaltCommand;
import org.kie.remote.impl.consumer.Listener;
import org.kie.remote.impl.producer.Sender;

public class RemoteStatefulSessionImpl implements RemoteStatefulSession {

    private final Sender sender;
    private final Listener listener;
    private final TopicsConfig topicsConfig;

    public RemoteStatefulSessionImpl( Sender sender, Listener listener, TopicsConfig topicsConfig ) {
        this.sender = sender;
        this.listener = listener;
        this.topicsConfig = topicsConfig;
    }

    @SuppressWarnings("unchecked conversion")
    @Override
    public CompletableFuture<Long> fireAllRules() {
        FireAllRulesCommand command = new FireAllRulesCommand();
        CompletableFuture<Long> callback = new CompletableFuture<>();
        ((Map) getRequestsStore()).put( command.getId(), callback );
        sender.sendCommand( command, topicsConfig.getEventsTopicName() );
        return callback;
    }

    public Map<String, CompletableFuture<Object>> getRequestsStore() {
        return listener.getRequestsStore();
    }

    @Override
    public void fireUntilHalt() {
        sender.sendCommand(new FireUntilHaltCommand(), topicsConfig.getEventsTopicName());
    }

    @Override
    public void halt() {
        sender.sendCommand(new HaltCommand(), topicsConfig.getEventsTopicName());
    }

    public void stop() {
        listener.stopConsumeEvents();
    }
}
