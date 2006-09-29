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

import org.drools.common.BetaNodeConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Accumulate;
import org.drools.spi.FieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;

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

    private static final long         serialVersionUID = -4081578178269297948L;

    private final Accumulate          accumulate;
    private final FieldConstraint[]   constraints;
    private final BetaNodeConstraints resultsBinder;

    /**
     * Construct.
     * 
     * @param id
     *            The id for the node
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     * @param accumulate
     *            The accumulate conditional element
     */
    AccumulateNode(final int id,
                   final TupleSource leftInput,
                   final ObjectSource rightInput,
                   final Accumulate accumulate) {
        this( id,
              leftInput,
              rightInput,
              new FieldConstraint[0],
              new BetaNodeConstraints(),
              new BetaNodeConstraints(),
              accumulate );
    }

    public AccumulateNode(final int id,
                          final TupleSource leftInput,
                          final ObjectSource rightInput,
                          final FieldConstraint[] constraints,
                          final BetaNodeConstraints sourceBinder,
                          final BetaNodeConstraints resultsBinder,
                          final Accumulate accumulate) {
        super( id,
               leftInput,
               rightInput,
               sourceBinder );
        this.resultsBinder = resultsBinder;
        this.constraints = constraints;
        this.accumulate = accumulate;
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
                            InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        memory.add( workingMemory,
                    leftTuple );

        //final BetaNodeBinder binder = getJoinNodeBinder();

        List matchingObjects = new ArrayList();
        for ( final Iterator it = memory.rightObjectIterator( workingMemory,
                                                              leftTuple ); it.hasNext(); ) {
            final ObjectMatches objectMatches = (ObjectMatches) it.next();
            final InternalFactHandle handle = objectMatches.getFactHandle();

            if ( attemptJoin( leftTuple,
                              handle,
                              objectMatches,
                              this.resultsBinder,
                              workingMemory ) != null ) {
                matchingObjects.add( handle.getObject() );
            }
        }

        Object result = this.accumulate.accumulate( leftTuple,
                                                    matchingObjects,
                                                    workingMemory );

        // First alpha node filters
        boolean isAllowed = true;
        for ( int i = 0, length = this.constraints.length; i < length; i++ ) {
            if ( !this.constraints[i].isAllowed( result,
                                                 leftTuple,
                                                 workingMemory ) ) {
                isAllowed = false;
                break;
            }
        }
        if ( isAllowed ) {
            InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( result );

            if ( this.resultsBinder.isAllowed( handle,
                                               leftTuple,
                                               workingMemory ) ) {
                this.sink.propagateAssertTuple( leftTuple,
                                                handle,
                                                context,
                                                workingMemory );
            }
        }
    }

    /**
     * @inheritDoc
     * 
     * As the accumulate node will propagate the tuple,
     * but will recalculate the accumulated result object every time,
     * a modify is really a retract + assert. 
     * 
     */
    public void modifyTuple(ReteTuple leftTuple,
                            PropagationContext context,
                            InternalWorkingMemory workingMemory) {

        this.retractTuple( leftTuple,
                           context,
                           workingMemory );
        this.assertTuple( leftTuple,
                          context,
                          workingMemory );

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
                             InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        memory.remove( workingMemory,
                       leftTuple );

        final Map matches = leftTuple.getTupleMatches();

        if ( !matches.isEmpty() ) {
            for ( final Iterator it = matches.values().iterator(); it.hasNext(); ) {
                final CompositeTupleMatch compositeTupleMatch = (CompositeTupleMatch) it.next();
                compositeTupleMatch.getObjectMatches().remove( compositeTupleMatch );
                it.remove();
            }
        }

        // if tuple was propagated
        if ( (leftTuple.getChildEntries() != null) && (leftTuple.getChildEntries().size() > 0) ) {
            // Need to store the accumulate result object for later disposal
            InternalFactHandle lastHandle = ((ReteTuple) ((LinkedListEntry) leftTuple.getChildEntries().getFirst()).getObject()).getLastHandle();

            leftTuple.retractChildEntries( context,
                                           workingMemory );

            // Destroying the acumulate result object 
            workingMemory.getFactHandleFactory().destroyFactHandle( lastHandle );
        }
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
    public void assertObject(InternalFactHandle handle,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        memory.add( workingMemory,
                    handle );

        final BetaNodeConstraints binder = constraints();
        for ( final Iterator it = memory.leftTupleIterator( workingMemory,
                                                            handle ); it.hasNext(); ) {
            final ReteTuple leftTuple = (ReteTuple) it.next();

            if ( binder.isAllowed( handle,
                                   leftTuple,
                                   workingMemory ) ) {
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
    public void modifyObject(InternalFactHandle handle,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // Remove the FactHandle from memory
        final ObjectMatches objectMatches = memory.remove( workingMemory,
                                                           handle );

        // remove references from tuple to the handle
        for ( CompositeTupleMatch compositeTupleMatch = objectMatches.getFirstTupleMatch(); compositeTupleMatch != null; compositeTupleMatch = (CompositeTupleMatch) compositeTupleMatch.getNext() ) {
            final ReteTuple leftTuple = compositeTupleMatch.getTuple();
            leftTuple.removeMatch( handle );
        }

        // reassert object modifying appropriate tuples
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
    public void retractObject(InternalFactHandle handle,
                              PropagationContext context,
                              InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // Remove the FactHandle from memory
        final ObjectMatches objectMatches = memory.remove( workingMemory,
                                                           handle );

        for ( CompositeTupleMatch compositeTupleMatch = objectMatches.getFirstTupleMatch(); compositeTupleMatch != null; compositeTupleMatch = (CompositeTupleMatch) compositeTupleMatch.getNext() ) {
            final ReteTuple leftTuple = compositeTupleMatch.getTuple();
            leftTuple.removeMatch( handle );

            this.modifyTuple( leftTuple,
                              context,
                              workingMemory );
        }
    }

    /**
     * @inheritDoc
     */
    public List getPropagatedTuples(InternalWorkingMemory workingMemory,
                                    TupleSink sink) {
        // FIXME
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        final List propagatedTuples = new ArrayList();

        for ( final Iterator it = memory.getLeftTupleMemory().iterator(); it.hasNext(); ) {
            final ReteTuple leftTuple = (ReteTuple) it.next();
            final LinkedList linkedTuples = leftTuple.getChildEntries();

            LinkedListEntry wrapper = (LinkedListEntry) linkedTuples.getFirst();
            propagatedTuples.add( wrapper.getObject() );
        }
        return propagatedTuples;
    }

    /**
     * @inheritDoc
     */
    public void updateNewNode(InternalWorkingMemory workingMemory,
                              PropagationContext context) {
        // FIXME
        this.attachingNewNode = true;

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        for ( final Iterator it = memory.getLeftTupleMemory().iterator(); it.hasNext(); ) {
            final ReteTuple leftTuple = (ReteTuple) it.next();
            this.sink.propagateNewTupleSink( (ReteTuple) leftTuple.getChildEntries().getFirst(),
                                             context,
                                             workingMemory );
        }
        this.attachingNewNode = false;
    }

    public String toString() {
        return "[ " + this.getClass().getName() + "(" + this.id + ") ]";
    }

}
