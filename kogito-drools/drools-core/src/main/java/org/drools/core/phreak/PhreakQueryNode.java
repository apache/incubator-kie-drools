/*
 * Copyright 2015 JBoss Inc
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
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.reteoo.QueryElementNode.UnificationNodeViewChangedEventListener;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.PropagationContext;
import org.kie.api.runtime.rule.Variable;

public class PhreakQueryNode {
    public void doNode(QueryElementNode queryNode,
                       QueryElementNodeMemory qmem,
                       StackEntry stackEntry,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       LeftTupleSets srcLeftTuples,
                       LeftTupleSets trgLeftTuples,
                       LeftTupleSets stagedLeftTuples) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(qmem, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(queryNode, qmem, sink, wm, srcLeftTuples);
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
                              LeftTupleSets srcLeftTuples) {
        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            PropagationContext pCtx = leftTuple.getPropagationContext();

            InternalFactHandle handle = queryNode.createFactHandle(pCtx,
                                                                   wm,
                                                                   leftTuple);

            DroolsQuery dquery = queryNode.createDroolsQuery(leftTuple, handle, stackEntry,
                                                             qmem.getSegmentMemory().getPathMemories(),
                                                             qmem,
                                                             qmem.getResultLeftTuples(),
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
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples) {
        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            InternalFactHandle fh = (InternalFactHandle) leftTuple.getObject();
            DroolsQuery dquery = (DroolsQuery) fh.getObject();

            Object[] argTemplate = queryNode.getQueryElement().getArgTemplate(); // an array of declr, variable and literals
            Object[] args = new Object[argTemplate.length]; // the actual args, to be created from the  template

            // first copy everything, so that we get the literals. We will rewrite the declarations and variables next
            System.arraycopy(argTemplate,
                             0,
                             args,
                             0,
                             args.length);

            int[] declIndexes = queryNode.getQueryElement().getDeclIndexes();

            for (int i = 0, length = declIndexes.length; i < length; i++) {
                Declaration declr = (Declaration) argTemplate[declIndexes[i]];

                Object tupleObject = leftTuple.get(declr).getObject();

                Object o;

                if (tupleObject instanceof DroolsQuery) {
                    // If the query passed in a Variable, we need to use it
                    ArrayElementReader arrayReader = (ArrayElementReader) declr.getExtractor();
                    if (((DroolsQuery) tupleObject).getVariables()[arrayReader.getIndex()] != null) {
                        o = Variable.v;
                    } else {
                        o = declr.getValue(wm,
                                           tupleObject);
                    }
                } else {
                    o = declr.getValue(wm,
                                       tupleObject);
                }

                args[declIndexes[i]] = o;
            }

            int[] varIndexes = queryNode.getQueryElement().getVariableIndexes();
            for (int i = 0, length = varIndexes.length; i < length; i++) {
                if (argTemplate[varIndexes[i]] == Variable.v) {
                    // Need to check against the arg template, as the varIndexes also includes re-declared declarations
                    args[varIndexes[i]] = Variable.v;
                }
            }

            dquery.setParameters(args);
            ((UnificationNodeViewChangedEventListener) dquery.getQueryResultCollector()).setVariables(varIndexes);

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
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples) {
        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            InternalFactHandle fh = (InternalFactHandle) leftTuple.getObject();
            DroolsQuery dquery = (DroolsQuery) fh.getObject();
            if (dquery.isOpen()) {
                LeftInputAdapterNode lian = (LeftInputAdapterNode) qmem.getQuerySegmentMemory().getRootNode();
                LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories().get(0);
                LeftTuple childLeftTuple = fh.getFirstLeftTuple(); // there is only one, all other LTs are peers
                LeftInputAdapterNode.doDeleteObject(childLeftTuple, childLeftTuple.getPropagationContext(), qmem.getQuerySegmentMemory(), wm, lian, false, lm);
            } else {
                LeftTuple childLeftTuple = leftTuple.getFirstChild();
                while (childLeftTuple != null) {
                    childLeftTuple = RuleNetworkEvaluator.deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                    LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories().get(0);
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }
}
