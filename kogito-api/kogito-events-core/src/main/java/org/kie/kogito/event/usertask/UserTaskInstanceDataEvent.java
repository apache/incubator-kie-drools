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
package org.kie.kogito.event.usertask;

import java.net.URI;
import java.util.Set;

import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserTaskInstanceDataEvent<T> extends AbstractDataEvent<T> {

    private static final Set<String> INTERNAL_EXTENSION_ATTRIBUTES = Set.of(
            CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID,
            CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE);

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID)
    private String kogitoUserTaskInstanceId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE)
    private String kogitoUserTaskInstanceState;

    public UserTaskInstanceDataEvent() {
    }

    public UserTaskInstanceDataEvent(T body) {
        setData(body);
    }

    protected UserTaskInstanceDataEvent(String type, URI source, T body) {
        super(type, source, body);
    }

    public UserTaskInstanceDataEvent(String type,
            String source,
            T body,
            String kogitoUserTaskInstanceId,
            String kogitoUserTaskInstanceState,
            String kogitoProcessInstanceId,
            String kogitoProcessInstanceVersion,
            String kogitoParentProcessInstanceId,
            String kogitoRootProcessInstanceId,
            String kogitoProcessId,
            String kogitoRootProcessId,
            String kogitoProcessInstanceState,
            String kogitoAddons,
            String kogitoProcessType,
            String kogitoReferenceId,
            String kogitoIdentity) {
        super(type,
                source,
                body,
                kogitoProcessInstanceId,
                kogitoRootProcessInstanceId,
                kogitoProcessId,
                kogitoRootProcessId,
                kogitoAddons,
                kogitoIdentity);
        setKogitoUserTaskInstanceId(kogitoUserTaskInstanceId);
        setKogitoUserTaskInstanceState(kogitoUserTaskInstanceState);
        setKogitoProcessInstanceVersion(kogitoProcessInstanceVersion);
        setKogitoParentProcessInstanceId(kogitoParentProcessInstanceId);
        setKogitoProcessInstanceState(kogitoProcessInstanceState);
        setKogitoReferenceId(kogitoReferenceId);
        setKogitoProcessType(kogitoProcessType);
    }

    public String getKogitoUserTaskInstanceId() {
        return kogitoUserTaskInstanceId;
    }

    public String getKogitoUserTaskInstanceState() {
        return kogitoUserTaskInstanceState;
    }

    public void setKogitoUserTaskInstanceId(String kogitoUserTaskInstanceId) {
        addExtensionAttribute(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID, kogitoUserTaskInstanceId);
    }

    public void setKogitoUserTaskInstanceState(String kogitoUserTaskInstanceState) {
        addExtensionAttribute(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE, kogitoUserTaskInstanceState);
    }

    @Override
    @JsonAnySetter
    public void addExtensionAttribute(String name, Object value) {
        if (value != null) {
            switch (name) {
                case CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE:
                    this.kogitoUserTaskInstanceState = (String) value;
                    break;
                case CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID:
                    this.kogitoUserTaskInstanceId = (String) value;
                    break;
            }
            super.addExtensionAttribute(name, value);
        }
    }

    @Override
    protected boolean isInternalAttribute(String name) {
        return INTERNAL_EXTENSION_ATTRIBUTES.contains(name) || super.isInternalAttribute(name);
    }

}
