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

import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;

public class SubnetworkTuple extends RightTuple {

    private static final AtomicInteger      idGenerator             = new AtomicInteger(0);
    private              InternalFactHandle factHandleForEvaluation = new DefaultFactHandle(idGenerator.decrementAndGet(), this);

    private boolean   stagedOnRight;
    private short     stagedTypeOnRight;
    private TupleImpl stagedNextOnRight;
    private TupleImpl stagedPreviousOnRight;


    public SubnetworkTuple() {
        // constructor needed for serialisation
        super();
    }


    public SubnetworkTuple(InternalFactHandle factHandle,
                           Sink sink,
                           boolean leftTupleMemoryEnabled) {
        super(factHandle, sink, leftTupleMemoryEnabled);
    }

    public SubnetworkTuple(InternalFactHandle factHandle,
                           TupleImpl leftTuple,
                           Sink sink) {
        super(factHandle, leftTuple, sink);
    }

    public SubnetworkTuple(TupleImpl leftTuple,
                           Sink sink,
                           PropagationContext pctx,
                           boolean leftTupleMemoryEnabled) {
        super(leftTuple, sink, pctx, leftTupleMemoryEnabled);
    }

    public SubnetworkTuple(TupleImpl leftTuple,
                           TupleImpl rightTuple,
                           Sink sink) {
        super(leftTuple, rightTuple, sink);
    }

    public SubnetworkTuple(TupleImpl leftTuple,
                           TupleImpl rightTuple,
                           TupleImpl currentLeftChild,
                           TupleImpl currentRightChild,
                           Sink sink,
                           boolean leftTupleMemoryEnabled) {
        super(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
    }

    @Override
    public void unlinkFromRightParent() {
        doUnlinkFromRightParent();
    }

    public InternalFactHandle getFactHandleForEvaluation() {
        return factHandleForEvaluation;
    }

    public boolean isStagedOnRight() {
        return stagedOnRight;
    }

    public void setStagedOnRight() {
        this.stagedOnRight = true;
    }

    public void prepareStagingOnRight() {
        super.clearStaged();
        setStagedOnRight();
        stagedTypeOnRight = Tuple.NONE;
    }



    @Override
    public void clearStaged() {
        super.clearStaged();
        stagedOnRight           = false;
    }

    public void retractTuple(PropagationContext context, ReteEvaluator reteEvaluator) {
        throw new UnsupportedOperationException();
    }

    public void setExpired(ReteEvaluator reteEvaluator, PropagationContext pctx) {
        super.setExpired();
    }

    public void moveStagingFromRightToLeft() {
        stagedTypeOnRight     = getStagedType();
        stagedPreviousOnRight = getStagedPrevious();
        stagedNextOnRight     = getStagedNext();
        clearStaged();
    }

    public SubnetworkTuple moveStagingFromLeftToRight() {
        stagedPrevious        = stagedPreviousOnRight;
        stagedPreviousOnRight = null;
        stagedNext            = stagedNextOnRight;
        stagedNextOnRight     = null;
        return this;
    }

    public short getStagedTypeOnRight() {
        return stagedTypeOnRight;
    }

    public boolean isSubnetworkTuple() {
        return true;
    }
}