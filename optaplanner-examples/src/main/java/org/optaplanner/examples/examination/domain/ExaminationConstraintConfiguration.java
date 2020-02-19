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

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Institutional weightings.
 * Allows the user to recalibrate score weights.
 * <p>
 * Each {@link Examination} has only 1 instance of this class.
 */
@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.examination.solver")
@XStreamAlias("ExaminationConstraintConfiguration")
public class ExaminationConstraintConfiguration extends AbstractPersistable {
    // Hard constraints
    private int conflictingExamsInSamePeriodPenalty = 1;
    private int periodDurationTooShortPenalty = 1;
    private int roomCapacityTooSmallPenalty = 1;
    private int periodPenaltyExamCoincidencePenalty = 1;
    private int periodPenaltyExclusionPenalty = 1;
    private int periodPenaltyAfterPenalty = 1;
    private int roomPenaltyExclusivePenalty = 1;

    // Soft constraints
    private int twoInARowPenalty;
    private int twoInADayPenalty;
    private int periodSpreadLength;
    private int periodSpreadPenalty;
    private int mixedDurationPenalty;
    private int frontLoadLargeTopicSize;
    private int frontLoadLastPeriodSize;
    private int frontLoadPenalty;
    private int periodPenaltyPenalty = 1;
    private int roomPenaltyPenalty = 1;

    // ************************************************************************
    // Constraint weight methods
    // ************************************************************************

    // Hard constraints
    @ConstraintWeight("conflictingExamsInSamePeriod")
    public HardSoftScore getConflictingExamsInSamePeriodPenaltyAsScore() {
        return HardSoftScore.ofHard(conflictingExamsInSamePeriodPenalty);
    }

    @ConstraintWeight("periodDurationTooShort")
    public HardSoftScore getPeriodDurationTooShortPenaltyAsScore() {
        return HardSoftScore.ofHard(periodDurationTooShortPenalty);
    }

    @ConstraintWeight("roomCapacityTooSmall")
    public HardSoftScore getRoomCapacityTooSmallPenaltyAsScore() {
        return HardSoftScore.ofHard(roomCapacityTooSmallPenalty);
    }

    @ConstraintWeight("periodPenaltyExamCoincidence")
    public HardSoftScore getPeriodPenaltyExamCoincidencePenaltyAsScore() {
        return HardSoftScore.ofHard(periodPenaltyExamCoincidencePenalty);
    }

    @ConstraintWeight("periodPenaltyExclusion")
    public HardSoftScore getPeriodPenaltyExclusionPenaltyAsScore() {
        return HardSoftScore.ofHard(periodPenaltyExclusionPenalty);
    }

    @ConstraintWeight("periodPenaltyAfter")
    public HardSoftScore getPeriodPenaltyAfterPenaltyAsScore() {
        return HardSoftScore.ofHard(periodPenaltyAfterPenalty);
    }

    @ConstraintWeight("roomPenaltyExclusive")
    public HardSoftScore getRoomPenaltyExclusivePenaltyAsScore() {
        return HardSoftScore.ofHard(roomPenaltyExclusivePenalty);
    }

    // Soft constraints
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

    @ConstraintWeight("periodPenalty")
    public HardSoftScore getPeriodPenaltyPenaltyAsScore() {
        return HardSoftScore.ofSoft(periodPenaltyPenalty);
    }

    @ConstraintWeight("roomPenalty")
    public HardSoftScore getRoomPenaltyPenaltyAsScore() {
        return HardSoftScore.ofSoft(roomPenaltyPenalty);
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    // Hard constraint functions
    public int getConflictingExamsInSamePeriodPenalty() {
        return conflictingExamsInSamePeriodPenalty;
    }

    public void setConflictingExamsInSamePeriodPenalty(int conflictingExamsInSamePeriodPenalty) {
        this.conflictingExamsInSamePeriodPenalty = conflictingExamsInSamePeriodPenalty;
    }

    public int getPeriodDurationTooShortPenalty() {
        return periodDurationTooShortPenalty;
    }

    public void setPeriodDurationTooShortPenalty(int periodDurationTooShortPenalty) {
        this.periodDurationTooShortPenalty = periodDurationTooShortPenalty;
    }

    public int getRoomCapacityTooSmallPenalty() {
        return roomCapacityTooSmallPenalty;
    }

    public void setRoomCapacityTooSmallPenalty(int roomCapacityTooSmallPenalty) {
        this.roomCapacityTooSmallPenalty = roomCapacityTooSmallPenalty;
    }

    public int getPeriodPenaltyExamCoincidencePenalty() {
        return periodPenaltyExamCoincidencePenalty;
    }

    public void setPeriodPenaltyExamCoincidencePenalty(int periodPenaltyExamCoincidencePenalty) {
        this.periodPenaltyExamCoincidencePenalty = periodPenaltyExamCoincidencePenalty;
    }

    public int getPeriodPenaltyExclusionPenalty() {
        return periodPenaltyExclusionPenalty;
    }

    public void setPeriodPenaltyExclusionPenalty(int periodPenaltyExclusionPenalty) {
        this.periodPenaltyExclusionPenalty = periodPenaltyExclusionPenalty;
    }

    public int getPeriodPenaltyAfterPenalty() {
        return periodPenaltyAfterPenalty;
    }

    public void setPeriodPenaltyAfterPenalty(int periodPenaltyAfterPenalty) {
        this.periodPenaltyAfterPenalty = periodPenaltyAfterPenalty;
    }

    public int getRoomPenaltyExclusivePenalty() {
        return roomPenaltyExclusivePenalty;
    }

    public void setRoomPenaltyExclusivePenalty(int roomPenaltyExclusivePenalty) {
        this.roomPenaltyExclusivePenalty = roomPenaltyExclusivePenalty;
    }

    // Soft constraint functions
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

    public int getPeriodPenaltyPenalty() {
        return periodPenaltyPenalty;
    }

    public void setPeriodPenaltyPenalty(int periodPenaltyPenalty) {
        this.periodPenaltyPenalty = periodPenaltyPenalty;
    }

    public int getRoomPenaltyPenalty() {
        return roomPenaltyPenalty;
    }

    public void setRoomPenaltyPenalty(int roomPenaltyPenalty) {
        this.roomPenaltyPenalty = roomPenaltyPenalty;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public ExaminationConstraintConfiguration withConflictingExamsInSamePeriodPenalty(int conflictingExamsInSamePeriodPenalty) {
        this.setConflictingExamsInSamePeriodPenalty(conflictingExamsInSamePeriodPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withPeriodDurationTooShortPenalty(int periodDurationTooShortPenalty) {
        this.setPeriodDurationTooShortPenalty(periodDurationTooShortPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withRoomCapacityTooSmallPenalty(int roomCapacityTooSmallPenalty) {
        this.setRoomCapacityTooSmallPenalty(roomCapacityTooSmallPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withPeriodPenaltyExamCoincidencePenalty(int periodPenaltyExamCoincidencePenalty) {
        this.setPeriodPenaltyExamCoincidencePenalty(periodPenaltyExamCoincidencePenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withPeriodPenaltyExclusionPenalty(int periodPenaltyExclusionPenalty) {
        this.setPeriodPenaltyExclusionPenalty(periodPenaltyExclusionPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withPeriodPenaltyAfterPenalty(int periodPenaltyAfterPenalty) {
        this.setPeriodPenaltyAfterPenalty(periodPenaltyAfterPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withRoomPenaltyExclusivePenalty(int roomPenaltyExclusivePenalty) {
        this.setRoomPenaltyExclusivePenalty(roomPenaltyExclusivePenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withTwoInARowPenalty(int twoInARowPenalty) {
        this.setTwoInARowPenalty(twoInARowPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withTwoInADayPenalty(int twoInADayPenalty) {
        this.setTwoInADayPenalty(twoInADayPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withPeriodSpreadLength(int periodSpreadLength) {
        this.setPeriodSpreadLength(periodSpreadLength);
        return this;
    }

    public ExaminationConstraintConfiguration withPeriodSpreadPenalty(int periodSpreadPenalty) {
        this.setPeriodSpreadPenalty(periodSpreadPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withMixedDurationPenalty(int mixedDurationPenalty) {
        this.setMixedDurationPenalty(mixedDurationPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withFrontLoadLargeTopicSize(int frontLoadLargeTopicSize) {
        this.setFrontLoadLargeTopicSize(frontLoadLargeTopicSize);
        return this;
    }

    public ExaminationConstraintConfiguration withFrontLoadLastPeriodSize(int frontLoadLastPeriodSize) {
        this.setFrontLoadLastPeriodSize(frontLoadLastPeriodSize);
        return this;
    }

    public ExaminationConstraintConfiguration withFrontLoadPenalty(int frontLoadPenalty) {
        this.setFrontLoadPenalty(frontLoadPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withPeriodPenaltyPenalty(int periodPenaltyPenalty) {
        this.setPeriodPenaltyPenalty(periodPenaltyPenalty);
        return this;
    }

    public ExaminationConstraintConfiguration withRoomPenaltyPenalty(int roomPenaltyPenalty) {
        this.setRoomPenaltyPenalty(roomPenaltyPenalty);
        return this;
    }
}
