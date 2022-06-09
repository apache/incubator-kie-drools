package org.optaplanner.benchmark.impl.ranking;

import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCompareToEquals;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCompareToOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;

class TotalRankSolverRankingWeightFactoryTest extends AbstractSolverRankingComparatorTest {

    private BenchmarkReport benchmarkReport;
    private TotalRankSolverRankingWeightFactory factory;
    private List<SolverBenchmarkResult> solverBenchmarkResultList;
    private SolverBenchmarkResult a;
    private SolverBenchmarkResult b;
    private List<SingleBenchmarkResult> aSingleBenchmarkResultList;
    private List<SingleBenchmarkResult> bSingleBenchmarkResultList;

    @BeforeEach
    void setUp() {
        benchmarkReport = mock(BenchmarkReport.class);
        factory = new TotalRankSolverRankingWeightFactory();
        solverBenchmarkResultList = new ArrayList<>();
        a = new SolverBenchmarkResult(null);
        a.setScoreDefinition(new SimpleScoreDefinition());
        b = new SolverBenchmarkResult(null);
        b.setScoreDefinition(new SimpleScoreDefinition());
        aSingleBenchmarkResultList = new ArrayList<>();
        bSingleBenchmarkResultList = new ArrayList<>();
    }

    @Test
    void normal() {
        addSingleBenchmark(a, aSingleBenchmarkResultList, -1000, -40, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -300, -40, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -40, -40, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(a);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -2000, -30, -2000); // Loses vs a
        addSingleBenchmark(b, bSingleBenchmarkResultList, -200, -30, -2000); // Wins vs a
        addSingleBenchmark(b, bSingleBenchmarkResultList, -30, -30, -2000); // Wins vs a
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(b);
        List<SingleBenchmarkResult> totalSingleBenchmarkResultList = new ArrayList<>(aSingleBenchmarkResultList);
        totalSingleBenchmarkResultList.addAll(bSingleBenchmarkResultList);
        addProblemBenchmark(totalSingleBenchmarkResultList);

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);
        assertCompareToOrder(aWeight, bWeight);
    }

    @Test
    void equalCount() {
        addSingleBenchmark(a, aSingleBenchmarkResultList, -5000, -90, -5000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -900, -90, -5000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -90, -90, -5000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList); // 0 wins - 1 equals - 5 losses
        a.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(a);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -1000, -20, -1000); // Wins vs a - wins vs c
        addSingleBenchmark(b, bSingleBenchmarkResultList, -200, -20, -1000); // Wins vs a - loses vs c
        addSingleBenchmark(b, bSingleBenchmarkResultList, -20, -20, -1000); // Wins vs a - loses vs c
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList); // 4 wins - 0 equals - 2 losses
        b.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(b);
        SolverBenchmarkResult c = new SolverBenchmarkResult(null);
        c.setScoreDefinition(new SimpleScoreDefinition());
        List<SingleBenchmarkResult> cSingleBenchmarkResultList = new ArrayList<>();
        addSingleBenchmark(c, cSingleBenchmarkResultList, -5000, -10, -5000); // Loses vs b, Equals vs a
        addSingleBenchmark(c, cSingleBenchmarkResultList, -100, -10, -5000); // Wins vs a - wins vs b
        addSingleBenchmark(c, cSingleBenchmarkResultList, -10, -10, -5000); // Wins vs a - wins vs b
        c.setSingleBenchmarkResultList(cSingleBenchmarkResultList); // 4 wins - 1 equals - 1 losses
        c.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(c);
        List<SingleBenchmarkResult> totalSingleBenchmarkResultList = new ArrayList<>(aSingleBenchmarkResultList);
        totalSingleBenchmarkResultList.addAll(bSingleBenchmarkResultList);
        totalSingleBenchmarkResultList.addAll(cSingleBenchmarkResultList);
        addProblemBenchmark(totalSingleBenchmarkResultList);

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);
        Comparable cWeight = factory.createRankingWeight(solverBenchmarkResultList, c);

        assertCompareToOrder(aWeight, bWeight, cWeight);
    }

    @Test
    void uninitializedSingleBenchmarks() {
        SingleBenchmarkResult a0 = addSingleBenchmark(a, aSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -30, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(a);
        SingleBenchmarkResult b0 = addSingleBenchmark(b, bSingleBenchmarkResultList, -1000, -30, -1000);
        SingleBenchmarkResult b1 = addSingleBenchmark(b, bSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -30, -30, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(b);
        List<SingleBenchmarkResult> totalSingleBenchmarkResultList = new ArrayList<>(aSingleBenchmarkResultList);
        totalSingleBenchmarkResultList.addAll(bSingleBenchmarkResultList);
        addProblemBenchmark(totalSingleBenchmarkResultList);

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);
        assertCompareToEquals(aWeight, bWeight);

        a0.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-100, -1000));
        b0.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-100, -1000));
        a.accumulateResults(benchmarkReport);
        b.accumulateResults(benchmarkReport);
        // ranks, uninitialized variable counts, total scores and worst scores are equal
        assertCompareToEquals(aWeight, bWeight);

        b0.setAverageAndTotalScoreForTesting(SimpleScore.of(-1000));
        b1.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-100, -400));
        b.accumulateResults(benchmarkReport);
        // ranks, uninitialized variable counts and total scores are equal, A loses on worst score (tie-breaker)
        assertCompareToOrder(aWeight, bWeight);

        b1.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-101, -400));
        b.accumulateResults(benchmarkReport);
        // ranks are equal, uninitialized variable count is bigger in B
        assertCompareToOrder(bWeight, aWeight);
    }

    @Test
    void differentNumberOfSingleBenchmarks() {
        addSingleBenchmark(a, aSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -400, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(a);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -30, -30, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(b);
        List<SingleBenchmarkResult> totalSingleBenchmarkResultList = new ArrayList<>(aSingleBenchmarkResultList);
        totalSingleBenchmarkResultList.addAll(bSingleBenchmarkResultList);
        addProblemBenchmark(totalSingleBenchmarkResultList);

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);
        assertCompareToOrder(aWeight, bWeight);
    }

    @Test
    void differentScoreTypeOfSingleBenchmarks() {
        addSingleBenchmark(a, aSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -30, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(a);
        // Scores with different number of levels are compared from the highest level, see ResilientScoreComparator.compare
        addSingleBenchmarkWithHardSoftLongScore(b, bSingleBenchmarkResultList, -1000, 0, -30, 0, -1000, -1000);
        addSingleBenchmarkWithHardSoftLongScore(b, bSingleBenchmarkResultList, -400, 0, -30, 0, -1000, -1000);
        addSingleBenchmarkWithHardSoftLongScore(b, bSingleBenchmarkResultList, -30, 0, -30, 0, -1000, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(b);
        List<SingleBenchmarkResult> totalSingleBenchmarkResultList = new ArrayList<>(aSingleBenchmarkResultList);
        totalSingleBenchmarkResultList.addAll(bSingleBenchmarkResultList);
        addProblemBenchmark(totalSingleBenchmarkResultList);

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);
        assertCompareToEquals(aWeight, bWeight);
    }

    @Test
    void disjunctPlannnerBenchmarks() {
        addSingleBenchmark(a, aSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -30, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(a);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -2000, -30, -2000);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -200, -30, -2000);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -30, -30, -2000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(b);
        // A and B have different datasets (6 datasets in total)
        for (SolverBenchmarkResult solverBenchmarkResult : solverBenchmarkResultList) {
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                addProblemBenchmark(Arrays.asList(singleBenchmarkResult));
            }
        }

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);
        // Tie-breaker, A wins on total score
        assertCompareToOrder(bWeight, aWeight);
    }

    @Test
    void disjunctEqualPlannerBenchmarks() {
        addSingleBenchmark(a, aSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(a, aSingleBenchmarkResultList, -30, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(a);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(b, bSingleBenchmarkResultList, -30, -30, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(b);
        // A and B have different datasets (6 datasets in total)
        for (SolverBenchmarkResult solverBenchmarkResult : solverBenchmarkResultList) {
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                addProblemBenchmark(Arrays.asList(singleBenchmarkResult));
            }
        }

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);
        // Tie-breaker (total score) is equal
        assertCompareToEquals(aWeight, bWeight);
    }

    @Test
    void overlappingPlannerBenchmarks() {
        SingleBenchmarkResult a0 = addSingleBenchmark(a, aSingleBenchmarkResultList, -1000, -30, -1000);
        SingleBenchmarkResult a1 = addSingleBenchmark(a, aSingleBenchmarkResultList, -400, -30, -1000);
        SingleBenchmarkResult a2 = addSingleBenchmark(a, aSingleBenchmarkResultList, -30, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(a);
        SingleBenchmarkResult b0 = addSingleBenchmark(b, bSingleBenchmarkResultList, -1000, -30, -1000);
        SingleBenchmarkResult b1 = addSingleBenchmark(b, bSingleBenchmarkResultList, -400, -30, -1000);
        SingleBenchmarkResult b2 = addSingleBenchmark(b, bSingleBenchmarkResultList, -30, -30, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(b);
        addProblemBenchmark(Arrays.asList(a0, b0));
        addProblemBenchmark(Arrays.asList(a1, b1));
        addProblemBenchmark(Arrays.asList(a2, b2));

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);
        assertCompareToEquals(aWeight, bWeight);

        addProblemBenchmark(Arrays.asList(a1));
        addProblemBenchmark(Arrays.asList(a2));
        addProblemBenchmark(Arrays.asList(b0));
        addProblemBenchmark(Arrays.asList(b2));
        addProblemBenchmark(Arrays.asList(a0, b1));
        aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);
        // A looses on score: a0 vs b1
        assertCompareToOrder(aWeight, bWeight);
    }

}
