/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.measurement;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

import static org.junit.Assert.*;

public class ScoreDifferencePercentageTest {

    @Test(expected = IllegalStateException.class)
    public void calculateScoreDifferencePercentageException() {
        BendableScore score1 = BendableScore.of(new int[]{1, 2, 3}, new int[]{4, 5});
        BendableScore score2 = BendableScore.of(new int[]{1, 2}, new int[]{4, 5});
        ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
    }

    @Test
    public void calculateScoreDifferencePercentage() {
        double tolerance = 0.00001;
        SimpleScore score1 = SimpleScore.of(-100);
        SimpleScore score2 = SimpleScore.of(-100);
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(0.0, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        score1 = SimpleScore.of(100);
        score2 = SimpleScore.of(100);
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(0.0, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        score1 = SimpleScore.of(-100);
        score2 = SimpleScore.of(-10);
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(0.9, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        score1 = SimpleScore.of(100);
        score2 = SimpleScore.of(10);
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(-0.9, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        score1 = SimpleScore.of(-100);
        score2 = SimpleScore.of(-1);
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(0.99, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        score1 = SimpleScore.of(100);
        score2 = SimpleScore.of(1);
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(-0.99, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        HardSoftScore hardSoftScore1 = HardSoftScore.of(-100, -1);
        HardSoftScore hardSoftScore2 = HardSoftScore.of(-100, -1);
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);
        assertEquals(0.0, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);
        assertEquals(0.0, scoreDifferencePercentage.getPercentageLevels()[1], tolerance);

        hardSoftScore1 = HardSoftScore.of(-100, -100);
        hardSoftScore2 = HardSoftScore.of(-1, -10);
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);
        assertEquals(0.99, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);
        assertEquals(0.9, scoreDifferencePercentage.getPercentageLevels()[1], tolerance);

        hardSoftScore1 = HardSoftScore.of(100, 100);
        hardSoftScore2 = HardSoftScore.of(1, 10);
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);
        assertEquals(-0.99, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);
        assertEquals(-0.9, scoreDifferencePercentage.getPercentageLevels()[1], tolerance);

        hardSoftScore1 = HardSoftScore.of(100, -100);
        hardSoftScore2 = HardSoftScore.of(-100, 200);
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);
        assertEquals(-2, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);
        assertEquals(3, scoreDifferencePercentage.getPercentageLevels()[1], tolerance);
    }

    @Test
    public void add() {
        double tolerance = 0.00001;
        HardSoftScore hardSoftScore1 = HardSoftScore.of(-100, -1);
        HardSoftScore hardSoftScore2 = HardSoftScore.of(-200, -10);
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        hardSoftScore1 = HardSoftScore.of(-100, -1);
        hardSoftScore2 = HardSoftScore.of(-200, -10);
        ScoreDifferencePercentage scoreDifferencePercentage2 =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        double[] levels = scoreDifferencePercentage.add(scoreDifferencePercentage2).getPercentageLevels();
        assertEquals(-2.0, levels[0], tolerance);
        assertEquals(-18.0, levels[1], tolerance);
    }

    @Test
    public void subtract() {
        double tolerance = 0.00001;
        HardSoftScore hardSoftScore1 = HardSoftScore.of(-100, -1);
        HardSoftScore hardSoftScore2 = HardSoftScore.of(-200, -10);
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        hardSoftScore1 = HardSoftScore.of(-100, -1);
        hardSoftScore2 = HardSoftScore.of(-200, -10);
        ScoreDifferencePercentage scoreDifferencePercentage2 =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        double[] levels = scoreDifferencePercentage.subtract(scoreDifferencePercentage2).getPercentageLevels();
        assertEquals(0.0, levels[0], tolerance);
        assertEquals(0.0, levels[1], tolerance);
    }

    @Test
    public void multiply() {
        double tolerance = 0.00001;
        HardSoftScore hardSoftScore1 = HardSoftScore.of(-100, -1);
        HardSoftScore hardSoftScore2 = HardSoftScore.of(-200, -10);
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        double[] levels = scoreDifferencePercentage.multiply(3.14).getPercentageLevels();
        assertEquals(-3.14, levels[0], tolerance);
        assertEquals(-28.26, levels[1], tolerance);

        levels = scoreDifferencePercentage.multiply(-1).getPercentageLevels();
        assertEquals(1, levels[0], tolerance);
        assertEquals(9.0, levels[1], tolerance);
    }

    @Test
    public void divide() {
        double tolerance = 0.00001;
        HardSoftScore hardSoftScore1 = HardSoftScore.of(-100, -1);
        HardSoftScore hardSoftScore2 = HardSoftScore.of(-200, -10);
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        double[] levels = scoreDifferencePercentage.multiply(0.5).getPercentageLevels();
        assertEquals(-0.5, levels[0], tolerance);
        assertEquals(-4.5, levels[1], tolerance);

        levels = scoreDifferencePercentage.multiply(-1).getPercentageLevels();
        assertEquals(1, levels[0], tolerance);
        assertEquals(9.0, levels[1], tolerance);
    }

    @Test(expected = IllegalStateException.class)
    public void addWithWrongDimension() {
        HardSoftScore hardSoftScore1 = HardSoftScore.of(-100, -1);
        HardSoftScore hardSoftScore2 = HardSoftScore.of(-200, -10);
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        SimpleScore score1 = SimpleScore.of(-100);
        SimpleScore score2 = SimpleScore.of(-200);
        ScoreDifferencePercentage scoreDifferencePercentage2 =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);

        scoreDifferencePercentage.add(scoreDifferencePercentage2);
    }

    @Test(expected = IllegalStateException.class)
    public void subtractWithWrongDimension() {
        HardSoftScore hardSoftScore1 = HardSoftScore.of(-100, -1);
        HardSoftScore hardSoftScore2 = HardSoftScore.of(-200, -10);
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        SimpleScore score1 = SimpleScore.of(-100);
        SimpleScore score2 = SimpleScore.of(-200);
        ScoreDifferencePercentage scoreDifferencePercentage2 =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);

        scoreDifferencePercentage.subtract(scoreDifferencePercentage2);
    }

}
