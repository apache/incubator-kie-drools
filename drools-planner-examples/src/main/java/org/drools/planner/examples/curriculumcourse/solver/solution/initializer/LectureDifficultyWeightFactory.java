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

import org.drools.planner.core.domain.entity.PlanningEntityDifficultyWeightFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.curriculumcourse.domain.Course;
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;
import org.drools.planner.examples.curriculumcourse.domain.Lecture;
import org.drools.planner.examples.curriculumcourse.domain.UnavailablePeriodConstraint;

public class LectureDifficultyWeightFactory implements PlanningEntityDifficultyWeightFactory {

    public Comparable createDifficultyWeight(Solution solution, Object planningEntity) {
        CurriculumCourseSchedule schedule = (CurriculumCourseSchedule) solution;
        Lecture lecture = (Lecture) planningEntity;
        Course course = lecture.getCourse();
        int unavailablePeriodConstraintCount = 0;
        for (UnavailablePeriodConstraint constraint : schedule.getUnavailablePeriodConstraintList()) {
            if (constraint.getCourse().equals(course)) {
                unavailablePeriodConstraintCount++;
            }
        }
        return new LectureDifficultyWeight(lecture, unavailablePeriodConstraintCount);
    }

}
