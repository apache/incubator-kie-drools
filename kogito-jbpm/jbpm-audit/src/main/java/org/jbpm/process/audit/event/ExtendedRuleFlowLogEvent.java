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
package org.jbpm.process.audit.event;

import org.drools.core.audit.event.RuleFlowLogEvent;

public class ExtendedRuleFlowLogEvent extends RuleFlowLogEvent {

    private long parentProcessInstanceId;
    private String outcome;
    private int processInstanceState;
    
    public ExtendedRuleFlowLogEvent(int type, String processId, String processName, long processInstanceId, long parentProcessInstanceId) {
        super(type, processId, processName, processInstanceId);
        this.parentProcessInstanceId = parentProcessInstanceId;
       
    }

    public ExtendedRuleFlowLogEvent(int type, String processId, String processName, long processInstanceId, int processInstanceState, String outcome) {
        super(type, processId, processName, processInstanceId);
        this.processInstanceState = processInstanceState;
        this.outcome = outcome;
    }

    public long getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(long parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public int getProcessInstanceState() {
        return processInstanceState;
    }

    protected void setProcessInstanceState(int processInstanceState) {
        this.processInstanceState = processInstanceState;
    }
}
