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

import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;
import org.kie.kogito.internal.process.event.KogitoProcessVariableChangedEvent;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.kie.kogito.internal.utils.KogitoTags;

public class ProcessVariableEventDataEventAdapter extends AbstractDataEventAdapter {

    public ProcessVariableEventDataEventAdapter() {
        super(ProcessVariableChangedEvent.class);
    }

    @Override
    public boolean accept(Object payload) {
        return payload instanceof ProcessVariableChangedEvent event && !event.getTags().contains(KogitoTags.INTERNAL_TAG);
    }

    @Override
    public DataEvent<?> adapt(Object payload) {
        ProcessVariableChangedEvent event = (ProcessVariableChangedEvent) payload;
        Map<String, Object> metadata = AdapterHelper.buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance());
        KogitoWorkflowProcessInstance pi = (KogitoWorkflowProcessInstance) event.getProcessInstance();
        ProcessInstanceVariableEventBody.Builder builder = ProcessInstanceVariableEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventIdentity())
                .processId(event.getProcessInstance().getProcessId())
                .processVersion(event.getProcessInstance().getProcessVersion())
                .processInstanceId(event.getProcessInstance().getId())
                .variableId(event.getVariableInstanceId())
                .variableName(event.getVariableId())
                .variableValue(event.getNewValue());

        if (event instanceof KogitoProcessVariableChangedEvent varEvent) {
            if (varEvent.getNodeInstance() != null && varEvent.getNodeInstance().getNodeInstanceContainer() != null) {
                if (varEvent.getNodeInstance().getNodeInstanceContainer() instanceof KogitoNodeInstance) {
                    builder.nodeContainerDefinitionId(((KogitoNodeInstance) varEvent.getNodeInstance().getNodeInstanceContainer()).getNodeDefinitionId());
                    builder.nodeContainerInstanceId(((KogitoNodeInstance) varEvent.getNodeInstance().getNodeInstanceContainer()).getId());
                }
            }
        }

        ProcessInstanceVariableEventBody body = builder.build();
        ProcessInstanceVariableDataEvent piEvent =
                new ProcessInstanceVariableDataEvent(AdapterHelper.buildSource(getConfig().service(), event.getProcessInstance().getProcessId()), getConfig().addons().toString(),
                        event.getEventIdentity(), metadata, body);
        piEvent.setKogitoBusinessKey(pi.getBusinessKey());
        return piEvent;
    }

}
