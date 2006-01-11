package org.drools.reteoo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.AssertionException;
import org.drools.FactException;
import org.drools.RetractionException;
import org.drools.spi.PropagationContext;

public class NotNode extends BetaNode {
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
            ObjectSource rightInput,
            int column)// ,
    // BetaNodeDecorator decorator)
    {
        super( id,
               leftInput,
               rightInput,
               column,
               // decorator,
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
            int column,
            // BetaNodeDecorator decorator,
            BetaNodeBinder joinNodeBinder) {
        super( id,
               leftInput,
               rightInput,
               column,
               joinNodeBinder );
    }

    /**
     * Assert a new <code>Tuple</code> from the left input.
     * 
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param workingMemory
     *            The working memory seesion.
     * @throws AssertionException
     *             If an error occurs while asserting.
     */
    public void assertTuple(ReteTuple leftTuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) throws FactException {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        if ( !memory.contains( leftTuple.getKey() ) ) {
            TupleMatches tupleMatches = new TupleMatches( leftTuple );
            memory.put( leftTuple.getKey(),
                        tupleMatches );

            FactHandleImpl handle = null;

            BetaNodeBinder binder = getJoinNodeBinder();
            Iterator it = memory.getRightMemory().iterator();
            while ( it.hasNext() ) {
                handle = (FactHandleImpl) it.next();
                if ( binder.isAllowed( handle,
                                       leftTuple,
                                       workingMemory ) ) {
                    tupleMatches.addMatch( handle );
                }
            }

            if ( tupleMatches.getMatches().size() == 0 ) {
                TupleSet tupleSet = new TupleSet();
                tupleSet.addTuple( leftTuple );
                propagateAssertTuples( tupleSet,
                                       context,
                                       workingMemory );
            }

        }
    }

    /**
     * Assert a new <code>Tuple</code> from the right input.
     * 
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param workingMemory
     *            The working memory seesion.
     */
    public void assertObject(Object object,
                             FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) throws FactException {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        if ( !memory.contains( handle ) ) {
            memory.add( handle );

            TupleSet assertTupleSet = null;           
            List retractKeyList = null;
            
            BetaNodeBinder binder = getJoinNodeBinder();
            

            for ( Iterator it = memory.getLeftMemory().values().iterator(); it.hasNext(); ) {
                TupleMatches tupleMatches = (TupleMatches) it.next();
                int previousSize  = tupleMatches.getMatches().size();
                ReteTuple leftTuple = tupleMatches.getTuple();
                if ( binder.isAllowed( object,
                                       handle,
                                       leftTuple,
                                       workingMemory ) ) {
                    tupleMatches.addMatch( handle );
                }
                
                int size = tupleMatches.getMatches().size();
                
                if ( size == 0 ) {
                    if (assertTupleSet == null) {
                        assertTupleSet = new TupleSet();
                    }
                    assertTupleSet.addTuple( leftTuple );
                } else if ( previousSize == 0 && size == 1 ) {
                    // If we previously had size of 0 and now its one we need to remove any created activations                    
                    if (retractKeyList == null) {
                        retractKeyList = new ArrayList();
                    }
                    retractKeyList.add( leftTuple.getKey() );                       
                }                
            }

            if (assertTupleSet != null) {
                propagateAssertTuples( assertTupleSet,
                                       context,
                                       workingMemory );
            }

            if (retractKeyList != null) {
                propagateRetractTuples( retractKeyList,
                                        context,
                                        workingMemory );
            }            
        }
    }

    /**
     * Retract tuples.
     * 
     * @param key
     *            The tuple key.
     * @param workingMemory
     *            The working memory seesion.
     * @throws RetractionException
     *             If an error occurs while retracting.
     */
    public void retractTuples(TupleKey leftKey,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) throws FactException {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        if ( memory.remove( leftKey ) != null ) {
            List keys = new ArrayList( 1 );
            keys.add( leftKey );
            propagateRetractTuples( keys,
                                     context,
                                     workingMemory );
        }
    }

    /**
     * Retract tuples.
     * 
     * @param key
     *            The tuple key.
     * @param workingMemory
     *            The working memory seesion.
     * @throws AssertionException
     */
    public void retractObject(FactHandleImpl handle,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) throws AssertionException,
                                                              FactException {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        if ( memory.contains( handle ) ) {
            TupleMatches tupleMatches = null;
            TupleSet tupleSet = new TupleSet();
            memory.remove( handle );
            Iterator it = memory.getLeftMemory().values().iterator();

            while ( it.hasNext() ) {
                tupleMatches = (TupleMatches) it.next();
                tupleMatches.removeMatch( handle );

                if ( tupleMatches.getMatches().size() == 0 ) {
                    tupleSet.addTuple( tupleMatches.getTuple() );
                }
            }

            propagateAssertTuples( tupleSet,
                                   context,
                                   workingMemory );
        }
    }

}
