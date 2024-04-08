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
package org.jbpm.flow.migration;

import java.util.Arrays;
import java.util.List;

import org.jbpm.flow.migration.model.MigrationPlan;
import org.jbpm.flow.migration.model.NodeInstanceMigrationPlan;
import org.jbpm.flow.migration.model.ProcessDefinitionMigrationPlan;
import org.jbpm.flow.migration.model.ProcessInstanceMigrationPlan;
import org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationPlanProviderTest {

    @Test
    public void testReadingFiles() {
        MigrationPlanProvider provider = MigrationPlanProvider.newMigrationPlanProviderBuilder().withEnvironmentDefaults().build();
        List<MigrationPlan> plans = provider.findMigrationPlans();

        NodeInstanceMigrationPlan a = new NodeInstanceMigrationPlan(
                WorkflowElementIdentifierFactory.fromExternalFormat("node_1"),
                WorkflowElementIdentifierFactory.fromExternalFormat("node_2"));

        NodeInstanceMigrationPlan b = new NodeInstanceMigrationPlan(
                WorkflowElementIdentifierFactory.fromExternalFormat("node_2"),
                WorkflowElementIdentifierFactory.fromExternalFormat("node_3"));

        ProcessInstanceMigrationPlan pdmp = new ProcessInstanceMigrationPlan();
        pdmp.setSourceProcessDefinition(new ProcessDefinitionMigrationPlan("process_A", "1"));
        pdmp.setTargetProcessDefinition(new ProcessDefinitionMigrationPlan("process_B", "2"));
        pdmp.setNodeInstanceMigrationPlan(Arrays.asList(a, b));

        MigrationPlan plan = new MigrationPlan();
        plan.setName("my simple migration");
        plan.setProcessMigrationPlan(pdmp);

        assertThat(plans)
                .hasSize(1)
                .containsExactly(plan);

    }

}
