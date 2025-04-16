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
import java.util.Set;

import org.kie.kogito.event.DataEvent;

import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.readDate;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.readUTF;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.toDate;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.writeDate;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.writeUTF;

public class ProcessInstanceSLAEventBody implements KogitoMarshallEventSupport, CloudEventVisitor {

    // common fields for events
    private Date eventDate;

    private String eventUser;

    // data fields for process instance event

    private String processId;

    private String processVersion;

    private String processInstanceId;

    // customs data fields for this event

    private String nodeDefinitionId;

    private String nodeInstanceId;

    private String nodeName;

    private String nodeType;

    private Date slaDueDate;

    @Override
    public void writeEvent(DataOutput out) throws IOException {
        out.writeUTF(nodeDefinitionId);
        writeUTF(out, nodeName);
        out.writeUTF(nodeType);
        out.writeUTF(nodeInstanceId);
        writeDate(out, slaDueDate);

    }

    @Override
    public void readEvent(DataInput in, Set<KogitoMarshallEventFlag> flags) throws IOException {
        nodeDefinitionId = in.readUTF();
        nodeName = readUTF(in);
        nodeType = in.readUTF();
        nodeInstanceId = in.readUTF();
        slaDueDate = readDate(in);
    }

    @Override
    public void visit(DataEvent<?> dataEvent) {
        this.processId = dataEvent.getKogitoProcessId();
        this.processInstanceId = dataEvent.getKogitoProcessInstanceId();
        this.processVersion = dataEvent.getKogitoProcessInstanceVersion();
        this.eventDate = toDate(dataEvent.getTime());
        this.eventUser = dataEvent.getKogitoIdentity();
    }

    public Date getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(Date slaDueDate) {
        this.slaDueDate = slaDueDate;
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

    public String getNodeDefinitionId() {
        return nodeDefinitionId;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    @Override
    public String toString() {
        return "ProcessErrorEventBody [processId=" + processId + ", processInstanceId=" + processInstanceId + ", nodeDefinitionId=" + nodeDefinitionId + ", nodeInstanceId=" + nodeInstanceId + "]";
    }

    public static Builder create() {
        return new Builder(new ProcessInstanceSLAEventBody());
    }

    public static class Builder {

        private ProcessInstanceSLAEventBody instance;

        private Builder(ProcessInstanceSLAEventBody instance) {
            this.instance = instance;
        }

        public Builder slaDueDate(Date slaDueDate) {
            this.instance.slaDueDate = slaDueDate;
            return this;
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

        public Builder processVersion(String version) {
            this.instance.processVersion = version;
            return this;
        }

        public Builder processInstanceId(String processInstanceId) {
            this.instance.processInstanceId = processInstanceId;
            return this;
        }

        public Builder nodeInstanceId(String nodeInstanceId) {
            this.instance.nodeInstanceId = nodeInstanceId;
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

        public Builder nodeType(String nodeType) {
            instance.nodeType = nodeType;
            return this;
        }

        public ProcessInstanceSLAEventBody build() {
            return instance;
        }
    }

}
