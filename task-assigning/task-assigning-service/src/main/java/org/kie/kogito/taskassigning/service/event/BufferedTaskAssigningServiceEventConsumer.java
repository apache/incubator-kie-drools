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

package org.kie.kogito.taskassigning.service.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BufferedTaskAssigningServiceEventConsumer implements TaskAssigningServiceEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedTaskAssigningServiceEventConsumer.class);

    private final List<DataEvent<?>> buffer = new ArrayList<>();

    private final AtomicBoolean paused = new AtomicBoolean(true);

    private Consumer<List<DataEvent<?>>> consumer;

    public void setConsumer(Consumer<List<DataEvent<?>>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public synchronized void pause() {
        LOGGER.debug("pause was invoked with current buffer.size: {}", buffer.size());
        paused.set(true);
    }

    @Override
    public synchronized void resume() {
        LOGGER.debug("resume was invoked with current buffer.size: {}", buffer.size());
        paused.set(false);
        if (!buffer.isEmpty()) {
            deliverToConsumer();
        }
    }

    @Override
    public synchronized List<DataEvent<?>> pollEvents() {
        LOGGER.debug("pollEvents was invoked with current buffer.size: {}", buffer.size());
        List<DataEvent<?>> result = new ArrayList<>(buffer);
        buffer.clear();
        return result;
    }

    @Override
    public synchronized int queuedEvents() {
        return buffer.size();
    }

    @Override
    public synchronized void accept(DataEvent<?> dataEvent) {
        LOGGER.debug("Event {} being accepted, current buffer.size: {},  paused: {}", dataEvent.getDataEventType(), buffer.size(), paused.get());
        buffer.add(dataEvent);
        if (!paused.get()) {
            LOGGER.debug("Delivering to consumer, current buffer.size: {}, paused: {}", buffer.size(), paused.get());
            deliverToConsumer();
        }
    }

    private void deliverToConsumer() {
        List<DataEvent<?>> result = new ArrayList<>(buffer);
        buffer.clear();
        consumer.accept(result);
    }
}