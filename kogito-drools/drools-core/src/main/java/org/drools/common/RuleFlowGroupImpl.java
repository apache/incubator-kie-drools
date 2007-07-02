package org.drools.common;

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

import org.drools.ruleflow.instance.RuleFlowNodeInstance;
import org.drools.ruleflow.instance.impl.RuleFlowSequenceNodeInstanceImpl;
import org.drools.spi.Activation;
import org.drools.util.Iterator;
import org.drools.util.LinkedList;
import org.drools.util.LinkedList.LinkedListIterator;

/**
 * Implementation of a <code>RuleFlowGroup</code> that collects activations
 * of rules of this ruleflow-group.
 * If this group is activated, all its activations are added to the agenda.
 * As long as this group is active, its activations are added to the agenda.
 * Deactivating the group removes all its activations from the agenda and
 * collects them until it is activated again.
 * By default, <code>RuleFlowGroups</code> are automatically deactivated when there are no more
 * activations in the <code>RuleFlowGroup</code>.  However, this can be configured.  
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 *
 */
public class RuleFlowGroupImpl extends RuleFlowSequenceNodeInstanceImpl
    implements
    InternalRuleFlowGroup {

    private static final long serialVersionUID = 320L;

    private InternalWorkingMemory 	workingMemory;
    private final String      		name;
    private boolean           		active           = false;
    private final LinkedList  		list;
    private boolean           		autoDeactivate   = true;

    /**
     * Construct a <code>RuleFlowGroupImpl</code> with the given name.
     * 
     * @param name
     *      The RuleFlowGroup name.
     */
    public RuleFlowGroupImpl(final String name) {
        this.name = name;
        this.list = new LinkedList();
    }

    public String getName() {
        return this.name;
    }
    
    public void setWorkingMemory(InternalWorkingMemory workingMemory) {
    	this.workingMemory = workingMemory;
    }
    
    public InternalWorkingMemory getWorkingMemory() {
    	return this.workingMemory;
    }

    public void setActive(final boolean active) {
        if ( this.active == active ) {
            return;
        }
        this.active = active;
        if ( active ) {
            if ( this.list.isEmpty() ) {
            	if ( this.autoDeactivate ) {
                	// if the list of activations is empty and
            		// auto-deactivate is on, deactivate this group
    				WorkingMemoryAction action = new DeactivateCallback( this );
    				this.workingMemory.queueWorkingMemoryAction( action );
            	}
            } else {
            	triggerActivations();
            }
            ((EventSupport) this.workingMemory).getRuleFlowEventSupport()
    			.fireRuleFlowGroupActivated(this, this.workingMemory);
        } else {
            final Iterator it = this.list.iterator();
            for ( RuleFlowGroupNode node = (RuleFlowGroupNode) it.next(); node != null; node = (RuleFlowGroupNode) it.next() ) {
                final Activation activation = node.getActivation();
                activation.remove();
                if ( activation.getActivationGroupNode() != null ) {
                    activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
                }
            }
            ((EventSupport) this.workingMemory).getRuleFlowEventSupport()
				.fireRuleFlowGroupDeactivated(this, this.workingMemory);
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isAutoDeactivate() {
        return this.autoDeactivate;
    }

    public void setAutoDeactivate(final boolean autoDeactivate) {
        this.autoDeactivate = autoDeactivate;
        if ( autoDeactivate && this.active && this.list.isEmpty() ) {
            this.active = false;
        }
    }

    private void triggerActivations() {
        // iterate all activations adding them to their AgendaGroups
        final Iterator it = this.list.iterator();
        for ( RuleFlowGroupNode node = (RuleFlowGroupNode) it.next(); node != null; node = (RuleFlowGroupNode) it.next() ) {
            final Activation activation = node.getActivation();
            ((BinaryHeapQueueAgendaGroup) activation.getAgendaGroup()).add( activation );
        }
    }

    public void clear() {
        final Iterator it = this.list.iterator();
        for ( RuleFlowGroupNode node = (RuleFlowGroupNode) it.next(); node != null; node = (RuleFlowGroupNode) it.next() ) {
            node.getActivation().remove();
        }
    }

    public int size() {
        return this.list.size();
    }

    public void addActivation(final Activation activation) {
        final RuleFlowGroupNode node = new RuleFlowGroupNode( activation,
                                                              this );
        activation.setRuleFlowGroupNode( node );
        this.list.add( node );

        if ( this.active ) {
            ((InternalAgendaGroup) activation.getAgendaGroup()).add( activation );
        }
    }

    public void removeActivation(final Activation activation) {
        final RuleFlowGroupNode node = activation.getRuleFlowGroupNode();
        this.list.remove( node );
        activation.setActivationGroupNode( null );
        if ( this.autoDeactivate ) {
            if ( this.list.isEmpty() ) {
            	// deactivate callback
            	WorkingMemoryAction action = new DeactivateCallback( this );
            	this.workingMemory.queueWorkingMemoryAction( action );
            }
        }
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public java.util.Iterator iterator() {
        return this.list.javaUtilIterator();
    }

    public String toString() {
        return "RuleFlowGroup '" + this.name + "'";
    }

    public boolean equal(final Object object) {
        if ( (object == null) || !(object instanceof RuleFlowGroupImpl) ) {
            return false;
        }

        if ( ((RuleFlowGroupImpl) object).name.equals( this.name ) ) {
            return true;
        }

        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public void trigger(final RuleFlowNodeInstance parent) {
        setActive( true );
    }

    public static class DeactivateCallback implements WorkingMemoryAction {
        private final InternalRuleFlowGroup ruleFlowGroup;

        public DeactivateCallback(InternalRuleFlowGroup ruleFlowGroup) {
            this.ruleFlowGroup = ruleFlowGroup;
        }

        public void execute(InternalWorkingMemory workingMemory) {
        	// check whether ruleflow group is still empty first
            if ( this.ruleFlowGroup.isEmpty() ) {
            	// deactivate ruleflow group
                this.ruleFlowGroup.setActive( false );
                // only trigger next node if this RuleFlowGroup was
                // triggered from inside a process instance
                if ( this.ruleFlowGroup.getProcessInstance() != null ) {
                    this.ruleFlowGroup.triggerCompleted();
                }
            }
        }
    }
}
