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

import org.drools.spi.Condition;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;

/**
 * Node which filters <code>ReteTuple</code>s.
 * 
 * <p>
 * Using a semantic <code>Test</code>, this node may allow or disallow
 * <code>Tuples</code> to proceed further through the Rete-OO network.
 * </p>
 * 
 * @see TestNode
 * @see Condition
 * @see ReteTuple
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
class TestNode extends TupleSource
    implements
    TupleSink,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The semantic <code>Test</code>. */
    private final Condition   condition;

    /** The source of incoming <code>Tuples</code>. */
    private final TupleSource tupleSource;

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
     * @param condition
     */
    TestNode(int id,
             TupleSource tupleSource,
             Condition condition,
             boolean hasMemory) {
        super( id );
        this.condition = condition;
        this.tupleSource = tupleSource;
        this.hasMemory = hasMemory;
    }

    /**
     * Attaches this node into the network.
     */
    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the <code>Test</code> associated with this node.
     * 
     * @return The <code>Test</code>.
     */
    public Condition getCondition() {
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
    public void assertTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {

        if ( hasMemory() ) {
            boolean allowed = this.condition.isAllowed( tuple );

            workingMemory.getReteooNodeEventSupport().propagateReteooNode( this,
                                                                           tuple,
                                                                           allowed );

            if ( allowed ) {
                LinkedList memory = (LinkedList) workingMemory.getNodeMemory( this );
                memory.add( tuple );
                propagateAssertTuple( tuple,
                                      context,
                                      workingMemory );
            }
        } else {
            boolean allowed = this.condition.isAllowed( tuple );

            workingMemory.getReteooNodeEventSupport().propagateReteooNode( this,
                                                                           tuple,
                                                                           allowed );

            if ( allowed ) {
                propagateAssertTuple( tuple,
                                      context,
                                      workingMemory );
            }
        }
    }

    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) {
        this.attachingNewNode = true;
        if ( hasMemory() ) {
            //            LinkedList memory = (LinkedList) workingMemory.getNodeMemory( this );
            //            for ( Iterator it = memory.iterator(null, null); it.hasNext(); ) {
            //                propagateAssertTuple( (ReteTuple) it.next(),
            //                                      context,
            //                                      workingMemory );
            //            }
        } else {
            // We need to detach and re-attach to make sure the node is at the
            // top
            // for the propagation
            this.tupleSource.removeTupleSink( this );
            this.tupleSource.addTupleSink( this );
            this.tupleSource.updateNewNode( workingMemory,
                                            context );
        }

        this.attachingNewNode = false;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // java.lang.Object
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

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

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        TestNode other = (TestNode) object;

        return this.tupleSource.equals( other.tupleSource ) && this.condition.equals( other.condition );
    }

    public void remove() {
        // TODO Auto-generated method stub

    }

    public Object createMemory() {
        return new LinkedList();
    }

    public void retractTuple(ReteTuple tuple,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        // TODO Auto-generated method stub

    }
}
