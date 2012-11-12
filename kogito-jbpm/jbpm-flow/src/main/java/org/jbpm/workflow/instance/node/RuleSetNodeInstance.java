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
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.process.core.datatype.DataType;
import org.drools.runtime.rule.impl.InternalAgenda;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.EventListener;
import org.kie.runtime.process.NodeInstance;
import org.kie.runtime.rule.FactHandle;
import org.mvel2.MVEL;
import org.mvel2.integration.impl.MapVariableResolverFactory;

/**
 * Runtime counterpart of a ruleset node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleSetNodeInstance extends StateBasedNodeInstance implements EventListener {

    private static final long serialVersionUID = 510l;
    
    private Map<String, FactHandle> factHandles = new HashMap<String, FactHandle>();
    private String ruleFlowGroup;

    protected RuleSetNode getRuleSetNode() {
        return (RuleSetNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
    	super.internalTrigger(from, type);
    	if ( !org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals( type ) ) {
            throw new IllegalArgumentException( "A RuleSetNode only accepts default incoming connections!" );
        }
    	// first set rule flow group
    	setRuleFlowGroup(resolveRuleFlowGroup(getRuleSetNode().getRuleFlowGroup()));
    	
    	//proceed
    	KnowledgeRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
    	Map<String, Object> inputs = evaluateParameters(getRuleSetNode());
    	for (Entry<String, Object> entry : inputs.entrySet()) {
    	    String inputKey = getRuleFlowGroup() + "_" +getProcessInstance().getId() +"_"+entry.getKey();
    	    
    	    factHandles.put(inputKey, kruntime.insert(entry.getValue()));
    	}
    	
        addRuleSetListener();
        ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda())
        	.activateRuleFlowGroup( getRuleFlowGroup(), getProcessInstance().getId(), getUniqueId() );
    }

    public void addEventListeners() {
        super.addEventListeners();
        addRuleSetListener();
    }
    
    private String getRuleSetEventType() {
    	InternalKnowledgeRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
    	if (kruntime instanceof StatefulKnowledgeSession) {
    		return "RuleFlowGroup_" + getRuleFlowGroup() + "_" + ((StatefulKnowledgeSession) kruntime).getId();
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
        ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda())
        	.deactivateRuleFlowGroup( getRuleFlowGroup() );
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
	    KnowledgeRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
	    
	    for (Entry<String, FactHandle> entry : factHandles.entrySet()) {
	        
            Object object = ((StatefulKnowledgeSession)kruntime).getObject(entry.getValue());
            String key = entry.getKey();
            key = key.replaceAll(getRuleFlowGroup()+"_", "");
            key = key.replaceAll(getProcessInstance().getId()+"_", "");
            objects.put(key , object);
            
            kruntime.retract(entry.getValue());
	        
	    }
	    
	    RuleSetNode ruleSetNode = getRuleSetNode();
        if (ruleSetNode != null) {
            for (Iterator<DataAssociation> iterator = ruleSetNode.getOutAssociations().iterator(); iterator.hasNext(); ) {
                DataAssociation association = iterator.next();
                if (association.getAssignments() == null || association.getAssignments().isEmpty()) {
                    VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                    resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getTarget());
                    if (variableScopeInstance != null) {
                        Object value = objects.get(association.getSources().get(0));
                        if (value == null) {
                            try {
                                value = MVEL.eval(association.getSources().get(0), new MapVariableResolverFactory(objects));
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
                    }

                }               
            }
        }
        factHandles.clear();
	}
	
	protected Map<String, Object> evaluateParameters(RuleSetNode ruleSetNode) {
	    Map<String, Object> replacements = new HashMap<String, Object>();
        
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
            Matcher matcher = PARAMETER_MATCHER.matcher((String) s);
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
                        Object variableValue = MVEL.eval(paramName, new NodeInstanceResolverFactory(this));
                        if (variableValue != null) {
                            return variableValue;
                        }
                    } catch (Throwable t) {
                        System.err.println("Could not find variable scope for variable " + paramName);
                    }
                }
            }
	    } 
        
        return s;
        
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
        return ruleFlowGroup;
    }

    public void setRuleFlowGroup(String ruleFlowGroup) {
        this.ruleFlowGroup = ruleFlowGroup;
    }

}
