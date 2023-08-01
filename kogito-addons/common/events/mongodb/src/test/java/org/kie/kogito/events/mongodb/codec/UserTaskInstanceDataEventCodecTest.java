/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.events.mongodb.codec;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.AttachmentEventBody;
import org.kie.kogito.event.process.CommentEventBody;
import org.kie.kogito.event.process.ProcessInstanceEventBody;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceEventBody;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.events.mongodb.codec.CodecUtils.ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UserTaskInstanceDataEventCodecTest {

    private UserTaskInstanceDataEventCodec codec;

    private UserTaskInstanceDataEvent event;

    @BeforeEach
    void setUp() {
        codec = new UserTaskInstanceDataEventCodec();

        String source = "testSource";
        String kogitoAddons = "testKogitoAddons";
        String identity = "testIdentity";

        CommentEventBody comment = CommentEventBody.create()
                .id("testCommentId")
                .updatedBy("testCommentUpdatedBy")
                .content("testCommentContent")
                .updatedAt(new Date())
                .build();
        Set<CommentEventBody> comments = Collections.singleton(comment);
        AttachmentEventBody attachment = AttachmentEventBody.create()
                .id("testAttachmentId")
                .updatedBy("testAttachmentUpdatedBy")
                .content(URI.create("test.attachment.test"))
                .name("testAttachmentName")
                .updatedAt(new Date())
                .build();
        Set<AttachmentEventBody> attachments = Collections.singleton(attachment);

        Map<String, String> metaData = new HashMap<>();
        metaData.put(ProcessInstanceEventBody.ID_META_DATA, "testKogitoProcessInstanceId");
        metaData.put(ProcessInstanceEventBody.VERSION_META_DATA, "testKogitoProcessInstanceVersion");
        metaData.put(ProcessInstanceEventBody.ROOT_ID_META_DATA, "testKogitoRootProcessInstanceId");
        metaData.put(ProcessInstanceEventBody.PROCESS_ID_META_DATA, "testKogitoProcessId");
        metaData.put(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA, "testKogitoRootProcessId");
        metaData.put(UserTaskInstanceEventBody.UT_STATE_META_DATA, "testKogitoUserTaskInstanceState");
        metaData.put(UserTaskInstanceEventBody.UT_ID_META_DATA, "testKogitoUserTaskInstanceId");

        UserTaskInstanceEventBody body = UserTaskInstanceEventBody.create()
                .id("testId")
                .taskName("testTaskName")
                .taskDescription("testTaskDescription")
                .taskPriority("testTaskPriority")
                .referenceName("testReferenceName")
                .startDate(new Date())
                .completeDate(new Date())
                .state("testState")
                .actualOwner("testActualOwner")
                .potentialUsers(Collections.singleton("testPotentialUsers"))
                .potentialGroups(Collections.singleton("testPotentialGroups"))
                .excludedUsers(Collections.singleton("testExcludedUsers"))
                .adminUsers(Collections.singleton("testAdminUsers"))
                .adminGroups(Collections.singleton("testAdminGroups"))
                .inputs(Collections.singletonMap("testInputsKey", "testInputsValue"))
                .outputs(Collections.singletonMap("testOutputsKey", "testOutputsValue"))
                .processInstanceId("testKogitoProcessInstanceId")
                .processInstanceVersion("testKogitoProcessInstanceVersion")
                .rootProcessInstanceId("testKogitoRootProcessInstanceId")
                .processId("testKogitoProcessId")
                .rootProcessId("testKogitoRootProcessId")
                .identity(identity)
                .comments(comments)
                .attachments(attachments)
                .build();

        event = new UserTaskInstanceDataEvent(source, kogitoAddons, identity, metaData, body);
    }

    @Test
    void generateIdIfAbsentFromDocument() {
        assertThat(codec.generateIdIfAbsentFromDocument(event)).isEqualTo(event);
    }

    @Test
    void documentHasId() {
        assertThat(codec.documentHasId(event)).isTrue();
    }

    @Test
    void getDocumentId() {
        assertThat(codec.getDocumentId(event)).isEqualTo(new BsonString(event.getId()));
    }

    @Test
    void decode() {
        assertThat(codec.decode(mock(BsonReader.class), DecoderContext.builder().build())).isNull();
    }

    @Test
    void encode() {
        try (MockedStatic<CodecUtils> codecUtils = mockStatic(CodecUtils.class)) {
            Codec<Document> mockCodec = mock(Codec.class);
            codecUtils.when(CodecUtils::codec).thenReturn(mockCodec);
            codecUtils.when(() -> CodecUtils.encodeDataEvent(any(), any())).thenCallRealMethod();
            BsonWriter writer = mock(BsonWriter.class);
            EncoderContext context = EncoderContext.builder().build();

            codec.encode(writer, event, context);

            ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
            verify(mockCodec, times(1)).encode(eq(writer), captor.capture(), eq(context));
            Document doc = captor.getValue();

            assertThat(doc).containsEntry(ID, event.getId())
                    .containsEntry("specversion", event.getSpecVersion().toString())
                    .containsEntry("source", event.getSource().toString())
                    .containsEntry("type", event.getType())
                    .containsEntry("time", event.getTime())
                    .containsEntry("subject", event.getSubject())
                    .containsEntry("dataContentType", event.getDataContentType())
                    .containsEntry("dataSchema", event.getDataSchema())
                    .containsEntry("kogitoProcessinstanceId", event.getKogitoProcessInstanceId())
                    .containsEntry("kogitoRootProcessinstanceId", event.getKogitoRootProcessInstanceId())
                    .containsEntry("kogitoProcessId", event.getKogitoProcessId())
                    .containsEntry("kogitoRootProcessId", event.getKogitoRootProcessId())
                    .containsEntry("kogitoAddons", event.getKogitoAddons())
                    .containsEntry("kogitoUserTaskinstanceId", event.getKogitoUserTaskinstanceId())
                    .containsEntry("kogitoUserTaskinstanceState", event.getKogitoUserTaskinstanceState())
                    .containsEntry("kogitoIdentity", event.getKogitoIdentity());

            assertThat(((Document) doc.get("data"))).containsEntry("id", event.getData().getId())
                    .containsEntry("taskName", event.getData().getTaskName())
                    .containsEntry("taskDescription", event.getData().getTaskDescription())
                    .containsEntry("taskPriority", event.getData().getTaskPriority())
                    .containsEntry("referenceName", event.getData().getReferenceName())
                    .containsEntry("startDate", event.getData().getStartDate())
                    .containsEntry("completeDate", event.getData().getCompleteDate())
                    .containsEntry("state", event.getData().getState())
                    .containsEntry("actualOwner", event.getData().getActualOwner())
                    .containsEntry("potentialUsers", event.getData().getPotentialUsers())
                    .containsEntry("potentialGroups", event.getData().getPotentialGroups())
                    .containsEntry("excludedUsers", event.getData().getExcludedUsers())
                    .containsEntry("adminUsers", event.getData().getAdminUsers())
                    .containsEntry("adminGroups", event.getData().getAdminGroups())
                    .containsEntry("inputs", new Document(event.getData().getInputs()))
                    .containsEntry("outputs", new Document(event.getData().getOutputs()))
                    .containsEntry("processInstanceId", event.getData().getProcessInstanceId())
                    .containsEntry("rootProcessInstanceId", event.getData().getRootProcessInstanceId())
                    .containsEntry("identity", event.getData().getIdentity())
                    .containsEntry("processId", event.getData().getProcessId())
                    .containsEntry("rootProcessId", event.getData().getRootProcessId());

            CommentEventBody c = event.getData().getComments().iterator().next();
            Document comment = new Document().append("id", c.getId()).append("content", c.getContent())
                    .append("updatedAt", c.getUpdatedAt()).append("updatedBy", c.getUpdatedBy());
            Set<Document> comments = new HashSet<>();
            comments.add(comment);
            assertThat(((Document) doc.get("data"))).containsEntry("comments", comments);

            AttachmentEventBody a = event.getData().getAttachments().iterator().next();
            Document attachment = new Document().append("id", a.getId()).append("content", a.getContent())
                    .append("updatedAt", a.getUpdatedAt()).append("updatedBy", a.getUpdatedBy()).append("name", a.getName());
            Set<Document> attachments = new HashSet<>();
            attachments.add(attachment);
            assertThat(((Document) doc.get("data"))).containsEntry("attachments", attachments);
        }
    }

    @Test
    void getEncoderClass() {
        assertThat(codec.getEncoderClass()).isEqualTo(UserTaskInstanceDataEvent.class);
    }
}
