/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.simpledouble;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * WARNING: NOT RECOMMENDED TO USE DUE TO ROUNDING ERRORS THAT CAUSE SCORE CORRUPTION.
 * Use {@link SimpleDoubleScoreHolder} instead.
 * @see SimpleDoubleScore
 */
public class SimpleDoubleScoreHolder extends AbstractScoreHolder {

    protected double score;

    public SimpleDoubleScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    public double getScore() {
        return score;
    }

    @Deprecated
    public void setScore(double score) {
        this.score = score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addConstraintMatch(RuleContext kcontext, final double weight) {
        score += weight;
        registerDoubleConstraintMatch(kcontext, 0, weight, new DoubleConstraintUndoListener() {
            public void undo() {
                score -= weight;
            }
        });
    }

    public Score extractScore() {
        return SimpleDoubleScore.valueOf(score);
    }

}
