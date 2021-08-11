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
import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.Comment;
import org.kie.kogito.services.event.UserTaskInstanceDataEvent;
import org.kie.kogito.services.event.impl.ProcessInstanceEventBody;
import org.kie.kogito.services.event.impl.UserTaskInstanceEventBody;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        Comment comment = new Comment("testCommentId", "testCommentUpdatedBy");
        comment.setContent("testCommentContent");
        comment.setUpdatedAt(new Date());
        Set<Comment> comments = Collections.singleton(comment);
        Attachment attachment = new Attachment("testAttachmentId", "testAttachmentUpdatedBy");
        attachment.setContent(URI.create("test.attachment.test"));
        attachment.setName("testAttachmentName");
        attachment.setUpdatedAt(new Date());
        Set<Attachment> attachments = Collections.singleton(attachment);

        Map<String, String> metaData = new HashMap<>();
        metaData.put(ProcessInstanceEventBody.ID_META_DATA, "testKogitoProcessinstanceId");
        metaData.put(ProcessInstanceEventBody.ROOT_ID_META_DATA, "testKogitoRootProcessinstanceId");
        metaData.put(ProcessInstanceEventBody.PROCESS_ID_META_DATA, "testKogitoProcessId");
        metaData.put(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA, "testKogitoRootProcessId");
        metaData.put(UserTaskInstanceEventBody.UT_STATE_META_DATA, "testKogitoUserTaskinstanceState");
        metaData.put(UserTaskInstanceEventBody.UT_ID_META_DATA, "testKogitoUserTaskinstanceId");

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
                .processInstanceId("testKogitoProcessinstanceId")
                .rootProcessInstanceId("testKogitoRootProcessinstanceId")
                .processId("testKogitoProcessId")
                .rootProcessId("testKogitoRootProcessId")
                .comments(comments)
                .attachments(attachments)
                .build();

        event = new UserTaskInstanceDataEvent(source, kogitoAddons, metaData, body);
    }

    @Test
    void generateIdIfAbsentFromDocument() {
        assertEquals(event, codec.generateIdIfAbsentFromDocument(event));
    }

    @Test
    void documentHasId() {
        assertTrue(codec.documentHasId(event));
    }

    @Test
    void getDocumentId() {
        assertEquals(new BsonString(event.getId()), codec.getDocumentId(event));
    }

    @Test
    void decode() {
        assertNull(codec.decode(mock(BsonReader.class), DecoderContext.builder().build()));
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

            assertEquals(event.getId(), doc.get(ID));
            assertEquals(event.getSpecVersion(), doc.get("specVersion"));
            assertEquals(event.getSource(), doc.get("source"));
            assertEquals(event.getType(), doc.get("type"));
            assertEquals(event.getTime(), doc.get("time"));
            assertEquals(event.getSubject(), doc.get("subject"));
            assertEquals(event.getDataContentType(), doc.get("dataContentType"));
            assertEquals(event.getDataSchema(), doc.get("dataSchema"));
            assertEquals(event.getKogitoProcessinstanceId(), doc.get("kogitoProcessinstanceId"));
            assertEquals(event.getKogitoRootProcessinstanceId(), doc.get("kogitoRootProcessinstanceId"));
            assertEquals(event.getKogitoProcessId(), doc.get("kogitoProcessId"));
            assertEquals(event.getKogitoRootProcessId(), doc.get("kogitoRootProcessId"));
            assertEquals(event.getKogitoAddons(), doc.get("kogitoAddons"));
            assertEquals(event.getKogitoUserTaskinstanceId(), doc.get("kogitoUserTaskinstanceId"));
            assertEquals(event.getKogitoUserTaskinstanceState(), doc.get("kogitoUserTaskinstanceState"));

            assertEquals(event.getData().getId(), ((Document) doc.get("data")).get("id"));
            assertEquals(event.getData().getTaskName(), ((Document) doc.get("data")).get("taskName"));
            assertEquals(event.getData().getTaskDescription(), ((Document) doc.get("data")).get("taskDescription"));
            assertEquals(event.getData().getTaskPriority(), ((Document) doc.get("data")).get("taskPriority"));
            assertEquals(event.getData().getReferenceName(), ((Document) doc.get("data")).get("referenceName"));
            assertEquals(event.getData().getStartDate(), ((Document) doc.get("data")).get("startDate"));
            assertEquals(event.getData().getCompleteDate(), ((Document) doc.get("data")).get("completeDate"));
            assertEquals(event.getData().getState(), ((Document) doc.get("data")).get("state"));
            assertEquals(event.getData().getActualOwner(), ((Document) doc.get("data")).get("actualOwner"));
            assertEquals(event.getData().getPotentialUsers(), ((Document) doc.get("data")).get("potentialUsers"));
            assertEquals(event.getData().getPotentialGroups(), ((Document) doc.get("data")).get("potentialGroups"));
            assertEquals(event.getData().getExcludedUsers(), ((Document) doc.get("data")).get("excludedUsers"));
            assertEquals(event.getData().getAdminUsers(), ((Document) doc.get("data")).get("adminUsers"));
            assertEquals(event.getData().getAdminGroups(), ((Document) doc.get("data")).get("adminGroups"));
            assertEquals(new Document(event.getData().getInputs()), ((Document) doc.get("data")).get("inputs"));
            assertEquals(new Document(event.getData().getOutputs()), ((Document) doc.get("data")).get("outputs"));
            assertEquals(event.getData().getProcessInstanceId(), ((Document) doc.get("data")).get("processInstanceId"));
            assertEquals(event.getData().getRootProcessInstanceId(), ((Document) doc.get("data")).get("rootProcessInstanceId"));
            assertEquals(event.getData().getProcessId(), ((Document) doc.get("data")).get("processId"));
            assertEquals(event.getData().getRootProcessId(), ((Document) doc.get("data")).get("rootProcessId"));

            Comment c = event.getData().getComments().iterator().next();
            Document comment = new Document().append("id", c.getId()).append("content", c.getContent())
                    .append("updatedAt", c.getUpdatedAt()).append("updatedBy", c.getUpdatedBy());
            Set<Document> comments = new HashSet<>();
            comments.add(comment);
            assertEquals(comments, ((Document) doc.get("data")).get("comments"));

            Attachment a = event.getData().getAttachments().iterator().next();
            Document attachment = new Document().append("id", a.getId()).append("content", a.getContent())
                    .append("updatedAt", a.getUpdatedAt()).append("updatedBy", a.getUpdatedBy()).append("name", a.getName());
            Set<Document> attachments = new HashSet<>();
            attachments.add(attachment);
            assertEquals(attachments, ((Document) doc.get("data")).get("attachments"));
        }
    }

    @Test
    void getEncoderClass() {
        assertEquals(UserTaskInstanceDataEvent.class, codec.getEncoderClass());
    }
}
