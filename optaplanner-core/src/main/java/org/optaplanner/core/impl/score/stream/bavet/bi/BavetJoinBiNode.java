/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet.bi;

import java.util.List;

import org.optaplanner.core.impl.score.stream.bavet.session.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetAbstractUniTuple;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinLeftBridgeUniNode;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinLeftBridgeUniTuple;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinRightBridgeUniNode;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinRightBridgeUniTuple;

public final class BavetJoinBiNode<A, B, Property_> extends BavetAbstractBiNode<A, B> {

    private BavetJoinLeftBridgeUniNode<A, B, Property_> leftParentNode;
    private BavetJoinRightBridgeUniNode<A, B, Property_> rightParentNode;
    private final BavetAbstractBiNode<A, B> nextNode;

    public BavetJoinBiNode(BavetConstraintSession session, int nodeOrder, BavetAbstractBiNode<A, B> nextNode) {
        super(session, nodeOrder);
        this.nextNode = nextNode;
    }

    @Override
    public BavetJoinBiTuple<A, B, Property_> createTuple(BavetAbstractBiTuple<A, B> previousTuple) {
        throw new IllegalStateException("The join node (" + getClass().getSimpleName()
                + ") can't have a previousTuple (" + previousTuple + ");");
    }

    public BavetJoinBiTuple<A, B, Property_> createTuple(
            BavetJoinLeftBridgeUniTuple<A, B, Property_> aTuple, BavetJoinRightBridgeUniTuple<A, B, Property_> bTuple) {
        return new BavetJoinBiTuple<>(this, aTuple, bTuple);
    }

    public void refresh(BavetJoinBiTuple<A, B, Property_> tuple) {
        A a = tuple.getFactA();
        B b = tuple.getFactB();
        BavetAbstractBiTuple<A, B> downstreamTuple = tuple.getDownstreamTuple();
        if (downstreamTuple != null) {
            session.transitionTuple(downstreamTuple, BavetTupleState.DYING);
        }
        if (tuple.isActive()) {
            BavetAbstractBiTuple<A, B> nextTuple = nextNode.createTuple(tuple);
            tuple.setDownstreamTuple(nextTuple);
            session.transitionTuple(nextTuple, BavetTupleState.CREATING);
        }
        tuple.refreshed();
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public void setLeftParentNode(BavetJoinLeftBridgeUniNode<A, B, Property_> leftParentNode) {
        this.leftParentNode = leftParentNode;
    }

    public void setRightParentNode(BavetJoinRightBridgeUniNode<A, B, Property_> rightParentNode) {
        this.rightParentNode = rightParentNode;
    }

    public List<BavetJoinLeftBridgeUniTuple<A, B, Property_>> getATupleListByProperty(Property_ property) {
        return leftParentNode.getTupleListByProperty(property);
    }

    public List<BavetJoinRightBridgeUniTuple<A, B, Property_>> getBTupleListByProperty(Property_ property) {
        return rightParentNode.getTupleListByProperty(property);
    }

}
