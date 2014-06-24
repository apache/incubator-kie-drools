/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;
import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScoreHolder;
import org.optaplanner.core.impl.score.definition.AbstractFeasibilityScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class BendableBigDecimalScoreDefinition extends AbstractFeasibilityScoreDefinition<BendableBigDecimalScore> {

    private final int hardLevelsSize;
    private final int softLevelsSize;

    public BendableBigDecimalScoreDefinition(int hardLevelsSize, int softLevelsSize) {
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

    public Class<BendableBigDecimalScore> getScoreClass() {
        return BendableBigDecimalScore.class;
    }

    public BendableBigDecimalScore parseScore(String scoreString) {
        return BendableBigDecimalScore.parseScore(hardLevelsSize, softLevelsSize, scoreString);
    }

    public BendableBigDecimalScore createScore(BigDecimal... scores) {
        int levelsSize = hardLevelsSize + softLevelsSize;
        if (scores.length != levelsSize) {
            throw new IllegalArgumentException("The scores (" + Arrays.toString(scores)
                    + ")'s length (" + scores.length
                    + ") is not levelsSize (" + levelsSize + ").");
        }
        return BendableBigDecimalScore.valueOf(Arrays.copyOfRange(scores, 0, hardLevelsSize),
                Arrays.copyOfRange(scores, hardLevelsSize, levelsSize));
    }

    public BendableBigDecimalScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new BendableBigDecimalScoreHolder(constraintMatchEnabled, hardLevelsSize, softLevelsSize);
    }

    public BendableBigDecimalScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, BendableBigDecimalScore score) {
        // TODO https://issues.jboss.org/browse/PLANNER-232
        throw new UnsupportedOperationException("PLANNER-232: BigDecimalScore does not support bounds" +
                " because a BigDecimal cannot represent infinity.");
    }

    public BendableBigDecimalScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, BendableBigDecimalScore score) {
        // TODO https://issues.jboss.org/browse/PLANNER-232
        throw new UnsupportedOperationException("PLANNER-232: BigDecimalScore does not support bounds" +
                " because a BigDecimal cannot represent infinity.");
    }

}
