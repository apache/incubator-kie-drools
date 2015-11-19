/*
 * Copyright 2015 JBoss Inc
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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.curriculumcourse.domain.Room;
import org.optaplanner.examples.meetingscheduling.domain.Meeting;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
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
                    "on stop shop",
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
        writeMeetingSchedule(200, 5);
        writeMeetingSchedule(400, 5);
        writeMeetingSchedule(800, 5);
    }

    private void writeMeetingSchedule(int meetingListSize, int roomListSize) {
        String fileName = determineFileName(meetingListSize, roomListSize);
        File outputFile = new File(outputDir, fileName + ".xml");
        MeetingSchedule meetingSchedule = createMeetingSchedule(fileName, meetingListSize, roomListSize);
        solutionDao.writeSolution(meetingSchedule, outputFile);
    }

    public MeetingSchedule createMeetingSchedule(int computerListSize, int processListSize) {
        return createMeetingSchedule(determineFileName(computerListSize, processListSize),
                computerListSize, processListSize);
    }

    private String determineFileName(int meetingListSize, int roomListSize) {
        return meetingListSize + "meetings-" + roomListSize + "rooms";
    }

    public MeetingSchedule createMeetingSchedule(String fileName, int meetingListSize, int roomListSize) {
        random = new Random(37);
        MeetingSchedule meetingSchedule = new MeetingSchedule();
        meetingSchedule.setId(0L);

        createMeetingList(meetingSchedule, meetingListSize);
        createRoomList(meetingSchedule, roomListSize);

        int timeGrainListSize = meetingSchedule.getTimeGrainList().size();
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

    private void createMeetingList(MeetingSchedule meetingSchedule, int meetingListSize) {
        List<Meeting> meetingList = new ArrayList<Meeting>(meetingListSize);
        for (int i = 0; i < meetingListSize; i++) {
            Meeting meeting = new Meeting();
            meeting.setId((long) i);
            String topic = generateStringFromPartOptions(topicPartOptions, i);
            meeting.setTopic(topic);
            int durationInGrains = durationInGrainsOptions[random.nextInt(durationInGrainsOptions.length)];
            meeting.setDurationInGrains(durationInGrains);
            logger.trace("Created meeting with topic ({}), durationInGrains ({}).",
                    topic, durationInGrains);
            meetingList.add(meeting);
        }
        meetingSchedule.setMeetingList(meetingList);
    }

    private void createRoomList(MeetingSchedule meetingSchedule, int roomListSize) {
        List<Room> roomList = new ArrayList<Room>(roomListSize);
        for (int i = 0; i < roomListSize; i++) {
            Room room = new Room();
            room.setId((long) i);
            int roomsPerFloor = 20;
            String code = "R " + ((i / roomsPerFloor * 100) + (i % roomsPerFloor) + 1);
            room.setCode(code);
            int capacityOptionsSubsetSize = personsPerMeetingOptions.length * 3 / 4;
            int capacity = personsPerMeetingOptions[personsPerMeetingOptions.length - (i % capacityOptionsSubsetSize) - 1];
            room.setCapacity(capacity);
            logger.trace("Created room with code ({}), capacity ({}).",
                    code, capacity);
            roomList.add(room);
        }
        meetingSchedule.setRoomList(roomList);
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
