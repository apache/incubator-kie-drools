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

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardMediumSoftLongScoreInliner extends AbstractScoreInliner<HardMediumSoftLongScore> {

    private long hardScore;
    private long mediumScore;
    private long softScore;

    HardMediumSoftLongScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter<HardMediumSoftLongScore, HardMediumSoftLongScoreContext> buildWeightedScoreImpacter(
            Constraint constraint, HardMediumSoftLongScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        long hardConstraintWeight = constraintWeight.hardScore();
        long mediumConstraintWeight = constraintWeight.mediumScore();
        long softConstraintWeight = constraintWeight.softScore();
        HardMediumSoftLongScoreContext context = new HardMediumSoftLongScoreContext(this, constraint, constraintWeight,
                impact -> this.hardScore += impact, impact -> this.mediumScore += impact,
                impact -> this.softScore += impact);
        if (mediumConstraintWeight == 0L && softConstraintWeight == 0L) {
            return WeightedScoreImpacter.of(context,
                    (HardMediumSoftLongScoreContext ctx, long matchWeight, JustificationsSupplier justificationsSupplier) -> ctx
                            .changeHardScoreBy(matchWeight, justificationsSupplier));
        } else if (hardConstraintWeight == 0L && softConstraintWeight == 0L) {
            return WeightedScoreImpacter.of(context,
                    (HardMediumSoftLongScoreContext ctx, long matchWeight, JustificationsSupplier justificationsSupplier) -> ctx
                            .changeMediumScoreBy(matchWeight, justificationsSupplier));
        } else if (hardConstraintWeight == 0L && mediumConstraintWeight == 0L) {
            return WeightedScoreImpacter.of(context,
                    (HardMediumSoftLongScoreContext ctx, long matchWeight, JustificationsSupplier justificationsSupplier) -> ctx
                            .changeSoftScoreBy(matchWeight, justificationsSupplier));
        } else {
            return WeightedScoreImpacter.of(context,
                    (HardMediumSoftLongScoreContext ctx, long matchWeight, JustificationsSupplier justificationsSupplier) -> ctx
                            .changeScoreBy(matchWeight, justificationsSupplier));
        }
    }

    @Override
    public HardMediumSoftLongScore extractScore(int initScore) {
        return HardMediumSoftLongScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

    @Override
    public String toString() {
        return HardMediumSoftLongScore.class.getSimpleName() + " inliner";
    }

}
