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

import java.util.Iterator;

import org.drools.rule.EvalCondition;
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
    EvalConditionNode(int id,
                      TupleSource tupleSource,
                      EvalCondition eval) {
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
    
    public void attach(WorkingMemoryImpl[] workingMemories, PropagationContext context) {
        attach();
        
        for (int i = 0, length = 0; i < length; i++) { 
            this.tupleSource.updateNewNode( workingMemories[i], context );
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
    public void assertTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {

        boolean allowed = this.condition.isAllowed( tuple,
                                                    workingMemory );

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
    }

    public void retractTuple(ReteTuple tuple,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        LinkedList memory = (LinkedList) workingMemory.getNodeMemory( this );

        memory.remove( tuple );

        propagateRetractTuple( tuple,
                               context,
                               workingMemory );
    }

    public void modifyTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {
        LinkedList memory = (LinkedList) workingMemory.getNodeMemory( this );
        boolean exists = (tuple.getPrevious() == null && tuple.getNext() == null);
        if ( exists ) {
            // Remove the tuple so it can be readded to the top of the list
            memory.remove( tuple );
        }

        boolean allowed = this.condition.isAllowed( tuple,
                                                    workingMemory );

        workingMemory.getReteooNodeEventSupport().propagateReteooNode( this,
                                                                       tuple,
                                                                       allowed );

        if ( allowed ) {
            memory.add( tuple );
            if ( exists ) {
                propagateAssertTuple( tuple,
                                      context,
                                      workingMemory );
            } else {
                propagateModifyTuple( tuple,
                                      context,
                                      workingMemory );
            }
        } else {
            propagateRetractTuple( tuple,
                                   context,
                                   workingMemory );
        }
    }

    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) {
        this.attachingNewNode = true;

        LinkedList memory = (LinkedList) workingMemory.getNodeMemory( this );
        
        for ( Iterator it = memory.iterator(); it.hasNext(); ) {
            propagateAssertTuple( (ReteTuple) it.next(),
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

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        EvalConditionNode other = (EvalConditionNode) object;

        return this.tupleSource.equals( other.tupleSource ) && this.condition.equals( other.condition );
    }

    public void remove(BaseNode node,
                       WorkingMemoryImpl workingMemory,
                       PropagationContext context) {
        getTupleSinks().remove( node );
        removeShare();
        if ( this.sharedCount < 0 ) {
            workingMemory.clearNodeMemory( this );
            this.tupleSource.remove( this,
                                     workingMemory,
                                     context );
        }
    }

    public Object createMemory() {
        return new LinkedList();
    }
}
