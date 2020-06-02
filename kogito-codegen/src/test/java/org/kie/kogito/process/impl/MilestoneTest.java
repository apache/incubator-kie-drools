/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.flexible.Milestone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.process.flexible.ItemDescription.Status.AVAILABLE;
import static org.kie.kogito.process.flexible.ItemDescription.Status.COMPLETED;
import static org.kie.kogito.process.impl.ProcessTestUtils.assertState;

class MilestoneTest extends AbstractCodegenTest {

    @Test
    void testSimpleMilestone() throws Exception {
        Application app = generateCodeProcessesOnly("cases/SimpleMilestone.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.processes().processById("TestCase.SimpleMilestone");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        assertState(processInstance, ProcessInstance.STATE_PENDING);

        Collection<Milestone> expected = new ArrayList<>();
        expected.add(new Milestone.Builder("").withName("AutoStartMilestone").withCondition("").withStatus(AVAILABLE).build());
        expected.add(new Milestone.Builder("").withName("SimpleMilestone").withCondition("").withStatus(AVAILABLE).build());
        assertMilestones(expected, processInstance.milestones());

        processInstance.start();
        assertState(processInstance, ProcessInstance.STATE_COMPLETED);

        expected = expected.stream().map(m -> new Milestone.Builder(m).withStatus(COMPLETED).build()).collect(Collectors.toList());
        assertMilestones(expected, processInstance.milestones());

        RuleFlowProcessInstance legacyProcessInstance = (RuleFlowProcessInstance) ((AbstractProcessInstance<?>) processInstance).legacyProcessInstance;
        assertThat(legacyProcessInstance.getNodeInstances()).isEmpty();
        assertThat(legacyProcessInstance.getNodeIdInError()).isNullOrEmpty();
        Optional<String> milestoneId = Stream.of(legacyProcessInstance.getNodeContainer().getNodes())
                .filter(node -> node.getName().equals("SimpleMilestone"))
                .map(n -> (String) n.getMetaData().get(Metadata.UNIQUE_ID))
                .findFirst();
        assertTrue(milestoneId.isPresent());
        assertThat(legacyProcessInstance.getCompletedNodeIds()).contains(milestoneId.get());
    }

    @Test
    void testConditionalMilestone() throws Exception {
        Application app = generateCodeProcessesOnly("cases/UserTaskCase.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.processes().processById("UserTaskCase");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        assertState(processInstance, ProcessInstance.STATE_PENDING);


        Collection<Milestone> expected = new ArrayList<>();
        expected.add(new Milestone.Builder("").withName("Milestone1").withStatus(AVAILABLE).withCondition("").build());
        expected.add(new Milestone.Builder("").withName("Milestone2").withStatus(AVAILABLE).withCondition("CaseData(data.get(\"dataComplete\") == true)").build());

        Collection<Milestone> milestones = processInstance.milestones();
        assertMilestones(expected, milestones);
    }

    private void assertMilestones(Collection<Milestone> expected, Collection<Milestone> milestones) {
        if (expected == null) {
            assertNull(milestones);
        }
        assertNotNull(milestones);
        assertThat(milestones.size()).isEqualTo(expected.size());
        expected.forEach(e -> assertThat(milestones.stream().anyMatch(c -> Objects.equals(c.getName(), e.getName()) &&
                Objects.equals(c.getCondition(), e.getCondition()) &&
                Objects.equals(c.getStatus(), e.getStatus()))).withFailMessage("Expected: " + e + " - Not present in: " + milestones).isTrue());
    }

}
