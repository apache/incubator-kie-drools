package org.optaplanner.constraint.streams.common;

import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;

/**
 * FP streams implementation of {@link ScoreDirectorFactory}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the score type to go with the solution
 * @see ScoreDirectorFactory
 */
public abstract class AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_ extends Score<Score_>>
        extends AbstractScoreDirectorFactory<Solution_, Score_> {

    protected AbstractConstraintStreamScoreDirectorFactory(SolutionDescriptor<Solution_> solutionDescriptor) {
        super(solutionDescriptor);
    }

    public abstract AbstractScoreInliner<Score_> fireAndForget(Object... facts);

    public abstract Constraint[] getConstraints();

}
