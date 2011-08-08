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

package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.FactHandle;
import org.drools.base.DroolsQuery;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.drools.core.util.Queue;
import org.drools.core.util.Queueable;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * Item entry in the <code>Agenda</code>.
 */
public class AgendaItem
    implements
    Activation,
    Queueable,
    Externalizable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long   serialVersionUID = 510l;

    /** The tuple. */
    private LeftTuple               tuple;
    
    /** The salience */
    private int                 salience;

    /** Used for sequential mode */
    private int                 sequenence;
    
    /** Rule terminal node, gives access to SubRule **/
    private RuleTerminalNode    rtn;

    /** The propagation context */
    private PropagationContext  context;

    /** The activation number */
    private long                activationNumber;

    /** A reference to the PriorityQeue the item is on */
    private Queue               queue;

    private int                 index;

    private LinkedList          justified;
    
    private LinkedList          blocked;
    
    private LinkedList          blockers;

    private boolean             activated;

    private InternalAgendaGroup agendaGroup;

    private ActivationGroupNode activationGroupNode;

    private ActivationNode      activationNode;
    
    private InternalFactHandle  factHandle;
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public AgendaItem() {

    }

    /**
     * Construct.
     *
     * @param tuple
     *            The tuple.
     * @param rule
     *            The rule.
     */
    public AgendaItem(final long activationNumber,
                      final LeftTuple tuple,
                      final int salience,
                      final PropagationContext context,
                      final RuleTerminalNode rtn) {
        this.tuple = tuple;
        this.context = context;
        this.salience = salience;
        this.rtn = rtn;
        this.activationNumber = activationNumber;
        this.index = -1;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public PropagationContext getPropagationContext() {
        return this.context;
    }
    
    public void setPropagationContext(PropagationContext context) {
        this.context = context;
    }

    /**
     * Retrieve the rule.
     *
     * @return The rule.
     */
    public Rule getRule() {
        return this.rtn.getRule();
    }

    /**
     * Retrieve the tuple.
     *
     * @return The tuple.
     */
    public LeftTuple getTuple() {
        return this.tuple;
    }

    public int getSalience() {
        return this.salience;
    }
    
    public void setSalience(int salience) {
        this.salience = salience;
    }

    public int getSequenence() {
        return sequenence;
    }

    public void setSequenence(int sequenence) {
        this.sequenence = sequenence;
    }

    public InternalFactHandle getFactHandle() {
        return factHandle;
    }

    public void setFactHandle(InternalFactHandle factHandle) {
        this.factHandle = factHandle;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.drools.spi.Activation#getActivationNumber()
     */
    public long getActivationNumber() {
        return this.activationNumber;
    }
    
    public void addBlocked(final LinkedListNode node) {        
        // Adds the blocked to the blockers list
        if ( this.blocked == null ) {
            this.blocked = new LinkedList();
        }

        this.blocked.add( node );
        LogicalDependency dep = ( LogicalDependency ) node;
        
        // now ad the blocker to the blocked's list - we need to check that references are null first
        AgendaItem blocked = (AgendaItem)dep.getJustified();
        if ( blocked.blockers == null ) {
            blocked.blockers = new LinkedList();
            blocked.blockers.add( dep.getJustifierEntry() );
        } else if ( dep.getJustifierEntry().getNext() == null && dep.getJustifierEntry().getPrevious() == null && blocked.getBlockers().getFirst() != dep.getJustifierEntry() ) {            
            blocked.blockers.add( dep.getJustifierEntry() );
        }
    }
    
    public void removeAllBlockersAndBlocked(DefaultAgenda agenda){
        if ( this.blockers != null ) {
            // Iterate and remove this node's logical dependency list from each of it's blockers
            for ( LinkedListEntry node = ( LinkedListEntry ) blockers.getFirst(); node != null; node = ( LinkedListEntry ) node.getNext() ) {
                LogicalDependency dep = ( LogicalDependency ) node.getObject();
                dep.getJustifier().getBlocked().remove( dep );                
            }
        }  
        this.blockers = null;
        
        if ( this.blocked != null ) {
            // Iterate and remove this node's logical dependency list from each of it's blocked
            for ( LogicalDependency dep = ( LogicalDependency ) blocked.getFirst(); dep != null; ) {
                LogicalDependency tmp = ( LogicalDependency ) dep.getNext();                
                removeBlocked( dep );
                AgendaItem justified = ( AgendaItem ) dep.getJustified();
                if (justified.getBlockers().isEmpty() ) {
                    // the match is no longer blocked, so stage it
                    agenda.getStageActivationsGroup().addActivation( justified );
                }                
                dep =  ( LogicalDependency ) tmp;
            }
        }
        this.blocked = null;
    }
    
    public void removeBlocked(final LinkedListNode node) {
        this.blocked.remove( node );
        
        LogicalDependency dep = ( LogicalDependency ) node;
        AgendaItem blocked = (AgendaItem)dep.getJustified();        
        blocked.blockers.remove( dep.getJustifierEntry() );
    }
    
    public void setBlocked(LinkedList justified) {
        this.blocked = justified;
    }        
    
    public LinkedList getBlocked() {
        return  this.blocked;
    } 
    
    public LinkedList getBlockers() {
        return  this.blockers;
    }    

    public void addLogicalDependency(final LogicalDependency node) {
        if ( this.justified == null ) {
            this.justified = new LinkedList();
        }

        this.justified.add( node );
    }

    public LinkedList getLogicalDependencies() {
        return this.justified;
    }

    public void setLogicalDependencies(LinkedList justified) {
        this.justified = justified;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(final boolean activated) {
        this.activated = activated;
    }

    public String toString() {                
        return "[Activation rule=" + this.rtn.getRule().getName() + ", act#=" + this.activationNumber + ", salience="+ this.salience + ", tuple=" + this.tuple + "]";
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || !(object instanceof AgendaItem) ) {
            return false;
        }

        final AgendaItem otherItem = (AgendaItem) object;

        return (this.rtn.getRule().equals( otherItem.getRule() ) && this.tuple.equals( otherItem.getTuple() ));
    }

    /**
     * Return the hashCode of the
     * <code>TupleKey<code> as the hashCode of the AgendaItem
     * @return
     */
    public int hashCode() {
        return this.tuple.hashCode();
    }

    public void enqueued(final int index) {
        this.index = index;
    }

    public void dequeue() {
        if ( this.agendaGroup != null ) {
            this.agendaGroup.remove( this );
        }
        this.activated = false;
        this.index = -1;
    }
    
    public int getIndex() {
        return this.index;
    }

    public void remove() {
        dequeue();
    }

    public ActivationGroupNode getActivationGroupNode() {
        return this.activationGroupNode;
    }

    public void setActivationGroupNode(final ActivationGroupNode activationNode) {
        this.activationGroupNode = activationNode;
    }

    public AgendaGroup getAgendaGroup() {
        return this.agendaGroup;
    }

    public void setAgendaGroup(final InternalAgendaGroup agendaGroup) {
        this.agendaGroup = agendaGroup;
    }

    public ActivationNode getActivationNode() {
        return this.activationNode;
    }

    public void setActivationNode(final ActivationNode activationNode) {
        this.activationNode = activationNode;
    }

    public GroupElement getSubRule() {
        return this.rtn.getSubRule();
    }
    
    public RuleTerminalNode getRuleTerminalNode() {
        return this.rtn;
    }

    public List<FactHandle> getFactHandles() {
        FactHandle[] factHandles = this.tuple.getFactHandles();
        List<FactHandle> list = new ArrayList<FactHandle>( factHandles.length );
        for ( FactHandle factHandle : factHandles ) {
            Object o = ((InternalFactHandle) factHandle).getObject();
            if ( !(o instanceof QueryElementFactHandle)) {
                list.add( factHandle );
            }
        }
        return Collections.unmodifiableList( list );
    }
    
    public String toExternalForm() {
        return "[ "+this.getRule().getName()+" active="+this.activated+ " ]";
    }

    public List<Object> getObjects() {
        FactHandle[] factHandles = this.tuple.getFactHandles();
        List<Object> list = new ArrayList<Object>( factHandles.length );
        for ( FactHandle factHandle : factHandles ) {
            Object o = ((InternalFactHandle) factHandle).getObject();
            if (!(o instanceof QueryElementFactHandle)) {
                list.add( o );
            }
        }
        return Collections.unmodifiableList( list );
    }

    public Object getDeclarationValue(String variableName) {
        Declaration decl = this.rtn.getSubRule().getOuterDeclarations().get( variableName );
        InternalFactHandle handle = this.tuple.get( decl );
        // need to double check, but the working memory reference is only used for resolving globals, right?
        return decl.getValue( null, handle.getObject() );
    }

    public List<String> getDeclarationIDs() {
        Declaration[] declArray = ((org.drools.reteoo.RuleTerminalNode)this.tuple.getLeftTupleSink()).getDeclarations();
        List<String> declarations = new ArrayList<String>();
        for( Declaration decl : declArray ) {
            declarations.add( decl.getIdentifier() );
        }
        return Collections.unmodifiableList( declarations );
    }

    public boolean isActive() {
        return isActivated();
    }
    
}
