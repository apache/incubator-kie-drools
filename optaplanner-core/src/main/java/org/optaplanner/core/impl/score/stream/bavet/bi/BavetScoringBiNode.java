/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.function.BiFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetScoringNode;

public final class BavetScoringBiNode<A, B> extends BavetAbstractBiNode<A, B> implements BavetScoringNode {

    private final Score<?> constraintWeight;
    private final BiFunction<A, B, UndoScoreImpacter> scoreImpacter;

    public BavetScoringBiNode(BavetConstraintSession session, int nodeIndex, Score<?> constraintWeight,
            BiFunction<A, B, UndoScoreImpacter> scoreImpacter) {
        super(session, nodeIndex);
        this.constraintWeight = constraintWeight;
        this.scoreImpacter = scoreImpacter;
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    // No node sharing

    // ************************************************************************
    // Runtime
    // ************************************************************************

    @Override
    public BavetScoringBiTuple<A, B> createTuple(BavetAbstractBiTuple<A, B> parentTuple) {
        return new BavetScoringBiTuple<>(this, parentTuple);
    }

    @Override
    public void refresh(BavetAbstractTuple uncastTuple) {
        BavetScoringBiTuple<A, B> tuple = (BavetScoringBiTuple<A, B>) uncastTuple;
        A a = tuple.getFactA();
        B b = tuple.getFactB();
        UndoScoreImpacter oldUndoScoreImpacter = tuple.getUndoScoreImpacter();
        if (oldUndoScoreImpacter != null) {
            oldUndoScoreImpacter.run();
        }
        if (tuple.isActive()) {
            UndoScoreImpacter undoScoreImpacter = scoreImpacter.apply(a, b);
            tuple.setUndoScoreImpacter(undoScoreImpacter);
        } else {
            tuple.setUndoScoreImpacter(null);
        }
    }

    @Override
    public String toString() {
        return "Scoring(" + constraintWeight + ")";
    }

}
