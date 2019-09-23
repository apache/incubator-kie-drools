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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.drools.core.WorkItemHandlerNotFoundException;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.drools.core.spi.ProcessContext;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.AssignmentAction;
import org.jbpm.process.instance.impl.ContextInstanceFactory;
import org.jbpm.process.instance.impl.ContextInstanceFactoryRegistry;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.Transformation;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.jbpm.workflow.instance.impl.WorkItemResolverFactory;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.DataTransformer;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessWorkItemHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime counterpart of a work item node.
 * 
 */
public class WorkItemNodeInstance extends StateBasedNodeInstance implements EventListener, ContextInstanceContainer {

    private static final long serialVersionUID = 510l;
    private static final Logger logger = LoggerFactory.getLogger(WorkItemNodeInstance.class);

    private static boolean variableStrictEnabled = Boolean.parseBoolean(System.getProperty("org.jbpm.variable.strict", "false"));
    private static List<String> defaultOutputVariables = Arrays.asList(new String[]{"ActorId"});

    // NOTE: ContetxInstances are not persisted as current functionality (exception scope) does not require it
    private Map<String, ContextInstance> contextInstances = new HashMap<String, ContextInstance>();
    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<String, List<ContextInstance>>();

    private String workItemId;
    private transient WorkItem workItem;
    
    private String exceptionHandlingProcessInstanceId;

    protected WorkItemNode getWorkItemNode() {
        return (WorkItemNode) getNode();
    }

    public WorkItem getWorkItem() {
        if (workItem == null && workItemId != null) {
            workItem = ((WorkItemManager) ((ProcessInstance) getProcessInstance())
                                                                                  .getKnowledgeRuntime().getWorkItemManager()).getWorkItem(workItemId);
        }
        return workItem;
    }

    public String getWorkItemId() {
        return workItemId;
    }

    public void internalSetWorkItemId(String workItemId) {
        this.workItemId = workItemId;
    }

    public void internalSetWorkItem(WorkItem workItem) {
        this.workItem = workItem;
    }

    public boolean isInversionOfControl() {
        // TODO WorkItemNodeInstance.isInversionOfControl
        return false;
    }
    
    public void internalRegisterWorkItem() {
        ((WorkItemManager) ((ProcessInstance) getProcessInstance())
                .getKnowledgeRuntime().getWorkItemManager()).internalAddWorkItem(workItem);
    }

    @Override
    public void internalTrigger(final NodeInstance from, String type) {
        super.internalTrigger(from, type);
        // if node instance was cancelled, abort
        if (getNodeInstanceContainer().getNodeInstance(getId()) == null) {
            return;
        }
        // TODO this should be included for ruleflow only, not for BPEL
        //        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
        //            throw new IllegalArgumentException(
        //                "A WorkItemNode only accepts default incoming connections!");
        //        }
        WorkItemNode workItemNode = getWorkItemNode();
        createWorkItem(workItemNode);
        if (workItemNode.isWaitForCompletion()) {
            addWorkItemListener();
        }
        String deploymentId = (String) getProcessInstance().getKnowledgeRuntime().getEnvironment().get(EnvironmentName.DEPLOYMENT_ID);
        ((WorkItem) workItem).setDeploymentId(deploymentId);
        ((WorkItem) workItem).setNodeInstanceId(this.getId());
        ((WorkItem) workItem).setNodeId(getNodeId());
        if (isInversionOfControl()) {
            ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime()
                                                    .update(((ProcessInstance) getProcessInstance()).getKnowledgeRuntime().getFactHandle(this), this);
        } else {
            try {
                ((WorkItemManager) ((ProcessInstance) getProcessInstance())
                                                                           .getKnowledgeRuntime().getWorkItemManager()).internalExecuteWorkItem(
                                                                                                                                                (org.drools.core.process.instance.WorkItem) workItem);
            } catch (WorkItemHandlerNotFoundException wihnfe) {
                getProcessInstance().setState(ProcessInstance.STATE_ABORTED);
                throw wihnfe;
            } catch (ProcessWorkItemHandlerException handlerException) {
                this.workItemId = workItem.getId();
                handleWorkItemHandlerException(handlerException, workItem);
            } catch (Exception e) {
                String exceptionName = e.getClass().getName();
                ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance) resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName);
                if (exceptionScopeInstance == null) {
                    throw new WorkflowRuntimeException(this, getProcessInstance(), "Unable to execute Action: " + e.getMessage(), e);
                }
                // workItemId must be set otherwise cancel activity will not find the right work item
                this.workItemId = workItem.getId();
                exceptionScopeInstance.handleException(exceptionName, e);
            }
        }
        if (!workItemNode.isWaitForCompletion()) {
            triggerCompleted();
        }
        this.workItemId = workItem.getId();
    }
    
    protected WorkItem newWorkItem() {
        return new WorkItemImpl();
    }

    protected WorkItem createWorkItem(WorkItemNode workItemNode) {
        Work work = workItemNode.getWork();
        workItem = newWorkItem();
        ((WorkItem) workItem).setName(work.getName());
        ((WorkItem) workItem).setProcessInstanceId(getProcessInstance().getId());
        ((WorkItem) workItem).setParameters(new HashMap<String, Object>(work.getParameters()));
        ((WorkItem) workItem).setStartDate(new Date());
        // if there are any dynamic parameters add them
        if (dynamicParameters != null) {
            ((WorkItem) workItem).getParameters().putAll(dynamicParameters);
        }

        for (Iterator<DataAssociation> iterator = workItemNode.getInAssociations().iterator(); iterator.hasNext();) {
            DataAssociation association = iterator.next();
            if (association.getTransformation() != null) {
                Transformation transformation = association.getTransformation();
                DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
                if (transformer != null) {
                    Object parameterValue = transformer.transform(transformation.getCompiledExpression(), getSourceParameters(association));
                    if (parameterValue != null) {
                        ((WorkItem) workItem).setParameter(association.getTarget(), parameterValue);
                    }
                }
            } else if (association.getAssignments() == null || association.getAssignments().isEmpty()) {
                Object parameterValue = null;
                VariableScopeInstance variableScopeInstance = (VariableScopeInstance) resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getSources().get(0));
                if (variableScopeInstance != null) {
                    parameterValue = variableScopeInstance.getVariable(association.getSources().get(0));
                } else {
                    try {
                        parameterValue = MVELSafeHelper.getEvaluator().eval(association.getSources().get(0), new NodeInstanceResolverFactory(this));
                    } catch (Throwable t) {
                        logger.error("Could not find variable scope for variable {}", association.getSources().get(0));
                        logger.error("when trying to execute Work Item {}", work.getName());
                        logger.error("Continuing without setting parameter.");
                    }
                }
                if (parameterValue != null) {
                    ((WorkItem) workItem).setParameter(association.getTarget(), parameterValue);
                }
            } else {
                for (Iterator<Assignment> it = association.getAssignments().iterator(); it.hasNext();) {
                    handleAssignment(it.next());
                }
            }
        }

        for (Map.Entry<String, Object> entry : workItem.getParameters().entrySet()) {
            if (entry.getValue() instanceof String) {
                String s = (String) entry.getValue();
                Map<String, String> replacements = new HashMap<String, String>();
                Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(s);
                while (matcher.find()) {
                    String paramName = matcher.group(1);
                    if (replacements.get(paramName) == null) {
                        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName);
                        if (variableScopeInstance != null) {
                            Object variableValue = variableScopeInstance.getVariable(paramName);
                            String variableValueString = variableValue == null ? "" : variableValue.toString();
                            replacements.put(paramName, variableValueString);
                        } else {
                            try {
                                Object variableValue = MVELSafeHelper.getEvaluator().eval(paramName, new NodeInstanceResolverFactory(this));
                                String variableValueString = variableValue == null ? "" : variableValue.toString();
                                replacements.put(paramName, variableValueString);
                            } catch (Throwable t) {
                                logger.error("Could not find variable scope for variable {}", paramName);
                                logger.error("when trying to replace variable in string for Work Item {}", work.getName());
                                logger.error("Continuing without setting parameter.");
                            }
                        }
                    }
                }

                for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                    s = s.replace("#{" + replacement.getKey() + "}", replacement.getValue());
                }
                ((WorkItem) workItem).setParameter(entry.getKey(), s);

            }
        }
        return workItem;
    }

    private void handleAssignment(Assignment assignment) {
        AssignmentAction action = (AssignmentAction) assignment.getMetaData("Action");
        try {
            ProcessContext context = new ProcessContext(getProcessInstance().getKnowledgeRuntime());
            context.setNodeInstance(this);
            action.execute(getWorkItem(), context);
        } catch (Exception e) {
            throw new RuntimeException("unable to execute Assignment", e);
        }
    }

    public void triggerCompleted(WorkItem workItem) {
        this.workItem = workItem;
        WorkItemNode workItemNode = getWorkItemNode();

        if (workItemNode != null && workItem.getState() == WorkItem.COMPLETED) {
            validateWorkItemResultVariable(getProcessInstance().getProcessName(), workItemNode.getOutAssociations(), workItem);
            for (Iterator<DataAssociation> iterator = getWorkItemNode().getOutAssociations().iterator(); iterator.hasNext();) {
                DataAssociation association = iterator.next();
                if (association.getTransformation() != null) {
                    Transformation transformation = association.getTransformation();
                    DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
                    if (transformer != null) {
                        Object parameterValue = transformer.transform(transformation.getCompiledExpression(), workItem.getResults());
                        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getTarget());
                        if (variableScopeInstance != null && parameterValue != null) {

                            variableScopeInstance.getVariableScope().validateVariable(getProcessInstance().getProcessName(), association.getTarget(), parameterValue);

                            variableScopeInstance.setVariable(association.getTarget(), parameterValue);
                        } else {
                            logger.warn("Could not find variable scope for variable {}", association.getTarget());
                            logger.warn("when trying to complete Work Item {}", workItem.getName());
                            logger.warn("Continuing without setting variable.");
                        }
                        if (parameterValue != null) {
                            ((WorkItem) workItem).setParameter(association.getTarget(), parameterValue);
                        }
                    }
                } else if (association.getAssignments() == null || association.getAssignments().isEmpty()) {
                    VariableScopeInstance variableScopeInstance = (VariableScopeInstance) resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getTarget());
                    if (variableScopeInstance != null) {
                        Object value = workItem.getResult(association.getSources().get(0));
                        if (value == null) {
                            try {
                                value = MVELSafeHelper.getEvaluator().eval(association.getSources().get(0), new WorkItemResolverFactory(workItem));
                            } catch (Throwable t) {
                                // do nothing
                            }
                        }
                        Variable varDef = variableScopeInstance.getVariableScope().findVariable(association.getTarget());
                        DataType dataType = varDef.getType();
                        // exclude java.lang.Object as it is considered unknown type
                        if (!dataType.getStringType().endsWith("java.lang.Object") &&
                            !dataType.getStringType().endsWith("Object") && value instanceof String) {
                            value = dataType.readValue((String) value);
                        } else {
                            variableScopeInstance.getVariableScope().validateVariable(getProcessInstance().getProcessName(), association.getTarget(), value);
                        }
                        variableScopeInstance.setVariable(association.getTarget(), value);
                    } else {
                        logger.warn("Could not find variable scope for variable {}", association.getTarget());
                        logger.warn("when trying to complete Work Item {}", workItem.getName());
                        logger.warn("Continuing without setting variable.");
                    }

                } else {
                    try {
                        for (Iterator<Assignment> it = association.getAssignments().iterator(); it.hasNext();) {
                            handleAssignment(it.next());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        // handle dynamic nodes
        if (getNode() == null) {
            setMetaData("NodeType", workItem.getName());

            mapDynamicOutputData(workItem.getResults());
        }
        if (isInversionOfControl()) {
            KieRuntime kruntime = ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime();
            kruntime.update(kruntime.getFactHandle(this), this);
        } else {
            triggerCompleted();
        }
    }

    @Override
    public void cancel() {
        WorkItem workItem = getWorkItem();
        if (workItem != null &&
            workItem.getState() != WorkItem.COMPLETED &&
            workItem.getState() != WorkItem.ABORTED) {
            try {
                ((WorkItemManager) ((ProcessInstance) getProcessInstance())
                                                                           .getKnowledgeRuntime().getWorkItemManager()).internalAbortWorkItem(workItem.getId());
            } catch (WorkItemHandlerNotFoundException wihnfe) {
                getProcessInstance().setState(ProcessInstance.STATE_ABORTED);
                throw wihnfe;
            }
        }
        
        if (exceptionHandlingProcessInstanceId != null) {
            ProcessInstance processInstance = null;
            KieRuntime kruntime = getKieRuntimeForSubprocess();
        
            processInstance = (ProcessInstance) kruntime.getProcessInstance(exceptionHandlingProcessInstanceId);
            

            if (processInstance != null) {
                processInstance.setState(ProcessInstance.STATE_ABORTED);
            }
        }
        super.cancel();
    }

    public void addEventListeners() {
        super.addEventListeners();
        addWorkItemListener();
        addExceptionProcessListener();
    }

    private void addWorkItemListener() {
        getProcessInstance().addEventListener("workItemCompleted", this, false);
        getProcessInstance().addEventListener("workItemAborted", this, false);
    }

    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("workItemCompleted", this, false);
        getProcessInstance().removeEventListener("workItemAborted", this, false);
    }

    @Override
    public void signalEvent(String type, Object event) {
        if ("workItemCompleted".equals(type)) {
            workItemCompleted((WorkItem) event);
        } else if ("workItemAborted".equals(type)) {
            workItemAborted((WorkItem) event);
        } else if (("processInstanceCompleted:" + exceptionHandlingProcessInstanceId).equals(type)) {
            exceptionHandlingCompleted((ProcessInstance) event, null);
        } else if (type.equals("RuleFlow-Activate" + getProcessInstance().getProcessId() + "-" + getNode().getMetaData().get("UniqueId"))) {

            trigger(null, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        } else {
            super.signalEvent(type, event);
        }
    }

    public String[] getEventTypes() {
        if (exceptionHandlingProcessInstanceId != null) {
            return new String[] {"workItemCompleted", "processInstanceCompleted:" + exceptionHandlingProcessInstanceId };
        } else {
            return new String[]{"workItemCompleted"};
        }
    }

    public void workItemAborted(WorkItem workItem) {
        if (workItem.getId().equals(workItemId) || (workItemId == null && getWorkItem().getId().equals(workItem.getId()))) {
            removeEventListeners();
            triggerCompleted(workItem);
        }
    }

    public void workItemCompleted(WorkItem workItem) {
        if (workItem.getId().equals(workItemId) || (workItemId == null && getWorkItem().getId().equals(workItem.getId()))) {
            removeEventListeners();
            triggerCompleted(workItem);
        }
    }

    public String getNodeName() {
        Node node = getNode();
        if (node == null) {
            String nodeName = "[Dynamic]";
            WorkItem workItem = getWorkItem();
            if (workItem != null) {
                nodeName += " " + workItem.getParameter("TaskName");
            }
            return nodeName;
        }
        return super.getNodeName();
    }

    @Override
    public List<ContextInstance> getContextInstances(String contextId) {
        return this.subContextInstances.get(contextId);
    }

    @Override
    public void addContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = this.subContextInstances.get(contextId);
        if (list == null) {
            list = new ArrayList<ContextInstance>();
            this.subContextInstances.put(contextId, list);
        }
        list.add(contextInstance);
    }

    @Override
    public void removeContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = this.subContextInstances.get(contextId);
        if (list != null) {
            list.remove(contextInstance);
        }
    }

    @Override
    public ContextInstance getContextInstance(String contextId, long id) {
        List<ContextInstance> contextInstances = subContextInstances.get(contextId);
        if (contextInstances != null) {
            for (ContextInstance contextInstance : contextInstances) {
                if (contextInstance.getContextId() == id) {
                    return contextInstance;
                }
            }
        }
        return null;
    }

    @Override
    public ContextInstance getContextInstance(Context context) {
        ContextInstanceFactory conf = ContextInstanceFactoryRegistry.INSTANCE.getContextInstanceFactory(context);
        if (conf == null) {
            throw new IllegalArgumentException("Illegal context type (registry not found): " + context.getClass());
        }
        ContextInstance contextInstance = (ContextInstance) conf.getContextInstance(context, this, (ProcessInstance) getProcessInstance());
        if (contextInstance == null) {
            throw new IllegalArgumentException("Illegal context type (instance not found): " + context.getClass());
        }
        return contextInstance;
    }

    @Override
    public ContextContainer getContextContainer() {
        return getWorkItemNode();
    }

    protected Map<String, Object> getSourceParameters(DataAssociation association) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        for (String sourceParam : association.getSources()) {
            Object parameterValue = null;
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance) resolveContextInstance(VariableScope.VARIABLE_SCOPE, sourceParam);
            if (variableScopeInstance != null) {
                parameterValue = variableScopeInstance.getVariable(sourceParam);
            } else {
                try {
                    parameterValue = MVELSafeHelper.getEvaluator().eval(sourceParam, new NodeInstanceResolverFactory(this));
                } catch (Throwable t) {
                    logger.warn("Could not find variable scope for variable {}", sourceParam);
                }
            }
            if (parameterValue != null) {
                parameters.put(association.getTarget(), parameterValue);
            }
        }

        return parameters;
    }

    public void validateWorkItemResultVariable(String processName, List<DataAssociation> outputs, WorkItem workItem) {
        // in case work item results are skip validation as there is no notion of mandatory data outputs
        if (!variableStrictEnabled || workItem.getResults().isEmpty()) {
            return;
        }

        List<String> outputNames = new ArrayList<String>();
        for (DataAssociation association : outputs) {
            if (association.getSources() != null) {
                outputNames.add(association.getSources().get(0));
            }
            if (association.getAssignments() != null) {
                for (Iterator<Assignment> it = association.getAssignments().iterator(); it.hasNext();) {
                    outputNames.add(it.next().getFrom());
                }
            }
        }

        for (String outputName : workItem.getResults().keySet()) {
            if (!outputNames.contains(outputName) && !defaultOutputVariables.contains(outputName)) {
                throw new IllegalArgumentException("Data output '" + outputName + "' is not defined in process '" + processName + "' for task '" + workItem.getParameter("NodeName") + "'");
            }
        }
    }
    
    /*
     * Work item handler exception handling 
     */
    

    private void handleWorkItemHandlerException(ProcessWorkItemHandlerException handlerException, WorkItem workItem) {
        Map<String, Object> parameters = new HashMap<>();
        
        parameters.put("DeploymentId", workItem.getDeploymentId());
        parameters.put("ProcessInstanceId", workItem.getProcessInstanceId());
        parameters.put("WorkItemId", workItem.getId());
        parameters.put("NodeInstanceId", this.getId());
        parameters.put("ErrorMessage", handlerException.getMessage());        
        parameters.put("Error", handlerException);  
        
        // add all parameters of the work item to the newly started process instance
        parameters.putAll(workItem.getParameters());
        
        KieRuntime kruntime = getKieRuntimeForSubprocess();       
                
        ProcessInstance processInstance = ( ProcessInstance ) kruntime.createProcessInstance(handlerException.getProcessId(), parameters);
        
        this.exceptionHandlingProcessInstanceId = processInstance.getId(); 
        ((ProcessInstanceImpl) processInstance).setMetaData("ParentProcessInstanceId", getProcessInstance().getId());
        ((ProcessInstanceImpl) processInstance).setMetaData("ParentNodeInstanceId", getUniqueId());
        
        ((ProcessInstanceImpl) processInstance).setParentProcessInstanceId(getProcessInstance().getId());
        ((ProcessInstanceImpl) processInstance).setSignalCompletion(true);

        kruntime.startProcessInstance(processInstance.getId());
        if (processInstance.getState() == ProcessInstance.STATE_COMPLETED
                || processInstance.getState() == ProcessInstance.STATE_ABORTED) {
            exceptionHandlingCompleted(processInstance, handlerException);
        } else {
            addExceptionProcessListener();
        }
    }

    private void exceptionHandlingCompleted(ProcessInstance processInstance, ProcessWorkItemHandlerException handlerException) {
        
        if (handlerException == null) {
            handlerException = (ProcessWorkItemHandlerException) ((WorkflowProcessInstance)processInstance).getVariable("Error");
        }
                
        switch (handlerException.getStrategy()) {
            case ABORT:
                ((WorkItemManager) ((ProcessInstance) getProcessInstance())
                        .getKnowledgeRuntime().getWorkItemManager()).abortWorkItem(getWorkItem().getId());
                break;
            case RETHROW:
                String exceptionName = handlerException.getCause().getClass().getName();
                ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance) resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName);
                if (exceptionScopeInstance == null) {
                    throw new WorkflowRuntimeException(this, getProcessInstance(), "Unable to execute work item " + handlerException.getMessage(), handlerException.getCause());
                }
               
                exceptionScopeInstance.handleException(exceptionName, handlerException.getCause());
                break;
            case RETRY:
                Map<String, Object> parameters = new HashMap<>(getWorkItem().getParameters());
                
                parameters.putAll(((WorkflowProcessInstanceImpl)processInstance).getVariables());
                
                ((WorkItemManager) ((ProcessInstance) getProcessInstance())
                        .getKnowledgeRuntime().getWorkItemManager()).retryWorkItem(getWorkItem().getId(), parameters);
                break;
            case COMPLETE:
                
                ((WorkItemManager) ((ProcessInstance) getProcessInstance())
                        .getKnowledgeRuntime().getWorkItemManager()).completeWorkItem(getWorkItem().getId(), 
                                ((WorkflowProcessInstanceImpl)processInstance).getVariables());                
                break;
            default:
                break;
        }
        
    }

    public void addExceptionProcessListener() {
        if (exceptionHandlingProcessInstanceId != null) {
            getProcessInstance().addEventListener("processInstanceCompleted:" + exceptionHandlingProcessInstanceId, this, true);
        }
    }

    public void removeExceptionProcessListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("processInstanceCompleted:" + exceptionHandlingProcessInstanceId, this, true);
    }
    
    public String getExceptionHandlingProcessInstanceId() {
        return exceptionHandlingProcessInstanceId;
    }
    
    public void internalSetProcessInstanceId(String processInstanceId) {
        if (processInstanceId != null && !processInstanceId.isEmpty()) {
            this.exceptionHandlingProcessInstanceId = processInstanceId;
        }
    }
    
    protected KieRuntime getKieRuntimeForSubprocess() {
        KieRuntime kruntime = ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime();        
        
        return kruntime;
    }
    
    /*
     * mainly for test coverage to easily switch between settings 
     */
    public static void setVariableStrictOption(boolean turnedOn) {
        variableStrictEnabled = turnedOn;
    }
}
