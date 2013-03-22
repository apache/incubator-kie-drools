/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.simple;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScoreHolder;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.api.score.holder.ScoreHolder;

public class SimpleScoreDefinition extends AbstractScoreDefinition<SimpleScore> {

    private SimpleScore perfectMaximumScore = SimpleScore.valueOf(0);
    private SimpleScore perfectMinimumScore = SimpleScore.valueOf(Integer.MIN_VALUE);

    @Override
    public SimpleScore getPerfectMaximumScore() {
        return perfectMaximumScore;
    }

    public void setPerfectMaximumScore(SimpleScore perfectMaximumScore) {
        this.perfectMaximumScore = perfectMaximumScore;
    }

    @Override
    public SimpleScore getPerfectMinimumScore() {
        return perfectMinimumScore;
    }

    public void setPerfectMinimumScore(SimpleScore perfectMinimumScore) {
        this.perfectMinimumScore = perfectMinimumScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Class<SimpleScore> getScoreClass() {
        return SimpleScore.class;
    }

    public Score parseScore(String scoreString) {
        return SimpleScore.parseScore(scoreString);
    }

    public double calculateTimeGradient(SimpleScore startScore, SimpleScore endScore, SimpleScore score) {
        if (score.getScore() >= endScore.getScore()) {
            return 1.0;
        } else if (startScore.getScore() >= score.getScore()) {
            return 0.0;
        }
        int scoreTotal = endScore.getScore() - startScore.getScore();
        int scoreDelta = score.getScore() - startScore.getScore();
        return ((double) scoreDelta) / ((double) scoreTotal);
    }

    public ScoreHolder buildScoreHolder() {
        return new SimpleScoreHolder();
    }

}
