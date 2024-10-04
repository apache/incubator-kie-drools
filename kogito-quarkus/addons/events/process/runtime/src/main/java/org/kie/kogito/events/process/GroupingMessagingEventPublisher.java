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
package org.kie.kogito.events.process;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.MultipleProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.MultipleUserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;

import io.quarkus.arc.properties.IfBuildProperty;

import jakarta.inject.Singleton;

@Singleton
@IfBuildProperty(name = "kogito.events.grouping", stringValue = "true")
public class GroupingMessagingEventPublisher extends AbstractMessagingEventPublisher {

    @Override
    public void publish(DataEvent<?> event) {
        publish(Collections.singletonList(event));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void publish(Collection<DataEvent<?>> events) {
        Map<AbstractMessageEmitter, Collection> eventsByChannel = new HashMap<>();
        for (DataEvent<?> event : events) {
            getConsumer(event).ifPresent(c -> eventsByChannel.computeIfAbsent(c, k -> new ArrayList<>()).add(event));
        }
        eventsByChannel.entrySet().forEach(this::publishEvents);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void publishEvents(Map.Entry<AbstractMessageEmitter, Collection> entry) {
        DataEvent<?> firstEvent = (DataEvent<?>) entry.getValue().iterator().next();
        URI source = firstEvent.getSource();
        if (firstEvent instanceof UserTaskInstanceDataEvent) {
            publishToTopic(entry.getKey(), new MultipleUserTaskInstanceDataEvent(source, (Collection<UserTaskInstanceDataEvent<?>>) entry.getValue()));
        } else if (firstEvent instanceof ProcessInstanceDataEvent) {
            publishToTopic(entry.getKey(), new MultipleProcessInstanceDataEvent(source, (Collection<ProcessInstanceDataEvent<?>>) entry.getValue()));
        } else {
            for (DataEvent<?> event : (Collection<DataEvent<?>>) entry.getValue()) {
                publishToTopic(entry.getKey(), event);
            }
        }
    }
}
