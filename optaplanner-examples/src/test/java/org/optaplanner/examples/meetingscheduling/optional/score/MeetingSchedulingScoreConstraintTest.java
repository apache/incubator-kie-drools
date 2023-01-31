package org.optaplanner.examples.meetingscheduling.optional.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.meetingscheduling.app.MeetingSchedulingApp;
import org.optaplanner.examples.meetingscheduling.domain.Attendance;
import org.optaplanner.examples.meetingscheduling.domain.Day;
import org.optaplanner.examples.meetingscheduling.domain.Meeting;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.domain.Person;
import org.optaplanner.examples.meetingscheduling.domain.RequiredAttendance;
import org.optaplanner.examples.meetingscheduling.domain.Room;
import org.optaplanner.examples.meetingscheduling.domain.TimeGrain;
import org.optaplanner.test.impl.score.buildin.hardmediumsoft.HardMediumSoftScoreVerifier;

class MeetingSchedulingScoreConstraintTest {

    private HardMediumSoftScoreVerifier<MeetingSchedule> scoreVerifier =
            new HardMediumSoftScoreVerifier<>(SolverFactory.createFromXmlResource(MeetingSchedulingApp.SOLVER_CONFIG));

    private MeetingSchedule getMeetingSchedule(int numberOfEntities) {
        // After getting the solution, need to set AttendanceList for it. And for every meeting Required & Preferred attendance lists
        MeetingSchedule solution = new MeetingSchedule();
        MeetingConstraintConfiguration constraintConfiguration = new MeetingConstraintConfiguration(0L);
        solution.setConstraintConfiguration(constraintConfiguration);

        List<Meeting> meetingList = new ArrayList<>();
        List<Day> dayList = new ArrayList<>();
        List<TimeGrain> timeGrainList = new ArrayList<>();
        List<Room> roomList = new ArrayList<>();
        List<Person> personList = new ArrayList<>();
        List<MeetingAssignment> meetingAssignmentList = new ArrayList<>();

        for (int i = 0; i < numberOfEntities; i++) {
            Meeting m = new Meeting(i);
            m.setTopic("meeting" + i);
            meetingList.add(m);

            Day d = new Day(i, i + 1);
            dayList.add(d);

            TimeGrain t = new TimeGrain(i, i, dayList.get(0), i * TimeGrain.GRAIN_LENGTH_IN_MINUTES);
            timeGrainList.add(t);

            Room r = new Room(i, "room" + i);
            roomList.add(r);

            Person p = new Person(i, "person" + i);
            personList.add(p);

            MeetingAssignment ma = new MeetingAssignment(i);
            meetingAssignmentList.add(ma);
        }

        solution.setRoomList(roomList);
        solution.setTimeGrainList(timeGrainList);
        solution.setDayList(dayList);
        solution.setPersonList(personList);
        solution.setMeetingList(meetingList);
        solution.setMeetingAssignmentList(meetingAssignmentList);

        return solution;
    }

    @Test
    void roomStability() {
        MeetingSchedule solution = getMeetingSchedule(6);
        MeetingConstraintConfiguration constraintConfiguration = solution.getConstraintConfiguration();
        List<Attendance> aList = new ArrayList<>();
        for (int i = 0; i < solution.getMeetingList().size(); i++) {
            Meeting m = solution.getMeetingList().get(i);
            m.setDurationInGrains(2);
            solution.getMeetingAssignmentList().get(i).setMeeting(m);

            RequiredAttendance ra = new RequiredAttendance(i, m);
            ra.setPerson(solution.getPersonList().get(0));
            aList.add(ra);
            m.setPreferredAttendanceList(new ArrayList<>());
            m.setRequiredAttendanceList(Collections.singletonList(ra));
        }
        solution.setAttendanceList(aList);

        /*
         * Scenario 1: should penalize
         * t0 t1 t2 t3 t4 t5
         * --- --- --- --- --- ---
         * r0 | m0 |
         * r1 | m1 |
         */
        MeetingAssignment ma0 = solution.getMeetingAssignmentList().get(0);
        ma0.setStartingTimeGrain(solution.getTimeGrainList().get(0));
        ma0.setRoom(solution.getRoomList().get(0));

        MeetingAssignment ma1 = solution.getMeetingAssignmentList().get(1);
        ma1.setStartingTimeGrain(solution.getTimeGrainList().get(2));
        ma1.setRoom(solution.getRoomList().get(1));

        scoreVerifier.assertSoftWeight("Room stability", -constraintConfiguration.getRoomStability().softScore(), solution);

        /*
         * Scenario 2: should penalize
         * t0 t1 t2 t3 t4 t5
         * --- --- --- --- --- ---
         * r0 | m0 |
         * r1 | m1 |
         */
        ma1.setStartingTimeGrain(solution.getTimeGrainList().get(3));
        scoreVerifier.assertSoftWeight("Room stability", -constraintConfiguration.getRoomStability().softScore(), solution);

        /*
         * Scenario 3: should penalize
         * t0 t1 t2 t3 t4 t5
         * --- --- --- --- --- ---
         * r0 | m0 |
         * r1 | m1 |
         */
        ma1.setStartingTimeGrain(solution.getTimeGrainList().get(4));
        scoreVerifier.assertSoftWeight("Room stability", -constraintConfiguration.getRoomStability().softScore(), solution);

        /*
         * Scenario 4: shouldn't penalize
         * t0 t1 t2 t3 t4 t5
         * --- --- --- --- --- ---
         * r0 | m0 |
         * r1 | m1 |
         */
        ma1.setStartingTimeGrain(solution.getTimeGrainList().get(5));
        scoreVerifier.assertSoftWeight("Room stability", 0, solution);

        /*
         * Scenario 5: shouldn't penalize
         * t0 t1 t2 t3 t4 t5
         * --- --- --- --- --- ---
         * r0 | m0 || m1 |
         * r1
         */
        ma1.setStartingTimeGrain(solution.getTimeGrainList().get(2));
        ma1.setRoom(solution.getRoomList().get(0));
        scoreVerifier.assertSoftWeight("Room stability", 0, solution);

        /*
         * Scenario 1: should penalize twice
         * t0 t1 t2 t3 t4 t5
         * --- --- --- --- --- ---
         * r0 | m0 | | m2 |
         * r1 | m1 |
         */
        ma1.setRoom(solution.getRoomList().get(1));
        MeetingAssignment ma2 = solution.getMeetingAssignmentList().get(2);
        ma2.setStartingTimeGrain(solution.getTimeGrainList().get(4));
        ma2.setRoom(solution.getRoomList().get(0));
        scoreVerifier.assertSoftWeight("Room stability", -constraintConfiguration.getRoomStability().softScore() * 2,
                solution);
    }

}
