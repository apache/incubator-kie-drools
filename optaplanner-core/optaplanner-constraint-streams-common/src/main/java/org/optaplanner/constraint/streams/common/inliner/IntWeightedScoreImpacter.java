package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;
import java.util.Objects;

final class IntWeightedScoreImpacter implements WeightedScoreImpacter {

    private final IntImpactFunction impactFunction;

    public IntWeightedScoreImpacter(IntImpactFunction impactFunction) {
        this.impactFunction = Objects.requireNonNull(impactFunction);
    }

    @Override
    public UndoScoreImpacter impactScore(int matchWeight, JustificationsSupplier justificationsSupplier) {
        return impactFunction.impact(matchWeight, justificationsSupplier);
    }

    @Override
    public UndoScoreImpacter impactScore(long matchWeight, JustificationsSupplier justificationsSupplier) {
        throw new UnsupportedOperationException("Impossible state: passing long into an int impacter.");
    }

    @Override
    public UndoScoreImpacter impactScore(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        throw new UnsupportedOperationException("Impossible state: passing BigDecimal into an int impacter.");
    }

}
