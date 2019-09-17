/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.curriculumcourse.domain.solver;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.examples.curriculumcourse.domain.Course;

/**
 * Calculated during initialization, not modified during score calculation.
 */
public class CourseConflict implements Serializable,
        Comparable<CourseConflict> {

    private static final Comparator<Course> COURSE_COMPARATOR = Comparator.comparingLong(Course::getId);
    private static final Comparator<CourseConflict> COMPARATOR =
            Comparator.comparing(CourseConflict::getLeftCourse, COURSE_COMPARATOR)
                    .thenComparing(CourseConflict::getRightCourse, COURSE_COMPARATOR);

    private final Course leftCourse;
    private final Course rightCourse;
    private final int conflictCount;

    public CourseConflict(Course leftCourse, Course rightCourse, int conflictCount) {
        this.leftCourse = leftCourse;
        this.rightCourse = rightCourse;
        this.conflictCount = conflictCount;
    }

    public Course getLeftCourse() {
        return leftCourse;
    }

    public Course getRightCourse() {
        return rightCourse;
    }

    public int getConflictCount() {
        return conflictCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CourseConflict) {
            CourseConflict other = (CourseConflict) o;
            return new EqualsBuilder()
                    .append(leftCourse, other.leftCourse)
                    .append(rightCourse, other.rightCourse)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftCourse)
                .append(rightCourse)
                .toHashCode();
    }

    @Override
    public int compareTo(CourseConflict other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return leftCourse + " & " + rightCourse;
    }
}
