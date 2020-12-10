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

import org.drools.core.common.GroupByFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.AccumulateNode.AccumulatePropagationContext;
import org.drools.core.reteoo.AccumulateNode.AccumulationContext;
import org.drools.core.reteoo.AccumulateNode.GroupByContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.rule.Accumulate;
import org.drools.core.spi.PropagationContext;

public class PhreakGroupByNode extends PhreakAccumulateNode {

    @Override
    protected AccumulationContext createAccumulationContext() {
        return new GroupByContext();
    }

    @Override
    protected InternalFactHandle createFactHandle( AccumulateNode accNode, LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory, Object key, Object result ) {
        return new GroupByFactHandle( accNode.createResultFactHandle( context, workingMemory, leftTuple, result ), key );
    }

    @Override
    protected void evaluateResultConstraints(final AccumulateNode accNode,
                                             final LeftTupleSink sink,
                                             final Accumulate accumulate,
                                             final LeftTuple leftTuple,
                                             final PropagationContext context,
                                             final InternalWorkingMemory workingMemory,
                                             final AccumulateMemory memory,
                                             final AccumulationContext accctx,
                                             final TupleSets<LeftTuple> trgLeftTuples,
                                             final TupleSets<LeftTuple> stagedLeftTuples) {

        PropagationContext propagationContext = accctx.getPropagationContext();
        accctx.setPropagationContext( null );

        List<Object[]> results = (List<Object[]>) accumulate.getResult(memory.workingMemoryContext, accctx.getContext(), leftTuple, workingMemory);
        for (Object[] keyValuePair : results) {
            Object key = keyValuePair[0];
            Object result = keyValuePair[1];
            AccumulatePropagationContext accPropCtx = (( GroupByContext ) accctx).getAccPropCtx(key);
            propagateResult( accNode, sink, leftTuple, context, workingMemory, memory, trgLeftTuples, stagedLeftTuples, key, result, accPropCtx, propagationContext );
        }
    }

    @Override
    protected void propagateDelete( TupleSets<LeftTuple> trgLeftTuples, TupleSets<LeftTuple> stagedLeftTuples, AccumulationContext accctx ) {
        for (AccumulatePropagationContext accPropCtx : (( GroupByContext ) accctx).getAllAccPropCtxs()) {
            propagateDelete( trgLeftTuples, stagedLeftTuples, accPropCtx );
        }
    }
}
