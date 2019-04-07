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

import java.util.List;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;

/**
 * Represents occurrence of case close operation
 */
public class CaseCancelEvent extends CaseEvent {

    private List<Long> processInstanceIds;
    
    public CaseCancelEvent(String user, String caseId, CaseFileInstance caseFile, List<Long> processInstanceIds) {
        super(user, caseId, caseFile);
        this.processInstanceIds = processInstanceIds;
    }
    
    /**
     * Returns list of process instances that were aborted as 
     * part of cancel case operation
     */
    public List<Long> getProcessInstanceIds() {
        return processInstanceIds;
    }

    @Override
    public String toString() {
        return "CancelCaseEvent [processInstanceIds=" + processInstanceIds + ", caseId=" + getCaseId() + "]";
    }
    
}
