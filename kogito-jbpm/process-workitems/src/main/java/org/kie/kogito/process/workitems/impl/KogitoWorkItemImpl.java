/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.process.workitems.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;

public class KogitoWorkItemImpl implements InternalKogitoWorkItem, Serializable {

    private static final long serialVersionUID = 510l;

    private String id;
    private String name;
    private int state = 0;
    private Map<String, Object> parameters = new HashMap<>();
    private Map<String, Object> results = new HashMap<>();
    private String processInstanceId;
    private String deploymentId;
    private String nodeInstanceId;
    private long nodeId;

    private String phaseId;
    private String phaseStatus;

    private Date startDate;
    private Date completeDate;

    private transient KogitoProcessInstance processInstance;
    private transient KogitoNodeInstance nodeInstance;

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public long getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStringId() {
        return id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void setProcessInstanceId(long processInstanceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void setParameter(String name, Object value) {
        this.parameters.put(name, value);
    }

    @Override
    public Object getParameter(String name) {
        Object value = parameters.get(name);
        if (value instanceof WorkItemHandlerParamResolver) {
            value = ((WorkItemHandlerParamResolver) value).apply(this);
        }
        return value;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public void setResults(Map<String, Object> results) {
        if (results != null) {
            this.results = results;
        }
    }

    public void setResult(String name, Object value) {
        results.put(name, value);
    }

    @Override
    public Object getResult(String name) {
        return results.get(name);
    }

    @Override
    public Map<String, Object> getResults() {
        return results;
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public long getProcessInstanceId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProcessInstanceStringId() {
        return processInstanceId;
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

    @Override
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    @Override
    public void setNodeInstanceId(long deploymentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getNodeInstanceId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNodeInstanceStringId() {
        return nodeInstanceId;
    }

    @Override
    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    @Override
    public long getNodeId() {
        return nodeId;
    }

    @Override
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getPhaseId() {
        return this.phaseId;
    }

    @Override
    public String getPhaseStatus() {
        return this.phaseStatus;
    }

    @Override
    public void setPhaseId(String phaseId) {
        this.phaseId = phaseId;
    }

    @Override
    public void setPhaseStatus(String phaseStatus) {
        this.phaseStatus = phaseStatus;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getCompleteDate() {
        return completeDate;
    }

    @Override
    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("WorkItem ");
        b.append(id);
        b.append(" [name=");
        b.append(name);
        b.append(", state=");
        b.append(state);
        b.append(", processInstanceId=");
        b.append(processInstanceId);
        b.append(", parameters{");
        for (Iterator<Map.Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Object> entry = iterator.next();
            b.append(entry.getKey());
            b.append("=");
            b.append(entry.getValue());
            if (iterator.hasNext()) {
                b.append(", ");
            }
        }
        b.append("}]");
        return b.toString();
    }

    @Override
    public KogitoNodeInstance getNodeInstance() {
        return this.nodeInstance;
    }

    @Override
    public KogitoProcessInstance getProcessInstance() {
        return this.processInstance;
    }

    @Override
    public void setNodeInstance(KogitoNodeInstance nodeInstance) {
        this.nodeInstance = nodeInstance;
    }

    @Override
    public void setProcessInstance(KogitoProcessInstance processInstance) {
        this.processInstance = processInstance;
    }
}
