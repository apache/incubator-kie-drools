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

package org.optaplanner.core.impl.solver.termination;

import java.math.BigDecimal;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BestScoreTerminationTest {

    @Test
    public void solveTermination() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getLevelsSize()).thenReturn(1);
        Termination termination = new BestScoreTermination(scoreDefinition, SimpleScore.valueOfInitialized(-1000), new double[]{});
        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        when(solverScope.isBestSolutionInitialized()).thenReturn(true);
        when(solverScope.getStartingInitializedScore()).thenReturn(SimpleScore.valueOfInitialized(-1100));

        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-1100));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-1100));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-1040));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.6, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-1040));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.6, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-1000));
        assertEquals(true, termination.isSolverTerminated(solverScope));
        assertEquals(1.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-900));
        assertEquals(true, termination.isSolverTerminated(solverScope));
        assertEquals(1.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
    }

    @Test
    public void phaseTermination() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getLevelsSize()).thenReturn(1);
        Termination termination = new BestScoreTermination(scoreDefinition, SimpleScore.valueOfInitialized(-1000), new double[]{});
        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);
        when(phaseScope.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        when(phaseScope.isBestSolutionInitialized()).thenReturn(true);
        when(phaseScope.getStartingScore()).thenReturn(SimpleScore.valueOfInitialized(-1100));

        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-1100));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-1100));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-1040));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.6, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-1040));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.6, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-1000));
        assertEquals(true, termination.isPhaseTerminated(phaseScope));
        assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOfInitialized(-900));
        assertEquals(true, termination.isPhaseTerminated(phaseScope));
        assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
    }

    @Test
    public void calculateTimeGradientSimpleScore() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getLevelsSize()).thenReturn(1);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition,
                SimpleScore.valueOfInitialized(10), new double[]{});

        assertEquals(0.0, termination.calculateTimeGradient(
                SimpleScore.valueOfInitialized(0), SimpleScore.valueOfInitialized(10), SimpleScore.valueOfInitialized(0)), 0.0);
        assertEquals(0.6, termination.calculateTimeGradient(
                SimpleScore.valueOfInitialized(0), SimpleScore.valueOfInitialized(10), SimpleScore.valueOfInitialized(6)), 0.0);
        assertEquals(1.0, termination.calculateTimeGradient(
                SimpleScore.valueOfInitialized(0), SimpleScore.valueOfInitialized(10), SimpleScore.valueOfInitialized(10)), 0.0);
        assertEquals(1.0, termination.calculateTimeGradient(
                SimpleScore.valueOfInitialized(0), SimpleScore.valueOfInitialized(10), SimpleScore.valueOfInitialized(11)), 0.0);

        assertEquals(0.25, termination.calculateTimeGradient(
                SimpleScore.valueOfInitialized(-10), SimpleScore.valueOfInitialized(30), SimpleScore.valueOfInitialized(0)), 0.0);
        assertEquals(0.33333, termination.calculateTimeGradient(
                SimpleScore.valueOfInitialized(10), SimpleScore.valueOfInitialized(40), SimpleScore.valueOfInitialized(20)), 0.00001);
    }

    @Test
    public void calculateTimeGradientSimpleBigDecimalScore() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getLevelsSize()).thenReturn(1);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition,
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("10.00")), new double[]{});

        assertEquals(0.0, termination.calculateTimeGradient(
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("0.00")), SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("10.00")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("0.00"))), 0.0);
        assertEquals(0.6, termination.calculateTimeGradient(
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("0.00")), SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("10.00")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("6.00"))), 0.0);
        assertEquals(1.0, termination.calculateTimeGradient(
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("0.00")), SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("10.00")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("10.00"))), 0.0);
        assertEquals(1.0, termination.calculateTimeGradient(
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("0.00")), SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("10.00")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("11.00"))), 0.0);
        assertEquals(0.25, termination.calculateTimeGradient(
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-10.00")), SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("30.00")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("0.00"))), 0.0);
        assertEquals(0.33333, termination.calculateTimeGradient(
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("10.00")), SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("40.00")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("20.00"))), 0.00001);
    }

    @Test
    public void calculateTimeGradientHardSoftScore() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getLevelsSize()).thenReturn(2);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition,
                HardSoftScore.valueOfInitialized(-10, -300), new double[]{0.75});

        // Normal cases
        // Smack in the middle
        assertEquals(0.6, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-20, -400), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-14, -340)), 0.0);
        // No hard broken, total soft broken
        assertEquals(0.75, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-20, -400), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-10, -400)), 0.0);
        // Total hard broken, no soft broken
        assertEquals(0.25, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-20, -400), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-20, -300)), 0.0);
        // No hard broken, more than total soft broken
        assertEquals(0.75, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-20, -400), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-10, -900)), 0.0);
        // More than total hard broken, no soft broken
        assertEquals(0.0, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-20, -400), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-90, -300)), 0.0);

        // Perfect min/max cases
        assertEquals(1.0, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-10, -300), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-10, -300)), 0.0);
        assertEquals(0.0, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-20, -400), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-20, -400)), 0.0);
        assertEquals(1.0, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-20, -400), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-10, -300)), 0.0);

        // Hard total delta is 0
        assertEquals(0.75 + (0.6 * 0.25), termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-10, -400), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-10, -340)), 0.0);
        assertEquals(0.0, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-10, -400), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-20, -340)), 0.0);
        assertEquals(1.0, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-10, -400), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-0, -340)), 0.0);

        // Soft total delta is 0
        assertEquals((0.6 * 0.75) + 0.25, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-20, -300), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-14, -300)), 0.0);
        assertEquals(0.6 * 0.75, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-20, -300), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-14, -400)), 0.0);
        assertEquals((0.6 * 0.75) + 0.25, termination.calculateTimeGradient(
                HardSoftScore.valueOfInitialized(-20, -300), HardSoftScore.valueOfInitialized(-10, -300),
                HardSoftScore.valueOfInitialized(-14, -0)), 0.0);
    }

    @Test
    public void calculateTimeGradientHardSoftBigDecimalScore() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getLevelsSize()).thenReturn(2);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition,
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("10.00"), new BigDecimal("10.00")), new double[]{0.75});

        // hard == soft
        assertEquals(0.0, termination.calculateTimeGradient(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0.00"), new BigDecimal("0.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("10.00"), new BigDecimal("10.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0.00"), new BigDecimal("0.00"))), 0.0);
        assertEquals(0.6, termination.calculateTimeGradient(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0.00"), new BigDecimal("0.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("10.00"), new BigDecimal("10.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("6.00"), new BigDecimal("6.00"))), 0.0);
        assertEquals(1.0, termination.calculateTimeGradient(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0.00"), new BigDecimal("0.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("10.00"), new BigDecimal("10.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("10.00"), new BigDecimal("10.00"))), 0.0);
        assertEquals(1.0, termination.calculateTimeGradient(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0.00"), new BigDecimal("0.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("10.00"), new BigDecimal("10.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("11.00"), new BigDecimal("11.00"))), 0.0);
        assertEquals(0.25, termination.calculateTimeGradient(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-10.00"), new BigDecimal("-10.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("30.00"), new BigDecimal("30.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0.00"), new BigDecimal("0.00"))), 0.0);
        assertEquals(0.33333, termination.calculateTimeGradient(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("10.00"), new BigDecimal("10.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("40.00"), new BigDecimal("40.00")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("20.00"), new BigDecimal("20.00"))), 0.00001);
    }

    @Test
    public void calculateTimeGradientBendableScoreHS() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getLevelsSize()).thenReturn(2);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition,
                BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}), new double[]{0.75});

        // Normal cases
        // Smack in the middle
        assertEquals(0.6, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-400}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-14}, new int[]{-340})), 0.0);
        // No hard broken, total soft broken
        assertEquals(0.75, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-400}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-400})), 0.0);
        // Total hard broken, no soft broken
        assertEquals(0.25, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-400}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-300})), 0.0);
        // No hard broken, more than total soft broken
        assertEquals(0.75, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-400}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-900})), 0.0);
        // More than total hard broken, no soft broken
        assertEquals(0.0, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-400}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-90}, new int[]{-300})), 0.0);

        // Perfect min/max cases
        assertEquals(1.0, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300})), 0.0);
        assertEquals(0.0, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-400}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-400})), 0.0);
        assertEquals(1.0, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-400}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300})), 0.0);

        // Hard total delta is 0
        assertEquals(0.75 + (0.6 * 0.25), termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-400}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-340})), 0.0);
        assertEquals(0.0, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-400}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-340})), 0.0);
        assertEquals(1.0, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-400}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-0}, new int[]{-340})), 0.0);

        // Soft total delta is 0
        assertEquals((0.6 * 0.75) + 0.25, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-300}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-14}, new int[]{-300})), 0.0);
        assertEquals(0.6 * 0.75, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-300}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-14}, new int[]{-400})), 0.0);
        assertEquals((0.6 * 0.75) + 0.25, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-20}, new int[]{-300}), BendableScore.valueOfInitialized(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOfInitialized(new int[]{-14}, new int[]{-0})), 0.0);
    }

    @Test
    public void calculateTimeGradientBendableScoreHHSSS() {
        ScoreDefinition scoreDefinition = mock(ScoreDefinition.class);
        when(scoreDefinition.getLevelsSize()).thenReturn(5);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition,
                BendableScore.valueOfInitialized(new int[]{0, 0}, new int[]{0, 0, -10}),
                new double[]{0.75, 0.75, 0.75, 0.75});

        // Normal cases
        // Smack in the middle
        assertEquals(0.6 * 0.75 + 0.6 * 0.25 * 0.75, termination.calculateTimeGradient(
                BendableScore.valueOfInitialized(new int[]{-10, -100}, new int[]{-50, -60, -70}),
                BendableScore.valueOfInitialized(new int[]{0, 0}, new int[]{0, 0, -10}),
                BendableScore.valueOfInitialized(new int[]{-4, -40}, new int[]{-50, -60, -70})), 0.0);
    }

}
