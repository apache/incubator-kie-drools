package org.optaplanner.core.config.heuristic.selector;

import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.mockito.Mockito.*;

public abstract class AbstractSelectorConfigTest {

    public HeuristicConfigPolicy buildHeuristicConfigPolicy() {
        ScoreDirectorFactory scoreDirectorFactory = mock(ScoreDirectorFactory.class);
        when(scoreDirectorFactory.getSolutionDescriptor()).thenReturn(TestdataSolution.buildSolutionDescriptor());
        when(scoreDirectorFactory.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        return new HeuristicConfigPolicy(EnvironmentMode.REPRODUCIBLE, scoreDirectorFactory);
    }

}
