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

import org.optaplanner.constraint.streams.common.inliner.BendableLongScoreContext.IntLongConsumer;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.stream.Constraint;

public final class BendableLongScoreInliner extends AbstractScoreInliner<BendableLongScore> {

    private final long[] hardScores;
    private final long[] softScores;

    BendableLongScoreInliner(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled);
        hardScores = new long[hardLevelsSize];
        softScores = new long[softLevelsSize];
    }

    @Override
    public WeightedScoreImpacter<BendableLongScore, BendableLongScoreContext> buildWeightedScoreImpacter(Constraint constraint,
            BendableLongScore constraintWeight) {
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
        IntLongConsumer hardScoreUpdater = (scoreLevel, impact) -> this.hardScores[scoreLevel] += impact;
        IntLongConsumer softScoreUpdater = (scoreLevel, impact) -> this.softScores[scoreLevel] += impact;
        if (singleLevel != null) {
            boolean isHardScore = singleLevel < constraintWeight.hardLevelsSize();
            int level = isHardScore ? singleLevel : singleLevel - constraintWeight.hardLevelsSize();
            BendableLongScoreContext context = new BendableLongScoreContext(this, constraint, constraintWeight,
                    hardScores.length, softScores.length, level, constraintWeight.hardOrSoftScore(singleLevel),
                    hardScoreUpdater, softScoreUpdater);
            if (isHardScore) {
                return WeightedScoreImpacter.of(context, (BendableLongScoreContext ctx, long impact,
                        JustificationsSupplier justificationSupplier) -> ctx.changeHardScoreBy(impact, justificationSupplier));
            } else {
                return WeightedScoreImpacter.of(context, (BendableLongScoreContext ctx, long impact,
                        JustificationsSupplier justificationSupplier) -> ctx.changeSoftScoreBy(impact, justificationSupplier));
            }
        } else {
            BendableLongScoreContext context = new BendableLongScoreContext(this, constraint, constraintWeight,
                    hardScores.length, softScores.length, hardScoreUpdater, softScoreUpdater);
            return WeightedScoreImpacter.of(context, (BendableLongScoreContext ctx, long impact,
                    JustificationsSupplier justificationSupplier) -> ctx.changeScoreBy(impact, justificationSupplier));
        }
    }

    @Override
    public BendableLongScore extractScore(int initScore) {
        return BendableLongScore.ofUninitialized(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

    @Override
    public String toString() {
        return BendableLongScore.class.getSimpleName() + " inliner";
    }

}
