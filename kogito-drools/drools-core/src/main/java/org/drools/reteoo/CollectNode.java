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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.common.BetaNodeConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Collect;
import org.drools.spi.FieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;

/**
 * @author etirelli
 *
 */
public class CollectNode extends BetaNode
    implements
    TupleSink,
    ObjectSink {

    private static final long         serialVersionUID = -8321568626178187047L;

    private final Collect             collect;
    private final FieldConstraint[]   resultConstraints;
    private final BetaNodeConstraints resultsBinder;

    /**
     * Constructor.
     * 
     * @param id
     *            The id for the node
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     * @param collect
     *            The collect conditional element
     */
    CollectNode(final int id,
                final TupleSource leftInput,
                final ObjectSource rightInput,
                final Collect collect) {
        this( id,
              leftInput,
              rightInput,
              new FieldConstraint[0],
              new BetaNodeConstraints(),
              new BetaNodeConstraints(),
              collect );
    }

    /**
     * Constructor.
     * 
     * @param id
     *            The id for the node
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     * @param resultConstraints
     *            The alpha constraints to be applied to the resulting collection
     * @param sourceBinder
     *            The beta binder to be applied to the source facts
     * @param resultsBinder
     *            The beta binder to be applied to the resulting collection
     * @param collect
     *            The collect conditional element
     */
    public CollectNode(final int id,
                       final TupleSource leftInput,
                       final ObjectSource rightInput,
                       final FieldConstraint[] resultConstraints,
                       final BetaNodeConstraints sourceBinder,
                       final BetaNodeConstraints resultsBinder,
                       final Collect collect) {
        super( id,
               leftInput,
               rightInput,
               sourceBinder );
        this.resultsBinder = resultsBinder;
        this.resultConstraints = resultConstraints;
        this.collect = collect;
    }

    /**
     * @inheritDoc
     * 
     *  When a new tuple is asserted into a CollectNode, do this:
     *  
     *  1. Select all matching objects from right memory
     *  2. Add them to the resulting collection object
     *  3. Apply resultConstraints and resultsBinder to the resulting collection
     *  4. In case all of them evaluates to true do the following:
     *  4.1. Create a new InternalFactHandle for the resulting collection and add it to the tuple
     *  4.2. Propagate the tuple
     *  
     */
    public void assertTuple(ReteTuple leftTuple,
                            PropagationContext context,
                            InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        memory.add( workingMemory,
                    leftTuple );

        //final BetaNodeBinder binder = getJoinNodeBinder();

        Collection result = this.collect.instantiateResultObject();
        for ( final Iterator it = memory.rightObjectIterator( workingMemory,
                                                              leftTuple ); it.hasNext(); ) {
            final ObjectMatches objectMatches = (ObjectMatches) it.next();
            final InternalFactHandle handle = objectMatches.getFactHandle();

            if ( attemptJoin( leftTuple,
                              handle,
                              objectMatches,
                              this.resultsBinder,
                              workingMemory ) != null ) {
                result.add( handle.getObject() );
            }
        }

        // First alpha node filters
        boolean isAllowed = true;
        for ( int i = 0, length = this.resultConstraints.length; i < length; i++ ) {
            if ( !this.resultConstraints[i].isAllowed( result,
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
     * As the collect node will propagate the tuple,
     * but will recalculate the collection result object every time,
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
            // Need to store the collection result object for later disposal
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
     *  When a new object is asserted into a CollectNode, do this:
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
     * NOTE: a modify tuple for collect node is exactly the 
     * same as a retract+assert tuple, since the collection object changes.
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
