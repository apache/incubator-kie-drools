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

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.SignalFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertOne;

public class SubProcessIT extends AbstractCodegenIT {

    @Test
    public void testSubProcess() throws Exception {
        Application app = generateCodeProcessesOnly("subprocess/SubProcess.bpmn", "subprocess/ParentProcess.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> parent = app.get(Processes.class).processById("parent");
        Process<? extends Model> subProcess = app.get(Processes.class).processById("subprocess");

        Model m = parent.createModel();
        m.fromMap(Collections.singletonMap("name", "test"));

        ProcessInstance<? extends Model> processInstance = parent.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        assertOne(subProcess.instances());

        ProcessInstance<? extends Model> subProcessInstance = subProcess.instances().stream(ProcessInstanceReadMode.MUTABLE).findAny().orElse(null);
        assertThat(subProcessInstance.variables().toMap()).hasSize(3).contains(
                entry("constant", "aString"), entry("name", "test"), entry("review", null));

        subProcessInstance.send(SignalFactory.of("end", "another review"));
        assertThat(subProcessInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        processInstance.send(SignalFactory.of("end", "my"));

        assertThat(processInstance.variables().toMap()).hasSize(2).contains(
                entry("name", "test"), entry("review", "another review"));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}
