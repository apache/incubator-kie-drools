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

import org.jboss.qa.bpms.remote.ejb.domain.Person;
import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.junit.Test;

public class ECustomVariableTest extends RemoteEjbTest {

    @Test
    public void testCustomProcessVariableRetrieval() {
        Person candidate = new Person("Dluhoslav Chudobny", 30);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("candidate", candidate);
        Long pid = ejb.startProcess(ProcessDefinitions.CUSTOM_VARIABLE, parameters);

        Person person = (Person) ejb.getProcessVariable(pid, "candidate");
        Assertions.assertThat(person).isNotNull().isEqualTo(candidate);
    }

    @Test
    public void testCustomProcessVariableSet() {
        Person candidate = new Person("Jack Sparrow", 35);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("candidate", candidate);
        Long pid = ejb.startProcess(ProcessDefinitions.CUSTOM_VARIABLE, parameters);

        Person betterCandidate = new Person("Barbossa", 53);
        ejb.setProcessVariable(pid, "candidate", betterCandidate);

        Person person = (Person) ejb.getProcessVariable(pid, "candidate");
        Assertions.assertThat(person).isNotNull().isEqualTo(betterCandidate);
    }

}
