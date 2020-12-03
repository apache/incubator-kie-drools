/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.phreak;

import java.util.List;

import org.drools.core.common.BetaConstraints;
import org.drools.core.common.GroupByFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.rule.Accumulate;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;

public class PhreakGroupByNode extends PhreakAccumulateNode {

    @Override
    protected void evaluateResultConstraints(final AccumulateNode accNode,
                                             final LeftTupleSink sink,
                                             final Accumulate accumulate,
                                             final LeftTuple leftTuple,
                                             final PropagationContext context,
                                             final InternalWorkingMemory workingMemory,
                                             final AccumulateNode.AccumulateMemory memory,
                                             final AccumulateNode.AccumulateContext accctx,
                                             final TupleSets<LeftTuple> trgLeftTuples,
                                             final TupleSets<LeftTuple> stagedLeftTuples) {

        List<Object[]> results = (List<Object[]>) accumulate.getResult(memory.workingMemoryContext, accctx.context, leftTuple, workingMemory);

//        if (result == null) {
//            if (accctx.propagated) {
//                // retract
//                trgLeftTuples.addDelete(accctx.getResultLeftTuple());
//                accctx.propagated = false;
//            }
//            return;
//        }

        PropagationContext propagationContext = accctx.getPropagationContext();
        accctx.setPropagationContext( null );

        for (Object[] keyValuePair : results) {

            final InternalFactHandle handle = new GroupByFactHandle( accNode.createResultFactHandle( context, workingMemory, leftTuple, keyValuePair[1] ), keyValuePair[0] );

//            if ( accctx.getResultFactHandle() == null ) {
//                final InternalFactHandle handle = accNode.createResultFactHandle( context, workingMemory, leftTuple, keyValuePair[1] );
//
//                accctx.setResultFactHandle( handle );
//
//                accctx.setResultLeftTuple( sink.createLeftTuple( handle, leftTuple, sink ) );
//            } else {
//                accctx.getResultFactHandle().setObject( keyValuePair[1] );
//            }

            // First alpha node filters
            AlphaNodeFieldConstraint[] resultConstraints = accNode.getResultConstraints();
            boolean isAllowed = true;
            for (AlphaNodeFieldConstraint resultConstraint : resultConstraints) {
                if ( !resultConstraint.isAllowed( handle, workingMemory ) ) {
                    isAllowed = false;
                    break;
                }
            }

            if ( isAllowed ) {
                BetaConstraints resultBinder = accNode.getResultBinder();
                resultBinder.updateFromTuple( memory.resultsContext, workingMemory, leftTuple );
                if ( !resultBinder.isAllowedCachedLeft( memory.resultsContext, handle ) ) {
                    isAllowed = false;
                }
                resultBinder.resetTuple( memory.resultsContext );
            }

            LeftTuple childLeftTuple = sink.createLeftTuple( handle, leftTuple, sink );
            if ( propagationContext != null ) {
                childLeftTuple.setPropagationContext( propagationContext);
            } else {
                childLeftTuple.setPropagationContext( leftTuple.getPropagationContext() );
            }

//            if ( accctx.propagated ) {
//                normalizeStagedTuples( stagedLeftTuples, childLeftTuple );
//
//                if ( isAllowed ) {
//                    // modify
//                    trgLeftTuples.addUpdate( childLeftTuple );
//                } else {
//                    // retract
//                    trgLeftTuples.addDelete( childLeftTuple );
//                    accctx.propagated = false;
//                }
//            } else if ( isAllowed ) {
//                // assert
//                trgLeftTuples.addInsert( childLeftTuple );
//                accctx.propagated = true;
//            }

            trgLeftTuples.addInsert( childLeftTuple );
        }
    }
}
