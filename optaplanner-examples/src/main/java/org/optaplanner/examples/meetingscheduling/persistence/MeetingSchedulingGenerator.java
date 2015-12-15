/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.meetingscheduling.persistence;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.meetingscheduling.domain.Attendance;
import org.optaplanner.examples.meetingscheduling.domain.Day;
import org.optaplanner.examples.meetingscheduling.domain.Meeting;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.domain.Person;
import org.optaplanner.examples.meetingscheduling.domain.PreferredAttendance;
import org.optaplanner.examples.meetingscheduling.domain.RequiredAttendance;
import org.optaplanner.examples.meetingscheduling.domain.Room;
import org.optaplanner.examples.meetingscheduling.domain.TimeGrain;

public class MeetingSchedulingGenerator extends LoggingMain {

    private static final String[][] topicPartOptions = {
            new String[] {
                    "Strategize",
                    "Fast track",
                    "Cross sell",
                    "Profitize",
                    "Transform",
                    "Engage",
                    "Downsize",
                    "Ramp up",
                    "On board",
                    "Reinvigorate"
            },
            new String[] {
                    "data driven",
                    "sales driven",
                    "compelling",
                    "reusable",
                    "negotiated",
                    "sustainable",
                    "laser-focused",
                    "flexible",
                    "real-time",
                    "targeted"
            },
            new String[] {
                    "B2B",
                    "e-business",
                    "virtualization",
                    "multitasking",
                    "one stop shop",
                    "braindumps",
                    "data mining",
                    "policies",
                    "synergies",
                    "user experience"
            },
            new String[] {
                    "in a nutshell",
                    "in practice",
                    "for dummies",
                    "in action",
                    "recipes",
                    "on the web",
                    "for decision makers",
                    "on the whiteboard",
                    "out of the box",
                    "in the new economy"
            }
    };

    private static final int[] durationInGrainsOptions = {
            1, // 15 mins
            2, // 30 mins
            3, // 45 mins
            4, // 1 hour
            6, // 90 mins
            8, // 2 hours
            16, // 4 hours
    };

    private static final int[] personsPerMeetingOptions = {
            2,
            3,
            4,
            5,
            6,
            8,
            10,
            12,
            14,
            16,
            20,
            30,
    };

    private static final int[] startingMinuteOfDayOptions = {
            8 * 60, // 08:00
            8 * 60 + 15, // 08:15
            8 * 60 + 30, // 08:30
            8 * 60 + 45, // 08:45
            9 * 60, // 09:00
            9 * 60 + 15, // 09:15
            9 * 60 + 30, // 09:30
            9 * 60 + 45, // 09:45
            10 * 60, // 10:00
            10 * 60 + 15, // 10:15
            10 * 60 + 30, // 10:30
            10 * 60 + 45, // 10:45
            11 * 60, // 11:00
            11 * 60 + 15, // 11:15
            11 * 60 + 30, // 11:30
            11 * 60 + 45, // 11:45
            13 * 60, // 13:00
            13 * 60 + 15, // 13:15
            13 * 60 + 30, // 13:30
            13 * 60 + 45, // 13:45
            14 * 60, // 14:00
            14 * 60 + 15, // 14:15
            14 * 60 + 30, // 14:30
            14 * 60 + 45, // 14:45
            15 * 60, // 15:00
            15 * 60 + 15, // 15:15
            15 * 60 + 30, // 15:30
            15 * 60 + 45, // 15:45
            16 * 60, // 16:00
            16 * 60 + 15, // 16:15
            16 * 60 + 30, // 16:30
            16 * 60 + 45, // 16:45
            17 * 60, // 17:00
            17 * 60 + 15, // 17:15
            17 * 60 + 30, // 17:30
            17 * 60 + 45, // 17:45
    };

    private static final String[][] fullNamePartOptions = {
            new String[] {
                    "Geoff",
                    "Mark",
                    "Edson",
                    "Ondrej",
                    "Lukas",
                    "Vicky",
                    "Shelly",
                    "Peter",
                    "Micha",
                    "Steph",
            },
            new String[] {
                    "A.",
                    "B.",
                    "C.",
                    "D.",
                    "E.",
                    "F.",
                    "G.",
                    "H.",
                    "I.",
                    "J.",
            },
            new String[] {
                    "O.",
                    "P.",
                    "Q.",
                    "R.",
                    "S.",
                    "T.",
                    "U.",
                    "V.",
                    "W.",
                    "X",
            },
            new String[] {
                    "Smet",
                    "Proc",
                    "Fusco",
                    "Skop",
                    "Davis",
                    "Smith",
                    "Gowan",
                    "Siro",
                    "Kief",
                    "Snos",
            }
    };

    public static void main(String[] args) {
        new MeetingSchedulingGenerator().generate();
    }

    protected final SolutionDao solutionDao;
    protected final File outputDir;
    protected Random random;

    public MeetingSchedulingGenerator() {
        solutionDao = new MeetingSchedulingDao();
        outputDir = new File(solutionDao.getDataDir(), "unsolved");
    }

    public void generate() {
        writeMeetingSchedule(50, 5);
        writeMeetingSchedule(100, 5);
//        writeMeetingSchedule(200, 5);
//        writeMeetingSchedule(400, 5);
//        writeMeetingSchedule(800, 5);
    }

    private void writeMeetingSchedule(int meetingListSize, int roomListSize) {
        int timeGrainListSize = meetingListSize * durationInGrainsOptions[durationInGrainsOptions.length - 1] / roomListSize;
        String fileName = determineFileName(meetingListSize, timeGrainListSize, roomListSize);
        File outputFile = new File(outputDir, fileName + ".xml");
        MeetingSchedule meetingSchedule = createMeetingSchedule(fileName, meetingListSize, timeGrainListSize, roomListSize);
        solutionDao.writeSolution(meetingSchedule, outputFile);
    }

    private String determineFileName(int meetingListSize, int timeGrainListSize, int roomListSize) {
        return meetingListSize + "meetings-" + timeGrainListSize + "timegrains-" + roomListSize + "rooms";
    }

    public MeetingSchedule createMeetingSchedule(String fileName, int meetingListSize, int timeGrainListSize, int roomListSize) {
        random = new Random(37);
        MeetingSchedule meetingSchedule = new MeetingSchedule();
        meetingSchedule.setId(0L);

        createMeetingListAndAttendanceList(meetingSchedule, meetingListSize);
        createTimeGrainList(meetingSchedule, timeGrainListSize);
        createRoomList(meetingSchedule, roomListSize);
        createPersonList(meetingSchedule);
        linkAttendanceListToPersons(meetingSchedule);
        createMeetingAssignmentList(meetingSchedule);

        BigInteger possibleSolutionSize = BigInteger.valueOf(timeGrainListSize * roomListSize)
                .pow(meetingSchedule.getMeetingAssignmentList().size());
        logger.info("MeetingSchedule {} has {} meetings, {} timeGrains and {} rooms with a search space of {}.",
                fileName,
                meetingListSize,
                timeGrainListSize,
                roomListSize,
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return meetingSchedule;
    }

    private void createMeetingListAndAttendanceList(MeetingSchedule meetingSchedule, int meetingListSize) {
        List<Meeting> meetingList = new ArrayList<Meeting>(meetingListSize);
        List<Attendance> globalAttendanceList = new ArrayList<Attendance>();
        long attendanceId = 0L;
        for (int i = 0; i < meetingListSize; i++) {
            Meeting meeting = new Meeting();
            meeting.setId((long) i);
            String topic = generateStringFromPartOptions(topicPartOptions, i);
            meeting.setTopic(topic);
            int durationInGrains = durationInGrainsOptions[random.nextInt(durationInGrainsOptions.length)];
            meeting.setDurationInGrains(durationInGrains);

            int attendanceListSize = personsPerMeetingOptions[random.nextInt(personsPerMeetingOptions.length)];
            int requiredAttendanceListSize = Math.max(2, random.nextInt(attendanceListSize + 1));
            List<RequiredAttendance> requiredAttendanceList = new ArrayList<RequiredAttendance>(requiredAttendanceListSize);
            for (int j = 0; j < requiredAttendanceListSize; j++) {
                RequiredAttendance attendance = new RequiredAttendance();
                attendance.setId(attendanceId);
                attendanceId++;
                attendance.setMeeting(meeting);
                // person is filled in later
                requiredAttendanceList.add(attendance);
                globalAttendanceList.add(attendance);
            }
            meeting.setRequiredAttendanceList(requiredAttendanceList);
            int preferredAttendanceListSize = attendanceListSize - requiredAttendanceListSize;
            List<PreferredAttendance> preferredAttendanceList = new ArrayList<PreferredAttendance>(preferredAttendanceListSize);
            for (int j = 0; j < preferredAttendanceListSize; j++) {
                PreferredAttendance attendance = new PreferredAttendance();
                attendance.setId(attendanceId);
                attendanceId++;
                attendance.setMeeting(meeting);
                // person is filled in later
                preferredAttendanceList.add(attendance);
                globalAttendanceList.add(attendance);
            }
            meeting.setPreferredAttendanceList(preferredAttendanceList);

            logger.trace("Created meeting with topic ({}), durationInGrains ({}),"
                    + " requiredAttendanceListSize ({}), preferredAttendanceListSize ({}).",
                    topic, durationInGrains,
                    requiredAttendanceListSize, preferredAttendanceListSize);
            meetingList.add(meeting);
        }
        meetingSchedule.setMeetingList(meetingList);
        meetingSchedule.setAttendanceList(globalAttendanceList);
    }

    private void createTimeGrainList(MeetingSchedule meetingSchedule, int timeGrainListSize) {
        List<Day> dayList = new ArrayList<Day>(timeGrainListSize);
        long dayId = 0;
        Day day = null;
        List<TimeGrain> timeGrainList = new ArrayList<TimeGrain>(timeGrainListSize);
        for (int i = 0; i < timeGrainListSize; i++) {
            TimeGrain timeGrain = new TimeGrain();
            timeGrain.setId((long) i);
            int grainIndex = i;
            timeGrain.setGrainIndex(grainIndex);
            int dayOfYear = (i / startingMinuteOfDayOptions.length) + 1;
            if (day == null || day.getDayOfYear() != dayOfYear) {
                day = new Day();
                day.setId(dayId);
                day.setDayOfYear(dayOfYear);
                dayId++;
                dayList.add(day);
            }
            timeGrain.setDay(day);
            int startingMinuteOfDay = startingMinuteOfDayOptions[i % startingMinuteOfDayOptions.length];
            timeGrain.setStartingMinuteOfDay(startingMinuteOfDay);
            logger.trace("Created timeGrain with grainIndex ({}), dayOfYear ({}), startingMinuteOfDay ({}).",
                    grainIndex, dayOfYear, startingMinuteOfDay);
            timeGrainList.add(timeGrain);
        }
        meetingSchedule.setDayList(dayList);
        meetingSchedule.setTimeGrainList(timeGrainList);
    }

    private void createRoomList(MeetingSchedule meetingSchedule, int roomListSize) {
        List<Room> roomList = new ArrayList<Room>(roomListSize);
        for (int i = 0; i < roomListSize; i++) {
            Room room = new Room();
            room.setId((long) i);
            int roomsPerFloor = 20;
            String name = "R " + ((i / roomsPerFloor * 100) + (i % roomsPerFloor) + 1);
            room.setName(name);
            int capacityOptionsSubsetSize = personsPerMeetingOptions.length * 3 / 4;
            int capacity = personsPerMeetingOptions[personsPerMeetingOptions.length - (i % capacityOptionsSubsetSize) - 1];
            room.setCapacity(capacity);
            logger.trace("Created room with name ({}), capacity ({}).",
                    name, capacity);
            roomList.add(room);
        }
        meetingSchedule.setRoomList(roomList);
    }

    private void createPersonList(MeetingSchedule meetingSchedule) {
        int attendanceListSize = 0;
        for (Meeting meeting : meetingSchedule.getMeetingList()) {
            attendanceListSize += meeting.getRequiredAttendanceList().size()
                    + meeting.getPreferredAttendanceList().size();
        }
        int personListSize = attendanceListSize * meetingSchedule.getRoomList().size() * 3
                / (4 * meetingSchedule.getMeetingList().size());
        List<Person> personList = new ArrayList<Person>(personListSize);
        for (int i = 0; i < personListSize; i++) {
            Person person = new Person();
            person.setId((long) i);
            String fullName = generateStringFromPartOptions(fullNamePartOptions, i);
            person.setFullName(fullName);
            logger.trace("Created person with fullName ({}).",
                    fullName);
            personList.add(person);
        }
        meetingSchedule.setPersonList(personList);
    }

    private void linkAttendanceListToPersons(MeetingSchedule meetingSchedule) {
        for (Meeting meeting : meetingSchedule.getMeetingList()) {
            List<Person> availablePersonList = new ArrayList<Person>(meetingSchedule.getPersonList());
            int attendanceListSize = meeting.getRequiredAttendanceList().size() + meeting.getPreferredAttendanceList().size();
            if (availablePersonList.size() < attendanceListSize) {
                throw new IllegalStateException("The availablePersonList size (" + availablePersonList.size()
                        + ") is less than the attendanceListSize (" + attendanceListSize + ").");
            }
            for (RequiredAttendance requiredAttendance : meeting.getRequiredAttendanceList()) {
                requiredAttendance.setPerson(availablePersonList.remove(random.nextInt(availablePersonList.size())));
            }
            for (PreferredAttendance preferredAttendance : meeting.getPreferredAttendanceList()) {
                preferredAttendance.setPerson(availablePersonList.remove(random.nextInt(availablePersonList.size())));
            }
        }
    }

    private void createMeetingAssignmentList(MeetingSchedule meetingSchedule) {
        List<Meeting> meetingList = meetingSchedule.getMeetingList();
        List<MeetingAssignment> meetingAssignmentList = new ArrayList<MeetingAssignment>(meetingList.size());
        for (Meeting meeting : meetingList) {
            MeetingAssignment meetingAssignment = new MeetingAssignment();
            meetingAssignment.setId(meeting.getId());
            meetingAssignment.setMeeting(meeting);
            meetingAssignmentList.add(meetingAssignment);
        }
        meetingSchedule.setMeetingAssignmentList(meetingAssignmentList);
    }

    private String generateStringFromPartOptions(String[][] partOptions, int index) {
        if (partOptions.length != 4) {
            throw new IllegalStateException("The partOptions length (" + partOptions.length + ") is invalid.");
        }
        final int partLength = partOptions[0].length;
        for (int i = 0; i < partOptions.length; i++) {
            if (partOptions[i].length != partLength) {
                throw new IllegalStateException("The partOptions[" + i + "] length (" + partOptions[i].length
                        + ") is not the same as the partOptions[0] length (" + partLength + ").");
            }
        }
        if (index > (int) Math.pow(partLength, partOptions.length)) {
            throw new IllegalStateException("The index (" + index + ") is invalid.");
        }
        int firstIndex = index % partLength;
        int secondIndex = (firstIndex + (index % (int) Math.pow(partLength, 3) / (int) Math.pow(partLength, 2)))
                % partLength;
        int thirdIndex = (secondIndex + (index % (int) Math.pow(partLength, 2) / (int) partLength))
                % partLength;
        int fourthIndex = (thirdIndex + (index / (int) Math.pow(partLength, 3)))
                % partLength;
        return partOptions[0][firstIndex] + " " + partOptions[1][secondIndex]
                + " " + partOptions[2][thirdIndex] + " " + partOptions[3][fourthIndex];
    }

}
