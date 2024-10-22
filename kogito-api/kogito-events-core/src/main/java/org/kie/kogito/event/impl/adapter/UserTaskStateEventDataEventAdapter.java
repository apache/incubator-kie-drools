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
package org.kie.kogito.event.impl.adapter;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateEventBody;
import org.kie.kogito.usertask.events.UserTaskStateEvent;

public class UserTaskStateEventDataEventAdapter extends AbstractDataEventAdapter {

    public UserTaskStateEventDataEventAdapter() {
        super(UserTaskStateEvent.class);
    }

    @Override
    public boolean accept(Object payload) {
        return payload instanceof UserTaskStateEvent event && event.getNewStatus() != null;
    }

    @Override
    public DataEvent<?> adapt(Object payload) {
        UserTaskStateEvent event = (UserTaskStateEvent) payload;
        Map<String, Object> metadata = AdapterHelper.buildUserTaskMetadata(event.getUserTaskInstance());

        UserTaskInstanceStateEventBody.Builder builder = UserTaskInstanceStateEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventUser())
                .userTaskDefinitionId(event.getUserTask().id())
                .userTaskInstanceId(event.getUserTaskInstance().getId())
                .userTaskName(event.getUserTaskInstance().getTaskName())
                .userTaskDescription(event.getUserTaskInstance().getTaskDescription())
                .userTaskPriority(event.getUserTaskInstance().getTaskPriority())
                .userTaskReferenceName(event.getUserTask().getReferenceName())
                .externalReferenceId(event.getUserTaskInstance().getExternalReferenceId())
                .state(event.getNewStatus().getName())
                .actualOwner(event.getUserTaskInstance().getActualOwner())
                .eventType(isTransition(event) ? event.getNewStatus().getName() : "Modify")
                .processInstanceId((String) event.getUserTaskInstance().getMetadata().get("ProcessInstanceId"));

        UserTaskInstanceStateEventBody body = builder.build();
        UserTaskInstanceStateDataEvent utEvent =
                new UserTaskInstanceStateDataEvent(AdapterHelper.buildSource(getConfig().service(), (String) event.getUserTaskInstance().getMetadata().get("ProcessId")),
                        getConfig().addons().toString(),
                        event.getEventUser(),
                        metadata, body);

        return utEvent;
    }

    private boolean isTransition(UserTaskStateEvent event) {
        return !Objects.equals(event.getOldStatus(), event.getNewStatus());
    }

}
