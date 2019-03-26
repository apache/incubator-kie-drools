/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.impl.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to provide default functions to use on {@link DeploymentService#undeploy(DeploymentUnit, Function<DeploymentUnit, Boolean>)}
 */
public class PreUndeployOperations {

    private static Logger logger = LoggerFactory.getLogger(PreUndeployOperations.class);

    private static List<Integer> activeProcessInstancessStates = new ArrayList<>();

    static {
        activeProcessInstancessStates.add(ProcessInstance.STATE_ACTIVE);
        activeProcessInstancessStates.add(ProcessInstance.STATE_PENDING);
        activeProcessInstancessStates.add(ProcessInstance.STATE_SUSPENDED);
    }

    /**
     * Returns a function that checks if a given {@link DeploymentUnit} has active process instances and prevents its undeployment.
     * That's the default operation when no other is supplied.
     * @param runtimeDataService a {@link RuntimeDataService} to query the process instances
     */
    public static Function<DeploymentUnit, Boolean> checkActiveProcessInstances(final RuntimeDataService runtimeDataService) {
        return unit -> {
            Collection<ProcessInstanceDesc> activeProcesses = runtimeDataService.getProcessInstancesByDeploymentId(unit.getIdentifier(), activeProcessInstancessStates, new QueryContext());
            if (!activeProcesses.isEmpty()) {
                throw new IllegalStateException("Undeploy forbidden - there are active processes instances for deployment " + unit.getIdentifier());
            }
            return true;
        };
    }

    /**
     * Returns a function that checks if a given {@link DeploymentUnit} has active process instances instances, aborts them and,
     * if nothing wrong happened, lets the undeployment operation continue.
     * @param runtimeDataService a {@link RuntimeDataService} to query the process instances
     * @param deploymentService a {@link DeploymentService} to provide access to the deployed unit.
     */
    public static Function<DeploymentUnit, Boolean> abortUnitActiveProcessInstances(final RuntimeDataService runtimeDataService, final DeploymentService deploymentService) {
        return unit -> {
            Collection<ProcessInstanceDesc> activeProcesses = runtimeDataService.getProcessInstancesByDeploymentId(unit.getIdentifier(), activeProcessInstancessStates, new QueryContext(0, -1));

            DeployedUnit deployedUnit = deploymentService.getDeployedUnit(unit.getIdentifier());

            if (deployedUnit == null) {
                throw new IllegalStateException("Undeploy forbidden - No deployments available for " + unit.getIdentifier());
            }

            for (ProcessInstanceDesc instanceDesc : activeProcesses) {
                RuntimeManager manager = deployedUnit.getRuntimeManager();
                RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(instanceDesc.getId()));
                try {
                    KieSession ksession = engine.getKieSession();
                    ksession.abortProcessInstance(instanceDesc.getId());
                } catch (Exception e) {
                    logger.error("Undeploy forbidden - Error aborting process instances for deployment unit {} due to: {}", unit.getIdentifier(), e.getMessage());
                    return false;
                } finally {
                    manager.disposeRuntimeEngine(engine);
                }
            }

            return true;
        };
    }

    /**
     * Returns a function that bypasses the check and always allows to undeploy.
     */
    public static Function<DeploymentUnit, Boolean> doNothing() {
        return unit -> true;
    }
}