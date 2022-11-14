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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.process.impl.ProcessTestUtils.assertState;

public class ErrorIT extends AbstractCodegenIT {

    @Test
    void testBoundaryErrorSubProcess() throws Exception {
        Application app = generateCodeProcessesOnly("error/BoundaryErrorSubProcess.bpmn2");
        assertThat(app).isNotNull();

        List<String> completedNodesNames = completedNodesListener(app);

        Process<? extends Model> p = app.get(Processes.class).processById("BoundaryErrorSubProcess");

        ProcessInstance<?> processInstance = p.createInstance(p.createModel());

        assertState(processInstance, ProcessInstance.STATE_PENDING);
        processInstance.start();

        assertState(processInstance, ProcessInstance.STATE_COMPLETED);
        assertThat(completedNodesNames).contains("Error2Task");
    }

    @Test
    void testBoundaryErrorWithCode1() throws Exception {
        testBoundaryError("error1", "Error1Task");
    }

    @Test
    void testBoundaryErrorWithCode2() throws Exception {
        testBoundaryError("error2", "Error2Task");
    }

    @Test
    void testBoundaryErrorWithoutCode() throws Exception {
        testBoundaryError(null, "Error1Task", "Error2Task");
    }

    private void testBoundaryError(String errorType, String... taskToAssert) throws Exception {
        Application app = generateCodeProcessesOnly("error/BoundaryError.bpmn2");
        assertThat(app).isNotNull();

        List<String> completedNodesNames = completedNodesListener(app);

        Process<? extends Model> p = app.get(Processes.class).processById("BoundaryError");

        Model model = p.createModel();

        model.update(Collections.singletonMap("errorType", errorType));
        ProcessInstance<?> processInstance = p.createInstance(model);

        assertState(processInstance, ProcessInstance.STATE_PENDING);
        processInstance.start();

        if (Objects.nonNull(errorType)) {
            assertState(processInstance, ProcessInstance.STATE_COMPLETED);
            assertThat(completedNodesNames).contains(taskToAssert);
        } else {
            assertState(processInstance, ProcessInstance.STATE_ERROR);
            assertThat(completedNodesNames).doesNotContain(taskToAssert);
        }
    }

    @Test
    void testEndError() throws Exception {
        Application app = generateCodeProcessesOnly("error/EndError.bpmn2");
        assertThat(app).isNotNull();

        List<String> completedNodesNames = completedNodesListener(app);

        Process<? extends Model> p = app.get(Processes.class).processById("EndError");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        assertState(processInstance, ProcessInstance.STATE_PENDING);

        processInstance.start();

        assertState(processInstance, ProcessInstance.STATE_ABORTED);

        assertThat(completedNodesNames).contains("task");
    }

    @Test
    void testEndErrorWithEventSubprocess() throws Exception {
        testErrorInSubprocess("error/EndErrorWithEventSubprocess.bpmn2", "EndErrorWithEventSubprocess");
    }

    @Test
    void testEndErrorInSubprocessWithEventSubprocess() throws Exception {
        testErrorInSubprocess("error/EndErrorInSubprocessWithEventSubprocess.bpmn2",
                "EndErrorInSubprocessWithEventSubprocess");
    }

    private List<String> completedNodesListener(Application app) {
        List<String> completedIds = new ArrayList<>();
        addProcessEventListener(app, new DefaultKogitoProcessEventListener() {
            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                completedIds.add(event.getNodeInstance().getNodeName());
            }
        });
        return completedIds;
    }

    private void testErrorInSubprocess(String processPath, String processId) throws Exception {
        Application app = generateCodeProcessesOnly(processPath);
        assertThat(app).isNotNull();

        List<String> completedNames = completedNodesListener(app);

        Process<? extends Model> p = app.get(Processes.class).processById(processId);
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());

        assertState(processInstance, ProcessInstance.STATE_PENDING);

        processInstance.start();

        assertState(processInstance, ProcessInstance.STATE_COMPLETED);

        assertThat(completedNames).containsAll(Arrays.asList("task", "subprocess-task"));
    }

    public void addProcessEventListener(Application app, KogitoProcessEventListener listener) {
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
    }
}
