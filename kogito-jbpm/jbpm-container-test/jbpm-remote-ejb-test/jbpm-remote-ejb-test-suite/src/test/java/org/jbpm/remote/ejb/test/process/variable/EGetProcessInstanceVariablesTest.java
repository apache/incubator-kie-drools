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

package org.jbpm.remote.ejb.test.process.variable;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.junit.Test;

public class EGetProcessInstanceVariablesTest extends RemoteEjbTest {

    @Test
    public void testGetProcessVariables() {
        Long processId = ejb.startProcess("designer.human-task", new HashMap<>());

        Map<String, Object> variables = ejb.getProcessInstanceVariables(processId);
        // The following expected values are set inside the process when no input is provided in the
        // start method
        Assertions.assertThat(variables).hasSize(3);
        Assertions.assertThat(variables).containsEntry("assigneeName", "john");
        Assertions.assertThat(variables).containsEntry("taskName", "user task");
        Assertions.assertThat(variables).containsEntry("localeName", "en-UK");
    }

    @Test
    public void testGetProcessVariable() {
        Long processId = ejb.startProcessSimple(ProcessDefinitions.SIGNAL);

        Object value = ejb.getProcessVariable(processId, "x");
        Assertions.assertThat(value).isNull();
    }

}
