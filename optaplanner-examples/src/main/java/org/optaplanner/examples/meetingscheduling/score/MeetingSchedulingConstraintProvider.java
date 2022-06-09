package org.optaplanner.examples.meetingscheduling.score;

import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;
import static org.optaplanner.core.api.score.stream.Joiners.greaterThan;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;
import static org.optaplanner.core.api.score.stream.Joiners.overlapping;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.meetingscheduling.domain.Attendance;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.PreferredAttendance;
import org.optaplanner.examples.meetingscheduling.domain.RequiredAttendance;
import org.optaplanner.examples.meetingscheduling.domain.Room;
import org.optaplanner.examples.meetingscheduling.domain.TimeGrain;

public class MeetingSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                roomConflict(constraintFactory),
                avoidOvertime(constraintFactory),
                requiredAttendanceConflict(constraintFactory),
                requiredRoomCapacity(constraintFactory),
                startAndEndOnSameDay(constraintFactory),
                requiredAndPreferredAttendanceConflict(constraintFactory),
                preferredAttendanceConflict(constraintFactory),
                doMeetingsAsSoonAsPossible(constraintFactory),
                oneBreakBetweenConsecutiveMeetings(constraintFactory),
                overlappingMeetings(constraintFactory),
                assignLargerRoomsFirst(constraintFactory),
                roomStability(constraintFactory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    protected Constraint roomConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(MeetingAssignment.class,
                equal(MeetingAssignment::getRoom),
                overlapping(assignment -> assignment.getStartingTimeGrain().getGrainIndex(),
                        assignment -> assignment.getStartingTimeGrain().getGrainIndex() +
                                assignment.getMeeting().getDurationInGrains()))
                .penalizeConfigurable("Room conflict",
                        (leftAssignment, rightAssignment) -> rightAssignment.calculateOverlap(leftAssignment));
    }

    protected Constraint avoidOvertime(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                .filter(meetingAssignment -> meetingAssignment.getStartingTimeGrain() != null)
                .ifNotExists(TimeGrain.class,
                        equal(MeetingAssignment::getLastTimeGrainIndex, TimeGrain::getGrainIndex))
                .penalizeConfigurable("Don't go in overtime", MeetingAssignment::getLastTimeGrainIndex);
    }

    protected Constraint requiredAttendanceConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(RequiredAttendance.class,
                equal(RequiredAttendance::getPerson))
                .join(MeetingAssignment.class,
                        equal((leftRequiredAttendance, rightRequiredAttendance) -> leftRequiredAttendance.getMeeting(),
                                MeetingAssignment::getMeeting))
                .join(MeetingAssignment.class,
                        equal((leftRequiredAttendance, rightRequiredAttendance, leftAssignment) -> rightRequiredAttendance
                                .getMeeting(),
                                MeetingAssignment::getMeeting),
                        overlapping((attendee1, attendee2, assignment) -> assignment.getStartingTimeGrain().getGrainIndex(),
                                (attendee1, attendee2, assignment) -> assignment.getStartingTimeGrain().getGrainIndex() +
                                        assignment.getMeeting().getDurationInGrains(),
                                assignment -> assignment.getStartingTimeGrain().getGrainIndex(),
                                assignment -> assignment.getStartingTimeGrain().getGrainIndex() +
                                        assignment.getMeeting().getDurationInGrains()))
                .penalizeConfigurable("Required attendance conflict",
                        (leftRequiredAttendance, rightRequiredAttendance, leftAssignment, rightAssignment) -> rightAssignment
                                .calculateOverlap(leftAssignment));
    }

    protected Constraint requiredRoomCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                .filter(meetingAssignment -> meetingAssignment.getRequiredCapacity() > meetingAssignment.getRoomCapacity())
                .penalizeConfigurable("Required room capacity",
                        meetingAssignment -> meetingAssignment.getRequiredCapacity() - meetingAssignment.getRoomCapacity());
    }

    protected Constraint startAndEndOnSameDay(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                .filter(meetingAssignment -> meetingAssignment.getStartingTimeGrain() != null)
                .join(TimeGrain.class,
                        equal(MeetingAssignment::getLastTimeGrainIndex, TimeGrain::getGrainIndex),
                        filtering((meetingAssignment,
                                timeGrain) -> meetingAssignment.getStartingTimeGrain().getDay() != timeGrain.getDay()))
                .penalizeConfigurable("Start and end on same day");
    }

    // ************************************************************************
    // Medium constraints
    // ************************************************************************

    protected Constraint requiredAndPreferredAttendanceConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(RequiredAttendance.class)
                .join(PreferredAttendance.class,
                        equal(RequiredAttendance::getPerson, PreferredAttendance::getPerson))
                .join(constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                        .filter(assignment -> assignment.getStartingTimeGrain() != null),
                        equal((requiredAttendance, preferredAttendance) -> requiredAttendance.getMeeting(),
                                MeetingAssignment::getMeeting))
                .join(constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                        .filter(assignment -> assignment.getStartingTimeGrain() != null),
                        equal((requiredAttendance, preferredAttendance, leftAssignment) -> preferredAttendance.getMeeting(),
                                MeetingAssignment::getMeeting),
                        overlapping((attendee1, attendee2, assignment) -> assignment.getStartingTimeGrain().getGrainIndex(),
                                (attendee1, attendee2, assignment) -> assignment.getStartingTimeGrain().getGrainIndex() +
                                        assignment.getMeeting().getDurationInGrains(),
                                assignment -> assignment.getStartingTimeGrain().getGrainIndex(),
                                assignment -> assignment.getStartingTimeGrain().getGrainIndex() +
                                        assignment.getMeeting().getDurationInGrains()))
                .penalizeConfigurable("Required and preferred attendance conflict");
    }

    protected Constraint preferredAttendanceConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(PreferredAttendance.class,
                equal(PreferredAttendance::getPerson))
                .join(constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                        .filter(assignment -> assignment.getStartingTimeGrain() != null),
                        equal((leftAttendance, rightAttendance) -> leftAttendance.getMeeting(),
                                MeetingAssignment::getMeeting))
                .join(constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                        .filter(assignment -> assignment.getStartingTimeGrain() != null),
                        equal((leftAttendance, rightAttendance, leftAssignment) -> rightAttendance.getMeeting(),
                                MeetingAssignment::getMeeting),
                        overlapping((attendee1, attendee2, assignment) -> assignment.getStartingTimeGrain().getGrainIndex(),
                                (attendee1, attendee2, assignment) -> assignment.getStartingTimeGrain().getGrainIndex() +
                                        assignment.getMeeting().getDurationInGrains(),
                                assignment -> assignment.getStartingTimeGrain().getGrainIndex(),
                                assignment -> assignment.getStartingTimeGrain().getGrainIndex() +
                                        assignment.getMeeting().getDurationInGrains()))
                .penalizeConfigurable("Preferred attendance conflict");
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    protected Constraint doMeetingsAsSoonAsPossible(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                .filter(meetingAssignment -> meetingAssignment.getStartingTimeGrain() != null)
                .penalizeConfigurable("Do all meetings as soon as possible",
                        MeetingAssignment::getLastTimeGrainIndex);
    }

    protected Constraint oneBreakBetweenConsecutiveMeetings(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                .filter(meetingAssignment -> meetingAssignment.getStartingTimeGrain() != null)
                .join(constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                        .filter(assignment -> assignment.getStartingTimeGrain() != null),
                        equal(MeetingAssignment::getLastTimeGrainIndex,
                                (rightAssignment) -> rightAssignment.getStartingTimeGrain().getGrainIndex() - 1))
                .penalizeConfigurable("One TimeGrain break between two consecutive meetings");
    }

    protected Constraint overlappingMeetings(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                .filter(meetingAssignment -> meetingAssignment.getStartingTimeGrain() != null)
                .join(constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                        .filter(meetingAssignment -> meetingAssignment.getStartingTimeGrain() != null),
                        greaterThan((leftAssignment) -> leftAssignment.getMeeting().getId(),
                                (rightAssignment) -> rightAssignment.getMeeting().getId()),
                        overlapping(assignment -> assignment.getStartingTimeGrain().getGrainIndex(),
                                assignment -> assignment.getStartingTimeGrain().getGrainIndex() +
                                        assignment.getMeeting().getDurationInGrains()))
                .penalizeConfigurable("Overlapping meetings", MeetingAssignment::calculateOverlap);
    }

    // TODO: Unspecified bug marked in DRL
    protected Constraint assignLargerRoomsFirst(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                .filter(meetingAssignment -> meetingAssignment.getRoom() != null)
                .join(Room.class,
                        lessThan(MeetingAssignment::getRoomCapacity, Room::getCapacity))
                .penalizeConfigurable("Assign larger rooms first",
                        (meetingAssignment, room) -> room.getCapacity() - meetingAssignment.getRoomCapacity());
    }

    protected Constraint roomStability(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Attendance.class)
                .join(Attendance.class,
                        equal(Attendance::getPerson),
                        filtering((leftAttendance,
                                rightAttendance) -> leftAttendance.getMeeting() != rightAttendance.getMeeting()))
                .join(constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                        .filter(assignment -> assignment.getStartingTimeGrain() != null),
                        equal((leftAttendance, rightAttendance) -> leftAttendance.getMeeting(),
                                MeetingAssignment::getMeeting))
                .join(constraintFactory.forEachIncludingNullVars(MeetingAssignment.class)
                        .filter(assignment -> assignment.getStartingTimeGrain() != null),
                        equal((leftAttendance, rightAttendance, leftAssignment) -> rightAttendance.getMeeting(),
                                MeetingAssignment::getMeeting),
                        lessThan(
                                (leftAttendance, rightAttendance, leftAssignment) -> leftAssignment.getStartingTimeGrain()
                                        .getGrainIndex(),
                                (rightAssignment) -> rightAssignment.getStartingTimeGrain().getGrainIndex()),
                        filtering((leftAttendance, rightAttendance, leftAssignment,
                                rightAssignment) -> leftAssignment.getRoom() != rightAssignment.getRoom()),
                        filtering((leftAttendance, rightAttendance, leftAssignment,
                                rightAssignment) -> rightAssignment.getStartingTimeGrain().getGrainIndex() -
                                        leftAttendance.getMeeting().getDurationInGrains() -
                                        leftAssignment.getStartingTimeGrain().getGrainIndex() <= 2))
                .penalizeConfigurable("Room stability");
    }
}
