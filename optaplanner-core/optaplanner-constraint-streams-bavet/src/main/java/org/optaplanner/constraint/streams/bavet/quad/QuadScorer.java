package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.AbstractScorer;
import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.Score;

final class QuadScorer<A, B, C, D> extends AbstractScorer<QuadTuple<A, B, C, D>> {

    private final QuadFunction<A, B, C, D, UndoScoreImpacter> scoreImpacter;

    public QuadScorer(String constraintPackage, String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, UndoScoreImpacter> scoreImpacter, int inputStoreIndex) {
        super(constraintPackage, constraintName, constraintWeight, inputStoreIndex);
        this.scoreImpacter = scoreImpacter;
    }

    @Override
    protected UndoScoreImpacter impact(QuadTuple<A, B, C, D> tuple) {
        return scoreImpacter.apply(tuple.getFactA(), tuple.getFactB(), tuple.getFactC(), tuple.getFactD());
    }
}
