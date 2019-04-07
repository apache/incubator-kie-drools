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

package org.jbpm.kie.services.impl.event;

import java.io.Serializable;

import org.kie.api.event.process.ProcessNodeEvent;

public class NodeInstanceEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    private long processInstanceId;
    private long nodeInstanceId;
    private String nodeName;
    private long nodeId;
    
    public NodeInstanceEvent(ProcessNodeEvent event) {
        this.nodeInstanceId = event.getNodeInstance().getId();
        this.nodeName = event.getNodeInstance().getNodeName();
        this.nodeId = event.getNodeInstance().getNodeId();
        this.processInstanceId = event.getProcessInstance().getId();
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public long getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(long nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
    
}
