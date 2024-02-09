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

import org.drools.core.base.DroolsQueryImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.util.index.TupleList;

public class PhreakQueryNode {
    public void doNode(QueryElementNode queryNode,
                       QueryElementNodeMemory qmem,
                       StackEntry stackEntry,
                       ReteEvaluator reteEvaluator,
                       TupleSets srcLeftTuples,
                       TupleSets trgLeftTuples,
                       TupleSets stagedLeftTuples) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(qmem, reteEvaluator, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(queryNode, qmem, reteEvaluator, srcLeftTuples);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(queryNode, qmem, stackEntry, reteEvaluator, srcLeftTuples);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(QueryElementNode queryNode,
                              QueryElementNodeMemory qmem,
                              StackEntry stackEntry,
                              ReteEvaluator reteEvaluator,
                              TupleSets srcLeftTuples) {
        for (TupleImpl leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            PropagationContext pCtx = leftTuple.getPropagationContext();

            InternalFactHandle handle = queryNode.createFactHandle(pCtx,
                                                                   reteEvaluator,
                                                                   leftTuple);

            DroolsQueryImpl dquery = queryNode.createDroolsQuery(leftTuple, handle, stackEntry,
                                                                 qmem.getSegmentMemory().getPathMemories(),
                                                                 qmem,
                                                                 stackEntry.getSink(), reteEvaluator);

            LeftInputAdapterNode lian = (LeftInputAdapterNode) qmem.getQuerySegmentMemory().getRootNode();
            LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories()[0];
            LeftInputAdapterNode.doInsertObject(handle, pCtx, lian, reteEvaluator, lm, false, dquery.isOpen());

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftUpdates(QueryElementNode queryNode,
                              QueryElementNodeMemory qmem,
                              ReteEvaluator reteEvaluator,
                              TupleSets srcLeftTuples) {
        for (TupleImpl leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            InternalFactHandle fh = (InternalFactHandle) leftTuple.getContextObject();
            DroolsQueryImpl dquery = (DroolsQueryImpl) fh.getObject();
            dquery.setParameters( queryNode.getActualArguments( leftTuple, reteEvaluator ) );

            SegmentMemory qsmem = qmem.getQuerySegmentMemory();
            LeftInputAdapterNode lian = (LeftInputAdapterNode) qsmem.getRootNode();
            LiaNodeMemory lmem = (LiaNodeMemory) qsmem.getNodeMemories()[0];
            if (dquery.isOpen()) {
                TupleImpl childLeftTuple = fh.getFirstLeftTuple(); // there is only one, all other LTs are peers
                LeftInputAdapterNode.doUpdateObject(childLeftTuple, childLeftTuple.getPropagationContext(), reteEvaluator, lian, false, lmem, qmem.getQuerySegmentMemory());
            } else {
                if (fh.getFirstLeftTuple() != null) {
                    throw new RuntimeException("defensive programming while testing"); // @TODO remove later (mdp)
                }
                LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories()[0];
                LeftInputAdapterNode.doInsertObject(fh, leftTuple.getPropagationContext(), lian, reteEvaluator, lm, false, dquery.isOpen());
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(QueryElementNodeMemory qmem,
                              ReteEvaluator reteEvaluator,
                              TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples,
                              TupleSets stagedLeftTuples) {
        for (TupleImpl leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            InternalFactHandle fh = (InternalFactHandle) leftTuple.getContextObject();
            DroolsQueryImpl dquery = (DroolsQueryImpl) fh.getObject();
            if (dquery.isOpen()) {
                LeftInputAdapterNode lian = (LeftInputAdapterNode) qmem.getQuerySegmentMemory().getRootNode();
                LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories()[0];
                TupleImpl childLeftTuple = fh.getFirstLeftTuple(); // there is only one, all other LTs are peers
                LeftInputAdapterNode.doDeleteObject(childLeftTuple, childLeftTuple.getPropagationContext(), qmem.getQuerySegmentMemory(), reteEvaluator, lian, false, lm);
            } else {
                TupleImpl childLeftTuple = leftTuple.getFirstChild();
                while (childLeftTuple != null) {
                    TupleImpl nextChild = childLeftTuple.getHandleNext();
                    RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    childLeftTuple = nextChild;
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }
}
