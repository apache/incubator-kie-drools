/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.runtime.manager.impl.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.drools.core.command.SingleSessionCommandService;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.persistence.api.SessionNotFoundException;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.impl.migration.MigrationEntry.Type;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.jbpm.workflow.instance.node.StateBasedNodeInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MigrationManager is responsible for updating all required components during process instance migration.
 * Each process instance should be have dedicated instance of the manager to allow simple execution model.
 * Each manager maintains MigrationReport that is constantly updated when migration is running.
 * 
 * It comes with following migration entries (as part of the report)
 * <ul>
 *  <li>INFO - written mostly for information about given migration step and its result</li>
 *  <li>WARN - not recommended operation performed though did not stop the migration</li>
 *  <li>ERROR - terminates the migration and restores to last state - before migration</li>
 * </ul>
 * There could be at most single ERROR type of entry as first one that occurred terminates the migration.
 * 
 * Migration is composed of two steps
 * <ul>
 *  <li>validation - various checks to ensure migration can be performed to limit number of failed migrations</li>
 *  <li>migration - actual migration that changes state of the process instance and its index data - history logs</li>
 * <ul>
 * 
 * Migration can either be performed with or without node instance mapping. Node instance mapping allows to map nodes
 * only of the same type as it simply changes node reference of the node and does not replace the node instance 
 * (by canceling current node and triggering new one).
 */
public class MigrationManager {

    private static final Logger logger = LoggerFactory.getLogger(MigrationManager.class);

    private MigrationReport report;
    private MigrationSpec migrationSpec;

    /**
     * Creates new instance of MigrationManager with given migration specification.
     * Migration specification will be validated upon call to {@link #migrate()} method
     * @param migrationSpec definition of what needs to be migrated
     */
    public MigrationManager(MigrationSpec migrationSpec) {
        this.report = new MigrationReport(migrationSpec);
        this.migrationSpec = migrationSpec;
    }

    /**
     * Performs migration without node instance mapping
     * @return returns migration report describing complete migration process.
     */
    public MigrationReport migrate() {
        return migrate(null);
    }

    /**
     * Performs migration with node mapping (if non null).
     * @param nodeMapping node instance mapping that is composed of unique ids of source node mapped to target node
     * @return returns migration report describing complete migration process.
     */
    public MigrationReport migrate(Map<String, String> nodeMapping) {

        validate();
        KieSession current = null;
        KieSession tobe = null;
        TransactionManager txm = null;
        boolean transactionOwner = false;
        InternalRuntimeManager currentManager = (InternalRuntimeManager) RuntimeManagerRegistry.get().getManager(migrationSpec.getDeploymentId());
        InternalRuntimeManager toBeManager = (InternalRuntimeManager) RuntimeManagerRegistry.get().getManager(migrationSpec.getToDeploymentId());

        Map<Long, List<TimerInstance>> timerMigrated = null;
        try {

            // collect and cancel any active timers before migration
            timerMigrated = cancelActiveTimersBeforeMigration(currentManager);

            // start transaction to secure consistency of the migration			
            txm = TransactionManagerFactory.get().newTransactionManager(currentManager.getEnvironment().getEnvironment());
            transactionOwner = txm.begin();

            org.kie.api.definition.process.Process toBeProcess = toBeManager.getEnvironment().getKieBase().getProcess(migrationSpec.getToProcessId());

            String auditPu = currentManager.getDeploymentDescriptor().getAuditPersistenceUnit();

            EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate(auditPu);
            EntityManager em = emf.createEntityManager();

            try {
                // update variable instance log information with new deployment id and process id
                Query varLogQuery = em.createQuery("update VariableInstanceLog set externalId = :depId, processId = :procId where processInstanceId = :procInstanceId");
                varLogQuery
                           .setParameter("depId", migrationSpec.getToDeploymentId())
                           .setParameter("procId", migrationSpec.getToProcessId())
                           .setParameter("procInstanceId", migrationSpec.getProcessInstanceId());

                int varsUpdated = varLogQuery.executeUpdate();
                report.addEntry(Type.INFO, "Variable instances updated = " + varsUpdated + " for process instance id " + migrationSpec.getProcessInstanceId());

                // update node instance log information with new deployment id and process id
                Query nodeLogQuery = em.createQuery("update NodeInstanceLog set externalId = :depId, processId = :procId where processInstanceId = :procInstanceId");
                nodeLogQuery
                            .setParameter("depId", migrationSpec.getToDeploymentId())
                            .setParameter("procId", migrationSpec.getToProcessId())
                            .setParameter("procInstanceId", migrationSpec.getProcessInstanceId());

                int nodesUpdated = nodeLogQuery.executeUpdate();
                report.addEntry(Type.INFO, "Node instances updated = " + nodesUpdated + " for process instance id " + migrationSpec.getProcessInstanceId());

                // update process instance log with new deployment and process id
                Query pInstanceLogQuery = em.createQuery(
                                                         "update ProcessInstanceLog set externalId = :depId, processId = :procId, processName = :procName, processVersion= :procVersion where processInstanceId = :procInstanceId");
                pInstanceLogQuery
                                 .setParameter("depId", migrationSpec.getToDeploymentId())
                                 .setParameter("procId", migrationSpec.getToProcessId())
                                 .setParameter("procName", toBeProcess.getName())
                                 .setParameter("procVersion", toBeProcess.getVersion())
                                 .setParameter("procInstanceId", migrationSpec.getProcessInstanceId());

                int pInstancesUpdated = pInstanceLogQuery.executeUpdate();
                report.addEntry(Type.INFO, "Process instances updated = " + pInstancesUpdated + " for process instance id " + migrationSpec.getProcessInstanceId());

                try {
                    // update task audit instance log with new deployment and process id
                    Query taskVarLogQuery = em.createQuery("update TaskVariableImpl set processId = :procId where processInstanceId = :procInstanceId");
                    taskVarLogQuery
                                   .setParameter("procId", migrationSpec.getToProcessId())
                                   .setParameter("procInstanceId", migrationSpec.getProcessInstanceId());

                    int taskVarUpdated = taskVarLogQuery.executeUpdate();
                    report.addEntry(Type.INFO, "Task variables updated = " + taskVarUpdated + " for process instance id " + migrationSpec.getProcessInstanceId());
                } catch (Throwable e) {
                    logger.warn("Unexpected error during migration", e);
                    report.addEntry(Type.WARN, "Cannot update task variables (added in version 6.3) due to " + e.getMessage());
                }

                // update task audit instance log with new deployment and process id
                Query auditTaskLogQuery = em.createQuery("update AuditTaskImpl set deploymentId = :depId, processId = :procId where processInstanceId = :procInstanceId");
                auditTaskLogQuery
                                 .setParameter("depId", migrationSpec.getToDeploymentId())
                                 .setParameter("procId", migrationSpec.getToProcessId())
                                 .setParameter("procInstanceId", migrationSpec.getProcessInstanceId());

                int auditTaskUpdated = auditTaskLogQuery.executeUpdate();
                report.addEntry(Type.INFO, "Task audit updated = " + auditTaskUpdated + " for process instance id " + migrationSpec.getProcessInstanceId());

                // update task  instance log with new deployment and process id
                Query taskLogQuery = em.createQuery("update TaskImpl set deploymentId = :depId, processId = :procId where processInstanceId = :procInstanceId");
                taskLogQuery
                            .setParameter("depId", migrationSpec.getToDeploymentId())
                            .setParameter("procId", migrationSpec.getToProcessId())
                            .setParameter("procInstanceId", migrationSpec.getProcessInstanceId());

                int taskUpdated = taskLogQuery.executeUpdate();
                report.addEntry(Type.INFO, "Tasks updated = " + taskUpdated + " for process instance id " + migrationSpec.getProcessInstanceId());

                try {
                    // update context mapping info with new deployment
                    Query contextInfoQuery = em.createQuery("update ContextMappingInfo set ownerId = :depId where contextId = :procInstanceId");
                    contextInfoQuery
                                    .setParameter("depId", migrationSpec.getToDeploymentId())
                                    .setParameter("procInstanceId", migrationSpec.getProcessInstanceId().toString());

                    int contextInfoUpdated = contextInfoQuery.executeUpdate();
                    report.addEntry(Type.INFO, "Context info updated = " + contextInfoUpdated + " for process instance id " + migrationSpec.getProcessInstanceId());
                } catch (Throwable e) {
                    logger.warn("Unexpected error during migration", e);
                    report.addEntry(Type.WARN, "Cannot update context mapping owner (added in version 6.2) due to " + e.getMessage());
                }

                current = JPAKnowledgeService.newStatefulKnowledgeSession(currentManager.getEnvironment().getKieBase(), null, currentManager.getEnvironment().getEnvironment());
                tobe = JPAKnowledgeService.newStatefulKnowledgeSession(toBeManager.getEnvironment().getKieBase(), null, toBeManager.getEnvironment().getEnvironment());

                upgradeProcessInstance(current, tobe, migrationSpec.getProcessInstanceId(), migrationSpec.getToProcessId(), nodeMapping, em, toBeManager.getIdentifier());

                if (!timerMigrated.isEmpty()) {
                    rescheduleTimersAfterMigration(toBeManager, timerMigrated);
                }
                em.flush();
            } finally {
                em.clear();
                em.close();
            }

            txm.commit(transactionOwner);
            report.addEntry(Type.INFO, "Migration of process instance (" + migrationSpec.getProcessInstanceId() + ") completed successfully to process " + migrationSpec.getToProcessId());
            report.setSuccessful(true);
            report.setEndDate(new Date());
        } catch (Throwable e) {
            txm.rollback(transactionOwner);
            logger.error("Unexpected error during migration", e);
            // put back timers (if there are any) in case of rollback
            if (timerMigrated != null && !timerMigrated.isEmpty()) {
                rescheduleTimersAfterMigration(currentManager, timerMigrated);
            }
            report.addEntry(Type.ERROR, "Migration of process instance (" + migrationSpec.getProcessInstanceId() + ") failed due to " + e.getMessage());

        } finally {
            if (current != null) {
                try {
                    current.destroy();
                } catch (SessionNotFoundException e) {
                    // in case of rollback session might not exist
                }
            }

            if (tobe != null) {

                try {
                    tobe.destroy();
                } catch (SessionNotFoundException e) {
                    // in case of rollback session might not exist
                }
            }
        }

        return report;
    }

    private void validate() {
        if (migrationSpec == null) {
            report.addEntry(Type.ERROR, "no process data given for migration");
            return;
        }
        // source (active) process instance information
        if (isEmpty(migrationSpec.getDeploymentId())) {
            report.addEntry(Type.ERROR, "No deployment id set");
        }
        if (migrationSpec.getProcessInstanceId() == null) {
            report.addEntry(Type.ERROR, "No process instance id set");
        }
        // target process information
        if (isEmpty(migrationSpec.getToDeploymentId())) {
            report.addEntry(Type.ERROR, "No target deployment id set");
        }
        if (isEmpty(migrationSpec.getToProcessId())) {
            report.addEntry(Type.ERROR, "No target process id set");
        }

        // verify if given runtime manager exists - registered under source deployment id
        if (!RuntimeManagerRegistry.get().isRegistered(migrationSpec.getDeploymentId())) {
            report.addEntry(Type.ERROR, "No deployment found for " + migrationSpec.getDeploymentId());
        }
        // verify if given runtime manager exists - registered under target deployment id
        if (!RuntimeManagerRegistry.get().isRegistered(migrationSpec.getToDeploymentId())) {
            report.addEntry(Type.ERROR, "No target deployment found for " + migrationSpec.getToDeploymentId());
        }

        // verify if given target process id exists in target runtime manager
        InternalRuntimeManager manager = (InternalRuntimeManager) RuntimeManagerRegistry.get().getManager(migrationSpec.getToDeploymentId());
        if (manager.getEnvironment().getKieBase().getProcess(migrationSpec.getToProcessId()) == null) {
            report.addEntry(Type.ERROR, "No process found for " + migrationSpec.getToProcessId() + " in deployment " + migrationSpec.getToDeploymentId());
        }
        
        // verify that source and target runtime manager is of the same type - represent the same runtime strategy
        InternalRuntimeManager sourceManager = (InternalRuntimeManager) RuntimeManagerRegistry.get().getManager(migrationSpec.getDeploymentId());
        if (!sourceManager.getClass().isAssignableFrom(manager.getClass())) {
            report.addEntry(Type.ERROR, "Source (" + sourceManager.getClass().getName() + ") and target (" + manager.getClass().getName() + ") deployments are of different type (they represent different runtime strategies)");
        }

        String auditPu = manager.getDeploymentDescriptor().getAuditPersistenceUnit();

        EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate(auditPu);

        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        try {
            ProcessInstanceLog log = auditService.findProcessInstance(migrationSpec.getProcessInstanceId());
            if (log == null || log.getStatus() != ProcessInstance.STATE_ACTIVE) {
                report.addEntry(Type.ERROR, "No process instance found or it is not active (id " + migrationSpec.getProcessInstanceId() + " in status " + (log == null ? "-1" : log.getStatus()));
            }
        } finally {
            auditService.dispose();
        }
    }

    private void upgradeProcessInstance(KieRuntime oldkruntime,
                                        KieRuntime kruntime,
                                        long processInstanceId,
                                        String processId,
                                        Map<String, String> nodeMapping,
                                        EntityManager em,
                                        String deploymentId) {
        if (nodeMapping == null) {
            nodeMapping = new HashMap<String, String>();
        }
        WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl) oldkruntime.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            report.addEntry(Type.ERROR, "Could not find process instance " + processInstanceId);
        }
        if (processId == null) {
            report.addEntry(Type.ERROR, "Null process id");
        }
        WorkflowProcess process = (WorkflowProcess) kruntime.getKieBase().getProcess(processId);
        if (process == null) {
            report.addEntry(Type.ERROR, "Could not find process " + processId);
        }
        if (processInstance.getProcessId().equals(processId)) {
            report.addEntry(Type.WARN, "Source and target process id is exactly the same (" + processId + ") it's recommended to use unique process ids");
        }
        synchronized (processInstance) {
            org.kie.api.definition.process.Process oldProcess = processInstance.getProcess();
            processInstance.disconnect();
            processInstance.setProcess(oldProcess);
            updateNodeInstances(processInstance, nodeMapping, (NodeContainer) process, em);
            processInstance.setKnowledgeRuntime((InternalKnowledgeRuntime) extractIfNeeded(kruntime));
            processInstance.setDeploymentId(deploymentId);
            processInstance.setProcess(process);
            processInstance.reconnect();
        }
    }

    @SuppressWarnings("unchecked")
    private void updateNodeInstances(NodeInstanceContainer nodeInstanceContainer, Map<String, String> nodeMapping, NodeContainer nodeContainer, EntityManager em) {

        for (NodeInstance nodeInstance : nodeInstanceContainer.getNodeInstances()) {
            Long upgradedNodeId = null;
            String oldNodeId = (String) ((NodeImpl) ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getNode()).getMetaData().get("UniqueId");
            String newNodeId = nodeMapping.get(oldNodeId);
            if (newNodeId == null) {
                newNodeId = oldNodeId;
            }
            Node upgradedNode = findNodeByUniqueId(newNodeId, nodeContainer);
            if (upgradedNode == null) {
                try {
                    upgradedNodeId = Long.parseLong(newNodeId);
                } catch (NumberFormatException e) {
                    continue;
                }
            } else {
                upgradedNodeId = upgradedNode.getId();
            }

            ((NodeInstanceImpl) nodeInstance).setNodeId(upgradedNodeId);

            if (upgradedNode != null) {
                // update log information for new node information
                Query nodeInstanceIdQuery = em.createQuery("select nodeInstanceId from NodeInstanceLog nil" +
                                                           " where nil.nodeId = :oldNodeId and processInstanceId = :processInstanceId " +
                                                           " GROUP BY nil.nodeInstanceId" +
                                                           " HAVING sum(nil.type) = 0");
                nodeInstanceIdQuery
                                   .setParameter("oldNodeId", oldNodeId)
                                   .setParameter("processInstanceId", nodeInstance.getProcessInstance().getId());

                List<Long> nodeInstanceIds = nodeInstanceIdQuery.getResultList();
                report.addEntry(Type.INFO, "Mapping: Node instance logs to be updated  = " + nodeInstanceIds);

                Query nodeLogQuery = em.createQuery("update NodeInstanceLog set nodeId = :nodeId, nodeName = :nodeName, nodeType = :nodeType " +
                                                    "where nodeInstanceId in (:ids) and processInstanceId = :processInstanceId");
                nodeLogQuery
                            .setParameter("nodeId", (String) upgradedNode.getMetaData().get("UniqueId"))
                            .setParameter("nodeName", upgradedNode.getName())
                            .setParameter("nodeType", upgradedNode.getClass().getSimpleName())
                            .setParameter("ids", nodeInstanceIds)
                            .setParameter("processInstanceId", nodeInstance.getProcessInstance().getId());

                int nodesUpdated = nodeLogQuery.executeUpdate();
                report.addEntry(Type.INFO, "Mapping: Node instance logs updated = " + nodesUpdated + " for node instance id " + nodeInstance.getId());

                if (upgradedNode instanceof HumanTaskNode && nodeInstance instanceof HumanTaskNodeInstance) {

                    Long taskId = (Long) em.createQuery("select id from TaskImpl where workItemId = :workItemId")
                                           .setParameter("workItemId", ((HumanTaskNodeInstance) nodeInstance).getWorkItemId())
                                           .getSingleResult();
                    String name = ((HumanTaskNode) upgradedNode).getName();
                    String description = (String) ((HumanTaskNode) upgradedNode).getWork().getParameter("Description");

                    // update task audit instance log with new deployment and process id
                    Query auditTaskLogQuery = em.createQuery("update AuditTaskImpl set name = :name, description = :description where taskId = :taskId");
                    auditTaskLogQuery
                                     .setParameter("name", name)
                                     .setParameter("description", description)
                                     .setParameter("taskId", taskId);

                    int auditTaskUpdated = auditTaskLogQuery.executeUpdate();
                    report.addEntry(Type.INFO, "Mapping: Task audit updated = " + auditTaskUpdated + " for task id " + taskId);

                    // update task  instance log with new deployment and process id
                    Query taskLogQuery = em.createQuery("update TaskImpl set name = :name, description = :description where id = :taskId");
                    taskLogQuery
                                .setParameter("name", name)
                                .setParameter("description", description)
                                .setParameter("taskId", taskId);

                    int taskUpdated = taskLogQuery.executeUpdate();
                    report.addEntry(Type.INFO, "Mapping: Task updated = " + taskUpdated + " for task id " + taskId);

                }
            }

            if (nodeInstance instanceof NodeInstanceContainer) {
                updateNodeInstances((NodeInstanceContainer) nodeInstance, nodeMapping, nodeContainer, em);
            }
        }

    }

    private Node findNodeByUniqueId(String uniqueId, NodeContainer nodeContainer) {
        Node result = null;

        for (Node node : nodeContainer.getNodes()) {
            if (uniqueId.equals(node.getMetaData().get("UniqueId"))) {
                return node;
            }
            if (node instanceof NodeContainer) {
                result = findNodeByUniqueId(uniqueId, (NodeContainer) node);
                if (result != null) {
                    return result;
                }
            }
        }

        return result;
    }

    private KieRuntime extractIfNeeded(KieRuntime ksession) {
        if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            return ((SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession).getRunner()).getKieSession();
        }

        return ksession;
    }

    private boolean isEmpty(String value) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        return false;
    }

    protected TimerManager getTimerManager(KieSession ksession) {
        KieSession internal = ksession;
        if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            internal = ((SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession).getRunner()).getKieSession();
        }

        return ((InternalProcessRuntime) ((StatefulKnowledgeSessionImpl) internal).getProcessRuntime()).getTimerManager();
    }

    protected Map<Long, List<TimerInstance>> cancelActiveTimersBeforeMigration(RuntimeManager manager) {
        RuntimeEngine engineBefore = manager.getRuntimeEngine(ProcessInstanceIdContext.get(migrationSpec.getProcessInstanceId()));
        try {
            Map<Long, List<TimerInstance>> timerMigrated = engineBefore.getKieSession().execute(new ExecutableCommand<Map<Long, List<TimerInstance>>>() {

                private static final long serialVersionUID = 7144271692067781976L;

                @Override
                public Map<Long, List<TimerInstance>> execute(Context context) {

                    Map<Long, List<TimerInstance>> result = new LinkedHashMap<>();
                    KieSession kieSession = ((RegistryContext) context).lookup(KieSession.class);

                    TimerManager timerManager = getTimerManager(kieSession);

                    WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl) kieSession.getProcessInstance(migrationSpec.getProcessInstanceId());

                    Collection<org.jbpm.workflow.instance.NodeInstance> activeInstances = processInstance.getNodeInstances(true);

                    for (org.jbpm.workflow.instance.NodeInstance active : activeInstances) {
                        if (active instanceof TimerNodeInstance) {
                            TimerInstance timerInstance = timerManager.getTimerMap().get(((TimerNodeInstance) active).getTimerId());

                            timerManager.cancelTimer(timerInstance.getId());
                            result.put(active.getId(), Arrays.asList(timerInstance));
                        } else if (active instanceof StateBasedNodeInstance) {
                            List<Long> timers = ((StateBasedNodeInstance) active).getTimerInstances();

                            if (timers != null && !timers.isEmpty()) {
                                List<TimerInstance> collected = new ArrayList<>();
                                for (Long timerId : timers) {
                                    TimerInstance timerInstance = timerManager.getTimerMap().get(timerId);

                                    timerManager.cancelTimer(timerInstance.getId());
                                    collected.add(timerInstance);
                                }
                                result.put(active.getId(), collected);
                            }
                        }
                    }

                    return result;
                }
            });

            return timerMigrated;
        } finally {
            manager.disposeRuntimeEngine(engineBefore);
        }
    }

    protected void rescheduleTimersAfterMigration(RuntimeManager manager, Map<Long, List<TimerInstance>> timerMigrated) {
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(migrationSpec.getProcessInstanceId()));
        try {
            engine.getKieSession().execute(new ExecutableCommand<Void>() {

                private static final long serialVersionUID = 7144657913971146080L;

                @Override
                public Void execute(Context context) {
                    KieSession kieSession = ((RegistryContext) context).lookup(KieSession.class);
                    TimerManager timerManager = getTimerManager(kieSession);

                    WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl) kieSession.getProcessInstance(migrationSpec.getProcessInstanceId());

                    for (Entry<Long, List<TimerInstance>> entry : timerMigrated.entrySet()) {

                        org.jbpm.workflow.instance.NodeInstance active = processInstance.getNodeInstance(entry.getKey(), false);
                        if (active instanceof TimerNodeInstance) {
                            TimerInstance timerInstance = entry.getValue().get(0);

                            long delay = timerInstance.getDelay() - (System.currentTimeMillis() - timerInstance.getActivated().getTime());
                            timerInstance.setDelay(delay);

                            timerManager.registerTimer(timerInstance, processInstance);
                            ((TimerNodeInstance) active).internalSetTimerId(timerInstance.getId());
                        } else if (active instanceof StateBasedNodeInstance) {

                            List<TimerInstance> timerInstances = entry.getValue();
                            List<Long> timers = new ArrayList<>();
                            for (TimerInstance timerInstance : timerInstances) {
                                long delay = timerInstance.getDelay() - (System.currentTimeMillis() - timerInstance.getActivated().getTime());
                                timerInstance.setDelay(delay);

                                timerManager.registerTimer(timerInstance, processInstance);
                                timers.add(timerInstance.getId());
                            }
                            ((StateBasedNodeInstance) active).internalSetTimerInstances(timers);

                        }
                    }

                    return null;
                }

            });
        } finally {

            manager.disposeRuntimeEngine(engine);
        }
    }

}
