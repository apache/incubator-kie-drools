/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.buildin.bendable;

import java.util.Arrays;
import java.util.function.Consumer;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;

public class BendableScoreInliner extends ScoreInliner<BendableScore> {

    private int[] hardScores;
    private int[] softScores;

    public BendableScoreInliner(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled);
        hardScores = new int[hardLevelsSize];
        softScores = new int[softLevelsSize];
    }

    @Override
    public IntWeightedScoreImpacter buildWeightedScoreImpacter(BendableScore constraintWeight) {
        if (constraintWeight.equals(BendableScore.zero(hardScores.length, softScores.length))) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight + ") cannot be zero,"
                    + " this constraint should have been culled during node creation.");
        }
        Integer singleLevel = null;
        for (int i = 0; i < constraintWeight.getLevelsSize(); i++) {
            if (constraintWeight.getHardOrSoftScore(i) != 0L) {
                if (singleLevel != null) {
                    singleLevel = null;
                    break;
                }
                singleLevel = i;
            }
        }
        if (singleLevel != null) {
            int levelWeight = constraintWeight.getHardOrSoftScore(singleLevel);
            if (singleLevel < constraintWeight.getHardLevelsSize()) {
                int level = singleLevel;
                return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                    int hardImpact = levelWeight * matchWeight;
                    this.hardScores[level] += hardImpact;
                    if (constraintMatchEnabled) {
                        matchScoreConsumer.accept(BendableScore.ofHard(hardScores.length, softScores.length, level, hardImpact));
                    }
                    return () -> this.hardScores[level] -= hardImpact;
                };
            } else {
                int level = singleLevel - constraintWeight.getHardLevelsSize();
                return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                    int softImpact = levelWeight * matchWeight;
                    this.softScores[level] += softImpact;
                    if (constraintMatchEnabled) {
                        matchScoreConsumer.accept(BendableScore.ofSoft(hardScores.length, softScores.length, level, softImpact));
                    }
                    return () -> {
                        this.softScores[level] -= softImpact;
                    };
                };
            }
        } else {
            return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                int[] hardImpacts = new int[hardScores.length];
                int[] softImpacts = new int[softScores.length];
                for (int i = 0; i < hardImpacts.length; i++) {
                    hardImpacts[i] = constraintWeight.getHardScore(i) * matchWeight;
                    this.hardScores[i] += hardImpacts[i];
                }
                for (int i = 0; i < softImpacts.length; i++) {
                    softImpacts[i] = constraintWeight.getSoftScore(i) * matchWeight;
                    this.softScores[i] += softImpacts[i];
                }
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(BendableScore.of(hardImpacts, softImpacts));
                }
                return () -> {
                    for (int i = 0; i < hardImpacts.length; i++) {
                        this.hardScores[i] -= hardImpacts[i];
                    }
                    for (int i = 0; i < softImpacts.length; i++) {
                        this.softScores[i] -= softImpacts[i];
                    }
                };
            };
        }
    }

    @Override
    public BendableScore extractScore(int initScore) {
        return BendableScore.ofUninitialized(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

    @Override
    public String toString() {
        return BendableScore.class.getSimpleName() + " inliner";
    }

}
