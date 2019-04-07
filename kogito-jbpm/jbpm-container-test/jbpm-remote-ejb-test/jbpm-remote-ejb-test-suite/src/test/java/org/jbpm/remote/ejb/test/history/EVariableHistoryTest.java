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

package org.jbpm.remote.ejb.test.history;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.services.api.model.VariableDesc;
import org.junit.Test;

public class EVariableHistoryTest extends RemoteEjbTest {

    @Test
    public void testVariableHistorySimply() {
        final String VARIABLE_NAME = "myobject";
        final String VARIABLE_VALUE = "10";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(VARIABLE_NAME, VARIABLE_VALUE);

        long pid = ejb.startProcess(ProcessDefinitions.OBJECT_VARIABLE, parameters);

        List<VariableDesc> vDescList = ejb.getVariableHistory(pid, VARIABLE_NAME);

        Assertions.assertThat(vDescList).isNotNull();
        Assertions.assertThat(vDescList.size()).isEqualTo(1);

        VariableDesc vd = vDescList.get(0);
        Assertions.assertThat(vd.getProcessInstanceId()).isEqualTo(pid);
        Assertions.assertThat(vd.getVariableId()).isEqualTo(VARIABLE_NAME);
        Assertions.assertThat(vd.getNewValue()).isEqualTo(VARIABLE_VALUE);
    }

    @Test
    public void testVariableHistory() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("var1", "testVariableHistory1");
        parameters.put("var2", "testVariableHistory2");

        long pid = ejb.startProcess(ProcessDefinitions.SCRIPT_TASK_TWO_VARIABLES, parameters);

        List<VariableDesc> vDescList = ejb.getVariableHistory(pid, "var1");

        Assertions.assertThat(vDescList).isNotNull();
        Assertions.assertThat(vDescList.size()).isEqualTo(1);

        VariableDesc vd = vDescList.get(0);
        Assertions.assertThat(vd.getProcessInstanceId()).isEqualTo(pid);
        Assertions.assertThat(vd.getVariableId()).isEqualTo("var1");
        Assertions.assertThat(vd.getNewValue()).isEqualTo("testVariableHistory1");
    }

}
