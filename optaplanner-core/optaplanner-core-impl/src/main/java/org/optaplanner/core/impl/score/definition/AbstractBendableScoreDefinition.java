/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.score.definition;

import org.optaplanner.core.api.score.IBendableScore;
import org.optaplanner.core.api.score.Score;

public abstract class AbstractBendableScoreDefinition<Score_ extends Score<Score_>> extends AbstractScoreDefinition<Score_>
        implements ScoreDefinition<Score_> {

    protected static String[] generateLevelLabels(int hardLevelsSize, int softLevelsSize) {
        if (hardLevelsSize < 0 || softLevelsSize < 0) {
            throw new IllegalArgumentException("The hardLevelsSize (" + hardLevelsSize
                    + ") and softLevelsSize (" + softLevelsSize + ") should be positive.");
        }
        String[] levelLabels = new String[hardLevelsSize + softLevelsSize];
        for (int i = 0; i < levelLabels.length; i++) {
            String labelPrefix;
            if (i < hardLevelsSize) {
                labelPrefix = "hard " + i;
            } else {
                labelPrefix = "soft " + (i - hardLevelsSize);
            }
            levelLabels[i] = labelPrefix + " score";
        }
        return levelLabels;
    }

    protected final int hardLevelsSize;
    protected final int softLevelsSize;

    public AbstractBendableScoreDefinition(int hardLevelsSize, int softLevelsSize) {
        super(generateLevelLabels(hardLevelsSize, softLevelsSize));
        this.hardLevelsSize = hardLevelsSize;
        this.softLevelsSize = softLevelsSize;
    }

    public int getHardLevelsSize() {
        return hardLevelsSize;
    }

    public int getSoftLevelsSize() {
        return softLevelsSize;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelsSize() {
        return hardLevelsSize + softLevelsSize;
    }

    @Override
    public int getFeasibleLevelsSize() {
        return hardLevelsSize;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score score) {
        if (super.isCompatibleArithmeticArgument(score)) {
            IBendableScore<?> bendableScore = (IBendableScore<?>) score;
            return getLevelsSize() == bendableScore.levelsSize()
                    && getHardLevelsSize() == bendableScore.hardLevelsSize()
                    && getSoftLevelsSize() == bendableScore.softLevelsSize();
        }
        return false;
    }
}
