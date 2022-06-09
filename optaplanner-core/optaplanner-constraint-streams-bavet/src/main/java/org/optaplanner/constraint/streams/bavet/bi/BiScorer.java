package org.optaplanner.constraint.streams.bavet.bi;

import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.bavet.common.AbstractScorer;
import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.core.api.score.Score;

final class BiScorer<A, B> extends AbstractScorer<BiTuple<A, B>> {

    private final BiFunction<A, B, UndoScoreImpacter> scoreImpacter;

    public BiScorer(String constraintPackage, String constraintName, Score<?> constraintWeight,
            BiFunction<A, B, UndoScoreImpacter> scoreImpacter, int inputStoreIndex) {
        super(constraintPackage, constraintName, constraintWeight, inputStoreIndex);
        this.scoreImpacter = scoreImpacter;
    }

    @Override
    protected UndoScoreImpacter impact(BiTuple<A, B> tuple) {
        return scoreImpacter.apply(tuple.factA, tuple.factB);
    }
}
