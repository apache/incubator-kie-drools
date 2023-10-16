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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.infinispan.protostream.MessageMarshaller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceError;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.mockito.InOrder;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.ADDONS;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.BUSINESS_KEY;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.CREATED_BY;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.END;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.ENDPOINT;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.ERROR;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.ID;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.LAST_UPDATE;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.MILESTONES;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.NODES;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.PARENT_PROCESS_INSTANCE_ID;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.PROCESS_ID;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.PROCESS_NAME;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.ROLES;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.ROOT_PROCESS_ID;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.ROOT_PROCESS_INSTANCE_ID;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.START;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.STATE;
import static org.kie.kogito.index.infinispan.protostream.ProcessInstanceMarshaller.VARIABLES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProcessInstanceMarshallerTest {

    @Test
    void testReadFrom() throws IOException {
        Date now = new Date();
        MessageMarshaller.ProtoStreamReader reader = mock(MessageMarshaller.ProtoStreamReader.class);
        when(reader.readString(ID)).thenReturn("id");
        when(reader.readString(PROCESS_ID)).thenReturn("processId");
        when(reader.readCollection(eq(ROLES), any(), eq(String.class))).thenReturn(new HashSet<>(singleton("admin")));
        when(reader.readString(VARIABLES)).thenReturn(null);
        when(reader.readString(ENDPOINT)).thenReturn("endpoint");
        when(reader.readCollection(eq(NODES), any(), eq(NodeInstance.class))).thenReturn(new ArrayList<>());
        when(reader.readInt(STATE)).thenReturn(1);
        when(reader.readDate(START)).thenReturn(now);
        when(reader.readDate(END)).thenReturn(now);
        when(reader.readString(ROOT_PROCESS_INSTANCE_ID)).thenReturn("rootProcessInstanceId");
        when(reader.readString(ROOT_PROCESS_ID)).thenReturn("rootProcessId");
        when(reader.readString(PARENT_PROCESS_INSTANCE_ID)).thenReturn("parentProcessInstanceId");
        when(reader.readString(PROCESS_NAME)).thenReturn("processName");
        when(reader.readObject(ERROR, ProcessInstanceError.class)).thenReturn(null);
        when(reader.readCollection(eq(ADDONS), any(), eq(String.class))).thenReturn(new HashSet<>(singleton("process-management")));
        when(reader.readDate(LAST_UPDATE)).thenReturn(now);
        when(reader.readString(BUSINESS_KEY)).thenReturn("businessKey");
        when(reader.readCollection(eq(MILESTONES), any(), eq(Milestone.class))).thenReturn(new ArrayList<>());
        when(reader.readString(CREATED_BY)).thenReturn("currentUser");

        ProcessInstanceMarshaller marshaller = new ProcessInstanceMarshaller(null);
        ProcessInstance pi = marshaller.readFrom(reader);

        assertThat(pi)
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID, "id")
                .hasFieldOrPropertyWithValue(PROCESS_ID, "processId")
                .hasFieldOrPropertyWithValue(ROLES, singleton("admin"))
                .hasFieldOrPropertyWithValue(VARIABLES, null)
                .hasFieldOrPropertyWithValue(ENDPOINT, "endpoint")
                .hasFieldOrPropertyWithValue(NODES, emptyList())
                .hasFieldOrPropertyWithValue(STATE, 1)
                .hasFieldOrPropertyWithValue(START, marshaller.dateToZonedDateTime(now))
                .hasFieldOrPropertyWithValue(END, marshaller.dateToZonedDateTime(now))
                .hasFieldOrPropertyWithValue(ROOT_PROCESS_INSTANCE_ID, "rootProcessInstanceId")
                .hasFieldOrPropertyWithValue(ROOT_PROCESS_ID, "rootProcessId")
                .hasFieldOrPropertyWithValue(PARENT_PROCESS_INSTANCE_ID, "parentProcessInstanceId")
                .hasFieldOrPropertyWithValue(PROCESS_NAME, "processName")
                .hasFieldOrPropertyWithValue(ADDONS, singleton("process-management"))
                .hasFieldOrPropertyWithValue(ERROR, null)
                .hasFieldOrPropertyWithValue(LAST_UPDATE, marshaller.dateToZonedDateTime(now))
                .hasFieldOrPropertyWithValue(BUSINESS_KEY, "businessKey")
                .hasFieldOrPropertyWithValue(MILESTONES, emptyList())
                .hasFieldOrPropertyWithValue(CREATED_BY, "currentUser");

        InOrder inOrder = inOrder(reader);
        inOrder.verify(reader).readString(ID);
        inOrder.verify(reader).readString(PROCESS_ID);
        inOrder.verify(reader).readCollection(ROLES, new HashSet<>(), String.class);
        inOrder.verify(reader).readString(VARIABLES);
        inOrder.verify(reader).readString(ENDPOINT);
        inOrder.verify(reader).readCollection(NODES, new ArrayList<>(), NodeInstance.class);
        inOrder.verify(reader).readInt(STATE);
        inOrder.verify(reader).readDate(START);
        inOrder.verify(reader).readDate(END);
        inOrder.verify(reader).readString(ROOT_PROCESS_INSTANCE_ID);
        inOrder.verify(reader).readString(ROOT_PROCESS_ID);
        inOrder.verify(reader).readString(PARENT_PROCESS_INSTANCE_ID);
        inOrder.verify(reader).readString(PROCESS_NAME);
        inOrder.verify(reader).readObject(ERROR, ProcessInstanceError.class);
        inOrder.verify(reader).readCollection(ADDONS, new HashSet<>(), String.class);
        inOrder.verify(reader).readDate(LAST_UPDATE);
        inOrder.verify(reader).readString(BUSINESS_KEY);
        inOrder.verify(reader).readCollection(MILESTONES, new ArrayList<>(), Milestone.class);
        inOrder.verify(reader).readString(CREATED_BY);
    }

    @Test
    void testWriteTo() throws IOException {
        ProcessInstance pi = new ProcessInstance();
        pi.setId("id");
        pi.setEndpoint("endpoint");
        pi.setProcessId("processId");
        pi.setProcessName("processName");
        pi.setRootProcessInstanceId("rootProcessInstanceId");
        pi.setParentProcessInstanceId("rootProcessInstanceId");
        pi.setRootProcessId("rootProcessId");
        pi.setRoles(singleton("admin"));
        pi.setVariables(null);
        pi.setNodes(emptyList());
        pi.setState(ProcessInstanceState.ERROR.ordinal());
        pi.setStart(ZonedDateTime.now());
        pi.setError(new ProcessInstanceError("StartEvent_1", "Something went wrong"));
        pi.setMilestones(emptyList());
        pi.setCreatedBy("currentUser");

        MessageMarshaller.ProtoStreamWriter writer = mock(MessageMarshaller.ProtoStreamWriter.class);

        ProcessInstanceMarshaller marshaller = new ProcessInstanceMarshaller(null);
        marshaller.writeTo(writer, pi);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).writeString(ID, pi.getId());
        inOrder.verify(writer).writeString(PROCESS_ID, pi.getProcessId());
        inOrder.verify(writer).writeCollection(ROLES, pi.getRoles(), String.class);
        inOrder.verify(writer).writeString(VARIABLES, null);
        inOrder.verify(writer).writeString(ENDPOINT, pi.getEndpoint());
        inOrder.verify(writer).writeCollection(NODES, pi.getNodes(), NodeInstance.class);
        inOrder.verify(writer).writeInt(STATE, pi.getState());
        inOrder.verify(writer).writeDate(START, marshaller.zonedDateTimeToDate(pi.getStart()));
        inOrder.verify(writer).writeDate(END, marshaller.zonedDateTimeToDate(pi.getEnd()));
        inOrder.verify(writer).writeString(ROOT_PROCESS_INSTANCE_ID, pi.getRootProcessInstanceId());
        inOrder.verify(writer).writeString(ROOT_PROCESS_ID, pi.getRootProcessId());
        inOrder.verify(writer).writeString(PARENT_PROCESS_INSTANCE_ID, pi.getParentProcessInstanceId());
        inOrder.verify(writer).writeString(PROCESS_NAME, pi.getProcessName());
        inOrder.verify(writer).writeObject(ERROR, pi.getError(), ProcessInstanceError.class);
        inOrder.verify(writer).writeCollection(ADDONS, pi.getAddons(), String.class);
        inOrder.verify(writer).writeDate(LAST_UPDATE, marshaller.zonedDateTimeToDate(pi.getLastUpdate()));
        inOrder.verify(writer).writeString(BUSINESS_KEY, pi.getBusinessKey());
        inOrder.verify(writer).writeCollection(MILESTONES, pi.getMilestones(), Milestone.class);
        inOrder.verify(writer).writeString(CREATED_BY, pi.getCreatedBy());
    }
}
