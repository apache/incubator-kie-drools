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

package org.optaplanner.core.impl.score.stream.bavet.uni;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetScoringTuple;

public final class BavetScoringUniTuple<A> extends BavetAbstractUniTuple<A> implements BavetScoringTuple {

    private final BavetScoringUniNode<A> node;
    private final BavetAbstractUniTuple<A> parentTuple;

    private UndoScoreImpacter undoScoreImpacter = null;
    /** Always null if {@link BavetConstraintSession#isConstraintMatchEnabled()} is false. */
    private Score<?> matchScore = null;

    public BavetScoringUniTuple(BavetScoringUniNode<A> node, BavetAbstractUniTuple<A> parentTuple) {
        this.node = node;
        this.parentTuple = parentTuple;
    }

    @Override
    public void refresh() {
        node.refresh(this);
    }

    @Override
    public String toString() {
        return "Scoring(" + getFactsString() + ")";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetScoringUniNode<A> getNode() {
        return node;
    }

    @Override
    public A getFactA() {
        return parentTuple.getFactA();
    }

    @Override
    public UndoScoreImpacter getUndoScoreImpacter() {
        return undoScoreImpacter;
    }

    @Override
    public void setUndoScoreImpacter(UndoScoreImpacter undoScoreImpacter) {
        this.undoScoreImpacter = undoScoreImpacter;
    }

    @Override
    public Score<?> getMatchScore() {
        return matchScore;
    }

    @Override
    public void setMatchScore(Score<?> matchScore) {
        this.matchScore = matchScore;
    }

}
