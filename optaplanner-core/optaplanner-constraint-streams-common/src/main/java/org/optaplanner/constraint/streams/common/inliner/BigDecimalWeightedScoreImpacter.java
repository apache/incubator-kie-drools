package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;
import java.util.Objects;

final class BigDecimalWeightedScoreImpacter implements WeightedScoreImpacter {

    private final BigDecimalImpactFunction impactFunction;

    public BigDecimalWeightedScoreImpacter(BigDecimalImpactFunction impactFunction) {
        this.impactFunction = Objects.requireNonNull(impactFunction);
    }

    @Override
    public UndoScoreImpacter impactScore(int matchWeight, JustificationsSupplier justificationsSupplier) {
        return impactFunction.impact(BigDecimal.valueOf(matchWeight), justificationsSupplier);
    }

    @Override
    public UndoScoreImpacter impactScore(long matchWeight, JustificationsSupplier justificationsSupplier) {
        return impactFunction.impact(BigDecimal.valueOf(matchWeight), justificationsSupplier);
    }

    @Override
    public UndoScoreImpacter impactScore(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        return impactFunction.impact(matchWeight, justificationsSupplier);
    }

}
