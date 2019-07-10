/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.curriculumcourse.optional.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.curriculumcourse.domain.Curriculum;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;
import org.optaplanner.examples.curriculumcourse.domain.solver.CourseConflict;

import static org.optaplanner.core.api.score.stream.common.ConstraintCollectors.*;
import static org.optaplanner.core.api.score.stream.common.JoinerType.*;
import static org.optaplanner.core.api.score.stream.common.Joiners.*;

public class CourseScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        // TODO replace the 2 conflictingLectures constraints with these
//        teacherConflict(constraintFactory);
//        curriculumConflict(constraintFactory);
        conflictingLecturesDifferentCourseInSamePeriod(constraintFactory);
        conflictingLecturesSameCourseInSamePeriod(constraintFactory);
        roomOccupancy(constraintFactory);
        unavailablePeriodPenalty(constraintFactory);
        roomCapacity(constraintFactory);
        minimumWorkingDays(constraintFactory);
        curriculumCompactness(constraintFactory);
        roomStability(constraintFactory);
    }

    private void teacherConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "teacherConflict", HardSoftScore.ofHard(1));
        c.from(Lecture.class)
                .join(Lecture.class,
                        equalTo(Lecture::getTeacher),
                        equalTo(Lecture::getPeriod),
                        lessThan(Lecture::getId))
                .penalize();
    }

    private void curriculumConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "curriculumConflict", HardSoftScore.ofHard(1));
        c.from(Lecture.class)
                .join(Lecture.class,
                        equalTo(Lecture::getPeriod),
                        lessThan(Lecture::getId))
                .filter((lecture1, lecture2) -> lecture1.getCurriculumList().stream()
                        .anyMatch(lecture -> lecture2.getCurriculumList().contains(lecture)))
                .penalize((lecture1, lecture2) -> (int) lecture1.getCurriculumList().stream()
                        .filter(lecture -> lecture2.getCurriculumList().contains(lecture))
                        .count());
    }

    protected void conflictingLecturesDifferentCourseInSamePeriod(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "conflictingLecturesDifferentCourseInSamePeriod", HardSoftScore.ofHard(1));
        c.from(CourseConflict.class)
                .join(Lecture.class,
                        on(CourseConflict::getLeftCourse, EQUAL_TO, Lecture::getCourse))
                .join(Lecture.class,
                        on(argABi(CourseConflict::getRightCourse), EQUAL_TO, Lecture::getCourse),
                        on(argBBi(Lecture::getPeriod), EQUAL_TO, Lecture::getPeriod))
                .filter(((courseConflict, lecture1, lecture2) -> lecture1 != lecture2))
                .penalize((courseConflict, lecture1, lecture2) -> courseConflict.getConflictCount());
    }

    protected void conflictingLecturesSameCourseInSamePeriod(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "conflictingLecturesSameCourseInSamePeriod", HardSoftScore.ofHard(1));
        c.from(Lecture.class)
                .join(Lecture.class,
                        on(Lecture::getCourse, EQUAL_TO, Lecture::getCourse),
                        on(Lecture::getPeriod, EQUAL_TO, Lecture::getPeriod),
                        on(Lecture::getId, LESS_THAN, Lecture::getId))
                .penalize((lecture1, lecture2) -> 1 + lecture1.getCurriculumList().size());
    }

    protected void roomOccupancy(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "roomOccupancy", HardSoftScore.ofHard(1));
        c.from(Lecture.class)
                .groupBy(Lecture::getPeriod, Lecture::getRoom, count())
                .filter((period, room, count) -> count > 1)
                .penalize((period, room, count) -> count - 1);
    }

    protected void unavailablePeriodPenalty(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "unavailablePeriodPenalty", HardSoftScore.ofHard(10));
        c.from(UnavailablePeriodPenalty.class)
                .join(Lecture.class,
                        on(UnavailablePeriodPenalty::getCourse, EQUAL_TO, Lecture::getCourse),
                        on(UnavailablePeriodPenalty::getPeriod, EQUAL_TO, Lecture::getPeriod))
                .penalize();
    }

    protected void roomCapacity(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "roomCapacity", HardSoftScore.ofSoft(1));
        c.from(Lecture.class)
                .filter(lecture -> lecture.getStudentSize() > lecture.getRoom().getCapacity())
                .penalize(lecture -> lecture.getStudentSize() - lecture.getRoom().getCapacity());
    }

    protected void minimumWorkingDays(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "minimumWorkingDays", HardSoftScore.ofSoft(5));
        c.from(Lecture.class)
                .groupBy(Lecture::getCourse, countDistinct(Lecture::getDay))
                .filter((course, dayCount) -> course.getMinWorkingDaySize() > dayCount)
                .penalize((course, dayCount) -> course.getMinWorkingDaySize() - dayCount);
    }

    protected void curriculumCompactness(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "curriculumCompactness", HardSoftScore.ofSoft(2));
        c.from(Curriculum.class)
                .join(Lecture.class)
                .filter((curriculum, lecture) -> lecture.getCurriculumList().contains(curriculum));
        // TODO .groupBy(curriculum, collectSortAndFilter(lecture, Lecture::getPeriod(), lectureList -> lectureList.filter(if no before or after))
        //      .flatten()
        //      .penalize();
        throw new UnsupportedOperationException();
    }

    protected void roomStability(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "roomStability", HardSoftScore.ofSoft(1));
        c.from(Lecture.class)
                .groupBy(Lecture::getCourse, countDistinct(Lecture::getRoom))
                .filter((course, roomCount) -> roomCount > 1)
                .penalize((course, roomCount) -> roomCount - 1);
    }

}
