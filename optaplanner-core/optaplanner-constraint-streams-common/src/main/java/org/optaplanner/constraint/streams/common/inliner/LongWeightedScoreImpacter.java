package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;
import java.util.Objects;

final class LongWeightedScoreImpacter implements WeightedScoreImpacter {

    private final LongImpactFunction impactFunction;

    public LongWeightedScoreImpacter(LongImpactFunction impactFunction) {
        this.impactFunction = Objects.requireNonNull(impactFunction);
    }

    @Override
    public UndoScoreImpacter impactScore(int matchWeight, JustificationsSupplier justificationsSupplier) {
        return impactFunction.impact(matchWeight, justificationsSupplier); // int can be cast to long
    }

    @Override
    public UndoScoreImpacter impactScore(long matchWeight, JustificationsSupplier justificationsSupplier) {
        return impactFunction.impact(matchWeight, justificationsSupplier);
    }

    @Override
    public UndoScoreImpacter impactScore(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        throw new UnsupportedOperationException("Impossible state: passing BigDecimal into a long impacter.");
    }

}
