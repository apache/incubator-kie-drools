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

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.optaplanner.core.impl.score.stream.bavet.session.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetAbstractUniNode;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetAbstractUniTuple;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFilterUniTuple;

public final class BavetFilterBiNode<A, B> extends BavetAbstractBiNode<A, B> {

    private final BiPredicate<A, B> predicate;
    private final BavetAbstractBiNode<A, B> nextNode;

    public BavetFilterBiNode(BavetConstraintSession session, int nodeOrder,
            BiPredicate<A, B> predicate, BavetAbstractBiNode<A, B> nextNode) {
        super(session, nodeOrder);
        this.predicate = predicate;
        this.nextNode = nextNode;
    }

    @Override
    public BavetFilterBiTuple<A, B> createTuple(BavetAbstractBiTuple<A, B> previousTuple) {
        return new BavetFilterBiTuple<>(this, previousTuple);
    }

    public void refresh(BavetFilterBiTuple<A, B> tuple) {
        A a = tuple.getFactA();
        B b = tuple.getFactB();
        BavetAbstractBiTuple<A, B> downstreamTuple = tuple.getDownstreamTuple();
        if (downstreamTuple != null) {
            session.transitionTuple(downstreamTuple, BavetTupleState.DYING);
        }
        if (tuple.isActive()) {
            if (predicate.test(a, b)) {
                BavetAbstractBiTuple<A, B> nextTuple = nextNode.createTuple(tuple);
                tuple.setDownstreamTuple(nextTuple);
                session.transitionTuple(nextTuple, BavetTupleState.CREATING);
            }
        }
        tuple.refreshed();
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
