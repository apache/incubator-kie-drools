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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.spi.ProcessContext;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.ContextInstanceFactory;
import org.jbpm.process.instance.impl.ContextInstanceFactoryRegistry;
import org.jbpm.process.instance.impl.util.VariableUtil;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.SubProcessFactory;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.Transformation;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.jbpm.workflow.instance.impl.VariableScopeResolverFactory;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.DataTransformer;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime counterpart of a SubFlow node.
 * 
 */
public class LambdaSubProcessNodeInstance extends StateBasedNodeInstance implements EventListener, ContextInstanceContainer {

    private static final long serialVersionUID = 510l;
    private static final Logger logger = LoggerFactory.getLogger(LambdaSubProcessNodeInstance.class);

    // NOTE: ContetxInstances are not persisted as current functionality (exception scope) does not require it
    private Map<String, ContextInstance> contextInstances = new HashMap<String, ContextInstance>();
    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<String, List<ContextInstance>>();

    private long processInstanceId;

    protected SubProcessNode getSubProcessNode() {
        return (SubProcessNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
    	super.internalTrigger(from, type);
    	// if node instance was cancelled, abort
		if (getNodeInstanceContainer().getNodeInstance(getId()) == null) {
			return;
		}
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A SubProcess node only accepts default incoming connections!");
        }

        String processId = getSubProcessNode().getProcessId();

        ProcessContext context = new ProcessContext(getProcessInstance().getKnowledgeRuntime());
        context.setNodeInstance(this);
        SubProcessFactory subProcessFactory = getSubProcessNode().getSubProcessFactory();
        Object o = subProcessFactory.bind(context);
        org.kie.kogito.process.ProcessInstance<?> processInstance = subProcessFactory.createInstance(o);
        

//            KieRuntime kruntime = ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime();
//            if (getSubProcessNode().getMetaData("MICollectionInput") != null) {
//                // remove foreach input variable to avoid problems when running in variable strict mode
//                parameters.remove(getSubProcessNode().getMetaData("MICollectionInput"));
//            }

            if (((WorkflowProcessInstanceImpl)getProcessInstance()).getCorrelationKey() != null) {
                // in case there is correlation key on parent instance pass it along to child so it can be easily correlated
                // since correlation key must be unique for active instances it appends processId and timestamp
                List<String> businessKeys = new ArrayList<String>();
                businessKeys.add(((WorkflowProcessInstanceImpl)getProcessInstance()).getCorrelationKey());
                businessKeys.add(processId);
                businessKeys.add(String.valueOf(System.currentTimeMillis()));
                CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
                CorrelationKey subProcessCorrelationKey = correlationKeyFactory.newCorrelationKey(businessKeys);
                throw new UnsupportedOperationException("correlation key is unsupported");
//                processInstance = (ProcessInstance) ((CorrelationAwareProcessRuntime)kruntime).createProcessInstance(processId, subProcessCorrelationKey, parameters);
            } else {
//                processInstance = ( ProcessInstance ) kruntime.createProcessInstance(processId, parameters);
            }
//	    	this.processInstanceId = processInstance.getId();
//	    	((ProcessInstanceImpl) processInstance).setMetaData("ParentProcessInstanceId", getProcessInstance().getId());
//	    	((ProcessInstanceImpl) processInstance).setMetaData("ParentNodeInstanceId", getUniqueId());
//	    	((ProcessInstanceImpl) processInstance).setMetaData("ParentNodeId", getSubProcessNode().getUniqueId());
//	    	((ProcessInstanceImpl) processInstance).setParentProcessInstanceId(getProcessInstance().getId());
//	    	((ProcessInstanceImpl) processInstance).setSignalCompletion(getSubProcessNode().isWaitForCompletion());
//
//	    	kruntime.startProcessInstance(processInstance.getId());
//	    	if (!getSubProcessNode().isWaitForCompletion()) {
//	    		triggerCompleted();
//	    	} else if (processInstance.getState() == ProcessInstance.STATE_COMPLETED
//	    	        || processInstance.getState() == ProcessInstance.STATE_ABORTED) {
//	    	    processInstanceCompleted(processInstance);
//	    	} else {
//	    		addProcessListener();
//	    	}


        processInstance.start();
        
        subProcessFactory.unbind(context, processInstance.variables());
        
        if (!getSubProcessNode().isWaitForCompletion()) {
            triggerCompleted();
        } else if (processInstance.status() == ProcessInstance.STATE_COMPLETED || processInstance.status() == ProcessInstance.STATE_ABORTED) {
            triggerCompleted();
        } else {
            addProcessListener();
        }
    }

    private Map<String, Object> prepareParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        for (Iterator<DataAssociation> iterator =  getSubProcessNode().getInAssociations().iterator(); iterator.hasNext(); ) {
        	DataAssociation mapping = iterator.next();
        	Object parameterValue = null;
        	if (mapping.getTransformation() != null) {
            	Transformation transformation = mapping.getTransformation();
            	DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
            	if (transformer != null) {
            		parameterValue = transformer.transform(transformation.getCompiledExpression(), getSourceParameters(mapping));

            	}
            } else {

	            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
	                resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getSources().get(0));
	            if (variableScopeInstance != null) {
	                parameterValue = variableScopeInstance.getVariable(mapping.getSources().get(0));
	            } else {
	            	try {
	            		parameterValue = MVELSafeHelper.getEvaluator().eval(mapping.getSources().get(0), new NodeInstanceResolverFactory(this));
	            	} catch (Throwable t) {
	            	    parameterValue = VariableUtil.resolveVariable(mapping.getSources().get(0), this);
	                    if (parameterValue != null) {
	                        parameters.put(mapping.getTarget(), parameterValue);
	                    } else {
    	            	    logger.error("Could not find variable scope for variable {}", mapping.getSources().get(0));
    	            	    logger.error("when trying to execute SubProcess node {}", getSubProcessNode().getName());
    	            	    logger.error("Continuing without setting parameter.");
	                    }
	            	}
	            }
            }
            if (parameterValue != null) {
            	parameters.put(mapping.getTarget(),parameterValue);
            }
        }
        return parameters;
    }

    private Process getProcess(String processId) {
        return null;
    }

    public void cancel() {
        super.cancel();
        if (getSubProcessNode() == null || !getSubProcessNode().isIndependent()) {
        	ProcessInstance processInstance = null;
        	InternalKnowledgeRuntime kruntime = ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime();

    		processInstance = (ProcessInstance) kruntime.getProcessInstance(processInstanceId);


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

    @Override
	public void signalEvent(String type, Object event) {
		if (("processInstanceCompleted:" + processInstanceId).equals(type)) {
			processInstanceCompleted((ProcessInstance) event);
		} else {
			super.signalEvent(type, event);
		}
	}

    @Override
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

                exceptionScopeInstance.handleException(faultName, processInstance.getFaultData());
                if (getSubProcessNode() != null && !getSubProcessNode().isIndependent() && getSubProcessNode().isAbortParent()){
                    cancel();
                }
                return;
            } else if (getSubProcessNode() != null && !getSubProcessNode().isIndependent() && getSubProcessNode().isAbortParent()){
                ((ProcessInstance) getProcessInstance()).setState(ProcessInstance.STATE_ABORTED, faultName);
                return;
            }

        }
        // handle dynamic subprocess
        if (getNode() == null) {
            setMetaData("NodeType", "SubProcessNode");
        }
        // if there were no exception proceed normally
        triggerCompleted();

    }

    private void handleOutMappings(ProcessInstance processInstance) {
        VariableScopeInstance subProcessVariableScopeInstance = (VariableScopeInstance)
	        processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
        SubProcessNode subProcessNode = getSubProcessNode();
        if (subProcessNode != null) {
		    for (Iterator<DataAssociation> iterator= subProcessNode.getOutAssociations().iterator(); iterator.hasNext(); ) {
		    	DataAssociation mapping = iterator.next();
		    	if (mapping.getTransformation() != null) {
                	Transformation transformation = mapping.getTransformation();
                	DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
                	if (transformer != null) {
                		Object parameterValue = transformer.transform(transformation.getCompiledExpression(), subProcessVariableScopeInstance.getVariables());
                		VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                        resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getTarget());
                        if (variableScopeInstance != null && parameterValue != null) {

                            variableScopeInstance.setVariable(mapping.getTarget(), parameterValue);
                        } else {
                            logger.warn("Could not find variable scope for variable {}", mapping.getTarget());
                            logger.warn("Continuing without setting variable.");
                        }
                	}
                } else {
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
        } else {
            // handle dynamic sub processes without data output mapping            
            mapDynamicOutputData(subProcessVariableScopeInstance.getVariables());
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

    protected Map<String, Object> getSourceParameters(DataAssociation association) {
    	Map<String, Object> parameters = new HashMap<String, Object>();
    	for (String sourceParam : association.getSources()) {
	    	Object parameterValue = null;
	        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
	        resolveContextInstance(VariableScope.VARIABLE_SCOPE, sourceParam);
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

}
