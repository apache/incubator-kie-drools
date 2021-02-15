/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.services.event.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VariableInstanceEventBody {

    public static final String ID_META_DATA = "kogito.processinstance.id";
    public static final String PARENT_ID_META_DATA = "kogito.processinstance.parentInstanceId";
    public static final String ROOT_ID_META_DATA = "kogito.processinstance.rootInstanceId";
    public static final String PROCESS_ID_META_DATA = "kogito.processinstance.processId";
    public static final String ROOT_PROCESS_ID_META_DATA = "kogito.processinstance.rootProcessId";
    public static final String STATE_META_DATA = "kogito.processinstance.state";

    private String variableName;
    private Object variableValue;
    private Object variablePreviousValue;
    private Date changeDate;

    private String changedByNodeId;
    private String changedByNodeName;
    private String changedByNodeType;

    private String changedByUser;

    private String processInstanceId;
    private String rootProcessInstanceId;
    private String processId;
    private String rootProcessId;

    private VariableInstanceEventBody() {}

    public String getVariableName() {
        return variableName;
    }

    public Object getVariableValue() {
        return variableValue;
    }

    public Object getVariablePreviousValue() {
        return variablePreviousValue;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public String getChangedByNodeId() {
        return changedByNodeId;
    }

    public String getChangedByNodeName() {
        return changedByNodeName;
    }

    public String getChangedByNodeType() {
        return changedByNodeType;
    }

    public String getChangedByUser() {
        return changedByUser;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public Map<String, String> metaData() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(ID_META_DATA, processInstanceId);
        metadata.put(ROOT_ID_META_DATA, rootProcessInstanceId);
        metadata.put(PROCESS_ID_META_DATA, processId);
        metadata.put(ROOT_PROCESS_ID_META_DATA, rootProcessId);
        return metadata;
    }

    @Override
    public String toString() {
        return "VariableInstanceEventBody [variableName=" + variableName + ", variableValue=" + variableValue + ", variablePreviousValue=" + variablePreviousValue + ", changeDate=" + changeDate + ", changedByNodeId=" +
               changedByNodeId + ", changedByNodeName=" + changedByNodeName + ", changedByNodeType=" + changedByNodeType + ", changedByUser=" + changedByUser + ", processInstanceId=" + processInstanceId +
               ", rootProcessInstanceId=" + rootProcessInstanceId + ", processId=" + processId + ", rootProcessId=" + rootProcessId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((changedByNodeId == null) ? 0 : changedByNodeId.hashCode());
        result = prime * result + ((changedByNodeName == null) ? 0 : changedByNodeName.hashCode());
        result = prime * result + ((changedByNodeType == null) ? 0 : changedByNodeType.hashCode());
        result = prime * result + ((processInstanceId == null) ? 0 : processInstanceId.hashCode());
        result = prime * result + ((variableName == null) ? 0 : variableName.hashCode());
        result = prime * result + ((variablePreviousValue == null) ? 0 : variablePreviousValue.hashCode());
        result = prime * result + ((variableValue == null) ? 0 : variableValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VariableInstanceEventBody other = (VariableInstanceEventBody) obj;
        if (changedByNodeId == null) {
            if (other.changedByNodeId != null)
                return false;
        } else if (!changedByNodeId.equals(other.changedByNodeId))
            return false;
        if (changedByNodeName == null) {
            if (other.changedByNodeName != null)
                return false;
        } else if (!changedByNodeName.equals(other.changedByNodeName))
            return false;
        if (changedByNodeType == null) {
            if (other.changedByNodeType != null)
                return false;
        } else if (!changedByNodeType.equals(other.changedByNodeType))
            return false;
        if (processInstanceId == null) {
            if (other.processInstanceId != null)
                return false;
        } else if (!processInstanceId.equals(other.processInstanceId))
            return false;
        if (variableName == null) {
            if (other.variableName != null)
                return false;
        } else if (!variableName.equals(other.variableName))
            return false;
        if (variablePreviousValue == null) {
            if (other.variablePreviousValue != null)
                return false;
        } else if (!variablePreviousValue.equals(other.variablePreviousValue))
            return false;
        if (variableValue == null) {
            if (other.variableValue != null)
                return false;
        } else if (!variableValue.equals(other.variableValue))
            return false;
        return true;
    }

    public static Builder create() {
        return new Builder(new VariableInstanceEventBody());
    }

    static class Builder {

        private VariableInstanceEventBody instance;

        public Builder(VariableInstanceEventBody instance) {
            this.instance = instance;
        }

        public Builder processInstanceId(String processInstanceId) {
            instance.processInstanceId = processInstanceId;
            return this;
        }

        public Builder rootProcessInstanceId(String rootProcessInstanceId) {
            instance.rootProcessInstanceId = rootProcessInstanceId;
            return this;
        }

        public Builder processId(String processId) {
            instance.processId = processId;
            return this;
        }

        public Builder rootProcessId(String rootProcessId) {
            instance.rootProcessId = rootProcessId;
            return this;
        }

        public Builder variableName(String variableName) {
            instance.variableName = variableName;
            return this;
        }

        public Builder variableValue(Object variableValue) {
            instance.variableValue = variableValue;
            return this;
        }

        public Builder variablePreviousValue(Object variablePreviousValue) {
            instance.variablePreviousValue = variablePreviousValue;
            return this;
        }

        public Builder changeDate(Date changeDate) {
            instance.changeDate = changeDate;
            return this;
        }

        public Builder changedByNodeId(String changedByNodeId) {
            instance.changedByNodeId = changedByNodeId;
            return this;
        }

        public Builder changedByNodeName(String changedByNodeName) {
            instance.changedByNodeName = changedByNodeName;
            return this;
        }

        public Builder changedByNodeType(String changedByNodeType) {
            instance.changedByNodeType = changedByNodeType;
            return this;
        }

        public Builder changedByUser(String changedByUser) {
            instance.changedByUser = changedByUser;
            return this;
        }

        public VariableInstanceEventBody build() {
            return instance;
        }
    }
}
