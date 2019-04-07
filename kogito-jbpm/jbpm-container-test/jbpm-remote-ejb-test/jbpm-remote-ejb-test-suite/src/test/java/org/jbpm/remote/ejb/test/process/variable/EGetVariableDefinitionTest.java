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

import java.util.Map;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.remote.ejb.test.TestKjars;
import org.junit.Test;

public class EGetVariableDefinitionTest extends RemoteEjbTest {

    @Test
    public void testGetProcessVariable() {
        Map<String, String> varDefinitions = ejb.getProcessVariables(TestKjars.INTEGRATION.getGav(), ProcessDefinitions.OBJECT_VARIABLE);
        Assertions.assertThat(varDefinitions)
                .hasSize(3)
                .containsEntry("initiator", "String")
                .containsEntry("myobject", "Object")
                .containsEntry("type", "String");
    }

    @Test
    public void testGetCustomProcessVariable() {
        Map<String, String> varDefinitions = ejb.getProcessVariables(TestKjars.INTEGRATION.getGav(), ProcessDefinitions.CUSTOM_VARIABLE);
        Assertions.assertThat(varDefinitions)
                .hasSize(2)
                .containsEntry("initiator", "String")
                .containsEntry("candidate", "org.jboss.qa.bpms.remote.ejb.domain.Person");
    }

}
