package org.optaplanner.examples.examination.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Institutional weightings.
 * Allows the user to recalibrate score weights.
 * <p>
 * Each {@link Examination} has only 1 instance of this class.
 */
@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.examination.score")
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

    public ExaminationConstraintConfiguration() {
    }

    public ExaminationConstraintConfiguration(long id) {
        super(id);
    }

    // ************************************************************************
    // Constraint weight methods
    // ************************************************************************

    // Hard constraints
    @ConstraintWeight("conflictingExamsInSamePeriod")
    @JsonIgnore
    public HardSoftScore getConflictingExamsInSamePeriodPenaltyAsScore() {
        return HardSoftScore.ofHard(conflictingExamsInSamePeriodPenalty);
    }

    @ConstraintWeight("periodDurationTooShort")
    @JsonIgnore
    public HardSoftScore getPeriodDurationTooShortPenaltyAsScore() {
        return HardSoftScore.ofHard(periodDurationTooShortPenalty);
    }

    @ConstraintWeight("roomCapacityTooSmall")
    @JsonIgnore
    public HardSoftScore getRoomCapacityTooSmallPenaltyAsScore() {
        return HardSoftScore.ofHard(roomCapacityTooSmallPenalty);
    }

    @ConstraintWeight("periodPenaltyExamCoincidence")
    @JsonIgnore
    public HardSoftScore getPeriodPenaltyExamCoincidencePenaltyAsScore() {
        return HardSoftScore.ofHard(periodPenaltyExamCoincidencePenalty);
    }

    @ConstraintWeight("periodPenaltyExclusion")
    @JsonIgnore
    public HardSoftScore getPeriodPenaltyExclusionPenaltyAsScore() {
        return HardSoftScore.ofHard(periodPenaltyExclusionPenalty);
    }

    @ConstraintWeight("periodPenaltyAfter")
    @JsonIgnore
    public HardSoftScore getPeriodPenaltyAfterPenaltyAsScore() {
        return HardSoftScore.ofHard(periodPenaltyAfterPenalty);
    }

    @ConstraintWeight("roomPenaltyExclusive")
    @JsonIgnore
    public HardSoftScore getRoomPenaltyExclusivePenaltyAsScore() {
        return HardSoftScore.ofHard(roomPenaltyExclusivePenalty);
    }

    // Soft constraints
    @ConstraintWeight("twoExamsInARow")
    @JsonIgnore
    public HardSoftScore getTwoInARowPenaltyAsScore() {
        return HardSoftScore.ofSoft(twoInARowPenalty);
    }

    @ConstraintWeight("twoExamsInADay")
    @JsonIgnore
    public HardSoftScore getTwoInADayPenaltyAsScore() {
        return HardSoftScore.ofSoft(twoInADayPenalty);
    }

    @ConstraintWeight("periodSpread")
    @JsonIgnore
    public HardSoftScore getPeriodSpreadPenaltyAsScore() {
        return HardSoftScore.ofSoft(periodSpreadPenalty);
    }

    @ConstraintWeight("mixedDurations")
    @JsonIgnore
    public HardSoftScore getMixedDurationPenaltyAsScore() {
        return HardSoftScore.ofSoft(mixedDurationPenalty);
    }

    @ConstraintWeight("frontLoad")
    @JsonIgnore
    public HardSoftScore getFrontLoadPenaltyAsScore() {
        return HardSoftScore.ofSoft(frontLoadPenalty);
    }

    @ConstraintWeight("periodPenalty")
    @JsonIgnore
    public HardSoftScore getPeriodPenaltyPenaltyAsScore() {
        return HardSoftScore.ofSoft(periodPenaltyPenalty);
    }

    @ConstraintWeight("roomPenalty")
    @JsonIgnore
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
