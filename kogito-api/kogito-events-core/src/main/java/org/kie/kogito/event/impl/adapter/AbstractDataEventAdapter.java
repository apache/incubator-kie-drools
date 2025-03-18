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
import java.util.function.BiConsumer;

import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessNodeEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;

public abstract class AbstractDataEventAdapter implements DataEventAdapter {

    private DataEventAdapterConfig config;

    private Class<?> type;

    public AbstractDataEventAdapter(Class<?> type) {
        this.type = type;
    }

    @Override
    public void setup(DataEventAdapterConfig config) {
        this.config = config;
    }

    public DataEventAdapterConfig getConfig() {
        return config;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    protected ProcessInstanceStateDataEvent adapt(ProcessEvent event, Integer eventType) {
        Map<String, Object> metadata = AdapterHelper.buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance());

        KogitoWorkflowProcessInstance pi = (KogitoWorkflowProcessInstance) event.getProcessInstance();

        ProcessInstanceStateEventBody.Builder builder = ProcessInstanceStateEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventIdentity())
                .eventType(eventType)
                .processId(event.getProcessInstance().getProcessId())
                .processVersion(event.getProcessInstance().getProcessVersion())
                .processInstanceId(event.getProcessInstance().getId())
                .processName(event.getProcessInstance().getProcessName())
                .processVersion(event.getProcessInstance().getProcessVersion())
                .processType(event.getProcessInstance().getProcess().getType())
                .parentInstanceId(pi.getParentProcessInstanceId())
                .rootProcessId(pi.getRootProcessId())
                .rootProcessInstanceId(pi.getRootProcessInstanceId())
                .state(event.getProcessInstance().getState())
                .businessKey(pi.getBusinessKey())
                .slaDueDate(pi.getSlaDueDate());

        String securityRoles = (String) event.getProcessInstance().getProcess().getMetaData().get("securityRoles");
        if (securityRoles != null) {
            builder.roles(securityRoles.split(","));
        }

        ProcessInstanceStateEventBody body = builder.build();
        ProcessInstanceStateDataEvent piEvent =
                new ProcessInstanceStateDataEvent(AdapterHelper.buildSource(getConfig().service(), event.getProcessInstance().getProcessId()), getConfig().addons().toString(),
                        event.getEventIdentity(), metadata, body);
        piEvent.setKogitoBusinessKey(pi.getBusinessKey());
        return piEvent;
    }

    protected ProcessInstanceNodeDataEvent toProcessInstanceNodeEvent(ProcessNodeTriggeredEvent event, int eventType) {
        return toProcessInstanceNodeEvent(event, eventType, (k, v) -> k.setRetrigger(v.isRetrigger()));
    }

    protected ProcessInstanceNodeDataEvent toProcessInstanceNodeEvent(ProcessNodeEvent event, int eventType) {
        return toProcessInstanceNodeEvent(event, eventType, (k, v) -> {
        });
    }

    protected <T extends ProcessNodeEvent> ProcessInstanceNodeDataEvent toProcessInstanceNodeEvent(T event, int eventType, BiConsumer<ProcessInstanceNodeEventBody.Builder, T> consumer) {
        Map<String, Object> metadata = AdapterHelper.buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance());
        KogitoWorkflowProcessInstance pi = (KogitoWorkflowProcessInstance) event.getProcessInstance();
        KogitoNodeInstance nodeInstance = (KogitoNodeInstance) event.getNodeInstance();
        ProcessInstanceNodeEventBody.Builder builder = ProcessInstanceNodeEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventIdentity())
                .eventType(eventType)
                .processId(event.getProcessInstance().getProcessId())
                .processVersion(event.getProcessInstance().getProcessVersion())
                .processInstanceId(event.getProcessInstance().getId())
                .nodeName(event.getNodeInstance().getNodeName())
                .nodeType(event.getNodeInstance().getNode().getClass().getSimpleName())
                .nodeInstanceId(event.getNodeInstance().getId())
                .nodeDefinitionId(event.getNodeInstance().getNode().getUniqueId())
                .slaDueDate(nodeInstance.getSlaDueDate());
        consumer.accept(builder, event);
        if (event.getNodeInstance() instanceof KogitoWorkItemNodeInstance workItemNodeInstance && workItemNodeInstance.getWorkItem() != null) {
            KogitoWorkItem workItem = workItemNodeInstance.getWorkItem();
            builder.workItemId(workItem.getStringId());
            builder.data("WorkItemId", workItem.getStringId());
            builder.data("WorkItemExternalReferenceId", workItem.getExternalReferenceId());
        }

        if (eventType == ProcessInstanceNodeEventBody.EVENT_TYPE_ENTER) {
            builder.connectionNodeDefinitionId((String) nodeInstance.getMetaData().get("IncomingConnection"));
        } else {
            builder.connectionNodeDefinitionId((String) nodeInstance.getMetaData().get("OutgoingConnection"));
        }

        ProcessInstanceNodeEventBody body = builder.build();
        ProcessInstanceNodeDataEvent piEvent = new ProcessInstanceNodeDataEvent(AdapterHelper.buildSource(getConfig().service(), event.getProcessInstance().getProcessId()),
                getConfig().addons().toString(), event.getEventIdentity(), metadata, body);
        piEvent.setKogitoBusinessKey(pi.getBusinessKey());
        return piEvent;
    }
}
