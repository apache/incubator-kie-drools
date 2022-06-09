package org.optaplanner.benchmark.impl.ranking;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCompareToOrder;

import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;

class ScoreSubSingleBenchmarkRankingComparatorTest {

    @Test
    void compareTo() {
        ScoreSubSingleBenchmarkRankingComparator comparator = new ScoreSubSingleBenchmarkRankingComparator();
        SolverBenchmarkResult solverBenchmarkResult = mock(SolverBenchmarkResult.class);
        when(solverBenchmarkResult.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        SingleBenchmarkResult singleBenchmarkResult = new SingleBenchmarkResult(solverBenchmarkResult,
                mock(ProblemBenchmarkResult.class));
        SubSingleBenchmarkResult a = new SubSingleBenchmarkResult(singleBenchmarkResult, 0);
        a.setSucceeded(false);
        a.setScore(null);
        SubSingleBenchmarkResult b = new SubSingleBenchmarkResult(singleBenchmarkResult, 1);
        b.setSucceeded(true);
        b.setScore(SimpleScore.ofUninitialized(-7, -1));
        SubSingleBenchmarkResult c = new SubSingleBenchmarkResult(singleBenchmarkResult, 2);
        c.setSucceeded(true);
        c.setScore(SimpleScore.of(-300));
        SubSingleBenchmarkResult d = new SubSingleBenchmarkResult(singleBenchmarkResult, 3);
        d.setSucceeded(true);
        d.setScore(SimpleScore.of(-20));
        assertCompareToOrder(comparator, a, b, c, d);
    }

}
