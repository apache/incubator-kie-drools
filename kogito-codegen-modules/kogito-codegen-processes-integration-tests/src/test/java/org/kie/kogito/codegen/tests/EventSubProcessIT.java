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
package org.kie.kogito.codegen.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.SignalFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class EventSubProcessIT extends AbstractCodegenIT {

    @Test
    public void testEventSignalSubProcess() throws Exception {

        Application app = generateCodeProcessesOnly("event-subprocess/EventSubprocessSignal.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("EventSubprocessSignal");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        processInstance.send(SignalFactory.of("MySignal", null));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ABORTED);
    }

    @Test
    public void testEventSignalSubProcessWithData() throws Exception {

        Application app = generateCodeProcessesOnly("event-subprocess/EventSubprocessSignalWithData.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("EventSubprocessSignal");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        processInstance.send(SignalFactory.of("MySignal", new Person("john", 20)));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("person");

        Person person = (Person) result.toMap().get("person");
        assertThat(person).isNotNull();
        assertThat(person.getName()).isEqualTo("john");

    }
}
