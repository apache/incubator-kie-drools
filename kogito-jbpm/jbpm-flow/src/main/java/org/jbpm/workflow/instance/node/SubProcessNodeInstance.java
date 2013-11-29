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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.drools.core.RuntimeDroolsException;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.StartProcessHelper;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.ContextInstanceFactory;
import org.jbpm.process.instance.impl.ContextInstanceFactoryRegistry;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.jbpm.workflow.instance.impl.VariableScopeResolverFactory;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.runtime.KnowledgeRuntime;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime counterpart of a SubFlow node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class SubProcessNodeInstance extends StateBasedNodeInstance implements EventListener, ContextInstanceContainer {

    private static final long serialVersionUID = 510l;
    private static final Logger logger = LoggerFactory.getLogger(SubProcessNodeInstance.class);
    
    // NOTE: ContetxInstances are not persisted as current functionality (exception scope) does not require it
    private Map<String, ContextInstance> contextInstances = new HashMap<String, ContextInstance>();
    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<String, List<ContextInstance>>();
    
    private long processInstanceId;
	
    protected SubProcessNode getSubProcessNode() {
        return (SubProcessNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
    	super.internalTrigger(from, type);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A SubProcess node only accepts default incoming connections!");
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        for (Iterator<DataAssociation> iterator =  getSubProcessNode().getInAssociations().iterator(); iterator.hasNext(); ) {
        	DataAssociation mapping = iterator.next();
        	Object parameterValue = null;
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getSources().get(0));
            if (variableScopeInstance != null) {
                parameterValue = variableScopeInstance.getVariable(mapping.getSources().get(0));
            } else {
            	try {
            		parameterValue = MVELSafeHelper.getEvaluator().eval(mapping.getSources().get(0), new NodeInstanceResolverFactory(this));
            	} catch (Throwable t) {
            	    logger.error("Could not find variable scope for variable {}", mapping.getSources().get(0));
            	    logger.error("when trying to execute SubProcess node {}", getSubProcessNode().getName());
            	    logger.error("Continuing without setting parameter.");
            	}
            }
            if (parameterValue != null) {
            	parameters.put(mapping.getTarget(),parameterValue); 
            }
        }
        String processId = getSubProcessNode().getProcessId();
        if (processId == null) {
            // if process id is not given try with process name
            processId = getSubProcessNode().getProcessName();
        }
        // resolve processId if necessary
        Map<String, String> replacements = new HashMap<String, String>();
		Matcher matcher = PARAMETER_MATCHER.matcher(processId);
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
                		Object variableValue = MVELSafeHelper.getEvaluator().eval(paramName, new NodeInstanceResolverFactory(this));
	                	String variableValueString = variableValue == null ? "" : variableValue.toString();
	                	replacements.put(paramName, variableValueString);
                	} catch (Throwable t) {
                	    logger.error("Could not find variable scope for variable {}", paramName);
                	    logger.error("when trying to replace variable in processId for sub process {}", getNodeName());
                	    logger.error("Continuing without setting process id.");
                	}
                }
        	}
        }
        for (Map.Entry<String, String> replacement: replacements.entrySet()) {
        	processId = processId.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }
        KieBase kbase = ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime().getKieBase();
        // start process instance
        Process process = kbase.getProcess(processId);
        
        if (process == null) {
            // try to find it by name
            String latestProcessId = StartProcessHelper.findLatestProcessByName(kbase, processId);
            if (latestProcessId != null) {
                processId = latestProcessId;
                process = kbase.getProcess(processId);
            
            }
        }
        
        if (process == null) {
            logger.error("Could not find process {}", processId);
            logger.error("Aborting process");
        	((ProcessInstance) getProcessInstance()).setState(ProcessInstance.STATE_ABORTED);
        	throw new RuntimeDroolsException("Could not find process " + processId);
        } else {
            KnowledgeRuntime kruntime = ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime();
            RuntimeManager manager = (RuntimeManager) kruntime.getEnvironment().get("RuntimeManager");
            if (manager != null) {
                RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
                kruntime = (KnowledgeRuntime) runtime.getKieSession();
            }
	    	ProcessInstance processInstance = ( ProcessInstance ) kruntime.createProcessInstance(processId, parameters);
	    	this.processInstanceId = processInstance.getId();
	    	((ProcessInstanceImpl) processInstance).setMetaData("ParentProcessInstanceId", getProcessInstance().getId());
	    	((ProcessInstanceImpl) processInstance).setParentProcessInstanceId(getProcessInstance().getId());
	    	kruntime.startProcessInstance(processInstance.getId());
	    	if (!getSubProcessNode().isWaitForCompletion()) {
	    		triggerCompleted();
	    	} else if (processInstance.getState() == ProcessInstance.STATE_COMPLETED
	    	        || processInstance.getState() == ProcessInstance.STATE_ABORTED) {
	    	    processInstanceCompleted(processInstance);
	    	} else {
	    		addProcessListener();
	    	}
        }
    }
    
    public void cancel() {
        super.cancel();
        if (getSubProcessNode() == null || !getSubProcessNode().isIndependent()) {
            ProcessInstance processInstance = (ProcessInstance)
                ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime()
                    .getProcessInstance(processInstanceId);
            if (processInstance != null) {
            	processInstance.setState(ProcessInstance.STATE_ABORTED);
            }
        }
    }
    
    public long getProcessInstanceId() {
    	return processInstanceId;
    }
    
    public void internalSetProcessInstanceId(long processInstanceId) {
    	this.processInstanceId = processInstanceId;
    }

    public void addEventListeners() {
        super.addEventListeners();
        addProcessListener();
    }
    
    private void addProcessListener() {
    	getProcessInstance().addEventListener("processInstanceCompleted:" + processInstanceId, this, true);
    }

    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("processInstanceCompleted:" + processInstanceId, this, true);
    }

	public void signalEvent(String type, Object event) {
		if (("processInstanceCompleted:" + processInstanceId).equals(type)) {
			processInstanceCompleted((ProcessInstance) event);
		} else {
			super.signalEvent(type, event);
		}
	}
    
    public String[] getEventTypes() {
    	return new String[] { "processInstanceCompleted:" + processInstanceId };
    }
    
    public void processInstanceCompleted(ProcessInstance processInstance) {
        removeEventListeners();
        handleOutMappings(processInstance);
        if (processInstance.getState() == ProcessInstance.STATE_ABORTED) {
            String faultName = processInstance.getOutcome()==null?"":processInstance.getOutcome();
            // handle exception as sub process failed with error code
            ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance) 
                    resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, faultName);
            if (exceptionScopeInstance != null) {
                
                exceptionScopeInstance.handleException(faultName, null);
                cancel();
                return;
            } else if (!getSubProcessNode().isIndependent()){
                ((ProcessInstance) getProcessInstance()).setState(ProcessInstance.STATE_ABORTED, faultName);
                return;
            }
            
        }      
        // if there were no exception proceed normally
        triggerCompleted();
        
    }
    
    private void handleOutMappings(ProcessInstance processInstance) {
        VariableScopeInstance subProcessVariableScopeInstance = (VariableScopeInstance)
	        processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
        SubProcessNode subProcessNode = getSubProcessNode();
        if (subProcessNode != null) {
		    for (Iterator<org.jbpm.workflow.core.node.DataAssociation> iterator= subProcessNode.getOutAssociations().iterator(); iterator.hasNext(); ) {
		    	org.jbpm.workflow.core.node.DataAssociation mapping = iterator.next();
		        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
		            resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getTarget());
		        if (variableScopeInstance != null) {
		        	Object value = subProcessVariableScopeInstance.getVariable(mapping.getSources().get(0));
		        	if (value == null) {
		        		try {
		            		value = MVELSafeHelper.getEvaluator().eval(mapping.getSources().get(0), new VariableScopeResolverFactory(subProcessVariableScopeInstance));
		            	} catch (Throwable t) {
		            		// do nothing
		            	}
		        	}
		            variableScopeInstance.setVariable(mapping.getTarget(), value);
		        } else {
		            logger.error("Could not find variable scope for variable {}", mapping.getTarget());
		            logger.error("when trying to complete SubProcess node {}", getSubProcessNode().getName());
		            logger.error("Continuing without setting variable.");
		        }
		    }
        }
    }
    
    public String getNodeName() {
    	Node node = getNode();
    	if (node == null) {
    		return "[Dynamic] Sub Process";
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
            for (ContextInstance contextInstance: contextInstances) {
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
        return getSubProcessNode();
    }

}
