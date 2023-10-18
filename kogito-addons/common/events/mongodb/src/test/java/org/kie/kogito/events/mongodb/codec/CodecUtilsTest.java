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
package org.kie.kogito.events.mongodb.codec;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessInstanceEventMetadata;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;

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

        Map<String, Object> metaData = new HashMap<>();
        metaData.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, kogitoProcessInstanceId);
        metaData.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, kogitoProcessInstanceVersion);
        metaData.put(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA, kogitoRootProcessInstanceId);
        metaData.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, kogitoProcessId);
        metaData.put(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA, kogitoRootProcessId);

        ProcessInstanceVariableDataEvent event = new ProcessInstanceVariableDataEvent(source, kogitoAddons, identity, metaData, mock(ProcessInstanceVariableEventBody.class));
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
