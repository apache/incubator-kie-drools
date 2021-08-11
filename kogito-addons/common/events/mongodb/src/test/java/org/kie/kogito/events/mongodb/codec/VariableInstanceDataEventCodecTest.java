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
import org.kie.kogito.services.event.VariableInstanceDataEvent;
import org.kie.kogito.services.event.impl.ProcessInstanceEventBody;
import org.kie.kogito.services.event.impl.VariableInstanceEventBody;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        metaData.put(ProcessInstanceEventBody.ID_META_DATA, "testKogitoProcessinstanceId");
        metaData.put(ProcessInstanceEventBody.ROOT_ID_META_DATA, "testKogitoRootProcessinstanceId");
        metaData.put(ProcessInstanceEventBody.PROCESS_ID_META_DATA, "testKogitoProcessId");
        metaData.put(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA, "testKogitoRootProcessId");

        VariableInstanceEventBody body = VariableInstanceEventBody.create()
                .changeDate(new Date())
                .changedByNodeId("testChangedByNodeId")
                .changedByNodeName("testChangedByNodeName")
                .changedByNodeType("testChangedByNodeType")
                .changedByUser("testChangedByUser")
                .processId("testKogitoProcessId")
                .processInstanceId("testKogitoProcessinstanceId")
                .rootProcessId("testKogitoRootProcessId")
                .rootProcessInstanceId("testKogitoRootProcessinstanceId")
                .variableName("testVariableName")
                .variablePreviousValue("testVariablePreviousValue")
                .variableValue("testVariableValue")
                .build();

        event = new VariableInstanceDataEvent(source, kogitoAddons, metaData, body);
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
            assertEquals(event.getKogitoVariableName(), doc.get("kogitoVariableName"));

            assertEquals(event.getData().getVariableName(), ((Document) doc.get("data")).get("variableName"));
            assertEquals(event.getData().getVariableValue(), ((Document) doc.get("data")).get("variableValue"));
            assertEquals(event.getData().getVariablePreviousValue(), ((Document) doc.get("data")).get("variablePreviousValue"));
            assertEquals(event.getData().getChangeDate(), ((Document) doc.get("data")).get("changeDate"));
            assertEquals(event.getData().getChangedByNodeId(), ((Document) doc.get("data")).get("changedByNodeId"));
            assertEquals(event.getData().getChangedByNodeName(), ((Document) doc.get("data")).get("changedByNodeName"));
            assertEquals(event.getData().getChangedByNodeType(), ((Document) doc.get("data")).get("changedByNodeType"));
            assertEquals(event.getData().getChangedByUser(), ((Document) doc.get("data")).get("changedByUser"));
            assertEquals(event.getData().getProcessInstanceId(), ((Document) doc.get("data")).get("processInstanceId"));
            assertEquals(event.getData().getRootProcessInstanceId(), ((Document) doc.get("data")).get("rootProcessInstanceId"));
            assertEquals(event.getData().getProcessId(), ((Document) doc.get("data")).get("processId"));
            assertEquals(event.getData().getRootProcessId(), ((Document) doc.get("data")).get("rootProcessId"));
        }
    }

    @Test
    void getEncoderClass() {
        assertEquals(VariableInstanceDataEvent.class, codec.getEncoderClass());
    }
}
