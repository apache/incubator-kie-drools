package org.drools.reteoo;

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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.DisconnectedFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

/**
 * Leaf Rete-OO node responsible for enacting <code>Action</code> s on a
 * matched <code>Rule</code>.
 *
 * @see org.drools.rule.Rule
 *
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
public final class QueryTerminalNode extends BaseNode
    implements
    LeftTupleSinkNode,
    NodeMemory,
    TerminalNode {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     *
     */
    private static final long serialVersionUID = 400L;
    
    public static final short type = 8;
    
    /** The rule to invoke upon match. */
    private Rule              rule;
    private GroupElement      subrule;
    private LeftTupleSource   tupleSource;
    private boolean           tupleMemoryEnabled;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public QueryTerminalNode() {
    }

    /**
     * Constructor
     *
     * @param id node ID
     * @param source the tuple source for this node
     * @param rule the rule this node belongs to
     * @param subrule the subrule this node belongs to
     * @param context the current build context
     */
    public QueryTerminalNode(final int id,
                             final LeftTupleSource source,
                             final Rule rule,
                             final GroupElement subrule,
                             final BuildContext context ) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.rule = rule;
        this.subrule = subrule;
        this.tupleSource = source;
        this.tupleMemoryEnabled = false; //hard coded to false
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        rule = (Rule) in.readObject();
        subrule = (GroupElement) in.readObject();
        tupleSource = (LeftTupleSource) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        previousTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        nextTupleSinkNode = (LeftTupleSinkNode) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( rule );
        out.writeObject( subrule );
        out.writeObject( tupleSource );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeObject( previousTupleSinkNode );
        out.writeObject( nextTupleSinkNode );
    }

    /**
     * Retrieve the <code>Action</code> associated with this node.
     *
     * @return The <code>Action</code> associated with this node.
     */
    public Rule getRule() {
        return this.rule;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.impl.TupleSink
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Assert a new <code>Tuple</code>.
     *
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param workingMemory
     *            The working memory seesion.
     * @throws AssertionException
     *             If an error occurs while asserting.
     */
    public void assertLeftTuple(final LeftTuple tuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        final LinkedList list = (LinkedList) workingMemory.getNodeMemory( this );
        if ( list.isEmpty() ) {
            ((ReteooWorkingMemory) workingMemory).setQueryResults( this.rule.getName(),
                                                                   this );
        }
        
        InternalFactHandle[] handles = new InternalFactHandle[tuple.getIndex()]; // don't add one, as we adjust for root Query object
        LeftTuple entry = tuple;
  
        // miss out 0, which is the DroolsQuery object
        while ( entry.getIndex() != 0 ) { 
            InternalFactHandle handle  = entry.getLastHandle();
            handles[entry.getIndex() - 1] = new DisconnectedFactHandle(handle.getId(), handle.getIdentityHashCode(), handle.getObjectHashCode(), handle.getRecency(), handle.getObject() );
            entry = entry.getParent();
        }
        
        list.add( handles );   
    }

    public void retractLeftTuple(final LeftTuple tuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
    }

    public String toString() {
        return "[QueryTerminalNode("+this.getId()+"): rule=" + this.rule.getName() + "]";
    }

    public void ruleAttached() {

    }

    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void networkUpdated() {
        this.tupleSource.networkUpdated();
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            workingMemories[i].clearNodeMemory( this );
        }

        if ( !context.alreadyVisited( this.tupleSource ) ) {
            this.tupleSource.remove( context,
                                     builder,
                                     this,
                                     workingMemories );
        }
    }

    public boolean isInUse() {
        return false;
    }

    public void updateNewNode(final InternalWorkingMemory workingMemory,
                              final PropagationContext context) {
        // There are no child nodes to update, do nothing.
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return new LinkedList();
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * @return the subrule
     */
    public GroupElement getSubrule() {
        return this.subrule;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }
    
    public short getType() {
        return NodeTypeEnums.QueryTerminalNode;
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    } 

}
