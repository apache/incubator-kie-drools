package org.optaplanner.core.impl.score.director.easy;

import org.junit.Test;
import org.mockito.Matchers;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedSolution;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EasyScoreDirectorFactoryTest {

    @Test
    public void getEasyScoreCalculator() {
        EasyScoreCalculator scoreCalculator = mock(EasyScoreCalculator.class);
        EasyScoreDirectorFactory directorFactory = new EasyScoreDirectorFactory(scoreCalculator);
        assertSame(scoreCalculator, directorFactory.getEasyScoreCalculator());
    }

    @Test
    public void buildScoreDirector() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EasyScoreCalculator scoreCalculator = mock(EasyScoreCalculator.class);
        when(scoreCalculator.calculateScore(any(TestdataSolution.class))).thenReturn(SimpleScore.valueOf(-10));
        EasyScoreDirectorFactory directorFactory = new EasyScoreDirectorFactory(scoreCalculator);
        directorFactory.setSolutionDescriptor(solutionDescriptor);

        EasyScoreDirector director = directorFactory.buildScoreDirector(false);
        director.setWorkingSolution(new TestdataSolution());
        assertEquals(SimpleScore.valueOf(-10), director.calculateScore());
    }

}
