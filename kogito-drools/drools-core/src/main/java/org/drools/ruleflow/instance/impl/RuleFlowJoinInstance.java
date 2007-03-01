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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.ruleflow.core.IConnection;
import org.drools.ruleflow.core.IJoin;
import org.drools.ruleflow.core.INode;
import org.drools.ruleflow.instance.IRuleFlowNodeInstance;

/**
 * Runtime counterpart of a join node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowJoinInstance extends RuleFlowNodeInstance implements IRuleFlowNodeInstance {

	private Map triggers = new HashMap();
	
	protected IJoin getJoinNode() {
		return (IJoin) getNode();
	}
	
	public void trigger(IRuleFlowNodeInstance from) {
		IJoin join = getJoinNode();
		switch (join.getType()) {
		case IJoin.TYPE_XOR:
			triggerCompleted();
			break;
		case IJoin.TYPE_AND:
			INode node = getProcessInstance().getRuleFlowProcess().getNode(from.getNodeId());
			Integer count = (Integer) triggers.get(node);
			if (count == null) {
				triggers.put(node, new Integer(1));
			} else {
				triggers.put(node, new Integer(count.intValue() + 1));
			}
			checkActivation();
			break;
		default:
			throw new IllegalArgumentException("Illegal join type " + join.getType());
		}
	}

	private void checkActivation() {
		// check whether all parent nodes have been triggered 
        for (Iterator it = getJoinNode().getIncomingConnections().iterator(); it.hasNext(); ) {
        	IConnection connection = (IConnection) it.next();
            if (triggers.get(connection.getFrom()) == null) {
            	return;
            }
        }
        // if true, decrease trigger count for all parents and trigger children
        for (Iterator it = getJoinNode().getIncomingConnections().iterator(); it.hasNext(); ) {
        	IConnection connection = (IConnection) it.next();
            Integer count = (Integer) triggers.get(connection.getFrom());
            if (count.intValue() == 1) {
            	triggers.remove(connection.getFrom());
            } else {
            	triggers.put(connection.getFrom(), new Integer(count.intValue() - 1));
            }
        }
        triggerCompleted();
	}
	
	public void triggerCompleted() {
		getProcessInstance().getNodeInstance(
			getJoinNode().getTo().getTo()).trigger(this);
	}

}
