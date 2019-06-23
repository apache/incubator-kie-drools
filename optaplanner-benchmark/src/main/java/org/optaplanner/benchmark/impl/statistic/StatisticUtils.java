/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.optaplanner.benchmark.impl.result.BenchmarkResult;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.ScoreUtils;

public class StatisticUtils {

    private StatisticUtils() {
        // This class is not instantiable
    }

    /**
     * Calculates standard deviation of {@link BenchmarkResult#getAverageScore()}s from {@code averageScore}.
     * @param averageScore not null
     * @return standard deviation double values
     */
    public static double[] determineStandardDeviationDoubles(
            List<? extends BenchmarkResult> benchmarkResultList, Score averageScore, int successCount) {
        if (successCount <= 0) {
            return new double[0];
        }
        if (averageScore == null) {
            throw new IllegalArgumentException("Average score (" + averageScore + ") cannot be null.");
        }
        // averageScore can no longer be null
        double[] differenceSquaredTotalDoubles = null;
        for (BenchmarkResult benchmarkResult : benchmarkResultList) {
            if (benchmarkResult.hasAllSuccess()) {
                Score difference = benchmarkResult.getAverageScore().subtract(averageScore);
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

        if (differenceSquaredTotalDoubles == null) { // no successful benchmarks
            return new double[0];
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
        // Abbreviate to 2 decimals
        // We don't use a local sensitive DecimalFormat, because other Scores don't use it either (see PLANNER-169)
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat exponentialFormat = new DecimalFormat("0.0#E0", decimalFormatSymbols);
        DecimalFormat decimalFormat = new DecimalFormat("0.0#", decimalFormatSymbols);
        boolean first = true;
        for (double standardDeviationDouble : standardDeviationDoubles) {
            if (first) {
                first = false;
            } else {
                standardDeviationString.append("/");
            }
            // See http://docs.oracle.com/javase/7/docs/api/java/lang/Double.html#toString%28double%29
            String abbreviated;
            if (0.001 <= standardDeviationDouble && standardDeviationDouble <= 10000000.0) {
                abbreviated = decimalFormat.format(standardDeviationDouble);
            } else {
                abbreviated = exponentialFormat.format(standardDeviationDouble);
            }
            standardDeviationString.append(abbreviated);
        }
        return standardDeviationString.toString();
    }

}
