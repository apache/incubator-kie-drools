/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.mongodb;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jbpm.marshalling.impl.JBPMMessages.ProcessInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.mongodb.marshalling.DocumentMarshallingException;
import org.kie.kogito.mongodb.model.ProcessInstanceDocument;
import org.kie.kogito.mongodb.utils.ProcessInstanceDocumentMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessInstanceDocumentMapperTest {

    ProcessInstanceDocumentMapper processInstanceDocumentMapper = new ProcessInstanceDocumentMapper();
    static ProcessInstance instance;

    @BeforeAll
    static void setup() throws InvalidProtocolBufferException, URISyntaxException, IOException {
        instance = TestHelper.getprocessInstance();
    }

    @Test
    void applyTest() {
        ProcessInstanceDocument doc = processInstanceDocumentMapper.apply(null, instance);
        assertNotNull(doc);
        assertThat(doc.getId()).isEqualTo(instance.getId());
        assertNotNull(doc.getProcessInstance());
        assertThat(doc.getProcessInstance().get("id")).isEqualTo(instance.getId());
        assertThat(doc.getProcessInstance().get("processId")).isEqualTo(instance.getProcessId());
        assertThrows(DocumentMarshallingException.class, () -> processInstanceDocumentMapper.apply(null, null));
    }
}
