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

package org.optaplanner.examples.curriculumcourse.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.common.JoinerType;
import org.optaplanner.core.api.score.stream.common.Joiners;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.solver.CourseConflict;

import static org.optaplanner.core.api.score.stream.common.JoinerType.EQUAL_TO;
import static org.optaplanner.core.api.score.stream.common.Joiners.*;

public class CourseScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        conflictingLecturesDifferentCourseInSamePeriod(constraintFactory);
    }

    protected void conflictingLecturesDifferentCourseInSamePeriod(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "conflictingLecturesDifferentCourseInSamePeriod", HardSoftScore.ofHard(1));
        c.from(CourseConflict.class)
                .join(c.from(Lecture.class),
                        on(CourseConflict::getLeftCourse, EQUAL_TO, Lecture::getCourse))
                .join(c.from(Lecture.class),
                        on(firstBi(CourseConflict::getLeftCourse), EQUAL_TO, Lecture::getCourse))
                .filter(((courseConflict, lecture1, lecture2) -> lecture1 != lecture2))
                .penalize();
    }

}
