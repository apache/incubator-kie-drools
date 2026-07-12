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

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class SimpleLongScoreInliner extends AbstractScoreInliner<SimpleLongScore> {

    private long score;

    SimpleLongScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter<SimpleLongScore, SimpleLongScoreContext> buildWeightedScoreImpacter(Constraint constraint,
            SimpleLongScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        SimpleLongScoreContext context = new SimpleLongScoreContext(this, constraint, constraintWeight,
                impact -> this.score += impact);
        return WeightedScoreImpacter.of(context,
                (SimpleLongScoreContext ctx, long matchWeight, JustificationsSupplier justificationsSupplier) -> ctx
                        .changeScoreBy(matchWeight, justificationsSupplier));
    }

    @Override
    public SimpleLongScore extractScore(int initScore) {
        return SimpleLongScore.ofUninitialized(initScore, score);
    }

    @Override
    public String toString() {
        return SimpleLongScore.class.getSimpleName() + " inliner";
    }

}
