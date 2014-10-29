package org.optaplanner.core.impl.score.director.easy;

import org.junit.Test;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class EasyScoreDirectorFactoryTest {

    private class TestSolution implements Solution<SimpleScore> {

        private SimpleScore score;

        @Override
        public SimpleScore getScore() {
            return score;
        }

        @Override
        public void setScore(SimpleScore score) {
            this.score = score;
        }

        @Override
        public Collection<? extends Object> getProblemFacts() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class EasyScoreCalcul implements EasyScoreCalculator<TestSolution> {

        public static final Integer TESTING_SCORE = -42;

        @Override
        public Score calculateScore(TestSolution solution) {
            return SimpleScore.parseScore(TESTING_SCORE.toString());
        }
    }

    @Test
    public void getEasyScoreCalculator() {
        EasyScoreDirectorFactory factory = new EasyScoreDirectorFactory(new EasyScoreCalcul());
        assertTrue(factory.getEasyScoreCalculator() instanceof EasyScoreCalcul);
        assertTrue(((SimpleScore) ((EasyScoreCalcul) factory.getEasyScoreCalculator()).calculateScore(null)).getScore()
                == EasyScoreCalcul.TESTING_SCORE);
    }

    @Test
    public void buildScoreDirector() {
        EasyScoreDirectorFactory factory = new EasyScoreDirectorFactory(new EasyScoreCalcul());
        factory.setSolutionDescriptor(new SolutionDescriptor(Solution.class));
        EasyScoreDirector director = factory.buildScoreDirector(true);
        director.setWorkingSolution(new TestSolution());
        assertTrue(((SimpleScore) director.calculateScore()).getScore() == EasyScoreCalcul.TESTING_SCORE);
        assertTrue(!director.isConstraintMatchEnabled());
        EasyScoreDirector director2 = factory.buildScoreDirector(false);
        assertTrue(!director2.isConstraintMatchEnabled());
    }

}
