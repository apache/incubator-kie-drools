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

package org.kie.kogito.taskassigning.service.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BufferedUserTaskEventConsumer implements UserTaskEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedUserTaskEventConsumer.class);

    private List<UserTaskEvent> buffer = new ArrayList<>();

    private AtomicBoolean paused = new AtomicBoolean(true);

    private ReentrantLock lock = new ReentrantLock();

    private Consumer<List<UserTaskEvent>> consumer;

    public void setConsumer(Consumer<List<UserTaskEvent>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void pause() {
        lock.lock();
        LOGGER.debug("pause was invoked with current buffer.size: {}", buffer.size());
        try {
            paused.set(true);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void resume() {
        lock.lock();
        LOGGER.debug("resume was invoked with current buffer.size: {}", buffer.size());
        try {
            paused.set(false);
            if (!buffer.isEmpty()) {
                deliverToConsumer();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<UserTaskEvent> pollEvents() {
        lock.lock();
        try {
            LOGGER.debug("pollEvents was invoked with current buffer.size: {}", buffer.size());
            List<UserTaskEvent> result = new ArrayList<>(buffer);
            buffer.clear();
            return result;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int queuedEvents() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void accept(UserTaskEvent userTaskEvent) {
        lock.lock();
        try {
            LOGGER.debug("Event being accepted, current buffer.size: {},  paused: {}", buffer.size(), paused.get());
            buffer.add(userTaskEvent);
            if (!paused.get()) {
                LOGGER.debug("Delivering to consumer, current buffer.size: {}, paused: {}", buffer.size(), paused.get());
                deliverToConsumer();
            }
        } finally {
            lock.unlock();
        }
    }

    private void deliverToConsumer() {
        List<UserTaskEvent> result = new ArrayList<>(buffer);
        buffer.clear();
        consumer.accept(result);
    }
}