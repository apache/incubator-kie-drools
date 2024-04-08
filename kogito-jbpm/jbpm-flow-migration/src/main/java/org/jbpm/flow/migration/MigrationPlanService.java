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

import java.util.HashMap;
import java.util.Map;

import org.jbpm.flow.migration.model.MigrationPlan;
import org.jbpm.flow.migration.model.ProcessDefinitionMigrationPlan;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Functions.identity;
import static java.util.stream.Collectors.toMap;

public class MigrationPlanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationPlanService.class);

    private MigrationPlanProvider migrationPlanProvider;
    private Map<ProcessDefinitionMigrationPlan, MigrationPlan> migrations;

    public MigrationPlanService() {
        this(MigrationPlanProvider.newMigrationPlanProviderBuilder().withEnvironmentDefaults().build());
    }

    public MigrationPlanService(MigrationPlanProvider migrationPlanProvider) {
        this.migrationPlanProvider = migrationPlanProvider;
        this.migrations = new HashMap<>();
        this.migrations.putAll(this.migrationPlanProvider.findMigrationPlans().stream().collect(toMap(MigrationPlan::getSource, identity())));
    }

    public void migrateProcessElement(KogitoWorkflowProcessInstance processInstance) {
        MigrationPlan plan = getMigrationPlan(processInstance);
        if (plan != null) {
            // the process will have to do nothing as it is done by the engine itself
            LOGGER.info("Process instance {} will be migrated from {} to {} with plan {}",
                    processInstance.getStringId(),
                    plan.getProcessMigrationPlan().getSourceProcessDefinition(),
                    plan.getProcessMigrationPlan().getTargetProcessDefinition(),
                    plan.getName());

            RuleFlowProcessInstance ruleFlowProcessInstance = (RuleFlowProcessInstance) processInstance;
            ruleFlowProcessInstance.setProcessId(plan.getProcessMigrationPlan().getTargetProcessDefinition().getProcessId());
            ruleFlowProcessInstance.setProcessVersion(plan.getProcessMigrationPlan().getTargetProcessDefinition().getProcessVersion());
        } else {
            LOGGER.debug("Process instance {} won't be migrated", processInstance);
        }
    }

    public void migrateNodeElement(KogitoNodeInstance nodeInstance) {
        KogitoWorkflowProcessInstance pi = (KogitoWorkflowProcessInstance) nodeInstance.getProcessInstance();
        MigrationPlan plan = getMigrationPlan(pi);
        if (plan == null) {
            return;
        }

        LOGGER.debug("Migration node element {}", nodeInstance);
        NodeInstanceImpl impl = (NodeInstanceImpl) nodeInstance;
        impl.setNodeId(plan.getProcessMigrationPlan().getNodeMigratedFor(nodeInstance));
    }

    private MigrationPlan getMigrationPlan(KogitoWorkflowProcessInstance processInstance) {
        RuleFlowProcessInstance pi = (RuleFlowProcessInstance) processInstance;
        ProcessDefinitionMigrationPlan pd =
                new ProcessDefinitionMigrationPlan(pi.getProcessId(), pi.getProcessVersion());
        return migrations.get(pd);
    }

    public boolean shouldMigrate(KogitoWorkflowProcessInstance processInstance) {
        return getMigrationPlan(processInstance) != null;
    }
}
