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
import java.util.function.ToIntBiFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetNodeBuildPolicy;

public final class BavetIntScoringBiConstraintStream<Solution_, A, B> extends BavetAbstractBiConstraintStream<Solution_, A, B> {

    private final ToIntBiFunction<A, B> matchWeigher;

    public BavetIntScoringBiConstraintStream(BavetConstraint<Solution_> bavetConstraint, ToIntBiFunction<A, B> matchWeigher) {
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
    protected BavetIntScoringBiNode<A, B> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, List<BavetAbstractBiNode<A, B>> childNodeList) {
        if (!childNodeList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") has an non-empty childNodeList (" + childNodeList + ") but it's an endpoint.");
        }
        // TODO HACK until we support all score types
        if (constraintWeight instanceof HardSoftScore) {
            constraintWeight = SimpleScore.valueOf(((HardSoftScore) constraintWeight).getHardScore());
        }
        return new BavetIntScoringBiNode<>(buildPolicy.getSession(), nodeOrder,
                constraint.getConstraintPackage(), constraint.getConstraintName(),
                ((SimpleScore) constraintWeight).getScore(), matchWeigher);
    }

    @Override
    public String toString() {
        return "IntScore() with " + childStreamList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
