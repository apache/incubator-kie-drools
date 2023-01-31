package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class SimpleBigDecimalScoreContext extends ScoreContext<SimpleBigDecimalScore> {

    private final Consumer<BigDecimal> scoreUpdater;

    public SimpleBigDecimalScoreContext(AbstractScoreInliner<SimpleBigDecimalScore> parent, Constraint constraint,
            SimpleBigDecimalScore constraintWeight, Consumer<BigDecimal> scoreUpdater) {
        super(parent, constraint, constraintWeight);
        this.scoreUpdater = scoreUpdater;
    }

    public UndoScoreImpacter changeScoreBy(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        BigDecimal impact = constraintWeight.score().multiply(matchWeight);
        scoreUpdater.accept(impact);
        UndoScoreImpacter undoScoreImpact = () -> scoreUpdater.accept(impact.negate());
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, SimpleBigDecimalScore.of(impact), justificationsSupplier);
    }

}
