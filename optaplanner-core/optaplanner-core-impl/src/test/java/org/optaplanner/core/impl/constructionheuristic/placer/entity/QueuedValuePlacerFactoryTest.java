package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.constructionheuristic.placer.entity.PlacementAssertions.assertValuePlacement;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedValuePlacerConfig;
import org.optaplanner.core.impl.constructionheuristic.placer.Placement;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedValuePlacer;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedValuePlacerFactory;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

class QueuedValuePlacerFactoryTest {

    @Test
    void buildEntityPlacer_withoutConfiguredMoveSelector() {
        QueuedValuePlacerConfig config = new QueuedValuePlacerConfig();
        config.setEntityClass(TestdataEntity.class);

        QueuedValuePlacer<TestdataSolution> placer =
                new QueuedValuePlacerFactory<TestdataSolution>(config).buildEntityPlacer(buildHeuristicConfigPolicy());

        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        placer.solvingStarted(solverScope);
        AbstractPhaseScope<TestdataSolution> phaseScope = mock(AbstractPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector = mock(InnerScoreDirector.class);
        when(phaseScope.getScoreDirector()).thenReturn((InnerScoreDirector) scoreDirector);
        when(scoreDirector.getWorkingSolution()).thenReturn(generateSolution());
        placer.phaseStarted(phaseScope);
        Iterator<Placement<TestdataSolution>> placementIterator = placer.iterator();
        assertThat(placementIterator.hasNext()).isTrue();

        AbstractStepScope<TestdataSolution> stepScope = mock(AbstractStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        when(stepScope.getScoreDirector()).thenReturn((InnerScoreDirector) scoreDirector);
        placer.stepStarted(stepScope);
        Placement<TestdataSolution> placement = placementIterator.next();

        assertValuePlacement(placement, "v1", "e1", "e2");
    }

    private TestdataSolution generateSolution() {
        TestdataEntity entity1 = new TestdataEntity("e1");
        TestdataEntity entity2 = new TestdataEntity("e2");
        TestdataValue value1 = new TestdataValue("v1");
        TestdataValue value2 = new TestdataValue("v2");
        TestdataSolution solution = new TestdataSolution();
        solution.setEntityList(Arrays.asList(entity1, entity2));
        solution.setValueList(Arrays.asList(value1, value2));
        return solution;
    }
}
