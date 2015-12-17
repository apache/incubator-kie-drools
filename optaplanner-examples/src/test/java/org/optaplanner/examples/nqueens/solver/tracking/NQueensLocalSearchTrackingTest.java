package org.optaplanner.examples.nqueens.solver.tracking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorType;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.examples.nqueens.app.NQueensApp;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.persistence.NQueensGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class NQueensLocalSearchTrackingTest extends NQueensAbstractTrackingTest {

    private static final int N = 6;

    private final AcceptorConfig acceptorConfig;
    private final LocalSearchForagerConfig localSearchForagerConfig;
    private final List<NQueensStepTracking> expectedCoordinates;

    public NQueensLocalSearchTrackingTest(AcceptorConfig acceptorConfig,
            LocalSearchForagerConfig localSearchForagerConfig,
            List<NQueensStepTracking> expectedCoordinates) {
        this.expectedCoordinates = expectedCoordinates;
        this.localSearchForagerConfig = localSearchForagerConfig;
        this.acceptorConfig = acceptorConfig;
    }

    @Test
    public void trackLocalSearch() {
        SolverFactory<NQueens> solverFactory = SolverFactory.createFromXmlResource(NQueensApp.SOLVER_CONFIG);
        SolverConfig solverConfig = solverFactory.getSolverConfig();

        NQueensGenerator generator = new NQueensGenerator();
        NQueens planningProblem = NQueensSolutionInitializer.initialize(generator.createNQueens(N));

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setAcceptorConfig(acceptorConfig);
        localSearchPhaseConfig.setForagerConfig(localSearchForagerConfig);
        localSearchPhaseConfig.getForagerConfig().setBreakTieRandomly(false);
        localSearchPhaseConfig.setMoveSelectorConfig(new ChangeMoveSelectorConfig());
        localSearchPhaseConfig.getMoveSelectorConfig().setSelectionOrder(SelectionOrder.ORIGINAL);
        localSearchPhaseConfig.setTerminationConfig(new TerminationConfig());
        localSearchPhaseConfig.getTerminationConfig().setStepCountLimit(20);
        solverConfig.getPhaseConfigList().set(1, localSearchPhaseConfig);

        NQueensStepTracker listener = new NQueensStepTracker();
        DefaultSolver<NQueens> solver = (DefaultSolver<NQueens>) solverFactory.buildSolver();
        solver.addPhaseLifecycleListener(listener);
        NQueens bestSolution = solver.solve(planningProblem);

        assertNotNull(bestSolution);
        assertTrackingList(expectedCoordinates, listener.getTrackingList());
    }

    @Parameterized.Parameters(name = "AcceptorType: {0}")
    public static Collection<Object[]> parameters() {
        Collection<Object[]> params = new ArrayList<Object[]>();

        AcceptorConfig acceptorConfig = new AcceptorConfig();
        LocalSearchForagerConfig localSearchForagerConfig = new LocalSearchForagerConfig();
        localSearchForagerConfig.setAcceptedCountLimit(N * N);
        acceptorConfig.setAcceptorTypeList(Arrays.asList(AcceptorType.HILL_CLIMBING));
        params.add(new Object[]{acceptorConfig, localSearchForagerConfig, Arrays.asList(
                new NQueensStepTracking(1, 5), new NQueensStepTracking(0, 1),
                new NQueensStepTracking(4, 3), new NQueensStepTracking(2, 2),
                new NQueensStepTracking(3, 5), new NQueensStepTracking(1, 4),
                new NQueensStepTracking(1, 5), new NQueensStepTracking(1, 4),
                new NQueensStepTracking(1, 5)
        )});
        acceptorConfig = new AcceptorConfig();
        acceptorConfig.setAcceptorTypeList(Arrays.asList(AcceptorType.ENTITY_TABU));
        acceptorConfig.setEntityTabuSize(N - 1);
        localSearchForagerConfig = new LocalSearchForagerConfig();
        localSearchForagerConfig.setAcceptedCountLimit(N * N);
        params.add(new Object[]{acceptorConfig, localSearchForagerConfig, Arrays.asList(
                new NQueensStepTracking(1, 5), new NQueensStepTracking(0, 1),
                new NQueensStepTracking(4, 3), new NQueensStepTracking(2, 2),
                new NQueensStepTracking(3, 5), new NQueensStepTracking(5, 4),
                new NQueensStepTracking(1, 4), new NQueensStepTracking(0, 0),
                new NQueensStepTracking(4, 1)
        )});
        acceptorConfig = new AcceptorConfig();
        acceptorConfig.setAcceptorTypeList(Arrays.asList(AcceptorType.LATE_ACCEPTANCE));
        acceptorConfig.setLateAcceptanceSize(1);
        localSearchForagerConfig = new LocalSearchForagerConfig();
        localSearchForagerConfig.setAcceptedCountLimit(1);
        params.add(new Object[]{acceptorConfig, localSearchForagerConfig, Arrays.asList(
                new NQueensStepTracking(0, 1), new NQueensStepTracking(0, 2), // (0, 0) is rejected due to high score
                new NQueensStepTracking(0, 1), new NQueensStepTracking(0, 2),
                new NQueensStepTracking(0, 1), new NQueensStepTracking(0, 2)
        )});
        return params;
    }

}
