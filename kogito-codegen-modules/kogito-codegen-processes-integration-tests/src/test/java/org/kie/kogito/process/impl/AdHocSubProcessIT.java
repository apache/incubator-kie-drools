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
package org.kie.kogito.process.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.process.impl.ProcessTestUtils.assertState;

class AdHocSubProcessIT extends AbstractCodegenIT {

    @Test
    void testActivationAdHoc() throws Exception {
        Application app = generateCodeProcessesOnly("cases/ActivationAdHoc.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("TestCase.ActivationAdHoc");
        Model model = p.createModel();
        Map<String, Object> params = new HashMap<>();
        params.put("favouriteColour", "yellow");
        model.fromMap(params);
        ProcessInstance<?> processInstance = p.createInstance(model);
        assertState(processInstance, ProcessInstance.STATE_PENDING);
        processInstance.start();

        assertState(processInstance, ProcessInstance.STATE_ACTIVE);

        List<WorkItem> workItems = processInstance.workItems();
        assertThat(workItems).hasSize(1);
        WorkItem workItem = workItems.get(0);
        params = new HashMap<>();
        params.put("favouriteColour", "blue");
        processInstance.completeWorkItem(workItem.getId(), params);

        assertState(processInstance, ProcessInstance.STATE_COMPLETED);
    }

    @Test
    void testCompletionAdHoc() throws Exception {
        Application app = generateCodeProcessesOnly("cases/CompletionAdHoc.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("TestCase.CompletionAdHoc");
        Model model = p.createModel();
        Map<String, Object> params = new HashMap<>();
        params.put("favouriteColour", "yellow");
        model.fromMap(params);
        ProcessInstance<?> processInstance = p.createInstance(model);
        assertState(processInstance, ProcessInstance.STATE_PENDING);
        processInstance.start();

        assertState(processInstance, ProcessInstance.STATE_ACTIVE);

        List<WorkItem> workItems = processInstance.workItems();
        assertThat(workItems).hasSize(1);
        WorkItem workItem = workItems.get(0);
        workItem.getParameters().put("favouriteColour", "green");
        params.put("favouriteColour", "green");
        processInstance.completeWorkItem(workItem.getId(), params);

        assertState(processInstance, ProcessInstance.STATE_COMPLETED);
    }
}
