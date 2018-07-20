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

package org.jbpm.workflow.instance.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.event.ProcessEventSupport;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.ProcessInstanceResolverFactory;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;

public class DynamicUtils {

    private static final Logger logger = LoggerFactory.getLogger(DynamicUtils.class);

    public static void addDynamicWorkItem(
            final DynamicNodeInstance dynamicContext,
            KieRuntime ksession,
            String workItemName,
            Map<String, Object> parameters) {
        final WorkflowProcessInstance processInstance = dynamicContext.getProcessInstance();
        internalAddDynamicWorkItem(processInstance,
                                   dynamicContext,
                                   ksession,
                                   workItemName,
                                   parameters);
    }

    public static void addDynamicWorkItem(
            final org.kie.api.runtime.process.ProcessInstance dynamicProcessInstance,
            KieRuntime ksession,
            String workItemName,
            Map<String, Object> parameters) {
        internalAddDynamicWorkItem((WorkflowProcessInstance) dynamicProcessInstance,
                                   null,
                                   ksession,
                                   workItemName,
                                   parameters);
    }

    private static void internalAddDynamicWorkItem(
            final WorkflowProcessInstance processInstance,
            final DynamicNodeInstance dynamicContext,
            KieRuntime ksession,
            String workItemName,
            Map<String, Object> parameters) {
        final WorkItemImpl workItem = new WorkItemImpl();
        workItem.setState(WorkItem.ACTIVE);
        workItem.setProcessInstanceId(processInstance.getId());
        workItem.setDeploymentId((String) ksession.getEnvironment().get(EnvironmentName.DEPLOYMENT_ID));
        workItem.setName(workItemName);
        workItem.setParameters(parameters);

        for (Map.Entry<String, Object> entry : workItem.getParameters().entrySet()) {
            if (entry.getValue() instanceof String) {
                String s = (String) entry.getValue();
                Object variableValue = null;
                Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(s);
                while (matcher.find()) {
                    String paramName = matcher.group(1);
                    variableValue = processInstance.getVariable(paramName);
                    if (variableValue == null) {
                        try {
                            variableValue = MVELSafeHelper.getEvaluator().eval(paramName,
                                                                               new ProcessInstanceResolverFactory(processInstance));
                        } catch (Throwable t) {
                            logger.error("Could not find variable scope for variable {}",
                                         paramName);
                            logger.error("when trying to replace variable in string for Dynamic Work Item {}",
                                         workItemName);
                            logger.error("Continuing without setting parameter.");
                        }
                    }
                }
                if (variableValue != null) {
                    workItem.setParameter(entry.getKey(),
                                          variableValue);
                }
            }
        }

        final WorkItemNodeInstance workItemNodeInstance = new WorkItemNodeInstance();
        workItemNodeInstance.internalSetWorkItem(workItem);
        workItemNodeInstance.setMetaData("NodeType",
                                         workItemName);
        workItem.setNodeInstanceId(workItemNodeInstance.getId());
        if (ksession instanceof StatefulKnowledgeSessionImpl) {
            workItemNodeInstance.setProcessInstance(processInstance);
            workItemNodeInstance.setNodeInstanceContainer(dynamicContext == null ? processInstance : dynamicContext);
            workItemNodeInstance.addEventListeners();
            executeWorkItem((StatefulKnowledgeSessionImpl) ksession,
                            workItem,
                            workItemNodeInstance);
        } else if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            ExecutableRunner runner = ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
            runner.execute(new ExecutableCommand<Void>() {
                private static final long serialVersionUID = 5L;

                public Void execute(Context context) {
                    StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) ((RegistryContext) context).lookup(KieSession.class);
                    WorkflowProcessInstance realProcessInstance = (WorkflowProcessInstance) ksession.getProcessInstance(processInstance.getId());
                    workItemNodeInstance.setProcessInstance(realProcessInstance);
                    if (dynamicContext == null) {
                        workItemNodeInstance.setNodeInstanceContainer(realProcessInstance);
                    } else {
                        DynamicNodeInstance realDynamicContext = findDynamicContext(realProcessInstance,
                                                                                    dynamicContext.getUniqueId());
                        workItemNodeInstance.setNodeInstanceContainer(realDynamicContext);
                    }
                    workItemNodeInstance.addEventListeners();
                    executeWorkItem((StatefulKnowledgeSessionImpl) ksession,
                                    workItem,
                                    workItemNodeInstance);
                    return null;
                }
            });
        } else {
            throw new IllegalArgumentException("Unsupported ksession: " + ksession == null ? "null" : ksession.getClass().getName());
        }
    }

    private static void executeWorkItem(StatefulKnowledgeSessionImpl ksession,
                                        WorkItemImpl workItem,
                                        WorkItemNodeInstance workItemNodeInstance) {
        ProcessEventSupport eventSupport = ((InternalProcessRuntime)
                ksession.getProcessRuntime()).getProcessEventSupport();
        eventSupport.fireBeforeNodeTriggered(workItemNodeInstance,
                                             ksession);
        ((WorkItemManager) ksession.getWorkItemManager()).internalExecuteWorkItem(workItem);
        workItemNodeInstance.internalSetWorkItemId(workItem.getId());
        eventSupport.fireAfterNodeTriggered(workItemNodeInstance,
                                            ksession);
    }

    private static DynamicNodeInstance findDynamicContext(WorkflowProcessInstance processInstance,
                                                          String uniqueId) {
        for (NodeInstance nodeInstance : ((WorkflowProcessInstanceImpl) processInstance).getNodeInstances(true)) {
            if (uniqueId.equals(((NodeInstanceImpl) nodeInstance).getUniqueId())) {
                return (DynamicNodeInstance) nodeInstance;
            }
        }
        throw new IllegalArgumentException("Could not find node instance " + uniqueId);
    }

    public static long addDynamicSubProcess(
            final DynamicNodeInstance dynamicContext,
            KieRuntime ksession,
            final String processId,
            final Map<String, Object> parameters) {
        final WorkflowProcessInstance processInstance = dynamicContext.getProcessInstance();
        return internalAddDynamicSubProcess(processInstance,
                                            dynamicContext,
                                            ksession,
                                            processId,
                                            parameters);
    }

    public static long addDynamicSubProcess(
            final org.kie.api.runtime.process.ProcessInstance processInstance,
            KieRuntime ksession,
            final String processId,
            final Map<String, Object> parameters) {
        return internalAddDynamicSubProcess((WorkflowProcessInstance) processInstance,
                                            null,
                                            ksession,
                                            processId,
                                            parameters);
    }

    public static long internalAddDynamicSubProcess(
            final WorkflowProcessInstance processInstance,
            final DynamicNodeInstance dynamicContext,
            KieRuntime ksession,
            final String processId,
            final Map<String, Object> parameters) {
        final SubProcessNodeInstance subProcessNodeInstance = new SubProcessNodeInstance();
        subProcessNodeInstance.setNodeInstanceContainer(dynamicContext == null ? processInstance : dynamicContext);
        subProcessNodeInstance.setProcessInstance(processInstance);
        subProcessNodeInstance.setMetaData("NodeType",
                                           "SubProcessNode");
        if (ksession instanceof StatefulKnowledgeSessionImpl) {
            return executeSubProcess((StatefulKnowledgeSessionImpl) ksession,
                                     processId,
                                     parameters,
                                     processInstance,
                                     subProcessNodeInstance);
        } else if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            ExecutableRunner commandService = ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
            return commandService.execute(new ExecutableCommand<Long>() {
                private static final long serialVersionUID = 5L;

                public Long execute(Context context) {
                    StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) ((RegistryContext) context).lookup(KieSession.class);
                    WorkflowProcessInstance realProcessInstance = (WorkflowProcessInstance) ksession.getProcessInstance(processInstance.getId());
                    subProcessNodeInstance.setProcessInstance(realProcessInstance);
                    if (dynamicContext == null) {
                        subProcessNodeInstance.setNodeInstanceContainer(realProcessInstance);
                    } else {
                        DynamicNodeInstance realDynamicContext = findDynamicContext(realProcessInstance,
                                                                                    dynamicContext.getUniqueId());
                        subProcessNodeInstance.setNodeInstanceContainer(realDynamicContext);
                    }
                    return executeSubProcess((StatefulKnowledgeSessionImpl) ksession,
                                             processId,
                                             parameters,
                                             processInstance,
                                             subProcessNodeInstance);
                }
            });
        } else {
            throw new IllegalArgumentException("Unsupported ksession: " + ksession == null ? "null" : ksession.getClass().getName());
        }
    }

    private static long executeSubProcess(StatefulKnowledgeSessionImpl ksession,
                                          String processId,
                                          Map<String, Object> parameters,
                                          ProcessInstance processInstance,
                                          SubProcessNodeInstance subProcessNodeInstance) {
        Process process = ksession.getKieBase().getProcess(processId);
        if (process == null) {
            logger.error("Could not find process {}",
                         processId);
            throw new IllegalArgumentException("No process definition found with id: " + processId);
        } else {
            ProcessEventSupport eventSupport = ((InternalProcessRuntime)
                    ((InternalKnowledgeRuntime) ksession).getProcessRuntime()).getProcessEventSupport();
            eventSupport.fireBeforeNodeTriggered(subProcessNodeInstance,
                                                 ksession);

            ProcessInstance subProcessInstance = null;
            if (((WorkflowProcessInstanceImpl) processInstance).getCorrelationKey() != null) {
                List<String> businessKeys = new ArrayList<String>();
                businessKeys.add(((WorkflowProcessInstanceImpl) processInstance).getCorrelationKey());
                businessKeys.add(processId);
                businessKeys.add(String.valueOf(System.currentTimeMillis()));
                CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
                CorrelationKey subProcessCorrelationKey = correlationKeyFactory.newCorrelationKey(businessKeys);
                subProcessInstance = (ProcessInstance) ((CorrelationAwareProcessRuntime) ksession).createProcessInstance(processId,
                                                                                                                         subProcessCorrelationKey,
                                                                                                                         parameters);
            } else {
                subProcessInstance = (ProcessInstance) ksession.createProcessInstance(processId,
                                                                                      parameters);
            }

            ((ProcessInstanceImpl) subProcessInstance).setMetaData("ParentProcessInstanceId",
                                                                   processInstance.getId());
            ((ProcessInstanceImpl) subProcessInstance).setParentProcessInstanceId(processInstance.getId());

            subProcessInstance = (ProcessInstance) ksession.startProcessInstance(subProcessInstance.getId());
            subProcessNodeInstance.internalSetProcessInstanceId(subProcessInstance.getId());

            eventSupport.fireAfterNodeTriggered(subProcessNodeInstance,
                                                ksession);
            if (subProcessInstance.getState() == ProcessInstance.STATE_COMPLETED) {
                subProcessNodeInstance.triggerCompleted();
            } else {

                subProcessNodeInstance.addEventListeners();
            }

            return subProcessInstance.getId();
        }
    }
}
