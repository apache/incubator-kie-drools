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

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableEventBody;
import org.kie.kogito.usertask.events.UserTaskVariableEvent;

public class UserTaskVariableEventDataEventAdapter extends AbstractDataEventAdapter {

    public UserTaskVariableEventDataEventAdapter() {
        super(UserTaskVariableEvent.class);
    }

    @Override
    public DataEvent<?> adapt(Object payload) {
        UserTaskVariableEvent event = (UserTaskVariableEvent) payload;
        Map<String, Object> metadata = AdapterHelper.buildUserTaskMetadata(event.getUserTaskInstance());

        UserTaskInstanceVariableEventBody.Builder builder = UserTaskInstanceVariableEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventUser())
                .userTaskDefinitionId(event.getUserTask().id())
                .userTaskInstanceId(event.getUserTaskInstance().getId())
                .userTaskName(event.getUserTaskInstance().getTaskName())
                .variableId(event.getVariableName())
                .variableName(event.getVariableName())
                .variableValue(event.getNewValue())
                .variableType(event.getVariableType().name());

        UserTaskInstanceVariableEventBody body = builder.build();
        UserTaskInstanceVariableDataEvent utEvent =
                new UserTaskInstanceVariableDataEvent(AdapterHelper.buildSource(getConfig().service(), event.getUserTaskInstance().getExternalReferenceId()), getConfig().addons().toString(),
                        event.getEventUser(), metadata, body);

        return utEvent;
    }

}
