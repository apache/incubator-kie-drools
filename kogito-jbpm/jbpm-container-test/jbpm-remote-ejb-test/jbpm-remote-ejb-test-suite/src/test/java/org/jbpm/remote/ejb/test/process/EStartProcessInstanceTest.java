/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.remote.ejb.test.process;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.Test;

import org.kie.api.runtime.process.ProcessInstance;

public class EStartProcessInstanceTest extends RemoteEjbTest {

    @Test
    public void testStartScriptTaskProcess() {
        long pid = ejb.startProcessSimple(ProcessDefinitions.SCRIPT_TASK);

        ProcessInstanceDesc log = ejb.getProcessInstanceById(pid);

        Assertions.assertThat(log.getProcessId()).isEqualTo(ProcessDefinitions.SCRIPT_TASK);
        Assertions.assertThat(log.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test()
    public void testProcessWithLongSpecialParameters() {
        String testValue = "a long string containing spaces and other characters +š@#$%^*()_{}\\/.,";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("myobject", testValue);

        long pid = ejb.startProcess(ProcessDefinitions.OBJECT_VARIABLE, parameters);

        String type = ejb.getVariableHistory(pid, "type").get(0).getNewValue();
        Assertions.assertThat(type).isNotNull();
        Assertions.assertThat(type).isEqualTo(String.class.getName());

        String myobject = ejb.getVariableHistory(pid, "myobject").get(0).getNewValue();
        Assertions.assertThat(myobject).isNotNull();
        Assertions.assertThat(myobject).isEqualTo(testValue);
    }

    @Test()
    public void testProcessWithAmpersandParameters() {
        String testValue = "Ampersand in the string &.";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("myobject", testValue);

        long pid = ejb.startProcess(ProcessDefinitions.OBJECT_VARIABLE, parameters);

        String type = ejb.getVariableHistory(pid, "type").get(0).getNewValue();
        Assertions.assertThat(type).isNotNull();
        Assertions.assertThat(type).isEqualTo(String.class.getName());

        String myobject = ejb.getVariableHistory(pid, "myobject").get(0).getNewValue();
        Assertions.assertThat(myobject).isNotNull();
        Assertions.assertThat(myobject).isEqualTo(testValue);
    }

    @Test()
    public void testProcessWithQuoteParameters() {
        String testValue = "\"quoted string\"";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("myobject", testValue);

        long pid = ejb.startProcess(ProcessDefinitions.OBJECT_VARIABLE, parameters);

        String type = ejb.getVariableHistory(pid, "type").get(0).getNewValue();
        Assertions.assertThat(type).isNotNull();
        Assertions.assertThat(type).isEqualTo(String.class.getName());

        String myobject = ejb.getVariableHistory(pid, "myobject").get(0).getNewValue();
        Assertions.assertThat(myobject).isNotNull();
        Assertions.assertThat(myobject).isEqualTo(testValue);
    }

    @Test()
    public void testStartProcessWithUnderscoreInID() {
        long pid = ejb.startProcess(ProcessDefinitions.PROCESS_WITH_UNDERSCORE_IN);

        ProcessInstanceDesc piDesc = ejb.getProcessInstanceById(pid);

        Assertions.assertThat(piDesc).isNotNull();
        Assertions.assertThat(piDesc.getProcessId()).isEqualTo(ProcessDefinitions.PROCESS_WITH_UNDERSCORE_IN);
        Assertions.assertThat(piDesc.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testStringNumberParams() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("var1", "\"10\"");
        parameters.put("var2", "\"20\"");

        long pid = ejb.startProcess(ProcessDefinitions.SCRIPT_TASK_TWO_VARIABLES, parameters);

        ProcessInstanceDesc piDesc = ejb.getProcessInstanceById(pid);

        Assertions.assertThat(piDesc).isNotNull();
        Assertions.assertThat(piDesc.getProcessId()).isEqualTo(ProcessDefinitions.SCRIPT_TASK_TWO_VARIABLES);
        Assertions.assertThat(piDesc.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

}
