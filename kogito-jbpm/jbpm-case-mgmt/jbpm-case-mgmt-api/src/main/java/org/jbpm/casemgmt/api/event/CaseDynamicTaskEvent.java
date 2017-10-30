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

package org.jbpm.casemgmt.api.event;

import java.util.Map;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;

/**
 * Represents occurrence of dynamic task add to case operation
 */
public class CaseDynamicTaskEvent extends CaseEvent {

    private String nodeType;
    private Map<String, Object> data;
    private long processInstanceId;

    public CaseDynamicTaskEvent(String user, String caseId, CaseFileInstance caseFile, String nodeType, Map<String, Object> data, long processInstanceId) {
        super(user, caseId, caseFile);
        this.data = data;
        this.nodeType = nodeType;
        this.processInstanceId = processInstanceId;
    }
    
    /**
     * Returns dynamic task input data 
     */
    public Map<String, Object> getData() {
        return data;
    }
    
    /**
     * Returns type of the node of the dynamic task
     */
    public String getNodeType() {
        return nodeType;
    }
    
    /**
     * Returns process instance id of process where dynamic task was added
     */
    public long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public String toString() {
        return "CaseDynamicTaskEvent [nodeType=" + nodeType + ", data=" + data + ", processInstanceId=" + processInstanceId + ", caseId=" + getCaseId() + "]";
    }
}
