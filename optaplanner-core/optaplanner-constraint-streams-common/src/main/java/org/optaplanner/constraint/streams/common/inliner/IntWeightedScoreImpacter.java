package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;
import java.util.Objects;

import org.optaplanner.core.api.score.Score;

final class IntWeightedScoreImpacter<Score_ extends Score<Score_>, Context_ extends ScoreContext<Score_>>
        implements WeightedScoreImpacter<Score_, Context_> {

    private final IntImpactFunction<Score_, Context_> impactFunction;
    private final Context_ context;

    public IntWeightedScoreImpacter(IntImpactFunction<Score_, Context_> impactFunction, Context_ context) {
        this.impactFunction = Objects.requireNonNull(impactFunction);
        this.context = context;
    }

    @Override
    public UndoScoreImpacter impactScore(int matchWeight, JustificationsSupplier justificationsSupplier) {
        return impactFunction.impact(context, matchWeight, justificationsSupplier);
    }

    @Override
    public UndoScoreImpacter impactScore(long matchWeight, JustificationsSupplier justificationsSupplier) {
        throw new UnsupportedOperationException("Impossible state: passing long into an int impacter.");
    }

    @Override
    public UndoScoreImpacter impactScore(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        throw new UnsupportedOperationException("Impossible state: passing BigDecimal into an int impacter.");
    }

    @Override
    public Context_ getContext() {
        return context;
    }

}
