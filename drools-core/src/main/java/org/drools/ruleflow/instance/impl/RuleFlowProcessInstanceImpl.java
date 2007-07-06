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
import org.drools.WorkingMemory;
import org.drools.common.EventSupport;
import org.drools.common.InternalWorkingMemory;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.ruleflow.common.instance.impl.ProcessInstanceImpl;
import org.drools.ruleflow.core.EndNode;
import org.drools.ruleflow.core.Join;
import org.drools.ruleflow.core.MilestoneNode;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.core.RuleSetNode;
import org.drools.ruleflow.core.Split;
import org.drools.ruleflow.core.StartNode;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;

/**
 * Default implementation of a RuleFlow process instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowProcessInstanceImpl extends ProcessInstanceImpl
    implements
    RuleFlowProcessInstance, AgendaEventListener {

    private static final long serialVersionUID = -6760756665603399413L;

    private InternalWorkingMemory 	workingMemory;
    private final List      nodeInstances    = new ArrayList();

    public RuleFlowProcess getRuleFlowProcess() {
        return (RuleFlowProcess) getProcess();
    }

    public void addNodeInstance(final RuleFlowNodeInstance nodeInstance) {
        this.nodeInstances.add( nodeInstance );
        nodeInstance.setProcessInstance( this );
    }

    public void removeNodeInstance(final RuleFlowNodeInstance nodeInstance) {
        this.nodeInstances.remove( nodeInstance );
    }

    public Collection getNodeInstances() {
        return Collections.unmodifiableCollection( this.nodeInstances );
    }

    public RuleFlowNodeInstance getFirstNodeInstance(final long nodeId) {
        for ( final Iterator iterator = this.nodeInstances.iterator(); iterator.hasNext(); ) {
            final RuleFlowNodeInstance nodeInstance = (RuleFlowNodeInstance) iterator.next();
            if ( nodeInstance.getNodeId() == nodeId ) {
                return nodeInstance;
            }
        }
        return null;
    }

    public Agenda getAgenda() {
    	if (this.workingMemory == null) {
    		return null;
    	}
        return this.workingMemory.getAgenda();
    }

    public void setWorkingMemory(final InternalWorkingMemory workingMemory) {
    	if (this.workingMemory != null) {
    		throw new IllegalArgumentException("A working memory can only be set once.");
    	}
        this.workingMemory = workingMemory;
        workingMemory.addEventListener(this);
    }
    
    public WorkingMemory getWorkingMemory() {
    	return this.workingMemory;
    }

    public RuleFlowNodeInstance getNodeInstance(final Node node) {
        if ( node instanceof RuleSetNode ) {
            final RuleFlowNodeInstance result = (RuleFlowNodeInstance) getAgenda().getRuleFlowGroup( ((RuleSetNode) node).getRuleFlowGroup() );
            result.setNodeId( node.getId() );
            addNodeInstance( result );
            return result;
        } else if ( node instanceof Split ) {
            RuleFlowNodeInstance result = getFirstNodeInstance( node.getId() );
            if ( result == null ) {
                result = new RuleFlowSplitInstanceImpl();
                result.setNodeId( node.getId() );
                addNodeInstance( result );
            }
            return result;
        } else if ( node instanceof Join ) {
            RuleFlowNodeInstance result = getFirstNodeInstance( node.getId() );
            if ( result == null ) {
                result = new RuleFlowJoinInstanceImpl();
                result.setNodeId( node.getId() );
                addNodeInstance( result );
            }
            return result;
        } else if ( node instanceof StartNode ) {
            final RuleFlowNodeInstance result = new StartNodeInstanceImpl();
            result.setNodeId( node.getId() );
            addNodeInstance( result );
            return result;
        } else if ( node instanceof EndNode ) {
            final RuleFlowNodeInstance result = new EndNodeInstanceImpl();
            result.setNodeId( node.getId() );
            addNodeInstance( result );
            return result;
        } else if ( node instanceof MilestoneNode ) {
            final RuleFlowNodeInstance result = new MilestoneNodeInstanceImpl();
            result.setNodeId( node.getId() );
            addNodeInstance( result );
            return result;
        }
        throw new IllegalArgumentException( "Illegal node type: " + node.getClass() );
    }

    public void start() {
        if ( getState() != ProcessInstanceImpl.STATE_PENDING ) {
            throw new IllegalArgumentException( "A process instance can only be started once" );
        }
        setState( ProcessInstanceImpl.STATE_ACTIVE );
        getNodeInstance( getRuleFlowProcess().getStart() ).trigger( null );
    }
    
    public void setState(final int state) {
        super.setState(state);
        // deactivate all node instances of this process instance
        while (!nodeInstances.isEmpty()) {
        	RuleFlowNodeInstance nodeInstance = (RuleFlowNodeInstance) nodeInstances.get(0);
        	nodeInstance.cancel();
        }
        workingMemory.removeEventListener(this);
        if (state == ProcessInstanceImpl.STATE_COMPLETED) {
        	((EventSupport) this.workingMemory).getRuleFlowEventSupport()
        		.fireRuleFlowProcessCompleted(this, this.workingMemory);
        }
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer( "RuleFlowProcessInstance" );
        sb.append( getId() );
        sb.append( " [processId=" );
        sb.append( getProcess().getId() );
        sb.append( ",state=" );
        sb.append( getState() );
        sb.append( "]" );
        return sb.toString();
    }

	public void activationCreated(ActivationCreatedEvent event, WorkingMemory workingMemory) {
		// TODO group all milestone related code in milestone instance impl?
		// check whether this activation is from the DROOLS_SYSTEM agenda group
		String ruleFlowGroup = event.getActivation().getRule().getRuleFlowGroup();
		if ("DROOLS_SYSTEM".equals(ruleFlowGroup)) {
			// new activations of the rule associate with a milestone node
			// trigger node instances of that milestone node
			String ruleName = event.getActivation().getRule().getName();
			for (Iterator iterator = getNodeInstances().iterator(); iterator.hasNext(); ) {
				RuleFlowNodeInstance nodeInstance = (RuleFlowNodeInstance) iterator.next();
				if (nodeInstance instanceof MilestoneNodeInstanceImpl) {
					String milestoneName = "RuleFlow-" + getProcess().getId()
		    			+ "-" + nodeInstance.getNodeId();
					if (milestoneName.equals(ruleName)) {
						((MilestoneNodeInstanceImpl) nodeInstance).triggerCompleted();
					}
				}
				
			}
		}
	}

	public void activationCancelled(ActivationCancelledEvent event, WorkingMemory workingMemory) {
		// Do nothing
	}

	public void afterActivationFired(AfterActivationFiredEvent event, WorkingMemory workingMemory) {
		// Do nothing
	}

	public void agendaGroupPopped(AgendaGroupPoppedEvent event, WorkingMemory workingMemory) {
		// Do nothing
	}

	public void agendaGroupPushed(AgendaGroupPushedEvent event, WorkingMemory workingMemory) {
		// Do nothing
	}

	public void beforeActivationFired(BeforeActivationFiredEvent event, WorkingMemory workingMemory) {
		// Do nothing
	}
}
