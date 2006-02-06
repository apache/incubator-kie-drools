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
import java.util.List;
import java.util.Map;

import org.drools.spi.BetaNodeBinder;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;
import org.drools.util.LinkedListNodeWrapper;

/**
 * <code>NotNode</code> extends <code>BetaNode</code> to perform tests for the non existence of a Fact plus one or more conditions. Where none existence
 * is found the left ReteTuple is copied and propgated.  Further to this it maintains the "truth" by cancelling any <code>Activation<code>s that are nolonger 
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
    static int notAssertObject =0;
    static int notAssertTuple =0;
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
            ObjectSource rightInput)
    {
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
     * Assert a new <code>ReteTuple</code> from the left input. It iterates over the right <code>FactHandleImpl</code>'s if no matches are found the a copy of
     * the <code>ReteTuple</code> is made and propagated.
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
        
        memory.add( leftTuple );

        BetaNodeBinder binder = getJoinNodeBinder();

        for ( Iterator it = memory.rightObjectIterator(); it.hasNext(); ) {
            ObjectMatches objectMatches = (ObjectMatches) it.next();
            FactHandleImpl handle = objectMatches.getFactHandle();
            if ( binder.isAllowed( handle,
                                   leftTuple,
                                   workingMemory ) ) {
                TupleMatch tupleMatch = objectMatches.add( leftTuple );
                leftTuple.addMatch( handle,
                                    tupleMatch );
            }
        }

        if ( leftTuple.matchesSize() == 0 ) {
            for ( int i = 0, size = getTupleSinks().size(); i < size; i++ ) {
                ReteTuple joined = new ReteTuple( leftTuple );
                leftTuple.addLinkedTuple( new LinkedListNodeWrapper( joined ) );
                ((TupleSink) getTupleSinks().get( i )).assertTuple( joined,
                                                                    context,
                                                                    workingMemory );
            }
        }

    }

    /**
     * Assert a new <code>FactHandleImpl</code> from the right input. If it matches any left ReteTuple's that already has propagations
     * then those propagations are retracted.
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
        ObjectMatches objectMatches = memory.add( handle );

        BetaNodeBinder binder = getJoinNodeBinder();
        
        for ( ReteTuple leftTuple = memory.getFirstTuple(); leftTuple != null; leftTuple = (ReteTuple) leftTuple.getNext() ) {
            if ( binder.isAllowed( handle,
                                   leftTuple,
                                   workingMemory ) ) {
                TupleMatch tupleMatch = objectMatches.add( leftTuple );
                int previousSize = leftTuple.matchesSize();
                leftTuple.addMatch( handle,
                                    tupleMatch );

                if ( previousSize == 0 && leftTuple.matchesSize() != 0 ) {
                    LinkedList list = leftTuple.getLinkedTuples();
                    if ( list != null ) {
                        int i = 0;
                        for ( LinkedListNode node = list.removeFirst(); node != null; node = list.removeFirst() ) {
                            ((TupleSink) getTupleSinks().get( i++ )).retractTuple( (ReteTuple) ( (LinkedListNodeWrapper) node).getNode(),
                                                                                 context,
                                                                                 workingMemory );   
                        }
                    } 
                }
            }            
        }
    }

    /**
     * Retract the <code>FactHandleImpl</code>. If the handle has any <code>ReteTuple</code> matches then those matches 
     * copied are propagated as new joins.
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
        ObjectMatches objectMatches = memory.remove( handle );
        int k = 0;
        for ( TupleMatch tupleMatch = objectMatches.getFirstTupleMatch(); tupleMatch != null; tupleMatch = (TupleMatch) tupleMatch.getNext()) {
            ReteTuple leftTuple = tupleMatch.getTuple();
            int previousSize = leftTuple.matchesSize();
            leftTuple.removeMatch( handle );

            if ( previousSize != 0 && leftTuple.matchesSize() == 0 ) {
                for ( int i = 0, size = getTupleSinks().size(); i < size; i++ ) {
                    ReteTuple joined = new ReteTuple( leftTuple );
                    leftTuple.addLinkedTuple( new LinkedListNodeWrapper( joined ) );
                    ((TupleSink) getTupleSinks().get( i )).assertTuple( joined,
                                                                        context,
                                                                        workingMemory );
                }
            }
        }
    }
    
   /**
    * Retract the <code>ReteTuple<code>, any resulting proppagated joins are also retracted. 
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
        memory.remove( leftTuple );
        
        Map matches = leftTuple.getTupleMatches();

        if ( !matches.isEmpty() ) {
            for ( Iterator it = matches.values().iterator(); it.hasNext(); ) {
                TupleMatch tupleMatch = (TupleMatch) it.next();
                tupleMatch.getObjectMatches().remove( tupleMatch );
            }
        }
        
        LinkedList list = leftTuple.getLinkedTuples();
        if ( list != null && !list.isEmpty() ) {
            int i = 0;
            for ( LinkedListNode node = list.removeFirst(); node != null; node = list.removeFirst() ) {
                ((TupleSink) getTupleSinks().get( i++ )).retractTuple( (ReteTuple) ( (LinkedListNodeWrapper) node).getNode(),
                                                                     context,
                                                                     workingMemory );   
            }
        }                
    }    

}
