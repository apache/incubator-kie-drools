package org.optaplanner.constraint.streams.common;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryService;

public abstract class AbstractConstraintStreamScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>>
        implements ScoreDirectorFactoryService<Solution_, Score_> {

    public abstract boolean supportsImplType(ConstraintStreamImplType constraintStreamImplType);

    public abstract AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> buildScoreDirectorFactory(
            SolutionDescriptor<Solution_> solutionDescriptor, ConstraintProvider constraintProvider,
            boolean droolsAlphaNetworkCompilationEnabled);

    public abstract ConstraintFactory buildConstraintFactory(SolutionDescriptor<Solution_> solutionDescriptor);

}
