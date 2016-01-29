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
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.jbpm.workflow.instance.impl.WorkItemResolverFactory;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.process.NodeInstance;

public class DynamicNodeInstance extends CompositeContextNodeInstance {

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
		if (getDynamicNode().isAutoComplete() && getNodeInstances(false).isEmpty()) {
    		triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
    	} else if (completionCondition != null) {
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

    @Override
	public void signalEvent(String type, Object event) {
		super.signalEvent(type, event);
		for (Node node: getCompositeNode().getNodes()) {
			if (type.equals(node.getName()) && node.getIncomingConnections().isEmpty()) {
    			NodeInstance nodeInstance = getNodeInstance(node);
                ((org.jbpm.workflow.instance.NodeInstance) nodeInstance)
                	.trigger(null, NodeImpl.CONNECTION_DEFAULT_TYPE);
    		}
		}
	}

}
