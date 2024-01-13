/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.phreak;

import org.drools.base.rule.ContextEntry;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.AsyncReceiveNode.AsyncReceiveMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.TupleFactory;
import org.drools.core.reteoo.TupleImpl;
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
                       TupleSets srcLeftTuples,
                       TupleSets trgLeftTuples) {

        if ( srcLeftTuples.getInsertFirst() != null ) {
            doLeftInserts( memory, srcLeftTuples );
        }

        doPropagateChildLeftTuples( node, memory, reteEvaluator, sink, trgLeftTuples );

        srcLeftTuples.resetAll();
    }

    private void doLeftInserts(AsyncReceiveMemory memory, TupleSets srcLeftTuples) {
        for (TupleImpl leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            memory.addInsertOrUpdateLeftTuple( leftTuple );

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    private static void doPropagateChildLeftTuples(AsyncReceiveNode node,
                                                   AsyncReceiveMemory memory,
                                                   ReteEvaluator reteEvaluator,
                                                   LeftTupleSink sink,
                                                   TupleSets trgLeftTuples) {

        BetaConstraints betaConstraints = node.getBetaConstraints();
        Object context = betaConstraints.createContext();

        TupleList leftTuples = memory.getInsertOrUpdateLeftTuples();
        for ( TupleImpl leftTuple = leftTuples.getFirst(); leftTuple != null; leftTuple = leftTuple.getNext() ) {

            betaConstraints.updateFromTuple(context, reteEvaluator, leftTuple);

            for (Object message : memory.getMessages()) {
                InternalFactHandle factHandle = reteEvaluator.getFactHandleFactory().newFactHandle( message, node.getObjectTypeConf( reteEvaluator ), reteEvaluator, null );
                if ( isAllowed( factHandle, node.getAlphaConstraints(), reteEvaluator ) ) {
                    if (betaConstraints.isAllowedCachedLeft(context, factHandle)) {
                        TupleImpl childLeftTuple = TupleFactory.createLeftTuple(factHandle, leftTuple, sink);
                        childLeftTuple.setPropagationContext( leftTuple.getPropagationContext() );
                        trgLeftTuples.addInsert( childLeftTuple );
                    }
                }
            }
        }

        memory.reset();
    }
}
