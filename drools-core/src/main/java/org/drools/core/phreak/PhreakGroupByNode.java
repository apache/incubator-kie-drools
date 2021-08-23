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

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateContextEntry;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.AccumulateNode.GroupByContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.Accumulate;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.index.TupleList;

public class PhreakGroupByNode extends PhreakAccumulateNode {

    @Override
    AccumulateNode.BaseAccumulation initAccumulationContext(AccumulateMemory am, InternalWorkingMemory wm, Accumulate accumulate, LeftTuple leftTuple) {
        GroupByContext accContext = new GroupByContext();
        leftTuple.setContextObject( accContext );
        // A lot less is done here, compared to super, as it needs to be done on demand during the Group creation.
        return accContext;
    }

    @Override
    protected Object createResult( AccumulateNode accNode, Object key, Object result ) {
        Object[] array;
        if (accNode.getAccumulate().isMultiFunction()) {
            array = (Object[]) result;
            array[array.length-1] = key;
        } else {
            array = new Object[] {result, key};
        }
        return array;
    }

    @Override
    protected void evaluateResultConstraints(final AccumulateNode accNode,
                                             final LeftTupleSink sink,
                                             final Accumulate accumulate,
                                             final LeftTuple leftTuple,
                                             final PropagationContext context,
                                             final InternalWorkingMemory workingMemory,
                                             final AccumulateMemory memory,
                                             final AccumulateNode.BaseAccumulation accctx,
                                             final TupleSets<LeftTuple> trgLeftTuples,
                                             final TupleSets<LeftTuple> stagedLeftTuples) {

        PropagationContext propagationContext = accctx.getPropagationContext();
        accctx.setPropagationContext( null );

        GroupByContext groupByContext = (GroupByContext)accctx;

        for (TupleList<AccumulateContextEntry> tupleList = groupByContext.takeToPropagateList(); tupleList != null; tupleList = tupleList.getNext()) {
            AccumulateContextEntry contextEntry = tupleList.getContext();

            Object result = accumulate.getResult(memory.workingMemoryContext, contextEntry, leftTuple, workingMemory);

            propagateResult( accNode, sink, leftTuple, context, workingMemory, memory, trgLeftTuples, stagedLeftTuples,
                             contextEntry.getKey(), result, contextEntry, propagationContext, false ); // don't want to propagate null

            contextEntry.setToPropagate(false);
        }
    }

    @Override
    protected void reaccumulateForLeftTuple(final AccumulateNode accNode,
                                            final Accumulate accumulate,
                                            final LeftTuple leftTuple,
                                            final RightTuple rightParent,
                                            final LeftTuple match,
                                            final InternalWorkingMemory wm,
                                            final AccumulateMemory am,
                                            final AccumulateNode.BaseAccumulation accctx,
                                            final boolean reaccumulate) {
        GroupByContext groupByContext = (GroupByContext) accctx;

        if (match != null) {
            // re-accumulate just for the sub group
            TupleList<AccumulateContextEntry> tupleList = match.getMemory();
            tupleList.remove(match);

            if (reaccumulate) {
                // re-init function context for the group
                Object functionContext = accumulate.createFunctionContext();
                tupleList.getContext().setFunctionContext(functionContext);

                for (LeftTuple childMatch = (LeftTuple) tupleList.getFirst(); childMatch != null; childMatch = (LeftTuple) childMatch.getNext()) {
                    RightTuple         rightTuple  = childMatch.getRightParent();
                    InternalFactHandle childHandle = rightTuple.getFactHandle();
                    LeftTuple          tuple       = leftTuple;
                    if (accNode.isRightInputIsRiaNode()) {
                        // if there is a subnetwork, handle must be unwrapped
                        tuple = (LeftTuple) rightTuple;
                        childHandle = rightTuple.getFactHandleForEvaluation();
                    }

                    Object value = accumulate.accumulate(am.workingMemoryContext, tuple, childHandle,
                                                         groupByContext, tupleList, wm);

                    match.setContextObject(value);
                }
            }
        } else {
            // re-accumulate all groups
            groupByContext.clear();
            super.reaccumulateForLeftTuple(accNode,
                                     accumulate,
                                     leftTuple,
                                     null,
                                     null,
                                     wm,
                                     am,
                                     accctx,
                                     true);
        }
    }

    @Override
    protected void propagateDelete( TupleSets<LeftTuple> trgLeftTuples, TupleSets<LeftTuple> stagedLeftTuples, Object accctx ) {
        GroupByContext groupByContext = (GroupByContext)accctx;
        for ( TupleList<AccumulateContextEntry> tupleList : groupByContext.getGroups().values()) {
            super.propagateDelete(trgLeftTuples, stagedLeftTuples, tupleList.getContext());
        }
    }

    void postAccumulate(AccumulateNode accNode, Object accctx, LeftTuple match) {
        ((GroupByContext)accctx).addMatchOnLastTupleList(match);
    }

}
