/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.container.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.process.ProcessInstance;

public class TrackingProcessEventListener extends DefaultProcessEventListener {
    private final List<String> processesStarted = new ArrayList<String>();
    private final List<String> processesCompleted = new ArrayList<String>();
    private final List<String> processesAborted = new ArrayList<String>();

    private final List<String> nodesTriggered = new ArrayList<String>();
    private final List<String> nodesLeft = new ArrayList<String>();

    private final List<String> variablesChanged = new ArrayList<String>();

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        nodesTriggered.add(event.getNodeInstance().getNodeName());
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        nodesLeft.add(event.getNodeInstance().getNodeName());
    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        processesStarted.add(event.getProcessInstance().getProcessId());
    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        if (event.getProcessInstance().getState() == ProcessInstance.STATE_ABORTED) {
            processesAborted.add(event.getProcessInstance().getProcessId());
        } else {
            processesCompleted.add(event.getProcessInstance().getProcessId());
        }
    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        variablesChanged.add(event.getVariableId());
    }

    public List<String> getNodesTriggered() {
        return Collections.unmodifiableList(nodesTriggered);
    }

    public List<String> getNodesLeft() {
        return Collections.unmodifiableList(nodesLeft);
    }

    public List<String> getProcessesStarted() {
        return Collections.unmodifiableList(processesStarted);
    }

    public List<String> getProcessesCompleted() {
        return Collections.unmodifiableList(processesCompleted);
    }

    public List<String> getProcessesAborted() {
        return Collections.unmodifiableList(processesAborted);
    }

    public List<String> getVariablesChanged() {
        return Collections.unmodifiableList(variablesChanged);
    }

    public boolean wasNodeTriggered(String nodeName) {
        return nodesTriggered.contains(nodeName);
    }

    public boolean wasNodeLeft(String nodeName) {
        return nodesLeft.contains(nodeName);
    }

    public boolean wasProcessStarted(String processName) {
        return processesStarted.contains(processName);
    }

    public boolean wasProcessCompleted(String processName) {
        return processesCompleted.contains(processName);
    }

    public boolean wasProcessAborted(String processName) {
        return processesAborted.contains(processName);
    }

    public boolean wasVariableChanged(String variableId) {
        return variablesChanged.contains(variableId);
    }

    public void clear() {
        nodesTriggered.clear();
        nodesLeft.clear();
        processesStarted.clear();
        processesCompleted.clear();
        processesAborted.clear();
        variablesChanged.clear();
    }
}
