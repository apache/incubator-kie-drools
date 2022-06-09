package org.optaplanner.examples.nqueens.optional.solver.tracking;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.examples.nqueens.app.NQueensApp;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.persistence.NQueensGenerator;

class NQueensConstructionHeuristicTrackingTest extends NQueensAbstractTrackingTest {

    @ParameterizedTest(name = "ConstructionHeuristicType: {0}, EntitySorterManner: {1}, ValueSorterManner: {2}")
    @MethodSource("parameters")
    void trackConstructionHeuristics(ConstructionHeuristicType constructionHeuristicType,
            EntitySorterManner entitySorterManner, ValueSorterManner valueSorterManner,
            List<NQueensStepTracking> expectedCoordinates) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(NQueensApp.SOLVER_CONFIG);

        ConstructionHeuristicPhaseConfig chConfig = new ConstructionHeuristicPhaseConfig();
        chConfig.setValueSorterManner(valueSorterManner);
        chConfig.setEntitySorterManner(entitySorterManner);
        chConfig.setConstructionHeuristicType(constructionHeuristicType);
        solverConfig.setPhaseConfigList(Collections.singletonList(chConfig));

        NQueensGenerator generator = new NQueensGenerator();
        NQueens problem = generator.createNQueens(8);

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

        params.add(new Object[] { ConstructionHeuristicType.FIRST_FIT, null, null, Arrays.asList(
                new NQueensStepTracking(0, 0), new NQueensStepTracking(1, 2), new NQueensStepTracking(2, 4),
                new NQueensStepTracking(3, 1), new NQueensStepTracking(4, 3), new NQueensStepTracking(5, 0),
                new NQueensStepTracking(6, 2), new NQueensStepTracking(7, 4))
        });
        params.add(new Object[] { ConstructionHeuristicType.FIRST_FIT_DECREASING, null, null, Arrays.asList(
                new NQueensStepTracking(4, 0), new NQueensStepTracking(3, 2), new NQueensStepTracking(5, 3),
                new NQueensStepTracking(2, 4), new NQueensStepTracking(6, 1), new NQueensStepTracking(1, 1),
                new NQueensStepTracking(7, 4), new NQueensStepTracking(0, 3))
        });
        params.add(new Object[] { ConstructionHeuristicType.WEAKEST_FIT, null, null, Arrays.asList(
                new NQueensStepTracking(0, 3), new NQueensStepTracking(1, 5), new NQueensStepTracking(2, 2),
                new NQueensStepTracking(3, 4), new NQueensStepTracking(4, 1), new NQueensStepTracking(5, 7),
                new NQueensStepTracking(6, 4), new NQueensStepTracking(7, 6))
        });
        params.add(new Object[] { ConstructionHeuristicType.WEAKEST_FIT_DECREASING, null, null, Arrays.asList(
                new NQueensStepTracking(4, 3), new NQueensStepTracking(3, 5), new NQueensStepTracking(5, 1),
                new NQueensStepTracking(2, 2), new NQueensStepTracking(6, 4), new NQueensStepTracking(1, 4),
                new NQueensStepTracking(7, 2), new NQueensStepTracking(0, 1))
        });
        params.add(new Object[] { ConstructionHeuristicType.STRONGEST_FIT, null, null, Arrays.asList(
                new NQueensStepTracking(0, 7), new NQueensStepTracking(1, 0), new NQueensStepTracking(2, 6),
                new NQueensStepTracking(3, 1), new NQueensStepTracking(4, 5), new NQueensStepTracking(5, 7),
                new NQueensStepTracking(6, 0), new NQueensStepTracking(7, 4))
        });
        params.add(new Object[] { ConstructionHeuristicType.STRONGEST_FIT_DECREASING, null, null, Arrays.asList(
                new NQueensStepTracking(4, 7), new NQueensStepTracking(3, 0), new NQueensStepTracking(5, 1),
                new NQueensStepTracking(2, 6), new NQueensStepTracking(6, 4), new NQueensStepTracking(1, 3),
                new NQueensStepTracking(7, 2), new NQueensStepTracking(0, 5))
        });
        params.add(new Object[] { ConstructionHeuristicType.CHEAPEST_INSERTION, null, null, Arrays.asList(
                new NQueensStepTracking(4, 3), new NQueensStepTracking(3, 5), new NQueensStepTracking(5, 1),
                new NQueensStepTracking(2, 2), new NQueensStepTracking(6, 4), new NQueensStepTracking(1, 4),
                new NQueensStepTracking(7, 2), new NQueensStepTracking(0, 1))
        });
        params.add(new Object[] { ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE, null, null, Arrays.asList(
                new NQueensStepTracking(4, 3), new NQueensStepTracking(3, 5), new NQueensStepTracking(5, 1),
                new NQueensStepTracking(2, 2), new NQueensStepTracking(6, 4), new NQueensStepTracking(1, 4),
                new NQueensStepTracking(7, 2), new NQueensStepTracking(0, 1))
        });
        params.add(new Object[] { ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE, EntitySorterManner.NONE,
                ValueSorterManner.NONE, Arrays.asList(
                        new NQueensStepTracking(0, 0), new NQueensStepTracking(1, 2), new NQueensStepTracking(2, 4),
                        new NQueensStepTracking(3, 1), new NQueensStepTracking(4, 3), new NQueensStepTracking(5, 0),
                        new NQueensStepTracking(6, 2), new NQueensStepTracking(7, 4))
        });
        params.add(new Object[] { ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE,
                EntitySorterManner.DECREASING_DIFFICULTY, ValueSorterManner.NONE, Arrays.asList(
                        new NQueensStepTracking(4, 0), new NQueensStepTracking(3, 2), new NQueensStepTracking(5, 3),
                        new NQueensStepTracking(2, 4), new NQueensStepTracking(6, 1), new NQueensStepTracking(1, 1),
                        new NQueensStepTracking(7, 4), new NQueensStepTracking(0, 3))
        });
        params.add(new Object[] { ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE,
                EntitySorterManner.DECREASING_DIFFICULTY_IF_AVAILABLE, ValueSorterManner.NONE, Arrays.asList(
                        new NQueensStepTracking(4, 0), new NQueensStepTracking(3, 2), new NQueensStepTracking(5, 3),
                        new NQueensStepTracking(2, 4), new NQueensStepTracking(6, 1), new NQueensStepTracking(1, 1),
                        new NQueensStepTracking(7, 4), new NQueensStepTracking(0, 3))
        });
        params.add(new Object[] { ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE, EntitySorterManner.NONE,
                ValueSorterManner.INCREASING_STRENGTH, Arrays.asList(
                        new NQueensStepTracking(0, 3), new NQueensStepTracking(1, 5), new NQueensStepTracking(2, 2),
                        new NQueensStepTracking(3, 4), new NQueensStepTracking(4, 1), new NQueensStepTracking(5, 7),
                        new NQueensStepTracking(6, 4), new NQueensStepTracking(7, 6))
        });
        params.add(new Object[] { ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE, EntitySorterManner.NONE,
                ValueSorterManner.DECREASING_STRENGTH, Arrays.asList(
                        new NQueensStepTracking(0, 7), new NQueensStepTracking(1, 0), new NQueensStepTracking(2, 6),
                        new NQueensStepTracking(3, 1), new NQueensStepTracking(4, 5), new NQueensStepTracking(5, 7),
                        new NQueensStepTracking(6, 0), new NQueensStepTracking(7, 4))
        });
        params.add(new Object[] { ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE,
                EntitySorterManner.DECREASING_DIFFICULTY, ValueSorterManner.DECREASING_STRENGTH_IF_AVAILABLE,
                Arrays.asList(
                        new NQueensStepTracking(4, 7), new NQueensStepTracking(3, 0), new NQueensStepTracking(5, 1),
                        new NQueensStepTracking(2, 6), new NQueensStepTracking(6, 4), new NQueensStepTracking(1, 3),
                        new NQueensStepTracking(7, 2), new NQueensStepTracking(0, 5))
        });
        params.add(new Object[] { ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE,
                EntitySorterManner.DECREASING_DIFFICULTY, ValueSorterManner.INCREASING_STRENGTH_IF_AVAILABLE,
                Arrays.asList(
                        new NQueensStepTracking(4, 3), new NQueensStepTracking(3, 5), new NQueensStepTracking(5, 1),
                        new NQueensStepTracking(2, 2), new NQueensStepTracking(6, 4), new NQueensStepTracking(1, 4),
                        new NQueensStepTracking(7, 2), new NQueensStepTracking(0, 1))
        });
        return params;
    }

}
