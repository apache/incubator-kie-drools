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

package org.optaplanner.core.impl.score.stream.bavet.tri;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBridgeBiNode;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBridgeBiTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndex;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniNode;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniTuple;

public final class BavetJoinTriNode<A, B, C> extends BavetAbstractTriNode<A, B, C> implements BavetJoinNode {

    private final BavetJoinBridgeBiNode<A, B> leftParentNode;
    private final BavetJoinBridgeUniNode<C> rightParentNode;

    private final List<BavetAbstractTriNode<A, B, C>> childNodeList = new ArrayList<>();

    public BavetJoinTriNode(BavetConstraintSession session, int nodeOrder,
            BavetJoinBridgeBiNode<A, B> leftParentNode, BavetJoinBridgeUniNode<C> rightParentNode) {
        super(session, nodeOrder);
        this.leftParentNode = leftParentNode;
        this.rightParentNode = rightParentNode;
    }

    @Override
    public void addChildNode(BavetAbstractTriNode<A, B, C> childNode) {
        childNodeList.add(childNode);
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    // TODO

    // ************************************************************************
    // Runtime
    // ************************************************************************

    @Override
    public BavetJoinTriTuple<A, B, C> createTuple(BavetAbstractTriTuple<A, B, C> parentTuple) {
        throw new IllegalStateException("The join node (" + getClass().getSimpleName()
                + ") can't have a parentTuple (" + parentTuple + ");");
    }

    public BavetJoinTriTuple<A, B, C> createTuple(
            BavetJoinBridgeBiTuple<A, B> abTuple, BavetJoinBridgeUniTuple<C> cTuple) {
        return new BavetJoinTriTuple<>(this, abTuple, cTuple);
    }

    public void refresh(BavetJoinTriTuple<A, B, C> tuple) {
        List<BavetAbstractTriTuple<A, B, C>> childTupleList = tuple.getChildTupleList();
        for (BavetAbstractTriTuple<A, B, C> childTuple : childTupleList) {
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleList.clear();
        if (tuple.isActive()) {
            for (BavetAbstractTriNode<A, B, C> childNode : childNodeList) {
                BavetAbstractTriTuple<A, B, C> childTuple = childNode.createTuple(tuple);
                childTupleList.add(childTuple);
                session.transitionTuple(childTuple, BavetTupleState.CREATING);
            }
        }
        tuple.refreshed();
    }

    public void refreshChildTuplesLeft(BavetJoinBridgeBiTuple<A, B> leftParentTuple) {
        Set<BavetJoinTuple> leftTupleSet = leftParentTuple.getChildTupleSet();
        for (BavetJoinTuple tuple_ : leftTupleSet) {
            BavetJoinTriTuple<A, B, C> tuple = (BavetJoinTriTuple<A, B, C>) tuple_;
            boolean removed = tuple.getCTuple().getChildTupleSet().remove(tuple);
            if (!removed) {
                throw new IllegalStateException("Impossible state: the facts (" + tuple.getFactA() + ", " + tuple.getFactB()
                        + ")'s tuple cannot be removed from the other fact (" + tuple.getFactC()
                        + ")'s join bridge.");
            }
            session.transitionTuple(tuple, BavetTupleState.DYING);
        }
        leftTupleSet.clear();
        if (leftParentTuple.isActive()) {
            Set<BavetJoinBridgeUniTuple<C>> rightParentTupleList = getRightIndex().get(leftParentTuple.getIndexProperties());
            for (BavetJoinBridgeUniTuple<C> rightParentTuple : rightParentTupleList) {
                if (!rightParentTuple.isDirty()) {
                    BavetJoinTriTuple<A, B, C> childTuple = createTuple(leftParentTuple, rightParentTuple);
                    leftTupleSet.add(childTuple);
                    rightParentTuple.getChildTupleSet().add(childTuple);
                    session.transitionTuple(childTuple, BavetTupleState.CREATING);
                }
            }
        }
    }

    public void refreshChildTuplesRight(BavetJoinBridgeUniTuple<C> rightParentTuple) {
        Set<BavetJoinTuple> rightTupleSet = rightParentTuple.getChildTupleSet();
        for (BavetJoinTuple uncastTuple : rightTupleSet) {
            BavetJoinTriTuple<A, B, C> tuple = (BavetJoinTriTuple<A, B, C>) uncastTuple;
            boolean removed = tuple.getAbTuple().getChildTupleSet().remove(tuple);
            if (!removed) {
                throw new IllegalStateException("Impossible state: the fact (" + tuple.getFactC()
                        + ")'s tuple cannot be removed from the other facts (" + tuple.getFactA() + ", " + tuple.getFactB()
                        + ")'s join bridge.");
            }
            session.transitionTuple(tuple, BavetTupleState.DYING);
        }
        rightTupleSet.clear();
        if (rightParentTuple.isActive()) {
            Set<BavetJoinBridgeBiTuple<A, B>> leftParentTupleList = getLeftIndex().get(rightParentTuple.getIndexProperties());
            for (BavetJoinBridgeBiTuple<A, B> leftParentTuple : leftParentTupleList) {
                if (!leftParentTuple.isDirty()) {
                    BavetJoinTriTuple<A, B, C> childTuple = createTuple(leftParentTuple, rightParentTuple);
                    leftParentTuple.getChildTupleSet().add(childTuple);
                    rightTupleSet.add(childTuple);
                    session.transitionTuple(childTuple, BavetTupleState.CREATING);
                }
            }
        }
    }

    public BavetIndex<BavetJoinBridgeBiTuple<A, B>> getLeftIndex() {
        return leftParentNode.getIndex();
    }

    public BavetIndex<BavetJoinBridgeUniTuple<C>> getRightIndex() {
        return rightParentNode.getIndex();
    }

    @Override
    public String toString() {
        return "Join() with " + childNodeList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
