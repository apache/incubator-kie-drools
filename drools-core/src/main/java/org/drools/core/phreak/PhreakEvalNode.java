/*
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

import org.drools.base.rule.EvalCondition;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.EvalConditionNode.EvalMemory;

import static org.drools.core.phreak.PhreakNodeOperations.deleteChildLeftTuple;
import static org.drools.core.phreak.PhreakNodeOperations.normalizeStagedTuples;
import static org.drools.core.phreak.PhreakNodeOperations.unlinkAndDeleteChildLeftTuple;
import static org.drools.core.phreak.PhreakNodeOperations.useLeftMemory;
import static org.drools.core.reteoo.TupleFactory.createLeftTuple;

import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.TupleImpl;


/**
* Created with IntelliJ IDEA.
* User: mdproctor
* Date: 03/05/2013
* Time: 15:44
* To change this template use File | Settings | File Templates.
*/
public class PhreakEvalNode {

    private static final String EVAL_LEFT_TUPLE_DELETED = "EVAL_LEFT_TUPLE_DELETED";
    private final ReteEvaluator reteEvaluator;

    public PhreakEvalNode(ReteEvaluator reteEvaluator) {
        this.reteEvaluator = reteEvaluator;
    }

    public void doNode(EvalConditionNode evalNode,
                       EvalMemory em,
                       LeftTupleSink sink,
                       TupleSets srcLeftTuples,
                       TupleSets trgLeftTuples,
                       TupleSets stagedLeftTuples) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(evalNode, em, sink, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(evalNode, em, sink, srcLeftTuples, trgLeftTuples);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(EvalConditionNode evalNode,
                              EvalMemory em,
                              LeftTupleSink sink,
                              TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples) {
        EvalCondition condition = evalNode.getCondition();
        for (TupleImpl leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            final boolean allowed = condition.isAllowed(leftTuple, reteEvaluator, em.context);

            if (allowed) {
                boolean useLeftMemory = useLeftMemory(evalNode, leftTuple);

                trgLeftTuples.addInsert(createLeftTuple(leftTuple, sink, leftTuple.getPropagationContext(), useLeftMemory));
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftUpdates(EvalConditionNode evalNode,
                              EvalMemory em,
                              LeftTupleSink sink,
                              TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples,
                              TupleSets stagedLeftTuples) {
        EvalCondition condition = evalNode.getCondition();
        for (TupleImpl leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            boolean wasPropagated = leftTuple.getFirstChild() != null && leftTuple.getContextObject() != EVAL_LEFT_TUPLE_DELETED;

            boolean allowed = condition.isAllowed(leftTuple, reteEvaluator, em.context);
            if (allowed) {
                leftTuple.setContextObject( null );

                if (wasPropagated) {
                    // update
                    TupleImpl childLeftTuple = leftTuple.getFirstChild();
                    childLeftTuple.setPropagationContext( leftTuple.getPropagationContext());
                    normalizeStagedTuples(stagedLeftTuples, childLeftTuple);
                    trgLeftTuples.addUpdate(childLeftTuple);
                } else {
                    // assert
                    trgLeftTuples.addInsert(createLeftTuple(leftTuple,
                                                                         sink,
                                                                         leftTuple.getPropagationContext(), true));
                }
            } else {
                if (wasPropagated) {
                    // retract
                    leftTuple.setContextObject( EVAL_LEFT_TUPLE_DELETED );

                    TupleImpl childLeftTuple = leftTuple.getFirstChild();
                    childLeftTuple.setPropagationContext( leftTuple.getPropagationContext());
                    unlinkAndDeleteChildLeftTuple(trgLeftTuples, stagedLeftTuples, childLeftTuple);
                }
                // else do nothing
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples,
                              TupleSets stagedLeftTuples) {
        for (TupleImpl leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();


            TupleImpl childLeftTuple = leftTuple.getFirstChild();
            if (childLeftTuple != null) {
                childLeftTuple.setPropagationContext( leftTuple.getPropagationContext());
                deleteChildLeftTuple(childLeftTuple, trgLeftTuples, stagedLeftTuples);
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }
}
