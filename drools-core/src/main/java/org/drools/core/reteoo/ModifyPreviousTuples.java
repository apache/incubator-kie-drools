/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.PropagationContext;

public class ModifyPreviousTuples {
    private final DefaultFactHandle.LinkedTuples linkedTuples;

    public ModifyPreviousTuples(InternalFactHandle.LinkedTuples linkedTuples) {
        this.linkedTuples = linkedTuples;
    }
    
    public LeftTuple peekLeftTuple() {
        return linkedTuples.getFirstLeftTuple();
    }
    
    public RightTuple peekRightTuple() {
        return linkedTuples.getFirstRightTuple();
    }

    public void removeLeftTuple() {
        linkedTuples.removeLeftTuple( peekLeftTuple() );
    }
    
    public void removeRightTuple() {
        linkedTuples.removeRightTuple( peekRightTuple() );
    }
    
    public void retractTuples(PropagationContext pctx,
                              InternalWorkingMemory wm) {
        linkedTuples.forEachLeftTuple( lt -> doDeleteObject(pctx, wm, lt) );
        linkedTuples.forEachRightTuple( rt -> doRightDelete(pctx, wm, rt) );
    }

    public void doDeleteObject(PropagationContext pctx, InternalWorkingMemory wm, LeftTuple leftTuple) {
        LeftInputAdapterNode liaNode = leftTuple.getTupleSource();
        LeftInputAdapterNode.LiaNodeMemory lm = wm.getNodeMemory( liaNode );
        LeftInputAdapterNode.doDeleteObject(leftTuple, pctx, lm.getSegmentMemory(), wm, liaNode, true, lm);
    }

    public void doRightDelete(PropagationContext pctx, InternalWorkingMemory wm, RightTuple rightTuple) {
        rightTuple.setPropagationContext( pctx );
        rightTuple.retractTuple( pctx, wm );
    }
}
