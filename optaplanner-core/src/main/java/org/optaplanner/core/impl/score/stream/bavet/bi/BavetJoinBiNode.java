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
import java.util.Set;

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndex;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinLeftBridgeUniNode;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinLeftBridgeUniTuple;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinRightBridgeUniNode;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinRightBridgeUniTuple;

public final class BavetJoinBiNode<A, B> extends BavetAbstractBiNode<A, B> {

    private BavetJoinLeftBridgeUniNode<A, B> leftParentNode;
    private BavetJoinRightBridgeUniNode<A, B> rightParentNode;

    private final List<BavetAbstractBiNode<A, B>> childNodeList;

    public BavetJoinBiNode(BavetConstraintSession session, int nodeOrder,
            List<BavetAbstractBiNode<A, B>> childNodeList) {
        super(session, nodeOrder);
        this.childNodeList = childNodeList;
    }

    @Override
    public BavetJoinBiTuple<A, B> createTuple(BavetAbstractBiTuple<A, B> parentTuple) {
        throw new IllegalStateException("The join node (" + getClass().getSimpleName()
                + ") can't have a parentTuple (" + parentTuple + ");");
    }

    public BavetJoinBiTuple<A, B> createTuple(
            BavetJoinLeftBridgeUniTuple<A, B> aTuple, BavetJoinRightBridgeUniTuple<A, B> bTuple) {
        return new BavetJoinBiTuple<>(this, aTuple, bTuple);
    }

    public void refresh(BavetJoinBiTuple<A, B> tuple) {
        A a = tuple.getFactA();
        B b = tuple.getFactB();
        List<BavetAbstractBiTuple<A, B>> childTupleList = tuple.getChildTupleList();
        for (BavetAbstractBiTuple<A, B> childTuple : childTupleList) {
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleList.clear();
        if (tuple.isActive()) {
            for (BavetAbstractBiNode<A, B> childNode : childNodeList) {
                BavetAbstractBiTuple<A, B> childTuple = childNode.createTuple(tuple);
                childTupleList.add(childTuple);
                session.transitionTuple(childTuple, BavetTupleState.CREATING);
            }
        }
        tuple.refreshed();
    }

    public BavetIndex<A, BavetJoinLeftBridgeUniTuple<A, B>> getLeftIndex() {
        return leftParentNode.getIndex();
    }

    public BavetIndex<B, BavetJoinRightBridgeUniTuple<A, B>> getRightIndex() {
        return rightParentNode.getIndex();
    }

    @Override
    public String toString() {
        return "Join() with " + childNodeList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public void setLeftParentNode(BavetJoinLeftBridgeUniNode<A, B> leftParentNode) {
        this.leftParentNode = leftParentNode;
    }

    public void setRightParentNode(BavetJoinRightBridgeUniNode<A, B> rightParentNode) {
        this.rightParentNode = rightParentNode;
    }

}
