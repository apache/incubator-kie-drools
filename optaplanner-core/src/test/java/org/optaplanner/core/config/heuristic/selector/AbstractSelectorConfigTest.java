package org.optaplanner.core.config.heuristic.selector;

import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public abstract class AbstractSelectorConfigTest {

    public HeuristicConfigPolicy buildHeuristicConfigPolicy() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        ScoreDefinition scoreDefinition = new SimpleScoreDefinition();
        return new HeuristicConfigPolicy(EnvironmentMode.REPRODUCIBLE, solutionDescriptor, scoreDefinition);
    }

}
