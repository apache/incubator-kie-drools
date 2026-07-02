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

import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardSoftBigDecimalScoreContext extends ScoreContext<HardSoftBigDecimalScore> {

    private final Consumer<BigDecimal> softScoreUpdater;
    private final Consumer<BigDecimal> hardScoreUpdater;

    public HardSoftBigDecimalScoreContext(AbstractScoreInliner<HardSoftBigDecimalScore> parent, Constraint constraint,
            HardSoftBigDecimalScore constraintWeight, Consumer<BigDecimal> hardScoreUpdater,
            Consumer<BigDecimal> softScoreUpdater) {
        super(parent, constraint, constraintWeight);
        this.softScoreUpdater = softScoreUpdater;
        this.hardScoreUpdater = hardScoreUpdater;
    }

    public UndoScoreImpacter changeSoftScoreBy(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        BigDecimal softImpact = constraintWeight.softScore().multiply(matchWeight);
        softScoreUpdater.accept(softImpact);
        UndoScoreImpacter undoScoreImpact = () -> softScoreUpdater.accept(softImpact.negate());
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardSoftBigDecimalScore.ofSoft(softImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeHardScoreBy(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        BigDecimal hardImpact = constraintWeight.hardScore().multiply(matchWeight);
        hardScoreUpdater.accept(hardImpact);
        UndoScoreImpacter undoScoreImpact = () -> hardScoreUpdater.accept(hardImpact.negate());
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardSoftBigDecimalScore.ofHard(hardImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeScoreBy(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        BigDecimal hardImpact = constraintWeight.hardScore().multiply(matchWeight);
        BigDecimal softImpact = constraintWeight.softScore().multiply(matchWeight);
        hardScoreUpdater.accept(hardImpact);
        softScoreUpdater.accept(softImpact);
        UndoScoreImpacter undoScoreImpact = () -> {
            hardScoreUpdater.accept(hardImpact.negate());
            softScoreUpdater.accept(softImpact.negate());
        };
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardSoftBigDecimalScore.of(hardImpact, softImpact),
                justificationsSupplier);
    }

}
