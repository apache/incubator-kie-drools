package org.drools.ruleflow.instance.impl;

/*
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

import java.util.Iterator;
import java.util.List;

import org.drools.common.RuleFlowGroupNode;
import org.drools.ruleflow.core.IConnection;
import org.drools.ruleflow.core.IConstraint;
import org.drools.ruleflow.core.ISplit;
import org.drools.ruleflow.instance.IRuleFlowNodeInstance;
import org.drools.spi.Activation;
import org.drools.spi.RuleFlowGroup;

/**
 * Runtime counterpart of a split node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowSplitInstance extends RuleFlowNodeInstance
    implements
    IRuleFlowNodeInstance {

    protected ISplit getSplitNode() {
        return (ISplit) getNode();
    }

    public void trigger(final IRuleFlowNodeInstance from) {
        final ISplit split = getSplitNode();
        switch ( split.getType() ) {
            case ISplit.TYPE_AND :
                List outgoing = split.getOutgoingConnections();
                for ( final Iterator iterator = outgoing.iterator(); iterator.hasNext(); ) {
                    final IConnection connection = (IConnection) iterator.next();
                    getProcessInstance().getNodeInstance( connection.getTo() ).trigger( this );
                }
                break;
            case ISplit.TYPE_XOR :
                outgoing = split.getOutgoingConnections();
                int priority = Integer.MAX_VALUE;
                IConnection selected = null;
            	RuleFlowGroup systemRuleFlowGroup = getProcessInstance().getAgenda().getRuleFlowGroup("DROOLS_SYSTEM");
                for ( final Iterator iterator = outgoing.iterator(); iterator.hasNext(); ) {
                    final IConnection connection = (IConnection) iterator.next();
                    IConstraint constraint = split.getConstraint(connection);
                    if (constraint != null && constraint.getPriority() < priority) {
                    	String rule = "RuleFlow-" + getProcessInstance().getProcess().getId() + "-" +
                		getNode().getId() + "-" + connection.getTo().getId();
                    	for (Iterator activations = systemRuleFlowGroup.iterator(); activations.hasNext(); ) {
                    		Activation activation = ((RuleFlowGroupNode) activations.next()).getActivation();
                    		if (rule.equals(activation.getRule().getName())) {
                        		selected = connection;
                        		priority = constraint.getPriority();
                        		break;
                    		}
                    	}
                    }
                }
                if (selected == null) {
                	throw new IllegalArgumentException("XOR split could not find at least one valid outgoing connection for split " + getSplitNode().getName());
                }
                getProcessInstance().getNodeInstance( selected.getTo() ).trigger( this );
                break;
            case ISplit.TYPE_OR :
                outgoing = split.getOutgoingConnections();
                boolean found = false;
            	systemRuleFlowGroup = getProcessInstance().getAgenda().getRuleFlowGroup("DROOLS_SYSTEM");
                for ( final Iterator iterator = outgoing.iterator(); iterator.hasNext(); ) {
                    final IConnection connection = (IConnection) iterator.next();
                    IConstraint constraint = split.getConstraint(connection);
                    if (constraint != null) {
                    	String rule = "RuleFlow-" + getProcessInstance().getProcess().getId() + "-" +
                    		getNode().getId() + "-" + connection.getTo().getId();
                    	for (Iterator activations = systemRuleFlowGroup.iterator(); activations.hasNext(); ) {
                    		Activation activation = (Activation) activations.next();
                    		if (rule.equals(activation.getRule().getName())) {
                                getProcessInstance().getNodeInstance( connection.getTo() ).trigger( this );
                                found = true;
                        		break;
                    		}
                    	}
                    }
                    if (!found) {
                    	throw new IllegalArgumentException("OR split could not find at least one valid outgoing connection for split " + getSplitNode().getName());
                    }
                }
                break;
            default :
                throw new IllegalArgumentException( "Illegal split type " + split.getType() );
        }
    }

    public void triggerCompleted() {
        // TODO Auto-generated method stub

    }

}
