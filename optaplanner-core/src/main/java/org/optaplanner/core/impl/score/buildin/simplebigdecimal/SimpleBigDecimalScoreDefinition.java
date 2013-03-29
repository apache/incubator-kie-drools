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

package org.optaplanner.core.impl.score.buildin.simplebigdecimal;

import java.math.BigDecimal;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScoreHolder;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.api.score.holder.ScoreHolder;

public class SimpleBigDecimalScoreDefinition extends AbstractScoreDefinition<SimpleBigDecimalScore> {

    private SimpleBigDecimalScore perfectMaximumScore = SimpleBigDecimalScore.valueOf(BigDecimal.ZERO);
    private SimpleBigDecimalScore perfectMinimumScore = null;

    @Override
    public SimpleBigDecimalScore getPerfectMaximumScore() {
        return perfectMaximumScore;
    }

    public void setPerfectMaximumScore(SimpleBigDecimalScore perfectMaximumScore) {
        this.perfectMaximumScore = perfectMaximumScore;
    }

    @Override
    public SimpleBigDecimalScore getPerfectMinimumScore() {
        return perfectMinimumScore;
    }

    public void setPerfectMinimumScore(SimpleBigDecimalScore perfectMinimumScore) {
        this.perfectMinimumScore = perfectMinimumScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Class<SimpleBigDecimalScore> getScoreClass() {
        return SimpleBigDecimalScore.class;
    }

    public Score parseScore(String scoreString) {
        return SimpleBigDecimalScore.parseScore(scoreString);
    }

    public double calculateTimeGradient(SimpleBigDecimalScore startScore, SimpleBigDecimalScore endScore, SimpleBigDecimalScore score) {
        if (score.getScore().compareTo(endScore.getScore()) >= 0) {
            return 1.0;
        } else if (startScore.getScore().compareTo(score.getScore()) >= 0) {
            return 0.0;
        }
        BigDecimal scoreTotal = endScore.getScore().subtract(startScore.getScore());
        BigDecimal scoreDelta = score.getScore().subtract(startScore.getScore());
        return scoreDelta.divide(scoreTotal).doubleValue();
    }

    public ScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new SimpleBigDecimalScoreHolder(constraintMatchEnabled);
    }

}
