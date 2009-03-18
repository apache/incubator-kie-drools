/*
 *  Copyright 2009 salaboy.
 * 
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
 *
 * @author salaboy
 */
public class RuleFlowVariableChangeEvent extends ProcessEvent {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    private Object value;
    public RuleFlowVariableChangeEvent(ProcessInstance instance,String name, Object value) {
        super(instance);
        this.name = name;
        this.value = value;

    }

    public String toString() {
        return "==>[VariableChangeEvent(name=" + getName() + "; value=" + getValue()
            + "; processName=" + getProcessInstance().getProcessName() + "; processId=" + getProcessInstance().getProcessId() + ")]";
    }

}
