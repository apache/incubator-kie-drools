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

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.spi.PropagationContext;

public class PhreakQueryNode {
    public void doNode(QueryElementNode queryNode,
                       QueryElementNodeMemory qmem,
                       StackEntry stackEntry,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(qmem, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(queryNode, qmem, wm, srcLeftTuples);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(queryNode, qmem, stackEntry, wm, srcLeftTuples);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(QueryElementNode queryNode,
                              QueryElementNodeMemory qmem,
                              StackEntry stackEntry,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples) {
        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            PropagationContext pCtx = leftTuple.getPropagationContext();

            InternalFactHandle handle = queryNode.createFactHandle(pCtx,
                                                                   wm,
                                                                   leftTuple);

            DroolsQuery dquery = queryNode.createDroolsQuery(leftTuple, handle, stackEntry,
                                                             qmem.getSegmentMemory().getPathMemories(),
                                                             qmem,
                                                             stackEntry.getSink(), wm);

            LeftInputAdapterNode lian = (LeftInputAdapterNode) qmem.getQuerySegmentMemory().getRootNode();
            LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories().get(0);
            LeftInputAdapterNode.doInsertObject(handle, pCtx, lian, wm, lm, false, dquery.isOpen());

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftUpdates(QueryElementNode queryNode,
                              QueryElementNodeMemory qmem,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples) {
        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            InternalFactHandle fh = (InternalFactHandle) leftTuple.getContextObject();
            DroolsQuery dquery = (DroolsQuery) fh.getObject();
            dquery.setParameters( queryNode.getActualArguments( leftTuple, wm ) );

            SegmentMemory qsmem = qmem.getQuerySegmentMemory();
            LeftInputAdapterNode lian = (LeftInputAdapterNode) qsmem.getRootNode();
            LiaNodeMemory lmem = (LiaNodeMemory) qsmem.getNodeMemories().getFirst();
            if (dquery.isOpen()) {
                LeftTuple childLeftTuple = fh.getFirstLeftTuple(); // there is only one, all other LTs are peers
                LeftInputAdapterNode.doUpdateObject(childLeftTuple, childLeftTuple.getPropagationContext(), wm, lian, false, lmem, qmem.getQuerySegmentMemory());
            } else {
                if (fh.getFirstLeftTuple() != null) {
                    throw new RuntimeException("defensive programming while testing"); // @TODO remove later (mdp)
                }
                LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories().get(0);
                LeftInputAdapterNode.doInsertObject(fh, leftTuple.getPropagationContext(), lian, wm, lm, false, dquery.isOpen());
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(QueryElementNodeMemory qmem,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              TupleSets<LeftTuple> stagedLeftTuples) {
        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            InternalFactHandle fh = (InternalFactHandle) leftTuple.getContextObject();
            DroolsQuery dquery = (DroolsQuery) fh.getObject();
            if (dquery.isOpen()) {
                LeftInputAdapterNode lian = (LeftInputAdapterNode) qmem.getQuerySegmentMemory().getRootNode();
                LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories().get(0);
                LeftTuple childLeftTuple = fh.getFirstLeftTuple(); // there is only one, all other LTs are peers
                LeftInputAdapterNode.doDeleteObject(childLeftTuple, childLeftTuple.getPropagationContext(), qmem.getQuerySegmentMemory(), wm, lian, false, lm);
            } else {
                LeftTuple childLeftTuple = leftTuple.getFirstChild();
                while (childLeftTuple != null) {
                    LeftTuple nextChild = childLeftTuple.getHandleNext();
                    RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    childLeftTuple = nextChild;
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }
}
