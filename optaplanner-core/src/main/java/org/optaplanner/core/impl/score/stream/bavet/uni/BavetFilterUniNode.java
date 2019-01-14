/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet.uni;

import java.util.function.Predicate;

import org.optaplanner.core.impl.score.stream.bavet.session.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetTupleState;

public final class BavetFilterUniNode<A> extends BavetAbstractUniNode<A> {

    private final Predicate<A> predicate;
    private final BavetAbstractUniNode<A> nextNode;

    public BavetFilterUniNode(BavetConstraintSession session, int nodeOrder,
            Predicate<A> predicate, BavetAbstractUniNode<A> nextNode) {
        super(session, nodeOrder);
        this.predicate = predicate;
        this.nextNode = nextNode;
    }

    @Override
    public BavetFilterUniTuple<A> createTuple(BavetAbstractUniTuple<A> previousTuple) {
        return new BavetFilterUniTuple<>(this, previousTuple);
    }

    public void refresh(BavetFilterUniTuple<A> tuple) {
        A a = tuple.getFactA();
        BavetAbstractUniTuple<A> downstreamTuple = tuple.getDownstreamTuple();
        if (downstreamTuple != null) {
            session.transitionTuple(downstreamTuple, BavetTupleState.DYING);
        }
        if (tuple.isActive()) {
            if (predicate.test(a)) {
                BavetAbstractUniTuple<A> nextTuple = nextNode.createTuple(tuple);
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
