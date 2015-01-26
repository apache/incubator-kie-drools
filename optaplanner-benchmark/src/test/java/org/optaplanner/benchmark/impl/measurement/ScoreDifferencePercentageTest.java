package org.optaplanner.benchmark.impl.measurement;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

import static org.junit.Assert.assertEquals;

public class ScoreDifferencePercentageTest {

    @Test(expected = IllegalStateException.class)
    public void calculateScoreDifferencePercentageException() {
        int[] hardScore1 = {1, 2, 3};
        int[] softScore1 = {4, 5};
        int[] hardScore2 = {1, 2};
        int[] softScore2 = {4, 5};
        BendableScore score1 = BendableScore.valueOf(hardScore1, softScore1);
        BendableScore score2 = BendableScore.valueOf(hardScore2, softScore2);
        ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
    }

    @Test
    public void calculateScoreDifferencePercentage() {
        double tolerance = 0.00001;
        SimpleScore score1 = SimpleScore.parseScore("-100");
        SimpleScore score2 = SimpleScore.parseScore("-100");
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(0.0, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        score1 = SimpleScore.parseScore("100");
        score2 = SimpleScore.parseScore("100");
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(0.0, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        score1 = SimpleScore.parseScore("-100");
        score2 = SimpleScore.parseScore("-10");
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(0.9, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        score1 = SimpleScore.parseScore("100");
        score2 = SimpleScore.parseScore("10");
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(-0.9, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        score1 = SimpleScore.parseScore("-100");
        score2 = SimpleScore.parseScore("-1");
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(0.99, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        score1 = SimpleScore.parseScore("100");
        score2 = SimpleScore.parseScore("1");
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);
        assertEquals(-0.99, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);

        HardSoftScore hardSoftScore1 = HardSoftScore.parseScore("-100hard/-1soft");
        HardSoftScore hardSoftScore2 = HardSoftScore.parseScore("-100hard/-1soft");
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);
        assertEquals(0.0, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);
        assertEquals(0.0, scoreDifferencePercentage.getPercentageLevels()[1], tolerance);

        hardSoftScore1 = HardSoftScore.parseScore("-100hard/-100soft");
        hardSoftScore2 = HardSoftScore.parseScore("-1hard/-10soft");
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);
        assertEquals(0.99, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);
        assertEquals(0.9, scoreDifferencePercentage.getPercentageLevels()[1], tolerance);

        hardSoftScore1 = HardSoftScore.parseScore("100hard/100soft");
        hardSoftScore2 = HardSoftScore.parseScore("1hard/10soft");
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);
        assertEquals(-0.99, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);
        assertEquals(-0.9, scoreDifferencePercentage.getPercentageLevels()[1], tolerance);

        hardSoftScore1 = HardSoftScore.parseScore("100hard/-100soft");
        hardSoftScore2 = HardSoftScore.parseScore("-100hard/200soft");
        scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);
        assertEquals(-2, scoreDifferencePercentage.getPercentageLevels()[0], tolerance);
        assertEquals(3, scoreDifferencePercentage.getPercentageLevels()[1], tolerance);
    }


    @Test
    public void add() {
        double tolerance = 0.00001;
        HardSoftScore hardSoftScore1 = HardSoftScore.parseScore("-100hard/-1soft");
        HardSoftScore hardSoftScore2 = HardSoftScore.parseScore("-200hard/-10soft");
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        hardSoftScore1 = HardSoftScore.parseScore("-100hard/-1soft");
        hardSoftScore2 = HardSoftScore.parseScore("-200hard/-10soft");
        ScoreDifferencePercentage scoreDifferencePercentage2 =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        double[] levels = scoreDifferencePercentage.add(scoreDifferencePercentage2).getPercentageLevels();
        assertEquals(-2.0, levels[0], tolerance);
        assertEquals(-18.0, levels[1], tolerance);
    }

    @Test
    public void substract() {
        double tolerance = 0.00001;
        HardSoftScore hardSoftScore1 = HardSoftScore.parseScore("-100hard/-1soft");
        HardSoftScore hardSoftScore2 = HardSoftScore.parseScore("-200hard/-10soft");
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        hardSoftScore1 = HardSoftScore.parseScore("-100hard/-1soft");
        hardSoftScore2 = HardSoftScore.parseScore("-200hard/-10soft");
        ScoreDifferencePercentage scoreDifferencePercentage2 =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        double[] levels = scoreDifferencePercentage.subtract(scoreDifferencePercentage2).getPercentageLevels();
        assertEquals(0.0, levels[0], tolerance);
        assertEquals(0.0, levels[1], tolerance);
    }

    @Test
    public void multiply() {
        double tolerance = 0.00001;
        HardSoftScore hardSoftScore1 = HardSoftScore.parseScore("-100hard/-1soft");
        HardSoftScore hardSoftScore2 = HardSoftScore.parseScore("-200hard/-10soft");
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
        HardSoftScore hardSoftScore1 = HardSoftScore.parseScore("-100hard/-1soft");
        HardSoftScore hardSoftScore2 = HardSoftScore.parseScore("-200hard/-10soft");
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
        HardSoftScore hardSoftScore1 = HardSoftScore.parseScore("-100hard/-1soft");
        HardSoftScore hardSoftScore2 = HardSoftScore.parseScore("-200hard/-10soft");
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        SimpleScore score1 = SimpleScore.parseScore("-100");
        SimpleScore score2 = SimpleScore.parseScore("-200");
        ScoreDifferencePercentage scoreDifferencePercentage2 =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);

        scoreDifferencePercentage.add(scoreDifferencePercentage2);
    }

    @Test(expected = IllegalStateException.class)
    public void subtractWithWrongDimension() {
        HardSoftScore hardSoftScore1 = HardSoftScore.parseScore("-100hard/-1soft");
        HardSoftScore hardSoftScore2 = HardSoftScore.parseScore("-200hard/-10soft");
        ScoreDifferencePercentage scoreDifferencePercentage =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(hardSoftScore1, hardSoftScore2);

        SimpleScore score1 = SimpleScore.parseScore("-100");
        SimpleScore score2 = SimpleScore.parseScore("-200");
        ScoreDifferencePercentage scoreDifferencePercentage2 =
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(score1, score2);

        scoreDifferencePercentage.subtract(scoreDifferencePercentage2);
    }

}
