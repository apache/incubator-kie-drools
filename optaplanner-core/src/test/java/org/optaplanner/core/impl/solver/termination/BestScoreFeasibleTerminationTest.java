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
package org.optaplanner.core.impl.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class BestScoreFeasibleTerminationTest {

    @Test
    public void solveTermination() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getFeasibleLevelsSize()).thenReturn(1);
        Termination termination = new BestScoreFeasibleTermination(scoreDefinition, new double[] {});
        SolverScope solverScope = mock(SolverScope.class);
        when(solverScope.getScoreDefinition()).thenReturn(new HardSoftScoreDefinition());
        when(solverScope.getStartingInitializedScore()).thenReturn(HardSoftScore.of(-100, -100));
        when(solverScope.isBestSolutionInitialized()).thenReturn(true);

        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(-100, -100));
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.0, offset(0.0));
        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(-80, -100));
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.2, offset(0.0));
        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(-60, -100));
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.4, offset(0.0));
        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(-40, -100));
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.6, offset(0.0));
        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(-20, -100));
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.8, offset(0.0));
        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(0, -100));
        assertThat(termination.isSolverTerminated(solverScope)).isTrue();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(1.0, offset(0.0));
    }

    @Test
    public void phaseTermination() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getFeasibleLevelsSize()).thenReturn(1);
        Termination termination = new BestScoreFeasibleTermination(scoreDefinition, new double[] {});
        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);
        when(phaseScope.getScoreDefinition()).thenReturn(new HardSoftScoreDefinition());
        when(phaseScope.getStartingScore()).thenReturn(HardSoftScore.of(-100, -100));
        when(phaseScope.isBestSolutionInitialized()).thenReturn(true);

        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(-100, -100));
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.0, offset(0.0));
        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(-80, -100));
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.2, offset(0.0));
        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(-60, -100));
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.4, offset(0.0));
        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(-40, -100));
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.6, offset(0.0));
        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(-20, -100));
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.8, offset(0.0));
        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(0, -100));
        assertThat(termination.isPhaseTerminated(phaseScope)).isTrue();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, offset(0.0));
    }

    @Test
    public void calculateTimeGradientBendableScoreHHSSS() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getFeasibleLevelsSize()).thenReturn(2);
        BestScoreFeasibleTermination termination = new BestScoreFeasibleTermination(scoreDefinition,
                new double[] { 0.75 });

        // Normal cases
        // Smack in the middle
        assertThat(termination.calculateFeasibilityTimeGradient(
                BendableScore.of(new int[] { -10, -100 }, new int[] { -50, -60, -70 }),
                BendableScore.of(new int[] { -4, -40 }, new int[] { -50, -60, -70 }))).isEqualTo(0.6, offset(0.0));
    }

}
