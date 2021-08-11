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
import org.kie.kogito.services.event.ProcessInstanceDataEvent;
import org.kie.kogito.services.event.impl.MilestoneEventBody;
import org.kie.kogito.services.event.impl.NodeInstanceEventBody;
import org.kie.kogito.services.event.impl.ProcessErrorEventBody;
import org.kie.kogito.services.event.impl.ProcessInstanceEventBody;
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

class ProcessInstanceDataEventCodecTest {

    private ProcessInstanceDataEventCodec codec;

    private ProcessInstanceDataEvent event;

    @BeforeEach
    void setUp() {
        codec = new ProcessInstanceDataEventCodec();

        String source = "testSource";
        String kogitoAddons = "testKogitoAddons";

        Map<String, String> metaData = new HashMap<>();
        metaData.put(ProcessInstanceEventBody.ID_META_DATA, "testKogitoProcessinstanceId");
        metaData.put(ProcessInstanceEventBody.ROOT_ID_META_DATA, "testKogitoRootProcessinstanceId");
        metaData.put(ProcessInstanceEventBody.PROCESS_ID_META_DATA, "testKogitoProcessId");
        metaData.put(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA, "testKogitoRootProcessId");
        metaData.put(ProcessInstanceEventBody.PARENT_ID_META_DATA, "testKogitoParentProcessinstanceId");
        metaData.put(ProcessInstanceEventBody.STATE_META_DATA, "testKogitoProcessinstanceState");

        ProcessInstanceEventBody body = ProcessInstanceEventBody.create()
                .id("testId")
                .parentInstanceId("testKogitoParentProcessinstanceId")
                .rootInstanceId("testKogitoRootProcessinstanceId")
                .processId("testKogitoProcessId")
                .rootProcessId("testKogitoRootProcessId")
                .processName("testProcessName")
                .startDate(new Date())
                .endDate(new Date())
                .state(1)
                .businessKey("testBusinessKey")
                .error(ProcessErrorEventBody.create()
                        .errorMessage("testErrorMessage")
                        .nodeDefinitionId("testNodeDefinitionId")
                        .build())
                .nodeInstance(NodeInstanceEventBody.create()
                        .id("testId")
                        .nodeId("testNodeId")
                        .nodeDefinitionId("testNodeDefinitionId")
                        .nodeName("testNodeName")
                        .nodeType("testNodeType")
                        .triggerTime(new Date())
                        .leaveTime(new Date())
                        .build())
                .variables(Collections.singletonMap("testVariableKey", "testVariableValue"))
                .roles("testRole")
                .milestones(Collections.singleton(
                        MilestoneEventBody.create()
                                .id("testId")
                                .name("testName")
                                .status("testStatus")
                                .build()))
                .build();

        event = new ProcessInstanceDataEvent(source, kogitoAddons, metaData, body);
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
            assertEquals(event.getKogitoParentProcessinstanceId(), doc.get("kogitoParentProcessinstanceId"));
            assertEquals(event.getKogitoProcessinstanceState(), doc.get("kogitoProcessinstanceState"));
            assertEquals(event.getKogitoReferenceId(), doc.get("kogitoReferenceId"));
            assertEquals(event.getKogitoStartFromNode(), doc.get("kogitoStartFromNode"));

            assertEquals(event.getData().getId(), ((Document) doc.get("data")).get("id"));
            assertEquals(event.getData().getParentInstanceId(), ((Document) doc.get("data")).get("parentInstanceId"));
            assertEquals(event.getData().getRootInstanceId(), ((Document) doc.get("data")).get("rootInstanceId"));
            assertEquals(event.getData().getProcessId(), ((Document) doc.get("data")).get("processId"));
            assertEquals(event.getData().getRootProcessId(), ((Document) doc.get("data")).get("rootProcessId"));
            assertEquals(event.getData().getProcessName(), ((Document) doc.get("data")).get("processName"));
            assertEquals(event.getData().getStartDate(), ((Document) doc.get("data")).get("startDate"));
            assertEquals(event.getData().getEndDate(), ((Document) doc.get("data")).get("endDate"));
            assertEquals(event.getData().getState(), ((Document) doc.get("data")).get("state"));
            assertEquals(event.getData().getBusinessKey(), ((Document) doc.get("data")).get("businessKey"));
            assertEquals(event.getData().getRoles(), ((Document) doc.get("data")).get("roles"));
            assertEquals(new Document(event.getData().getVariables()), ((Document) doc.get("data")).get("variables"));
            Document error = new Document().append("errorMessage", event.getData().getError().getErrorMessage())
                    .append("nodeDefinitionId", event.getData().getError().getNodeDefinitionId());
            assertEquals(error, ((Document) doc.get("data")).get("error"));
            NodeInstanceEventBody ni = event.getData().getNodeInstances().iterator().next();
            Document nodeInstance = new Document().append("id", ni.getId()).append("nodeId", ni.getNodeId())
                    .append("nodeDefinitionId", ni.getNodeDefinitionId()).append("nodeName", ni.getNodeName())
                    .append("nodeType", ni.getNodeType()).append("triggerTime", ni.getTriggerTime())
                    .append("leaveTime", ni.getLeaveTime());
            Set<Document> nodeInstances = new HashSet<>();
            nodeInstances.add(nodeInstance);
            assertEquals(nodeInstances, ((Document) doc.get("data")).get("nodeInstances"));
            MilestoneEventBody mi = event.getData().getMilestones().iterator().next();
            Document milestone = new Document().append("id", mi.getId()).append("name", mi.getName()).append("status", mi.getStatus());
            Set<Document> milestones = new HashSet<>();
            milestones.add(milestone);
            assertEquals(milestones, ((Document) doc.get("data")).get("milestones"));
        }
    }

    @Test
    void getEncoderClass() {
        assertEquals(ProcessInstanceDataEvent.class, codec.getEncoderClass());
    }
}
