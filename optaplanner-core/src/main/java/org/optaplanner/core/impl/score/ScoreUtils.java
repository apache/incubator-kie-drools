/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score;

import org.optaplanner.core.api.score.Score;

public class ScoreUtils {

    public static double[] extractLevelDoubles(Score score) {
        Number[] levelNumbers = score.toLevelNumbers();
        double[] levelDoubles = new double[levelNumbers.length];
        for (int i = 0; i < levelNumbers.length; i++) {
            levelDoubles[i] = levelNumbers[i].doubleValue();
        }
        return levelDoubles;
    }

    /**
     *
     * @param totalDiffNumbers never null
     * @param scoreDiffNumbers never null
     * @param timeGradientWeightNumbers never null
     * @param levelDepth The number of levels of the diffNumbers that are included
     * @return {@code 0.0 <= value <= 1.0}
     */
    public static double calculateTimeGradient(Number[] totalDiffNumbers, Number[] scoreDiffNumbers,
            double[] timeGradientWeightNumbers, int levelDepth) {
        double timeGradient = 0.0;
        double remainingTimeGradient = 1.0;
        for (int i = 0; i < levelDepth; i++) {
            double levelTimeGradientWeight;
            if (i != (levelDepth - 1)) {
                levelTimeGradientWeight = remainingTimeGradient * timeGradientWeightNumbers[i];
                remainingTimeGradient -= levelTimeGradientWeight;
            } else {
                levelTimeGradientWeight = remainingTimeGradient;
                remainingTimeGradient = 0.0;
            }
            double totalDiffLevel = totalDiffNumbers[i].doubleValue();
            double scoreDiffLevel = scoreDiffNumbers[i].doubleValue();
            if (scoreDiffLevel == totalDiffLevel) {
                // Max out this level
                timeGradient += levelTimeGradientWeight;
            } else if (scoreDiffLevel > totalDiffLevel) {
                // Max out this level and all softer levels too
                timeGradient += levelTimeGradientWeight + remainingTimeGradient;
                break;
            } else if (scoreDiffLevel == 0.0) {
                // Ignore this level
                // timeGradient += 0.0
            } else if (scoreDiffLevel < 0.0) {
                // Ignore this level and all softer levels too
                // timeGradient += 0.0
                break;
            } else {
                double levelTimeGradient = (double) scoreDiffLevel / (double) totalDiffLevel;
                timeGradient += levelTimeGradient * levelTimeGradientWeight;
            }

        }
        if (timeGradient > 1.0) {
            // Rounding error due to calculating with doubles
            timeGradient = 1.0;
        }
        return timeGradient;
    }

    // TODO remove me (and all occurences) once https://issues.jboss.org/browse/PLANNER-405 is fixed
    public static String getScoreWithUninitializedPrefix(int uninitializedVariableCount, Score score) {
        if (score == null) {
            return null;
        }
        return uninitializedVariableCount == 0 ? score.toString() : uninitializedVariableCount + "uninitialized/" + score.toString();
    }

    private ScoreUtils() {
    }

}
