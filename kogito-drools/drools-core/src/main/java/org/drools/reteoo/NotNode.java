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
import org.drools.util.LinkedList;
import org.drools.util.LinkedListObjectWrapper;

/**
 * <code>NotNode</code> extends <code>BetaNode</code> to perform tests for
 * the non existence of a Fact plus one or more conditions. Where none existence
 * is found the left ReteTuple is copied and propgated. Further to this it
 * maintains the "truth" by cancelling any
 * <code>Activation<code>s that are nolonger 
 * considered true by the assertion of ReteTuple's or FactHandleImpl.  Tuples are considered to be asserted from the left input and facts from the right input.
 * The <code>BetaNode</code> provides the BetaMemory to store assserted ReteTuples and <code>FactHandleImpl<code>s. Each fact handle is stored in the right 
 * memory as a key in a <code>HashMap</code>, the value is an <code>ObjectMatches</code> instance which maintains a <code>LinkedList of <code>TuplesMatches - 
 * The tuples that are matched with the handle. the left memory is a <code>LinkedList</code> of <code>ReteTuples</code> which maintains a <code>HashMa</code>, 
 * where the keys are the matching <code>FactHandleImpl</code>s and the value is populated <code>TupleMatche</code>es, the keys are matched fact handles. 
 * <code>TupleMatch</code> maintains a <code>List</code> of resulting joins, where there is joined <code>ReteTuple</code> per <code>TupleSink</code>.
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class NotNode extends BetaNode {
    static int notAssertObject = 0;
    static int notAssertTuple  = 0;

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
    NotNode(int id,
            TupleSource leftInput,
            ObjectSource rightInput) {
        super( id,
               leftInput,
               rightInput,
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
    NotNode(int id,
            TupleSource leftInput,
            ObjectSource rightInput,
            BetaNodeBinder joinNodeBinder) {
        super( id,
               leftInput,
               rightInput,
               joinNodeBinder );
    }

    /**
     * Assert a new <code>ReteTuple</code> from the left input. It iterates
     * over the right <code>FactHandleImpl</code>'s if no matches are found
     * the a copy of the <code>ReteTuple</code> is made and propagated.
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
            attemptJoin( leftTuple,
                         handle,
                         objectMatches,
                         binder,
                         workingMemory );
        }

        if ( leftTuple.matchesSize() == 0 ) {
            propagateAssertTuple( leftTuple,
                                  context,
                                  workingMemory );
        }

    }

    /**
     * Assert a new <code>FactHandleImpl</code> from the right input. If it
     * matches any left ReteTuple's that already has propagations then those
     * propagations are retracted.
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
            int previousSize = leftTuple.matchesSize();
            attemptJoin( leftTuple,
                         handle,
                         objectMatches,
                         binder,
                         workingMemory );
            if ( previousSize == 0 && leftTuple.matchesSize() != 0 ) {
                propagateRetractTuple( leftTuple,
                                       context,
                                       workingMemory );
            }
        }
    }

    /**
     * Retract the <code>FactHandleImpl</code>. If the handle has any
     * <code>ReteTuple</code> matches then those matches copied are propagated
     * as new joins.
     * 
     * @param handle
     *            the <codeFactHandleImpl</code> being retracted
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     * @throws AssertionException
     */
    public void retractObject(FactHandleImpl handle,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        ObjectMatches objectMatches = memory.remove( workingMemory,
                                                     handle );

        for ( TupleMatch tupleMatch = objectMatches.getFirstTupleMatch(); tupleMatch != null; tupleMatch = (TupleMatch) tupleMatch.getNext() ) {
            ReteTuple leftTuple = tupleMatch.getTuple();
            int previousSize = leftTuple.matchesSize();
            leftTuple.removeMatch( handle );

            if ( previousSize != 0 && leftTuple.matchesSize() == 0 ) {
                propagateAssertTuple( leftTuple,
                                      context,
                                      workingMemory );
            }
        }
    }

    /**
     * Retract the
     * <code>ReteTuple<code>, any resulting proppagated joins are also retracted. 
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
            }
        }

        propagateRetractTuple( leftTuple,
                               context,
                               workingMemory );
    }

    public void modifyTuple(ReteTuple leftTuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // We remove the tuple as now its modified it needs to go to the top of
        // the stack, which is added back in else where
        memory.remove( workingMemory,
                       leftTuple );
        // ensure the tuple is at the top of the memory
        memory.add( workingMemory,
                    leftTuple );

        Map matches = leftTuple.getTupleMatches();

        int previous = matches.size();
        BetaNodeBinder binder = getJoinNodeBinder();

        for ( Iterator rightIterator = memory.rightObjectIterator( workingMemory,
                                                                   leftTuple ); rightIterator.hasNext(); ) {
            ObjectMatches objectMatches = (ObjectMatches) rightIterator.next();
            FactHandleImpl handle = objectMatches.getFactHandle();

            if ( binder.isAllowed( handle,
                                   leftTuple,
                                   workingMemory ) ) {
                // test passes
                TupleMatch tupleMatch = (TupleMatch) leftTuple.getTupleMatches().get( handle );
                if ( tupleMatch == null ) {
                    // no previous matches so add a match now
                    tupleMatch = objectMatches.add( leftTuple );
                    leftTuple.addTupleMatch( handle,
                                             tupleMatch );
                }
            } else {
                TupleMatch tupleMatch = leftTuple.removeMatch( handle );
                if ( tupleMatch != null ) {
                    // use to match and doesn't any more, so remove match
                    objectMatches.remove( tupleMatch );
                }
            }
        }
        if ( previous == 0 && leftTuple.matchesSize() == 0 ) {
            propagateModifyTuple( leftTuple,
                                  context,
                                  workingMemory );
        } else if ( previous != 0 && leftTuple.matchesSize() == 0 ) {
            propagateAssertTuple( leftTuple,
                                  context,
                                  workingMemory );
        } else if ( previous == 0 && leftTuple.matchesSize() != 0 ) {
            propagateRetractTuple( leftTuple,
                                   context,
                                   workingMemory );
        }
    }

    public void modifyObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // Remove the FactHandle from memory
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
                int previous = leftTuple.getTupleMatches().size();
                if ( !binder.isAllowed( handle,
                                        leftTuple,
                                        workingMemory ) ) {
                    leftTuple.removeMatch( handle );
                    objectMatches.remove( tupleMatch );
                }
                if ( previous == 0 && leftTuple.matchesSize() == 0 ) {
                    propagateModifyTuple( leftTuple,
                                          context,
                                          workingMemory );
                } else if ( previous != 0 && leftTuple.matchesSize() == 0 ) {
                    propagateAssertTuple( leftTuple,
                                          context,
                                          workingMemory );
                } else if ( previous == 0 && leftTuple.matchesSize() != 0 ) {
                    propagateRetractTuple( leftTuple,
                                           context,
                                           workingMemory );
                }

                tupleMatch = (TupleMatch) tupleMatch.getNext();
            } else {
                // no previous join, so attempt join now
                int previousSize = leftTuple.matchesSize();
                attemptJoin( leftTuple,
                             handle,
                             objectMatches,
                             binder,
                             workingMemory );
                if ( previousSize == 0 && leftTuple.matchesSize() != 0 ) {
                    propagateRetractTuple( leftTuple,
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
        for ( Iterator it = memory.getLeftTupleMemory().iterator(); it.hasNext(); ) {
            ReteTuple leftTuple = (ReteTuple) it.next();
            if ( leftTuple.matchesSize() == 0 ) {
                ReteTuple child = new ReteTuple( leftTuple );
                // no TupleMatch so instead add as a linked tuple
                leftTuple.addLinkedTuple( new LinkedListObjectWrapper( child ) );
                ((TupleSink) getTupleSinks().get( getTupleSinks().size() - 1 )).assertTuple( child,
                                                                    context,
                                                                    workingMemory );
            }
        }

        this.attachingNewNode = true;
    }
    
    /**
     * @inheritDoc
     */
    public List getPropagatedTuples(WorkingMemoryImpl workingMemory, TupleSink sink) {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        int index = this.getTupleSinks().indexOf( sink );
        List propagatedTuples = new ArrayList();

        for ( Iterator it = memory.getLeftTupleMemory().iterator(); it.hasNext(); ) {
            ReteTuple leftTuple = (ReteTuple) it.next();
            LinkedList linkedTuples = leftTuple.getLinkedTuples();
            
            LinkedListObjectWrapper wrapper = (LinkedListObjectWrapper) linkedTuples.getFirst();
            for( int i = 0; i < index; i++) {
                wrapper = (LinkedListObjectWrapper) wrapper.getNext();
            }
            propagatedTuples.add( wrapper.getObject() );
        }
        return propagatedTuples;
    }   
    

}
