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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.infinispan.protostream.MessageMarshaller;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.Attachment;
import org.kie.kogito.index.model.Comment;
import org.kie.kogito.index.model.UserTaskInstance;
import org.mockito.InOrder;

import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.ACTUAL_OWNER;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.ADMIN_GROUPS;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.ADMIN_USERS;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.ATTACHMENTS;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.COMMENTS;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.COMPLETED;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.DESCRIPTION;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.ENDPOINT;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.EXCLUDED_USERS;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.ID;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.INPUTS;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.LAST_UPDATE;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.NAME;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.OUTPUTS;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.POTENTIAL_GROUPS;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.POTENTIAL_USERS;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.PRIORITY;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.PROCESS_ID;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.PROCESS_INSTANCE_ID;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.REFERENCE_NAME;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.ROOT_PROCESS_ID;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.ROOT_PROCESS_INSTANCE_ID;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.STARTED;
import static org.kie.kogito.index.infinispan.protostream.UserTaskInstanceMarshaller.STATE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class UserTaskInstanceMarshallerTest {

    private static ObjectMapper MAPPER = new ObjectMapper();

    private static UserTaskInstance TASK = new UserTaskInstance();

    @BeforeAll
    static void setup() {
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
        TASK.setId("id");
        TASK.setDescription("description");
        TASK.setName("name");
        TASK.setPriority("priority");
        TASK.setProcessInstanceId("processInstanceId");
        TASK.setProcessId("processId");
        TASK.setRootProcessInstanceId("rootProcessInstanceId");
        TASK.setRootProcessId("rootProcessId");
        TASK.setState("state");
        TASK.setActualOwner("actualOwner");
        TASK.setAdminUsers(singleton("admin"));
        TASK.setAdminGroups(singleton("admin"));
        TASK.setCompleted(time);
        TASK.setStarted(time);
        TASK.setExcludedUsers(singleton("admin"));
        TASK.setPotentialGroups(singleton("user"));
        TASK.setPotentialUsers(singleton("user"));
        TASK.setInputs(MAPPER.createObjectNode());
        TASK.setOutputs(MAPPER.createObjectNode());
        TASK.setReferenceName("referenceName");
        TASK.setLastUpdate(ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS));
        TASK.setEndpoint("endpoint");
        TASK.setComments(List.of(Comment.builder()
                .id("attId")
                .content("Text comment")
                .updatedBy("user")
                .updatedAt(time)
                .build()));
        TASK.setAttachments(List.of(Attachment.builder()
                .id("attId")
                .name("doc1")
                .content("http://documentation.com/doc1")
                .updatedBy("user")
                .updatedAt(time)
                .build()));
    }

    @Test
    void testReadFrom() throws IOException {
        MessageMarshaller.ProtoStreamReader reader = mock(MessageMarshaller.ProtoStreamReader.class);
        UserTaskInstanceMarshaller marshaller = new UserTaskInstanceMarshaller(MAPPER);

        when(reader.readString(ID)).thenReturn(TASK.getId());
        when(reader.readString(DESCRIPTION)).thenReturn(TASK.getDescription());
        when(reader.readString(NAME)).thenReturn(TASK.getName());
        when(reader.readString(PRIORITY)).thenReturn(TASK.getPriority());
        when(reader.readString(PROCESS_INSTANCE_ID)).thenReturn(TASK.getProcessInstanceId());
        when(reader.readString(PROCESS_ID)).thenReturn(TASK.getProcessId());
        when(reader.readString(ROOT_PROCESS_INSTANCE_ID)).thenReturn(TASK.getRootProcessInstanceId());
        when(reader.readString(ROOT_PROCESS_ID)).thenReturn(TASK.getRootProcessId());
        when(reader.readString(STATE)).thenReturn(TASK.getState());
        when(reader.readString(ACTUAL_OWNER)).thenReturn(TASK.getActualOwner());
        when(reader.readCollection(eq(ADMIN_USERS), any(), eq(String.class))).thenReturn(TASK.getAdminUsers());
        when(reader.readCollection(eq(ADMIN_GROUPS), any(), eq(String.class))).thenReturn(TASK.getAdminGroups());
        when(reader.readDate(COMPLETED)).thenReturn(marshaller.zonedDateTimeToDate(TASK.getCompleted()));
        when(reader.readDate(STARTED)).thenReturn(marshaller.zonedDateTimeToDate(TASK.getStarted()));
        when(reader.readCollection(eq(EXCLUDED_USERS), any(), eq(String.class))).thenReturn(TASK.getExcludedUsers());
        when(reader.readCollection(eq(POTENTIAL_GROUPS), any(), eq(String.class))).thenReturn(TASK.getPotentialGroups());
        when(reader.readCollection(eq(POTENTIAL_USERS), any(), eq(String.class))).thenReturn(TASK.getPotentialUsers());
        when(reader.readString(INPUTS)).thenReturn(TASK.getInputs().toString());
        when(reader.readString(OUTPUTS)).thenReturn(TASK.getOutputs().toString());
        when(reader.readString(REFERENCE_NAME)).thenReturn(TASK.getReferenceName());
        when(reader.readDate(LAST_UPDATE)).thenReturn(marshaller.zonedDateTimeToDate(TASK.getLastUpdate()));
        when(reader.readString(ENDPOINT)).thenReturn(TASK.getEndpoint());
        when(reader.readCollection(eq(COMMENTS), any(), eq(Comment.class))).thenReturn(TASK.getComments());
        when(reader.readCollection(eq(ATTACHMENTS), any(), eq(Attachment.class))).thenReturn(TASK.getAttachments());

        UserTaskInstance task = marshaller.readFrom(reader);

        assertThat(task).isNotNull().isEqualToIgnoringGivenFields(TASK);

        InOrder inOrder = inOrder(reader);
        inOrder.verify(reader).readString(ID);
        inOrder.verify(reader).readString(DESCRIPTION);
        inOrder.verify(reader).readString(NAME);
        inOrder.verify(reader).readString(PRIORITY);
        inOrder.verify(reader).readString(PROCESS_INSTANCE_ID);
        inOrder.verify(reader).readString(PROCESS_ID);
        inOrder.verify(reader).readString(ROOT_PROCESS_INSTANCE_ID);
        inOrder.verify(reader).readString(ROOT_PROCESS_ID);
        inOrder.verify(reader).readString(STATE);
        inOrder.verify(reader).readString(ACTUAL_OWNER);
        inOrder.verify(reader).readCollection(ADMIN_GROUPS, new HashSet<>(), String.class);
        inOrder.verify(reader).readCollection(ADMIN_USERS, new HashSet<>(), String.class);
        inOrder.verify(reader).readDate(COMPLETED);
        inOrder.verify(reader).readDate(STARTED);
        inOrder.verify(reader).readCollection(EXCLUDED_USERS, new HashSet<>(), String.class);
        inOrder.verify(reader).readCollection(POTENTIAL_GROUPS, new HashSet<>(), String.class);
        inOrder.verify(reader).readCollection(POTENTIAL_USERS, new HashSet<>(), String.class);
        inOrder.verify(reader).readString(INPUTS);
        inOrder.verify(reader).readString(OUTPUTS);
        inOrder.verify(reader).readString(REFERENCE_NAME);
        inOrder.verify(reader).readDate(LAST_UPDATE);
        inOrder.verify(reader).readString(ENDPOINT);
        inOrder.verify(reader).readCollection(COMMENTS, new ArrayList<>(), Comment.class);
        inOrder.verify(reader).readCollection(ATTACHMENTS, new ArrayList<>(), Attachment.class);
        verifyNoMoreInteractions(reader);
    }

    @Test
    void testWriteTo() throws IOException {
        MessageMarshaller.ProtoStreamWriter writer = mock(MessageMarshaller.ProtoStreamWriter.class);

        UserTaskInstanceMarshaller marshaller = new UserTaskInstanceMarshaller(null);
        marshaller.writeTo(writer, TASK);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).writeString(ID, TASK.getId());
        inOrder.verify(writer).writeString(DESCRIPTION, TASK.getDescription());
        inOrder.verify(writer).writeString(NAME, TASK.getName());
        inOrder.verify(writer).writeString(PRIORITY, TASK.getPriority());
        inOrder.verify(writer).writeString(PROCESS_INSTANCE_ID, TASK.getProcessInstanceId());
        inOrder.verify(writer).writeString(PROCESS_ID, TASK.getProcessId());
        inOrder.verify(writer).writeString(ROOT_PROCESS_INSTANCE_ID, TASK.getRootProcessInstanceId());
        inOrder.verify(writer).writeString(ROOT_PROCESS_ID, TASK.getRootProcessId());
        inOrder.verify(writer).writeString(STATE, TASK.getState());
        inOrder.verify(writer).writeString(ACTUAL_OWNER, TASK.getActualOwner());
        inOrder.verify(writer).writeCollection(ADMIN_GROUPS, TASK.getAdminGroups(), String.class);
        inOrder.verify(writer).writeCollection(ADMIN_USERS, TASK.getAdminUsers(), String.class);
        inOrder.verify(writer).writeDate(COMPLETED, marshaller.zonedDateTimeToDate(TASK.getCompleted()));
        inOrder.verify(writer).writeDate(STARTED, marshaller.zonedDateTimeToDate(TASK.getStarted()));
        inOrder.verify(writer).writeCollection(EXCLUDED_USERS, TASK.getExcludedUsers(), String.class);
        inOrder.verify(writer).writeCollection(POTENTIAL_GROUPS, TASK.getPotentialGroups(), String.class);
        inOrder.verify(writer).writeCollection(POTENTIAL_USERS, TASK.getPotentialUsers(), String.class);
        inOrder.verify(writer).writeString(INPUTS, TASK.getInputs().toString());
        inOrder.verify(writer).writeString(OUTPUTS, TASK.getOutputs().toString());
        inOrder.verify(writer).writeString(REFERENCE_NAME, TASK.getReferenceName());
        inOrder.verify(writer).writeDate(LAST_UPDATE, marshaller.zonedDateTimeToDate(TASK.getLastUpdate()));
        inOrder.verify(writer).writeString(ENDPOINT, TASK.getEndpoint());
        inOrder.verify(writer).writeCollection(COMMENTS, TASK.getComments(), Comment.class);
        inOrder.verify(writer).writeCollection(ATTACHMENTS, TASK.getAttachments(), Attachment.class);
        verifyNoMoreInteractions(writer);
    }

    @Test
    void testMarshaller() {
        UserTaskInstanceMarshaller marshaller = new UserTaskInstanceMarshaller(null);
        assertThat(marshaller.getJavaClass()).isEqualTo(UserTaskInstance.class);
        assertThat(marshaller.getTypeName()).isEqualTo(UserTaskInstance.class.getName());
    }
}
