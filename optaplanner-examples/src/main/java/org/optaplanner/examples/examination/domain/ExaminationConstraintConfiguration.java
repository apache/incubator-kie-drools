/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.examination.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

/**
 * Institutional weightings.
 * Allows the user to recalibrate score weights.
 * <p>
 * Each {@link Examination} has only 1 instance of this class.
 */
@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.examination.solver")
@XStreamAlias("ExaminationConstraintConfiguration")
public class ExaminationConstraintConfiguration extends AbstractPersistable {

    private int twoInARowPenalty;
    private int twoInADayPenalty;
    private int periodSpreadLength;
    private int periodSpreadPenalty;
    private int mixedDurationPenalty;
    private int frontLoadLargeTopicSize;
    private int frontLoadLastPeriodSize;
    private int frontLoadPenalty;

    @ConstraintWeight("twoExamsInARow")
    public HardSoftScore getTwoInARowPenaltyAsScore() {
        return HardSoftScore.ofSoft(twoInARowPenalty);
    }

    @ConstraintWeight("twoExamsInADay")
    public HardSoftScore getTwoInADayPenaltyAsScore() {
        return HardSoftScore.ofSoft(twoInADayPenalty);
    }

    @ConstraintWeight("periodSpread")
    public HardSoftScore getPeriodSpreadPenaltyAsScore() {
        return HardSoftScore.ofSoft(periodSpreadPenalty);
    }

    @ConstraintWeight("mixedDurations")
    public HardSoftScore getMixedDurationPenaltyAsScore() {
        return HardSoftScore.ofSoft(mixedDurationPenalty);
    }

    @ConstraintWeight("frontLoad")
    public HardSoftScore getFrontLoadPenaltyAsScore() {
        return HardSoftScore.ofSoft(frontLoadPenalty);
    }


    public int getTwoInARowPenalty() {
        return twoInARowPenalty;
    }

    public void setTwoInARowPenalty(int twoInARowPenalty) {
        this.twoInARowPenalty = twoInARowPenalty;
    }

    public int getTwoInADayPenalty() {
        return twoInADayPenalty;
    }

    public void setTwoInADayPenalty(int twoInADayPenalty) {
        this.twoInADayPenalty = twoInADayPenalty;
    }

    public int getPeriodSpreadLength() {
        return periodSpreadLength;
    }

    public void setPeriodSpreadLength(int periodSpreadLength) {
        this.periodSpreadLength = periodSpreadLength;
    }

    public int getPeriodSpreadPenalty() {
        return periodSpreadPenalty;
    }

    public void setPeriodSpreadPenalty(int periodSpreadPenalty) {
        this.periodSpreadPenalty = periodSpreadPenalty;
    }

    public int getMixedDurationPenalty() {
        return mixedDurationPenalty;
    }

    public void setMixedDurationPenalty(int mixedDurationPenalty) {
        this.mixedDurationPenalty = mixedDurationPenalty;
    }

    public int getFrontLoadLargeTopicSize() {
        return frontLoadLargeTopicSize;
    }

    public void setFrontLoadLargeTopicSize(int frontLoadLargeTopicSize) {
        this.frontLoadLargeTopicSize = frontLoadLargeTopicSize;
    }

    public int getFrontLoadLastPeriodSize() {
        return frontLoadLastPeriodSize;
    }

    public void setFrontLoadLastPeriodSize(int frontLoadLastPeriodSize) {
        this.frontLoadLastPeriodSize = frontLoadLastPeriodSize;
    }

    public int getFrontLoadPenalty() {
        return frontLoadPenalty;
    }

    public void setFrontLoadPenalty(int frontLoadPenalty) {
        this.frontLoadPenalty = frontLoadPenalty;
    }

}
