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

package org.optaplanner.core.impl.score.buildin.simplelong;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScoreHolder;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.api.score.holder.ScoreHolder;

public class SimpleLongScoreDefinition extends AbstractScoreDefinition<SimpleLongScore> {

    private SimpleLongScore perfectMaximumScore = SimpleLongScore.valueOf(0L);
    private SimpleLongScore perfectMinimumScore = SimpleLongScore.valueOf(Long.MIN_VALUE);

    @Override
    public SimpleLongScore getPerfectMaximumScore() {
        return perfectMaximumScore;
    }

    public void setPerfectMaximumScore(SimpleLongScore perfectMaximumScore) {
        this.perfectMaximumScore = perfectMaximumScore;
    }

    @Override
    public SimpleLongScore getPerfectMinimumScore() {
        return perfectMinimumScore;
    }

    public void setPerfectMinimumScore(SimpleLongScore perfectMinimumScore) {
        this.perfectMinimumScore = perfectMinimumScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Class<SimpleLongScore> getScoreClass() {
        return SimpleLongScore.class;
    }

    public Score parseScore(String scoreString) {
        return SimpleLongScore.parseScore(scoreString);
    }

    public double calculateTimeGradient(SimpleLongScore startScore, SimpleLongScore endScore, SimpleLongScore score) {
        if (score.getScore() >= endScore.getScore()) {
            return 1.0;
        } else if (startScore.getScore() >= score.getScore()) {
            return 0.0;
        }
        long scoreTotal = endScore.getScore() - startScore.getScore();
        long scoreDelta = score.getScore() - startScore.getScore();
        return ((double) scoreDelta) / ((double) scoreTotal);
    }

    public ScoreHolder buildScoreHolder() {
        return new SimpleLongScoreHolder();
    }

}
