/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.common.InternalAgenda;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.spi.Activation;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.kie.api.definition.process.Node;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.process.NodeInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicNodeInstance extends CompositeContextNodeInstance implements AgendaEventListener {

	private static final long serialVersionUID = 510l;

	private String getRuleFlowGroupName() {
		return getNodeName();
	}

	protected DynamicNode getDynamicNode() {
		return (DynamicNode) getNode();
	}

    public void internalTrigger(NodeInstance from, String type) {
    	triggerEvent(ExtendedNodeImpl.EVENT_NODE_ENTER);
    	// if node instance was cancelled, abort
		if (getNodeInstanceContainer().getNodeInstance(getId()) == null) {
			return;
		}
    	InternalAgenda agenda =  (InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda();
    	agenda.getRuleFlowGroup(getRuleFlowGroupName()).setAutoDeactivate(false);
    	agenda.activateRuleFlowGroup(
			getRuleFlowGroupName(), getProcessInstance().getId(), getUniqueId());
//    	if (getDynamicNode().isAutoComplete() && getNodeInstances(false).isEmpty()) {
//    		triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
//    	}

        
        String rule = "RuleFlow-AdHocComplete-" + getProcessInstance().getProcessId() + "-" + getDynamicNode().getUniqueId();
        boolean isActive = ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda())
            .isRuleActiveInRuleFlowGroup(getRuleFlowGroupName(), rule, getProcessInstance().getId());
        if (isActive) {
            triggerCompleted();
        } else {
            addActivationListener();
        }
    	
    	// activate ad hoc fragments if they are marked as such
        List<Node> autoStartNodes = getDynamicNode().getAutoStartNodes();
        autoStartNodes
            .forEach(austoStartNode -> signalEvent(austoStartNode.getName(), null));

    }
    public void addEventListeners() {
        super.addEventListeners();
        addActivationListener();
    }
    
    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().getKnowledgeRuntime().removeEventListener(this);
        getProcessInstance().removeEventListener(getActivationEventType(), this, true);
    }
    
    private void addActivationListener() {
        getProcessInstance().getKnowledgeRuntime().addEventListener(this);
        getProcessInstance().addEventListener(getActivationEventType(), this, true);
    }
    
    private String getActivationEventType() {
        return "RuleFlow-AdHocComplete-" + getProcessInstance().getProcessId()
            + "-" + getDynamicNode().getUniqueId();
    }

	public void nodeInstanceCompleted(org.jbpm.workflow.instance.NodeInstance nodeInstance, String outType) {
	    Node nodeInstanceNode = nodeInstance.getNode();
	    if( nodeInstanceNode != null ) {
	        Object compensationBoolObj =  nodeInstanceNode.getMetaData().get("isForCompensation");
	        boolean isForCompensation = compensationBoolObj == null ? false : ((Boolean) compensationBoolObj);
	        if( isForCompensation ) {
	            return;
	        }
	    }
	    String completionCondition = getDynamicNode().getCompletionExpression();
		// TODO what if we reach the end of one branch but others might still need to be created ?
		// TODO are we sure there will always be node instances left if we are not done yet?
		if (isTerminated(nodeInstance)) {
		    triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
		} else if (getDynamicNode().isAutoComplete() && getNodeInstances(false).isEmpty()) {
    		triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
    	} else if (completionCondition != null && "mvel".equals(getDynamicNode().getLanguage())) {
    		Object value = MVELSafeHelper.getEvaluator().eval(completionCondition, new NodeInstanceResolverFactory(this));
    		if ( !(value instanceof Boolean) ) {
                throw new RuntimeException( "Completion condition expression must return boolean values: " + value
                		+ " for expression " + completionCondition);
            }
            if (((Boolean) value).booleanValue()) {
            	triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
            }
    	}
	}

    public void triggerCompleted(String outType) {
    	((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda())
    		.deactivateRuleFlowGroup(getRuleFlowGroupName());
    	super.triggerCompleted(outType);
    }

    @SuppressWarnings("unchecked")
    @Override
	public void signalEvent(String type, Object event) {
        if (getActivationEventType().equals(type)) {
            if (event instanceof MatchCreatedEvent) {
                matchCreated((MatchCreatedEvent) event);
            }
        } else {
    		super.signalEvent(type, event);
    		for (Node node: getCompositeNode().getNodes()) {
    			if (type.equals(node.getName()) && node.getIncomingConnections().isEmpty()) {
        			NodeInstance nodeInstance = getNodeInstance(node);
        			if (event != null) {                             
                        Map<String, Object> dynamicParams = new HashMap<>();
                        if (event instanceof Map) {
                            dynamicParams.putAll((Map<String, Object>) event);                                  
                        } else {
                            dynamicParams.put("Data", event);
                        }
                        ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).setDynamicParameters(dynamicParams);
                    }
                    ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).trigger(null, NodeImpl.CONNECTION_DEFAULT_TYPE);
        		}
    		}
        }
	}

    protected boolean isTerminated(NodeInstance from) {
        if (from instanceof EndNodeInstance) {
            
            return ((EndNodeInstance) from).getEndNode().isTerminate();
        }
        
        return false;
    }
    
    public void matchCreated(MatchCreatedEvent event) {
        // check whether this activation is from the DROOLS_SYSTEM agenda group
        String ruleFlowGroup = ((RuleImpl) event.getMatch().getRule()).getRuleFlowGroup();
        if ("DROOLS_SYSTEM".equals(ruleFlowGroup)) {
            // new activations of the rule associate with a milestone node
            // trigger node instances of that milestone node
            String ruleName = event.getMatch().getRule().getName();
            String milestoneName = "RuleFlow-AdHocComplete-" + getProcessInstance().getProcessId() + "-" + getNodeId();
            if (milestoneName.equals(ruleName) && checkProcessInstance((Activation) event.getMatch())) {
                synchronized(getProcessInstance()) {
                    DynamicNodeInstance.this.removeEventListeners();
                    DynamicNodeInstance.this.triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
                }
            }
        }
    }

    public void matchCancelled(MatchCancelledEvent event) {
        // Do nothing
    }

    public void afterMatchFired(AfterMatchFiredEvent event) {
        // Do nothing
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        // Do nothing
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        // Do nothing
    }

    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        // Do nothing
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }
}
