/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FEELTest extends JbpmBpmn2TestCase {

    @Test
    public void testGatewayFEEL() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-GatewayFEEL.bpmn2");

        Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("VA", Boolean.TRUE);
        params1.put("VB", Boolean.FALSE);
        org.jbpm.workflow.instance.WorkflowProcessInstance procInstance1 = (org.jbpm.workflow.instance.WorkflowProcessInstance) kruntime.startProcess("BPMN2-GatewayFEEL", params1);
        assertThat(procInstance1.getVariable("Task1")).isEqualTo("ok");
        assertThat(procInstance1.getVariable("Task2")).isEqualTo("ok");
        assertThat(procInstance1.getVariable("Task3")).isNull();
        assertNodeTriggered(procInstance1.getStringId(), "Task2", "VA and not(VB)");

        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("VA", Boolean.FALSE);
        params2.put("VB", Boolean.TRUE);
        org.jbpm.workflow.instance.WorkflowProcessInstance procInstance2 = (org.jbpm.workflow.instance.WorkflowProcessInstance) kruntime.startProcess("BPMN2-GatewayFEEL", params2);
        assertThat(procInstance2.getVariable("Task1")).isEqualTo("ok");
        assertThat(procInstance2.getVariable("Task2")).isNull();
        assertThat(procInstance2.getVariable("Task3")).isEqualTo("ok");
        assertNodeTriggered(procInstance2.getStringId(), "Task3", "VB or not(VA)");
    }

    @Test
    public void testGatewayFEELWrong() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> createKogitoProcessRuntime("BPMN2-GatewayFEEL-wrong.bpmn2"))
                .withMessageContaining("Invalid FEEL expression: 'VA and Not(VB)'")
                .withMessageContaining("Invalid FEEL expression: 'VB or nOt(VA)'");
    }

}
