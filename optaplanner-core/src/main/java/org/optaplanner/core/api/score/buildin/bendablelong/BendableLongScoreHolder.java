/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.bendablelong;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

import java.util.Arrays;

/**
 * @see BendableLongScore
 */
public class BendableLongScoreHolder extends AbstractScoreHolder {

    private long[] hardScores;
    private long[] softScores;

    public BendableLongScoreHolder(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled);
        hardScores = new long[hardLevelsSize];
        softScores = new long[softLevelsSize];
    }

    public int getHardLevelsSize() {
        return hardScores.length;
    }

    public long getHardScore(int hardLevel) {
        return hardScores[hardLevel];
    }

    @Deprecated
    public void setHardScore(int hardLevel, long hardScore) {
        hardScores[hardLevel] = hardScore;
    }

    public int getSoftLevelsSize() {
        return softScores.length;
    }

    public long getSoftScore(int softLevel) {
        return softScores[softLevel];
    }

    @Deprecated
    public void setSoftScore(int softLevel, long softScore) {
        softScores[softLevel] = softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addHardConstraintMatch(RuleContext kcontext, final int hardLevel, final long weight) {
        hardScores[hardLevel] += weight;
        registerLongConstraintMatch(kcontext, hardLevel, weight, new LongConstraintUndoListener() {
            public void undo() {
                hardScores[hardLevel] -= weight;
            }
        });
    }

    public void addSoftConstraintMatch(RuleContext kcontext, final int softLevel, final long weight) {
        softScores[softLevel] += weight;
        registerLongConstraintMatch(kcontext, getHardLevelsSize() + softLevel, weight, new LongConstraintUndoListener() {
            public void undo() {
                softScores[softLevel] -= weight;
            }
        });
    }

    public Score extractScore() {
        return new BendableLongScore(Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

}
