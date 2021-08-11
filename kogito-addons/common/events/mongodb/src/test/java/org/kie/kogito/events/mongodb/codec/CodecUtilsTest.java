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

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.junit.jupiter.api.Test;
import org.kie.kogito.services.event.VariableInstanceDataEvent;
import org.kie.kogito.services.event.impl.ProcessInstanceEventBody;
import org.kie.kogito.services.event.impl.VariableInstanceEventBody;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.events.mongodb.codec.CodecUtils.ID;
import static org.mockito.Mockito.mock;

class CodecUtilsTest {

    @Test
    void encodeDataEvent() {
        String source = "testSource";
        String kogitoProcessinstanceId = "testKogitoProcessinstanceId";
        String kogitoRootProcessinstanceId = "testKogitoRootProcessinstanceId";
        String kogitoProcessId = "testKogitoProcessId";
        String kogitoRootProcessId = "testKogitoRootProcessId";
        String kogitoAddons = "testKogitoAddons";

        Map<String, String> metaData = new HashMap<>();
        metaData.put(ProcessInstanceEventBody.ID_META_DATA, kogitoProcessinstanceId);
        metaData.put(ProcessInstanceEventBody.ROOT_ID_META_DATA, kogitoRootProcessinstanceId);
        metaData.put(ProcessInstanceEventBody.PROCESS_ID_META_DATA, kogitoProcessId);
        metaData.put(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA, kogitoRootProcessId);

        VariableInstanceDataEvent event = new VariableInstanceDataEvent(source, kogitoAddons, metaData, mock(VariableInstanceEventBody.class));
        Document doc = new Document();

        CodecUtils.encodeDataEvent(doc, event);

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
    }

    @Test
    void codec() {
        assertEquals(DocumentCodec.class, CodecUtils.codec().getClass());
    }
}
