/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.constraint.streams.common.inliner;

import java.util.Arrays;

import org.optaplanner.constraint.streams.common.inliner.BendableScoreContext.IntBiConsumer;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class BendableScoreInliner extends AbstractScoreInliner<BendableScore> {

    private final int[] hardScores;
    private final int[] softScores;

    BendableScoreInliner(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled);
        hardScores = new int[hardLevelsSize];
        softScores = new int[softLevelsSize];
    }

    @Override
    public WeightedScoreImpacter<BendableScore, BendableScoreContext> buildWeightedScoreImpacter(Constraint constraint,
            BendableScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        Integer singleLevel = null;
        for (int i = 0; i < constraintWeight.levelsSize(); i++) {
            if (constraintWeight.hardOrSoftScore(i) != 0L) {
                if (singleLevel != null) {
                    singleLevel = null;
                    break;
                }
                singleLevel = i;
            }
        }
        IntBiConsumer hardScoreUpdater = (scoreLevel, impact) -> this.hardScores[scoreLevel] += impact;
        IntBiConsumer softScoreUpdater = (scoreLevel, impact) -> this.softScores[scoreLevel] += impact;
        if (singleLevel != null) {
            boolean isHardScore = singleLevel < constraintWeight.hardLevelsSize();
            int level = isHardScore ? singleLevel : singleLevel - constraintWeight.hardLevelsSize();
            BendableScoreContext context = new BendableScoreContext(this, constraint, constraintWeight,
                    hardScores.length, softScores.length, level, constraintWeight.hardOrSoftScore(singleLevel),
                    hardScoreUpdater, softScoreUpdater);
            if (isHardScore) {
                return WeightedScoreImpacter.of(context, BendableScoreContext::changeHardScoreBy);
            } else {
                return WeightedScoreImpacter.of(context, BendableScoreContext::changeSoftScoreBy);
            }
        } else {
            BendableScoreContext context = new BendableScoreContext(this, constraint, constraintWeight,
                    hardScores.length, softScores.length, hardScoreUpdater, softScoreUpdater);
            return WeightedScoreImpacter.of(context, BendableScoreContext::changeScoreBy);
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
