/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.process;

import java.util.Map;

import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserTaskInstanceDataEvent extends AbstractDataEvent<UserTaskInstanceEventBody> {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID)
    private String kogitoUserTaskinstanceId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE)
    private String kogitoUserTaskinstanceState;

    public UserTaskInstanceDataEvent() {
    }

    public UserTaskInstanceDataEvent(String source, String addons, Map<String, String> metaData, UserTaskInstanceEventBody body) {

        super("UserTaskInstanceEvent",
                source,
                body,
                metaData.get(ProcessInstanceEventBody.ID_META_DATA),
                metaData.get(ProcessInstanceEventBody.ROOT_ID_META_DATA),
                metaData.get(ProcessInstanceEventBody.PROCESS_ID_META_DATA),
                metaData.get(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA),
                addons);
        addExtensionAttribute(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE, metaData.get(UserTaskInstanceEventBody.UT_STATE_META_DATA));
        addExtensionAttribute(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID, metaData.get(metaData.get(UserTaskInstanceEventBody.UT_ID_META_DATA)));
    }

    public String getKogitoUserTaskinstanceId() {
        return kogitoUserTaskinstanceId;
    }

    public String getKogitoUserTaskinstanceState() {
        return kogitoUserTaskinstanceState;
    }

    @Override
    @JsonAnySetter
    public void addExtensionAttribute(String name, Object value) {
        if (value != null) {
            switch (name) {
                case CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE:
                    this.kogitoUserTaskinstanceState = (String) value;
                    break;
                case CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID:
                    this.kogitoUserTaskinstanceId = (String) value;
            }
            super.addExtensionAttribute(name, value);
        }
    }
}
