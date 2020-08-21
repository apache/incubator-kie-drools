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

import org.jbpm.marshalling.impl.JBPMMessages.ProcessInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.mongodb.marshalling.DocumentUnmarshallingException;
import org.kie.kogito.mongodb.model.ProcessInstanceDocument;
import org.kie.kogito.mongodb.utils.ProcessInstanceMessageMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessInstanceMessageMapperTest {

    ProcessInstanceMessageMapper processInstanceMessageMapper = new ProcessInstanceMessageMapper();
    static ProcessInstanceDocument doc;

    @BeforeAll
    static void setup() throws URISyntaxException, IOException {
        doc = new ProcessInstanceDocument();
        doc.setId(TestHelper.getProcessInstanceDocument().getString("_id"));
        doc.setProcessInstance((org.bson.Document) TestHelper.getProcessInstanceDocument().get("processInstance"));
    }

    @Test
    void applyTest() {
        ProcessInstance instance = processInstanceMessageMapper.apply(null, doc);
        assertNotNull(instance);
        assertThat(instance.getId()).isEqualTo(doc.getProcessInstance().get("id"));
        assertThat(instance.getProcessId()).isEqualTo(doc.getProcessInstance().get("processId"));
        assertThrows(DocumentUnmarshallingException.class, () -> processInstanceMessageMapper.apply(null, null));
    }
}
