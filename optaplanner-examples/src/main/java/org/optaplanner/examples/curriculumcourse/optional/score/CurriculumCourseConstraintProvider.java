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

package org.optaplanner.examples.curriculumcourse.optional.score;

import static org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore.ONE_HARD;
import static org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore.ofHard;
import static org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore.ofSoft;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countDistinct;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.curriculumcourse.domain.Curriculum;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;
import org.optaplanner.examples.curriculumcourse.domain.solver.CourseConflict;

public class CurriculumCourseConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                conflictingLecturesDifferentCourseInSamePeriod(factory),
                conflictingLecturesSameCourseInSamePeriod(factory),
                roomOccupancy(factory),
                unavailablePeriodPenalty(factory),
                roomCapacity(factory),
                minimumWorkingDays(factory),
                curriculumCompactness(factory),
                roomStability(factory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    Constraint conflictingLecturesDifferentCourseInSamePeriod(ConstraintFactory factory) {
        return factory.from(CourseConflict.class)
                .join(Lecture.class,
                        equal(CourseConflict::getLeftCourse, Lecture::getCourse))
                .join(Lecture.class,
                        equal((courseConflict, lecture1) -> courseConflict.getRightCourse(), Lecture::getCourse),
                        equal((courseConflict, lecture1) -> lecture1.getPeriod(), Lecture::getPeriod))
                .filter(((courseConflict, lecture1, lecture2) -> lecture1 != lecture2))
                .penalize("conflictingLecturesDifferentCourseInSamePeriod", ONE_HARD,
                        (courseConflict, lecture1, lecture2) -> courseConflict.getConflictCount());
    }

    Constraint conflictingLecturesSameCourseInSamePeriod(ConstraintFactory factory) {
        return factory.fromUniquePair(Lecture.class,
                equal(Lecture::getPeriod),
                equal(Lecture::getCourse))
                .penalize("conflictingLecturesSameCourseInSamePeriod", ONE_HARD,
                        (lecture1, lecture2) -> 1 + lecture1.getCurriculumSet().size());
    }

    Constraint roomOccupancy(ConstraintFactory factory) {
        return factory.fromUniquePair(Lecture.class,
                equal(Lecture::getRoom),
                equal(Lecture::getPeriod))
                .penalize("roomOccupancy", ONE_HARD);
    }

    Constraint unavailablePeriodPenalty(ConstraintFactory factory) {
        return factory.from(UnavailablePeriodPenalty.class)
                .join(Lecture.class,
                        equal(UnavailablePeriodPenalty::getCourse, Lecture::getCourse),
                        equal(UnavailablePeriodPenalty::getPeriod, Lecture::getPeriod))
                .penalize("unavailablePeriodPenalty", ofHard(10));
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    Constraint roomCapacity(ConstraintFactory factory) {
        return factory.from(Lecture.class)
                .filter(lecture -> lecture.getStudentSize() > lecture.getRoom().getCapacity())
                .penalize("roomCapacity", ofSoft(1),
                        lecture -> lecture.getStudentSize() - lecture.getRoom().getCapacity());
    }

    Constraint minimumWorkingDays(ConstraintFactory factory) {
        return factory.from(Lecture.class)
                .groupBy(Lecture::getCourse, countDistinct(Lecture::getDay))
                .filter((course, dayCount) -> course.getMinWorkingDaySize() > dayCount)
                .penalize("minimumWorkingDays", ofSoft(5),
                        (course, dayCount) -> course.getMinWorkingDaySize() - dayCount);
    }

    Constraint curriculumCompactness(ConstraintFactory factory) {
        return factory.from(Curriculum.class)
                .join(Lecture.class,
                        filtering((curriculum, lecture) -> lecture.getCurriculumSet().contains(curriculum)))
                .ifNotExists(Lecture.class,
                        equal((curriculum, lecture) -> lecture.getDay(), Lecture::getDay),
                        equal((curriculum, lecture) -> lecture.getTimeslotIndex(), lecture -> lecture.getTimeslotIndex() + 1),
                        filtering((curriculum, lectureA, lectureB) -> lectureB.getCurriculumSet().contains(curriculum)))
                .ifNotExists(Lecture.class,
                        equal((curriculum, lecture) -> lecture.getDay(), Lecture::getDay),
                        equal((curriculum, lecture) -> lecture.getTimeslotIndex(), lecture -> lecture.getTimeslotIndex() - 1),
                        filtering((curriculum, lectureA, lectureB) -> lectureB.getCurriculumSet().contains(curriculum)))
                .penalize("curriculumCompactness", ofSoft(2));
    }

    Constraint roomStability(ConstraintFactory factory) {
        return factory.from(Lecture.class)
                .groupBy(Lecture::getCourse, countDistinct(Lecture::getRoom))
                .filter((course, roomCount) -> roomCount > 1)
                .penalize("roomStability", ofSoft(1),
                        (course, roomCount) -> roomCount - 1);
    }

}
