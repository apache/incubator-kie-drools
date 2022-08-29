package org.optaplanner.test.impl.score.stream;

import java.util.Objects;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.test.api.score.stream.MultiConstraintVerification;

public final class DefaultMultiConstraintVerification<Solution_, Score_ extends Score<Score_>>
        implements MultiConstraintVerification<Solution_> {

    private final AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory;
    private final ConstraintProvider constraintProvider;
    private final SessionBasedAssertionBuilder<Solution_, Score_> sessionBasedAssertionBuilder;

    DefaultMultiConstraintVerification(AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory,
            ConstraintProvider constraintProvider) {
        this.scoreDirectorFactory = scoreDirectorFactory;
        this.constraintProvider = constraintProvider;
        this.sessionBasedAssertionBuilder = new SessionBasedAssertionBuilder<>(scoreDirectorFactory);
    }

    @Override
    public DefaultMultiConstraintAssertion<Score_> given(Object... facts) {
        return sessionBasedAssertionBuilder.multiConstraintGiven(constraintProvider, facts);
    }

    @Override
    public DefaultMultiConstraintAssertion<Score_> givenSolution(Solution_ solution) {
        try (InnerScoreDirector<Solution_, Score_> scoreDirector =
                scoreDirectorFactory.buildScoreDirector(true, true)) {
            scoreDirector.setWorkingSolution(Objects.requireNonNull(solution));
            return new DefaultMultiConstraintAssertion<>(constraintProvider, scoreDirector.calculateScore(),
                    scoreDirector.getConstraintMatchTotalMap(), scoreDirector.getIndictmentMap());
        }
    }

}
