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
package org.kie.kogito.jobs.embedded;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestEventPublisher implements EventPublisher {
    private List<DataEvent<?>> events;

    private CountDownLatch latch;

    public List<DataEvent<?>> getEvents() {
        return events;
    }

    public TestEventPublisher() {
        events = new ArrayList<>();
    }

    @Override
    public void publish(DataEvent<?> event) {
        events.add(event);
        latch.countDown();
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        events.addAll(events);
        events.forEach(e -> latch.countDown());
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

}
