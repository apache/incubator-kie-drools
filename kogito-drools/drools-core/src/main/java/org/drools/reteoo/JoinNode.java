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
import java.util.Map;

import org.drools.common.BetaNodeBinder;
import org.drools.spi.PropagationContext;

/**
 * <code>JoinNode</code> extends <code>BetaNode</code> to perform
 * <code>ReteTuple</code> and <code>FactHandle</code> joins. Tuples are
 * considered to be asserted from the left input and facts from the right input.
 * The <code>BetaNode</code> provides the BetaMemory to store assserted
 * ReteTuples and
 * <code>FactHandleImpl<code>s. Each fact handle is stored in the right memory as a key in a <code>HashMap</code>, the value is an <code>ObjectMatches</code> 
 * instance which maintains a <code>LinkedList of <code>TuplesMatches - The tuples that are matched with the handle. the left memory is a <code>LinkedList</code> 
 * of <code>ReteTuples</code> which maintains a <code>HashMa</code>, where the keys are the matching <code>FactHandleImpl</code>s and the value is 
 * populated <code>TupleMatche</code>es, the keys are matched fact handles. <code>TupleMatch</code> maintains a <code>List</code> of resulting joins, 
 * where there is joined <code>ReteTuple</code> per <code>TupleSink</code>.
 *  
 * 
 * The BetaNode provides
 * the BetaMemory which stores the 
 * 
 * @see BetaNode
 * @see ObjectMatches
 * @see TupleMatch
 * @see TupleSink
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
class JoinNode extends BetaNode {
    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>TupleSource</code>.
     */
    JoinNode(int id,
             TupleSource leftInput,
             ObjectSource rightInput) {
        super( id,
               leftInput,
               rightInput );
    }

    JoinNode(int id,
             TupleSource leftInput,
             ObjectSource rightInput,
             BetaNodeBinder binder) {
        super( id,
               leftInput,
               rightInput,
               binder );
    }

    /**
     * Assert a new <code>ReteTuple</code>. The right input of
     * <code>FactHandleInput</code>'s is iterated and joins attemped, via the
     * binder, any successful bindings results in joined tuples being created
     * and propaged. there is a joined tuple per TupleSink.
     * 
     * @see ReteTuple
     * @see ObjectMatches
     * @see TupleSink
     * @see TupleMatch
     * 
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
    public void assertTuple(ReteTuple leftTuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        memory.add( workingMemory,
                    leftTuple );

        BetaNodeBinder binder = getJoinNodeBinder();

        for ( Iterator it = memory.rightObjectIterator( workingMemory,
                                                        leftTuple ); it.hasNext(); ) {
            ObjectMatches objectMatches = (ObjectMatches) it.next();
            FactHandleImpl handle = objectMatches.getFactHandle();
            TupleMatch tupleMatch = attemptJoin( leftTuple,
                                                 handle,
                                                 objectMatches,
                                                 binder,
                                                 workingMemory );
            if ( tupleMatch != null ) {
                propagateAssertTuple( new ReteTuple( leftTuple,
                                                     handle ),
                                      tupleMatch,
                                      context,
                                      workingMemory );
            }
        }
    }

    /**
     * Assert a new <code>FactHandleImpl</code>. The left input of
     * <code>ReteTuple</code>s is iterated and joins attemped, via the
     * binder, any successful bindings results in joined tuples being created
     * and propaged. there is a joined tuple per TupleSink.
     * 
     * @see ReteTuple
     * @see ObjectMatches
     * @see TupleSink
     * @see TupleMatch
     * 
     * @param handle
     *            The <code>FactHandleImpl</code> being asserted.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
    public void assertObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        ObjectMatches objectMatches = memory.add( workingMemory,
                                                  handle );

        BetaNodeBinder binder = getJoinNodeBinder();
        for ( Iterator it = memory.leftTupleIterator( workingMemory,
                                                      handle ); it.hasNext(); ) {
            ReteTuple leftTuple = (ReteTuple) it.next();
            TupleMatch tupleMatch = attemptJoin( leftTuple,
                                                 handle,
                                                 objectMatches,
                                                 binder,
                                                 workingMemory );
            if ( tupleMatch != null ) {
                propagateAssertTuple( new ReteTuple( leftTuple,
                                                     handle ),
                                      tupleMatch,
                                      context,
                                      workingMemory );
            }
        }
    }

    /**
     * Retract a FactHandleImpl. Iterates the referenced TupleMatches stored in
     * the handle's ObjectMatches retracting joined tuples.
     * 
     * @param handle
     *            the <codeFactHandleImpl</code> being retracted
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
    public void retractObject(FactHandleImpl handle,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // Remove the FactHandle from memory
        ObjectMatches objectMatches = memory.remove( workingMemory,
                                                     handle );

        for ( TupleMatch tupleMatch = objectMatches.getFirstTupleMatch(); tupleMatch != null; tupleMatch = (TupleMatch) tupleMatch.getNext() ) {
            ReteTuple leftTuple = tupleMatch.getTuple();
            leftTuple.removeMatch( handle );
            propagateRetractTuple( tupleMatch,
                                   context,
                                   workingMemory );
        }
    }

    /**
     * Retract a <code>ReteTuple</code>. Iterates the referenced
     * <code>TupleMatche</code>'s stored in the tuples <code>Map</code>
     * retracting all joined tuples.
     * 
     * @param key
     *            The tuple key.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
    public void retractTuple(ReteTuple leftTuple,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {

        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        memory.remove( workingMemory,
                       leftTuple );

        Map matches = leftTuple.getTupleMatches();

        if ( !matches.isEmpty() ) {
            for ( Iterator it = matches.values().iterator(); it.hasNext(); ) {
                TupleMatch tupleMatch = (TupleMatch) it.next();
                tupleMatch.getObjectMatches().remove( tupleMatch );
                propagateRetractTuple( tupleMatch,
                                       context,
                                       workingMemory );
            }
        }
    }

    public void modifyTuple(ReteTuple leftTuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // We remove the tuple as now its modified it needs to go to the top of
        // the stack, which is added back in else where
        memory.remove( workingMemory,
                       leftTuple );

        Map matches = leftTuple.getTupleMatches();

        if ( matches.isEmpty() ) {
            // No child propagations, so try as a new assert, will ensure the
            // tuple is added to the top of the memory
            assertTuple( leftTuple,
                         context,
                         workingMemory );
        } else {
            // ensure the tuple is at the top of the memory
            memory.add( workingMemory,
                        leftTuple );
            BetaNodeBinder binder = getJoinNodeBinder();

            for ( Iterator rightIterator = memory.rightObjectIterator( workingMemory,
                                                                       leftTuple ); rightIterator.hasNext(); ) {
                ObjectMatches objectMatches = (ObjectMatches) rightIterator.next();
                FactHandleImpl handle = objectMatches.getFactHandle();

                if ( binder.isAllowed( handle,
                                       leftTuple,
                                       workingMemory ) ) {
                    TupleMatch tupleMatch = (TupleMatch) leftTuple.getTupleMatches().get( handle );
                    if ( tupleMatch != null ) {
                        propagateModifyTuple( tupleMatch,
                                              context,
                                              workingMemory );
                    } else {
                        tupleMatch = objectMatches.add( leftTuple );
                        leftTuple.addTupleMatch( handle,
                                                 tupleMatch );
                        propagateAssertTuple( new ReteTuple( leftTuple,
                                                             handle ),
                                              tupleMatch,
                                              context,
                                              workingMemory );
                    }

                } else {
                    TupleMatch tupleMatch = leftTuple.removeMatch( handle );
                    if ( tupleMatch != null ) {
                        objectMatches.remove( tupleMatch );
                        propagateRetractTuple( tupleMatch,
                                               context,
                                               workingMemory );
                    }
                }
            }
        }
    }

    public void modifyObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // Remove and re-add the FactHandle from memory, ensures that its the latest on the list
        ObjectMatches objectMatches = memory.remove( workingMemory,
                                                     handle );
        memory.add( workingMemory,
                    objectMatches );

        TupleMatch tupleMatch = objectMatches.getFirstTupleMatch();
        BetaNodeBinder binder = getJoinNodeBinder();

        for ( Iterator it = memory.leftTupleIterator( workingMemory,
                                                      handle ); it.hasNext(); ) {
            ReteTuple leftTuple = (ReteTuple) it.next();
            if ( tupleMatch != null && tupleMatch.getTuple() == leftTuple ) {
                // has previous match so need to decide whether to continue
                // modify or retract                
                if ( binder.isAllowed( handle,
                                       leftTuple,
                                       workingMemory ) ) {
                    propagateModifyTuple( tupleMatch,
                                          context,
                                          workingMemory );
                } else {
                    leftTuple.removeMatch( handle );
                    objectMatches.remove( tupleMatch );
                    propagateRetractTuple( tupleMatch,
                                           context,
                                           workingMemory );
                }
                tupleMatch = (TupleMatch) tupleMatch.getNext();
            } else {
                // no previous join, so attempt join now
                TupleMatch newTupleMatch = attemptJoin( leftTuple,
                                                        handle,
                                                        objectMatches,
                                                        binder,
                                                        workingMemory );
                if ( newTupleMatch != null ) {
                    propagateAssertTuple( new ReteTuple( leftTuple,
                                                         handle ),
                                          newTupleMatch,
                                          context,
                                          workingMemory );
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) {
        this.attachingNewNode = true;

        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        for ( Iterator it = memory.getRightObjectMemory().iterator(); it.hasNext(); ) {
            ObjectMatches objectMatches = (ObjectMatches) it.next();
            FactHandleImpl handle = objectMatches.getFactHandle();
            for ( TupleMatch tupleMatch = objectMatches.getFirstTupleMatch(); tupleMatch != null; tupleMatch = (TupleMatch) tupleMatch.getNext() ) {
                ReteTuple tuple = new ReteTuple( tupleMatch.getTuple(),
                                                 handle );
                TupleSink sink = (TupleSink) this.tupleSinks.get( this.tupleSinks.size() - 1 );
                if ( sink != null ) {
                    tupleMatch.addJoinedTuple( tuple );
                    sink.assertTuple( tuple,
                                      context,
                                      workingMemory );
                } else {
                    throw new RuntimeException( "Possible BUG: trying to propagate an assert to a node that was the last added node" );
                }
            }
        }

        this.attachingNewNode = false;
    }
    
    /**
     * @inheritDoc
     */
    public List getPropagatedTuples(WorkingMemoryImpl workingMemory, TupleSink sink) {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        int index = this.getTupleSinks().indexOf( sink );
        List propagatedTuples = new ArrayList();

        for ( Iterator it = memory.getRightObjectMemory().iterator(); it.hasNext(); ) {
            ObjectMatches objectMatches = (ObjectMatches) it.next();
            for ( TupleMatch tupleMatch = objectMatches.getFirstTupleMatch(); tupleMatch != null; tupleMatch = (TupleMatch) tupleMatch.getNext() ) {
                propagatedTuples.add( tupleMatch.getJoinedTuples().get( index ) );
            }
        }
        return propagatedTuples;
    }

}
