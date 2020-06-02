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
import java.util.List;

import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.flexible.AdHocFragment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdHocFragmentsTest extends AbstractCodegenTest {

    @Test
    void testAdHocFragments() throws Exception {
        Application app = generateCodeProcessesOnly("cases/AdHocFragments.bpmn");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.processes().processById("TestCase.AdHocFragments");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        Collection<AdHocFragment> adHocFragments = processInstance.adHocFragments();
        List<AdHocFragment> expected = new ArrayList<>();
        expected.add(new AdHocFragment.Builder(MilestoneNode.class).withName("AdHoc Milestone").withAutoStart(true).build());
        expected.add(new AdHocFragment.Builder(ActionNode.class).withName("AdHoc Script").withAutoStart(false).build());
        expected.add(new AdHocFragment.Builder(HumanTaskNode.class).withName("AdHoc User Task").withAutoStart(true).build());
        assertAdHocFragments(expected, adHocFragments);
    }

    private static void assertAdHocFragments(Collection<AdHocFragment> expected, Collection<AdHocFragment> current) {
        if (expected == null) {
            assertThat(current).isNull();
        }
        assertThat(current).isNotNull();
        assertThat(current.size()).isEqualTo(expected.size());
        expected.forEach(e -> assertTrue(
                current.stream().anyMatch(c -> c.getName().equals(e.getName()) && c.getType().equals(e.getType()) && c.isAutoStart() == e.isAutoStart()),
                "Expected: " + e.toString() + ", Got: " + current.toString())
        );
    }
}
