/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.conferencescheduling.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.StringDataGenerator;
import org.optaplanner.examples.conferencescheduling.app.ConferenceSchedulingApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class ConferenceSchedulingGenerator extends LoggingMain {

    public static void main(String[] args) {
        ConferenceSchedulingGenerator generator = new ConferenceSchedulingGenerator();
        generator.writeConferenceSolution(50, 5);
        generator.writeConferenceSolution(100, 10);
//        generator.writeMeetingSchedule(200, 5);
//        generator.writeMeetingSchedule(400, 5);
//        generator.writeMeetingSchedule(800, 5);
    }
    private final StringDataGenerator conferenceNameGenerator = new StringDataGenerator()
            .addPart(true, 0,
                    "Javoxx",
                    "Red Bonnet Summit",
                    "JayFocus",
                    "YCon",
                    "JAQ")
            .addPart(false, 0,
                    "2021",
                    "2022",
                    "2023",
                    "2024",
                    "2025");

    private final List<Pair<String, Double>> roomTagProbabilityList = Arrays.asList(
            Pair.of("Large", 0.20),
            Pair.of("Lab", 0.10),
            Pair.of("Recorded", 0.50)
    );

    private final StringDataGenerator speakerNameGenerator = StringDataGenerator.buildFullNames();

    private final StringDataGenerator talkTitleGenerator = new StringDataGenerator()
            .addPart(true, 0,
                    "Hands on",
                    "Advanced",
                    "Learn",
                    "Intro to",
                    "Discover",
                    "Mastering",
                    "Tuning",
                    "Building",
                    "Securing",
                    "Debug")
            .addPart(true, 0,
                    "real-time",
                    "containerized",
                    "virtualized",
                    "serverless",
                    "AI-driven",
                    "machine learning",
                    "IOT-driven",
                    "deep learning",
                    "scalable",
                    "enterprise")
            .addPart(true, 1,
                    "OpenShift",
                    "WildFly",
                    "Spring",
                    "Drools",
                    "OptaPlanner",
                    "jBPM",
                    "Camel",
                    "XStream",
                    "Docker",
                    "JUnit")
            .addPart(false, 3,
                    "in a nutshell",
                    "in practice",
                    "for dummies",
                    "in action",
                    "recipes",
                    "on the web",
                    "for decision makers",
                    "on the whiteboard",
                    "out of the box",
                    "for programmers");

    private final int[] durationInMinutesOptions = {
            30, // 30 mins
            60, // 1 hour
            120, // 2 hours
    };


    protected final SolutionFileIO<ConferenceSolution> solutionFileIO;
    protected final File outputDir;
    protected Random random;

    public ConferenceSchedulingGenerator() {
        solutionFileIO = new ConferenceSchedulingXslxFileIO();
        outputDir = new File(CommonApp.determineDataDir(ConferenceSchedulingApp.DATA_DIR_NAME), "unsolved");
    }

    private void writeConferenceSolution(int talkListSize, int roomListSize) {
        int speakerListSize = talkListSize * 2 / 3;

        String fileName = talkListSize + "talks-" + roomListSize + "rooms";
        File outputFile = new File(outputDir, fileName + "." + solutionFileIO.getOutputFileExtension());
        ConferenceSolution solution = createConferenceSolution(fileName, roomListSize, speakerListSize, talkListSize);
        solutionFileIO.write(solution, outputFile);
    }

    public ConferenceSolution createConferenceSolution(String fileName, int roomListSize, int speakerListSize, int talkListSize) {
        random = new Random(37);
        ConferenceSolution solution = new ConferenceSolution();
        solution.setId(0L);
        solution.setName(conferenceNameGenerator.generateNextValue());

        createRoomList(solution, roomListSize);
        createSpeakerList(solution, speakerListSize);
        createTalkList(solution, talkListSize);


//        createMeetingListAndAttendanceList(solution, meetingListSize);
//        createTimeGrainList(solution, timeGrainListSize);
//        createPersonList(solution);
//        linkAttendanceListToPersons(solution);
//        createMeetingAssignmentList(solution);

//        BigInteger possibleSolutionSize = BigInteger.valueOf((long) timeGrainListSize * roomListSize)
//                .pow(solution.getMeetingAssignmentList().size());
//        logger.info("MeetingSchedule {} has {} meetings, {} timeGrains and {} rooms with a search space of {}.",
//                fileName,
//                meetingListSize,
//                timeGrainListSize,
//                roomListSize,
//                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return solution;
    }

    private void createRoomList(ConferenceSolution solution, int roomListSize) {
        final int roomsPerFloor = 12;
        List<Room> roomList = new ArrayList<>(roomListSize);
        for (int i = 0; i < roomListSize; i++) {
            Room room = new Room();
            room.setId((long) i);
            room.setName("R " + ((i / roomsPerFloor * 100) + (i % roomsPerFloor) + 1));
            Set<String> roomTagSet = new LinkedHashSet<>(roomTagProbabilityList.size());
            for (Pair<String, Double> roomTagProbability : roomTagProbabilityList) {
                if (random.nextDouble() < roomTagProbability.getValue()) {
                    roomTagSet.add(roomTagProbability.getKey());
                }
            }
            room.setRoomTagSet(roomTagSet);
            logger.trace("Created room with name ({}) and tags ({}).",
                    room.getName(), roomTagSet);
            roomList.add(room);
        }
        solution.setRoomList(roomList);
    }

    private void createSpeakerList(ConferenceSolution solution, int speakerListSize) {
        List<Speaker> speakerList = new ArrayList<>(speakerListSize);
        speakerNameGenerator.predictMaximumSizeAndReset(speakerListSize);
        for (int i = 0; i < speakerListSize; i++) {
            Speaker speaker = new Speaker();
            speaker.setId((long) i);
            speaker.setName(speakerNameGenerator.generateNextValue());
//            Set<String> speakerTagSet = new LinkedHashSet<>(speakerTagProbabilityList.size());
//            for (Pair<String, Double> speakerTagProbability : speakerTagProbabilityList) {
//                if (random.nextDouble() < speakerTagProbability.getValue()) {
//                    speakerTagSet.add(speakerTagProbability.getKey());
//                }
//            }
//            speaker.setSpeakerTagSet(speakerTagSet);
            logger.trace("Created speaker with name ({}).",
                    speaker.getName());
            speakerList.add(speaker);
        }
        solution.setSpeakerList(speakerList);
    }

    private void createTalkList(ConferenceSolution solution, int talkListSize) {
        List<Talk> talkList = new ArrayList<>(talkListSize);
        talkTitleGenerator.predictMaximumSizeAndReset(talkListSize);
        int speakerListIndex = 0;
        for (int i = 0; i < talkListSize; i++) {
            Talk talk = new Talk();
            talk.setId((long) i);
            talk.setCode(String.format("S%0" + ((String.valueOf(talkListSize).length()) + "d"), i));
            talk.setTitle(talkTitleGenerator.generateNextValue());
            double randomDouble = random.nextDouble();
            int speakerCount = (randomDouble < 0.03) ? 4 :
                    (randomDouble < 0.10) ? 3 :
                    (randomDouble < 0.50) ? 2 : 1;
            List<Speaker> speakerList = new ArrayList<>(speakerCount);
            for (int j = 0; j < speakerCount; j++) {
                speakerList.add(solution.getSpeakerList().get(speakerListIndex));
                speakerListIndex = (speakerListIndex + 1) % solution.getSpeakerList().size();
            }
            talk.setSpeakerList(speakerList);
            logger.trace("Created talk with title ({}).",
                    talk.getTitle());
            talkList.add(talk);
        }
        solution.setTalkList(talkList);
    }

//    private void createMeetingListAndAttendanceList(MeetingSchedule meetingSchedule, int meetingListSize) {
//        List<Meeting> meetingList = new ArrayList<>(meetingListSize);
//        List<Attendance> globalAttendanceList = new ArrayList<>();
//        long attendanceId = 0L;
//        talkTitleGenerator.predictMaximumSizeAndReset(meetingListSize);
//        for (int i = 0; i < meetingListSize; i++) {
//            Meeting meeting = new Meeting();
//            meeting.setId((long) i);
//            String topic = talkTitleGenerator.generateNextValue();
//            meeting.setTopic(topic);
//            int durationInGrains = durationInMinutesOptions[random.nextInt(durationInMinutesOptions.length)];
//            meeting.setDurationInGrains(durationInGrains);
//
//            int attendanceListSize = personsPerMeetingOptions[random.nextInt(personsPerMeetingOptions.length)];
//            int requiredAttendanceListSize = Math.max(2, random.nextInt(attendanceListSize + 1));
//            List<RequiredAttendance> requiredAttendanceList = new ArrayList<>(requiredAttendanceListSize);
//            for (int j = 0; j < requiredAttendanceListSize; j++) {
//                RequiredAttendance attendance = new RequiredAttendance();
//                attendance.setId(attendanceId);
//                attendanceId++;
//                attendance.setMeeting(meeting);
//                // person is filled in later
//                requiredAttendanceList.add(attendance);
//                globalAttendanceList.add(attendance);
//            }
//            meeting.setRequiredAttendanceList(requiredAttendanceList);
//            int preferredAttendanceListSize = attendanceListSize - requiredAttendanceListSize;
//            List<PreferredAttendance> preferredAttendanceList = new ArrayList<>(preferredAttendanceListSize);
//            for (int j = 0; j < preferredAttendanceListSize; j++) {
//                PreferredAttendance attendance = new PreferredAttendance();
//                attendance.setId(attendanceId);
//                attendanceId++;
//                attendance.setMeeting(meeting);
//                // person is filled in later
//                preferredAttendanceList.add(attendance);
//                globalAttendanceList.add(attendance);
//            }
//            meeting.setPreferredAttendanceList(preferredAttendanceList);
//
//            logger.trace("Created meeting with topic ({}), durationInGrains ({}),"
//                    + " requiredAttendanceListSize ({}), preferredAttendanceListSize ({}).",
//                    topic, durationInGrains,
//                    requiredAttendanceListSize, preferredAttendanceListSize);
//            meetingList.add(meeting);
//        }
//        meetingSchedule.setMeetingList(meetingList);
//        meetingSchedule.setAttendanceList(globalAttendanceList);
//    }
//
//    private void createTimeGrainList(MeetingSchedule meetingSchedule, int timeGrainListSize) {
//        List<Day> dayList = new ArrayList<>(timeGrainListSize);
//        long dayId = 0;
//        Day day = null;
//        List<TimeGrain> timeGrainList = new ArrayList<>(timeGrainListSize);
//        for (int i = 0; i < timeGrainListSize; i++) {
//            TimeGrain timeGrain = new TimeGrain();
//            timeGrain.setId((long) i);
//            int grainIndex = i;
//            timeGrain.setGrainIndex(grainIndex);
//            int dayOfYear = (i / startingMinuteOfDayOptions.length) + 1;
//            if (day == null || day.getDayOfYear() != dayOfYear) {
//                day = new Day();
//                day.setId(dayId);
//                day.setDayOfYear(dayOfYear);
//                dayId++;
//                dayList.add(day);
//            }
//            timeGrain.setDay(day);
//            int startingMinuteOfDay = startingMinuteOfDayOptions[i % startingMinuteOfDayOptions.length];
//            timeGrain.setStartingMinuteOfDay(startingMinuteOfDay);
//            logger.trace("Created timeGrain with grainIndex ({}), dayOfYear ({}), startingMinuteOfDay ({}).",
//                    grainIndex, dayOfYear, startingMinuteOfDay);
//            timeGrainList.add(timeGrain);
//        }
//        meetingSchedule.setDayList(dayList);
//        meetingSchedule.setTimeGrainList(timeGrainList);
//    }
//
//    private void createPersonList(MeetingSchedule meetingSchedule) {
//        int attendanceListSize = 0;
//        for (Meeting meeting : meetingSchedule.getMeetingList()) {
//            attendanceListSize += meeting.getRequiredAttendanceList().size()
//                    + meeting.getPreferredAttendanceList().size();
//        }
//        int personListSize = attendanceListSize * meetingSchedule.getRoomList().size() * 3
//                / (4 * meetingSchedule.getMeetingList().size());
//        List<Person> personList = new ArrayList<>(personListSize);
//        fullNameGenerator.predictMaximumSizeAndReset(personListSize);
//        for (int i = 0; i < personListSize; i++) {
//            Person person = new Person();
//            person.setId((long) i);
//            String fullName = fullNameGenerator.generateNextValue();
//            person.setFullName(fullName);
//            logger.trace("Created person with fullName ({}).",
//                    fullName);
//            personList.add(person);
//        }
//        meetingSchedule.setPersonList(personList);
//    }
//
//    private void linkAttendanceListToPersons(MeetingSchedule meetingSchedule) {
//        for (Meeting meeting : meetingSchedule.getMeetingList()) {
//            List<Person> availablePersonList = new ArrayList<>(meetingSchedule.getPersonList());
//            int attendanceListSize = meeting.getRequiredAttendanceList().size() + meeting.getPreferredAttendanceList().size();
//            if (availablePersonList.size() < attendanceListSize) {
//                throw new IllegalStateException("The availablePersonList size (" + availablePersonList.size()
//                        + ") is less than the attendanceListSize (" + attendanceListSize + ").");
//            }
//            for (RequiredAttendance requiredAttendance : meeting.getRequiredAttendanceList()) {
//                requiredAttendance.setPerson(availablePersonList.remove(random.nextInt(availablePersonList.size())));
//            }
//            for (PreferredAttendance preferredAttendance : meeting.getPreferredAttendanceList()) {
//                preferredAttendance.setPerson(availablePersonList.remove(random.nextInt(availablePersonList.size())));
//            }
//        }
//    }
//
//    private void createMeetingAssignmentList(MeetingSchedule meetingSchedule) {
//        List<Meeting> meetingList = meetingSchedule.getMeetingList();
//        List<MeetingAssignment> meetingAssignmentList = new ArrayList<>(meetingList.size());
//        for (Meeting meeting : meetingList) {
//            MeetingAssignment meetingAssignment = new MeetingAssignment();
//            meetingAssignment.setId(meeting.getId());
//            meetingAssignment.setMeeting(meeting);
//            meetingAssignmentList.add(meetingAssignment);
//        }
//        meetingSchedule.setMeetingAssignmentList(meetingAssignmentList);
//    }

}
