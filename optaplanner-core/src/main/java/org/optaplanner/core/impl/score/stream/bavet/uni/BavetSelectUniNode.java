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

import org.optaplanner.core.impl.score.stream.bavet.session.BavetConstraintSession;

public final class BavetSelectUniNode<A> extends BavetAbstractUniNode<A> {

    private final Class<A> selectClass;
    private final BavetAbstractUniNode<A> nextNode;

    public BavetSelectUniNode(BavetConstraintSession session, int nodeOrder,
            Class<A> selectClass, BavetAbstractUniNode<A> nextNode) {
        super(session, nodeOrder);
        this.selectClass = selectClass;
        this.nextNode = nextNode;
    }

    public BavetSelectUniTuple<A> createTuple(A a) {
        return new BavetSelectUniTuple<>(this, a);
    }

    @Override
    public BavetAbstractUniTuple<A> createTuple(BavetAbstractUniTuple<A> previousTuple) {
        throw new IllegalStateException("The select node (" + getClass().getSimpleName()
                + ") can't have a previousTuple (" + previousTuple + ");");
    }

    public void refresh(BavetSelectUniTuple<A> tuple) {
        A a = tuple.getFactA();
        BavetAbstractUniTuple<A> downstreamTuple = tuple.getDownstreamTuple();
        if (downstreamTuple != null) {
            // TODO the entire Select node isn't really doing anything, so the destruction/construction is just an update op
            downstreamTuple.kill();
            session.addDirty(downstreamTuple);
        }
        if (tuple.isActive()) {
            BavetAbstractUniTuple<A> nextTuple = nextNode.createTuple(tuple);
            tuple.setDownstreamTuple(nextTuple);
            session.addDirty(nextTuple);
        }
        tuple.refreshed();
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
