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
package org.drools.core.reteoo;

import org.drools.base.common.RuleBasePartitionId;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.SuperCacheFixer;
import org.drools.core.phreak.PhreakRuleTerminalNode;

public class ModifyPreviousTuples {
    private final DefaultFactHandle.LinkedTuples linkedTuples;

    public ModifyPreviousTuples(InternalFactHandle.LinkedTuples linkedTuples) {
        this.linkedTuples = linkedTuples;
    }
    
    public TupleImpl peekLeftTuple(int partition) {
        return linkedTuples.getFirstLeftTuple(partition);
    }

    public TupleImpl peekLeftTuple(RuleBasePartitionId partitionId) {
        return linkedTuples.getFirstLeftTuple(partitionId);
    }

    public TupleImpl peekRightTuple(int partition) {
        return linkedTuples.getFirstRightTuple(partition);
    }

    public TupleImpl peekRightTuple(RuleBasePartitionId partitionId) {
        return linkedTuples.getFirstRightTuple(partitionId);
    }

    public void removeLeftTuple(int partition) {
        linkedTuples.removeLeftTuple( peekLeftTuple(partition) );
    }

    public void removeLeftTuple(RuleBasePartitionId partitionId) {
        linkedTuples.removeLeftTuple( peekLeftTuple(partitionId) );
    }

    public void removeRightTuple(int partition) {
        linkedTuples.removeRightTuple( peekRightTuple(partition) );
    }

    public void removeRightTuple(RuleBasePartitionId partitionId) {
        linkedTuples.removeRightTuple( peekRightTuple(partitionId) );
    }

    public void retractTuples(PropagationContext pctx,
                              ReteEvaluator reteEvaluator) {
        linkedTuples.forEachLeftTuple( lt -> doDeleteObject(pctx, reteEvaluator, lt) );
        linkedTuples.forEachRightTuple( rt -> doRightDelete(pctx, reteEvaluator, rt) );
    }

    public void doDeleteObject(PropagationContext pctx, ReteEvaluator reteEvaluator, TupleImpl leftTuple) {
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) SuperCacheFixer.getLeftTupleSource(leftTuple);
        LeftInputAdapterNode.LiaNodeMemory lm = reteEvaluator.getNodeMemory( liaNode );
        SegmentMemory sm = lm.getSegmentMemory();
        if (sm != null) {
            LeftInputAdapterNode.doDeleteObject( leftTuple, pctx, sm, reteEvaluator, liaNode, true, lm );
        } else {
            ActivationsManager activationsManager = reteEvaluator.getActivationsManager();
            PathMemory pathMemory = reteEvaluator.getNodeMemory( (TerminalNode) leftTuple.getSink() );
            PhreakRuleTerminalNode.doLeftDelete(activationsManager, pathMemory.getRuleAgendaItem().getRuleExecutor(), (RuleTerminalNodeLeftTuple) leftTuple);
        }
    }

    public void doRightDelete(PropagationContext pctx, ReteEvaluator reteEvaluator, TupleImpl rightTuple) {
        rightTuple.setPropagationContext( pctx );
        ((RightTuple)rightTuple).retractTuple(pctx, reteEvaluator);
    }
}
