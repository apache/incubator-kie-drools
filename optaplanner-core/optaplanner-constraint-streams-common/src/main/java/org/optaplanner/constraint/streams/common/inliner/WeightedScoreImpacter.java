package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;

/**
 * There are several valid ways how an impacter could be called from a constraint stream:
 *
 * <ul>
 * <li>{@code .penalize(..., (int) 1)}</li>
 * <li>{@code .penalizeLong(..., (int) 1)}</li>
 * <li>{@code .penalizeLong(..., (long) 1)}</li>
 * <li>{@code .penalizeBigDecimal(..., (int) 1)}</li>
 * <li>{@code .penalizeBigDecimal(..., (long) 1)}</li>
 * <li>{@code .penalizeBigDecimal(..., BigDecimal.ONE)}</li>
 * <li>Plus reward variants of the above.</li>
 * </ul>
 *
 * An implementation of this interface can throw an {@link UnsupportedOperationException}
 * for the method types it doesn't support. The CS API guarantees no types are mixed. For example,
 * a {@link BigDecimal} parameter method won't be called on an instance built with an {@link IntImpactFunction}.
 */
public interface WeightedScoreImpacter {

    static WeightedScoreImpacter of(IntImpactFunction impactFunction) {
        return new IntWeightedScoreImpacter(impactFunction);
    }

    static WeightedScoreImpacter of(LongImpactFunction impactFunction) {
        return new LongWeightedScoreImpacter(impactFunction);
    }

    static WeightedScoreImpacter of(BigDecimalImpactFunction impactFunction) {
        return new BigDecimalWeightedScoreImpacter(impactFunction);
    }

    /**
     * @param matchWeight never null
     * @param justificationsSupplier never null
     * @return never null
     */
    UndoScoreImpacter impactScore(int matchWeight, JustificationsSupplier justificationsSupplier);

    /**
     * @param matchWeight never null
     * @param justificationsSupplier never null
     * @return never null
     */
    UndoScoreImpacter impactScore(long matchWeight, JustificationsSupplier justificationsSupplier);

    /**
     * @param matchWeight never null
     * @param justificationsSupplier never null
     * @return never null
     */
    UndoScoreImpacter impactScore(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier);

    @FunctionalInterface
    interface IntImpactFunction {

        UndoScoreImpacter impact(int matchWeight, JustificationsSupplier justificationsSupplier);

    }

    @FunctionalInterface
    interface LongImpactFunction {

        UndoScoreImpacter impact(long matchWeight, JustificationsSupplier justificationsSupplier);

    }

    @FunctionalInterface
    interface BigDecimalImpactFunction {

        UndoScoreImpacter impact(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier);

    }

}
