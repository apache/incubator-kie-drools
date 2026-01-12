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
package org.kie.kogito.serverless.workflow.devservices;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.kogito.event.cloudevents.extension.ProcessMeta;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.services.context.ProcessInstanceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class DevModeServerlessWorkflowLogger extends DefaultKogitoProcessEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevModeServerlessWorkflowLogger.class);

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        String processInstanceId = ((KogitoProcessInstance) event.getProcessInstance()).getStringId();
        ProcessInstanceContext.setProcessInstanceId(processInstanceId);
        org.slf4j.MDC.put("processInstanceId", processInstanceId);
        LOGGER.info("Starting workflow '{}' ({})", event.getProcessInstance().getProcessId(), processInstanceId);
        JsonNode node = (JsonNode) ((KogitoProcessInstance) event.getProcessInstance()).getVariables().get(SWFConstants.DEFAULT_WORKFLOW_VAR);
        if (!node.isEmpty()) {
            LOGGER.info("Workflow data \n{}", node.toPrettyString());
        }
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        if (event.getProcessInstance().getState() != 2) {
            String processInstanceId = ((KogitoProcessInstance) event.getProcessInstance()).getStringId();
            ProcessInstanceContext.setProcessInstanceId(processInstanceId);
            LOGGER.info("Workflow '{}' ({}) was started, now '{}'", event.getProcessInstance().getProcessId(), processInstanceId,
                    ProcessMeta.fromState(event.getProcessInstance().getState()));
        }
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        String processInstanceId = ((KogitoProcessInstance) event.getProcessInstance()).getStringId();
        ProcessInstanceContext.setProcessInstanceId(processInstanceId);
        LOGGER.info("Workflow '{}' ({}) completed", event.getProcessInstance().getProcessId(), processInstanceId);
        // Clear context after process completion to avoid leaking to other processes
        ProcessInstanceContext.clear();
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        String nodeName = event.getNodeInstance().getNodeName();
        if (!"EmbeddedStart".equals(nodeName) && !"EmbeddedEnd".equals(nodeName) && !"Script".equals(nodeName)) {
            String processInstanceId = ((KogitoProcessInstance) event.getProcessInstance()).getStringId();
            ProcessInstanceContext.setProcessInstanceId(processInstanceId);
            LOGGER.info("Triggered node '{}' for process '{}' ({})", nodeName, event.getProcessInstance().getProcessId(), processInstanceId);
        }
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        if (event.getVariableId().startsWith(SWFConstants.DEFAULT_WORKFLOW_VAR) && event.getNewValue() instanceof JsonNode) {
            String processInstanceId = ((KogitoProcessInstance) event.getProcessInstance()).getStringId();
            ProcessInstanceContext.setProcessInstanceId(processInstanceId);
            if (event.getVariableId().length() == SWFConstants.DEFAULT_WORKFLOW_VAR.length()) {
                if (event.getOldValue() != null) {
                    LOGGER.info("Workflow data change\n{}", ((JsonNode) event.getNewValue()).toPrettyString());
                }
            } else {
                LOGGER.info("Property '{}' changed value from: '{}', to: '{}'", event.getVariableId(), event.getOldValue(), event.getNewValue());
            }
        }
    }

}
