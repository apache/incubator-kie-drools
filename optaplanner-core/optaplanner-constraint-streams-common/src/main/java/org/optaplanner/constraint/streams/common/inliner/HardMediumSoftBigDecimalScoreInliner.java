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

import java.math.BigDecimal;

import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardMediumSoftBigDecimalScoreInliner extends AbstractScoreInliner<HardMediumSoftBigDecimalScore> {

    private BigDecimal hardScore = BigDecimal.ZERO;
    private BigDecimal mediumScore = BigDecimal.ZERO;
    private BigDecimal softScore = BigDecimal.ZERO;

    HardMediumSoftBigDecimalScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter<HardMediumSoftBigDecimalScore, HardMediumSoftBigDecimalScoreContext>
            buildWeightedScoreImpacter(Constraint constraint,
                    HardMediumSoftBigDecimalScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        BigDecimal hardConstraintWeight = constraintWeight.hardScore();
        BigDecimal mediumConstraintWeight = constraintWeight.mediumScore();
        BigDecimal softConstraintWeight = constraintWeight.softScore();
        HardMediumSoftBigDecimalScoreContext context =
                new HardMediumSoftBigDecimalScoreContext(this, constraint, constraintWeight,
                        impact -> this.hardScore = this.hardScore.add(impact),
                        impact -> this.mediumScore = this.mediumScore.add(impact),
                        impact -> this.softScore = this.softScore.add(impact));
        if (mediumConstraintWeight.equals(BigDecimal.ZERO) && softConstraintWeight.equals(BigDecimal.ZERO)) {
            return WeightedScoreImpacter.of(context, HardMediumSoftBigDecimalScoreContext::changeHardScoreBy);
        } else if (hardConstraintWeight.equals(BigDecimal.ZERO) && softConstraintWeight.equals(BigDecimal.ZERO)) {
            return WeightedScoreImpacter.of(context, HardMediumSoftBigDecimalScoreContext::changeMediumScoreBy);
        } else if (hardConstraintWeight.equals(BigDecimal.ZERO) && mediumConstraintWeight.equals(BigDecimal.ZERO)) {
            return WeightedScoreImpacter.of(context, HardMediumSoftBigDecimalScoreContext::changeSoftScoreBy);
        } else {
            return WeightedScoreImpacter.of(context, HardMediumSoftBigDecimalScoreContext::changeScoreBy);
        }
    }

    @Override
    public HardMediumSoftBigDecimalScore extractScore(int initScore) {
        return HardMediumSoftBigDecimalScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

    @Override
    public String toString() {
        return HardMediumSoftBigDecimalScore.class.getSimpleName() + " inliner";
    }

}
