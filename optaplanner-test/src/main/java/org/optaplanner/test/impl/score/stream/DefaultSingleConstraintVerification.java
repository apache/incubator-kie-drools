package org.optaplanner.test.impl.score.stream;

import java.util.Objects;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.test.api.score.stream.SingleConstraintVerification;

public final class DefaultSingleConstraintVerification<Solution_, Score_ extends Score<Score_>>
        implements SingleConstraintVerification<Solution_> {

    private final AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory;
    private final SessionBasedAssertionBuilder<Solution_, Score_> sessionBasedAssertionBuilder;

    DefaultSingleConstraintVerification(AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
        this.sessionBasedAssertionBuilder = new SessionBasedAssertionBuilder(scoreDirectorFactory);
    }

    @Override
    public final DefaultSingleConstraintAssertion<Solution_, Score_> given(Object... facts) {
        return sessionBasedAssertionBuilder.singleConstraintGiven(facts);
    }

    @Override
    public final DefaultSingleConstraintAssertion<Solution_, Score_> givenSolution(Solution_ solution) {
        try (InnerScoreDirector<Solution_, Score_> scoreDirector = scoreDirectorFactory.buildScoreDirector(true, true)) {
            scoreDirector.setWorkingSolution(Objects.requireNonNull(solution));
            return new DefaultSingleConstraintAssertion<>(scoreDirectorFactory, scoreDirector.calculateScore(),
                    scoreDirector.getConstraintMatchTotalMap(), scoreDirector.getIndictmentMap());
        }
    }

}
