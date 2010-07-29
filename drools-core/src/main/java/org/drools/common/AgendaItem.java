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

package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.FactHandle;
import org.drools.core.util.LinkedList;
import org.drools.core.util.Queue;
import org.drools.core.util.Queueable;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * Item entry in the <code>Agenda</code>.
 *
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public class AgendaItem
    implements
    Activation,
    Queueable,
    Externalizable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     *
     */
    private static final long   serialVersionUID = 510l;

    /** The tuple. */
    private Tuple               tuple;

    /** The rule. */
    private Rule                rule;

    /** The salience */
    private int                 salience;

    /** Used for sequential mode */
    private int                 sequenence;

    /** The subrule */
    private GroupElement        subrule;

    /** The propagation context */
    private PropagationContext  context;

    /** The activation number */
    private long                activationNumber;

    /** A reference to the PriorityQeue the item is on */
    private Queue               queue;

    private int                 index;

    private LinkedList          justified;

    private boolean             activated;

    private InternalAgendaGroup agendaGroup;

    private ActivationGroupNode activationGroupNode;

    private ActivationNode   activationNode;
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
                      final Tuple tuple,
                      final int salience,
                      final PropagationContext context,
                      final Rule rule,
                      final GroupElement subrule) {
        this.tuple = tuple;
        this.context = context;
        this.rule = rule;
        this.salience = salience;
        this.subrule = subrule;
        this.activationNumber = activationNumber;
        this.index = -1;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        tuple = (Tuple) in.readObject();
        rule = (Rule) in.readObject();
        salience = in.readInt();
        sequenence = in.readInt();
        subrule = (GroupElement) in.readObject();
        context = (PropagationContext) in.readObject();
        activationNumber = in.readLong();
        queue = (Queue) in.readObject();
        index = in.readInt();
        justified = (LinkedList) in.readObject();
        activated = in.readBoolean();
        agendaGroup = (InternalAgendaGroup) in.readObject();
        activationGroupNode = (ActivationGroupNode) in.readObject();
        activationNode = (ActivationNode) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( tuple );
        out.writeObject( rule );
        out.writeInt( salience );
        out.writeInt( sequenence );
        out.writeObject( subrule );
        out.writeObject( context );
        out.writeLong( activationNumber );
        out.writeObject( queue );
        out.writeInt( index );
        out.writeObject( justified );
        out.writeBoolean( activated );
        out.writeObject( agendaGroup );
        out.writeObject( activationGroupNode );
        out.writeObject( activationNode );
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
        return this.rule;
    }

    /**
     * Retrieve the tuple.
     *
     * @return The tuple.
     */
    public Tuple getTuple() {
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

    /*
     * (non-Javadoc)
     *
     * @see org.drools.spi.Activation#getActivationNumber()
     */
    public long getActivationNumber() {
        return this.activationNumber;
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
        return "[Activation rule=" + this.rule.getName() + ", tuple=" + this.tuple + "]";
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

        return (this.rule.equals( otherItem.getRule() ) && this.tuple.equals( otherItem.getTuple() ));
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
        return this.subrule;
    }

    public List<FactHandle> getFactHandles() {
        FactHandle[] factHandles = this.tuple.getFactHandles();
        List<FactHandle> list = new ArrayList<FactHandle>( factHandles.length );
        for ( FactHandle factHandle : factHandles ) {
            list.add( factHandle );
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
            list.add( ((InternalFactHandle) factHandle).getObject() );
        }
        return Collections.unmodifiableList( list );
    }

    public Object getDeclarationValue(String variableName) {
        Declaration decl = this.getRule().getDeclaration( variableName ); 
        InternalFactHandle handle = this.tuple.get( decl );
        // need to double check, but the working memory reference is only used for resolving globals, right?
        return decl.getValue( null, handle.getObject() );
    }

    public List<String> getDeclarationIDs() {
        Declaration[] declArray = this.getRule().getDeclarations(); 
        List<String> declarations = new ArrayList<String>();
        for( Declaration decl : declArray ) {
            declarations.add( decl.getIdentifier() );
        }
        return Collections.unmodifiableList( declarations );
    }
}
