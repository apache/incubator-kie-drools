package org.optaplanner.examples.nqueens.integration.constructionheuristic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.integration.util.QueenCoordinates;
import org.optaplanner.examples.nqueens.integration.util.QueenCoordinatesStepListener;
import org.optaplanner.examples.nqueens.persistence.NQueensGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class ConstructionHeuristicTest {

    private final ConstructionHeuristicType constructionHeuristicType;
    private final EntitySorterManner entitySorterManner;
    private final ValueSorterManner valueSorterManner;
    private final List<QueenCoordinates> expectedCoordinates;

    public ConstructionHeuristicTest(ConstructionHeuristicType constructionHeuristicType,
                                     EntitySorterManner entitySorterManner, ValueSorterManner valueSorterManner,
                                     List<QueenCoordinates> expectedCoordinates) {
        this.constructionHeuristicType = constructionHeuristicType;
        this.entitySorterManner = entitySorterManner;
        this.valueSorterManner = valueSorterManner;
        this.expectedCoordinates = expectedCoordinates;
    }

    @Test
    public void testConstructionHeuristics() {
        SolverConfig config = SolverFactory.createFromXmlResource(
                "org/optaplanner/examples/nqueens/solver/nqueensSolverConfig.xml").getSolverConfig();
        List<PhaseConfig> phaseConfigs = config.getPhaseConfigList();

        if(phaseConfigs.get(1) instanceof LocalSearchPhaseConfig) {
            phaseConfigs.remove(1);
        } else {
            throw new IllegalStateException("Config file had to be changed! Check org/optaplanner/examples/nqueens/solver/nqueensSolverConfig.xml");
        }

        if(phaseConfigs.get(0) instanceof ConstructionHeuristicPhaseConfig) {
            ConstructionHeuristicPhaseConfig chConfig = (ConstructionHeuristicPhaseConfig) phaseConfigs.get(0);
            chConfig.setValueSorterManner(valueSorterManner);
            chConfig.setEntitySorterManner(entitySorterManner);
            chConfig.setConstructionHeuristicType(constructionHeuristicType);
        } else {
            throw new IllegalStateException("Config file had to be changed! Check org/optaplanner/examples/nqueens/solver/nqueensSolverConfig.xml");
        }

        NQueensGenerator generator = new NQueensGenerator();
        NQueens solution = generator.createNQueens(8);

        QueenCoordinatesStepListener listener = new QueenCoordinatesStepListener();

        DefaultSolver solver = (DefaultSolver) config.buildSolver();
        solver.addPhaseLifecycleListener(listener);
        solver.solve(solution);

        NQueens result = (NQueens) solver.getBestSolution();

        assertNotNull(result);
        assertCoordinates(expectedCoordinates, listener.getCoordinates());
    }

    @Parameterized.Parameters(name = "ConstructionHeuristicType: {0}, EntitySorterManner: {1}, ValueSorterManner: {2}")
    public static Collection<Object[]> parameters() {
        Collection params = new ArrayList();

        params.add(new Object[]{ConstructionHeuristicType.FIRST_FIT, null, null, Arrays.asList(
                new QueenCoordinates(0, 0), new QueenCoordinates(1, 2), new QueenCoordinates(2, 4),
                new QueenCoordinates(3, 1), new QueenCoordinates(4, 3), new QueenCoordinates(5, 0),
                new QueenCoordinates(6, 2), new QueenCoordinates(7, 4))
        });
        params.add(new Object[]{ConstructionHeuristicType.FIRST_FIT_DECREASING, null, null, Arrays.asList(
                new QueenCoordinates(4, 0), new QueenCoordinates(3, 2), new QueenCoordinates(5, 3),
                new QueenCoordinates(2, 4), new QueenCoordinates(6, 1), new QueenCoordinates(1, 1),
                new QueenCoordinates(7, 4), new QueenCoordinates(0, 3))
        });
        params.add(new Object[]{ConstructionHeuristicType.WEAKEST_FIT, null, null, Arrays.asList(
                new QueenCoordinates(0, 3), new QueenCoordinates(1, 5), new QueenCoordinates(2, 2),
                new QueenCoordinates(3, 4), new QueenCoordinates(4, 1), new QueenCoordinates(5, 7),
                new QueenCoordinates(6, 4), new QueenCoordinates(7, 6))
        });
        params.add(new Object[]{ConstructionHeuristicType.WEAKEST_FIT_DECREASING, null, null, Arrays.asList(
                new QueenCoordinates(4, 3), new QueenCoordinates(3, 5), new QueenCoordinates(5, 1),
                new QueenCoordinates(2, 2), new QueenCoordinates(6, 4), new QueenCoordinates(1, 4),
                new QueenCoordinates(7, 2), new QueenCoordinates(0, 1))
        });
        params.add(new Object[]{ConstructionHeuristicType.STRONGEST_FIT, null, null, Arrays.asList(
                new QueenCoordinates(0, 7), new QueenCoordinates(1, 0), new QueenCoordinates(2, 6),
                new QueenCoordinates(3, 1), new QueenCoordinates(4, 5), new QueenCoordinates(5, 7),
                new QueenCoordinates(6, 0), new QueenCoordinates(7, 4))
        });
        params.add(new Object[]{ConstructionHeuristicType.STRONGEST_FIT_DECREASING, null, null, Arrays.asList(
                new QueenCoordinates(4, 7), new QueenCoordinates(3, 0), new QueenCoordinates(5, 1),
                new QueenCoordinates(2, 6), new QueenCoordinates(6, 4), new QueenCoordinates(1, 3),
                new QueenCoordinates(7, 2), new QueenCoordinates(0, 5))
        });
        params.add(new Object[]{ConstructionHeuristicType.CHEAPEST_INSERTION, null, null, Arrays.asList(
                new QueenCoordinates(4, 3), new QueenCoordinates(3, 5), new QueenCoordinates(5, 1),
                new QueenCoordinates(2, 2), new QueenCoordinates(6, 4), new QueenCoordinates(1, 4),
                new QueenCoordinates(7, 2), new QueenCoordinates(0, 1))
        });
        params.add(new Object[]{ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE, null, null, Arrays.asList(
                new QueenCoordinates(4, 3), new QueenCoordinates(3, 5), new QueenCoordinates(5, 1),
                new QueenCoordinates(2, 2), new QueenCoordinates(6, 4), new QueenCoordinates(1, 4),
                new QueenCoordinates(7, 2), new QueenCoordinates(0, 1))
        });
        params.add(new Object[]{ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE, EntitySorterManner.NONE,
                ValueSorterManner.NONE, Arrays.asList(
                new QueenCoordinates(0, 0), new QueenCoordinates(1, 2), new QueenCoordinates(2, 4),
                new QueenCoordinates(3, 1), new QueenCoordinates(4, 3), new QueenCoordinates(5, 0),
                new QueenCoordinates(6, 2), new QueenCoordinates(7, 4))
        });
        params.add(new Object[]{ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE,
                EntitySorterManner.DECREASING_DIFFICULTY, ValueSorterManner.NONE, Arrays.asList(
                new QueenCoordinates(4, 0), new QueenCoordinates(3, 2), new QueenCoordinates(5, 3),
                new QueenCoordinates(2, 4), new QueenCoordinates(6, 1), new QueenCoordinates(1, 1),
                new QueenCoordinates(7, 4), new QueenCoordinates(0, 3))
        });
        params.add(new Object[]{ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE,
                EntitySorterManner.DECREASING_DIFFICULTY_IF_AVAILABLE, ValueSorterManner.NONE, Arrays.asList(
                new QueenCoordinates(4, 0), new QueenCoordinates(3, 2), new QueenCoordinates(5, 3),
                new QueenCoordinates(2, 4), new QueenCoordinates(6, 1), new QueenCoordinates(1, 1),
                new QueenCoordinates(7, 4), new QueenCoordinates(0, 3))
        });
        params.add(new Object[]{ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE, EntitySorterManner.NONE,
                ValueSorterManner.INCREASING_STRENGTH, Arrays.asList(
                new QueenCoordinates(0, 3), new QueenCoordinates(1, 5), new QueenCoordinates(2, 2),
                new QueenCoordinates(3, 4), new QueenCoordinates(4, 1), new QueenCoordinates(5, 7),
                new QueenCoordinates(6, 4), new QueenCoordinates(7, 6))
        });
        params.add(new Object[]{ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE, EntitySorterManner.NONE,
                ValueSorterManner.DECREASING_STRENGTH, Arrays.asList(
                new QueenCoordinates(0, 7), new QueenCoordinates(1, 0), new QueenCoordinates(2, 6),
                new QueenCoordinates(3, 1), new QueenCoordinates(4, 5), new QueenCoordinates(5, 7),
                new QueenCoordinates(6, 0), new QueenCoordinates(7, 4))
        });
        params.add(new Object[]{ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE,
                EntitySorterManner.DECREASING_DIFFICULTY, ValueSorterManner.DECREASING_STRENGTH_IF_AVAILABLE,
                Arrays.asList(
                new QueenCoordinates(4, 7), new QueenCoordinates(3, 0), new QueenCoordinates(5, 1),
                new QueenCoordinates(2, 6), new QueenCoordinates(6, 4), new QueenCoordinates(1, 3),
                new QueenCoordinates(7, 2), new QueenCoordinates(0, 5))
        });
        params.add(new Object[]{ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE,
                EntitySorterManner.DECREASING_DIFFICULTY, ValueSorterManner.INCREASING_STRENGTH_IF_AVAILABLE,
                Arrays.asList(
                new QueenCoordinates(4, 3), new QueenCoordinates(3, 5), new QueenCoordinates(5, 1),
                new QueenCoordinates(2, 2), new QueenCoordinates(6, 4), new QueenCoordinates(1, 4),
                new QueenCoordinates(7, 2), new QueenCoordinates(0, 1))
        });
        return params;
    }

    private void assertCoordinates(List<QueenCoordinates> expected, List<QueenCoordinates> recorded) {
        assertEquals(expected.size(), recorded.size());

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getColumnIndex(), recorded.get(i).getColumnIndex());
            assertEquals(expected.get(i).getRowIndex(), recorded.get(i).getRowIndex());
        }
    }

}
