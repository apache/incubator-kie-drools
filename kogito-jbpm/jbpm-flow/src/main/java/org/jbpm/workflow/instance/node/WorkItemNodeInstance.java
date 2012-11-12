/**
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.instance.node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

import org.drools.WorkItemHandlerNotFoundException;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.DataType;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.spi.ProcessContext;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.AssignmentAction;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.jbpm.workflow.instance.impl.WorkItemResolverFactory;
import org.kie.definition.process.Node;
import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.process.EventListener;
import org.mvel2.MVEL;

/**
 * Runtime counterpart of a work item node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class WorkItemNodeInstance extends StateBasedNodeInstance implements EventListener {
    
    private static final long serialVersionUID = 510l;
    
    private long workItemId = -1;
    private transient WorkItem workItem;
    
    protected WorkItemNode getWorkItemNode() {
        return (WorkItemNode) getNode();
    }
    
    public WorkItem getWorkItem() {
        if (workItem == null && workItemId >= 0) {
            workItem = ((WorkItemManager) ((ProcessInstance) getProcessInstance())
                .getKnowledgeRuntime().getWorkItemManager()).getWorkItem(workItemId);
        }
        return workItem;
    }
    
    public long getWorkItemId() {
        return workItemId;
    }
    
    public void internalSetWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }
    
    public void internalSetWorkItem(WorkItem workItem) {
        this.workItem = workItem;
    }
    
    public boolean isInversionOfControl() {
        // TODO
        return false;
    }

    public void internalTrigger(final NodeInstance from, String type) {
        super.internalTrigger(from, type);
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
        if (isInversionOfControl()) {
            ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime()
                .update(((ProcessInstance) getProcessInstance()).getKnowledgeRuntime().getFactHandle(this), this);
        } else {
            try {
                ((WorkItemManager) ((ProcessInstance) getProcessInstance())
                    .getKnowledgeRuntime().getWorkItemManager()).internalExecuteWorkItem(
                        (org.drools.process.instance.WorkItem) workItem);
            } catch (WorkItemHandlerNotFoundException wihnfe){
                getProcessInstance().setState( ProcessInstance.STATE_ABORTED );
                throw wihnfe;
            }
        }
        if (!workItemNode.isWaitForCompletion()) {
            triggerCompleted();
        }
        this.workItemId = workItem.getId();
    }    

    protected WorkItem createWorkItem(WorkItemNode workItemNode) {
        Work work = workItemNode.getWork();
        workItem = new WorkItemImpl();
        ((WorkItem) workItem).setName(work.getName());
        ((WorkItem) workItem).setProcessInstanceId(getProcessInstance().getId());
        ((WorkItem) workItem).setParameters(new HashMap<String, Object>(work.getParameters()));
        for (Iterator<DataAssociation> iterator = workItemNode.getInAssociations().iterator(); iterator.hasNext(); ) {
            DataAssociation association = iterator.next();
            if (association.getAssignments() == null || association.getAssignments().isEmpty()) {
                Object parameterValue = null;
                VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getSources().get(0));
                if (variableScopeInstance != null) {
                    parameterValue = variableScopeInstance.getVariable(association.getSources().get(0));
                } else {
                    try {
                        parameterValue = MVEL.eval(association.getSources().get(0), new NodeInstanceResolverFactory(this));
                    } catch (Throwable t) {
                        System.err.println("Could not find variable scope for variable " + association.getSources().get(0));
                        System.err.println("when trying to execute Work Item " + work.getName());
                        System.err.println("Continuing without setting parameter.");
                    }
                }
                if (parameterValue != null) {
                    ((WorkItem) workItem).setParameter(association.getTarget(), parameterValue);
                }
            } else {
                for(Iterator<Assignment> it = association.getAssignments().iterator(); it.hasNext(); ) {
                    handleAssignment(it.next());
                }
            }
        }
        
        for (Map.Entry<String, Object> entry: workItem.getParameters().entrySet()) {
            if (entry.getValue() instanceof String) {
                String s = (String) entry.getValue();
                Map<String, String> replacements = new HashMap<String, String>();
                Matcher matcher = PARAMETER_MATCHER.matcher(s);
                while (matcher.find()) {
                    String paramName = matcher.group(1);
                    if (replacements.get(paramName) == null) {
                        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                            resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName);
                        if (variableScopeInstance != null) {
                            Object variableValue = variableScopeInstance.getVariable(paramName);
                            String variableValueString = variableValue == null ? "" : variableValue.toString(); 
                            replacements.put(paramName, variableValueString);
                        } else {
                            try {
                                Object variableValue = MVEL.eval(paramName, new NodeInstanceResolverFactory(this));
                                String variableValueString = variableValue == null ? "" : variableValue.toString();
                                replacements.put(paramName, variableValueString);
                            } catch (Throwable t) {
                                System.err.println("Could not find variable scope for variable " + paramName);
                                System.err.println("when trying to replace variable in string for Work Item " + work.getName());
                                System.err.println("Continuing without setting parameter.");
                            }
                        }
                    }
                }
                for (Map.Entry<String, String> replacement: replacements.entrySet()) {
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
        if (workItemNode != null) {
            for (Iterator<DataAssociation> iterator = getWorkItemNode().getOutAssociations().iterator(); iterator.hasNext(); ) {
                DataAssociation association = iterator.next();
                if (association.getAssignments() == null || association.getAssignments().isEmpty()) {
                    VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                    resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getTarget());
                    if (variableScopeInstance != null) {
                        Object value = workItem.getResult(association.getSources().get(0));
                        if (value == null) {
                            try {
                                value = MVEL.eval(association.getSources().get(0), new WorkItemResolverFactory(workItem));
                            } catch (Throwable t) {
                                // do nothing
                            }
                        }
                        Variable varDef = variableScopeInstance.getVariableScope().findVariable(association.getTarget());
                        DataType dataType = varDef.getType();
                        // exclude java.lang.Object as it is considered unknown type
                        if (!dataType.getStringType().endsWith("java.lang.Object") && value instanceof String) {
                            value = dataType.readValue((String) value);
                        }
                        variableScopeInstance.setVariable(association.getTarget(), value);
                    } else {
                        System.out.println("Could not find variable scope for variable " + association.getTarget());
                        System.out.println("when trying to complete Work Item " + workItem.getName());
                        System.out.println("Continuing without setting variable.");
                    }

                } else {
                    try {
                        for (Iterator<Assignment> it = association.getAssignments().iterator(); it.hasNext(); ) {
                            handleAssignment(it.next());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }                
            }
        }
        if (isInversionOfControl()) {
            KnowledgeRuntime kruntime = ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime();
            kruntime.update(kruntime.getFactHandle(this), this);
        } else {
            triggerCompleted();
        }
    }
  
    public void cancel() {
        WorkItem workItem = getWorkItem();
        if (workItem != null &&
                workItem.getState() != WorkItem.COMPLETED && 
                workItem.getState() != WorkItem.ABORTED) {
            try {
                ((WorkItemManager) ((ProcessInstance) getProcessInstance())
                    .getKnowledgeRuntime().getWorkItemManager()).internalAbortWorkItem(workItemId);
            } catch (WorkItemHandlerNotFoundException wihnfe){
                getProcessInstance().setState( ProcessInstance.STATE_ABORTED );
                throw wihnfe;
            }
        }
        super.cancel();
    }
    
    public void addEventListeners() {
        super.addEventListeners();
        addWorkItemListener();
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
    
    public void signalEvent(String type, Object event) {
        if ("workItemCompleted".equals(type)) {
            workItemCompleted((WorkItem) event);
        } else if ("workItemAborted".equals(type)) {
            workItemAborted((WorkItem) event);
        } else {
            super.signalEvent(type, event);
        }
    }

    public String[] getEventTypes() {
        return new String[] { "workItemCompleted" };
    }
    
    public void workItemAborted(WorkItem workItem) {
        if ( workItemId == workItem.getId()
                || ( workItemId == -1 && getWorkItem().getId() == workItem.getId()) ) {
            removeEventListeners();
            triggerCompleted(workItem);
        }
    }

    public void workItemCompleted(WorkItem workItem) {
        if ( workItemId == workItem.getId()
                || ( workItemId == -1 && getWorkItem().getId() == workItem.getId()) ) {
            removeEventListeners();
            triggerCompleted(workItem);
        }
    }
    
    public String getNodeName() {
        Node node = getNode();
        if (node == null) {
            String nodeName =  "[Dynamic]";
            WorkItem workItem = getWorkItem();
            if (workItem != null) {
                nodeName += " " + workItem.getParameter("TaskName");
            }
            return nodeName;
        }
        return super.getNodeName();
    }
    
}
