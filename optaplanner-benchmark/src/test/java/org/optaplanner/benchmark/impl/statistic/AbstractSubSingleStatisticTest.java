package org.optaplanner.benchmark.impl.statistic;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public abstract class AbstractSubSingleStatisticTest<Point_ extends StatisticPoint, SubSingleStatistic_ extends SubSingleStatistic<TestdataSolution, Point_>> {

    @Test
    void test(@TempDir Path tempDir) {
        SubSingleBenchmarkResult subSingleBenchmarkResult = createSubStatistic(tempDir.toFile());
        Function<SubSingleBenchmarkResult, SubSingleStatistic_> constructor = getSubSingleStatisticConstructor();
        SubSingleStatistic_ subSingleStatistic = constructor.apply(subSingleBenchmarkResult);

        // Persist the point list.
        subSingleStatistic.setPointList(getInputPoints());
        subSingleStatistic.hibernatePointList();

        // Re-read the point list.
        SubSingleStatistic_ subSingleStatisticUnhibernated = constructor.apply(subSingleBenchmarkResult);
        subSingleStatisticUnhibernated.unhibernatePointList();

        SoftAssertions.assertSoftly(softly -> runTest(softly, subSingleStatisticUnhibernated.getPointList()));
    }

    protected abstract Function<SubSingleBenchmarkResult, SubSingleStatistic_> getSubSingleStatisticConstructor();

    protected abstract List<Point_> getInputPoints();

    protected abstract void runTest(SoftAssertions assertions, List<Point_> outputPoints);

    private SubSingleBenchmarkResult createSubStatistic(File tempDirectory) {
        PlannerBenchmarkResult plannerBenchmarkResult = new PlannerBenchmarkResult();
        plannerBenchmarkResult.setBenchmarkReportDirectory(tempDirectory);
        ProblemBenchmarkResult<TestdataSolution> problemBenchmarkResult = new ProblemBenchmarkResult<>(plannerBenchmarkResult);
        problemBenchmarkResult.setName(UUID.randomUUID().toString());
        SolverBenchmarkResult solverBenchmarkResult = new SolverBenchmarkResult(plannerBenchmarkResult);
        solverBenchmarkResult.setScoreDefinition(TestdataSolution.buildSolutionDescriptor()
                .getScoreDefinition());
        solverBenchmarkResult.setName(UUID.randomUUID().toString());
        SingleBenchmarkResult singleBenchmarkResult = new SingleBenchmarkResult(solverBenchmarkResult, problemBenchmarkResult);
        SubSingleBenchmarkResult subSingleBenchmarkResult = new SubSingleBenchmarkResult(singleBenchmarkResult, 0);
        singleBenchmarkResult.setSubSingleBenchmarkResultList(Collections.singletonList(subSingleBenchmarkResult));
        problemBenchmarkResult.setSingleBenchmarkResultList(Collections.singletonList(singleBenchmarkResult));
        problemBenchmarkResult.makeDirs();
        return subSingleBenchmarkResult;
    }

}
