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
import java.util.function.Consumer;

import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class SimpleBigDecimalScoreContext extends ScoreContext<SimpleBigDecimalScore> {

    private final Consumer<BigDecimal> scoreUpdater;

    public SimpleBigDecimalScoreContext(AbstractScoreInliner<SimpleBigDecimalScore> parent, Constraint constraint,
            SimpleBigDecimalScore constraintWeight, Consumer<BigDecimal> scoreUpdater) {
        super(parent, constraint, constraintWeight);
        this.scoreUpdater = scoreUpdater;
    }

    public UndoScoreImpacter changeScoreBy(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        BigDecimal impact = constraintWeight.score().multiply(matchWeight);
        scoreUpdater.accept(impact);
        UndoScoreImpacter undoScoreImpact = () -> scoreUpdater.accept(impact.negate());
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, SimpleBigDecimalScore.of(impact), justificationsSupplier);
    }

}
