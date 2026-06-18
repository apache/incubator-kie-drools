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

import java.util.function.IntConsumer;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardMediumSoftScoreContext extends ScoreContext<HardMediumSoftScore> {

    private final IntConsumer softScoreUpdater;
    private final IntConsumer mediumScoreUpdater;
    private final IntConsumer hardScoreUpdater;

    public HardMediumSoftScoreContext(AbstractScoreInliner<HardMediumSoftScore> parent, Constraint constraint,
            HardMediumSoftScore constraintWeight, IntConsumer hardScoreUpdater, IntConsumer mediumScoreUpdater,
            IntConsumer softScoreUpdater) {
        super(parent, constraint, constraintWeight);
        this.softScoreUpdater = softScoreUpdater;
        this.mediumScoreUpdater = mediumScoreUpdater;
        this.hardScoreUpdater = hardScoreUpdater;
    }

    public UndoScoreImpacter changeSoftScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int softImpact = constraintWeight.softScore() * matchWeight;
        softScoreUpdater.accept(softImpact);
        UndoScoreImpacter undoScoreImpact = () -> softScoreUpdater.accept(-softImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftScore.ofSoft(softImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeMediumScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int mediumImpact = constraintWeight.mediumScore() * matchWeight;
        mediumScoreUpdater.accept(mediumImpact);
        UndoScoreImpacter undoScoreImpact = () -> mediumScoreUpdater.accept(-mediumImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftScore.ofMedium(mediumImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeHardScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int hardImpact = constraintWeight.hardScore() * matchWeight;
        hardScoreUpdater.accept(hardImpact);
        UndoScoreImpacter undoScoreImpact = () -> hardScoreUpdater.accept(-hardImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftScore.ofHard(hardImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int hardImpact = constraintWeight.hardScore() * matchWeight;
        int mediumImpact = constraintWeight.mediumScore() * matchWeight;
        int softImpact = constraintWeight.softScore() * matchWeight;
        hardScoreUpdater.accept(hardImpact);
        mediumScoreUpdater.accept(mediumImpact);
        softScoreUpdater.accept(softImpact);
        UndoScoreImpacter undoScoreImpact = () -> {
            hardScoreUpdater.accept(-hardImpact);
            mediumScoreUpdater.accept(-mediumImpact);
            softScoreUpdater.accept(-softImpact);
        };
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftScore.of(hardImpact, mediumImpact, softImpact),
                justificationsSupplier);
    }

}
