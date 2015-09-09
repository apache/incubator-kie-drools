/*
 * Copyright 2012 JBoss Inc
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

    protected SingleBenchmarkResult addSingleBenchmark(List<SingleBenchmarkResult> singleBenchmarkResultList,
            int score, int bestScore, int worstScore) {
        SingleBenchmarkResult singleBenchmarkResult = new SingleBenchmarkResult(null, null);
        SimpleScore scoreObject = SimpleScore.valueOf(score);
        SimpleScore bestScoreObject = SimpleScore.valueOf(bestScore);
        SimpleScore worstScoreObject = SimpleScore.valueOf(worstScore);
        singleBenchmarkResult.setAverageScore(scoreObject);
        singleBenchmarkResult.setWinningScoreDifference(scoreObject.subtract(bestScoreObject));
        singleBenchmarkResult.setWorstScoreDifferencePercentage(
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(worstScoreObject, scoreObject));
        singleBenchmarkResult.setAverageUninitializedVariableCount(0);
        singleBenchmarkResultList.add(singleBenchmarkResult);
        return singleBenchmarkResult;
    }

    protected SingleBenchmarkResult addSingleBenchmarkWithHardSoftLongScore(List<SingleBenchmarkResult> singleBenchmarkResultList,
            long hardScore, long softScore, long hardBestScore, long softBestScore, long hardWorstScore, long softWorstScore) {
        SingleBenchmarkResult singleBenchmarkResult = new SingleBenchmarkResult(null, null);
        HardSoftLongScore scoreObject = HardSoftLongScore.valueOf(hardScore, softScore);
        HardSoftLongScore bestScoreObject = HardSoftLongScore.valueOf(hardBestScore, softBestScore);
        HardSoftLongScore worstScoreObject = HardSoftLongScore.valueOf(hardWorstScore, softWorstScore);
        singleBenchmarkResult.setAverageScore(scoreObject);
        singleBenchmarkResult.setWinningScoreDifference(scoreObject.subtract(bestScoreObject));
        singleBenchmarkResult.setWorstScoreDifferencePercentage(
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(worstScoreObject, scoreObject));
        singleBenchmarkResult.setAverageUninitializedVariableCount(0);
        singleBenchmarkResultList.add(singleBenchmarkResult);
        return singleBenchmarkResult;
    }

}
