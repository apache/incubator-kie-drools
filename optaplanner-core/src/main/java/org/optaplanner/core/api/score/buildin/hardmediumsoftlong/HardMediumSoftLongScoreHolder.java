/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardmediumsoftlong;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see HardMediumSoftScore
 */
public class HardMediumSoftLongScoreHolder extends AbstractScoreHolder {

    protected long hardScore;
    protected long mediumScore;
    protected long softScore;

    public HardMediumSoftLongScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    public long getHardScore() {
        return hardScore;
    }

    @Deprecated
    public void setHardScore(long hardScore) {
        this.hardScore = hardScore;
    }

    public long getMediumScore() {
        return mediumScore;
    }

    @Deprecated
    public void setMediumScore(long mediumScore) {
        this.mediumScore = mediumScore;
    }

    public long getSoftScore() {
        return softScore;
    }

    @Deprecated
    public void setSoftScore(long softScore) {
        this.softScore = softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addHardConstraintMatch(RuleContext kcontext, final long weight) {
        hardScore += weight;
        registerLongConstraintMatch(kcontext, 0, weight, new LongConstraintUndoListener() {
            public void undo() {
                hardScore -= weight;
            }
        });
    }

    public void addMediumConstraintMatch(RuleContext kcontext, final long weight) {
        mediumScore += weight;
        registerLongConstraintMatch(kcontext, 1, weight, new LongConstraintUndoListener() {
            public void undo() {
                mediumScore -= weight;
            }
        });
    }

    public void addSoftConstraintMatch(RuleContext kcontext, final long weight) {
        softScore += weight;
        registerLongConstraintMatch(kcontext, 2, weight, new LongConstraintUndoListener() {
            public void undo() {
                softScore -= weight;
            }
        });
    }

    public Score extractScore() {
        return HardMediumSoftLongScore.valueOf(hardScore, mediumScore, softScore);
    }

}
