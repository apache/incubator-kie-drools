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
import java.util.Set;

import org.kie.kogito.event.DataEvent;

import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.readDate;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.readInteger;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.readObject;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.readUTF;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.toDate;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.writeDate;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.writeInteger;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.writeObject;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.writeUTF;

public class ProcessInstanceNodeEventBody implements KogitoMarshallEventSupport, CloudEventVisitor {

    public static final int EVENT_TYPE_ENTER = 1;

    public static final int EVENT_TYPE_EXIT = 2;

    public static final int EVENT_TYPE_ABORTED = 3;

    public static final int EVENT_TYPE_SKIPPED = 4;

    public static final int EVENT_TYPE_OBSOLETE = 5;

    public static final int EVENT_TYPE_ERROR = 6;

    public static final int EVENT_TYPE_UPDATED = 7;

    // common fields for events

    private Date eventDate;

    private String eventUser;

    private Integer eventType; // enter, leave, obsolete....

    // data fields for process instance event

    private String processId;

    private String processVersion;

    private String processInstanceId;

    // custom data fields

    private String connectionNodeDefinitionId; // only for entering and leaving

    private String nodeDefinitionId; // definition on bpmn2

    private String nodeName; // evaluated name

    private String nodeType;

    private String nodeInstanceId;

    private String workItemId;

    private Date slaDueDate;

    private Boolean retrigger;

    private Object inputParameters;

    private Object outputParameters;

    private Map<String, Object> data;

    @Override
    public void writeEvent(DataOutput out) throws IOException {
        writeInteger(out, eventType);
        writeUTF(out, connectionNodeDefinitionId);
        out.writeUTF(nodeDefinitionId);
        writeUTF(out, nodeName);
        out.writeUTF(nodeType);
        out.writeUTF(nodeInstanceId);
        writeUTF(out, workItemId);
        writeDate(out, slaDueDate);
        writeObject(out, data);
        writeObject(out, retrigger);
        writeObject(out, inputParameters);
        writeObject(out, outputParameters);
    }

    @Override
    public void readEvent(DataInput in, Set<KogitoMarshallEventFlag> flags) throws IOException {
        eventType = readInteger(in);
        connectionNodeDefinitionId = readUTF(in);
        nodeDefinitionId = in.readUTF();
        nodeName = readUTF(in);
        nodeType = in.readUTF();
        nodeInstanceId = in.readUTF();
        workItemId = readUTF(in);
        slaDueDate = readDate(in);
        data = (Map<String, Object>) readObject(in);
        if (flags.contains(KogitoMarshallEventFlag.RETRIGGER)) {
            retrigger = (Boolean) readObject(in);
        }
        if (flags.contains(KogitoMarshallEventFlag.FUNCTION_ARGS)) {
            inputParameters = readObject(in);
            outputParameters = readObject(in);
        }
    }

    @Override
    public void visit(DataEvent<?> dataEvent) {
        this.processId = dataEvent.getKogitoProcessId();
        this.processInstanceId = dataEvent.getKogitoProcessInstanceId();
        this.processVersion = dataEvent.getKogitoProcessInstanceVersion();
        this.eventDate = toDate(dataEvent.getTime());
        this.eventUser = dataEvent.getKogitoIdentity();
    }

    public ProcessInstanceNodeEventBody() {
        this.data = new HashMap<>();
    }

    public Date getEventDate() {
        return eventDate;
    }

    public Boolean isRetrigger() {
        return retrigger;
    }

    public String getEventUser() {
        return eventUser;
    }

    public Integer getEventType() {
        return eventType;
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

    public String getConnectionNodeDefinitionId() {
        return connectionNodeDefinitionId;
    }

    public String getNodeDefinitionId() {
        return nodeDefinitionId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public Date getSlaDueDate() {
        return slaDueDate;
    }

    public String getWorkItemId() {
        return workItemId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object getInputParameters() {
        return inputParameters;
    }

    public Object getOutputParameters() {
        return outputParameters;
    }

    @Override
    public String toString() {
        return "ProcessInstanceNodeEventBody [eventDate=" + eventDate + ", eventUser=" + eventUser + ", eventType=" + eventType + ", processId=" + processId + ", processVersion=" + processVersion
                + ", processInstanceId=" + processInstanceId + ", connectionNodeInstanceId=" + connectionNodeDefinitionId + ", nodeDefinitionId=" + nodeDefinitionId + ", nodeName=" + nodeName
                + ", nodeType=" + nodeType + ", nodeInstanceId=" + nodeInstanceId + ", data=" + data + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeInstanceId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessInstanceNodeEventBody other = (ProcessInstanceNodeEventBody) obj;
        return Objects.equals(nodeInstanceId, other.nodeInstanceId);
    }

    public Builder update() {
        return new Builder(this);
    }

    public static Builder create() {
        return new Builder(new ProcessInstanceNodeEventBody());
    }

    public static class Builder {

        private ProcessInstanceNodeEventBody instance;

        private Builder(ProcessInstanceNodeEventBody instance) {
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

        public Builder eventType(Integer eventType) {
            this.instance.eventType = eventType;
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
            instance.processInstanceId = processInstanceId;
            return this;
        }

        public Builder connectionNodeDefinitionId(String connectionNodeDefinitionId) {
            instance.connectionNodeDefinitionId = connectionNodeDefinitionId;
            return this;
        }

        public Builder nodeDefinitionId(String nodeDefinitionId) {
            instance.nodeDefinitionId = nodeDefinitionId;
            return this;
        }

        public Builder nodeName(String nodeName) {
            instance.nodeName = nodeName;
            return this;
        }

        public Builder setRetrigger(Boolean isRetrigger) {
            instance.retrigger = isRetrigger;
            return this;
        }

        public Builder nodeType(String nodeType) {
            instance.nodeType = nodeType;
            return this;
        }

        public Builder nodeInstanceId(String nodeInstanceId) {
            this.instance.nodeInstanceId = nodeInstanceId;
            return this;
        }

        public Builder data(String name, Object value) {
            instance.data.put(name, value);
            return this;
        }

        public Builder slaDueDate(Date slaDueDate) {
            instance.slaDueDate = slaDueDate;
            return this;
        }

        public Builder workItemId(String workItemId) {
            instance.workItemId = workItemId;
            return this;
        }

        public Builder inputParameters(Object inputParameters) {
            instance.inputParameters = inputParameters;
            return this;
        }

        public Builder outputParameters(Object outputParameters) {
            instance.outputParameters = outputParameters;
            return this;
        }

        public ProcessInstanceNodeEventBody build() {
            return instance;
        }

    }
}
