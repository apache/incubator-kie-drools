package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractScorer;
import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.core.api.score.Score;

final class UniScorer<A> extends AbstractScorer<UniTuple<A>> {

    private final Function<A, UndoScoreImpacter> scoreImpacter;

    public UniScorer(String constraintPackage, String constraintName, Score<?> constraintWeight,
            Function<A, UndoScoreImpacter> scoreImpacter, int inputStoreIndex) {
        super(constraintPackage, constraintName, constraintWeight, inputStoreIndex);
        this.scoreImpacter = scoreImpacter;
    }

    @Override
    protected UndoScoreImpacter impact(UniTuple<A> tuple) {
        return scoreImpacter.apply(tuple.getFactA());
    }
}
