/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.api.score.buildin.hardmediumsoft;

import org.kie.internal.event.rule.ActivationUnMatchListener;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.RuleContext;
import org.kie.api.runtime.rule.Session;

/**
 * @see HardMediumSoftScore
 */
public class HardMediumSoftScoreHolder extends AbstractScoreHolder {

    protected int hardScore;
    protected int mediumScore;
    protected int softScore;

    public HardMediumSoftScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    public int getHardScore() {
        return hardScore;
    }

    @Deprecated
    public void setHardScore(int hardScore) {
        this.hardScore = hardScore;
    }

    public int getMediumScore() {
        return mediumScore;
    }

    @Deprecated
    public void setMediumScore(int mediumScore) {
        this.mediumScore = mediumScore;
    }

    public int getSoftScore() {
        return softScore;
    }

    @Deprecated
    public void setSoftScore(int softScore) {
        this.softScore = softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addHardConstraintMatch(RuleContext kcontext, final int weight) {
        hardScore += weight;
        registerIntConstraintMatch(kcontext, 0, weight, new Runnable() {
            public void run() {
                hardScore -= weight;
            }
        });
    }

    public void addMediumConstraintMatch(RuleContext kcontext, final int weight) {
        mediumScore += weight;
        registerIntConstraintMatch(kcontext, 1, weight, new Runnable() {
            public void run() {
                mediumScore -= weight;
            }
        });
    }

    public void addSoftConstraintMatch(RuleContext kcontext, final int weight) {
        softScore += weight;
        registerIntConstraintMatch(kcontext, 2, weight, new Runnable() {
            public void run() {
                softScore -= weight;
            }
        });
    }

    public Score extractScore() {
        return HardMediumSoftScore.valueOf(hardScore, mediumScore, softScore);
    }

}
