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

import org.jbpm.bpmn2.feel.GatewayFEELModel;
import org.jbpm.bpmn2.feel.GatewayFEELProcess;
import org.jbpm.test.utils.EventTrackerProcessListener;
import org.jbpm.test.utils.ProcessTestHelper;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FEELTest extends JbpmBpmn2TestCase {
    @Test
    public void testGatewayFEEL() {
        Application app = ProcessTestHelper.newApplication();
        EventTrackerProcessListener eventTrackerProcessListener = new EventTrackerProcessListener();

        ProcessTestHelper.registerProcessEventListener(app, eventTrackerProcessListener);
        org.kie.kogito.process.Process<GatewayFEELModel> process = GatewayFEELProcess.newProcess(app);
        GatewayFEELModel model = process.createModel();
        model.setVA(Boolean.TRUE);
        model.setVB(Boolean.FALSE);
        ProcessInstance<GatewayFEELModel> procInstance1 = process.createInstance(model);
        procInstance1.start();

        assertThat(procInstance1.variables().getTask1()).isEqualTo("ok");
        assertThat(procInstance1.variables().getTask2()).isEqualTo("ok");
        assertThat(procInstance1.variables().getTask3()).isNull();

        assertThat(eventTrackerProcessListener.tracked()).anyMatch(ProcessTestHelper.triggered("Task2"))
                .anyMatch(ProcessTestHelper.triggered("VA and not(VB)"));

        model.setVA(Boolean.FALSE);
        model.setVB(Boolean.TRUE);

        ProcessInstance<GatewayFEELModel> procInstance2 = process.createInstance(model);
        procInstance2.start();

        assertThat(procInstance2.variables().getTask1()).isEqualTo("ok");
        assertThat(procInstance2.variables().getTask2()).isNull();
        assertThat(procInstance2.variables().getTask3()).isEqualTo("ok");
        assertThat(eventTrackerProcessListener.tracked()).anyMatch(ProcessTestHelper.triggered("Task3"))
                .anyMatch(ProcessTestHelper.triggered("VB or not(VA)"));
    }

    @Test
    public void testGatewayFEELWrong() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<GatewayFEELModel> process = GatewayFEELProcess.newProcess(app);
        ProcessInstance<GatewayFEELModel> instance = process.createInstance(process.createModel());
        instance.start();
        assertThat(instance.status()).isEqualTo(ProcessInstance.STATE_ERROR);
        assertThat(instance.error().isPresent()).isTrue();
        assertThatExceptionOfType(ProcessInstanceExecutionException.class)
                .isThrownBy(instance::checkError).withMessageContaining("org.jbpm.process.instance.impl.FeelReturnValueEvaluatorException")
                .withMessageContaining("ERROR Unknown variable 'VA'")
                .withMessageContaining("ERROR Unknown variable name 'VB'");

    }

}
