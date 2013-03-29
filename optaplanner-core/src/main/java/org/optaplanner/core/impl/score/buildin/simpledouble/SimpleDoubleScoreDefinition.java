/*
 * Copyright 2011 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.simpledouble;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScoreHolder;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.api.score.holder.ScoreHolder;

public class SimpleDoubleScoreDefinition extends AbstractScoreDefinition<SimpleDoubleScore> {

    private SimpleDoubleScore perfectMaximumScore = SimpleDoubleScore.valueOf(0.0);
    private SimpleDoubleScore perfectMinimumScore = SimpleDoubleScore.valueOf(-Double.MAX_VALUE);

    @Override
    public SimpleDoubleScore getPerfectMaximumScore() {
        return perfectMaximumScore;
    }

    public void setPerfectMaximumScore(SimpleDoubleScore perfectMaximumScore) {
        this.perfectMaximumScore = perfectMaximumScore;
    }

    @Override
    public SimpleDoubleScore getPerfectMinimumScore() {
        return perfectMinimumScore;
    }

    public void setPerfectMinimumScore(SimpleDoubleScore perfectMinimumScore) {
        this.perfectMinimumScore = perfectMinimumScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Class<SimpleDoubleScore> getScoreClass() {
        return SimpleDoubleScore.class;
    }

    public Score parseScore(String scoreString) {
        return SimpleDoubleScore.parseScore(scoreString);
    }

    public double calculateTimeGradient(SimpleDoubleScore startScore, SimpleDoubleScore endScore, SimpleDoubleScore score) {
        if (score.getScore() >= endScore.getScore()) {
            return 1.0;
        } else if (startScore.getScore() >= score.getScore()) {
            return 0.0;
        }
        double scoreTotal = endScore.getScore() - startScore.getScore();
        double scoreDelta = score.getScore() - startScore.getScore();
        return scoreDelta / scoreTotal;
    }

    public ScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new SimpleDoubleScoreHolder(constraintMatchEnabled);
    }

}
