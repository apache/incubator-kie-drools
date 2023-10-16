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
package org.kie.kogito.index.infinispan.protostream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceError;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ProcessInstanceMarshaller extends AbstractMarshaller implements MessageMarshaller<ProcessInstance> {

    protected static final String ID = "id";
    protected static final String PROCESS_ID = "processId";
    protected static final String ROLES = "roles";
    protected static final String VARIABLES = "variables";
    protected static final String ENDPOINT = "endpoint";
    protected static final String NODES = "nodes";
    protected static final String STATE = "state";
    protected static final String START = "start";
    protected static final String END = "end";
    protected static final String ROOT_PROCESS_INSTANCE_ID = "rootProcessInstanceId";
    protected static final String ROOT_PROCESS_ID = "rootProcessId";
    protected static final String PARENT_PROCESS_INSTANCE_ID = "parentProcessInstanceId";
    protected static final String PROCESS_NAME = "processName";
    protected static final String VERSION = "version";
    protected static final String ERROR = "error";
    protected static final String ADDONS = "addons";
    protected static final String LAST_UPDATE = "lastUpdate";
    protected static final String BUSINESS_KEY = "businessKey";
    protected static final String MILESTONES = "milestones";
    protected static final String CREATED_BY = "createdBy";
    protected static final String UPDATED_BY = "updatedBy";

    public ProcessInstanceMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public ProcessInstance readFrom(ProtoStreamReader reader) throws IOException {
        ProcessInstance pi = new ProcessInstance();
        pi.setId(reader.readString(ID));
        pi.setProcessId(reader.readString(PROCESS_ID));
        pi.setRoles(reader.readCollection(ROLES, new HashSet<>(), String.class));
        pi.setVariables((ObjectNode) jsonFromString(reader.readString(VARIABLES)));
        pi.setEndpoint(reader.readString(ENDPOINT));
        pi.setNodes(reader.readCollection(NODES, new ArrayList<>(), NodeInstance.class));
        pi.setState(reader.readInt(STATE));
        pi.setStart(dateToZonedDateTime(reader.readDate(START)));
        pi.setEnd(dateToZonedDateTime(reader.readDate(END)));
        pi.setRootProcessInstanceId(reader.readString(ROOT_PROCESS_INSTANCE_ID));
        pi.setRootProcessId(reader.readString(ROOT_PROCESS_ID));
        pi.setParentProcessInstanceId(reader.readString(PARENT_PROCESS_INSTANCE_ID));
        pi.setProcessName(reader.readString(PROCESS_NAME));
        pi.setError(reader.readObject(ERROR, ProcessInstanceError.class));
        pi.setAddons(reader.readCollection(ADDONS, new HashSet<>(), String.class));
        pi.setLastUpdate(dateToZonedDateTime(reader.readDate(LAST_UPDATE)));
        pi.setBusinessKey(reader.readString(BUSINESS_KEY));
        pi.setMilestones(reader.readCollection(MILESTONES, new ArrayList<>(), Milestone.class));
        pi.setVersion(reader.readString(VERSION));
        pi.setCreatedBy(reader.readString(CREATED_BY));
        pi.setUpdatedBy(reader.readString(UPDATED_BY));
        return pi;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, ProcessInstance pi) throws IOException {
        writer.writeString(ID, pi.getId());
        writer.writeString(PROCESS_ID, pi.getProcessId());
        writer.writeCollection(ROLES, pi.getRoles(), String.class);
        writer.writeString(VARIABLES, pi.getVariables() == null ? null : pi.getVariables().toString());
        writer.writeString(ENDPOINT, pi.getEndpoint());
        writer.writeCollection(NODES, pi.getNodes(), NodeInstance.class);
        writer.writeInt(STATE, pi.getState());
        writer.writeDate(START, zonedDateTimeToDate(pi.getStart()));
        writer.writeDate(END, zonedDateTimeToDate(pi.getEnd()));
        writer.writeString(ROOT_PROCESS_INSTANCE_ID, pi.getRootProcessInstanceId());
        writer.writeString(ROOT_PROCESS_ID, pi.getRootProcessId());
        writer.writeString(PARENT_PROCESS_INSTANCE_ID, pi.getParentProcessInstanceId());
        writer.writeString(PROCESS_NAME, pi.getProcessName());
        writer.writeObject(ERROR, pi.getError(), ProcessInstanceError.class);
        writer.writeCollection(ADDONS, pi.getAddons(), String.class);
        writer.writeDate(LAST_UPDATE, zonedDateTimeToDate(pi.getLastUpdate()));
        writer.writeString(BUSINESS_KEY, pi.getBusinessKey());
        writer.writeCollection(MILESTONES, pi.getMilestones(), Milestone.class);
        writer.writeString(VERSION, pi.getVersion());
        writer.writeString(CREATED_BY, pi.getCreatedBy());
        writer.writeString(UPDATED_BY, pi.getCreatedBy());
    }

    @Override
    public Class<? extends ProcessInstance> getJavaClass() {
        return ProcessInstance.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
