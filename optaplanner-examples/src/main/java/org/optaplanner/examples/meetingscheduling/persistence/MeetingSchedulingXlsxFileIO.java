package org.optaplanner.examples.meetingscheduling.persistence;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.ASSIGN_LARGER_ROOMS_FIRST;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.DONT_GO_IN_OVERTIME;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.OVERLAPPING_MEETINGS;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.PREFERRED_ATTENDANCE_CONFLICT;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.REQUIRED_AND_PREFERRED_ATTENDANCE_CONFLICT;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.REQUIRED_ATTENDANCE_CONFLICT;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.REQUIRED_ROOM_CAPACITY;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.ROOM_CONFLICT;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.ROOM_STABILITY;
import static org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration.START_AND_END_ON_SAME_DAY;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

import org.apache.commons.text.WordUtils;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionFileIO;
import org.optaplanner.examples.meetingscheduling.app.MeetingSchedulingApp;
import org.optaplanner.examples.meetingscheduling.domain.Attendance;
import org.optaplanner.examples.meetingscheduling.domain.Day;
import org.optaplanner.examples.meetingscheduling.domain.Meeting;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.domain.Person;
import org.optaplanner.examples.meetingscheduling.domain.PreferredAttendance;
import org.optaplanner.examples.meetingscheduling.domain.RequiredAttendance;
import org.optaplanner.examples.meetingscheduling.domain.Room;
import org.optaplanner.examples.meetingscheduling.domain.TimeGrain;

public class MeetingSchedulingXlsxFileIO extends AbstractXlsxSolutionFileIO<MeetingSchedule> {

    @Override
    public MeetingSchedule read(File inputScheduleFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputScheduleFile))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            return new MeetingSchedulingXlsxReader(workbook).read();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputScheduleFile ("
                    + inputScheduleFile + ").", e);
        }
    }

    private static class MeetingSchedulingXlsxReader extends AbstractXlsxReader<MeetingSchedule, HardMediumSoftScore> {

        MeetingSchedulingXlsxReader(XSSFWorkbook workbook) {
            super(workbook, MeetingSchedulingApp.SOLVER_CONFIG);
        }

        @Override
        public MeetingSchedule read() {
            solution = new MeetingSchedule();
            readConfiguration();
            readDayList();
            readRoomList();
            readPersonList();
            readMeetingList();

            return solution;
        }

        private void readConfiguration() {
            nextSheet("Configuration");
            nextRow();
            nextRow(true);
            readHeaderCell("Constraint");
            readHeaderCell("Weight");
            readHeaderCell("Description");

            MeetingConstraintConfiguration constraintConfiguration = new MeetingConstraintConfiguration(0L);

            // TODO refactor this to allow setting pos/neg, weight and score level
            readIntConstraintParameterLine(ROOM_CONFLICT,
                    hardScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(DONT_GO_IN_OVERTIME,
                    hardScore -> constraintConfiguration.setDontGoInOvertime(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(REQUIRED_ATTENDANCE_CONFLICT,
                    hardScore -> constraintConfiguration.setRequiredAttendanceConflict(HardMediumSoftScore.ofHard(hardScore)),
                    "");
            readIntConstraintParameterLine(REQUIRED_ROOM_CAPACITY,
                    hardScore -> constraintConfiguration.setRequiredRoomCapacity(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(START_AND_END_ON_SAME_DAY,
                    hardScore -> constraintConfiguration.setStartAndEndOnSameDay(HardMediumSoftScore.ofHard(hardScore)), "");

            readIntConstraintParameterLine(REQUIRED_AND_PREFERRED_ATTENDANCE_CONFLICT, mediumScore -> constraintConfiguration
                    .setRequiredAndPreferredAttendanceConflict(HardMediumSoftScore.ofMedium(mediumScore)), "");
            readIntConstraintParameterLine(PREFERRED_ATTENDANCE_CONFLICT, mediumScore -> constraintConfiguration
                    .setPreferredAttendanceConflict(HardMediumSoftScore.ofMedium(mediumScore)), "");

            readIntConstraintParameterLine(DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE, softScore -> constraintConfiguration
                    .setDoAllMeetingsAsSoonAsPossible(HardMediumSoftScore.ofSoft(softScore)), "");
            readIntConstraintParameterLine(ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS,
                    softScore -> constraintConfiguration
                            .setOneTimeGrainBreakBetweenTwoConsecutiveMeetings(HardMediumSoftScore.ofSoft(softScore)),
                    "");
            readIntConstraintParameterLine(OVERLAPPING_MEETINGS,
                    softScore -> constraintConfiguration.setOverlappingMeetings(HardMediumSoftScore.ofSoft(softScore)), "");
            readIntConstraintParameterLine(ASSIGN_LARGER_ROOMS_FIRST,
                    softScore -> constraintConfiguration.setAssignLargerRoomsFirst(HardMediumSoftScore.ofSoft(softScore)), "");
            readIntConstraintParameterLine(ROOM_STABILITY,
                    softScore -> constraintConfiguration.setRoomStability(HardMediumSoftScore.ofSoft(softScore)), "");

            solution.setConstraintConfiguration(constraintConfiguration);
        }

        private void readPersonList() {
            nextSheet("Persons");
            nextRow(false);
            readHeaderCell("Full name");
            List<Person> personList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Person person = new Person(id++, nextStringCell().getStringCellValue());
                if (!VALID_NAME_PATTERN.matcher(person.getFullName()).matches()) {
                    throw new IllegalStateException(
                            currentPosition() + ": The person name (" + person.getFullName()
                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
                personList.add(person);
            }
            solution.setPersonList(personList);
        }

        private void readMeetingList() {
            Map<String, Person> personMap = solution.getPersonList().stream().collect(
                    toMap(Person::getFullName, person -> person));
            nextSheet("Meetings");
            nextRow(false);
            readHeaderCell("Topic");
            readHeaderCell("Group");
            readHeaderCell("Duration");
            readHeaderCell("Speakers");
            readHeaderCell("Content");
            readHeaderCell("Required attendance list");
            readHeaderCell("Preferred attendance list");
            readHeaderCell("Day");
            readHeaderCell("Starting time");
            readHeaderCell("Room");

            List<Meeting> meetingList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            List<MeetingAssignment> meetingAssignmentList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            List<Attendance> attendanceList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            AtomicLong attendanceIdCounter = new AtomicLong();
            long meetingId = 0L;
            long meetingAssignmentId = 0L;
            Map<LocalDateTime, TimeGrain> timeGrainMap = solution.getTimeGrainList().stream().collect(
                    Collectors.toMap(TimeGrain::getDateTime, Function.identity()));
            Map<String, Room> roomMap = solution.getRoomList().stream().collect(
                    Collectors.toMap(Room::getName, Function.identity()));

            while (nextRow()) {
                Meeting meeting = new Meeting(meetingId++);
                List<Attendance> speakerAttendanceList = new ArrayList<>();
                Set<Person> speakerSet = new HashSet<>();
                MeetingAssignment meetingAssignment = new MeetingAssignment(meetingAssignmentId++);

                meeting.setTopic(nextStringCell().getStringCellValue());
                meeting.setEntireGroupMeeting(nextStringCell().getStringCellValue().equalsIgnoreCase("y"));
                readMeetingDuration(meeting);
                readSpeakerList(personMap, meeting, speakerAttendanceList, speakerSet, attendanceIdCounter);
                meeting.setContent(nextStringCell().getStringCellValue());

                if (meeting.isEntireGroupMeeting()) {
                    List<RequiredAttendance> requiredAttendanceList = new ArrayList<>(solution.getPersonList().size());
                    for (Person person : solution.getPersonList()) {
                        RequiredAttendance requiredAttendance =
                                createAttendance(attendanceIdCounter, id -> new RequiredAttendance(id, meeting));
                        requiredAttendance.setPerson(person);
                        requiredAttendanceList.add(requiredAttendance);
                        attendanceList.add(requiredAttendance);
                    }
                    meeting.setRequiredAttendanceList(requiredAttendanceList);
                    meeting.setPreferredAttendanceList(new ArrayList<>());
                } else {
                    attendanceList.addAll(speakerAttendanceList);
                    List<Attendance> meetingAttendanceList =
                            getAttendanceLists(meeting, personMap, speakerSet, attendanceIdCounter);
                    attendanceList.addAll(meetingAttendanceList);
                }
                meetingAssignment.setStartingTimeGrain(extractTimeGrain(meeting, timeGrainMap));
                meetingAssignment.setRoom(extractRoom(meeting, roomMap));
                meetingList.add(meeting);
                meetingAssignment.setMeeting(meeting);
                meetingAssignmentList.add(meetingAssignment);
            }
            solution.setMeetingList(meetingList);
            solution.setMeetingAssignmentList(meetingAssignmentList);
            solution.setAttendanceList(attendanceList);
        }

        private void readSpeakerList(Map<String, Person> personMap, Meeting meeting, List<Attendance> speakerAttendanceList,
                Set<Person> speakerSet, AtomicLong attendanceIdCounter) {
            meeting.setSpeakerList(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                    .filter(speaker -> !speaker.isEmpty())
                    .map(speakerName -> {
                        Person speaker = personMap.get(speakerName);
                        if (speaker == null) {
                            throw new IllegalStateException(
                                    currentPosition() + ": The meeting with id (" + meeting.getId()
                                            + ") has a speaker (" + speakerName + ") that doesn't exist in the Persons list.");
                        }
                        if (speakerSet.contains(speaker)) {
                            throw new IllegalStateException(
                                    currentPosition() + ": The meeting with id (" + meeting.getId()
                                            + ") has a duplicate speaker (" + speakerName + ").");
                        }
                        speakerSet.add(speaker);
                        RequiredAttendance speakerAttendance =
                                createAttendance(attendanceIdCounter, id -> new RequiredAttendance(id, meeting));
                        speakerAttendance.setMeeting(meeting);
                        speakerAttendance.setPerson(speaker);
                        speakerAttendanceList.add(speakerAttendance);
                        return speaker;
                    }).collect(toList()));
        }

        private <E extends Attendance> E createAttendance(AtomicLong idCounter, LongFunction<E> constructor) {
            return constructor.apply(idCounter.getAndIncrement());
        }

        private void readMeetingDuration(Meeting meeting) {
            double durationDouble = nextNumericCell().getNumericCellValue();
            if (durationDouble <= 0 || durationDouble != Math.floor(durationDouble)) {
                throw new IllegalStateException(
                        currentPosition() + ": The meeting with id (" + meeting.getId()
                                + ")'s has a duration (" + durationDouble + ") that isn't a strictly positive integer number.");
            }
            if (durationDouble % TimeGrain.GRAIN_LENGTH_IN_MINUTES != 0) {
                throw new IllegalStateException(
                        currentPosition() + ": The meeting with id (" + meeting.getId()
                                + ") has a duration (" + durationDouble + ") that isn't a multiple of "
                                + TimeGrain.GRAIN_LENGTH_IN_MINUTES + ".");
            }
            meeting.setDurationInGrains((int) durationDouble / TimeGrain.GRAIN_LENGTH_IN_MINUTES);
        }

        private List<Attendance> getAttendanceLists(Meeting meeting, Map<String, Person> personMap, Set<Person> speakerSet,
                AtomicLong attendanceIdCounter) {
            List<Attendance> attendanceList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            Set<Person> requiredPersonSet = new HashSet<>();

            List<RequiredAttendance> requiredAttendanceList =
                    getRequiredAttendanceList(meeting, personMap, speakerSet, requiredPersonSet, attendanceIdCounter);
            meeting.setRequiredAttendanceList(requiredAttendanceList);
            attendanceList.addAll(requiredAttendanceList);

            List<PreferredAttendance> preferredAttendanceList =
                    getPreferredAttendanceList(meeting, personMap, speakerSet, requiredPersonSet, attendanceIdCounter);
            meeting.setPreferredAttendanceList(preferredAttendanceList);
            attendanceList.addAll(preferredAttendanceList);

            return attendanceList;
        }

        private List<RequiredAttendance> getRequiredAttendanceList(Meeting meeting, Map<String, Person> personMap,
                Set<Person> speakerSet, Set<Person> requiredPersonSet, AtomicLong attendanceIdCounter) {
            return Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                    .filter(requiredAttendee -> !requiredAttendee.isEmpty())
                    .map(personName -> {
                        RequiredAttendance requiredAttendance =
                                createAttendance(attendanceIdCounter, id -> new RequiredAttendance(id, meeting));
                        Person person = personMap.get(personName);
                        if (person == null) {
                            throw new IllegalStateException(
                                    currentPosition() + ": The meeting with id (" + meeting.getId()
                                            + ") has a required attendee (" + personName
                                            + ") that doesn't exist in the Persons list.");
                        }
                        if (requiredPersonSet.contains(person)) {
                            throw new IllegalStateException(
                                    currentPosition() + ": The meeting with id (" + meeting.getId()
                                            + ") has a duplicate required attendee (" + personName + ").");
                        }
                        if (speakerSet.contains(person)) {
                            throw new IllegalStateException(
                                    currentPosition() + ": The meeting with id (" + meeting.getId()
                                            + ") has a required attendee  (" + personName + ") who is also the speaker.");
                        }
                        requiredPersonSet.add(person);
                        requiredAttendance.setMeeting(meeting);
                        requiredAttendance.setPerson(person);
                        return requiredAttendance;
                    })
                    .collect(toList());
        }

        private List<PreferredAttendance> getPreferredAttendanceList(Meeting meeting, Map<String, Person> personMap,
                Set<Person> speakerSet, Set<Person> requiredPersonSet, AtomicLong attendanceIdCounter) {
            Set<Person> preferredPersonSet = new HashSet<>();
            return Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                    .filter(preferredAttendee -> !preferredAttendee.isEmpty())
                    .map(personName -> {
                        PreferredAttendance preferredAttendance =
                                createAttendance(attendanceIdCounter, id -> new PreferredAttendance(id, meeting));
                        Person person = personMap.get(personName);
                        if (person == null) {
                            throw new IllegalStateException(
                                    currentPosition() + ": The meeting with id (" + meeting.getId()
                                            + ") has a preferred attendee (" + personName
                                            + ") that doesn't exist in the Persons list.");
                        }
                        if (preferredPersonSet.contains(person)) {
                            throw new IllegalStateException(
                                    currentPosition() + ": The meeting with id (" + meeting.getId()
                                            + ") has a duplicate preferred attendee (" + personName + ").");
                        }
                        if (requiredPersonSet.contains(person)) {
                            throw new IllegalStateException(
                                    currentPosition() + ": The meeting with id (" + meeting.getId()
                                            + ") has a preferred attendee (" + personName
                                            + ") that is also a required attendee.");
                        }
                        if (speakerSet.contains(person)) {
                            throw new IllegalStateException(
                                    currentPosition() + ": The meeting with id (" + meeting.getId()
                                            + ") has a preferred attendee  (" + personName + ") who is also the speaker.");
                        }
                        preferredPersonSet.add(person);
                        preferredAttendance.setMeeting(meeting);
                        preferredAttendance.setPerson(person);
                        return preferredAttendance;
                    })
                    .collect(toList());
        }

        private TimeGrain extractTimeGrain(Meeting meeting, Map<LocalDateTime, TimeGrain> timeGrainMap) {
            String dateString = nextStringCell().getStringCellValue();
            String startTimeString = nextStringCell().getStringCellValue();
            if (!dateString.isEmpty() || !startTimeString.isEmpty()) {
                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.of(LocalDate.parse(dateString, DAY_FORMATTER),
                            LocalTime.parse(startTimeString, TIME_FORMATTER));
                } catch (DateTimeParseException e) {
                    throw new IllegalStateException(currentPosition() + ": The meeting with id (" + meeting.getId()
                            + ") has a timeGrain date (" + dateString + ") and startTime (" + startTimeString
                            + ") that doesn't parse as a date or time.", e);
                }

                TimeGrain timeGrain = timeGrainMap.get(dateTime);
                if (timeGrain == null) {
                    throw new IllegalStateException(currentPosition() + ": The meeting with id (" + meeting.getId()
                            + ") has a timeGrain date (" + dateString + ") and startTime (" + startTimeString
                            + ") that doesn't exist in the other sheet (Day).");
                }
                return timeGrain;
            }
            return null;
        }

        private Room extractRoom(Meeting meeting, Map<String, Room> roomMap) {
            String roomName = nextStringCell().getStringCellValue();
            if (!roomName.isEmpty()) {
                Room room = roomMap.get(roomName);
                if (room == null) {
                    throw new IllegalStateException(currentPosition() + ": The meeting with id (" + meeting.getId()
                            + ") has a roomName (" + roomName
                            + ") that doesn't exist in the other sheet (Rooms).");
                }
                return room;
            }
            return null;
        }

        private void readDayList() {
            nextSheet("Days");
            nextRow(false);
            readHeaderCell("Day");
            readHeaderCell("Start");
            readHeaderCell("End");
            List<Day> dayList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            List<TimeGrain> timeGrainList = new ArrayList<>();
            int dayId = 0;
            int timeGrainId = 0;
            while (nextRow()) {
                Day day = new Day(dayId, LocalDate.parse(nextStringCell().getStringCellValue(), DAY_FORMATTER).getDayOfYear());
                dayList.add(day);
                dayId++;

                LocalTime startTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                LocalTime endTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                LocalTime lunchHourStartTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                int startMinuteOfDay = startTime.getHour() * 60 + startTime.getMinute();
                int endMinuteOfDay = endTime.getHour() * 60 + endTime.getMinute();
                int lunchHourStartMinuteOfDay = lunchHourStartTime.getHour() * 60 + lunchHourStartTime.getMinute();
                for (int i = 0; (endMinuteOfDay - startMinuteOfDay) > i * TimeGrain.GRAIN_LENGTH_IN_MINUTES; i++) {
                    int timeGrainStartingMinuteOfDay = i * TimeGrain.GRAIN_LENGTH_IN_MINUTES + startMinuteOfDay;
                    if (timeGrainStartingMinuteOfDay < lunchHourStartMinuteOfDay
                            || timeGrainStartingMinuteOfDay >= lunchHourStartMinuteOfDay + 60) {
                        TimeGrain timeGrain = new TimeGrain(timeGrainId, timeGrainId++, day, timeGrainStartingMinuteOfDay);
                        timeGrainList.add(timeGrain);
                    }
                }
            }
            solution.setDayList(dayList);
            solution.setTimeGrainList(timeGrainList);
        }

        private void readRoomList() {
            nextSheet("Rooms");
            nextRow();
            readHeaderCell("Name");
            readHeaderCell("Capacity");
            List<Room> roomList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                String name = nextStringCell().getStringCellValue();
                if (!VALID_NAME_PATTERN.matcher(name).matches()) {
                    throw new IllegalStateException(
                            currentPosition() + ": The room name (" + name
                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
                double capacityDouble = nextNumericCell().getNumericCellValue();
                if (capacityDouble <= 0 || capacityDouble != Math.floor(capacityDouble)) {
                    throw new IllegalStateException(
                            currentPosition() + ": The room with name (" + name
                                    + ") has a capacity (" + capacityDouble
                                    + ") that isn't a strictly positive integer number.");
                }
                Room room = new Room(id++, name, (int) capacityDouble);
                roomList.add(room);
            }
            solution.setRoomList(roomList);
        }
    }

    @Override
    public void write(MeetingSchedule solution, File outputScheduleFile) {
        try (FileOutputStream out = new FileOutputStream(outputScheduleFile);
                Workbook workbook = new MeetingSchedulingXlsxWriter(solution).write()) {
            workbook.write(out);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed writing outputScheduleFile (" + outputScheduleFile
                    + ") for schedule (" + solution + ").", e);
        }
    }

    private static final class MeetingSchedulingXlsxWriter
            extends AbstractXlsxWriter<MeetingSchedule, HardMediumSoftScore> {

        MeetingSchedulingXlsxWriter(MeetingSchedule solution) {
            super(solution, MeetingSchedulingApp.SOLVER_CONFIG);
        }

        @Override
        public Workbook write() {
            workbook = new XSSFWorkbook();
            creationHelper = workbook.getCreationHelper();
            createStyles();
            writeConfiguration();
            writeDays();
            writeRooms();
            writePersons();
            writeMeetings();
            writeRoomsView();
            writePersonsView();
            writePrintedFormView();
            writeScoreView(justificationList -> justificationList.stream()
                    .filter(MeetingAssignment.class::isInstance)
                    .map(Object::toString)
                    .collect(joining(", ")));
            return workbook;
        }

        private void writeConfiguration() {
            nextSheet("Configuration", 1, 3, false);
            nextRow();
            nextCell()
                    .setCellValue(DAY_FORMATTER.format(LocalDateTime.now()) + " " + TIME_FORMATTER.format(LocalDateTime.now()));
            nextRow();
            nextRow();
            nextHeaderCell("Constraint");
            nextHeaderCell("Weight");
            nextHeaderCell("Description");

            MeetingConstraintConfiguration constraintConfiguration = solution.getConstraintConfiguration();

            // TODO refactor this to allow setting pos/neg, weight and score level
            writeIntConstraintParameterLine(ROOM_CONFLICT, constraintConfiguration.getRoomConflict().hardScore(), "");
            writeIntConstraintParameterLine(DONT_GO_IN_OVERTIME, constraintConfiguration.getDontGoInOvertime().hardScore(),
                    "");
            writeIntConstraintParameterLine(REQUIRED_ATTENDANCE_CONFLICT,
                    constraintConfiguration.getRequiredAttendanceConflict().hardScore(), "");
            writeIntConstraintParameterLine(REQUIRED_ROOM_CAPACITY,
                    constraintConfiguration.getRequiredRoomCapacity().hardScore(), "");
            writeIntConstraintParameterLine(START_AND_END_ON_SAME_DAY,
                    constraintConfiguration.getStartAndEndOnSameDay().hardScore(), "");
            nextRow();
            writeIntConstraintParameterLine(REQUIRED_AND_PREFERRED_ATTENDANCE_CONFLICT,
                    constraintConfiguration.getRequiredAndPreferredAttendanceConflict().mediumScore(), "");
            writeIntConstraintParameterLine(PREFERRED_ATTENDANCE_CONFLICT,
                    constraintConfiguration.getPreferredAttendanceConflict().mediumScore(), "");
            nextRow();
            writeIntConstraintParameterLine(DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE,
                    constraintConfiguration.getDoAllMeetingsAsSoonAsPossible().softScore(), "");
            writeIntConstraintParameterLine(ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS,
                    constraintConfiguration.getOneTimeGrainBreakBetweenTwoConsecutiveMeetings().softScore(), "");
            writeIntConstraintParameterLine(OVERLAPPING_MEETINGS,
                    constraintConfiguration.getOverlappingMeetings().softScore(), "");
            writeIntConstraintParameterLine(ASSIGN_LARGER_ROOMS_FIRST,
                    constraintConfiguration.getAssignLargerRoomsFirst().softScore(), "");
            writeIntConstraintParameterLine(ROOM_STABILITY, constraintConfiguration.getRoomStability().softScore(), "");

            autoSizeColumnsWithHeader();
        }

        private void writePersons() {
            nextSheet("Persons", 1, 0, false);
            nextRow();
            nextHeaderCell("Full name");
            for (Person person : solution.getPersonList()) {
                nextRow();
                nextCell().setCellValue(person.getFullName());
            }
            autoSizeColumnsWithHeader();
        }

        private void writeMeetings() {
            nextSheet("Meetings", 1, 1, false);
            nextRow();
            nextHeaderCell("Topic");
            nextHeaderCell("Group");
            nextHeaderCell("Duration");
            nextHeaderCell("Speakers");
            nextHeaderCell("Content");
            nextHeaderCell("Required attendance list");
            nextHeaderCell("Preferred attendance list");
            nextHeaderCell("Day");
            nextHeaderCell("Starting time");
            nextHeaderCell("Room");
            Map<Meeting, List<MeetingAssignment>> meetingAssignmentMap = solution.getMeetingAssignmentList().stream()
                    .collect(groupingBy(MeetingAssignment::getMeeting, toList()));
            for (Meeting meeting : solution.getMeetingList()) {
                nextRow();
                nextCell().setCellValue(meeting.getTopic());
                nextCell().setCellValue(meeting.isEntireGroupMeeting() ? "Y" : "");
                nextCell().setCellValue(meeting.getDurationInGrains() * TimeGrain.GRAIN_LENGTH_IN_MINUTES);
                nextCell().setCellValue(meeting.getSpeakerList() == null ? ""
                        : meeting.getSpeakerList().stream()
                                .map(Person::getFullName)
                                .collect(joining(", ")));
                nextCell().setCellValue(meeting.getContent() == null ? "" : meeting.getContent());
                nextCell().setCellValue(
                        meeting.getRequiredAttendanceList().stream()
                                .map(requiredAttendance -> requiredAttendance.getPerson().getFullName())
                                .collect(joining(", ")));
                nextCell().setCellValue(
                        meeting.getPreferredAttendanceList().stream()
                                .map(preferredAttendance -> preferredAttendance.getPerson().getFullName())
                                .collect(joining(", ")));
                List<MeetingAssignment> meetingAssignmentList = meetingAssignmentMap.get(meeting);
                if (meetingAssignmentList.size() != 1) {
                    throw new IllegalStateException("Impossible state: the meeting (" + meeting
                            + ") does not have exactly one assignment, but " + meetingAssignmentList.size()
                            + " assignments instead.");
                }
                MeetingAssignment meetingAssignment = meetingAssignmentList.get(0);
                TimeGrain startingTimeGrain = meetingAssignment.getStartingTimeGrain();
                nextCell().setCellValue(startingTimeGrain == null ? "" : DAY_FORMATTER.format(startingTimeGrain.getDate()));
                nextCell().setCellValue(startingTimeGrain == null ? "" : TIME_FORMATTER.format(startingTimeGrain.getTime()));
                nextCell().setCellValue(meetingAssignment.getRoom() == null ? "" : meetingAssignment.getRoom().getName());
            }
            setSizeColumnsWithHeader(5000);
        }

        private void writeDays() {
            nextSheet("Days", 1, 1, false);
            nextRow();
            nextHeaderCell("Day");
            nextHeaderCell("Start");
            nextHeaderCell("End");
            nextHeaderCell("Lunch hour start time");
            for (Day dayOfYear : solution.getDayList()) {
                nextRow();
                LocalDate date = LocalDate.ofYearDay(Year.now().getValue(), dayOfYear.getDayOfYear());
                int startMinuteOfDay = 24 * 60;
                int endMinuteOfDay = 0;
                for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                    if (timeGrain.getDay().equals(dayOfYear)) {
                        startMinuteOfDay = Math.min(timeGrain.getStartingMinuteOfDay(), startMinuteOfDay);
                        endMinuteOfDay = Math.max(timeGrain.getStartingMinuteOfDay() + TimeGrain.GRAIN_LENGTH_IN_MINUTES,
                                endMinuteOfDay);
                    }
                }
                LocalTime startTime = LocalTime.ofSecondOfDay(startMinuteOfDay * 60L);
                LocalTime endTime = LocalTime.ofSecondOfDay(endMinuteOfDay * 60L);
                LocalTime lunchHourStartTime = LocalTime.ofSecondOfDay(12 * 60 * 60); // 12pm

                nextCell().setCellValue(DAY_FORMATTER.format(date));
                nextCell().setCellValue(TIME_FORMATTER.format(startTime));
                nextCell().setCellValue(TIME_FORMATTER.format(endTime));
                nextCell().setCellValue(TIME_FORMATTER.format(lunchHourStartTime));
            }
            autoSizeColumnsWithHeader();
        }

        private void writeRooms() {
            nextSheet("Rooms", 1, 1, false);
            nextRow();
            nextHeaderCell("Name");
            nextHeaderCell("Capacity");
            for (Room room : solution.getRoomList()) {
                nextRow();
                nextCell().setCellValue(room.getName());
                nextCell().setCellValue(room.getCapacity());
            }
            autoSizeColumnsWithHeader();
        }

        private void writeRoomsView() {
            nextSheet("Rooms view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainDaysHeaders();
            nextRow();
            nextHeaderCell("Room");
            writeTimeGrainHoursHeaders();
            for (Room room : solution.getRoomList()) {
                nextRow();
                currentRow.setHeightInPoints(2 * currentSheet.getDefaultRowHeightInPoints());
                nextCell().setCellValue(room.getName());
                List<MeetingAssignment> roomMeetingAssignmentList = solution.getMeetingAssignmentList().stream()
                        .filter(meetingAssignment -> meetingAssignment.getRoom() == room).collect(toList());
                writeMeetingAssignmentList(roomMeetingAssignmentList);
            }
            autoSizeColumnsWithHeader();
        }

        private void writePersonsView() {
            nextSheet("Persons view", 2, 2, true);
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("");
            writeTimeGrainDaysHeaders();
            nextRow();
            nextHeaderCell("Person");
            nextHeaderCell("Attendance");
            writeTimeGrainHoursHeaders();
            for (Person person : solution.getPersonList()) {
                writePersonMeetingList(person, true);
                writePersonMeetingList(person, false);
            }
            autoSizeColumnsWithHeader();
        }

        private void writePersonMeetingList(Person person, boolean required) {
            nextRow();
            currentRow.setHeightInPoints(2 * currentSheet.getDefaultRowHeightInPoints());
            nextHeaderCell(person.getFullName());
            if (required) {
                nextHeaderCell("Required");
            } else {
                currentSheet.addMergedRegion(
                        new CellRangeAddress(currentRowNumber - 1, currentRowNumber, currentColumnNumber, currentColumnNumber));
                nextHeaderCell("Preferred");
            }

            List<Meeting> personMeetingList;
            if (required) {
                personMeetingList = solution.getAttendanceList().stream()
                        .filter(attendance -> attendance.getPerson().equals(person) && attendance instanceof RequiredAttendance)
                        .map(Attendance::getMeeting)
                        .collect(toList());
            } else {
                personMeetingList = solution.getAttendanceList().stream()
                        .filter(attendance -> attendance.getPerson().equals(person)
                                && attendance instanceof PreferredAttendance)
                        .map(Attendance::getMeeting)
                        .collect(toList());
            }
            List<MeetingAssignment> personMeetingAssignmentList = solution.getMeetingAssignmentList().stream()
                    .filter(meetingAssignment -> personMeetingList.contains(meetingAssignment.getMeeting()))
                    .collect(toList());
            writeMeetingAssignmentList(personMeetingAssignmentList);
        }

        private void writePrintedFormView() {
            nextSheet("Printed form view", 1, 1, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainsHoursVertically(30);
            currentColumnNumber = 0;
            for (Room room : solution.getRoomList()) {
                List<MeetingAssignment> roomMeetingAssignmentList = solution.getMeetingAssignmentList().stream()
                        .filter(meetingAssignment -> meetingAssignment.getRoom() == room)
                        .collect(toList());
                if (roomMeetingAssignmentList.isEmpty()) {
                    continue;
                }

                currentColumnNumber++;
                currentRowNumber = -1;
                nextHeaderCellVertically(room.getName());
                writeMeetingAssignmentListVertically(roomMeetingAssignmentList);
            }
            setSizeColumnsWithHeader(6000);
        }

        private void writeMeetingAssignmentListVertically(List<MeetingAssignment> roomMeetingAssignmentList) {
            int mergeStart = -1;
            int previousMeetingRemainingTimeGrains = 0;
            boolean mergingPreviousTimeGrain = false;
            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                List<MeetingAssignment> meetingAssignmentList = roomMeetingAssignmentList.stream()
                        .filter(meetingAssignment -> meetingAssignment.getStartingTimeGrain() == timeGrain)
                        .collect(toList());
                if (meetingAssignmentList.isEmpty() && mergingPreviousTimeGrain && previousMeetingRemainingTimeGrains > 0) {
                    previousMeetingRemainingTimeGrains--;
                    nextCellVertically();
                } else {
                    if (mergingPreviousTimeGrain && mergeStart < currentRowNumber) {
                        currentSheet.addMergedRegion(
                                new CellRangeAddress(mergeStart, currentRowNumber, currentColumnNumber, currentColumnNumber));
                    }

                    StringBuilder meetingInfo = new StringBuilder();
                    for (MeetingAssignment meetingAssignment : meetingAssignmentList) {
                        String startTimeString = getTimeString(
                                meetingAssignment.getStartingTimeGrain().getStartingMinuteOfDay());
                        int lastTimeGrainIndex = meetingAssignment.getLastTimeGrainIndex() <= solution.getTimeGrainList().size()
                                - 1 ? meetingAssignment.getLastTimeGrainIndex() : solution.getTimeGrainList().size() - 1;
                        String endTimeString = getTimeString(
                                solution.getTimeGrainList().get(lastTimeGrainIndex).getStartingMinuteOfDay()
                                        + TimeGrain.GRAIN_LENGTH_IN_MINUTES);
                        meetingInfo.append(WordUtils.abbreviate(meetingAssignment.getMeeting().getTopic(), 150, -1, "..."))
                                .append("\n  ")
                                .append(meetingAssignment.getMeeting().getSpeakerList().stream().map(Person::getFullName)
                                        .collect(joining(", ")))
                                .append("\n  ")
                                .append(startTimeString).append(" - ").append(endTimeString)
                                .append(" (").append(meetingAssignment.getMeeting().getDurationInGrains()
                                        * TimeGrain.GRAIN_LENGTH_IN_MINUTES)
                                .append(" mins)");
                    }
                    nextCellVertically().setCellValue(meetingInfo.toString());

                    previousMeetingRemainingTimeGrains = getLongestDurationInGrains(meetingAssignmentList) - 1;
                    mergingPreviousTimeGrain = previousMeetingRemainingTimeGrains > 0;
                    mergeStart = currentRowNumber;
                }
            }
            if (mergeStart < currentRowNumber) {
                currentSheet.addMergedRegion(
                        new CellRangeAddress(mergeStart, currentRowNumber, currentColumnNumber, currentColumnNumber));
            }
        }

        private String getTimeString(int minuteOfDay) {
            return TIME_FORMATTER.format(LocalTime.ofSecondOfDay(minuteOfDay * 60L));
        }

        private void writeMeetingAssignmentList(List<MeetingAssignment> meetingAssignmentList) {
            String[] filteredConstraintNames = {
                    ROOM_CONFLICT, DONT_GO_IN_OVERTIME, REQUIRED_ATTENDANCE_CONFLICT, REQUIRED_ROOM_CAPACITY,
                    START_AND_END_ON_SAME_DAY,

                    REQUIRED_AND_PREFERRED_ATTENDANCE_CONFLICT, PREFERRED_ATTENDANCE_CONFLICT,

                    DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE, ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS,
                    OVERLAPPING_MEETINGS, ASSIGN_LARGER_ROOMS_FIRST, ROOM_STABILITY
            };
            int mergeStart = -1;
            int previousMeetingRemainingTimeGrains = 0;
            boolean mergingPreviousMeetingList = false;

            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                List<MeetingAssignment> timeGrainMeetingAssignmentList = meetingAssignmentList.stream()
                        .filter(meetingAssignment -> meetingAssignment.getStartingTimeGrain() == timeGrain)
                        .collect(toList());
                if (timeGrainMeetingAssignmentList.isEmpty() && mergingPreviousMeetingList
                        && previousMeetingRemainingTimeGrains > 0) {
                    previousMeetingRemainingTimeGrains--;
                    nextCell();
                } else {
                    if (mergingPreviousMeetingList && mergeStart < currentColumnNumber) {
                        currentSheet.addMergedRegion(
                                new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                    }
                    nextMeetingAssignmentListCell(timeGrainMeetingAssignmentList,
                            meetingAssignment -> meetingAssignment.getMeeting().getTopic() + "\n  "
                                    + meetingAssignment.getMeeting().getSpeakerList().stream().map(Person::getFullName)
                                            .collect(joining(", ")),
                            Arrays.asList(filteredConstraintNames));
                    mergingPreviousMeetingList = !timeGrainMeetingAssignmentList.isEmpty();
                    mergeStart = currentColumnNumber;
                    previousMeetingRemainingTimeGrains = getLongestDurationInGrains(timeGrainMeetingAssignmentList) - 1;
                }
            }

            if (mergingPreviousMeetingList && mergeStart < currentColumnNumber) {
                currentSheet.addMergedRegion(
                        new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
            }
        }

        private int getLongestDurationInGrains(List<MeetingAssignment> meetingAssignmentList) {
            int longestDurationInGrains = 1;
            for (MeetingAssignment meetingAssignment : meetingAssignmentList) {
                if (meetingAssignment.getMeeting().getDurationInGrains() > longestDurationInGrains) {
                    longestDurationInGrains = meetingAssignment.getMeeting().getDurationInGrains();
                }
            }
            return longestDurationInGrains;
        }

        private void writeTimeGrainDaysHeaders() {
            Day previousTimeGrainDay = null;
            int mergeStart = -1;

            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                Day timeGrainDay = timeGrain.getDay();
                if (timeGrainDay.equals(previousTimeGrainDay)) {
                    nextHeaderCell("");
                } else {
                    if (previousTimeGrainDay != null) {
                        currentSheet.addMergedRegion(
                                new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                    }
                    nextHeaderCell(DAY_FORMATTER.format(
                            LocalDate.ofYearDay(Year.now().getValue(), timeGrainDay.getDayOfYear())));
                    previousTimeGrainDay = timeGrainDay;
                    mergeStart = currentColumnNumber;
                }
            }
            if (previousTimeGrainDay != null) {
                currentSheet.addMergedRegion(
                        new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
            }
        }

        private void writeTimeGrainHoursHeaders() {
            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                LocalTime startTime = LocalTime.ofSecondOfDay(timeGrain.getStartingMinuteOfDay() * 60L);
                nextHeaderCell(TIME_FORMATTER.format(startTime));
            }
        }

        private void writeTimeGrainsHoursVertically(int minimumInterval) {
            int mergeStart = -1;
            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                if (timeGrain.getGrainIndex() % (Math.ceil(minimumInterval * 1.0 / TimeGrain.GRAIN_LENGTH_IN_MINUTES)) == 0) {
                    if (mergeStart > 0) {
                        currentSheet.addMergedRegion(new CellRangeAddress(mergeStart, currentRowNumber, 0, 0));
                    }
                    nextRow();
                    nextCell().setCellValue(timeGrain.getDateTimeString());
                    mergeStart = currentRowNumber;
                } else {
                    nextRow();
                }
            }
            if (mergeStart < currentRowNumber) {
                currentSheet.addMergedRegion(new CellRangeAddress(mergeStart, currentRowNumber, 0, 0));
            }
        }

        void nextMeetingAssignmentListCell(List<MeetingAssignment> meetingAssignmentList,
                Function<MeetingAssignment, String> stringFunction, List<String> filteredConstraintNames) {
            if (meetingAssignmentList == null) {
                meetingAssignmentList = Collections.emptyList();
            }
            HardMediumSoftScore score = meetingAssignmentList.stream()
                    .map(indictmentMap::get).filter(Objects::nonNull)
                    .flatMap(indictment -> indictment.getConstraintMatchSet().stream())
                    // Filter out filtered constraints
                    .filter(constraintMatch -> filteredConstraintNames == null
                            || filteredConstraintNames.contains(constraintMatch.getConstraintName()))
                    .map(ConstraintMatch::getScore)
                    // Filter out positive constraints
                    .filter(indictmentScore -> !(indictmentScore.hardScore() >= 0 && indictmentScore.softScore() >= 0))
                    .reduce(HardMediumSoftScore::add).orElse(HardMediumSoftScore.ZERO);

            XSSFCell cell = getXSSFCellOfScore(score);

            if (!meetingAssignmentList.isEmpty()) {
                ClientAnchor anchor = creationHelper.createClientAnchor();
                anchor.setCol1(cell.getColumnIndex());
                anchor.setCol2(cell.getColumnIndex() + 4);
                anchor.setRow1(currentRow.getRowNum());
                anchor.setRow2(currentRow.getRowNum() + 4);
                Comment comment = currentDrawing.createCellComment(anchor);
                String commentString = getMeetingAssignmentListString(meetingAssignmentList);
                comment.setString(creationHelper.createRichTextString(commentString));
                cell.setCellComment(comment);
            }
            cell.setCellValue(meetingAssignmentList.stream().map(stringFunction).collect(joining("\n")));
            currentRow.setHeightInPoints(Math.max(currentRow.getHeightInPoints(),
                    meetingAssignmentList.size() * currentSheet.getDefaultRowHeightInPoints()));
        }

        private String getMeetingAssignmentListString(List<MeetingAssignment> meetingAssignmentList) {
            StringBuilder commentString = new StringBuilder(meetingAssignmentList.size() * 200);
            for (MeetingAssignment meetingAssignment : meetingAssignmentList) {
                commentString.append("Date and Time: ").append(meetingAssignment.getStartingTimeGrain().getDateTimeString())
                        .append("\n")
                        .append("Duration: ")
                        .append(meetingAssignment.getMeeting().getDurationInGrains() * TimeGrain.GRAIN_LENGTH_IN_MINUTES)
                        .append(" minutes.\n")
                        .append("Room: ").append(meetingAssignment.getRoom().getName()).append("\n");

                Indictment<HardMediumSoftScore> indictment = indictmentMap.get(meetingAssignment);
                if (indictment != null) {
                    commentString.append("\n").append(indictment.getScore().toShortString())
                            .append(" total");
                    Set<ConstraintMatch<HardMediumSoftScore>> constraintMatchSet = indictment.getConstraintMatchSet();
                    List<String> constraintNameList = constraintMatchSet.stream()
                            .map(ConstraintMatch::getConstraintName).distinct().collect(toList());
                    for (String constraintName : constraintNameList) {
                        List<ConstraintMatch<HardMediumSoftScore>> filteredConstraintMatchList = constraintMatchSet.stream()
                                .filter(constraintMatch -> constraintMatch.getConstraintName().equals(constraintName))
                                .collect(toList());
                        HardMediumSoftScore sum = filteredConstraintMatchList.stream()
                                .map(ConstraintMatch::getScore)
                                .reduce(HardMediumSoftScore::add)
                                .orElse(HardMediumSoftScore.ZERO);
                        String justificationTalkCodes = filteredConstraintMatchList.stream()
                                .flatMap(constraintMatch -> constraintMatch.getIndictedObjectList().stream())
                                .filter(indictedObject -> indictedObject instanceof MeetingAssignment
                                        && indictedObject != meetingAssignment)
                                .distinct().map(o -> Long.toString(((MeetingAssignment) o).getMeeting().getId()))
                                .collect(joining(", "));
                        commentString.append("\n    ").append(sum.toShortString())
                                .append(" for ").append(filteredConstraintMatchList.size())
                                .append(" ").append(constraintName).append("s")
                                .append("\n        ").append(justificationTalkCodes);
                    }
                }
                commentString.append("\n\n");
            }
            return commentString.toString();
        }

        private XSSFCell getXSSFCellOfScore(HardMediumSoftScore score) {
            XSSFCell cell;
            if (!score.isFeasible()) {
                cell = nextCell(hardPenaltyStyle);
            } else if (score.mediumScore() < 0) {
                cell = nextCell(mediumPenaltyStyle);
            } else if (score.softScore() < 0) {
                cell = nextCell(softPenaltyStyle);
            } else {
                cell = nextCell(wrappedStyle);
            }
            return cell;
        }
    }
}
