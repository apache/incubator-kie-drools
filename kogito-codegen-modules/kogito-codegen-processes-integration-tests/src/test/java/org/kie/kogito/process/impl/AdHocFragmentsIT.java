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
package org.kie.kogito.process.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.SignalFactory;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.flexible.AdHocFragment;

import static org.assertj.core.api.Assertions.assertThat;

class AdHocFragmentsIT extends AbstractCodegenIT {

    @Test
    void testAdHocFragments() throws Exception {
        Application app = generateCodeProcessesOnly("cases/AdHocFragments.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("TestCase.AdHocFragments");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        Collection<AdHocFragment> adHocFragments = processInstance.adHocFragments();
        List<AdHocFragment> expected = new ArrayList<>();
        expected.add(new AdHocFragment.Builder(MilestoneNode.class).withName("AdHoc Milestone").withAutoStart(true).build());
        expected.add(new AdHocFragment.Builder(ActionNode.class).withName("AdHoc Script").withAutoStart(false).build());
        expected.add(new AdHocFragment.Builder(HumanTaskNode.class).withName("AdHoc User Task").withAutoStart(false).build());
        expected.add(new AdHocFragment.Builder(WorkItemNode.class).withName("Service Task").withAutoStart(false).build());
        assertAdHocFragments(expected, adHocFragments);
    }

    @Test
    void testStartUserTask() throws Exception {
        String taskName = "AdHoc User Task";
        Application app = generateCodeProcessesOnly("cases/AdHocFragments.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("TestCase.AdHocFragments");
        ProcessInstance<? extends Model> processInstance = p.createInstance(p.createModel());
        processInstance.start();

        Optional<WorkItem> workItem = processInstance.workItems().stream().filter(wi -> wi.getParameters().get("NodeName").equals(taskName)).findFirst();
        assertThat(workItem).isNotPresent();

        processInstance.send(SignalFactory.of(taskName, p.createModel()));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        workItem = processInstance.workItems().stream().filter(wi -> wi.getParameters().get("NodeName").equals(taskName)).findFirst();
        assertThat(workItem).isPresent();
        assertThat(workItem.get().getId()).isNotBlank();
        assertThat(workItem.get().getName()).isNotBlank();
    }

    @Test
    void testStartFragments() throws Exception {
        Application app = generateCodeProcessesOnly("cases/AdHocFragments.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("TestCase.AdHocFragments");
        ProcessInstance<? extends Model> processInstance = p.createInstance(p.createModel());
        processInstance.start();
        Map<String, Object> params = new HashMap<>();
        params.put("user", "Juan");
        processInstance.send(SignalFactory.of("Service Task", params));

        Model result = processInstance.variables();
        assertThat(result.toMap()).containsEntry("var1", "Hello Juan 5!");

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
    }

    @Test
    void testProcessAutoStart() throws Exception {
        Application app = generateCodeProcessesOnly("cases/AdHocProcess.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("AdHocProcess");
        Model model = p.createModel();
        Map<String, Object> params = new HashMap<>();
        params.put("var1", "Pablo");
        params.put("var2", "Luis");
        model.fromMap(params);
        ProcessInstance<? extends Model> processInstance = p.createInstance(model);

        processInstance.start();

        Model result = processInstance.variables();
        assertThat(result.toMap()).containsEntry("var1", "Hello Pablo! Script")
                .containsEntry("var2", "Luis Script 2");

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
    }

    private static void assertAdHocFragments(Collection<AdHocFragment> expected, Collection<AdHocFragment> current) {
        if (expected == null) {
            assertThat(current).isNull();
        }
        assertThat(current).hasSameSizeAs(expected);
        expected.forEach(e -> assertThat(current.stream().anyMatch(c -> c.getName().equals(e.getName()) && c.getType().equals(e.getType()) && c.isAutoStart() == e.isAutoStart()))
                .as("Expected: " + e.toString() + ", Got: " + current.toString()).isTrue());
    }

}
