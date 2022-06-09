package org.optaplanner.examples.meetingscheduling.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.meetingscheduling.score")
public class MeetingConstraintConfiguration extends AbstractPersistable {

    public static final String ROOM_CONFLICT = "Room conflict";
    public static final String DONT_GO_IN_OVERTIME = "Don't go in overtime";
    public static final String REQUIRED_ATTENDANCE_CONFLICT = "Required attendance conflict";
    public static final String REQUIRED_ROOM_CAPACITY = "Required room capacity";
    public static final String START_AND_END_ON_SAME_DAY = "Start and end on same day";

    public static final String REQUIRED_AND_PREFERRED_ATTENDANCE_CONFLICT = "Required and preferred attendance conflict";
    public static final String PREFERRED_ATTENDANCE_CONFLICT = "Preferred attendance conflict";

    public static final String DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE = "Do all meetings as soon as possible";
    public static final String ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS =
            "One TimeGrain break between two consecutive meetings";
    public static final String OVERLAPPING_MEETINGS = "Overlapping meetings";
    public static final String ASSIGN_LARGER_ROOMS_FIRST = "Assign larger rooms first";
    public static final String ROOM_STABILITY = "Room stability";

    @ConstraintWeight(ROOM_CONFLICT)
    private HardMediumSoftScore roomConflict = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DONT_GO_IN_OVERTIME)
    private HardMediumSoftScore dontGoInOvertime = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(REQUIRED_ATTENDANCE_CONFLICT)
    private HardMediumSoftScore requiredAttendanceConflict = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(REQUIRED_ROOM_CAPACITY)
    private HardMediumSoftScore requiredRoomCapacity = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(START_AND_END_ON_SAME_DAY)
    private HardMediumSoftScore startAndEndOnSameDay = HardMediumSoftScore.ofHard(1);

    @ConstraintWeight(REQUIRED_AND_PREFERRED_ATTENDANCE_CONFLICT)
    private HardMediumSoftScore requiredAndPreferredAttendanceConflict = HardMediumSoftScore.ofMedium(1);
    @ConstraintWeight(PREFERRED_ATTENDANCE_CONFLICT)
    private HardMediumSoftScore preferredAttendanceConflict = HardMediumSoftScore.ofMedium(1);

    @ConstraintWeight(DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE)
    private HardMediumSoftScore doAllMeetingsAsSoonAsPossible = HardMediumSoftScore.ofSoft(1);
    @ConstraintWeight(ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS)
    private HardMediumSoftScore oneTimeGrainBreakBetweenTwoConsecutiveMeetings = HardMediumSoftScore.ofSoft(100);
    @ConstraintWeight(OVERLAPPING_MEETINGS)
    private HardMediumSoftScore overlappingMeetings = HardMediumSoftScore.ofSoft(10);
    @ConstraintWeight(ASSIGN_LARGER_ROOMS_FIRST)
    private HardMediumSoftScore assignLargerRoomsFirst = HardMediumSoftScore.ofSoft(1);
    @ConstraintWeight(ROOM_STABILITY)
    private HardMediumSoftScore roomStability = HardMediumSoftScore.ofSoft(1);

    public MeetingConstraintConfiguration() {
    }

    public MeetingConstraintConfiguration(long id) {
        super(id);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public HardMediumSoftScore getRoomConflict() {
        return roomConflict;
    }

    public void setRoomConflict(HardMediumSoftScore roomConflict) {
        this.roomConflict = roomConflict;
    }

    public HardMediumSoftScore getDontGoInOvertime() {
        return dontGoInOvertime;
    }

    public void setDontGoInOvertime(HardMediumSoftScore dontGoInOvertime) {
        this.dontGoInOvertime = dontGoInOvertime;
    }

    public HardMediumSoftScore getRequiredAttendanceConflict() {
        return requiredAttendanceConflict;
    }

    public void setRequiredAttendanceConflict(HardMediumSoftScore requiredAttendanceConflict) {
        this.requiredAttendanceConflict = requiredAttendanceConflict;
    }

    public HardMediumSoftScore getRequiredRoomCapacity() {
        return requiredRoomCapacity;
    }

    public void setRequiredRoomCapacity(HardMediumSoftScore requiredRoomCapacity) {
        this.requiredRoomCapacity = requiredRoomCapacity;
    }

    public HardMediumSoftScore getStartAndEndOnSameDay() {
        return startAndEndOnSameDay;
    }

    public void setStartAndEndOnSameDay(HardMediumSoftScore startAndEndOnSameDay) {
        this.startAndEndOnSameDay = startAndEndOnSameDay;
    }

    public HardMediumSoftScore getRequiredAndPreferredAttendanceConflict() {
        return requiredAndPreferredAttendanceConflict;
    }

    public void setRequiredAndPreferredAttendanceConflict(HardMediumSoftScore requiredAndPreferredAttendanceConflict) {
        this.requiredAndPreferredAttendanceConflict = requiredAndPreferredAttendanceConflict;
    }

    public HardMediumSoftScore getPreferredAttendanceConflict() {
        return preferredAttendanceConflict;
    }

    public void setPreferredAttendanceConflict(HardMediumSoftScore preferredAttendanceConflict) {
        this.preferredAttendanceConflict = preferredAttendanceConflict;
    }

    public HardMediumSoftScore getDoAllMeetingsAsSoonAsPossible() {
        return doAllMeetingsAsSoonAsPossible;
    }

    public void setDoAllMeetingsAsSoonAsPossible(HardMediumSoftScore doAllMeetingsAsSoonAsPossible) {
        this.doAllMeetingsAsSoonAsPossible = doAllMeetingsAsSoonAsPossible;
    }

    public HardMediumSoftScore getOneTimeGrainBreakBetweenTwoConsecutiveMeetings() {
        return oneTimeGrainBreakBetweenTwoConsecutiveMeetings;
    }

    public void setOneTimeGrainBreakBetweenTwoConsecutiveMeetings(
            HardMediumSoftScore oneTimeGrainBreakBetweenTwoConsecutiveMeetings) {
        this.oneTimeGrainBreakBetweenTwoConsecutiveMeetings = oneTimeGrainBreakBetweenTwoConsecutiveMeetings;
    }

    public HardMediumSoftScore getOverlappingMeetings() {
        return overlappingMeetings;
    }

    public void setOverlappingMeetings(HardMediumSoftScore overlappingMeetings) {
        this.overlappingMeetings = overlappingMeetings;
    }

    public HardMediumSoftScore getAssignLargerRoomsFirst() {
        return assignLargerRoomsFirst;
    }

    public void setAssignLargerRoomsFirst(HardMediumSoftScore assignLargerRoomsFirst) {
        this.assignLargerRoomsFirst = assignLargerRoomsFirst;
    }

    public HardMediumSoftScore getRoomStability() {
        return roomStability;
    }

    public void setRoomStability(HardMediumSoftScore roomStability) {
        this.roomStability = roomStability;
    }

}
