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

package org.optaplanner.core.impl.score;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public class ScoreUtils {

    /**
     * @param scoreClass never null, should be of the same {@link ClassLoader} as this class.
     * @param scoreString never null
     * @return never null
     * @throws IllegalArgumentException if the scoreClass is a custom {@link Score}
     * @see ScoreDefinition#parseScore(String)
     */
    public static Score parseScore(Class<? extends Score> scoreClass, String scoreString) {
        if (SimpleScore.class.equals(scoreClass)) {
            return SimpleScore.parseScore(scoreString);
        } else if (SimpleLongScore.class.equals(scoreClass)) {
            return SimpleLongScore.parseScore(scoreString);
        } else if (SimpleBigDecimalScore.class.equals(scoreClass)) {
            return SimpleBigDecimalScore.parseScore(scoreString);
        } else if (HardSoftScore.class.equals(scoreClass)) {
            return HardSoftScore.parseScore(scoreString);
        } else if (HardSoftLongScore.class.equals(scoreClass)) {
            return HardSoftLongScore.parseScore(scoreString);
        } else if (HardSoftBigDecimalScore.class.equals(scoreClass)) {
            return HardSoftBigDecimalScore.parseScore(scoreString);
        } else if (HardMediumSoftScore.class.equals(scoreClass)) {
            return HardMediumSoftScore.parseScore(scoreString);
        } else if (HardMediumSoftLongScore.class.equals(scoreClass)) {
            return HardMediumSoftLongScore.parseScore(scoreString);
        } else if (BendableScore.class.equals(scoreClass)) {
            return BendableScore.parseScore(scoreString);
        } else if (BendableLongScore.class.equals(scoreClass)) {
            return BendableLongScore.parseScore(scoreString);
        } else if (BendableBigDecimalScore.class.equals(scoreClass)) {
            return BendableBigDecimalScore.parseScore(scoreString);
        } else {
            throw new IllegalArgumentException("Unrecognized scoreClass (" + scoreClass
                    + ") for scoreString (" + scoreString + ").");
        }
    }

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

    private ScoreUtils() {
    }

}
