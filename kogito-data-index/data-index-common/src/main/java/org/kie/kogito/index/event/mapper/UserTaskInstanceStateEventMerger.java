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
package org.kie.kogito.index.event.mapper;

import java.net.URI;
import java.util.List;

import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.index.model.UserTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.UrlEscapers;

import jakarta.enterprise.context.ApplicationScoped;

import static java.lang.String.format;
import static org.kie.kogito.index.DateTimeUtils.toZonedDateTime;

@ApplicationScoped
public class UserTaskInstanceStateEventMerger implements UserTaskInstanceEventMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTaskInstanceStateEventMerger.class);

    @Override
    public boolean accept(Object event) {
        return event instanceof UserTaskInstanceStateDataEvent;
    }

    @Override
    public UserTaskInstance merge(UserTaskInstance task, UserTaskInstanceDataEvent<?> data) {
        List<String> finalState = List.of("Completed", "Aborted");

        UserTaskInstanceStateDataEvent event = (UserTaskInstanceStateDataEvent) data;
        LOGGER.debug("value before merging: {}", task);
        task.setId(event.getData().getUserTaskInstanceId());
        task.setProcessInstanceId(event.getData().getProcessInstanceId());
        task.setProcessId(event.getKogitoProcessId());
        task.setRootProcessId(event.getKogitoRootProcessId());
        task.setRootProcessInstanceId(event.getKogitoRootProcessInstanceId());
        task.setName(event.getData().getUserTaskName());
        task.setDescription(event.getData().getUserTaskDescription());
        task.setState(event.getData().getState());
        task.setPriority(event.getData().getUserTaskPriority());
        if (task.getStarted() == null) {
            task.setStarted(toZonedDateTime(event.getData().getEventDate()));
        } else if (finalState.contains(event.getData().getEventType())) {
            task.setCompleted(toZonedDateTime(event.getData().getEventDate()));
        }

        task.setActualOwner(event.getData().getActualOwner());
        task.setEndpoint(
                event.getSource() == null ? null : getEndpoint(event.getSource(), event.getData().getProcessInstanceId(), event.getData().getUserTaskName(), event.getData().getUserTaskInstanceId()));
        task.setLastUpdate(toZonedDateTime(event.getData().getEventDate()));
        task.setReferenceName(event.getData().getUserTaskReferenceName());
        LOGGER.debug("value after merging: {}", task);
        return task;
    }

    public String getEndpoint(URI source, String pId, String taskName, String taskId) {
        String name = UrlEscapers.urlPathSegmentEscaper().escape(taskName);
        return source.toString() + format("/%s/%s/%s", pId, name, taskId);
    }

}
