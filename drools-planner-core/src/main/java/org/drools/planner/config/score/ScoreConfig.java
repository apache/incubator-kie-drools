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

package org.drools.planner.config.score;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.core.score.DefaultSimpleScore;
import org.drools.planner.core.score.DefaultHardAndSoftScore;

/**
 * @author Geoffrey De Smet
 */
@XStreamAlias("score")
public class ScoreConfig {

    private Comparable score = null;
    private ScoreType scoreType = null;
    private String scoreString;

    public Comparable getScore() {
        return score;
    }

    public void setScore(Comparable score) {
        this.score = score;
    }

    public ScoreType getScoreType() {
        return scoreType;
    }

    public void setScoreType(ScoreType scoreType) {
        this.scoreType = scoreType;
    }

    public String getScoreString() {
        return scoreString;
    }

    public void setScoreString(String scoreString) {
        this.scoreString = scoreString;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Comparable buildScore() {
        if (score != null) {
            return score;
        } else if (scoreType != null) {
            switch (scoreType) {
                case SIMPLE:
                    return DefaultSimpleScore.parseScore(scoreString);
                case HARD_AND_SOFT:
                    return DefaultHardAndSoftScore.parseScore(scoreString);
                default:
                    throw new IllegalStateException("The scoreType (" + scoreType + ") is not implemented");
            }
        } else {
            return null;
        }
    }

    public void inherit(ScoreConfig inheritedConfig) {
        if (score == null && scoreType == null && scoreString == null) {
            score = inheritedConfig.getScore();
            scoreType = inheritedConfig.getScoreType();
            scoreString = inheritedConfig.getScoreString();
        }
    }

    public static enum ScoreType {
        SIMPLE,
        HARD_AND_SOFT,
    }

}
