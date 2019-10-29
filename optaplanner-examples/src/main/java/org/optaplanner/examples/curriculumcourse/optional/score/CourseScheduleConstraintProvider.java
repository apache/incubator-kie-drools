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
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;
import org.optaplanner.examples.curriculumcourse.domain.solver.CourseConflict;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.*;
import static org.optaplanner.core.api.score.stream.Joiners.*;

public class CourseScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                // TODO replace the 2 conflictingLectures constraints with these
                // teacherConflict(factory),
                // curriculumConflict(factory),
                conflictingLecturesDifferentCourseInSamePeriod(factory),
                conflictingLecturesSameCourseInSamePeriod(factory),
                roomOccupancy(factory), // TODO Doesn't work
                unavailablePeriodPenalty(factory),
                roomCapacity(factory),
                minimumWorkingDays(factory),
                curriculumCompactness(factory), // TODO Implement it
                roomStability(factory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    private Constraint teacherConflict(ConstraintFactory factory) {
        return factory.from(Lecture.class)
                .join(Lecture.class,
                        equal(Lecture::getTeacher),
                        equal(Lecture::getPeriod),
                        lessThan(Lecture::getId))
                .penalize("teacherConflict",
                        HardSoftScore.ofHard(1));
    }

    private Constraint curriculumConflict(ConstraintFactory factory) {
        return factory.from(Lecture.class)
                .join(Lecture.class,
                        equal(Lecture::getPeriod),
                        lessThan(Lecture::getId))
                .filter((lecture1, lecture2) -> lecture1.getCurriculumList().stream()
                        .anyMatch(lecture -> lecture2.getCurriculumList().contains(lecture)))
                .penalize("curriculumConflict",
                        HardSoftScore.ofHard(1),
                        (lecture1, lecture2) -> (int) lecture1.getCurriculumList().stream()
                        .filter(lecture -> lecture2.getCurriculumList().contains(lecture))
                        .count());
    }

    private Constraint conflictingLecturesDifferentCourseInSamePeriod(ConstraintFactory factory) {
        return factory.from(CourseConflict.class)
                .join(Lecture.class,
                        equal(CourseConflict::getLeftCourse, Lecture::getCourse))
                .join(Lecture.class,
                        equal((courseConflict, lecture1) -> courseConflict.getRightCourse(), Lecture::getCourse),
                        equal((courseConflict, lecture1) -> lecture1.getPeriod(), Lecture::getPeriod))
                .filter(((courseConflict, lecture1, lecture2) -> lecture1 != lecture2))
                .penalize("conflictingLecturesDifferentCourseInSamePeriod",
                        HardSoftScore.ofHard(1),
                        (courseConflict, lecture1, lecture2) -> courseConflict.getConflictCount());
    }

    private Constraint conflictingLecturesSameCourseInSamePeriod(ConstraintFactory factory) {
        return factory.from(Lecture.class)
                .join(Lecture.class,
                        equal(Lecture::getCourse, Lecture::getCourse),
                        equal(Lecture::getPeriod, Lecture::getPeriod),
                        lessThan(Lecture::getId, Lecture::getId))
                .penalize("conflictingLecturesSameCourseInSamePeriod",
                        HardSoftScore.ofHard(1),
                        (lecture1, lecture2) -> 1 + lecture1.getCurriculumList().size());
    }

    private Constraint roomOccupancy(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for tri-grouping.");
//        return factory.from(Lecture.class)
//                .groupBy(Lecture::getPeriod, Lecture::getRoom, count())
//                .filter((period, room, count) -> count > 1)
//                .penalize("roomOccupancy",
//                        HardSoftScore.ofHard(1),
//                        (period, room, count) -> count - 1);
    }

    private Constraint unavailablePeriodPenalty(ConstraintFactory factory) {
        return factory.from(UnavailablePeriodPenalty.class)
                .join(Lecture.class,
                        equal(UnavailablePeriodPenalty::getCourse, Lecture::getCourse),
                        equal(UnavailablePeriodPenalty::getPeriod, Lecture::getPeriod))
                .penalize("unavailablePeriodPenalty",
                        HardSoftScore.ofHard(10));
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    private Constraint roomCapacity(ConstraintFactory factory) {
        return factory.from(Lecture.class)
                .filter(lecture -> lecture.getStudentSize() > lecture.getRoom().getCapacity())
                .penalize("roomCapacity",
                        HardSoftScore.ofSoft(1),
                        lecture -> lecture.getStudentSize() - lecture.getRoom().getCapacity());
    }

    private Constraint minimumWorkingDays(ConstraintFactory factory) {
        return factory.from(Lecture.class)
                .groupBy(Lecture::getCourse, countDistinct(Lecture::getDay))
                .filter((course, dayCount) -> course.getMinWorkingDaySize() > dayCount)
                .penalize("minimumWorkingDays",
                        HardSoftScore.ofSoft(5),
                        (course, dayCount) -> course.getMinWorkingDaySize() - dayCount);
    }

    private Constraint curriculumCompactness(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for bi-grouping.");
//        return factory.from(Curriculum.class)
//                .join(Lecture.class)
//                .filter((curriculum, lecture) -> lecture.getCurriculumList().contains(curriculum));
//                .groupBy(curriculum, collectSortAndFilter(lecture, Lecture::getPeriod(), lectureList -> lectureList.filter(if no before or after))
//                .flatten()
//                .penalize("curriculumCompactness",
//                        HardSoftScore.ofSoft(2));
    }

    private Constraint roomStability(ConstraintFactory factory) {
        return factory.from(Lecture.class)
                .groupBy(Lecture::getCourse, countDistinct(Lecture::getRoom))
                .filter((course, roomCount) -> roomCount > 1)
                .penalize("roomStability",
                        HardSoftScore.ofSoft(1),
                        (course, roomCount) -> roomCount - 1);
    }

}
