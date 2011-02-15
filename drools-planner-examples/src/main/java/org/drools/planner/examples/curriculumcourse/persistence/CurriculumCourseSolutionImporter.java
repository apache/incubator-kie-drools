/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.curriculumcourse.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.curriculumcourse.domain.Course;
import org.drools.planner.examples.curriculumcourse.domain.Curriculum;
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;
import org.drools.planner.examples.curriculumcourse.domain.Day;
import org.drools.planner.examples.curriculumcourse.domain.Period;
import org.drools.planner.examples.curriculumcourse.domain.Room;
import org.drools.planner.examples.curriculumcourse.domain.Teacher;
import org.drools.planner.examples.curriculumcourse.domain.Timeslot;
import org.drools.planner.examples.curriculumcourse.domain.UnavailablePeriodConstraint;
import org.drools.planner.core.solution.Solution;

public class CurriculumCourseSolutionImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = ".ctt";
    private static final String SPLIT_REGEX = "[\\ \\t]+";

    public static void main(String[] args) {
        new CurriculumCourseSolutionImporter().convertAll();
    }

    public CurriculumCourseSolutionImporter() {
        super(new CurriculumCourseDaoImpl());
    }

    @Override
    protected String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new CurriculumCourseInputBuilder();
    }

    public class CurriculumCourseInputBuilder extends TxtInputBuilder {

        public Solution readSolution() throws IOException {
            CurriculumCourseSchedule schedule = new CurriculumCourseSchedule();
            schedule.setId(0L);
            // Name: ToyExample
            schedule.setName(readParam("Name:"));
            // Courses: 4
            int courseListSize = Integer.parseInt(readParam("Courses:"));
            // Rooms: 2
            int roomListSize = Integer.parseInt(readParam("Rooms:"));
            // Days: 5
            int dayListSize = Integer.parseInt(readParam("Days:"));
            // Periods_per_day: 4
            int timeslotListSize = Integer.parseInt(readParam("Periods_per_day:"));
            // Curricula: 2
            int curriculumListSize = Integer.parseInt(readParam("Curricula:"));
            // Constraints: 8
            int unavailablePeriodConstraintListSize = Integer.parseInt(readParam("Constraints:"));

            Map<String, Course> courseMap = readCourseListAndTeacherList(
                    schedule, courseListSize);
            readRoomList(
                    schedule, roomListSize);
            Map<List<Integer>, Period> periodMap = createPeriodListAndDayListAndTimeslotList(
                    schedule, dayListSize, timeslotListSize);
            readCurriculumList(
                    schedule, courseMap, curriculumListSize);
            readUnavailablePeriodConstraintList(
                    schedule, courseMap, periodMap, unavailablePeriodConstraintListSize);
            readHeader("END.");

            logger.info("CurriculumCourseSchedule with {} teachers, {} curricula, {} courses, {} periods, {} rooms" +
                    " and {} unavailable period constraints.",
                    new Object[]{schedule.getTeacherList().size(),
                            schedule.getCurriculumList().size(),
                            schedule.getCourseList().size(),
                            schedule.getPeriodList().size(),
                            schedule.getRoomList().size(),
                            schedule.getUnavailablePeriodConstraintList().size()});

            // Note: lectureList stays null, that's work for the StartingSolutionInitializer
            return schedule;
        }

        private Map<String, Course> readCourseListAndTeacherList(
                CurriculumCourseSchedule schedule, int courseListSize) throws IOException {
            Map<String, Course> courseMap = new HashMap<String, Course>(courseListSize);
            Map<String, Teacher> teacherMap = new HashMap<String, Teacher>();
            List<Course> courseList = new ArrayList<Course>(courseListSize);
            readHeader("COURSES:");
            for (int i = 0; i < courseListSize; i++) {
                Course course = new Course();
                course.setId((long) i);
                // Courses: <CourseID> <Teacher> <# Lectures> <MinWorkingDays> <# Students>
                String line = bufferedReader.readLine();
                String[] lineTokens = line.split(SPLIT_REGEX);
                if (lineTokens.length != 5) {
                    throw new IllegalArgumentException("Read line (" + line + ") is expected to contain 4 tokens.");
                }
                course.setCode(lineTokens[0]);
                course.setTeacher(findOrCreateTeacher(teacherMap, lineTokens[1]));
                course.setLectureSize(Integer.parseInt(lineTokens[2]));
                course.setMinWorkingDaySize(Integer.parseInt(lineTokens[3]));
                course.setCurriculumList(new ArrayList<Curriculum>());
                course.setStudentSize(Integer.parseInt(lineTokens[4]));
                courseList.add(course);
                courseMap.put(course.getCode(), course);
            }
            schedule.setCourseList(courseList);
            List<Teacher> teacherList = new ArrayList<Teacher>(teacherMap.values());
            schedule.setTeacherList(teacherList);
            return courseMap;
        }

        private Teacher findOrCreateTeacher(Map<String, Teacher> teacherMap, String code) {
            Teacher teacher = teacherMap.get(code);
            if (teacher == null) {
                teacher = new Teacher();
                int id = teacherMap.size();
                teacher.setId((long) id);
                teacher.setCode(code);
                teacherMap.put(code, teacher);
            }
            return teacher;
        }

        private void readRoomList(CurriculumCourseSchedule schedule, int roomListSize)
                throws IOException {
            readHeader("ROOMS:");
            List<Room> roomList = new ArrayList<Room>(roomListSize);
            for (int i = 0; i < roomListSize; i++) {
                Room room = new Room();
                room.setId((long) i);
                // Rooms: <RoomID> <Capacity>
                String line = bufferedReader.readLine();
                String[] lineTokens = line.split(SPLIT_REGEX);
                if (lineTokens.length != 2) {
                    throw new IllegalArgumentException("Read line (" + line + ") is expected to contain 2 tokens.");
                }
                room.setCode(lineTokens[0]);
                room.setCapacity(Integer.parseInt(lineTokens[1]));
                roomList.add(room);
            }
            schedule.setRoomList(roomList);
        }

        private Map<List<Integer>, Period> createPeriodListAndDayListAndTimeslotList(
                CurriculumCourseSchedule schedule, int dayListSize, int timeslotListSize) throws IOException {
            int periodListSize = dayListSize * timeslotListSize;
            Map<List<Integer>, Period> periodMap = new HashMap<List<Integer>, Period>(periodListSize);
            List<Day> dayList = new ArrayList<Day>(dayListSize);
            for (int i = 0; i < dayListSize; i++) {
                Day day = new Day();
                day.setId((long) i);
                day.setDayIndex(i);
                dayList.add(day);
            }
            schedule.setDayList(dayList);
            List<Timeslot> timeslotList = new ArrayList<Timeslot>(timeslotListSize);
            for (int i = 0; i < timeslotListSize; i++) {
                Timeslot timeslot = new Timeslot();
                timeslot.setId((long) i);
                timeslot.setTimeslotIndex(i);
                timeslotList.add(timeslot);
            }
            schedule.setTimeslotList(timeslotList);
            List<Period> periodList = new ArrayList<Period>(periodListSize);
            for (int i = 0; i < dayListSize; i++) {
                for (int j = 0; j < timeslotListSize; j++) {
                    Period period = new Period();
                    period.setId((long) (i * timeslotListSize + j));
                    period.setDay(dayList.get(i));
                    period.setTimeslot(timeslotList.get(j));
                    periodList.add(period);
                    periodMap.put(Arrays.asList(i, j), period);
                }
            }
            schedule.setPeriodList(periodList);
            return periodMap;
        }

        private void readCurriculumList(CurriculumCourseSchedule schedule,
                Map<String, Course> courseMap, int curriculumListSize) throws IOException {
            readHeader("CURRICULA:");
            List<Curriculum> curriculumList = new ArrayList<Curriculum>(curriculumListSize);
            for (int i = 0; i < curriculumListSize; i++) {
                Curriculum curriculum = new Curriculum();
                curriculum.setId((long) i);
                // Curricula: <CurriculumID> <# Courses> <MemberID> ... <MemberID>
                String line = bufferedReader.readLine();
                String[] lineTokens = line.split(SPLIT_REGEX);
                if (lineTokens.length < 2) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") is expected to contain at least 2 tokens.");
                }
                curriculum.setCode(lineTokens[0]);
                int coursesInCurriculum = Integer.parseInt(lineTokens[1]);
                if (lineTokens.length != (coursesInCurriculum + 2)) {
                    throw new IllegalArgumentException("Read line (" + line + ") is expected to contain "
                            + (coursesInCurriculum + 2) + " tokens.");
                }
                for (int j = 2; j < lineTokens.length; j++) {
                    Course course = courseMap.get(lineTokens[j]);
                    if (course == null) {
                        throw new IllegalArgumentException("Read line (" + line + ") uses an unexisting course("
                                + lineTokens[j] + ").");
                    }
                    course.getCurriculumList().add(curriculum);
                }
                curriculumList.add(curriculum);
            }
            schedule.setCurriculumList(curriculumList);
        }

        private void readUnavailablePeriodConstraintList(CurriculumCourseSchedule schedule,
                Map<String, Course> courseMap, Map<List<Integer>, Period> periodMap, int unavailablePeriodConstraintListSize)
                throws IOException {
            readHeader("UNAVAILABILITY_CONSTRAINTS:");
            List<UnavailablePeriodConstraint> constraintList = new ArrayList<UnavailablePeriodConstraint>(
                    unavailablePeriodConstraintListSize);
            for (int i = 0; i < unavailablePeriodConstraintListSize; i++) {
                UnavailablePeriodConstraint constraint = new UnavailablePeriodConstraint();
                constraint.setId((long) i);
                // Unavailability_Constraints: <CourseID> <Day> <Day_Period>
                String line = bufferedReader.readLine();
                String[] lineTokens = line.split(SPLIT_REGEX);
                if (lineTokens.length != 3) {
                    throw new IllegalArgumentException("Read line (" + line + ") is expected to contain 3 tokens.");
                }
                constraint.setCourse(courseMap.get(lineTokens[0]));
                int dayIndex = Integer.parseInt(lineTokens[1]);
                int timeslotIndex = Integer.parseInt(lineTokens[2]);
                Period period = periodMap.get(Arrays.asList(dayIndex, timeslotIndex));
                if (period == null) {
                    throw new IllegalArgumentException("Read line (" + line + ") uses an unexisting period("
                            + dayIndex + " " + timeslotIndex + ").");
                }
                constraint.setPeriod(period);
                constraintList.add(constraint);
            }
            schedule.setUnavailablePeriodConstraintList(constraintList);
        }

        private String readParam(String key) throws IOException {
            String line = bufferedReader.readLine();
            String[] lineTokens = line.split(SPLIT_REGEX);
            if (lineTokens.length != 2 || !lineTokens[0].equals(key)) {
                throw new IllegalArgumentException("Read line (" + line + ") is expected to contain 2 tokens"
                        + " and start with \"" + key + "\".");
            }
            return lineTokens[1];
        }

        private void readHeader(String header) throws IOException {
            String line = bufferedReader.readLine();
            if (line.length() != 0) {
                throw new IllegalArgumentException("Read line (" + line + ") is expected to be empty"
                        + " and be followed with a line \"" + header + "\".");
            }
            line = bufferedReader.readLine();
            if (!line.equals(header)) {
                throw new IllegalArgumentException("Read line (" + line + ") is expected to be \"" + header + "\".");
            }
        }

    }

}
