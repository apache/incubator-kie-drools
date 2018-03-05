/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.executor.commands;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryStringCommand;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.Reoccurring;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SLA Tracking command that aims at looking up for process or node instances with SLA violations:
 * Command by default is auto configured to run once an hour from the time it was initially scheduled though it can be reconfigured
 * in terms of frequency when it is executed and if it shall run multiple times at all.<br/>
 * Following is a complete list of accepted parameters:
 * <ul>
 * 	<li>EmfName - name of entity manager factory to be used for queries (valid persistence unit name)</li>
 * 	<li>SingleRun - indicates if execution should be single run only (true|false)</li>
 * 	<li>NextRun - provides next execution time (valid time expression e.g. 1d, 5h, etc)</li>
 * 	<li>ForDeployment - indicates errors to be deleted that are from given deployment id</li>
 * </ul>
 */
public class SLATrackingCommand implements Command, Reoccurring {

    private static final Logger logger = LoggerFactory.getLogger(SLATrackingCommand.class);

    private long nextScheduleTimeAdd = 1 * 60 * 60 * 1000; // one hour in milliseconds

    @Override
    public Date getScheduleTime() {
        if (nextScheduleTimeAdd < 0) {
            return null;
        }

        long current = System.currentTimeMillis();

        Date nextSchedule = new Date(current + nextScheduleTimeAdd);
        logger.debug("Next schedule for job {} is set to {}", this.getClass().getSimpleName(), nextSchedule);

        return nextSchedule;
    }

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        ExecutionResults executionResults = new ExecutionResults();
        String emfName = (String) ctx.getData("EmfName");
        if (emfName == null) {
            emfName = "org.jbpm.domain";
        }
        String singleRun = (String) ctx.getData("SingleRun");
        if ("true".equalsIgnoreCase(singleRun)) {
            // disable rescheduling
            this.nextScheduleTimeAdd = -1;
        }
        String nextRun = (String) ctx.getData("NextRun");
        if (nextRun != null) {
            nextScheduleTimeAdd = DateTimeUtils.parseDateAsDuration(nextRun);
        }

        // get hold of persistence and create instance of audit service
        EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate(emfName);

        // collect parameters
        String forDeployment = (String) ctx.getData("ForDeployment");

        // SLA Violations on nodes
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("now", new Date());
        StringBuilder lookupQuery = new StringBuilder();

        lookupQuery.append("select log from NodeInstanceLog log where ");
        lookupQuery.append("log.nodeInstanceId in ( select nil.nodeInstanceId from NodeInstanceLog nil where nil.slaDueDate < :now and nil.slaCompliance = 1 ");
        lookupQuery.append("GROUP BY nil.nodeInstanceId ");
        lookupQuery.append("HAVING sum(nil.type) = 0) ");
        lookupQuery.append("and log.type = 0 ");
        if (forDeployment != null && !forDeployment.isEmpty()) {
            lookupQuery.append(" and log.externalId = :forDeployment");
            parameters.put("forDeployment", forDeployment);
        }

        TransactionalCommandService commandService = new TransactionalCommandService(emf);
        List<NodeInstanceLog> nodeInstancesViolations = commandService.execute(new QueryStringCommand<List<NodeInstanceLog>>(lookupQuery.toString(), parameters));
        logger.debug("Number of node instances with violated SLA {}", nodeInstancesViolations.size());

        if (!nodeInstancesViolations.isEmpty()) {
            logger.debug("Signaling process instances that have SLA violations on nodes");
            int nodeSignals = 0;
            for (NodeInstanceLog niLog : nodeInstancesViolations) {
                RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(niLog.getExternalId());
                if (runtimeManager == null) {
                    logger.debug("No runtime manager found for {}, not able to send SLA violation signal", niLog.getExternalId());
                    continue;
                }

                RuntimeEngine engine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(niLog.getProcessInstanceId()));

                try {

                    engine.getKieSession().signalEvent("slaViolation:" + niLog.getNodeInstanceId(), null, niLog.getProcessInstanceId());
                    nodeSignals++;
                } catch (Exception e) {
                    logger.warn("Unexpected error when signalig process instance {} about SLA violation {}", niLog.getProcessInstanceId(), e.getMessage(), e);
                } finally {
                    runtimeManager.disposeRuntimeEngine(engine);
                }
            }
            logger.info("SLA Violations JOB :: Number of nodes successfully signaled is {}", nodeSignals);
            executionResults.setData("NodeSLASignals", nodeSignals);
        }
        // SLA Violations on process instances
        parameters = new HashMap<>();
        parameters.put("now", new Date());
        lookupQuery = new StringBuilder();

        lookupQuery.append("select log from ProcessInstanceLog log where log.slaDueDate < :now and log.slaCompliance = 1 ");
        if (forDeployment != null && !forDeployment.isEmpty()) {
            lookupQuery.append(" and log.externalId = :forDeployment");
            parameters.put("forDeployment", forDeployment);
        }

        List<ProcessInstanceLog> processInstancesViolations = commandService.execute(new QueryStringCommand<List<ProcessInstanceLog>>(lookupQuery.toString(), parameters));
        logger.debug("Number of node instances with violated SLA {}", nodeInstancesViolations.size());

        if (!processInstancesViolations.isEmpty()) {
            logger.debug("Signaling process instances that have SLA violations");
            int processSignals = 0;
            for (ProcessInstanceLog piLog : processInstancesViolations) {
                RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(piLog.getExternalId());
                if (runtimeManager == null) {
                    logger.debug("No runtime manager found for {}, not able to send SLA violation signal", piLog.getExternalId());
                    continue;
                }

                RuntimeEngine engine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(piLog.getProcessInstanceId()));

                try {

                    engine.getKieSession().signalEvent("slaViolation", null, piLog.getProcessInstanceId());
                    processSignals++;
                } catch (Exception e) {
                    logger.warn("Unexpected error when signalig process instance {} about SLA violation {}", piLog.getProcessInstanceId(), e.getMessage(), e);
                } finally {
                    runtimeManager.disposeRuntimeEngine(engine);
                }
            }
            logger.info("SLA Violations JOB :: Number of process instances successfully signaled is {}", processSignals);
            executionResults.setData("ProcessSLASignals", processSignals);
        }

        return executionResults;
    }

}
