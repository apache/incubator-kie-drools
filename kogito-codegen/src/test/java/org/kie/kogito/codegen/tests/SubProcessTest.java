/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.tests;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.Sig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class SubProcessTest extends AbstractCodegenTest {

    @Test
    public void testSubProcess() throws Exception {
        Application app = generateCodeProcessesOnly("subprocess/SubProcess.bpmn", "subprocess/ParentProcess.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> parent = app.processes().processById("parent");
        Process<? extends Model> subProcess = app.processes().processById("subprocess");

        Model m = parent.createModel();
        m.fromMap(Collections.singletonMap("name", "test"));

        ProcessInstance<? extends Model> processInstance = parent.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        Collection<? extends ProcessInstance<? extends Model>> instances = subProcess.instances().values();
        assertThat(instances).hasSize(1);

        ProcessInstance<? extends Model> subProcessInstance = instances.iterator().next();
        assertThat(subProcessInstance.variables().toMap()).hasSize(3).contains(
                entry("constant", "aString"), entry("name", "test"), entry("review", null));

        subProcessInstance.send(Sig.of("end", "another review"));
        assertThat(subProcessInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        assertThat(processInstance.variables().toMap()).hasSize(2).contains(
                entry("name", "test"), entry("review", "another review"));

        processInstance.send(Sig.of("end", null));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}
