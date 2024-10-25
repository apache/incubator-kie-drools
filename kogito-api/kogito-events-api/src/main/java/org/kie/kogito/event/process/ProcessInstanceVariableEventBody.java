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
package org.kie.kogito.event.process;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.kie.kogito.event.DataEvent;

import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.*;

public class ProcessInstanceVariableEventBody implements KogitoMarshallEventSupport, CloudEventVisitor {

    // common fields for events
    private Date eventDate;

    private String eventUser;

    // data fields for process instance event

    private String processId;

    private String processVersion;

    private String processInstanceId;

    // custom data fields for this event
    private String nodeContainerDefinitionId;
    private String nodeContainerInstanceId;

    private String variableId;
    private String variableName;
    private Object variableValue;

    @Override
    public void writeEvent(DataOutput out) throws IOException {
        writeUTF(out, nodeContainerDefinitionId);
        writeUTF(out, nodeContainerInstanceId);
        writeUTF(out, variableId);
        out.writeUTF(variableName);
        writeObject(out, variableValue);
    }

    @Override
    public void readEvent(DataInput in) throws IOException {
        nodeContainerDefinitionId = readUTF(in);
        nodeContainerInstanceId = readUTF(in);
        variableId = readUTF(in);
        variableName = in.readUTF();
        variableValue = readObject(in);
    }

    @Override
    public void visit(DataEvent<?> dataEvent) {
        this.processId = dataEvent.getKogitoProcessId();
        this.processInstanceId = dataEvent.getKogitoProcessInstanceId();
        this.processVersion = dataEvent.getKogitoProcessInstanceVersion();
        this.eventDate = toDate(dataEvent.getTime());
        this.eventUser = dataEvent.getKogitoIdentity();
    }

    public Date getEventDate() {
        return eventDate;
    }

    public String getEventUser() {
        return eventUser;
    }

    public String getProcessId() {
        return processId;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getNodeContainerDefinitionId() {
        return nodeContainerDefinitionId;
    }

    public String getNodeContainerInstanceId() {
        return nodeContainerInstanceId;
    }

    public String getVariableId() {
        return variableId;
    }

    public String getVariableName() {
        return variableName;
    }

    public Object getVariableValue() {
        return variableValue;
    }

    public Map<String, String> metaData() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, processInstanceId);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, processId);
        return metadata;
    }

    @Override
    public String toString() {
        return "ProcessInstanceVariableEventBody [eventDate=" + eventDate + ", eventUser=" + eventUser + ", processId=" + processId + ", processVersion=" + processVersion
                + ", processInstanceId=" + processInstanceId + ", nodeContainerDefinitionId=" + nodeContainerDefinitionId + ", nodeContainerInstanceId="
                + nodeContainerInstanceId + ", variableId=" + variableId
                + ", variableName=" + variableName + ", variableValue=" + variableValue + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeContainerInstanceId, processInstanceId, variableId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessInstanceVariableEventBody other = (ProcessInstanceVariableEventBody) obj;
        return Objects.equals(nodeContainerInstanceId, other.nodeContainerInstanceId) && Objects.equals(processInstanceId, other.processInstanceId) && Objects.equals(variableId, other.variableId);
    }

    public static Builder create() {
        return new Builder(new ProcessInstanceVariableEventBody());
    }

    public static class Builder {

        private ProcessInstanceVariableEventBody instance;

        public Builder(ProcessInstanceVariableEventBody instance) {
            this.instance = instance;
        }

        public Builder eventDate(Date eventDate) {
            this.instance.eventDate = eventDate;
            return this;
        }

        public Builder eventUser(String userId) {
            this.instance.eventUser = userId;
            return this;
        }

        public Builder processId(String processId) {
            this.instance.processId = processId;
            return this;
        }

        public Builder processVersion(String processVersion) {
            this.instance.processVersion = processVersion;
            return this;
        }

        public Builder processInstanceId(String processInstanceId) {
            this.instance.processInstanceId = processInstanceId;
            return this;
        }

        public Builder nodeContainerDefinitionId(String nodeContainerDefinitionId) {
            this.instance.nodeContainerDefinitionId = nodeContainerDefinitionId;
            return this;
        }

        public Builder nodeContainerInstanceId(String nodeContainerInstanceId) {
            this.instance.nodeContainerInstanceId = nodeContainerInstanceId;
            return this;
        }

        public Builder variableId(String variableId) {
            this.instance.variableId = variableId;
            return this;
        }

        public Builder variableName(String variableName) {
            this.instance.variableName = variableName;
            return this;
        }

        public Builder variableValue(Object variableValue) {
            this.instance.variableValue = variableValue;
            return this;
        }

        public ProcessInstanceVariableEventBody build() {
            return instance;
        }
    }

}
