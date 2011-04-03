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

package org.drools.planner.examples.curriculumcourse.solver.solution.initializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.WorkingMemory;
import org.drools.planner.core.score.DefaultHardAndSoftScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.initializer.AbstractStartingSolutionInitializer;
import org.drools.planner.core.solver.AbstractSolverScope;
import org.drools.planner.examples.common.domain.PersistableIdComparator;
import org.drools.planner.examples.curriculumcourse.domain.Course;
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;
import org.drools.planner.examples.curriculumcourse.domain.Lecture;
import org.drools.planner.examples.curriculumcourse.domain.Period;
import org.drools.planner.examples.curriculumcourse.domain.Room;
import org.drools.planner.examples.curriculumcourse.domain.UnavailablePeriodConstraint;
import org.drools.runtime.rule.FactHandle;

public class CurriculumCourseStartingSolutionInitializer extends AbstractStartingSolutionInitializer {

    @Override
    public boolean isSolutionInitialized(AbstractSolverScope abstractSolverScope) {
        CurriculumCourseSchedule schedule = (CurriculumCourseSchedule) abstractSolverScope.getWorkingSolution();
        return schedule.isInitialized();
    }

    public void initializeSolution(AbstractSolverScope abstractSolverScope) {
        CurriculumCourseSchedule schedule = (CurriculumCourseSchedule) abstractSolverScope.getWorkingSolution();
        initializeLectureList(abstractSolverScope, schedule);
    }

    private void initializeLectureList(AbstractSolverScope abstractSolverScope,
            CurriculumCourseSchedule schedule) {
        List<Period> periodList = schedule.getPeriodList();
        List<Room> roomList = schedule.getRoomList();
        WorkingMemory workingMemory = abstractSolverScope.getWorkingMemory();

        List<Lecture> lectureList = createLectureList(schedule);
        for (Lecture lecture : lectureList) {
            Score unscheduledScore = abstractSolverScope.calculateScoreFromWorkingMemory();
            FactHandle lectureHandle = null;

            List<PeriodScoring> periodScoringList = new ArrayList<PeriodScoring>(periodList.size());
            for (Period period : periodList) {
                lecture.setPeriod(period);
                if (lectureHandle == null) {
                    lectureHandle = workingMemory.insert(lecture);
                } else {
                    workingMemory.update(lectureHandle, lecture);
                }
                Score score = abstractSolverScope.calculateScoreFromWorkingMemory();
                periodScoringList.add(new PeriodScoring(period, score));
            }
            Collections.sort(periodScoringList);

            boolean almostPerfectMatch = false;
            Score bestScore = DefaultHardAndSoftScore.valueOf(Integer.MIN_VALUE, Integer.MIN_VALUE);
            Period bestPeriod = null;
            Room bestRoom = null;
            for (PeriodScoring periodScoring : periodScoringList) {
                if (bestScore.compareTo(periodScoring.getScore()) >= 0) {
                    // No need to check the rest
                    break;
                }
                lecture.setPeriod(periodScoring.getPeriod());
                workingMemory.update(lectureHandle, lecture);

                for (Room room : roomList) {
                    lecture.setRoom(room);
                    workingMemory.update(lectureHandle, lecture);
                    Score score = abstractSolverScope.calculateScoreFromWorkingMemory();
                    if (score.compareTo(unscheduledScore) < 0) {
                        if (score.compareTo(bestScore) > 0) {
                            bestScore = score;
                            bestPeriod = periodScoring.getPeriod();
                            bestRoom = room;
                        }
                    } else if (score.compareTo(unscheduledScore) >= 0) {
                        // TODO due to the score rules, the score can unscheduledScore can be higher than the score
                        // In theory every possibility should be looked into
                        almostPerfectMatch = true;
                        break;
                    }
                }
                if (almostPerfectMatch) {
                    break;
                }
            }
            if (!almostPerfectMatch) {
                if (bestPeriod == null || bestRoom == null) {
                    throw new IllegalStateException("The bestPeriod (" + bestPeriod + ") or the bestRoom ("
                            + bestRoom + ") cannot be null.");
                }
                lecture.setPeriod(bestPeriod);
                lecture.setRoom(bestRoom);
                workingMemory.update(lectureHandle, lecture);
            }
            logger.debug("    Lecture ({}) initialized for starting solution.", lecture);
        }

        Collections.sort(lectureList, new PersistableIdComparator());
        schedule.setLectureList(lectureList);
    }

    public List<Lecture> createLectureList(CurriculumCourseSchedule schedule) {
        List<Course> courseList = schedule.getCourseList();
        List<CourseInitializationWeight> courseInitializationWeightList
                = new ArrayList<CourseInitializationWeight>(courseList.size());
        for (Course course : courseList) {
            courseInitializationWeightList.add(new CourseInitializationWeight(schedule, course));
        }
        Collections.sort(courseInitializationWeightList);

        List<Lecture> lectureList = new ArrayList<Lecture>(courseList.size() * 5);
        int lectureId = 0;
        for (CourseInitializationWeight courseInitializationWeight : courseInitializationWeightList) {
            Course course = courseInitializationWeight.getCourse();
            for (int i = 0; i < course.getLectureSize(); i++) {
                Lecture lecture = new Lecture();
                lecture.setId((long) lectureId);
                lectureId++;
                lecture.setCourse(course);
                lecture.setLectureIndexInCourse(i);
                lectureList.add(lecture);
            }
        }
        return lectureList;
    }

    private class CourseInitializationWeight implements Comparable<CourseInitializationWeight> {

        private Course course;
        private int unavailablePeriodConstraintCount;

        private CourseInitializationWeight(CurriculumCourseSchedule schedule, Course course) {
            this.course = course;
            unavailablePeriodConstraintCount = 0;
            // TODO this could be improved by iteration the unavailablePeriodConstraintList and using a hashmap
            for (UnavailablePeriodConstraint constraint : schedule.getUnavailablePeriodConstraintList()) {
                if (constraint.getCourse().equals(course)) {
                    unavailablePeriodConstraintCount++;
                }
            }
        }

        public Course getCourse() {
            return course;
        }

        public int compareTo(CourseInitializationWeight other) {

            return new CompareToBuilder()
                    .append(other.course.getCurriculumList().size(), course.getCurriculumList().size()) // Descending
                    .append(other.unavailablePeriodConstraintCount, unavailablePeriodConstraintCount) // Descending
                    .append(other.course.getLectureSize(), course.getLectureSize()) // Descending
                    .append(other.course.getStudentSize(), course.getStudentSize()) // Descending
                    .append(other.course.getMinWorkingDaySize(), course.getMinWorkingDaySize()) // Descending
                    .append(course.getId(), other.course.getId()) // Ascending
                    .toComparison();
        }

    }

    private class PeriodScoring implements Comparable<PeriodScoring> {

        private Period period;
        private Score score;

        private PeriodScoring(Period period, Score score) {
            this.period = period;
            this.score = score;
        }

        public Period getPeriod() {
            return period;
        }

        public Score getScore() {
            return score;
        }

        public int compareTo(PeriodScoring other) {
            return -new CompareToBuilder().append(score, other.score).toComparison();
        }

    }

}
