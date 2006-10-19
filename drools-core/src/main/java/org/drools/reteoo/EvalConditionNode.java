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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.EvalCondition;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;

/**
 * Node which filters <code>ReteTuple</code>s.
 * 
 * <p>
 * Using a semantic <code>Test</code>, this node may allow or disallow
 * <code>Tuples</code> to proceed further through the Rete-OO network.
 * </p>
 * 
 * @see EvalConditionNode
 * @see Eval
 * @see ReteTuple
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
class EvalConditionNode extends TupleSource
    implements
    TupleSink,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long   serialVersionUID = 1986131208174298080L;

    /** The semantic <code>Test</code>. */
    private final EvalCondition condition;

    /** The source of incoming <code>Tuples</code>. */
    private final TupleSource   tupleSource;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param rule
     *            The rule
     * @param tupleSource
     *            The source of incoming <code>Tuples</code>.
     * @param eval
     */
    EvalConditionNode(final int id,
                      final TupleSource tupleSource,
                      final EvalCondition eval) {
        super( id );
        this.condition = eval;
        this.tupleSource = tupleSource;
        this.hasMemory = true;
    }

    /**
     * Attaches this node into the network.
     */
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
                                                                                      null );
            this.tupleSource.updateNewNode( workingMemory,
                                            propagationContext );
        }
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the <code>Test</code> associated with this node.
     * 
     * @return The <code>Test</code>.
     */
    public EvalCondition getCondition() {
        return this.condition;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.reteoo.impl.TupleSink
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
    public void assertTuple(final ReteTuple tuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {

        final boolean allowed = this.condition.isAllowed( tuple,
                                                          workingMemory );

        if ( allowed ) {
            final LinkedList memory = (LinkedList) workingMemory.getNodeMemory( this );
            memory.add( tuple );

            this.sink.propagateAssertTuple( tuple,
                                            context,
                                            workingMemory );
        }
    }

    public void retractTuple(final ReteTuple tuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final LinkedList memory = (LinkedList) workingMemory.getNodeMemory( this );

        // checks if the tuple is attach to tuple
        if ( tuple.getChildEntries() != null && !tuple.getChildEntries().isEmpty() ) {
            memory.remove( tuple );

            tuple.retractChildEntries( context,
                                       workingMemory );
        }
    }

    public void modifyTuple(final ReteTuple tuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        final LinkedList memory = (LinkedList) workingMemory.getNodeMemory( this );
        boolean exists = (tuple.getChildEntries() != null && !tuple.getChildEntries().isEmpty());

        if ( exists ) {
            // Remove the tuple so it can be readded to the top of the list
            memory.remove( tuple );
        }

        final boolean allowed = this.condition.isAllowed( tuple,
                                                          workingMemory );

        if ( allowed ) {
            memory.add( tuple );
            if ( !exists ) {
                this.sink.propagateAssertTuple( tuple,
                                                context,
                                                workingMemory );
            } else {
                tuple.modifyChildEntries( context,
                                          workingMemory );
            }
        } else {
            tuple.retractChildEntries( context,
                                       workingMemory );
        }
    }

    public void updateNewNode(final InternalWorkingMemory workingMemory,
                              final PropagationContext context) {
        this.attachingNewNode = true;

        final LinkedList memory = (LinkedList) workingMemory.getNodeMemory( this );

        for ( final Iterator it = memory.iterator(); it.hasNext(); ) {
            final ReteTuple tuple = (ReteTuple) it.next();
            this.sink.propagateNewTupleSink( (ReteTuple) tuple.getChildEntries().getFirst(),
                                             context,
                                             workingMemory );
        }
        this.attachingNewNode = false;
    }

    /**
     * Produce a debug string.
     * 
     * @return The debug string.
     */
    public String toString() {
        return "[ConditionNode: cond=" + this.condition + "]";
    }

    public int hashCode() {
        return this.tupleSource.hashCode() ^ this.condition.hashCode();
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || object.getClass() != EvalConditionNode.class ) {
            return false;
        }

        final EvalConditionNode other = (EvalConditionNode) object;

        return this.tupleSource.equals( other.tupleSource ) && this.condition.equals( other.condition );
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return new LinkedList();
    }

    /**
     * @inheritDoc
     */
    public List getPropagatedTuples(final InternalWorkingMemory workingMemory,
                                    final TupleSink sink) {
        // FIXME
        final LinkedList memory = (LinkedList) workingMemory.getNodeMemory( this );
        final List propagatedTuples = new ArrayList();

        for ( final Iterator it = memory.iterator(); it.hasNext(); ) {
            final ReteTuple leftTuple = (ReteTuple) it.next();
            final LinkedList linkedTuples = leftTuple.getChildEntries();

            final LinkedListEntry wrapper = (LinkedListEntry) linkedTuples.getFirst();
            propagatedTuples.add( wrapper.getObject() );
        }
        return propagatedTuples;
    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {
        // FIXME
    }

}
