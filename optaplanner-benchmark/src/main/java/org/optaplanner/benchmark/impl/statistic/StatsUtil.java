/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.impl.statistic;

import java.util.List;

import org.optaplanner.benchmark.impl.result.SolverProblemBenchmarkResult;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.ScoreUtils;

public class StatsUtil {

    /**
     * Calculates standard deviation of {@link SolverProblemBenchmarkResult#getScore()}s from {@code averageScore}.
     * @param averageScore not null
     * @return standard deviation double values
     */
    public static double[] determineStandardDeviationDoubles(
            List<? extends SolverProblemBenchmarkResult> solverProblemBenchmarkResultList, Score averageScore, int successCount) {
        if (successCount <= 0) {
            return null;
        }
        if (averageScore == null) {
            throw new IllegalArgumentException("Average score ( " + averageScore + " ) cannot be null.");
        }
        // averageScore can no longer be null
        double[] differenceSquaredTotalDoubles = null;
        for (SolverProblemBenchmarkResult solverProblemBenchmarkResult : solverProblemBenchmarkResultList) {
            if (solverProblemBenchmarkResult.isSuccess()) {
                Score difference = solverProblemBenchmarkResult.getScore().subtract(averageScore);
                // Calculations done on doubles to avoid common overflow when executing with an int score > 500 000
                double[] differenceDoubles = ScoreUtils.extractLevelDoubles(difference);
                if (differenceSquaredTotalDoubles == null) {
                    differenceSquaredTotalDoubles = new double[differenceDoubles.length];
                }
                for (int i = 0; i < differenceDoubles.length; i++) {
                    differenceSquaredTotalDoubles[i] += Math.pow(differenceDoubles[i], 2.0);
                }
            }
        }
        double[] standardDeviationDoubles = new double[differenceSquaredTotalDoubles.length];
        for (int i = 0; i < differenceSquaredTotalDoubles.length; i++) {
            standardDeviationDoubles[i] = Math.pow(differenceSquaredTotalDoubles[i] / successCount, 0.5);
        }
        return standardDeviationDoubles;
    }

    // TODO Do the locale formatting in benchmarkReport.html.ftl - https://issues.jboss.org/browse/PLANNER-169
    public static String getStandardDeviationString(double[] standardDeviationDoubles) {
        if (standardDeviationDoubles == null) {
            return null;
        }
        StringBuilder standardDeviationString = new StringBuilder(standardDeviationDoubles.length * 9);
        boolean first = true;
        for (double standardDeviationDouble : standardDeviationDoubles) {
            if (first) {
                first = false;
            } else {
                standardDeviationString.append("/");
            }
            String abbreviated = Double.toString(standardDeviationDouble);
            // Abbreviate to 2 decimals
            // We don't use DecimalFormat to abbreviate because it's written locale insensitive (like java literals)
            int dotIndex = abbreviated.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex + 3 < abbreviated.length()) {
                abbreviated = abbreviated.substring(0, dotIndex + 3);
            }
            standardDeviationString.append(abbreviated);
        }
        return standardDeviationString.toString();
    }

}
