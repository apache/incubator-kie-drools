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

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.casemgmt.Milestone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.process.casemgmt.ItemDescription.Status.AVAILABLE;
import static org.kie.kogito.process.casemgmt.ItemDescription.Status.COMPLETED;

class MilestoneTest extends AbstractCodegenTest {

    @Test
    void testSimpleMilestone() throws Exception {
        Application app = generateCodeProcessesOnly("cases/SimpleMilestone.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.processes().processById("TestCase.SimpleMilestone");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        assertState(processInstance, ProcessInstance.STATE_PENDING);

        Collection<Milestone> milestones = processInstance.milestones();
        assertThat(milestones.size()).isEqualTo(1);
        Milestone milestone = milestones.iterator().next();
        assertThat(milestone).isNotNull();
        assertThat(milestone.getName()).isEqualTo("Milestone1");
        assertThat(milestone.getStatus()).isEqualByComparingTo(AVAILABLE);

        processInstance.start();
        assertState(processInstance, ProcessInstance.STATE_COMPLETED);

        milestones = processInstance.milestones();
        assertThat(milestones.size()).isEqualTo(1);
        milestone = milestones.iterator().next();
        assertThat(milestone).isNotNull();
        assertThat(milestone.getName()).isEqualTo("Milestone1");
        assertThat(milestone.getStatus()).isEqualByComparingTo(COMPLETED);

        RuleFlowProcessInstance legacyProcessInstance = (RuleFlowProcessInstance) ((AbstractProcessInstance<?>) processInstance).legacyProcessInstance;
        assertThat(legacyProcessInstance.getNodeInstances()).isEmpty();
        assertThat(legacyProcessInstance.getNodeIdInError()).isNullOrEmpty();
        Optional<String> milestoneId = Stream.of(legacyProcessInstance.getNodeContainer().getNodes()).filter(node -> node.getName().equals("Milestone1")).map(n -> (String) n.getMetaData().get(Metadata.UNIQUE_ID)).findFirst();
        assertTrue(milestoneId.isPresent());
        assertThat(legacyProcessInstance.getCompletedNodeIds()).contains(milestoneId.get());
    }

    private void assertState(ProcessInstance<?> processInstance, int state) {
        assertThat(processInstance).isInstanceOf(AbstractProcessInstance.class);
        AbstractProcessInstance<?> abstractProcessInstance = (AbstractProcessInstance<?>) processInstance;
        assertThat(abstractProcessInstance.status).isEqualTo(state);
        assertThat(abstractProcessInstance.legacyProcessInstance.getState()).isEqualTo(state);
    }
}
