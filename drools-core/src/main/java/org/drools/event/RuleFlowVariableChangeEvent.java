/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.drools.event;

import org.drools.runtime.process.ProcessInstance;

/**
 * @author krisv
 * @author salaboy
 */
public class RuleFlowVariableChangeEvent extends ProcessEvent {

	private static final long serialVersionUID = 4L;
	
	private String variableId;
	private String variableInstanceId;
    private Object value;

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getVariableInstanceId() {
		return variableInstanceId;
	}

	public void setVariableInstanceId(String variableInstanceId) {
		this.variableInstanceId = variableInstanceId;
	}

	public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public RuleFlowVariableChangeEvent(ProcessInstance instance, String variableId, String variableInstanceId, Object value) {
        super(instance);
        this.variableId = variableId;
        this.variableInstanceId = variableInstanceId;
        this.value = value;
    }

    public String toString() {
        return "==>[VariableChangeEvent(variableId=" + getVariableId() + "; variableInstanceId=" + getVariableInstanceId() + "; value=" + getValue()
            + "; processName=" + getProcessInstance().getProcessName() + "; processId=" + getProcessInstance().getProcessId() + ")]";
    }

}
