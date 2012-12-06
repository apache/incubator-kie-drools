/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.integration.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.CompositeNodeInstance;
import org.kie.KnowledgeBase;
import org.kie.command.Context;
import org.kie.definition.KnowledgePackage;
import org.kie.definition.process.Process;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.NodeInstance;
import org.kie.runtime.process.ProcessInstance;

/**
 * This class encapsulates the logic for executing operations via the Drools/jBPM api and retrieving information 
 * from that api. 
 */
public class CommandDelegate {

    /**
     * The methods in this class are purposefully static and no instance of this class should be initialized.
     */
    private CommandDelegate() {
    }
    
    /**
     * This method retrieves the stateful knowledge session associated with the console.
     * @return a (stateful knowledge) session.
     */
    private static StatefulKnowledgeSession getSession() { 
        return StatefulKnowledgeSessionUtil.getStatefulKnowledgeSession();
    }
    
    public static List<Process> getProcesses() {
        List<Process> result = new ArrayList<Process>();
        StatefulKnowledgeSessionUtil.getKnowledgeBaseManager().syncPackages();
        KnowledgeBase kbase = getSession().getKnowledgeBase();
        for (KnowledgePackage kpackage: kbase.getKnowledgePackages()) {
            result.addAll(kpackage.getProcesses());
        }
        return result;
    }
    
    public static Process getProcess(String processId) {
        KnowledgeBase kbase = getSession().getKnowledgeBase();
        for (KnowledgePackage kpackage: kbase.getKnowledgePackages()) {
            for (Process process: kpackage.getProcesses()) {
                if (processId.equals(process.getId())) {
                    return process;
                }
            }
        }
        return null;
    }
    
    public static Process getProcessByName(String name) {
        KnowledgeBase kbase = getSession().getKnowledgeBase();
        for (KnowledgePackage kpackage: kbase.getKnowledgePackages()) {
            for (Process process: kpackage.getProcesses()) {
                if (name.equals(process.getName())) {
                    return process;
                }
            }
        }
        return null;
    }

    /**
     * This method is not supported by jBPM and will throw a {@link UnsupportedOperationException}.
     * @param processId
     */
    public static void removeProcess(String processId) {
        throw new UnsupportedOperationException();
    }
    
    public static ProcessInstanceLog getProcessInstanceLog(String processInstanceId) {
        return JPAProcessInstanceDbLog.findProcessInstance(new Long(processInstanceId));
    }

    public static List<ProcessInstanceLog> getProcessInstanceLogsByProcessId(String processId) {
        return JPAProcessInstanceDbLog.findProcessInstances(processId);
    }
    
    public static List<ProcessInstanceLog> getActiveProcessInstanceLogsByProcessId(String processId) {
        return JPAProcessInstanceDbLog.findActiveProcessInstances(processId);
    }
    
    public static ProcessInstanceLog startProcess(String processId, Map<String, Object> parameters) {
        long processInstanceId = getSession().startProcess(processId, parameters).getId();
        return JPAProcessInstanceDbLog.findProcessInstance(processInstanceId);
    }
    
    public static void abortProcessInstance(String processInstanceIdString) {
        StatefulKnowledgeSession session = getSession();
        Long processInstanceId = new Long(processInstanceIdString);
        if ( session.getProcessInstance(processInstanceId) != null) {
            session.abortProcessInstance(processInstanceId);
        } else {
            throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
        }
        
    }
    
    /**
     * This returns the variables associated with the process instance. This is a "read-only" function: modifying
     * the values of the map will not have any effect on the actual variables associated with the process instance. 
     * @param processInstanceId
     * @return
     */
    public static Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
        ProcessInstance processInstance = getSession().getProcessInstance(new Long(processInstanceId));
        if (processInstance != null) {
            Map<String, Object> variables = ((WorkflowProcessInstanceImpl) processInstance).getVariables();
            if (variables == null) {
                return new HashMap<String, Object>();
            }
            // filter out null values
            Map<String, Object> result = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry: variables.entrySet()) {
                if (entry.getValue() != null) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
            return result;
        } else {
            throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
        }
    }
    
    /**
     * This method adds the variables provided in the map, to the (process) instance.
     * NOTE: the variables given will be <i>added</i> to the existing map of variables. 
     * They will <i>not</i> replace the variables that are already associated with the proces instance. 
     * @param processInstanceId The id of the process instance. 
     * @param variables The variables to add.
     */
    @SuppressWarnings("serial")
    public static void setProcessInstanceVariables(final String processInstanceId, final Map<String, Object> variables) {

        GenericCommand<Void> setProcInstVariablesCommand = new GenericCommand<Void>() {
            public Void execute(Context context) {
                StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
                ProcessInstance processInstance = ksession.getProcessInstance(new Long(processInstanceId));
                if (processInstance != null) {
                    VariableScopeInstance variableScope = (VariableScopeInstance) 
                        ((org.jbpm.process.instance.ProcessInstance) processInstance)
                            .getContextInstance(VariableScope.VARIABLE_SCOPE);
                    if (variableScope == null) {
                        throw new IllegalArgumentException(
                            "Could not find variable scope for process instance " + processInstanceId);
                    }
                    for (Map.Entry<String, Object> entry: variables.entrySet()) {
                        variableScope.setVariable(entry.getKey(), entry.getValue());
                    }
                } else {
                    throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
                }
                return null;
            }
        };
        
        /**
         * We execute the above code as a command for a couple of reasons, mostly because it's the easiest way.  
         * One of the positive side effects is that the command will then happen
         * 1. within a transaction and 
         * 2. use the persistence logic of the SingleSessionCommandService. 
         */
        ((CommandBasedStatefulKnowledgeSession) getSession())
            .getCommandService()
            .execute(setProcInstVariablesCommand);
    }   
    
    public static void signalExecution(String executionId, String signalRef,  String signal) {
        
        getSession().signalEvent(signalRef, signal, Long.parseLong(executionId));
    }
    
    public static Collection<NodeInstance> getActiveNodeInstances(long processInstanceId) {
        
        ProcessInstance processInstance = getSession().getProcessInstance(processInstanceId);
        
        if (processInstance != null){
            ((ProcessInstanceImpl)processInstance).setProcess(getSession().getKnowledgeBase().getProcess(processInstance.getProcessId()));
            Collection<NodeInstance> activeNodes = ((WorkflowProcessInstance)processInstance).getNodeInstances();
            
            activeNodes.addAll(collectActiveNodeInstances(activeNodes));
            
            return activeNodes;
        }
        
        return null;
    }
    
    protected static Collection<NodeInstance> collectActiveNodeInstances(Collection<NodeInstance> activeNodes) {
        Collection<NodeInstance> activeNodesComposite = new ArrayList<NodeInstance>();
        for (NodeInstance nodeInstance : activeNodes) {
            if (nodeInstance instanceof CompositeNodeInstance) {
                Collection<NodeInstance> currentNodeInstances = ((CompositeNodeInstance) nodeInstance).getNodeInstances();
                activeNodesComposite.addAll(currentNodeInstances);
                
                // recursively check current nodes
                activeNodesComposite.addAll(collectActiveNodeInstances(currentNodeInstances));
            }
        }
        
        return activeNodesComposite;
    }

}
