package org.optaplanner.examples.nqueens.optional.solver.tracking;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorType;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.examples.nqueens.app.NQueensApp;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.persistence.NQueensGenerator;

class NQueensLocalSearchTrackingTest extends NQueensAbstractTrackingTest {

    private static final int N = 6;

    @ParameterizedTest(name = "AcceptorType: {0}")
    @MethodSource("parameters")
    void trackLocalSearch(LocalSearchAcceptorConfig acceptorConfig,
            LocalSearchForagerConfig localSearchForagerConfig,
            List<NQueensStepTracking> expectedCoordinates) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(NQueensApp.SOLVER_CONFIG);

        NQueens problem = new NQueensGenerator().createNQueens(N);
        problem.getQueenList().forEach(queen -> queen.setRow(problem.getRowList().get(0)));

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setAcceptorConfig(acceptorConfig);
        localSearchPhaseConfig.setForagerConfig(localSearchForagerConfig);
        localSearchPhaseConfig.getForagerConfig().setBreakTieRandomly(false);
        localSearchPhaseConfig.setMoveSelectorConfig(new ChangeMoveSelectorConfig());
        localSearchPhaseConfig.getMoveSelectorConfig().setSelectionOrder(SelectionOrder.ORIGINAL);
        localSearchPhaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(20));
        solverConfig.getPhaseConfigList().set(1, localSearchPhaseConfig);

        NQueensStepTracker listener = new NQueensStepTracker();
        SolverFactory<NQueens> solverFactory = SolverFactory.create(solverConfig);
        DefaultSolver<NQueens> solver = (DefaultSolver<NQueens>) solverFactory.buildSolver();
        solver.addPhaseLifecycleListener(listener);
        NQueens bestSolution = solver.solve(problem);

        assertThat(bestSolution).isNotNull();
        assertTrackingList(expectedCoordinates, listener.getTrackingList());
    }

    static Collection<Object[]> parameters() {
        Collection<Object[]> params = new ArrayList<>();

        params.add(new Object[] {
                new LocalSearchAcceptorConfig()
                        .withAcceptorTypeList(Arrays.asList(AcceptorType.HILL_CLIMBING)),
                new LocalSearchForagerConfig()
                        .withAcceptedCountLimit(N * N),
                Arrays.asList(
                        new NQueensStepTracking(1, 5), new NQueensStepTracking(0, 1),
                        new NQueensStepTracking(4, 3), new NQueensStepTracking(2, 2),
                        new NQueensStepTracking(3, 5), new NQueensStepTracking(1, 4),
                        new NQueensStepTracking(1, 5), new NQueensStepTracking(1, 4),
                        new NQueensStepTracking(1, 5)) });
        params.add(new Object[] {
                new LocalSearchAcceptorConfig()
                        .withAcceptorTypeList(Arrays.asList(AcceptorType.ENTITY_TABU))
                        .withEntityTabuSize(N - 1),
                new LocalSearchForagerConfig()
                        .withAcceptedCountLimit(N * N),
                Arrays.asList(
                        new NQueensStepTracking(1, 5), new NQueensStepTracking(0, 1),
                        new NQueensStepTracking(4, 3), new NQueensStepTracking(2, 2),
                        new NQueensStepTracking(3, 5), new NQueensStepTracking(5, 4),
                        new NQueensStepTracking(1, 4), new NQueensStepTracking(0, 0),
                        new NQueensStepTracking(4, 1)) });
        params.add(new Object[] {
                new LocalSearchAcceptorConfig()
                        .withAcceptorTypeList(Arrays.asList(AcceptorType.LATE_ACCEPTANCE))
                        .withLateAcceptanceSize(1),
                new LocalSearchForagerConfig()
                        .withAcceptedCountLimit(1),
                Arrays.asList(
                        // (0, 0) is rejected due to high score
                        new NQueensStepTracking(0, 1), new NQueensStepTracking(0, 2),
                        new NQueensStepTracking(0, 1), new NQueensStepTracking(0, 2),
                        new NQueensStepTracking(0, 1), new NQueensStepTracking(0, 2)) });
        return params;
    }
}
