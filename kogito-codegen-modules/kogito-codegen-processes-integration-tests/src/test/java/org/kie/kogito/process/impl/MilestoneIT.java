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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.flexible.ItemDescription.Status;
import org.kie.kogito.process.flexible.Milestone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.process.flexible.ItemDescription.Status.AVAILABLE;
import static org.kie.kogito.process.flexible.ItemDescription.Status.COMPLETED;
import static org.kie.kogito.process.impl.ProcessTestUtils.assertState;

class MilestoneIT extends AbstractCodegenIT {

    @Test
    void testSimpleMilestone() throws Exception {
        Application app = generateCodeProcessesOnly("cases/milestones/SimpleMilestone.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("TestCase.SimpleMilestone");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        assertState(processInstance, ProcessInstance.STATE_PENDING);

        Collection<Milestone> expected = new ArrayList<>();
        expected.add(Milestone.builder().withName("AutoStartMilestone").withStatus(AVAILABLE).build());
        expected.add(Milestone.builder().withName("SimpleMilestone").withStatus(AVAILABLE).build());
        assertMilestones(expected, processInstance.milestones());

        processInstance.start();
        assertState(processInstance, ProcessInstance.STATE_COMPLETED);

        expected = expected.stream().map(m -> Milestone.builder().withId(m.getId()).withName(m.getName()).withStatus(COMPLETED).build()).collect(Collectors.toList());
        assertMilestones(expected, processInstance.milestones());

        RuleFlowProcessInstance legacyProcessInstance = (RuleFlowProcessInstance) ((AbstractProcessInstance<?>) processInstance).processInstance;
        assertThat(legacyProcessInstance.getNodeInstances()).isEmpty();
        assertThat(legacyProcessInstance.getNodeIdInError()).isNullOrEmpty();
        Optional<String> milestoneId = Stream.of(legacyProcessInstance.getNodeContainer().getNodes())
                .filter(node -> node.getName().equals("SimpleMilestone"))
                .map(n -> n.getUniqueId())
                .findFirst();
        assertThat(milestoneId).isPresent();
        assertThat(legacyProcessInstance.getCompletedNodeIds()).contains(milestoneId.get());
    }

    @Test
    void testConditionalMilestone() throws Exception {
        Application app = generateCodeProcessesOnly("cases/milestones/ConditionalMilestone.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("TestCase.ConditionalMilestone");
        Model model = p.createModel();
        Map<String, Object> params = new HashMap<>();
        params.put("favouriteColour", "orange");
        model.fromMap(params);
        ProcessInstance<?> processInstance = p.createInstance(model);
        assertState(processInstance, ProcessInstance.STATE_PENDING);

        Collection<Milestone> expected = new ArrayList<>();
        expected.add(Milestone.builder().withId("_8060F4FE-534E-475A-ACCD-80CBDF90D878").withName("Milestone").withStatus(AVAILABLE).build());
        assertMilestones(expected, processInstance.milestones());

        processInstance.start();
        assertState(processInstance, ProcessInstance.STATE_ACTIVE);

        expected = expected.stream().map(m -> Milestone.builder().withId(m.getId()).withName(m.getName()).withStatus(Status.ACTIVE).build()).collect(Collectors.toList());
        assertMilestones(expected, processInstance.milestones());

        List<WorkItem> workItems = processInstance.workItems();
        params.put("favouriteColour", "blue");
        processInstance.completeWorkItem(workItems.get(0).getId(), params);

        expected = expected.stream().map(m -> Milestone.builder().withId(m.getId()).withName(m.getName()).withStatus(COMPLETED).build()).collect(Collectors.toList());
        assertMilestones(expected, processInstance.milestones());
    }

    private void assertMilestones(Collection<Milestone> expected, Collection<Milestone> milestones) {
        if (expected == null) {
            assertThat(milestones).isNull();
        }
        assertThat(milestones).hasSameSizeAs(expected);
        expected.forEach(e -> assertThat(milestones.stream().anyMatch(c -> Objects.equals(c.getName(), e.getName()) &&
                Objects.equals(c.getStatus(), e.getStatus()))).withFailMessage("Expected: " + e + " - Not present in: " + milestones).isTrue());
    }

}
