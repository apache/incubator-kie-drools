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
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
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
import org.jbpm.process.instance.impl.ContextInstanceFactory;
import org.jbpm.process.instance.impl.ContextInstanceFactoryRegistry;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.Transformation;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.DataTransformer;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime counterpart of a ruleset node.
 * 
 */
public class RuleSetNodeInstance extends StateBasedNodeInstance implements EventListener, ContextInstanceContainer {

    private static final long serialVersionUID = 510l;
    private static final Logger logger = LoggerFactory.getLogger(RuleSetNodeInstance.class);
    
    private static final String ACT_AS_WAIT_STATE_PROPERTY = "org.jbpm.rule.task.waitstate";
    private static final String FIRE_RULE_LIMIT_PROPERTY = "org.jbpm.rule.task.firelimit";
    private static final String FIRE_RULE_LIMIT_PARAMETER = "FireRuleLimit";
    private static final int DEFAULT_FIRE_RULE_LIMIT = Integer.parseInt(System.getProperty(FIRE_RULE_LIMIT_PROPERTY, "10000"));
    
    private Map<String, FactHandle> factHandles = new HashMap<String, FactHandle>();
    private String ruleFlowGroup;

    // NOTE: ContetxInstances are not persisted as current functionality (exception scope) does not require it
    private Map<String, ContextInstance> contextInstances = new HashMap<String, ContextInstance>();
    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<String, List<ContextInstance>>();

    protected RuleSetNode getRuleSetNode() {
        return (RuleSetNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        try {
            super.internalTrigger(from, type);
            // if node instance was cancelled, abort
            if (getNodeInstanceContainer().getNodeInstance(getId()) == null) {
                return;
            }
            if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
                throw new IllegalArgumentException("A RuleSetNode only accepts default incoming connections!");
            }
            RuleSetNode ruleSetNode = getRuleSetNode();
            KieRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
            Map<String, Object> inputs = evaluateParameters(ruleSetNode);

            if (ruleSetNode.isDMN()) {
                String namespace = resolveVariable(ruleSetNode.getNamespace());
                String model = resolveVariable(ruleSetNode.getModel());
                String decision = resolveVariable(ruleSetNode.getDecision());

                DMNRuntime runtime = ((KieSession) kruntime).getKieRuntime(DMNRuntime.class);
                DMNModel dmnModel = runtime.getModel(namespace, model);
                if (dmnModel == null) {
                    // if was not found by name try to look it up by id
                    dmnModel = runtime.getModelById(namespace, model);
                }
                
                if (dmnModel == null) {
                    throw new IllegalArgumentException("DMN model '" + model + "' not found with namespace '" + namespace + "'");
                }
                DMNResult dmnResult = null;
                DMNContext context = runtime.newContext();

                for (Entry<String, Object> entry : inputs.entrySet()) {
                    context.set(entry.getKey(), entry.getValue());
                }

                if (decision != null && !decision.isEmpty()) {
                    dmnResult = runtime.evaluateByName(dmnModel, context, decision);
                } else {
                    dmnResult = runtime.evaluateAll(dmnModel, context);
                }

                if (dmnResult.hasErrors()) {
                    String errors = dmnResult.getMessages(Severity.ERROR).stream()
                            .map(message -> message.toString())
                            .collect(Collectors.joining(", "));

                    throw new RuntimeException("DMN result errors:: " + errors);
                }
                processOutputs(dmnResult.getContext().getAll());
                triggerCompleted();
            } else {

                // first set rule flow group
                setRuleFlowGroup(resolveRuleFlowGroup(ruleSetNode.getRuleFlowGroup()));

                //proceed
                for (Entry<String, Object> entry : inputs.entrySet()) {
                    if (FIRE_RULE_LIMIT_PARAMETER.equals(entry.getKey())) {
                        // don't put control parameter for fire limit into working memory
                        continue;
                    }
                    
                    String inputKey = getRuleFlowGroup() + "_" + getProcessInstance().getId() + "_" + entry.getKey();

                    factHandles.put(inputKey, kruntime.insert(entry.getValue()));
                }

                if (actAsWaitState()) {
                    addRuleSetListener();
                    ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda())
                            .activateRuleFlowGroup(getRuleFlowGroup(), getProcessInstance().getId(), getUniqueId());

                } else {
                    int fireLimit = DEFAULT_FIRE_RULE_LIMIT;
                    
                    if (inputs.containsKey(FIRE_RULE_LIMIT_PARAMETER)) {
                        fireLimit = Integer.parseInt(inputs.get(FIRE_RULE_LIMIT_PARAMETER).toString());
                    }
                    ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda())
                            .activateRuleFlowGroup(getRuleFlowGroup(), getProcessInstance().getId(), getUniqueId());

                    int fired = ((KieSession) getProcessInstance().getKnowledgeRuntime()).fireAllRules(fireLimit);
                    if (fired == fireLimit) {
                        throw new RuntimeException("Fire rule limit reached " + fireLimit + ", limit can be set via system property " + FIRE_RULE_LIMIT_PROPERTY 
                                                   + " or via data input of business rule task named " + FIRE_RULE_LIMIT_PARAMETER);
                    }

                    removeEventListeners();
                    retractFacts();
                    triggerCompleted();
                }
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Throwable e) {
        ExceptionScopeInstance exceptionScopeInstance = getExceptionScopeInstance(e);
        if(exceptionScopeInstance != null) {
            exceptionScopeInstance.handleException(e.getClass().getName(), e);
            
            return;
        } else {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if(rootCause != null) {                
                exceptionScopeInstance = getExceptionScopeInstance(rootCause);
                if(exceptionScopeInstance != null) {
                    exceptionScopeInstance.handleException(rootCause.getClass().getName(), rootCause);
                    
                    return;
                }
            }
            throw new WorkflowRuntimeException(this, getProcessInstance(), "Unable to execute Action: " + e.getMessage(), e);
            
        }
    }

    private ExceptionScopeInstance getExceptionScopeInstance(Throwable e) {
        return (ExceptionScopeInstance) resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, e.getClass().getName());
    }
   
    
    public void addEventListeners() {
        super.addEventListeners();
        addRuleSetListener();
    }

    private String getRuleSetEventType() {
        InternalKnowledgeRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
        if (kruntime instanceof StatefulKnowledgeSession) {
            return "RuleFlowGroup_" + getRuleFlowGroup() + "_" + ((StatefulKnowledgeSession) kruntime).getIdentifier();
        } else {
            return "RuleFlowGroup_" + getRuleFlowGroup();
        }
    }

    private void addRuleSetListener() {
        getProcessInstance().addEventListener(getRuleSetEventType(), this, true);
    }

    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener(getRuleSetEventType(), this, true);

    }

    public void cancel() {
        super.cancel();
        if (actAsWaitState()) {
            ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda()).deactivateRuleFlowGroup(getRuleFlowGroup());
        }
    }

    public void signalEvent(String type, Object event) {
        if (getRuleSetEventType().equals(type)) {
            removeEventListeners();
            retractFacts();
            triggerCompleted();
        }
    }

	
	public void retractFacts() {
	    Map<String, Object> objects = new HashMap<String, Object>();
        KieRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
	    
	    for (Entry<String, FactHandle> entry : factHandles.entrySet()) {
	        
            Object object = ((StatefulKnowledgeSession)kruntime).getObject(entry.getValue());
            String key = entry.getKey();
            key = key.replaceAll(getRuleFlowGroup()+"_", "");
            key = key.replaceAll(getProcessInstance().getId()+"_", "");
            objects.put(key , object);
            
            kruntime.delete(entry.getValue());
	        
	    }
	    
	    processOutputs(objects);
        factHandles.clear();
	}
	
	private void processOutputs(Map<String, Object> objects) {
	    RuleSetNode ruleSetNode = getRuleSetNode();
        if (ruleSetNode != null) {
            for (Iterator<DataAssociation> iterator = ruleSetNode.getOutAssociations().iterator(); iterator.hasNext(); ) {
                DataAssociation association = iterator.next();
                if (association.getTransformation() != null) {
                    Transformation transformation = association.getTransformation();
                    DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
                    if (transformer != null) {
                        Object parameterValue = transformer.transform(transformation.getCompiledExpression(), objects);
                        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                        resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getTarget());
                        if (variableScopeInstance != null && parameterValue != null) {
                              
                            variableScopeInstance.setVariable(association.getTarget(), parameterValue);
                        } else {
                            logger.warn("Could not find variable scope for variable {}", association.getTarget());
                            logger.warn("Continuing without setting variable.");
                        }
                        if (parameterValue != null) {
                            variableScopeInstance.setVariable(association.getTarget(), parameterValue);
                        }
                    }
                } else if (association.getAssignments() == null || association.getAssignments().isEmpty()) {
                    VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                    resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getTarget());
                    if (variableScopeInstance != null) {
                        Object value = objects.get(association.getSources().get(0));
                        if (value == null) {
                            try {
                                value = MVELSafeHelper.getEvaluator().eval(association.getSources().get(0), new MapVariableResolverFactory(objects));
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
                        logger.warn("Could not find variable scope for variable {}", association.getTarget());
                    }

                }               
            }
        }
        
    }

    protected Map<String, Object> evaluateParameters(RuleSetNode ruleSetNode) {
	    Map<String, Object> replacements = new HashMap<String, Object>();
	    
        for (Iterator<DataAssociation> iterator = ruleSetNode.getInAssociations().iterator(); iterator.hasNext(); ) {
            DataAssociation association = iterator.next();
            if (association.getTransformation() != null) {
            	Transformation transformation = association.getTransformation();
            	DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
            	if (transformer != null) {
            		Object parameterValue = transformer.transform(transformation.getCompiledExpression(), getSourceParameters(association));
            		if (parameterValue != null) {
            			replacements.put(association.getTarget(), parameterValue);
                    }
            	}
            } else if (association.getAssignments() == null || association.getAssignments().isEmpty()) {
                Object parameterValue = null;
                VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getSources().get(0));
                if (variableScopeInstance != null) {
                    parameterValue = variableScopeInstance.getVariable(association.getSources().get(0));
                } else {
                    try {
                        parameterValue = MVELSafeHelper.getEvaluator().eval(association.getSources().get(0), new NodeInstanceResolverFactory(this));
                    } catch (Throwable t) {
                        logger.error("Could not find variable scope for variable {}", association.getSources().get(0));
                        logger.error("when trying to execute RuleSetNode {}", ruleSetNode.getName());
                        logger.error("Continuing without setting parameter.");
                    }
                }
                if (parameterValue != null) {
                	replacements.put(association.getTarget(), parameterValue);
                }
            }
        }
        
        for (Map.Entry<String, Object> entry: ruleSetNode.getParameters().entrySet()) {
            if (entry.getValue() instanceof String) {
                
                Object value = resolveVariable(entry.getValue());
                if (value != null) {
                    replacements.put(entry.getKey(), value);
                }
                
            }
        }
        
        return replacements;
	}
	
	private Object resolveVariable(Object s) {
        
	    if (s instanceof String) {
            Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher((String) s);
            while (matcher.find()) {
                String paramName = matcher.group(1);
               
                VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                    resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName);
                if (variableScopeInstance != null) {
                    Object variableValue = variableScopeInstance.getVariable(paramName);
                    if (variableValue != null) { 
                        return variableValue;
                    }
                } else {
                    try {
                        Object variableValue = MVELSafeHelper.getEvaluator().eval(paramName, new NodeInstanceResolverFactory(this));
                        if (variableValue != null) {
                            return variableValue;
                        }
                    } catch (Throwable t) {
                        logger.error("Could not find variable scope for variable {}", paramName);
                    }
                }
            }
	    } 
        
        return s;
        
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
	
	private String resolveRuleFlowGroup(String origin) {
	    return (String) resolveVariable(origin);
	}

    public Map<String, FactHandle> getFactHandles() {
        return factHandles;
    }

    public void setFactHandles(Map<String, FactHandle> factHandles) {
        this.factHandles = factHandles;
    }

    public String getRuleFlowGroup() {
        if (ruleFlowGroup == null || ruleFlowGroup.trim().length() == 0) {
            ruleFlowGroup = getRuleSetNode().getRuleFlowGroup();
        }
        return ruleFlowGroup;
    }

    public void setRuleFlowGroup(String ruleFlowGroup) {
        
        this.ruleFlowGroup = ruleFlowGroup;
    }

    protected boolean actAsWaitState() {
        Object asWaitState = getProcessInstance().getKnowledgeRuntime().getEnvironment().get(ACT_AS_WAIT_STATE_PROPERTY);
        if (asWaitState != null) {
            return Boolean.parseBoolean(asWaitState.toString());
        }
        
        return false;
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
        return getRuleSetNode();
    }
}
