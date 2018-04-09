/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.process.instance.impl;

import org.drools.core.process.instance.TypedWorkItem;
import org.kie.api.runtime.process.WorkItem;

/**
 * A TypedWorkItem implementation, using arbitrary classes
 * to represent parameters and results.
 */
public class TypedWorkItemImpl<P, R> implements TypedWorkItem<P, R> {

    private P parameters;
    private R results;

    public static <W extends TypedWorkItem<?, ?>> W forceTyped(WorkItem workItem) {
        return (W) ((UntypedWorkItemImpl) workItem).asTypedWorkItem();
    }

    public TypedWorkItemImpl() {
    }

    public TypedWorkItemImpl(P parameters) {
        this.parameters = parameters;
    }

    private static final long serialVersionUID = 510l;

    private long id;
    private String name;
    private int state = 0;
    private long processInstanceId;
    private String deploymentId;
    private long nodeInstanceId;
    private long nodeId;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public long getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(long nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public void setParameters(P parameters) {
        this.parameters = parameters;
    }

    public P getParameters() {
        return parameters;
    }

    public void setResults(R results) {
        if (results != null) {
            this.results = results;
        }
    }

    public R getResults() {
        return results;
    }

    public WorkItem asWorkItem() {
        return new UntypedWorkItemImpl(this);
    }

    public String toString() {
        StringBuilder b = new StringBuilder("WorkItem ");
        b.append(getId());
        b.append(" [name=");
        b.append(getName());
        b.append(", state=");
        b.append(getState());
        b.append(", processInstanceId=");
        b.append(getProcessInstanceId());
        b.append(", parameters=");
        b.append(parameters);
        b.append("]");
        return b.toString();
    }
}
