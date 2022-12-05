package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;

import org.optaplanner.core.api.score.Score;

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
public interface WeightedScoreImpacter<Score_ extends Score<Score_>, Context_ extends ScoreContext<Score_>> {

    static <Score_ extends Score<Score_>, Context_ extends ScoreContext<Score_>> WeightedScoreImpacter<Score_, Context_>
            of(Context_ context, IntImpactFunction<Score_, Context_> impactFunction) {
        return new IntWeightedScoreImpacter<>(impactFunction, context);
    }

    static <Score_ extends Score<Score_>, Context_ extends ScoreContext<Score_>> WeightedScoreImpacter<Score_, Context_>
            of(Context_ context, LongImpactFunction<Score_, Context_> impactFunction) {
        return new LongWeightedScoreImpacter<>(impactFunction, context);
    }

    static <Score_ extends Score<Score_>, Context_ extends ScoreContext<Score_>> WeightedScoreImpacter<Score_, Context_>
            of(Context_ context, BigDecimalImpactFunction<Score_, Context_> impactFunction) {
        return new BigDecimalWeightedScoreImpacter<>(impactFunction, context);
    }

    /**
     * @param matchWeight never null
     * @param justificationsSupplier ignored unless constraint match enableds
     * @return never null
     */
    UndoScoreImpacter impactScore(int matchWeight, JustificationsSupplier justificationsSupplier);

    /**
     * @param matchWeight never null
     * @param justificationsSupplier ignored unless constraint match enabled
     * @return never null
     */
    UndoScoreImpacter impactScore(long matchWeight, JustificationsSupplier justificationsSupplier);

    /**
     * @param matchWeight never null
     * @param justificationsSupplier ignored unless constraint match enabled
     * @return never null
     */
    UndoScoreImpacter impactScore(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier);

    Context_ getContext();

    @FunctionalInterface
    interface IntImpactFunction<Score_ extends Score<Score_>, Context_ extends ScoreContext<Score_>> {

        UndoScoreImpacter impact(Context_ context, int matchWeight, JustificationsSupplier justificationsSupplier);

    }

    @FunctionalInterface
    interface LongImpactFunction<Score_ extends Score<Score_>, Context_ extends ScoreContext<Score_>> {

        UndoScoreImpacter impact(Context_ context, long matchWeight, JustificationsSupplier justificationsSupplier);

    }

    @FunctionalInterface
    interface BigDecimalImpactFunction<Score_ extends Score<Score_>, Context_ extends ScoreContext<Score_>> {

        UndoScoreImpacter impact(Context_ context, BigDecimal matchWeight, JustificationsSupplier justificationsSupplier);

    }

}
