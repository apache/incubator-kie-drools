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

package org.drools.planner.examples.curriculumcourse.domain.solver;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.api.domain.entity.PlanningEntityDifficultyWeightFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.curriculumcourse.domain.Course;
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;
import org.drools.planner.examples.curriculumcourse.domain.Lecture;
import org.drools.planner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;

public class LectureDifficultyWeightFactory implements PlanningEntityDifficultyWeightFactory {

    public Comparable createDifficultyWeight(Solution solution, Object planningEntity) {
        CurriculumCourseSchedule schedule = (CurriculumCourseSchedule) solution;
        Lecture lecture = (Lecture) planningEntity;
        Course course = lecture.getCourse();
        int unavailablePeriodPenaltyCount = 0;
        for (UnavailablePeriodPenalty penalty : schedule.getUnavailablePeriodPenaltyList()) {
            if (penalty.getCourse().equals(course)) {
                unavailablePeriodPenaltyCount++;
            }
        }
        return new LectureDifficultyWeight(lecture, unavailablePeriodPenaltyCount);
    }

    public static class LectureDifficultyWeight implements Comparable<LectureDifficultyWeight> {

        private final Lecture lecture;
        private final int unavailablePeriodPenaltyCount;

        public LectureDifficultyWeight(Lecture lecture, int unavailablePeriodPenaltyCount) {
            this.lecture = lecture;
            this.unavailablePeriodPenaltyCount = unavailablePeriodPenaltyCount;
        }

        public int compareTo(LectureDifficultyWeight other) {
            Course course = lecture.getCourse();
            Course otherCourse = other.lecture.getCourse();
            return new CompareToBuilder()
                    .append(course.getCurriculumList().size(), otherCourse.getCurriculumList().size())
                    .append(unavailablePeriodPenaltyCount, other.unavailablePeriodPenaltyCount)
                    .append(course.getLectureSize(), otherCourse.getLectureSize())
                    .append(course.getStudentSize(), otherCourse.getStudentSize())
                    .append(course.getMinWorkingDaySize(), otherCourse.getMinWorkingDaySize())
                    .append(lecture.getId(), other.lecture.getId())
                    .toComparison();
        }

    }

}
