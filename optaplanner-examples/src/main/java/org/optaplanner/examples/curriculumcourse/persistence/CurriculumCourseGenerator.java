/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.curriculumcourse.persistence;

import static org.optaplanner.examples.common.persistence.AbstractSolutionImporter.getFlooredPossibleSolutionSize;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.generator.StringDataGenerator;
import org.optaplanner.examples.curriculumcourse.app.CurriculumCourseApp;
import org.optaplanner.examples.curriculumcourse.domain.Course;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Curriculum;
import org.optaplanner.examples.curriculumcourse.domain.Day;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.Period;
import org.optaplanner.examples.curriculumcourse.domain.Room;
import org.optaplanner.examples.curriculumcourse.domain.Teacher;
import org.optaplanner.examples.curriculumcourse.domain.Timeslot;
import org.optaplanner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class CurriculumCourseGenerator extends LoggingMain {

    private static final int DAY_LIST_SIZE = 5;
    private static final int TIMESLOT_LIST_SIZE = 7;
    private static final int PERIOD_LIST_SIZE = DAY_LIST_SIZE * TIMESLOT_LIST_SIZE - TIMESLOT_LIST_SIZE + 4;

    public static void main(String[] args) {
        CurriculumCourseGenerator generator = new CurriculumCourseGenerator();
        generator.writeCourseSchedule(200, 8);
        generator.writeCourseSchedule(400, 16);
        generator.writeCourseSchedule(800, 32);
    }

    private final int[] roomCapacityOptions = {
            20,
            25,
            30,
            40,
            50
    };

    private final String[] courseCodes = new String[] {
            "Math",
            "Chemistry",
            "Physics",
            "Geography",
            "Biology",
            "History",
            "English",
            "Spanish",
            "French",
            "German",
            "ICT",
            "Economics",
            "Psychology",
            "Art",
            "Music" };

    private final StringDataGenerator teacherNameGenerator = StringDataGenerator.buildFullNames();

    protected final SolutionFileIO<CourseSchedule> solutionFileIO;
    protected final File outputDir;

    protected Random random;

    public CurriculumCourseGenerator() {
        solutionFileIO = new XStreamSolutionFileIO<>(CourseSchedule.class);
        outputDir = new File(CommonApp.determineDataDir(CurriculumCourseApp.DATA_DIR_NAME), "unsolved");
    }

    private void writeCourseSchedule(int lectureListSize, int curriculumListSize) {
        int courseListSize = lectureListSize * 2 / 9 + 1;
        int teacherListSize = courseListSize / 3 + 1;
        int roomListSize = lectureListSize * 2 / PERIOD_LIST_SIZE;
        String fileName = determineFileName(lectureListSize, PERIOD_LIST_SIZE, roomListSize);
        File outputFile = new File(outputDir, fileName + ".xml");
        CourseSchedule schedule = createCourseSchedule(fileName, teacherListSize, curriculumListSize, courseListSize,
                lectureListSize, roomListSize);
        solutionFileIO.write(schedule, outputFile);
        logger.info("Saved: {}", outputFile);
    }

    private String determineFileName(int lectureListSize, int periodListSize, int roomListSize) {
        return lectureListSize + "lectures-" + periodListSize + "periods-" + roomListSize + "rooms";
    }

    public CourseSchedule createCourseSchedule(String fileName, int teacherListSize, int curriculumListSize, int courseListSize,
            int lectureListSize, int roomListSize) {
        random = new Random(37);
        CourseSchedule schedule = new CourseSchedule();
        schedule.setId(0L);

        createDayList(schedule);
        createTimeslotList(schedule);
        createPeriodList(schedule);
        createTeacherList(schedule, teacherListSize);
        createCourseList(schedule, courseListSize);
        createLectureList(schedule, lectureListSize);
        createRoomList(schedule, roomListSize);
        createCurriculumList(schedule, curriculumListSize);
        createUnavailablePeriodPenaltyList(schedule);

        int possibleForOneLectureSize = schedule.getPeriodList().size() * schedule.getRoomList().size();
        BigInteger possibleSolutionSize = BigInteger.valueOf(possibleForOneLectureSize).pow(
                schedule.getLectureList().size());
        logger.info("CourseSchedule {} has {} teachers, {} curricula, {} courses, {} lectures," +
                " {} periods, {} rooms and {} unavailable period constraints with a search space of {}.",
                fileName,
                schedule.getTeacherList().size(),
                schedule.getCurriculumList().size(),
                schedule.getCourseList().size(),
                schedule.getLectureList().size(),
                schedule.getPeriodList().size(),
                schedule.getRoomList().size(),
                schedule.getUnavailablePeriodPenaltyList().size(),
                getFlooredPossibleSolutionSize(possibleSolutionSize));
        return schedule;
    }

    private void createDayList(CourseSchedule schedule) {
        List<Day> dayList = new ArrayList<>(DAY_LIST_SIZE);
        for (int i = 0; i < DAY_LIST_SIZE; i++) {
            Day day = new Day();
            day.setId((long) i);
            day.setDayIndex(i);
            day.setPeriodList(new ArrayList<>(TIMESLOT_LIST_SIZE));
            dayList.add(day);
        }
        schedule.setDayList(dayList);
    }

    private void createTimeslotList(CourseSchedule schedule) {
        List<Timeslot> timeslotList = new ArrayList<>(TIMESLOT_LIST_SIZE);
        for (int i = 0; i < TIMESLOT_LIST_SIZE; i++) {
            Timeslot timeslot = new Timeslot(i);
            timeslotList.add(timeslot);
        }
        schedule.setTimeslotList(timeslotList);
    }

    private void createPeriodList(CourseSchedule schedule) {
        List<Period> periodList = new ArrayList<>(schedule.getDayList().size() * schedule.getTimeslotList().size());
        long periodId = 0L;
        for (Day day : schedule.getDayList()) {
            for (Timeslot timeslot : schedule.getTimeslotList()) {
                if (day.getDayIndex() == 2 && timeslot.getTimeslotIndex() >= 4) {
                    // No lectures Wednesday afternoon
                    continue;
                }
                Period period = new Period();
                period.setId(periodId);
                periodId++;
                period.setDay(day);
                day.getPeriodList().add(period);
                period.setTimeslot(timeslot);
                periodList.add(period);
            }
        }
        schedule.setPeriodList(periodList);
    }

    private void createTeacherList(CourseSchedule schedule, int teacherListSize) {
        List<Teacher> teacherList = new ArrayList<>(teacherListSize);
        teacherNameGenerator.predictMaximumSizeAndReset(teacherListSize);
        for (int i = 0; i < teacherListSize; i++) {
            Teacher teacher = new Teacher();
            teacher.setId((long) i);
            teacher.setCode(teacherNameGenerator.generateNextValue());
            teacherList.add(teacher);
        }
        schedule.setTeacherList(teacherList);
    }

    private void createCourseList(CourseSchedule schedule, int courseListSize) {
        List<Teacher> teacherList = schedule.getTeacherList();
        List<Course> courseList = new ArrayList<>(courseListSize);
        Set<String> codeSet = new HashSet<>();
        for (int i = 0; i < courseListSize; i++) {
            Course course = new Course();
            course.setId((long) i);
            String code = (i < courseCodes.length * 2)
                    ? courseCodes[i % courseCodes.length]
                    : courseCodes[random.nextInt(courseCodes.length)];
            StringDataGenerator codeSuffixGenerator = new StringDataGenerator("")
                    .addAToZPart(true, 0);
            if (courseListSize >= courseCodes.length) {
                String codeSuffix = codeSuffixGenerator.generateNextValue();
                while (codeSet.contains(code + codeSuffix)) {
                    codeSuffix = codeSuffixGenerator.generateNextValue();
                }
                code = code + codeSuffix;
                codeSet.add(code);
            }
            course.setCode(code);
            Teacher teacher = (i < teacherList.size() * 2)
                    ? teacherList.get(i % teacherList.size())
                    : teacherList.get(random.nextInt(teacherList.size()));
            course.setTeacher(teacher);
            course.setLectureSize(0);
            course.setMinWorkingDaySize(1);
            course.setCurriculumSet(new LinkedHashSet<>());
            course.setStudentSize(0);
            courseList.add(course);
        }
        schedule.setCourseList(courseList);
    }

    private void createLectureList(CourseSchedule schedule, int lectureListSize) {
        List<Course> courseList = schedule.getCourseList();
        List<Lecture> lectureList = new ArrayList<>(lectureListSize);
        for (int i = 0; i < lectureListSize; i++) {
            Lecture lecture = new Lecture();
            lecture.setId((long) i);
            Course course = (i < courseList.size() * 2)
                    ? courseList.get(i % courseList.size())
                    : courseList.get(random.nextInt(courseList.size()));
            lecture.setCourse(course);
            lecture.setLectureIndexInCourse(course.getLectureSize());
            course.setLectureSize(course.getLectureSize() + 1);
            lecture.setPinned(false);
            lectureList.add(lecture);
        }
        schedule.setLectureList(lectureList);

    }

    private void createRoomList(CourseSchedule schedule, int roomListSize) {
        List<Room> roomList = new ArrayList<>(roomListSize);
        for (int i = 0; i < roomListSize; i++) {
            Room room = new Room();
            room.setId((long) i);
            room.setCode("R" + ((i / 50 * 100) + 1 + i));
            room.setCapacity(roomCapacityOptions[random.nextInt(roomCapacityOptions.length)]);
            roomList.add(room);
        }
        schedule.setRoomList(roomList);
    }

    private void createCurriculumList(CourseSchedule schedule, int curriculumListSize) {
        int maximumCapacity = schedule.getRoomList().stream().mapToInt(Room::getCapacity).max().getAsInt();
        List<Course> courseList = schedule.getCourseList();
        List<Curriculum> curriculumList = new ArrayList<>(curriculumListSize);
        StringDataGenerator codeGenerator = new StringDataGenerator("")
                .addAToZPart(true, 0).addAToZPart(false, 1).addAToZPart(false, 1).addAToZPart(false, 1);
        codeGenerator.predictMaximumSizeAndReset(curriculumListSize);
        for (int i = 0; i < curriculumListSize; i++) {
            Curriculum curriculum = new Curriculum();
            curriculum.setId((long) i);
            curriculum.setCode("Group " + codeGenerator.generateNextValue());
            // The studentSize is more likely to be 15 than 5 or 25
            int studentSize = 5 + random.nextInt(10) + random.nextInt(10);

            List<Course> courseSubList = courseList.stream()
                    .filter(course -> course.getStudentSize() + studentSize < maximumCapacity)
                    .collect(Collectors.toList());
            Collections.shuffle(courseSubList, random);

            int lectureCount = 0;
            for (Course course : courseSubList) {
                lectureCount += course.getLectureSize();
                if (lectureCount > PERIOD_LIST_SIZE) {
                    break;
                }
                course.getCurriculumSet().add(curriculum);
                course.setStudentSize(course.getStudentSize() + studentSize);
            }

            curriculumList.add(curriculum);
        }
        schedule.setCurriculumList(curriculumList);
    }

    private void createUnavailablePeriodPenaltyList(CourseSchedule schedule) {
        List<Course> courseList = schedule.getCourseList();
        List<Period> periodList = schedule.getPeriodList();
        List<UnavailablePeriodPenalty> unavailablePeriodPenaltyList = new ArrayList<>(courseList.size());
        long penaltyId = 0L;
        for (Course course : courseList) {
            UnavailablePeriodPenalty penalty = new UnavailablePeriodPenalty();
            penalty.setId(penaltyId);
            penaltyId++;
            penalty.setCourse(course);
            penalty.setPeriod(periodList.get(random.nextInt(periodList.size())));
            unavailablePeriodPenaltyList.add(penalty);
        }
        schedule.setUnavailablePeriodPenaltyList(unavailablePeriodPenaltyList);
    }

}
