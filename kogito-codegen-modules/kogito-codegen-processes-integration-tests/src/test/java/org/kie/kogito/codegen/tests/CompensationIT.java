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

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.process.impl.ProcessTestUtils.assertState;

class CompensationIT extends AbstractCodegenIT {

    @Test
    void testCompensateFirst() throws Exception {
        Application app = generateCodeProcessesOnly("compensation/compensateFirst.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("compensateFirst");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        processInstance.start();
        assertState(processInstance, ProcessInstance.STATE_COMPLETED);

        Model model = (Model) processInstance.variables();
        assertThat(model.toMap()).containsEntry("counter", 2)
                .containsEntry("counter2", 1);
    }

    @Test
    void testCompensateSecond() throws Exception {
        Application app = generateCodeProcessesOnly("compensation/compensateSecond.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("compensateSecond");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        processInstance.start();
        assertState(processInstance, ProcessInstance.STATE_COMPLETED);

        Model model = (Model) processInstance.variables();
        assertThat(model.toMap()).containsEntry("counter", 1)
                .containsEntry("counter2", 2);
    }

    @Test
    void testCompensateAll() throws Exception {
        Application app = generateCodeProcessesOnly("compensation/compensateAll.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("compensateAll");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        processInstance.start();
        assertState(processInstance, ProcessInstance.STATE_COMPLETED);

        Model model = (Model) processInstance.variables();
        assertThat(model.toMap()).containsEntry("counter", 2)
                .containsEntry("counter2", 2);
    }

    @Test
    void testThrowSpecificForSubProcess() throws Exception {
        Application app = generateCodeProcessesOnly("compensation/BPMN2-Compensation-ThrowSpecificForSubProcess.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("CompensationSpecificSubProcess");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        processInstance.start();

        Model model = (Model) processInstance.variables();
        assertState(processInstance, ProcessInstance.STATE_COMPLETED);
        assertThat(model.toMap().get("x")).isNull();
    }

}