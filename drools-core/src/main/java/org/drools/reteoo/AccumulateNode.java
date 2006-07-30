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

package org.drools.reteoo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.common.BetaNodeBinder;
import org.drools.common.DefaultFactHandle;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListObjectWrapper;

/**
 * AccumulateNode
 * A beta node capable of doing accumulate logic.
 *
 * Created: 04/06/2006
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a> 
 *
 * @version $Id$
 */
public class AccumulateNode extends BetaNode {

    private static final long serialVersionUID = -4081578178269297948L;

    // MENTAL NOTE: it MUST be stateless
    private final Accumulator accumulator;

    /**
     * Construct.
     * 
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>TupleSource</code>.
     */
    AccumulateNode(final int id,
                   final TupleSource leftInput,
                   final ObjectSource rightInput,
                   final Accumulator accumulator) {
        this( id,
              leftInput,
              rightInput,
              accumulator,
              new BetaNodeBinder() );
    }

    /**
     * Construct.
     * 
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>TupleSource</code>.
     */
    AccumulateNode(final int id,
                   final TupleSource leftInput,
                   final ObjectSource rightInput,
                   final Accumulator accumulator,
                   final BetaNodeBinder joinNodeBinder) {
        super( id,
               leftInput,
               rightInput,
               joinNodeBinder );
        this.accumulator = accumulator;
    }

    /**
     * @inheritDoc
     * 
     *  When a new tuple is asserted into an AccumulateNode, do this:
     *  
     *  1. Select all matching objects from right memory
     *  2. Execute the initialization code using the tuple + matching objects
     *  3. Execute the accumulation code for each combination of tuple+object
     *  4. Execute the return code
     *  5. Create a new CalculatedObjectHandle for the resulting object and add it to the tuple
     *  6. Propagate the tuple
     *  
     *  The initialization, accumulation and return codes, in JBRules, are assembled
     *  into a generated method code and called once for the whole match, as you can see
     *  bellow:
     *  
     *   Object result = this.accumulator.accumulate( ... );
     *  
     */
    public void assertTuple(ReteTuple leftTuple,
                            PropagationContext context,
                            ReteooWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        memory.add( workingMemory,
                    leftTuple );

        final BetaNodeBinder binder = getJoinNodeBinder();

        List matchingObjects = new ArrayList();
        for ( final Iterator it = memory.rightObjectIterator( workingMemory,
                                                              leftTuple ); it.hasNext(); ) {
            final ObjectMatches objectMatches = (ObjectMatches) it.next();
            final DefaultFactHandle handle = objectMatches.getFactHandle();

            if ( attemptJoin( leftTuple, 
                              handle, 
                              objectMatches, 
                              binder, 
                              workingMemory ) != null ) {
                matchingObjects.add( handle.getObject() );
            }
        }

        Object result = this.accumulator.accumulate( leftTuple,
                                                     matchingObjects,
                                                     workingMemory );

        DefaultFactHandle handle = new CalculatedObjectHandle( result );

        propagateAssertTuple( leftTuple,
                              handle,
                              context,
                              workingMemory );
    }

    /**
     * @inheritDoc
     * 
     * As the accumulate node will always propagate the tuple,
     * but will recalculate the accumulated result object every time,
     * a modify is really a retract + assert. 
     * 
     */
    public void modifyTuple(ReteTuple leftTuple,
                            PropagationContext context,
                            ReteooWorkingMemory workingMemory) {
        
        this.retractTuple( leftTuple, context, workingMemory );
        this.assertTuple( leftTuple, context, workingMemory );

    }

    /**
     * @inheritDoc
     * 
     * As the accumulate node will always propagate the tuple,
     * it must always also retreat it.
     * 
     */
    public void retractTuple(ReteTuple leftTuple,
                             PropagationContext context,
                             ReteooWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        memory.remove( workingMemory,
                       leftTuple );

        final Map matches = leftTuple.getTupleMatches();

        if ( !matches.isEmpty() ) {
            for ( final Iterator it = matches.values().iterator(); it.hasNext(); ) {
                final TupleMatch tupleMatch = (TupleMatch) it.next();
                tupleMatch.getObjectMatches().remove( tupleMatch );
            }
        }
        
        propagateRetractTuple( leftTuple,
                               context,
                               workingMemory );
    }

    /**
     * @inheritDoc
     * 
     *  When a new object is asserted into an AccumulateNode, do this:
     *  
     *  1. Select all matching tuples from left memory
     *  2. For each matching tuple, call a modify tuple
     *  
     */
    public void assertObject(DefaultFactHandle handle,
                             PropagationContext context,
                             ReteooWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        ObjectMatches objectMatches = memory.add( workingMemory,
                                                  handle );

        final BetaNodeBinder binder = getJoinNodeBinder();
        for ( final Iterator it = memory.leftTupleIterator( workingMemory,
                                                            handle ); it.hasNext(); ) {
            final ReteTuple leftTuple = (ReteTuple) it.next();

            if ( attemptJoin( leftTuple, 
                              handle, 
                              objectMatches, 
                              binder, 
                              workingMemory ) != null ) {
                this.modifyTuple( leftTuple,
                                  context,
                                  workingMemory );
            }
        }

    }

    /**
     * @inheritDoc
     * 
     * If an object is modified, iterate over all matching tuples
     * and propagate a modify tuple for them.
     * 
     * NOTE: a modify tuple for accumulate node is exactly the 
     * same as a retract+assert tuple, since the calculated object changes.
     * So, a modify object is in fact a retract+assert object.
     * 
     */
    public void modifyObject(DefaultFactHandle handle,
                             PropagationContext context,
                             ReteooWorkingMemory workingMemory) {
        this.retractObject( handle,
                            context,
                            workingMemory );
        this.assertObject( handle,
                           context,
                           workingMemory );
    }

    /**
     *  @inheritDoc
     *  
     *  If an object is retract, call modify tuple for each
     *  tuple match.
     */
    public void retractObject(DefaultFactHandle handle,
                              PropagationContext context,
                              ReteooWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // Remove the FactHandle from memory
        final ObjectMatches objectMatches = memory.remove( workingMemory,
                                                           handle );

        for ( TupleMatch tupleMatch = objectMatches.getFirstTupleMatch(); tupleMatch != null; tupleMatch = (TupleMatch) tupleMatch.getNext() ) {
            final ReteTuple leftTuple = tupleMatch.getTuple();
            leftTuple.removeMatch( handle );

            this.modifyTuple( leftTuple,
                              context,
                              workingMemory );
        }
    }

    /**
     * @inheritDoc
     */
    public List getPropagatedTuples(ReteooWorkingMemory workingMemory,
                                    TupleSink sink) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        final int index = this.getTupleSinks().indexOf( sink );
        final List propagatedTuples = new ArrayList();

        for ( final Iterator it = memory.getLeftTupleMemory().iterator(); it.hasNext(); ) {
            final ReteTuple leftTuple = (ReteTuple) it.next();
            final LinkedList linkedTuples = leftTuple.getLinkedTuples();

            LinkedListObjectWrapper wrapper = (LinkedListObjectWrapper) linkedTuples.getFirst();
            for ( int i = 0; i < index; i++ ) {
                wrapper = (LinkedListObjectWrapper) wrapper.getNext();
            }
            propagatedTuples.add( wrapper.getObject() );
        }
        return propagatedTuples;
    }

    /**
     * @inheritDoc
     */
    public void updateNewNode(ReteooWorkingMemory workingMemory,
                              PropagationContext context) {
        this.attachingNewNode = true;

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        for ( final Iterator it = memory.getLeftTupleMemory().iterator(); it.hasNext(); ) {
            final ReteTuple leftTuple = (ReteTuple) it.next();
            final ReteTuple child = new ReteTuple( leftTuple );
            leftTuple.addLinkedTuple( child );
            ((TupleSink) getTupleSinks().get( getTupleSinks().size() - 1 )).assertTuple( child,
                                                                                         context,
                                                                                         workingMemory );
        }
        this.attachingNewNode = false;
    }

}
