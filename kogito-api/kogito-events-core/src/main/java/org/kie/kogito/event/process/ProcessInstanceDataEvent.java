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
package org.kie.kogito.event.process;

import java.net.URI;
import java.util.Map;

import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.DataEventState;

public class ProcessInstanceDataEvent<T> extends AbstractDataEvent<T> {

    public ProcessInstanceDataEvent() {
    }

    public ProcessInstanceDataEvent(T body) {
        setData(body);
    }

    protected ProcessInstanceDataEvent(String type, URI source, T body) {
        super(type, source, body);
    }

    @Deprecated
    public ProcessInstanceDataEvent(String type,
            String source,
            T body,
            String kogitoProcessInstanceId,
            String kogitoProcessVersion,
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
        setKogitoProcessVersion(kogitoProcessVersion);
        setKogitoParentProcessInstanceId(kogitoParentProcessInstanceId);
        setKogitoProcessInstanceState(kogitoProcessInstanceState);
        setKogitoReferenceId(kogitoReferenceId);
        setKogitoProcessType(kogitoProcessType);
    }

    public ProcessInstanceDataEvent(ProcessInstanceDataEventState<T> processInstanceDataEventState) {
        super(processInstanceDataEventState.baseState());
    }

    public static <T> ProcessInstanceDataEventBuilder<T> baseBuilder() {
        return new ProcessInstanceDataEventBuilder<>();
    }

    public abstract static class AbstractProcessInstanceDataEventBuilder<B extends AbstractProcessInstanceDataEventBuilder<B, T>, T> extends AbstractDataEvent.AbstractDataEventBuilder<B, T> {

        public AbstractProcessInstanceDataEventBuilder<B, T> metaData(Map<String, Object> metaData) {
            if (metaData != null) {
                this.kogitoProcessInstanceId = (String) metaData.get(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA);
                this.kogitoParentProcessInstanceId = (String) metaData.get(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA);
                this.kogitoRootProcessInstanceId = (String) metaData.get(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA);
                this.kogitoProcessId = (String) metaData.get(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA);
                this.kogitoProcessVersion = (String) metaData.get(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA);
                this.kogitoRootProcessId = (String) metaData.get(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA);
                this.kogitoRootProcessVersion = (String) metaData.get(ProcessInstanceEventMetadata.ROOT_PROCESS_VERSION_META_DATA);
                this.kogitoProcessInstanceState = (String) metaData.get(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA);
                this.kogitoProcessType = (String) metaData.get(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA);
            }
            return self();
        }

        protected ProcessInstanceDataEventState<T> toStateRecord() {
            return new ProcessInstanceDataEventState<>(
                    this.toCommonStateRecord());
        }
    }

    public static class ProcessInstanceDataEventBuilder<T>
            extends AbstractProcessInstanceDataEventBuilder<ProcessInstanceDataEventBuilder<T>, T> {

        public ProcessInstanceDataEventBuilder() {
        }

        public ProcessInstanceDataEvent<T> build() {
            return new ProcessInstanceDataEvent<>(this.toStateRecord());
        }
    }

    public record ProcessInstanceDataEventState<E> (
            DataEventState<E> baseState) {
    }
}
