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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.Agenda;
import org.drools.ruleflow.common.instance.IProcessInstance;
import org.drools.ruleflow.common.instance.impl.ProcessInstance;
import org.drools.ruleflow.core.IEndNode;
import org.drools.ruleflow.core.IJoin;
import org.drools.ruleflow.core.INode;
import org.drools.ruleflow.core.IRuleFlowProcess;
import org.drools.ruleflow.core.IRuleSetNode;
import org.drools.ruleflow.core.ISplit;
import org.drools.ruleflow.core.IStartNode;
import org.drools.ruleflow.instance.IRuleFlowNodeInstance;
import org.drools.ruleflow.instance.IRuleFlowProcessInstance;

/**
 * Default implementation of a RuleFlow process instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowProcessInstance extends ProcessInstance implements IRuleFlowProcessInstance {
    
	private static final long serialVersionUID = -6760756665603399413L;
	
	private Agenda agenda;
	private List nodeInstances = new ArrayList();
	
	public IRuleFlowProcess getRuleFlowProcess() {
		return (IRuleFlowProcess) getProcess();
	}

    public void addNodeInstance(IRuleFlowNodeInstance nodeInstance) {
        nodeInstances.add(nodeInstance);
        nodeInstance.setProcessInstance(this);
    }
    
    public void removeNodeInstance(IRuleFlowNodeInstance nodeInstance) {    	
        nodeInstances.remove(nodeInstance);
    }
    
    public Collection getNodeInstances() {
        return Collections.unmodifiableCollection(nodeInstances);
    }
    
    public IRuleFlowNodeInstance getFirstNodeInstance(long nodeId) {
        for (Iterator iterator = nodeInstances.iterator(); iterator.hasNext(); ) {
        	IRuleFlowNodeInstance nodeInstance = (IRuleFlowNodeInstance) iterator.next();
            if (nodeInstance.getNodeId() == nodeId) {
                return nodeInstance;
            }
        }
        return null;
    }
    
	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

	public IRuleFlowNodeInstance getNodeInstance(INode node) {
		if (node instanceof IRuleSetNode) {
			IRuleFlowNodeInstance result = (IRuleFlowNodeInstance)
				agenda.getRuleFlowGroup(((IRuleSetNode) node).getRuleFlowGroup());
			result.setNodeId(node.getId());
			addNodeInstance(result);
			return result;
		} else if (node instanceof ISplit) {
			IRuleFlowNodeInstance result = getFirstNodeInstance(node.getId());
			if (result == null) {
				result = new RuleFlowSplitInstance();
				result.setNodeId(node.getId());
				addNodeInstance(result);
				return result;
			}
		} else if (node instanceof IJoin) {
			IRuleFlowNodeInstance result = getFirstNodeInstance(node.getId());
			if (result == null) {
				result = new RuleFlowJoinInstance();
				result.setNodeId(node.getId());
				addNodeInstance(result);
			}
			return result;
		} else if (node instanceof IStartNode) {
			IRuleFlowNodeInstance result = new StartNodeInstance();
			result.setNodeId(node.getId());
			addNodeInstance(result);
			return result;
		} else if (node instanceof IEndNode) {
			IRuleFlowNodeInstance result = new EndNodeInstance();
			result.setNodeId(node.getId());
			addNodeInstance(result);
			return result;
		}
		throw new IllegalArgumentException("Illegal node type: " + node.getClass());
	}
	
	public void start() {
		if (getState() != IProcessInstance.STATE_PENDING) {
			throw new IllegalArgumentException(
				"A process instance can only be started once");
		}
		setState(IProcessInstance.STATE_ACTIVE);
		getNodeInstance(getRuleFlowProcess().getStart()).trigger(null);
	}
	
	public String toString() {
    	StringBuilder sb = new StringBuilder("RuleFlowProcessInstance");
    	sb.append(getId());
    	sb.append(" [processId=");
    	sb.append(getProcess().getId());
    	sb.append(",state=");
    	sb.append(getState());
    	sb.append("]");
    	return sb.toString();
    }
}
