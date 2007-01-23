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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.conflict.DepthConflictResolver;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.RuleFlowGroup;
import org.drools.util.BinaryHeapQueue;
import org.drools.util.Iterator;
import org.drools.util.LinkedList;
import org.drools.util.ObjectHashMap;
import org.drools.util.Queueable;
import org.drools.util.LinkedList.LinkedListIterator;

/**
 * <code>AgendaGroup</code> implementation that uses a <code>PriorityQueue</code> to prioritise the evaluation of added
 * <code>ActivationQueue</code>s. The <code>AgendaGroup</code> also maintains a <code>Map</code> of <code>ActivationQueues</code> 
 * for requested salience values.
 * 
 * @see PriorityQueue
 * @see ActivationQueue
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class RuleFlowGroupImpl
    implements
    RuleFlowGroup {

    private static final long serialVersionUID = 320L;

    private final String      name;

    private final LinkedList  list;

    private List              childNodes       = Collections.EMPTY_LIST;

    /**
     * Construct an <code>AgendaGroup</code> with the given name.
     * 
     * @param name
     *      The <AgendaGroup> name.
     */
    public RuleFlowGroupImpl(final String name) {
        this.name = name;
        this.list = new LinkedList();
    }

    /* (non-Javadoc)
     * @see org.drools.spi.AgendaGroup#getName()
     */
    /* (non-Javadoc)
     * @see org.drools.common.RuleFlowGroup#getName()
     */
    public String getName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see org.drools.common.RuleFlowGroup#addChild(org.drools.common.RuleFlowGroup)
     */
    public void addChild(final RuleFlowGroup child) {
        if ( this.childNodes == Collections.EMPTY_LIST ) {
            this.childNodes = new ArrayList( 1 );
        }
        this.childNodes.add( child );
    }

    /* (non-Javadoc)
     * @see org.drools.common.RuleFlowGroup#removeChild(org.drools.common.RuleFlowGroup)
     */
    public boolean removeChild(final RuleFlowGroup child) {
        return this.childNodes.remove( child );
    }

    /* (non-Javadoc)
     * @see org.drools.common.RuleFlowGroup#activate()
     */
    public void activate() {
        // iterate all activations adding them to their AgendaGroups
        LinkedListIterator it = this.list.iterator();
        for ( RuleFlowGroupNode node = (RuleFlowGroupNode) it.next(); node != null; node = (RuleFlowGroupNode) it.next() ) {
            Activation activation = node.getActivation();
            ( (AgendaGroupImpl) activation.getAgendaGroup() ).add( activation );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.RuleFlowGroup#activateChildren()
     */
    public void activateChildren() {
        // do we have any children still to fire, if so remove them from the AgendaGroups
        if ( !this.list.isEmpty() ) {
            clear();
        }

        // iterate all children calling activate
        for ( java.util.Iterator it = this.childNodes.iterator(); it.hasNext(); ) {
            ((RuleFlowGroup) it.next() ).activate();
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.RuleFlowGroup#clear()
     */
    public void clear() {
        LinkedListIterator it = this.list.iterator();
        for ( RuleFlowGroupNode node = (RuleFlowGroupNode) it.next(); node != null; node = (RuleFlowGroupNode) it.next() ) {
            Activation activation = node.getActivation();
            activation.remove();
        }
    }

    /* (non-Javadoc)
     * @see org.drools.spi.AgendaGroup#size()
     */
    /* (non-Javadoc)
     * @see org.drools.common.RuleFlowGroup#size()
     */
    public int size() {
        return this.list.size();
    }

    public void addActivation(final Activation activation) {
        final RuleFlowGroupNode node = new RuleFlowGroupNode( activation,
                                                              this );
        activation.setRuleFlowGroupNode( node );
        this.list.add( node );
    }

    public void removeActivation(final Activation activation) {
        final RuleFlowGroupNode node = activation.getRuleFlowGroupNode();
        this.list.remove( node );
        activation.setActivationGroupNode( null );
    }

    /* (non-Javadoc)
     * @see org.drools.common.RuleFlowGroup#isEmpty()
     */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.drools.common.RuleFlowGroup#getActivations()
     */
    public Activation[] getActivations() {
        //return (Activation[]) this.activations.toArray( new AgendaItem[this.queue.size()] );
        return null;
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
}
