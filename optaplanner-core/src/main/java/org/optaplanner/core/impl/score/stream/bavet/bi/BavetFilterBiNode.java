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
import java.util.function.BiPredicate;

import org.optaplanner.core.impl.score.stream.bavet.session.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetTupleState;

public final class BavetFilterBiNode<A, B> extends BavetAbstractBiNode<A, B> {

    private final BiPredicate<A, B> predicate;

    private final List<BavetAbstractBiNode<A, B>> childNodeList;

    public BavetFilterBiNode(BavetConstraintSession session, int nodeOrder,
            BiPredicate<A, B> predicate, List<BavetAbstractBiNode<A, B>> childNodeList) {
        super(session, nodeOrder);
        this.predicate = predicate;
        this.childNodeList = childNodeList;
    }

    @Override
    public BavetFilterBiTuple<A, B> createTuple(BavetAbstractBiTuple<A, B> parentTuple) {
        return new BavetFilterBiTuple<>(this, parentTuple);
    }

    public void refresh(BavetFilterBiTuple<A, B> tuple) {
        A a = tuple.getFactA();
        B b = tuple.getFactB();
        List<BavetAbstractBiTuple<A, B>> childTupleList = tuple.getChildTupleList();
        for (BavetAbstractBiTuple<A, B> childTuple : childTupleList) {
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleList.clear();
        if (tuple.isActive()) {
            if (predicate.test(a, b)) {
                for (BavetAbstractBiNode<A, B> childNode : childNodeList) {
                    BavetAbstractBiTuple<A, B> childTuple = childNode.createTuple(tuple);
                    childTupleList.add(childTuple);
                    session.transitionTuple(childTuple, BavetTupleState.CREATING);
                }
            }
        }
        tuple.refreshed();
    }

    @Override
    public String toString() {
        return "Filter() to " + childNodeList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
