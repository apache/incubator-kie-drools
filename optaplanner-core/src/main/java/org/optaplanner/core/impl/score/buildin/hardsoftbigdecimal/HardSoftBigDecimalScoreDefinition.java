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

package org.optaplanner.core.impl.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreHolder;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.score.definition.AbstractFeasibilityScoreDefinition;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class HardSoftBigDecimalScoreDefinition extends AbstractFeasibilityScoreDefinition<HardSoftBigDecimalScore> {

    private double hardScoreTimeGradientWeight = 0.75; // TODO this is a guess

    public double getHardScoreTimeGradientWeight() {
        return hardScoreTimeGradientWeight;
    }

    /**
     * It's recommended to use a number which can be exactly represented as a double,
     * such as 0.5, 0.25, 0.75, 0.125, ... but not 0.1, 0.2, ...
     * @param hardScoreTimeGradientWeight 0.0 <= hardScoreTimeGradientWeight <= 1.0
     */
    public void setHardScoreTimeGradientWeight(double hardScoreTimeGradientWeight) {
        this.hardScoreTimeGradientWeight = hardScoreTimeGradientWeight;
        if (hardScoreTimeGradientWeight < 0.0 || hardScoreTimeGradientWeight > 1.0) {
            throw new IllegalArgumentException("Property hardScoreTimeGradientWeight (" + hardScoreTimeGradientWeight
                    + ") must be greater or equal to 0.0 and smaller or equal to 1.0.");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelCount() {
        return 2;
    }

    @Override
    public int getFeasibleLevelCount() {
        return 1;
    }

    public Class<HardSoftBigDecimalScore> getScoreClass() {
        return HardSoftBigDecimalScore.class;
    }

    public HardSoftBigDecimalScore parseScore(String scoreString) {
        return HardSoftBigDecimalScore.parseScore(scoreString);
    }

    public HardSoftBigDecimalScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new HardSoftBigDecimalScoreHolder(constraintMatchEnabled);
    }

    public HardSoftBigDecimalScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, HardSoftBigDecimalScore score) {
        // TODO https://issues.jboss.org/browse/PLANNER-232
        throw new UnsupportedOperationException("PLANNER-232: BigDecimalScore does not support bounds" +
                " because a BigDecimal cannot represent infinity.");
    }

    public HardSoftBigDecimalScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, HardSoftBigDecimalScore score) {
        // TODO https://issues.jboss.org/browse/PLANNER-232
        throw new UnsupportedOperationException("PLANNER-232: BigDecimalScore does not support bounds" +
                " because a BigDecimal cannot represent infinity.");
    }

}
