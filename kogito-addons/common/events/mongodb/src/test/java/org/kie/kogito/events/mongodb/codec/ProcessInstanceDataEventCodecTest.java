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
import org.kie.kogito.event.process.MilestoneEventBody;
import org.kie.kogito.event.process.NodeInstanceEventBody;
import org.kie.kogito.event.process.ProcessErrorEventBody;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceEventBody;
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

class ProcessInstanceDataEventCodecTest {

    private ProcessInstanceDataEventCodec codec;

    private ProcessInstanceDataEvent event;

    @BeforeEach
    void setUp() {
        codec = new ProcessInstanceDataEventCodec();

        String source = "testSource";
        String kogitoAddons = "testKogitoAddons";
        String identity = "testIdentity";

        Map<String, String> metaData = new HashMap<>();
        metaData.put(ProcessInstanceEventBody.ID_META_DATA, "testKogitoProcessInstanceId");
        metaData.put(ProcessInstanceEventBody.VERSION_META_DATA, "testKogitoProcessInstanceVersion");
        metaData.put(ProcessInstanceEventBody.ROOT_ID_META_DATA, "testKogitoRootProcessInstanceId");
        metaData.put(ProcessInstanceEventBody.PROCESS_ID_META_DATA, "testKogitoProcessId");
        metaData.put(ProcessInstanceEventBody.PROCESS_TYPE_META_DATA, "testKogitoProcessType");
        metaData.put(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA, "testKogitoRootProcessId");
        metaData.put(ProcessInstanceEventBody.PARENT_ID_META_DATA, "testKogitoParentProcessInstanceId");
        metaData.put(ProcessInstanceEventBody.STATE_META_DATA, "testKogitoProcessInstanceState");

        ProcessInstanceEventBody body = ProcessInstanceEventBody.create()
                .id("testId")
                .version("testVersion")
                .parentInstanceId("testKogitoParentProcessInstanceId")
                .rootInstanceId("testKogitoRootProcessInstanceId")
                .processId("testKogitoProcessId")
                .processType("testKogitoProcessType")
                .rootProcessId("testKogitoRootProcessId")
                .processName("testProcessName")
                .identity(identity)
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

        event = new ProcessInstanceDataEvent(source, kogitoAddons, identity, metaData, body);
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
                    .containsEntry("kogitoProcessInstanceVersion", event.getKogitoProcessInstanceVersion())
                    .containsEntry("kogitoRootProcessinstanceId", event.getKogitoRootProcessInstanceId())
                    .containsEntry("kogitoProcessId", event.getKogitoProcessId())
                    .containsEntry("kogitoProcessType", event.getKogitoProcessType())
                    .containsEntry("kogitoRootProcessId", event.getKogitoRootProcessId())
                    .containsEntry("kogitoAddons", event.getKogitoAddons())
                    .containsEntry("kogitoParentProcessinstanceId", event.getKogitoParentProcessInstanceId())
                    .containsEntry("kogitoProcessinstanceState", event.getKogitoProcessInstanceState())
                    .containsEntry("kogitoReferenceId", event.getKogitoReferenceId())
                    .containsEntry("kogitoIdentity", event.getKogitoIdentity())
                    .containsEntry("kogitoStartFromNode", event.getKogitoStartFromNode());

            assertThat(((Document) doc.get("data"))).containsEntry("id", event.getData().getId())
                    .containsEntry("version", event.getData().getVersion())
                    .containsEntry("parentInstanceId", event.getData().getParentInstanceId())
                    .containsEntry("rootInstanceId", event.getData().getRootInstanceId())
                    .containsEntry("processId", event.getData().getProcessId())
                    .containsEntry("rootProcessId", event.getData().getRootProcessId())
                    .containsEntry("processName", event.getData().getProcessName())
                    .containsEntry("identity", event.getData().getIdentity())
                    .containsEntry("startDate", event.getData().getStartDate())
                    .containsEntry("endDate", event.getData().getEndDate())
                    .containsEntry("state", event.getData().getState())
                    .containsEntry("businessKey", event.getData().getBusinessKey())
                    .containsEntry("roles", event.getData().getRoles())
                    .containsEntry("variables", new Document(event.getData().getVariables()));
            Document error = new Document().append("errorMessage", event.getData().getError().getErrorMessage())
                    .append("nodeDefinitionId", event.getData().getError().getNodeDefinitionId());
            assertThat(((Document) doc.get("data"))).containsEntry("error", error);
            NodeInstanceEventBody ni = event.getData().getNodeInstances().iterator().next();
            Document nodeInstance = new Document().append("id", ni.getId()).append("nodeId", ni.getNodeId())
                    .append("nodeDefinitionId", ni.getNodeDefinitionId()).append("nodeName", ni.getNodeName())
                    .append("nodeType", ni.getNodeType()).append("triggerTime", ni.getTriggerTime())
                    .append("leaveTime", ni.getLeaveTime());
            Set<Document> nodeInstances = new HashSet<>();
            nodeInstances.add(nodeInstance);
            assertThat(((Document) doc.get("data"))).containsEntry("nodeInstances", nodeInstances);
            MilestoneEventBody mi = event.getData().getMilestones().iterator().next();
            Document milestone = new Document().append("id", mi.getId()).append("name", mi.getName()).append("status", mi.getStatus());
            Set<Document> milestones = new HashSet<>();
            milestones.add(milestone);
            assertThat(((Document) doc.get("data"))).containsEntry("milestones", milestones);
        }
    }

    @Test
    void getEncoderClass() {
        assertThat(codec.getEncoderClass()).isEqualTo(ProcessInstanceDataEvent.class);
    }
}
