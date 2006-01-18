package org.drools.reteoo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.AssertionException;
import org.drools.FactException;
import org.drools.RetractionException;
import org.drools.spi.PropagationContext;

public class JoinNode extends BetaNode {
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
    JoinNode(int id,
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
        
        TupleMatches tupleMatches = new TupleMatches( leftTuple );
        memory.put( leftTuple.getKey(),
                    tupleMatches );

        int column = getColumn();
        FactHandleImpl handle = null;
        ReteTuple merged = null;
        TupleSet tupleSet = new TupleSet();
        BetaNodeBinder binder = getJoinNodeBinder();
        Iterator it = memory.getRightMemory().iterator();
        while ( it.hasNext() ) {
            handle = (FactHandleImpl) it.next();
            if ( binder.isAllowed( handle,
                                   leftTuple,
                                   workingMemory ) ) {
                tupleMatches.addMatch( handle );

                merged = new ReteTuple( leftTuple,
                                        new ReteTuple( column,
                                                       handle,
                                                       workingMemory ) );
                tupleSet.addTuple( merged );
            }
        }

        propagateAssertTuples( tupleSet,
                               context,
                               workingMemory );
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
        memory.add( handle );

        ReteTuple leftTuple = null;
        TupleMatches tupleMatches = null;
        ReteTuple merged = null;

        ReteTuple rightTuple = new ReteTuple( getColumn(),
                                              handle,
                                              workingMemory );
        TupleSet tupleSet = new TupleSet();
        BetaNodeBinder binder = getJoinNodeBinder();
        Iterator it = memory.getLeftMemory().values().iterator();

        while ( it.hasNext() ) {
            tupleMatches = (TupleMatches) it.next();
            leftTuple = tupleMatches.getTuple();
            if ( binder.isAllowed( object,
                                   handle,
                                   leftTuple,
                                   workingMemory ) ) {
                tupleMatches.addMatch( handle );
                merged = new ReteTuple( leftTuple,
                                        rightTuple );
                tupleSet.addTuple( merged );
            }
        }

        propagateAssertTuples( tupleSet,
                               context,
                               workingMemory );
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

        if ( memory.contains( leftKey ) ) {
            FactHandleImpl handle = null;
            List keys = new ArrayList();
            TupleMatches tupleMatches = (TupleMatches) memory.remove( leftKey );
            int column = getColumn();
            Iterator it = tupleMatches.getMatches().iterator();
            while ( it.hasNext() ) {
                handle = (FactHandleImpl) it.next();
                keys.add( new TupleKey( leftKey,
                                        new TupleKey( column,
                                                      handle ) ) );
            }
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
     */
    public void retractObject(FactHandleImpl handle,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) throws FactException {
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        if ( memory.contains( handle ) ) {
            TupleMatches tupleMatches = null;
            List keys = new ArrayList();
            memory.remove( handle );
            Iterator it = memory.getLeftMemory().values().iterator();

            TupleKey rightKey = new TupleKey( getColumn(),
                                              handle );
            while ( it.hasNext() ) {
                tupleMatches = (TupleMatches) it.next();
                if ( tupleMatches.matched( handle ) ) {
                    tupleMatches.removeMatch( handle );
                    keys.add( new TupleKey( tupleMatches.getKey(),
                                            rightKey ) );
                }
            }
            propagateRetractTuples( keys,
                                     context,
                                     workingMemory );
        }
    }

    public void remove() {
        // TODO Auto-generated method stub

    }

}
