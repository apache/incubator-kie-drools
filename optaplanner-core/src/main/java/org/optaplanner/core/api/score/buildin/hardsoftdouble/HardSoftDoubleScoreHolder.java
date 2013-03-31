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

package org.optaplanner.core.api.score.buildin.hardsoftdouble;

import org.kie.internal.event.rule.ActivationUnMatchListener;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.RuleContext;
import org.kie.api.runtime.rule.Session;

/**
 * @see HardSoftDoubleScore
 */
public class HardSoftDoubleScoreHolder extends AbstractScoreHolder {

    protected double hardScore;
    protected double softScore;

    public HardSoftDoubleScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    public double getHardScore() {
        return hardScore;
    }

    public void setHardScore(double hardScore) {
        this.hardScore = hardScore;
    }

    public double getSoftScore() {
        return softScore;
    }

    public void setSoftScore(double softScore) {
        this.softScore = softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addHardConstraintMatch(RuleContext kcontext, final double weight) {
        hardScore += weight;
        registerDoubleConstraintMatch(kcontext, 0, weight, new Runnable() {
            public void run() {
                hardScore -= weight;
            }
        });
    }

    public void addSoftConstraintMatch(RuleContext kcontext, final double weight) {
        softScore += weight;
        registerDoubleConstraintMatch(kcontext, 1, weight, new Runnable() {
            public void run() {
                softScore -= weight;
            }
        });
    }

    public Score extractScore() {
        return HardSoftDoubleScore.valueOf(hardScore, softScore);
    }

}
