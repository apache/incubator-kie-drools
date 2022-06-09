package org.optaplanner.core.impl.heuristic.selector;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public abstract class AbstractSelectorFactoryTest {

    public HeuristicConfigPolicy<TestdataSolution> buildHeuristicConfigPolicy() {
        return buildHeuristicConfigPolicy(TestdataSolution.buildSolutionDescriptor());
    }

    public <Solution_> HeuristicConfigPolicy<Solution_>
            buildHeuristicConfigPolicy(SolutionDescriptor<Solution_> solutionDescriptor) {
        InnerScoreDirectorFactory<Solution_, SimpleScore> scoreDirectorFactory = mock(InnerScoreDirectorFactory.class);
        when(scoreDirectorFactory.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        when(scoreDirectorFactory.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        return new HeuristicConfigPolicy.Builder<>(EnvironmentMode.REPRODUCIBLE, null, null, null, scoreDirectorFactory)
                .build();
    }

}
