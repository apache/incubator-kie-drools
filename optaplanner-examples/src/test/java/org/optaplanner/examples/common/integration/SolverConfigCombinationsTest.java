package org.optaplanner.examples.common.integration;

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorType;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.cheaptime.app.CheapTimeApp;
import org.optaplanner.examples.cheaptime.persistence.CheapTimeDao;
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalancingDao;

import java.io.File;
import java.util.Arrays;

public class SolverConfigCombinationsTest {

    @Test
    public void noPhaseConfiguration() {
        SolverConfig config = SolverFactory.createFromXmlResource("org/optaplanner/examples/cloudbalancing/solver/cloudBalancingSolverConfig.xml").getSolverConfig();
        config.getTerminationConfig().setSecondsSpentLimit(null);
        config.getTerminationConfig().setSecondsSpentLimit(2L);
        Solver solver = config.buildSolver();
        Solution planningProblem = new CloudBalancingDao().readSolution(new File("data/cloudbalancing/unsolved/2computers-6processes.xml"));
        solver.solve(planningProblem);
    }

    @Test
    public void simpleChangeMoveSelectorWithMultipleVariables() {
        SolverConfig config = SolverFactory.createFromXmlResource(CheapTimeApp.SOLVER_CONFIG).getSolverConfig();
        PhaseConfig phaseConfig = config.getPhaseConfigList().get(1);
        ((LocalSearchPhaseConfig) phaseConfig).setMoveSelectorConfig(new ChangeMoveSelectorConfig());
        phaseConfig.setTerminationConfig(new TerminationConfig());
        phaseConfig.getTerminationConfig().setStepCountLimit(2);
        Solver solver = config.buildSolver();
        Solution planningProblem = new CheapTimeDao().readSolution(new File("data/cheaptime/unsolved/sample01.xml"));
        solver.solve(planningProblem);
    }

    @Test
    @Ignore("don't know how to make default move selector from code.")
    public void defaultMoveSelectorWithMultipleVariables() {
        SolverConfig config = SolverFactory.createFromXmlResource(CheapTimeApp.SOLVER_CONFIG).getSolverConfig();
        PhaseConfig phaseConfig = config.getPhaseConfigList().get(1);
        ((LocalSearchPhaseConfig) phaseConfig).setMoveSelectorConfig(null);
        phaseConfig.setTerminationConfig(new TerminationConfig());
        phaseConfig.getTerminationConfig().setStepCountLimit(2);
        Solver solver = config.buildSolver();
        Solution planningProblem = new CheapTimeDao().readSolution(new File("data/cheaptime/unsolved/demo01.xml"));
        solver.solve(planningProblem);
    }

}
