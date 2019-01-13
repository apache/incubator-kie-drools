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

import java.util.function.ToIntFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetNodeBuildPolicy;

public final class BavetIntScoringUniConstraintStream<Solution_, A> extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final ToIntFunction<A> matchWeigher;

    public BavetIntScoringUniConstraintStream(BavetConstraint<Solution_> bavetConstraint, ToIntFunction<A> matchWeigher) {
        super(bavetConstraint);
        this.matchWeigher = matchWeigher;
        if (matchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    // ************************************************************************
    // Node creation methods
    // ************************************************************************

    @Override
    protected BavetIntScoringUniNode<A> createNode(BavetNodeBuildPolicy buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> nextNode) {
        if (nextNode != null) {
            throw new IllegalStateException("Impossible state: the stream (" + this + ") has one or more nextStreams ("
                    + nextStreamList + ") but it's an endpoint.");
        }
        return new BavetIntScoringUniNode<>(buildPolicy.getSession(), nodeOrder,
                bavetConstraint.getConstraintPackage(), bavetConstraint.getConstraintName(),
                ((SimpleScore) constraintWeight).getScore(), matchWeigher);
    }

}
