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
package org.jbpm.bpmn2;

import org.jbpm.bpmn2.textAnnotation.*;
import org.jbpm.test.utils.ProcessTestHelper;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;

import static org.assertj.core.api.Assertions.assertThat;

public class TextAnnotationTest extends JbpmBpmn2TestCase {
    @Test
    public void testTextAnnotationProcess() {
        Application app = ProcessTestHelper.newApplication();

        org.kie.kogito.process.Process<BPMN2_TextAnnotationModel> minimalProcess = BPMN2_TextAnnotationProcess.newProcess(app);
        BPMN2_TextAnnotationModel model = minimalProcess.createModel();
        org.kie.kogito.process.ProcessInstance<BPMN2_TextAnnotationModel> instance = minimalProcess.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testTextAnnotationProcessForTimers() {
        Application app = ProcessTestHelper.newApplication();

        org.kie.kogito.process.Process<BPMN2_TimerTxtAnnotationModel> minimalProcess = BPMN2_TimerTxtAnnotationProcess.newProcess(app);
        BPMN2_TimerTxtAnnotationModel model = minimalProcess.createModel();
        org.kie.kogito.process.ProcessInstance<BPMN2_TimerTxtAnnotationModel> instance = minimalProcess.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testTextAnnotationProcessForTasks() {
        Application app = ProcessTestHelper.newApplication();

        org.kie.kogito.process.Process<BPMN2_TaskTextAnnotationModel> minimalProcess = BPMN2_TaskTextAnnotationProcess.newProcess(app);
        BPMN2_TaskTextAnnotationModel model = minimalProcess.createModel();
        org.kie.kogito.process.ProcessInstance<BPMN2_TaskTextAnnotationModel> instance = minimalProcess.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testTextAnnotationProcessForScriptTask() {
        Application app = ProcessTestHelper.newApplication();

        org.kie.kogito.process.Process<BPMN2_ScriptTextAnnotationModel> minimalProcess = BPMN2_ScriptTextAnnotationProcess.newProcess(app);
        BPMN2_ScriptTextAnnotationModel model = minimalProcess.createModel();
        org.kie.kogito.process.ProcessInstance<BPMN2_ScriptTextAnnotationModel> instance = minimalProcess.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }
}
