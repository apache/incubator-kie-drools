/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedValuePlacerConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.constructionheuristic.placer.Placement;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedValuePlacer;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedValuePlacerFactory;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

class QueuedValuePlacerFactoryTest extends AbstractEntityPlacerTest {

    @Test
    void buildEntityPlacer_withoutConfiguredMoveSelector() {
        QueuedValuePlacerConfig config = new QueuedValuePlacerConfig();
        config.setEntityClass(TestdataEntity.class);

        QueuedValuePlacer placer = new QueuedValuePlacerFactory(config).buildEntityPlacer(buildHeuristicConfigPolicy());

        SolverScope solverScope = mock(SolverScope.class);
        placer.solvingStarted(solverScope);
        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        when(phaseScope.getScoreDirector()).thenReturn(scoreDirector);
        when(scoreDirector.getWorkingSolution()).thenReturn(generateSolution());
        placer.phaseStarted(phaseScope);
        Iterator<Placement> placementIterator = placer.iterator();
        assertThat(placementIterator.hasNext()).isTrue();

        AbstractStepScope stepScope = mock(AbstractStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        when(stepScope.getScoreDirector()).thenReturn(scoreDirector);
        placer.stepStarted(stepScope);
        Placement placement = placementIterator.next();

        assertValuePlacement(placement, "v1", "e1", "e2");
    }

    public HeuristicConfigPolicy buildHeuristicConfigPolicy() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        InnerScoreDirectorFactory scoreDirectorFactory = mock(InnerScoreDirectorFactory.class);
        when(scoreDirectorFactory.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        when(scoreDirectorFactory.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        return new HeuristicConfigPolicy(EnvironmentMode.REPRODUCIBLE, null, null, null, scoreDirectorFactory);
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
