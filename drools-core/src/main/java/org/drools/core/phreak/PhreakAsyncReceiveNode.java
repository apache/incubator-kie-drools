/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.phreak;

import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.AsyncReceiveNode.AsyncReceiveMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.rule.ContextEntry;
import org.drools.core.util.index.TupleList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.core.phreak.PhreakAsyncSendNode.isAllowed;

public class PhreakAsyncReceiveNode {
    private static final Logger log = LoggerFactory.getLogger( PhreakAsyncReceiveNode.class );

    public void doNode(AsyncReceiveNode node,
                       AsyncReceiveMemory memory,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples) {

        if ( srcLeftTuples.getInsertFirst() != null ) {
            doLeftInserts( memory, srcLeftTuples );
        }

        doPropagateChildLeftTuples( node, memory, wm, sink, trgLeftTuples );

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
                                                   InternalWorkingMemory wm,
                                                   LeftTupleSink sink,
                                                   TupleSets<LeftTuple> trgLeftTuples) {

        BetaConstraints betaConstraints = node.getBetaConstraints();
        ContextEntry[] context = betaConstraints.createContext();

        TupleList leftTuples = memory.getInsertOrUpdateLeftTuples();
        for ( LeftTuple leftTuple = (LeftTuple) leftTuples.getFirst(); leftTuple != null; leftTuple = ( LeftTuple ) leftTuple.getNext() ) {

            betaConstraints.updateFromTuple(context, wm, leftTuple);

            for (Object message : memory.getMessages()) {
                InternalFactHandle factHandle = wm.getFactHandleFactory().newFactHandle( message, node.getObjectTypeConf( wm ), wm, null );
                if ( isAllowed( factHandle, node.getAlphaConstraints(), wm ) ) {
                    if (betaConstraints.isAllowedCachedLeft(context, factHandle)) {
                        LeftTuple childLeftTuple = sink.createLeftTuple( factHandle, leftTuple, sink );
                        childLeftTuple.setPropagationContext( leftTuple.getPropagationContext() );
                        trgLeftTuples.addInsert( childLeftTuple );
                    }
                }
            }
        }

        memory.reset();
    }
}
