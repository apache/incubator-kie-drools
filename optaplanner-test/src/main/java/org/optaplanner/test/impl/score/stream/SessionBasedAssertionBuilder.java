package org.optaplanner.test.impl.score.stream;

import java.util.Objects;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

final class SessionBasedAssertionBuilder<Solution_, Score_ extends Score<Score_>> {

    private final AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> constraintStreamScoreDirectorFactory;

    public SessionBasedAssertionBuilder(
            AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> constraintStreamScoreDirectorFactory) {
        this.constraintStreamScoreDirectorFactory = Objects.requireNonNull(constraintStreamScoreDirectorFactory);
    }

    public DefaultMultiConstraintAssertion<Score_> multiConstraintGiven(ConstraintProvider constraintProvider,
            Object... facts) {
        AbstractScoreInliner<Score_> scoreInliner = constraintStreamScoreDirectorFactory.fireAndForget(facts);
        return new DefaultMultiConstraintAssertion<>(constraintProvider, scoreInliner.extractScore(0),
                scoreInliner.getConstraintMatchTotalMap(), scoreInliner.getIndictmentMap());
    }

    public DefaultSingleConstraintAssertion<Solution_, Score_> singleConstraintGiven(Object... facts) {
        AbstractScoreInliner<Score_> scoreInliner = constraintStreamScoreDirectorFactory.fireAndForget(facts);
        return new DefaultSingleConstraintAssertion<>(constraintStreamScoreDirectorFactory,
                scoreInliner.extractScore(0), scoreInliner.getConstraintMatchTotalMap(),
                scoreInliner.getIndictmentMap());
    }

}
