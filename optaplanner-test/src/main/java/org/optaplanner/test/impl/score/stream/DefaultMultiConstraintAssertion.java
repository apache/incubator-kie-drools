package org.optaplanner.test.impl.score.stream;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Map;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.score.DefaultScoreExplanation;
import org.optaplanner.test.api.score.stream.MultiConstraintAssertion;

public final class DefaultMultiConstraintAssertion<Score_ extends Score<Score_>>
        implements MultiConstraintAssertion {

    private final ConstraintProvider constraintProvider;
    private final Score_ actualScore;
    private final Collection<ConstraintMatchTotal<Score_>> constraintMatchTotalCollection;
    private final Collection<Indictment<Score_>> indictmentCollection;

    DefaultMultiConstraintAssertion(ConstraintProvider constraintProvider, Score_ actualScore,
            Map<String, ConstraintMatchTotal<Score_>> constraintMatchTotalMap,
            Map<Object, Indictment<Score_>> indictmentMap) {
        this.constraintProvider = requireNonNull(constraintProvider);
        this.actualScore = requireNonNull(actualScore);
        this.constraintMatchTotalCollection = requireNonNull(constraintMatchTotalMap).values();
        this.indictmentCollection = requireNonNull(indictmentMap).values();
    }

    @Override
    public final void scores(Score<?> score, String message) {
        if (actualScore.equals(score)) {
            return;
        }
        Class<?> constraintProviderClass = constraintProvider.getClass();
        String expectation = message == null ? "Broken expectation." : message;
        throw new AssertionError(expectation + System.lineSeparator() +
                "  Constraint provider: " + constraintProviderClass + System.lineSeparator() +
                "       Expected score: " + score + " (" + score.getClass() + ")" + System.lineSeparator() +
                "         Actual score: " + actualScore + " (" + actualScore.getClass() + ")" +
                System.lineSeparator() + System.lineSeparator() +
                "  " + DefaultScoreExplanation.explainScore(actualScore, constraintMatchTotalCollection,
                        indictmentCollection));
    }

}
