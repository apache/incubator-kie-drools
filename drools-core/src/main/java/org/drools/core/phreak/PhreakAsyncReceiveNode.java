package org.drools.core.phreak;

import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.AsyncReceiveNode.AsyncReceiveMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.base.rule.ContextEntry;
import org.drools.core.util.index.TupleList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.core.phreak.PhreakAsyncSendNode.isAllowed;

public class PhreakAsyncReceiveNode {
    private static final Logger log = LoggerFactory.getLogger( PhreakAsyncReceiveNode.class );

    public void doNode(AsyncReceiveNode node,
                       AsyncReceiveMemory memory,
                       LeftTupleSink sink,
                       ReteEvaluator reteEvaluator,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples) {

        if ( srcLeftTuples.getInsertFirst() != null ) {
            doLeftInserts( memory, srcLeftTuples );
        }

        doPropagateChildLeftTuples( node, memory, reteEvaluator, sink, trgLeftTuples );

        srcLeftTuples.resetAll();
    }

    private void doLeftInserts(AsyncReceiveMemory memory, TupleSets<LeftTuple> srcLeftTuples) {
        for ( LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            memory.addInsertOrUpdateLeftTuple( leftTuple );

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    private static void doPropagateChildLeftTuples(AsyncReceiveNode node,
                                                   AsyncReceiveMemory memory,
                                                   ReteEvaluator reteEvaluator,
                                                   LeftTupleSink sink,
                                                   TupleSets<LeftTuple> trgLeftTuples) {

        BetaConstraints betaConstraints = node.getBetaConstraints();
        ContextEntry[] context = betaConstraints.createContext();

        TupleList leftTuples = memory.getInsertOrUpdateLeftTuples();
        for ( LeftTuple leftTuple = (LeftTuple) leftTuples.getFirst(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getNext() ) {

            betaConstraints.updateFromTuple(context, reteEvaluator, leftTuple);

            for (Object message : memory.getMessages()) {
                InternalFactHandle factHandle = reteEvaluator.getFactHandleFactory().newFactHandle( message, node.getObjectTypeConf( reteEvaluator ), reteEvaluator, null );
                if ( isAllowed( factHandle, node.getAlphaConstraints(), reteEvaluator ) ) {
                    if (betaConstraints.isAllowedCachedLeft(context, factHandle)) {
                        LeftTuple childLeftTuple = sink.createLeftTuple(factHandle, leftTuple, sink );
                        childLeftTuple.setPropagationContext( leftTuple.getPropagationContext() );
                        trgLeftTuples.addInsert( childLeftTuple );
                    }
                }
            }
        }

        memory.reset();
    }
}
