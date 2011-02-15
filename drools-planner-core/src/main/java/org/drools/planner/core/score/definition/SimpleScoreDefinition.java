/**
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

package org.drools.planner.core.score.definition;

import org.drools.planner.core.score.SimpleScore;
import org.drools.planner.core.score.DefaultSimpleScore;
import org.drools.planner.core.score.Score;

/**
 * @author Geoffrey De Smet
 */
public class SimpleScoreDefinition extends AbstractScoreDefinition<SimpleScore> {

    private SimpleScore perfectMaximumScore = new DefaultSimpleScore(0);
    private SimpleScore perfectMinimumScore = new DefaultSimpleScore(Integer.MIN_VALUE);

    public void setPerfectMaximumScore(SimpleScore perfectMaximumScore) {
        this.perfectMaximumScore = perfectMaximumScore;
    }

    public void setPerfectMinimumScore(SimpleScore perfectMinimumScore) {
        this.perfectMinimumScore = perfectMinimumScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public SimpleScore getPerfectMaximumScore() {
        return perfectMaximumScore;
    }

    public SimpleScore getPerfectMinimumScore() {
        return perfectMinimumScore;
    }

    public Score parseScore(String scoreString) {
        return DefaultSimpleScore.parseScore(scoreString);
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

    public Double translateScoreToGraphValue(SimpleScore score) {
        return Double.valueOf(score.getScore());
    }

}
