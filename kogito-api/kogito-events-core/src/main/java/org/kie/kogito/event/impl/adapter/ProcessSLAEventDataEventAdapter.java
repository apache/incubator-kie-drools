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

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLAEventBody;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;

public class ProcessSLAEventDataEventAdapter extends AbstractDataEventAdapter {

    public ProcessSLAEventDataEventAdapter() {
        super(SLAViolatedEvent.class);
    }

    @Override
    public DataEvent<?> adapt(Object payload) {
        SLAViolatedEvent event = (SLAViolatedEvent) payload;
        Map<String, Object> metadata = AdapterHelper.buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance());
        KogitoWorkflowProcessInstance pi = (KogitoWorkflowProcessInstance) event.getProcessInstance();

        ProcessInstanceSLAEventBody.Builder builder = ProcessInstanceSLAEventBody.create()
                .eventDate(Date.from(Instant.now()))
                .eventUser(event.getEventIdentity())
                .processId(event.getProcessInstance().getProcessId())
                .processVersion(event.getProcessInstance().getProcessVersion())
                .processInstanceId(event.getProcessInstance().getId());

        if (event.getNodeInstance() instanceof KogitoNodeInstance) {
            KogitoNodeInstance ni = (KogitoNodeInstance) event.getNodeInstance();
            builder.nodeDefinitionId(ni.getNode().getUniqueId())
                    .nodeInstanceId(ni.getId())
                    .nodeName(ni.getNodeName())
                    .nodeType(ni.getNode().getClass().getSimpleName())
                    .slaDueDate(ni.getSlaDueDate());
        } else {
            builder.slaDueDate(pi.getSlaDueDate());
        }

        ProcessInstanceSLAEventBody body = builder.build();
        ProcessInstanceSLADataEvent piEvent = new ProcessInstanceSLADataEvent(AdapterHelper.buildSource(getConfig().service(), event.getProcessInstance().getProcessId()),
                getConfig().addons().toString(), event.getEventIdentity(), metadata, body);
        piEvent.setKogitoBusinessKey(pi.getBusinessKey());
        return piEvent;
    }

}
