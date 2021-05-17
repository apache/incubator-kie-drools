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
package org.kie.kogito.codegen.tests;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;

import static org.assertj.core.api.Assertions.assertThat;

class WorkItemParamsIT extends AbstractCodegenIT {

    @Test
    void testBasicServiceProcessTask() throws Exception {
        Application app = generateCodeProcessesOnly("servicetask/WorkItemParams.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("WorkItemParamsTest");

        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        processInstance.start();

        assertThat(processInstance.startDate()).isNotNull();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        Map<String, Object> data = result.toMap();
        assertThat(data).hasSize(4).containsKeys("boolValue", "intValue", "floatValue", "stringValue");
        assertThat(data.get("boolValue")).isNotNull().isEqualTo(Boolean.FALSE);
        assertThat(data.get("intValue")).isNotNull().isEqualTo(101);
        assertThat(data.get("floatValue")).isNotNull().isEqualTo(2.1f);
        assertThat(data.get("stringValue")).isNotNull().isEqualTo("foofoo");
    }

}
