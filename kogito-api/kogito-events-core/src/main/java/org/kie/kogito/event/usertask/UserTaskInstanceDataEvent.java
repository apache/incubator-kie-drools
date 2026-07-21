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
import java.util.Map;
import java.util.Set;

import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.DataEventState;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.process.ProcessInstanceEventMetadata;

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

    @Deprecated
    public UserTaskInstanceDataEvent(String type,
            String source,
            T body,
            String kogitoUserTaskInstanceId,
            String kogitoUserTaskInstanceState,
            String kogitoProcessInstanceId,
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

    public UserTaskInstanceDataEvent(UserTaskInstanceDataEventState<T> state) {
        super(state.baseState());
        setKogitoUserTaskInstanceId(state.kogitoUserTaskInstanceId());
        setKogitoUserTaskInstanceState(state.kogitoUserTaskInstanceState());
    }

    public static <T> UserTaskInstanceDataEventBuilder<?, T> builder() {
        return new InnerUserTaskInstanceDataEventBuilder<>();
    }

    public abstract static class UserTaskInstanceDataEventBuilder<B extends UserTaskInstanceDataEventBuilder<B, T>, T> extends AbstractDataEvent.AbstractDataEventBuilder<B, T> {
        private String kogitoUserTaskInstanceId;
        private String kogitoUserTaskInstanceState;

        public static <T> UserTaskInstanceDataEventBuilder<?, T> builder() {
            return new InnerUserTaskInstanceDataEventBuilder<>();
        }

        public UserTaskInstanceDataEventBuilder<B, T> kogitoUserTaskInstanceId(String userTaskInstanceId) {
            this.kogitoUserTaskInstanceId = userTaskInstanceId;
            return self();
        }

        public UserTaskInstanceDataEventBuilder<B, T> kogitoUserTaskInstanceState(String userTaskInstanceState) {
            this.kogitoUserTaskInstanceState = userTaskInstanceState;
            return self();
        }

        public UserTaskInstanceDataEventBuilder<B, T> metaData(Map<String, Object> metaData) {
            if (metaData == null) {
                return self();
            }
            return this
                    .kogitoProcessInstanceId((String) metaData.get(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA))
                    .kogitoParentProcessInstanceId((String) metaData.get(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA))
                    .kogitoRootProcessInstanceId((String) metaData.get(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA))
                    .kogitoProcessId((String) metaData.get(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA))
                    .kogitoProcessVersion((String) metaData.get(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA))
                    .kogitoRootProcessId((String) metaData.get(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA))
                    .kogitoRootProcessVersion((String) metaData.get(ProcessInstanceEventMetadata.ROOT_PROCESS_VERSION_META_DATA))
                    .kogitoProcessInstanceState((String) metaData.get(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA))
                    .kogitoProcessType((String) metaData.get(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA))
                    .kogitoReferenceId((String) metaData.get(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_REFERENCE_ID_META_DATA))
                    .kogitoUserTaskInstanceId((String) metaData.get(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_ID_META_DATA))
                    .kogitoUserTaskInstanceState((String) metaData.get(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_STATE_META_DATA));
        }

        protected UserTaskInstanceDataEventState<T> toStateRecord() {
            return new UserTaskInstanceDataEventState<>(this.toCommonStateRecord(), kogitoUserTaskInstanceId, kogitoUserTaskInstanceState);
        }
    }

    // Private helper class to fulfill the recursive generic structure
    private static class InnerUserTaskInstanceDataEventBuilder<T>
            extends UserTaskInstanceDataEventBuilder<InnerUserTaskInstanceDataEventBuilder<T>, T> {

        public InnerUserTaskInstanceDataEventBuilder() {
        }
    }

    public record UserTaskInstanceDataEventState<E> (
            DataEventState<E> baseState,
            String kogitoUserTaskInstanceId,
            String kogitoUserTaskInstanceState) {
    }

}
