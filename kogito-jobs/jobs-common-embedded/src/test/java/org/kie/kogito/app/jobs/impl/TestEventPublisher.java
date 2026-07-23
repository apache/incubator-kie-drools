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
package org.kie.kogito.app.jobs.impl;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestEventPublisher implements EventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(TestEventPublisher.class);

    private final AtomicInteger publishedEventsCount;

    public TestEventPublisher() {
        this.publishedEventsCount = new AtomicInteger(0);
    }

    @Override
    public void publish(DataEvent<?> event) {
        JobInstanceDataEvent jobInstanceDataEvent = (JobInstanceDataEvent) event;
        this.publishedEventsCount.incrementAndGet();
        LOG.info("job event {}, publishedEventsCount {}", new String(jobInstanceDataEvent.getData()), publishedEventsCount.get());
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        events.forEach(this::publish);
    }

    public int getPublishedEventsCount() {
        return publishedEventsCount.get();
    }

}
