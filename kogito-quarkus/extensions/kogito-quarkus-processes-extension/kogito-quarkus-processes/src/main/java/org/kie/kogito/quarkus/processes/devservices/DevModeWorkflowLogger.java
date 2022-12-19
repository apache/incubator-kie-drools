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

package org.kie.kogito.quarkus.processes.devservices;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.kogito.event.cloudevents.extension.ProcessMeta;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevModeWorkflowLogger extends DefaultKogitoProcessEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevModeWorkflowLogger.class);

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        LOGGER.info("Starting workflow '{}' ({})", event.getProcessInstance().getProcessId(), ((KogitoProcessInstance) event.getProcessInstance()).getStringId());
        ((KogitoProcessInstance) event.getProcessInstance()).getVariables().forEach((name, value) -> LOGGER.info("Variable '{}' value: '{}'", name, value));
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        if (event.getProcessInstance().getState() != 2) {
            LOGGER.info("Workflow '{}' ({}) was started, now '{}'", event.getProcessInstance().getProcessId(), ((KogitoProcessInstance) event.getProcessInstance()).getStringId(),
                    ProcessMeta.fromState(event.getProcessInstance().getState()));
        }
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        LOGGER.info("Workflow '{}' ({}) completed", event.getProcessInstance().getProcessId(), ((KogitoProcessInstance) event.getProcessInstance()).getStringId());
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        String nodeName = event.getNodeInstance().getNodeName();
        LOGGER.info("Triggered node '{}' for process '{}' ({})", nodeName, event.getProcessInstance().getProcessId(),
                ((KogitoProcessInstance) event.getProcessInstance()).getStringId());
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        LOGGER.info("Variable '{}' changed value from: '{}', to: '{}'", event.getVariableId(), event.getOldValue(), event.getNewValue());
    }

}
