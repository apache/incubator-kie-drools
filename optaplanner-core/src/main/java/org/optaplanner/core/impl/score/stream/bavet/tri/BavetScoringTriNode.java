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

package org.optaplanner.core.impl.score.stream.bavet.tri;

import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetScoringNode;

public final class BavetScoringTriNode<A, B, C> extends BavetAbstractTriNode<A, B, C> implements BavetScoringNode {

    private final Score<?> constraintWeight;
    private final TriFunction<A, B, C, UndoScoreImpacter> scoreImpacter;

    public BavetScoringTriNode(BavetConstraintSession session, int nodeIndex, Score<?> constraintWeight,
            TriFunction<A, B, C, UndoScoreImpacter> scoreImpacter) {
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
    public BavetScoringTriTuple<A, B, C> createTuple(BavetAbstractTriTuple<A, B, C> parentTuple) {
        return new BavetScoringTriTuple<>(this, parentTuple);
    }

    @Override
    public void refresh(BavetAbstractTuple uncastTuple) {
        BavetScoringTriTuple<A, B, C> tuple = (BavetScoringTriTuple<A, B, C>) uncastTuple;
        A a = tuple.getFactA();
        B b = tuple.getFactB();
        C c = tuple.getFactC();
        UndoScoreImpacter oldUndoScoreImpacter = tuple.getUndoScoreImpacter();
        if (oldUndoScoreImpacter != null) {
            oldUndoScoreImpacter.run();
        }
        if (tuple.isActive()) {
            UndoScoreImpacter undoScoreImpacter = scoreImpacter.apply(a, b, c);
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
