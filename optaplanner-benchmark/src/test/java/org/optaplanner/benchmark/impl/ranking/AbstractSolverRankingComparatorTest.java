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

package org.optaplanner.benchmark.impl.ranking;

import java.util.List;

import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

public abstract class AbstractSolverRankingComparatorTest {

    protected ProblemBenchmarkResult addProblemBenchmark(List<SingleBenchmarkResult> singleBenchmarkResultList) {
        ProblemBenchmarkResult problemBenchmarkResult = new ProblemBenchmarkResult(null);
        problemBenchmarkResult.setSingleBenchmarkResultList(singleBenchmarkResultList);
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            singleBenchmarkResult.setProblemBenchmarkResult(problemBenchmarkResult);
        }
        return problemBenchmarkResult;
    }

    protected SingleBenchmarkResult addSingleBenchmark(SolverBenchmarkResult solverBenchmarkResult,
            List<SingleBenchmarkResult> singleBenchmarkResultList, int score, int bestScore, int worstScore) {
        return addSingleBenchmark(solverBenchmarkResult, singleBenchmarkResultList,
                SimpleScore.of(score),
                SimpleScore.of(bestScore),
                SimpleScore.of(worstScore));
    }

    protected SingleBenchmarkResult addSingleBenchmarkWithHardSoftLongScore(SolverBenchmarkResult solverBenchmarkResult,
            List<SingleBenchmarkResult> singleBenchmarkResultList,
            long hardScore, long softScore, long hardBestScore, long softBestScore, long hardWorstScore, long softWorstScore) {
        return addSingleBenchmark(solverBenchmarkResult, singleBenchmarkResultList,
                HardSoftLongScore.of(hardScore, softScore),
                HardSoftLongScore.of(hardBestScore, softBestScore),
                HardSoftLongScore.of(hardWorstScore, softWorstScore));
    }

    protected <S extends Score<S>> SingleBenchmarkResult addSingleBenchmark(SolverBenchmarkResult solverBenchmarkResult,
            List<SingleBenchmarkResult> singleBenchmarkResultList,
            S score, S bestScore, S worstScore) {
        SingleBenchmarkResult singleBenchmarkResult = new SingleBenchmarkResult(solverBenchmarkResult, null);
        singleBenchmarkResult.setFailureCount(0);
        singleBenchmarkResult.setAverageAndTotalScoreForTesting(score);
        singleBenchmarkResult.setWinningScoreDifference(score.subtract(bestScore));
        singleBenchmarkResult.setWorstScoreDifferencePercentage(
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(worstScore, score));
        singleBenchmarkResult.setWorstScoreCalculationSpeedDifferencePercentage(5.0);
        singleBenchmarkResultList.add(singleBenchmarkResult);
        return singleBenchmarkResult;
    }

}
