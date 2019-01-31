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

import java.util.function.ToIntBiFunction;

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;

public final class BavetIntScoringBiNode<A, B> extends BavetAbstractBiNode<A, B> {

    private final String constraintPackage;
    private final String constraintName;
    private final int constraintWeight;
    private final ToIntBiFunction<A, B> matchWeigher;

    public BavetIntScoringBiNode(BavetConstraintSession session, int nodeOrder,
            String constraintPackage, String constraintName, int constraintWeight,
            ToIntBiFunction<A, B> matchWeigher) {
        super(session, nodeOrder);
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.constraintWeight = constraintWeight;
        this.matchWeigher = matchWeigher;
    }

    @Override
    public BavetIntScoringBiTuple<A, B> createTuple(BavetAbstractBiTuple<A, B> parentTuple) {
        return new BavetIntScoringBiTuple<>(this, parentTuple);
    }

    public void refresh(BavetIntScoringBiTuple<A, B> tuple) {
        A a = tuple.getFactA();
        B b = tuple.getFactB();
        int oldTupleScore = tuple.getScore();
        int scoreDelta = - oldTupleScore;
        if (tuple.isActive()) {
            int newTupleScore = constraintWeight * matchWeigher.applyAsInt(a, b);
            scoreDelta += newTupleScore;
            tuple.setScore(newTupleScore);
        }
        session.addScoreDelta(scoreDelta);
        tuple.refreshed();
    }

    @Override
    public String toString() {
        return "IntScore(" + constraintWeight + ")";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
