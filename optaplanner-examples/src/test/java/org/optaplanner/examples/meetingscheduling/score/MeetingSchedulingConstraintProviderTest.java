package org.optaplanner.examples.meetingscheduling.score;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.examples.meetingscheduling.domain.Day;
import org.optaplanner.examples.meetingscheduling.domain.Meeting;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.domain.Person;
import org.optaplanner.examples.meetingscheduling.domain.PreferredAttendance;
import org.optaplanner.examples.meetingscheduling.domain.RequiredAttendance;
import org.optaplanner.examples.meetingscheduling.domain.Room;
import org.optaplanner.examples.meetingscheduling.domain.TimeGrain;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class MeetingSchedulingConstraintProviderTest
        extends AbstractConstraintProviderTest<MeetingSchedulingConstraintProvider, MeetingSchedule> {

    @ConstraintProviderTest
    void roomConflictUnpenalized(ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Room room = new Room();

        TimeGrain timeGrain1 = new TimeGrain();
        timeGrain1.setGrainIndex(0);

        Meeting meeting1 = new Meeting();
        meeting1.setDurationInGrains(4);

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, meeting1, timeGrain1, room);

        TimeGrain timeGrain2 = new TimeGrain();
        timeGrain2.setGrainIndex(4);

        Meeting meeting2 = new Meeting();
        meeting2.setDurationInGrains(4);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, meeting2, timeGrain2, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::roomConflict)
                .given(leftAssignment, rightAssignment)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void roomConflictPenalized(ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Room room = new Room();

        TimeGrain timeGrain1 = new TimeGrain();
        timeGrain1.setGrainIndex(0);

        Meeting meeting1 = new Meeting();
        meeting1.setDurationInGrains(4);

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, meeting1, timeGrain1, room);

        TimeGrain timeGrain2 = new TimeGrain();
        timeGrain2.setGrainIndex(2);

        Meeting meeting2 = new Meeting();
        meeting2.setDurationInGrains(4);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, meeting2, timeGrain2, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::roomConflict)
                .given(leftAssignment, rightAssignment)
                .penalizesBy(2);
    }

    @ConstraintProviderTest
    void avoidOvertimeUnpenalized(ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        TimeGrain timeGrain = new TimeGrain();
        timeGrain.setGrainIndex(3);

        TimeGrain assignmentTimeGrain = new TimeGrain();
        assignmentTimeGrain.setGrainIndex(0);

        Meeting meeting = new Meeting();
        meeting.setDurationInGrains(4);

        Room room = new Room();

        MeetingAssignment meetingAssignment = new MeetingAssignment(0L, meeting, assignmentTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::avoidOvertime)
                .given(meetingAssignment, timeGrain)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void avoidOvertimePenalized(ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        TimeGrain assignmentTimeGrain = new TimeGrain();
        assignmentTimeGrain.setGrainIndex(0);

        Meeting meeting = new Meeting();
        meeting.setDurationInGrains(4);

        Room room = new Room();

        MeetingAssignment meetingAssignment = new MeetingAssignment(0L, meeting, assignmentTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::avoidOvertime)
                .given(meetingAssignment)
                .penalizesBy(3);
    }

    @ConstraintProviderTest
    void requiredAttendanceConflictUnpenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Person person = new Person();
        Meeting leftMeeting = new Meeting();
        leftMeeting.setDurationInGrains(4);

        RequiredAttendance requiredAttendance1 = new RequiredAttendance(0L, leftMeeting);
        requiredAttendance1.setPerson(person);

        TimeGrain leftTimeGrain = new TimeGrain();
        leftTimeGrain.setGrainIndex(0);

        Room room = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, leftMeeting, leftTimeGrain, room);

        Meeting rightMeeting = new Meeting();
        rightMeeting.setDurationInGrains(4);

        RequiredAttendance requiredAttendance2 = new RequiredAttendance(1L, rightMeeting);
        requiredAttendance2.setPerson(person);

        TimeGrain rightTimeGrain = new TimeGrain();
        rightTimeGrain.setGrainIndex(4);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, rightMeeting, rightTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::requiredAttendanceConflict)
                .given(requiredAttendance1, requiredAttendance2, leftAssignment, rightAssignment)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void requiredAttendanceConflictPenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Person person = new Person();
        Meeting leftMeeting = new Meeting();
        leftMeeting.setDurationInGrains(4);

        RequiredAttendance requiredAttendance1 = new RequiredAttendance(0L, leftMeeting);
        requiredAttendance1.setPerson(person);

        TimeGrain leftTimeGrain = new TimeGrain();
        leftTimeGrain.setGrainIndex(0);

        Room room = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, leftMeeting, leftTimeGrain, room);

        Meeting rightMeeting = new Meeting();
        rightMeeting.setDurationInGrains(4);

        RequiredAttendance requiredAttendance2 = new RequiredAttendance(1L, rightMeeting);
        requiredAttendance2.setPerson(person);

        TimeGrain rightTimeGrain = new TimeGrain();
        rightTimeGrain.setGrainIndex(2);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, rightMeeting, rightTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::requiredAttendanceConflict)
                .given(requiredAttendance1, requiredAttendance2, leftAssignment, rightAssignment)
                .penalizesBy(2);
    }

    @ConstraintProviderTest
    void requiredRoomCapacityUnpenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Room room = new Room();
        room.setCapacity(2);

        List<RequiredAttendance> requiredAttendanceList = new ArrayList<>(1);
        RequiredAttendance requiredAttendance = new RequiredAttendance();
        requiredAttendanceList.add(requiredAttendance);

        List<PreferredAttendance> preferredAttendanceList = new ArrayList<>(1);
        PreferredAttendance preferredAttendance = new PreferredAttendance();
        preferredAttendanceList.add(preferredAttendance);

        Meeting meeting = new Meeting();
        meeting.setRequiredAttendanceList(requiredAttendanceList);
        meeting.setPreferredAttendanceList(preferredAttendanceList);

        TimeGrain startingTimeGrain = new TimeGrain();

        MeetingAssignment meetingAssignment = new MeetingAssignment(0L, meeting, startingTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::requiredRoomCapacity)
                .given(meetingAssignment)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void requiredRoomCapacityPenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Room room = new Room();
        room.setCapacity(1);

        List<RequiredAttendance> requiredAttendanceList = new ArrayList<>(1);
        RequiredAttendance requiredAttendance = new RequiredAttendance();
        requiredAttendanceList.add(requiredAttendance);

        List<PreferredAttendance> preferredAttendanceList = new ArrayList<>(1);
        PreferredAttendance preferredAttendance = new PreferredAttendance();
        preferredAttendanceList.add(preferredAttendance);

        Meeting meeting = new Meeting();
        meeting.setRequiredAttendanceList(requiredAttendanceList);
        meeting.setPreferredAttendanceList(preferredAttendanceList);

        TimeGrain startingTimeGrain = new TimeGrain();

        MeetingAssignment meetingAssignment = new MeetingAssignment(0L, meeting, startingTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::requiredRoomCapacity)
                .given(meetingAssignment)
                .penalizesBy(1);
    }

    @ConstraintProviderTest
    void startAndEndOnSameDayUnpenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Day day = new Day();
        day.setDayOfYear(0);

        TimeGrain startingTimeGrain = new TimeGrain();
        startingTimeGrain.setGrainIndex(0);
        startingTimeGrain.setDay(day);

        Meeting meeting = new Meeting();
        meeting.setDurationInGrains(4);

        Room room = new Room();

        MeetingAssignment meetingAssignment = new MeetingAssignment(0L, meeting, startingTimeGrain, room);

        TimeGrain timeGrain = new TimeGrain();
        timeGrain.setGrainIndex(3);
        timeGrain.setDay(day);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::startAndEndOnSameDay)
                .given(meetingAssignment, timeGrain)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void startAndEndOnSameDayPenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Day day = new Day();
        day.setDayOfYear(0);

        TimeGrain startingTimeGrain = new TimeGrain();
        startingTimeGrain.setGrainIndex(0);
        startingTimeGrain.setDay(day);

        Meeting meeting = new Meeting();
        meeting.setDurationInGrains(4);

        Room room = new Room();

        MeetingAssignment meetingAssignment = new MeetingAssignment(0L, meeting, startingTimeGrain, room);

        TimeGrain timeGrain = new TimeGrain();
        timeGrain.setGrainIndex(3);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::startAndEndOnSameDay)
                .given(meetingAssignment, timeGrain)
                .penalizesBy(1);
    }

    @ConstraintProviderTest
    void requiredAndPreferredAttendanceConflictUnpenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Person person = new Person();

        Meeting leftMeeting = new Meeting();
        leftMeeting.setDurationInGrains(4);

        RequiredAttendance requiredAttendance = new RequiredAttendance();
        requiredAttendance.setPerson(person);
        requiredAttendance.setMeeting(leftMeeting);

        Meeting rightMeeting = new Meeting();
        rightMeeting.setDurationInGrains(4);

        PreferredAttendance preferredAttendance = new PreferredAttendance();
        preferredAttendance.setPerson(person);
        preferredAttendance.setMeeting(rightMeeting);

        TimeGrain leftTimeGrain = new TimeGrain();
        leftTimeGrain.setGrainIndex(0);

        Room room = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, leftMeeting, leftTimeGrain, room);

        TimeGrain rightTimeGrain = new TimeGrain();
        rightTimeGrain.setGrainIndex(4);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, rightMeeting, rightTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::requiredAndPreferredAttendanceConflict)
                .given(requiredAttendance, preferredAttendance, leftAssignment, rightAssignment)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void requiredAndPreferredAttendanceConflictPenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Person person = new Person();

        Meeting leftMeeting = new Meeting();
        leftMeeting.setDurationInGrains(4);

        RequiredAttendance requiredAttendance = new RequiredAttendance();
        requiredAttendance.setPerson(person);
        requiredAttendance.setMeeting(leftMeeting);

        Meeting rightMeeting = new Meeting();
        rightMeeting.setDurationInGrains(4);

        PreferredAttendance preferredAttendance = new PreferredAttendance();
        preferredAttendance.setPerson(person);
        preferredAttendance.setMeeting(rightMeeting);

        TimeGrain leftTimeGrain = new TimeGrain();
        leftTimeGrain.setGrainIndex(0);

        Room room = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, leftMeeting, leftTimeGrain, room);

        TimeGrain rightTimeGrain = new TimeGrain();
        rightTimeGrain.setGrainIndex(0);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, rightMeeting, rightTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::requiredAndPreferredAttendanceConflict)
                .given(requiredAttendance, preferredAttendance, leftAssignment, rightAssignment)
                .penalizesBy(4);
    }

    @ConstraintProviderTest
    void preferredAttendanceConflictUnpenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Person person = new Person();

        Meeting leftMeeting = new Meeting();
        leftMeeting.setDurationInGrains(4);

        PreferredAttendance leftAttendance = new PreferredAttendance(0L, leftMeeting);
        leftAttendance.setPerson(person);

        Meeting rightMeeting = new Meeting();
        rightMeeting.setDurationInGrains(4);

        PreferredAttendance rightAttendance = new PreferredAttendance(1L, rightMeeting);
        rightAttendance.setPerson(person);

        TimeGrain leftTimeGrain = new TimeGrain();
        leftTimeGrain.setGrainIndex(0);

        Room room = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, leftMeeting, leftTimeGrain, room);

        TimeGrain rightTimeGrain = new TimeGrain();
        rightTimeGrain.setGrainIndex(4);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, rightMeeting, rightTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::preferredAttendanceConflict)
                .given(leftAttendance, rightAttendance, leftAssignment, rightAssignment)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void preferredAttendanceConflictPenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Person person = new Person();

        Meeting leftMeeting = new Meeting();
        leftMeeting.setDurationInGrains(4);

        PreferredAttendance leftAttendance = new PreferredAttendance(0L, leftMeeting);
        leftAttendance.setPerson(person);

        Meeting rightMeeting = new Meeting();
        rightMeeting.setDurationInGrains(4);

        PreferredAttendance rightAttendance = new PreferredAttendance(1L, rightMeeting);
        rightAttendance.setPerson(person);

        TimeGrain leftTimeGrain = new TimeGrain();
        leftTimeGrain.setGrainIndex(0);

        Room room = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, leftMeeting, leftTimeGrain, room);

        TimeGrain rightTimeGrain = new TimeGrain();
        rightTimeGrain.setGrainIndex(0);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, rightMeeting, rightTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::preferredAttendanceConflict)
                .given(leftAttendance, rightAttendance, leftAssignment, rightAssignment)
                .penalizesBy(4);
    }

    @ConstraintProviderTest
    void doMeetingsAsSoonAsPossibleUnpenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        TimeGrain timeGrain = new TimeGrain();
        timeGrain.setGrainIndex(0);

        Meeting meeting = new Meeting();
        meeting.setDurationInGrains(1);

        Room room = new Room();

        MeetingAssignment meetingAssignment = new MeetingAssignment(0L, meeting, timeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::doMeetingsAsSoonAsPossible)
                .given(meetingAssignment)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void doMeetingsAsSoonAsPossiblePenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        TimeGrain timeGrain = new TimeGrain();
        timeGrain.setGrainIndex(0);

        Meeting meeting = new Meeting();
        meeting.setDurationInGrains(4);

        Room room = new Room();

        MeetingAssignment meetingAssignment = new MeetingAssignment(0L, meeting, timeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::doMeetingsAsSoonAsPossible)
                .given(meetingAssignment)
                .penalizesBy(3);
    }

    @ConstraintProviderTest
    void oneBreakBetweenConsecutiveMeetingsUnpenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        TimeGrain leftTimeGrain = new TimeGrain();
        leftTimeGrain.setGrainIndex(0);

        Meeting meeting = new Meeting();
        meeting.setDurationInGrains(4);

        Room room = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, meeting, leftTimeGrain, room);

        TimeGrain rightTimeGrain = new TimeGrain();
        rightTimeGrain.setGrainIndex(0);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, meeting, rightTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::oneBreakBetweenConsecutiveMeetings)
                .given(leftAssignment, rightAssignment)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void oneBreakBetweenConsecutiveMeetingsPenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        TimeGrain leftTimeGrain = new TimeGrain();
        leftTimeGrain.setGrainIndex(0);

        Meeting meeting = new Meeting();
        meeting.setDurationInGrains(4);

        Room room = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, meeting, leftTimeGrain, room);

        TimeGrain rightTimeGrain = new TimeGrain();
        rightTimeGrain.setGrainIndex(4);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, meeting, rightTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::oneBreakBetweenConsecutiveMeetings)
                .given(leftAssignment, rightAssignment)
                .penalizesBy(1);
    }

    @ConstraintProviderTest
    void overlappingMeetingsUnpenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        TimeGrain leftTimeGrain = new TimeGrain();
        leftTimeGrain.setGrainIndex(0);

        Meeting leftMeeting = new Meeting(1L);
        leftMeeting.setDurationInGrains(4);

        Room room = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, leftMeeting, leftTimeGrain, room);

        TimeGrain rightTimeGrain = new TimeGrain();
        rightTimeGrain.setGrainIndex(4);

        Meeting rightMeeting = new Meeting(0L);
        rightMeeting.setDurationInGrains(4);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, rightMeeting, rightTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::overlappingMeetings)
                .given(leftAssignment, rightAssignment)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void overlappingMeetingsPenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        TimeGrain leftTimeGrain = new TimeGrain();
        leftTimeGrain.setGrainIndex(1);

        Meeting leftMeeting = new Meeting(1L);
        leftMeeting.setDurationInGrains(3);

        Room room = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, leftMeeting, leftTimeGrain, room);

        TimeGrain rightTimeGrain = new TimeGrain();
        rightTimeGrain.setGrainIndex(0);

        Meeting rightMeeting = new Meeting(0L);
        rightMeeting.setDurationInGrains(3);

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, rightMeeting, rightTimeGrain, room);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::overlappingMeetings)
                .given(leftAssignment, rightAssignment)
                .penalizesBy(2);
    }

    @ConstraintProviderTest
    void assignLargerRoomsFirstUnpenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Room meetingRoom = new Room();
        meetingRoom.setCapacity(1);

        Meeting meeting = new Meeting();

        TimeGrain startingTimeGrain = new TimeGrain();

        MeetingAssignment meetingAssignment = new MeetingAssignment(0L, meeting, startingTimeGrain, meetingRoom);
        meetingAssignment.setRoom(meetingRoom);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::assignLargerRoomsFirst)
                .given(meetingAssignment)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void assignLargerRoomsFirstPenalized(
            ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Room meetingRoom = new Room();
        meetingRoom.setCapacity(1);

        Meeting meeting = new Meeting();

        TimeGrain startingTimeGrain = new TimeGrain();

        MeetingAssignment meetingAssignment = new MeetingAssignment(0L, meeting, startingTimeGrain, meetingRoom);

        Room largerRoom = new Room();
        largerRoom.setCapacity(2);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::assignLargerRoomsFirst)
                .given(meetingAssignment, largerRoom)
                .penalizesBy(1);
    }

    @ConstraintProviderTest
    void roomStabilityUnpenalized(ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Day day = new Day();
        day.setDayOfYear(1);
        Person person = new Person();

        Meeting leftMeeting = new Meeting();
        leftMeeting.setDurationInGrains(4);

        RequiredAttendance leftAttendance = new RequiredAttendance();
        leftAttendance.setMeeting(leftMeeting);
        leftAttendance.setPerson(person);

        Meeting rightMeeting = new Meeting();
        rightMeeting.setDurationInGrains(4);

        RequiredAttendance rightAttendance = new RequiredAttendance();
        rightAttendance.setMeeting(rightMeeting);
        rightAttendance.setPerson(person);

        TimeGrain leftStartTimeGrain = new TimeGrain();
        leftStartTimeGrain.setDay(day);
        leftStartTimeGrain.setStartingMinuteOfDay(0);
        leftStartTimeGrain.setGrainIndex(0);

        Room leftRoom = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, leftMeeting, leftStartTimeGrain, leftRoom);

        TimeGrain rightStartTimeGrain = new TimeGrain();
        rightStartTimeGrain.setDay(day);
        rightStartTimeGrain.setStartingMinuteOfDay(8 * TimeGrain.GRAIN_LENGTH_IN_MINUTES);
        rightStartTimeGrain.setGrainIndex(8);

        Room rightRoom = new Room();

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, rightMeeting, rightStartTimeGrain, rightRoom);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::roomStability)
                .given(leftAttendance, rightAttendance, leftAssignment, rightAssignment)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void roomStabilityPenalized(ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> constraintVerifier) {
        Day day = new Day();
        day.setDayOfYear(1);
        Person person = new Person();

        Meeting leftMeeting = new Meeting();
        leftMeeting.setDurationInGrains(4);

        RequiredAttendance leftAttendance = new RequiredAttendance();
        leftAttendance.setMeeting(leftMeeting);
        leftAttendance.setPerson(person);

        Meeting rightMeeting = new Meeting();
        rightMeeting.setDurationInGrains(4);

        RequiredAttendance rightAttendance = new RequiredAttendance();
        rightAttendance.setMeeting(rightMeeting);
        rightAttendance.setPerson(person);

        TimeGrain leftStartTimeGrain = new TimeGrain();
        leftStartTimeGrain.setDay(day);
        leftStartTimeGrain.setStartingMinuteOfDay(0);
        leftStartTimeGrain.setGrainIndex(0);

        Room leftRoom = new Room();

        MeetingAssignment leftAssignment = new MeetingAssignment(0L, leftMeeting, leftStartTimeGrain, leftRoom);

        TimeGrain rightStartTimeGrain = new TimeGrain();
        rightStartTimeGrain.setDay(day);
        rightStartTimeGrain.setStartingMinuteOfDay(4 * TimeGrain.GRAIN_LENGTH_IN_MINUTES);
        rightStartTimeGrain.setGrainIndex(4);

        Room rightRoom = new Room();

        MeetingAssignment rightAssignment = new MeetingAssignment(1L, rightMeeting, rightStartTimeGrain, rightRoom);

        constraintVerifier.verifyThat(MeetingSchedulingConstraintProvider::roomStability)
                .given(leftAttendance, rightAttendance, leftAssignment, rightAssignment)
                .penalizesBy(1);
    }

    @Override
    protected ConstraintVerifier<MeetingSchedulingConstraintProvider, MeetingSchedule> createConstraintVerifier() {
        return ConstraintVerifier.build(new MeetingSchedulingConstraintProvider(), MeetingSchedule.class,
                MeetingAssignment.class);
    }
}
