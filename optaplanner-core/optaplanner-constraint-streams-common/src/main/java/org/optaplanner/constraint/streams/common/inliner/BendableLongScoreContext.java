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

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class BendableLongScoreContext extends ScoreContext<BendableLongScore> {

    private final int hardScoreLevelCount;
    private final int softScoreLevelCount;
    private final int scoreLevel;
    private final long scoreLevelWeight;
    private final IntLongConsumer softScoreLevelUpdater;
    private final IntLongConsumer hardScoreLevelUpdater;

    public BendableLongScoreContext(AbstractScoreInliner<BendableLongScore> parent, Constraint constraint,
            BendableLongScore constraintWeight, int hardScoreLevelCount, int softScoreLevelCount, int scoreLevel,
            long scoreLevelWeight, IntLongConsumer hardScoreLevelUpdater, IntLongConsumer softScoreLevelUpdater) {
        super(parent, constraint, constraintWeight);
        this.hardScoreLevelCount = hardScoreLevelCount;
        this.softScoreLevelCount = softScoreLevelCount;
        this.scoreLevel = scoreLevel;
        this.scoreLevelWeight = scoreLevelWeight;
        this.softScoreLevelUpdater = softScoreLevelUpdater;
        this.hardScoreLevelUpdater = hardScoreLevelUpdater;
    }

    public BendableLongScoreContext(AbstractScoreInliner<BendableLongScore> parent, Constraint constraint,
            BendableLongScore constraintWeight, int hardScoreLevelCount, int softScoreLevelCount,
            IntLongConsumer hardScoreLevelUpdater, IntLongConsumer softScoreLevelUpdater) {
        this(parent, constraint, constraintWeight, hardScoreLevelCount, softScoreLevelCount, -1, -1, hardScoreLevelUpdater,
                softScoreLevelUpdater);
    }

    public UndoScoreImpacter changeSoftScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long softImpact = scoreLevelWeight * matchWeight;
        softScoreLevelUpdater.accept(scoreLevel, softImpact);
        UndoScoreImpacter undoScoreImpact = () -> softScoreLevelUpdater.accept(scoreLevel, -softImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact,
                BendableLongScore.ofSoft(hardScoreLevelCount, softScoreLevelCount, scoreLevel, softImpact),
                justificationsSupplier);
    }

    public UndoScoreImpacter changeHardScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long hardImpact = scoreLevelWeight * matchWeight;
        hardScoreLevelUpdater.accept(scoreLevel, hardImpact);
        UndoScoreImpacter undoScoreImpact = () -> hardScoreLevelUpdater.accept(scoreLevel, -hardImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact,
                BendableLongScore.ofHard(hardScoreLevelCount, softScoreLevelCount, scoreLevel, hardImpact),
                justificationsSupplier);
    }

    public UndoScoreImpacter changeScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long[] hardImpacts = new long[hardScoreLevelCount];
        long[] softImpacts = new long[softScoreLevelCount];
        for (int hardScoreLevel = 0; hardScoreLevel < hardScoreLevelCount; hardScoreLevel++) {
            long hardImpact = constraintWeight.hardScore(hardScoreLevel) * matchWeight;
            hardImpacts[hardScoreLevel] = hardImpact;
            hardScoreLevelUpdater.accept(hardScoreLevel, hardImpact);
        }
        for (int softScoreLevel = 0; softScoreLevel < softScoreLevelCount; softScoreLevel++) {
            long softImpact = constraintWeight.softScore(softScoreLevel) * matchWeight;
            softImpacts[softScoreLevel] = softImpact;
            softScoreLevelUpdater.accept(softScoreLevel, softImpact);
        }
        UndoScoreImpacter undoScoreImpact = () -> {
            for (int hardScoreLevel = 0; hardScoreLevel < hardScoreLevelCount; hardScoreLevel++) {
                hardScoreLevelUpdater.accept(hardScoreLevel, -hardImpacts[hardScoreLevel]);
            }
            for (int softScoreLevel = 0; softScoreLevel < softScoreLevelCount; softScoreLevel++) {
                softScoreLevelUpdater.accept(softScoreLevel, -softImpacts[softScoreLevel]);
            }
        };
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, BendableLongScore.of(hardImpacts, softImpacts),
                justificationsSupplier);
    }

    public interface IntLongConsumer {

        void accept(int value1, long value2);

    }

}
