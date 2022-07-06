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

package org.kie.kogito.serverless.workflow.devservices;

import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.quarkus.processes.devservices.DevModeWorkflowLogger;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class DevModeServerlessWorkflowLogger extends DevModeWorkflowLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevModeServerlessWorkflowLogger.class);

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        if (isSWEvent(event)) {
            LOGGER.info("Starting workflow '{}' ({})", event.getProcessInstance().getProcessId(), ((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            JsonNode node = (JsonNode) ((KogitoProcessInstance) event.getProcessInstance()).getVariables().get(SWFConstants.DEFAULT_WORKFLOW_VAR);
            if (!node.isEmpty()) {
                LOGGER.info("Workflow data \n{}", node.toPrettyString());
            }
        } else {
            super.beforeProcessStarted(event);
        }
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        if (isSWEvent(event)) {
            String nodeName = event.getNodeInstance().getNodeName();
            if (!"EmbeddedStart".equals(nodeName) && !"EmbeddedEnd".equals(nodeName) && !"Script".equals(nodeName)) {
                LOGGER.info("Triggered node '{}' for process '{}' ({})", nodeName, event.getProcessInstance().getProcessId(),
                        ((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }
        } else {
            super.beforeNodeTriggered(event);
        }
    }

    protected boolean isSWEvent(ProcessEvent event) {
        return event.getProcessInstance().getProcess().getType().equals(KogitoWorkflowProcess.SW_TYPE);
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        if (isSWEvent(event)) {
            if (SWFConstants.DEFAULT_WORKFLOW_VAR.equals(event.getVariableId())) {
                if (event.getNewValue() instanceof JsonNode) {
                    JsonNode node = (JsonNode) event.getNewValue();
                    if (!node.isEmpty()) {
                        LOGGER.info("Workflow data change\n{}", node.toPrettyString());
                    }
                }
            }
        } else {
            super.afterVariableChanged(event);
        }
    }

}
