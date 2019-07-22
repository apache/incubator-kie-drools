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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndex;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniNode;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniTuple;

public final class BavetJoinBiNode<A, B> extends BavetAbstractBiNode<A, B> {

    private final BavetJoinBridgeUniNode<A> leftParentNode;
    private final BavetJoinBridgeUniNode<B> rightParentNode;

    private final List<BavetAbstractBiNode<A, B>> childNodeList = new ArrayList<>();

    public BavetJoinBiNode(BavetConstraintSession session, int nodeOrder,
            BavetJoinBridgeUniNode<A> leftParentNode, BavetJoinBridgeUniNode<B> rightParentNode) {
        super(session, nodeOrder);
        this.leftParentNode = leftParentNode;
        this.rightParentNode = rightParentNode;
    }

    @Override
    public void addChildNode(BavetAbstractBiNode<A, B> childNode) {
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
    public BavetJoinBiTuple<A, B> createTuple(BavetAbstractBiTuple<A, B> parentTuple) {
        throw new IllegalStateException("The join node (" + getClass().getSimpleName()
                + ") can't have a parentTuple (" + parentTuple + ");");
    }

    public BavetJoinBiTuple<A, B> createTuple(
            BavetJoinBridgeUniTuple<A> aTuple, BavetJoinBridgeUniTuple<B> bTuple) {
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

    public void refreshChildTuplesLeft(BavetJoinBridgeUniTuple<A> aParentTuple) {
        Set<BavetJoinTuple> aTupleSet = aParentTuple.getChildTupleSet();
        for (BavetJoinTuple tuple_ : aTupleSet) {
            BavetJoinBiTuple<A, B> tuple = (BavetJoinBiTuple<A, B>) tuple_;
            boolean removed = tuple.getBTuple().getChildTupleSet().remove(tuple);
            if (!removed) {
                throw new IllegalStateException("Impossible state: the fact (" + tuple.getFactA()
                        + ")'s tuple cannot be removed from the other fact (" + tuple.getFactB()
                        + ")'s join bridge.");
            }
            session.transitionTuple(tuple, BavetTupleState.DYING);
        }
        aTupleSet.clear();
        if (aParentTuple.isActive()) {
            Set<BavetJoinBridgeUniTuple<B>> bParentTupleList = getRightIndex().get(aParentTuple.getIndexProperties());
            for (BavetJoinBridgeUniTuple<B> bParentTuple : bParentTupleList) {
                if (!bParentTuple.isDirty()) {
                    BavetJoinBiTuple<A, B> childTuple = createTuple(aParentTuple, bParentTuple);
                    aTupleSet.add(childTuple);
                    bParentTuple.getChildTupleSet().add(childTuple);
                    session.transitionTuple(childTuple, BavetTupleState.CREATING);
                }
            }
        }
    }

    public void refreshChildTuplesRight(BavetJoinBridgeUniTuple<B> bParentTuple) {
        Set<BavetJoinTuple> bTupleSet = bParentTuple.getChildTupleSet();
        for (BavetJoinTuple uncastTuple : bTupleSet) {
            BavetJoinBiTuple<A, B> tuple = (BavetJoinBiTuple<A, B>) uncastTuple;
            boolean removed = tuple.getATuple().getChildTupleSet().remove(tuple);
            if (!removed) {
                throw new IllegalStateException("Impossible state: the fact (" + tuple.getFactA()
                        + ")'s tuple cannot be removed from the other fact (" + tuple.getFactB()
                        + ")'s join bridge.");
            }
            session.transitionTuple(tuple, BavetTupleState.DYING);
        }
        bTupleSet.clear();
        if (bParentTuple.isActive()) {
            Set<BavetJoinBridgeUniTuple<A>> aParentTupleList = getLeftIndex().get(bParentTuple.getIndexProperties());
            for (BavetJoinBridgeUniTuple<A> aParentTuple : aParentTupleList) {
                if (!aParentTuple.isDirty()) {
                    BavetJoinBiTuple<A, B> childTuple = createTuple(aParentTuple, bParentTuple);
                    aParentTuple.getChildTupleSet().add(childTuple);
                    bTupleSet.add(childTuple);
                    session.transitionTuple(childTuple, BavetTupleState.CREATING);
                }
            }
        }
    }

    public BavetIndex<A, BavetJoinBridgeUniTuple<A>> getLeftIndex() {
        return leftParentNode.getIndex();
    }

    public BavetIndex<B, BavetJoinBridgeUniTuple<B>> getRightIndex() {
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
