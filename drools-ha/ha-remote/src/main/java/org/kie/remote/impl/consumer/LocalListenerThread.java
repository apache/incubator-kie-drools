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

package org.kie.remote.impl.consumer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.kie.remote.TopicsConfig;
import org.kie.remote.message.ResultMessage;
import org.kie.remote.util.LocalMessageSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalListenerThread implements ListenerThread {

    private static Logger logger = LoggerFactory.getLogger(LocalListenerThread.class);

    private final LocalMessageSystem queue = LocalMessageSystem.get();

    private TopicsConfig topicsConfig;
    private Map<String, CompletableFuture<Object>> requestsStore;

    private volatile boolean running = true;

    public LocalListenerThread(TopicsConfig topicsConfig) {
        this.topicsConfig = topicsConfig;
    }

    @Override
    public void init(Map<String, CompletableFuture<Object>> requestsStore) {
        this.requestsStore = requestsStore;
    }

    @Override
    public void run() {
        while (running) {
            Object msg = queue.poll(topicsConfig.getKieSessionInfosTopicName());
            if (msg instanceof ResultMessage) {
                complete(requestsStore, (ResultMessage) msg, logger);
            } else if (msg != null) {
                throw new IllegalStateException("Wrong type of response message: found " +
                                                        msg.getClass().getCanonicalName() +
                                                        " instead of " +
                                                        ResultMessage.class.getCanonicalName());
            }
        }
    }

    private void complete(Map<String, CompletableFuture<Object>> requestsStore, ResultMessage message, Logger logger) {
        CompletableFuture<Object> completableFuture = requestsStore.get(message.getId());
        if (completableFuture != null) {
            completableFuture.complete(message.getResult());
            if (logger.isDebugEnabled()) {
                logger.debug("completed msg with key {}", message.getId());
            }
        }
    }

    @Override
    public void stop() {
        running = false;
    }
}
