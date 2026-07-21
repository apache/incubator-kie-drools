/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.kogito.usertask.impl.events;

import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.events.UserTaskVariableEvent;

public class UserTaskVariableEventImpl extends UserTaskEventImpl implements UserTaskVariableEvent {

    private static final long serialVersionUID = -1160081990418929010L;
    private String variableName;
    private Object oldValue;
    private Object newValue;
    private VariableEventType variableType;

    public UserTaskVariableEventImpl(UserTaskInstance usertaskInstance, String varName, Object oldValue, Object newValue, VariableEventType variableType, String user) {
        super(usertaskInstance, user);
        this.variableName = varName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.variableType = variableType;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    @Override
    public Object getOldValue() {
        return oldValue;
    }

    @Override
    public Object getNewValue() {
        return newValue;
    }

    public void setVariableType(VariableEventType variableType) {
        this.variableType = variableType;
    }

    @Override
    public VariableEventType getVariableType() {
        return variableType;
    }

    @Override
    public String toString() {
        return "UserTaskVariableEventImpl [variableName=" + variableName + ", oldValue=" + oldValue + ", newValue=" + newValue + ", variableType=" + variableType + "]";
    }

}
