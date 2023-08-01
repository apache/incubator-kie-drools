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
import org.kie.kogito.event.process.ProcessInstanceEventBody;
import org.kie.kogito.event.process.VariableInstanceDataEvent;
import org.kie.kogito.event.process.VariableInstanceEventBody;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.events.mongodb.codec.CodecUtils.ID;
import static org.mockito.Mockito.mock;

class CodecUtilsTest {

    @Test
    void encodeDataEvent() {
        String source = "testSource";
        String kogitoProcessInstanceId = "testKogitoProcessInstanceId";
        String kogitoProcessInstanceVersion = "testKogitoProcessInstanceVersion";
        String kogitoRootProcessInstanceId = "testKogitoRootProcessInstanceId";
        String kogitoProcessId = "testKogitoProcessId";
        String kogitoRootProcessId = "testKogitoRootProcessId";
        String kogitoAddons = "testKogitoAddons";
        String identity = "testKogitoIdentity";

        Map<String, String> metaData = new HashMap<>();
        metaData.put(ProcessInstanceEventBody.ID_META_DATA, kogitoProcessInstanceId);
        metaData.put(ProcessInstanceEventBody.VERSION_META_DATA, kogitoProcessInstanceVersion);
        metaData.put(ProcessInstanceEventBody.ROOT_ID_META_DATA, kogitoRootProcessInstanceId);
        metaData.put(ProcessInstanceEventBody.PROCESS_ID_META_DATA, kogitoProcessId);
        metaData.put(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA, kogitoRootProcessId);

        VariableInstanceDataEvent event = new VariableInstanceDataEvent(source, kogitoAddons, identity, metaData, mock(VariableInstanceEventBody.class));
        Document doc = new Document();

        CodecUtils.encodeDataEvent(doc, event);

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
                .containsEntry("kogitoIdentity", event.getKogitoIdentity());
    }

    @Test
    void codec() {
        assertThat(CodecUtils.codec().getClass()).isEqualTo(DocumentCodec.class);
    }
}
