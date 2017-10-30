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
 * Represents occurrence of dynamic subprocess add to case operation
 */
public class CaseDynamicSubprocessEvent extends CaseEvent {

    private String processId;
    private Map<String, Object> data;
    private long processInstanceId;
    private long subProcessInstanceId;

    public CaseDynamicSubprocessEvent(String user, String caseId, CaseFileInstance caseFile, String processId, Map<String, Object> data, long processInstanceId) {
        super(user, caseId, caseFile);
        this.data = data;
        this.processId = processId;
        this.processInstanceId = processInstanceId;
    }
    
    public CaseDynamicSubprocessEvent(String user, String caseId, CaseFileInstance caseFile, String processId, Map<String, Object> data, long processInstanceId, long subProcessInstanceId) {
        super(user, caseId, caseFile);
        this.data = data;
        this.processId = processId;
        this.processInstanceId = processInstanceId;
        this.subProcessInstanceId = subProcessInstanceId;
    }
    
    /**
     * Returns parameters used when starting sub process instance 
     */
    public Map<String, Object> getData() {
        return data;
    }
    
    /**
     * Returns process id of the subprocess to be started
     */
    public String getProcessId() {
        return processId;
    }
    
    /**
     * Returns process instance id of the parent process where subprocess is added to
     */
    public long getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * Returns subprocess instance id after it was started. This information
     * might not be available before the subprocess was actually started - meaning in before* methods of the listener.
     */
    public long getSubProcessInstanceId() {
        return subProcessInstanceId;
    }

    @Override
    public String toString() {
        return "CaseDynamicSubprocessEvent [processId=" + processId + ", data=" + data + ", processInstanceId=" + processInstanceId + ", caseId=" + getCaseId() + "]";
    }
  
}
