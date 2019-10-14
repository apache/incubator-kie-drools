/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.exhaustivesearch;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.exhaustivesearch.decider.ExhaustiveSearchDecider;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchLayer;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchPhaseScope;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchStepScope;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.immovable.TestdataImmovableEntity;
import org.optaplanner.core.impl.testdata.domain.immovable.TestdataImmovableSolution;
import org.optaplanner.core.impl.testdata.domain.reinitialize.TestdataReinitializeEntity;
import org.optaplanner.core.impl.testdata.domain.reinitialize.TestdataReinitializeSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class DefaultExhaustiveSearchPhaseTest {

    @Test
    public void restoreWorkingSolution() {
        ExhaustiveSearchPhaseScope<TestdataSolution> phaseScope = mock(ExhaustiveSearchPhaseScope.class);
        ExhaustiveSearchStepScope<TestdataSolution> lastCompletedStepScope = mock(ExhaustiveSearchStepScope.class);
        when(phaseScope.getLastCompletedStepScope()).thenReturn(lastCompletedStepScope);
        ExhaustiveSearchStepScope<TestdataSolution> stepScope = mock(ExhaustiveSearchStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        TestdataSolution workingSolution = new TestdataSolution();
        when(phaseScope.getWorkingSolution()).thenReturn(workingSolution);
        InnerScoreDirector<TestdataSolution> scoreDirector = mock(InnerScoreDirector.class);
        when(phaseScope.getScoreDirector()).thenReturn(scoreDirector);

        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        when(phaseScope.getSolutionDescriptor()).thenReturn(solutionDescriptor);

        ExhaustiveSearchLayer layer0 = new ExhaustiveSearchLayer(0, mock(Object.class));
        ExhaustiveSearchLayer layer1 = new ExhaustiveSearchLayer(1, mock(Object.class));
        ExhaustiveSearchLayer layer2 = new ExhaustiveSearchLayer(2, mock(Object.class));
        ExhaustiveSearchLayer layer3 = new ExhaustiveSearchLayer(3, mock(Object.class));
        ExhaustiveSearchLayer layer4 = new ExhaustiveSearchLayer(4, mock(Object.class));
        ExhaustiveSearchNode node0 = new ExhaustiveSearchNode(layer0, null);
        node0.setMove(mock(Move.class));
        node0.setUndoMove(mock(Move.class));
        ExhaustiveSearchNode node1 = new ExhaustiveSearchNode(layer1, node0);
        node1.setMove(mock(Move.class));
        node1.setUndoMove(mock(Move.class));
        ExhaustiveSearchNode node2A = new ExhaustiveSearchNode(layer2, node1);
        node2A.setMove(mock(Move.class));
        node2A.setUndoMove(mock(Move.class));
        ExhaustiveSearchNode node3A = new ExhaustiveSearchNode(layer3, node2A); // oldNode
        node3A.setMove(mock(Move.class));
        node3A.setUndoMove(mock(Move.class));
        ExhaustiveSearchNode node2B = new ExhaustiveSearchNode(layer2, node1);
        node2B.setMove(mock(Move.class));
        node2B.setUndoMove(mock(Move.class));
        ExhaustiveSearchNode node3B = new ExhaustiveSearchNode(layer3, node2B);
        node3B.setMove(mock(Move.class));
        node3B.setUndoMove(mock(Move.class));
        ExhaustiveSearchNode node4B = new ExhaustiveSearchNode(layer4, node3B); // newNode
        node4B.setMove(mock(Move.class));
        node4B.setUndoMove(mock(Move.class));
        node4B.setScore(SimpleScore.ofUninitialized(-96, 7));
        when(lastCompletedStepScope.getExpandingNode()).thenReturn(node3A);
        when(stepScope.getExpandingNode()).thenReturn(node4B);

        DefaultExhaustiveSearchPhase<TestdataSolution> phase = new DefaultExhaustiveSearchPhase<>(0, "", null, null);
        phase.setEntitySelector(mock(EntitySelector.class));
        phase.setDecider(mock(ExhaustiveSearchDecider.class));
        phase.restoreWorkingSolution(stepScope);

        verify(node0.getMove(), times(0)).doMove(any(ScoreDirector.class));
        verify(node0.getUndoMove(), times(0)).doMove(any(ScoreDirector.class));
        verify(node1.getMove(), times(0)).doMove(any(ScoreDirector.class));
        verify(node1.getUndoMove(), times(0)).doMove(any(ScoreDirector.class));
        verify(node2A.getMove(), times(0)).doMove(any(ScoreDirector.class));
        verify(node2A.getUndoMove(), times(1)).doMove(scoreDirector);
        verify(node3A.getMove(), times(0)).doMove(any(ScoreDirector.class));
        verify(node3A.getUndoMove(), times(1)).doMove(scoreDirector);
        verify(node2B.getMove(), times(1)).doMove(scoreDirector);
        verify(node2B.getUndoMove(), times(0)).doMove(any(ScoreDirector.class));
        verify(node3B.getMove(), times(1)).doMove(scoreDirector);
        verify(node3B.getUndoMove(), times(0)).doMove(any(ScoreDirector.class));
        verify(node4B.getMove(), times(1)).doMove(scoreDirector);
        verify(node4B.getUndoMove(), times(0)).doMove(any(ScoreDirector.class));
        // TODO FIXME
        // verify(workingSolution).setScore(newScore);
    }

    @Test
    public void solveWithInitializedEntities() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        solverConfig.setPhaseConfigList(Collections.singletonList(
                new ExhaustiveSearchPhaseConfig()));

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataEntity("e1", null),
                new TestdataEntity("e2", v2),
                new TestdataEntity("e3", v1)));

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertNotNull(solution);
        TestdataEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertNotNull(solvedE1.getValue());
        TestdataEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertEquals(v2, solvedE2.getValue());
        TestdataEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertEquals(v1, solvedE3.getValue());
        assertEquals(0, solution.getScore().getInitScore());
    }

    @Test
    public void solveWithImmovableEntities() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataImmovableSolution.class, TestdataImmovableEntity.class);
        solverConfig.setPhaseConfigList(Collections.singletonList(
                new ExhaustiveSearchPhaseConfig()));

        TestdataImmovableSolution solution = new TestdataImmovableSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataImmovableEntity("e1", null, false, false),
                new TestdataImmovableEntity("e2", v2, true, false),
                new TestdataImmovableEntity("e3", null, false, true)));

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertNotNull(solution);
        TestdataImmovableEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertNotNull(solvedE1.getValue());
        TestdataImmovableEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertEquals(v2, solvedE2.getValue());
        TestdataImmovableEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertEquals(null, solvedE3.getValue());
        assertEquals(-1, solution.getScore().getInitScore());
    }

    @Test
    public void solveWithEmptyEntityList() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        solverConfig.setPhaseConfigList(Collections.singletonList(
                new ExhaustiveSearchPhaseConfig()));

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Collections.emptyList());

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertNotNull(solution);
        assertEquals(0, solution.getEntityList().size());
    }

    @Test
    public void solveWithReinitializeVariable() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataReinitializeSolution.class, TestdataReinitializeEntity.class);
        solverConfig.setPhaseConfigList(Collections.singletonList(
                new ExhaustiveSearchPhaseConfig()));

        TestdataReinitializeSolution solution = new TestdataReinitializeSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataReinitializeEntity("e1", null, false),
                new TestdataReinitializeEntity("e2", v2, false),
                new TestdataReinitializeEntity("e3", v2, true),
                new TestdataReinitializeEntity("e4", null, true)));

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertNotNull(solution);
        TestdataReinitializeEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertNotNull(solvedE1.getValue());
        TestdataReinitializeEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertNotNull(solvedE2.getValue());
        TestdataReinitializeEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertEquals(v2, solvedE3.getValue());
        TestdataReinitializeEntity solvedE4 = solution.getEntityList().get(3);
        assertCode("e4", solvedE4);
        assertEquals(null, solvedE4.getValue());
        assertEquals(-1, solution.getScore().getInitScore());
    }

}
