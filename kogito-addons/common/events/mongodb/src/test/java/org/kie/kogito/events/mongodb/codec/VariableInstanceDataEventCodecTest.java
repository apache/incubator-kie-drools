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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessInstanceEventBody;
import org.kie.kogito.event.process.VariableInstanceDataEvent;
import org.kie.kogito.event.process.VariableInstanceEventBody;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.events.mongodb.codec.CodecUtils.ID;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class VariableInstanceDataEventCodecTest {

    private VariableInstanceDataEventCodec codec;

    private VariableInstanceDataEvent event;

    @BeforeEach
    void setUp() {
        codec = new VariableInstanceDataEventCodec();

        String source = "testSource";
        String kogitoAddons = "testKogitoAddons";

        Map<String, String> metaData = new HashMap<>();
        metaData.put(ProcessInstanceEventBody.ID_META_DATA, "testKogitoProcessInstanceId");
        metaData.put(ProcessInstanceEventBody.VERSION_META_DATA, "testKogitoProcessInstanceVersion");
        metaData.put(ProcessInstanceEventBody.ROOT_ID_META_DATA, "testKogitoRootProcessInstanceId");
        metaData.put(ProcessInstanceEventBody.PROCESS_ID_META_DATA, "testKogitoProcessId");
        metaData.put(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA, "testKogitoRootProcessId");

        VariableInstanceEventBody body = VariableInstanceEventBody.create()
                .changeDate(new Date())
                .changedByNodeId("testChangedByNodeId")
                .changedByNodeName("testChangedByNodeName")
                .changedByNodeType("testChangedByNodeType")
                .identity("testChangedByUser")
                .processId("testKogitoProcessId")
                .processInstanceId("testKogitoProcessInstanceId")
                .rootProcessId("testKogitoRootProcessId")
                .rootProcessInstanceId("testKogitoRootProcessInstanceId")
                .variableName("testVariableName")
                .variablePreviousValue("testVariablePreviousValue")
                .variableValue("testVariableValue")
                .build();

        event = new VariableInstanceDataEvent(source, kogitoAddons, "identity", metaData, body);
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
                    .containsEntry("kogitoVariableName", event.getKogitoVariableName())
                    .containsEntry("kogitoIdentity", event.getKogitoIdentity());

            assertThat(((Document) doc.get("data"))).containsEntry("variableName", event.getData().getVariableName())
                    .containsEntry("variableValue", event.getData().getVariableValue())
                    .containsEntry("variablePreviousValue", event.getData().getVariablePreviousValue())
                    .containsEntry("changeDate", event.getData().getChangeDate())
                    .containsEntry("changedByNodeId", event.getData().getChangedByNodeId())
                    .containsEntry("changedByNodeName", event.getData().getChangedByNodeName())
                    .containsEntry("changedByNodeType", event.getData().getChangedByNodeType())
                    .containsEntry("identity", event.getData().getIdentity())
                    .containsEntry("processInstanceId", event.getData().getProcessInstanceId())
                    .containsEntry("rootProcessInstanceId", event.getData().getRootProcessInstanceId())
                    .containsEntry("processId", event.getData().getProcessId())
                    .containsEntry("rootProcessId", event.getData().getRootProcessId())
                    .containsEntry("identity", event.getData().getIdentity());
        }
    }

    @Test
    void getEncoderClass() {
        assertThat(codec.getEncoderClass()).isEqualTo(VariableInstanceDataEvent.class);
    }
}
