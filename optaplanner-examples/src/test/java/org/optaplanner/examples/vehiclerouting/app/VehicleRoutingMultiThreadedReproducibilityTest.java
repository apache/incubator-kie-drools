package org.optaplanner.examples.vehiclerouting.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.common.TurtleTest;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingImporter;

/**
 * The idea is to verify one of the basic requirements of Multithreaded Solving - the reproducibility of results. After
 * a constant number of steps, every iteration must finish with the same score.
 */
class VehicleRoutingMultiThreadedReproducibilityTest {

    private static final int REPETITION_COUNT = 10;

    private static final int STEP_LIMIT = 5000;

    private static final String MOVE_THREAD_COUNT = "4";

    private static final String DATA_SET = "import/belgium/basic/air/belgium-n50-k10.vrp";

    private final VehicleRoutingApp vehicleRoutingApp = new VehicleRoutingApp();

    private VehicleRoutingSolution[] vehicleRoutingSolutions = new VehicleRoutingSolution[REPETITION_COUNT];

    private SolverFactory<VehicleRoutingSolution> solverFactory;

    @BeforeEach
    void createUninitializedSolutions() {
        final VehicleRoutingImporter importer = new VehicleRoutingImporter();
        for (int i = 0; i < REPETITION_COUNT; i++) {
            File dataSetFile = new File(CommonApp.determineDataDir(vehicleRoutingApp.getDataDirName()), DATA_SET);
            VehicleRoutingSolution solution = importer.readSolution(dataSetFile);
            vehicleRoutingSolutions[i] = solution;
        }

        SolverConfig solverConfig =
                SolverConfig.createFromXmlResource(vehicleRoutingApp.getSolverConfigResource());
        solverConfig.withEnvironmentMode(EnvironmentMode.REPRODUCIBLE)
                .withMoveThreadCount(MOVE_THREAD_COUNT);
        solverConfig.getPhaseConfigList().forEach(phaseConfig -> {
            if (LocalSearchPhaseConfig.class.isAssignableFrom(phaseConfig.getClass())) {
                phaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(STEP_LIMIT));
            }
        });
        solverFactory = SolverFactory.create(solverConfig);
    }

    @TurtleTest
    void multiThreadedSolvingIsReproducible() {
        IntStream.range(0, REPETITION_COUNT).forEach(this::solveAndCompareWithPrevious);
    }

    private void solveAndCompareWithPrevious(final int iteration) {
        Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();
        VehicleRoutingSolution bestSolution = solver.solve(vehicleRoutingSolutions[iteration]);
        vehicleRoutingSolutions[iteration] = bestSolution;

        if (iteration > 0) {
            VehicleRoutingSolution previousBestSolution = vehicleRoutingSolutions[iteration - 1];
            assertThat(previousBestSolution.getScore()).isEqualTo(bestSolution.getScore());
        }
    }

}
