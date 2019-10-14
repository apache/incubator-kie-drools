package org.optaplanner.examples.vehiclerouting.app;

import java.io.File;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.common.app.AbstractTurtleTest;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingImporter;

import static org.junit.Assert.*;

/**
 * The idea is to verify one of the basic requirements of Multithreaded Solving - the reproducibility of results. After
 * a constant number of steps, every iteration must finish with the same score.
 */
public class VehicleRoutingMultiThreadedReproducibilityTest extends AbstractTurtleTest {

    private static final int REPETITION_COUNT = 10;

    private static final int STEP_LIMIT = 5000;

    private static final String MOVE_THREAD_COUNT = "4";

    private static final String DATA_SET = "import/belgium/basic/air/belgium-n50-k10.vrp";

    private final VehicleRoutingApp vehicleRoutingApp = new VehicleRoutingApp();

    private VehicleRoutingSolution[] vehicleRoutingSolutions = new VehicleRoutingSolution[REPETITION_COUNT];

    private SolverFactory<VehicleRoutingSolution> solverFactory;

    @Before
    public void createUninitializedSolutions() {
        checkRunTurtleTests();
        final VehicleRoutingImporter importer = new VehicleRoutingImporter();
        for (int i = 0; i < REPETITION_COUNT; i++) {
            File dataSetFile = new File(CommonApp.determineDataDir(vehicleRoutingApp.getDataDirName()), DATA_SET);
            VehicleRoutingSolution solution = importer.readSolution(dataSetFile);
            vehicleRoutingSolutions[i] = solution;
        }

        SolverConfig solverConfig = SolverConfig.createFromXmlResource(vehicleRoutingApp.getSolverConfigResource());
        solverConfig.withEnvironmentMode(EnvironmentMode.REPRODUCIBLE)
                .withMoveThreadCount(MOVE_THREAD_COUNT);
        solverConfig.getPhaseConfigList().forEach(phaseConfig -> {
            if (LocalSearchPhaseConfig.class.isAssignableFrom(phaseConfig.getClass())) {
                phaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(STEP_LIMIT));
            }
        });
        solverFactory = SolverFactory.create(solverConfig);
    }

    @Test
    public void multiThreadedSolvingIsReproducible() {
        checkRunTurtleTests();
        IntStream.range(0, REPETITION_COUNT).forEach(iteration -> solveAndCompareWithPrevious(iteration));
    }

    private void solveAndCompareWithPrevious(final int iteration) {
        Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();
        VehicleRoutingSolution bestSolution = solver.solve(vehicleRoutingSolutions[iteration]);
        vehicleRoutingSolutions[iteration] = bestSolution;

        if (iteration > 0) {
            VehicleRoutingSolution previousBestSolution = vehicleRoutingSolutions[iteration - 1];
            assertEquals(bestSolution.getScore(), previousBestSolution.getScore());
        }
    }

}
