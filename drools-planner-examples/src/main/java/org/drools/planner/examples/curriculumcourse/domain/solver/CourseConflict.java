/*
 * Copyright 2013 JBoss Inc
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

import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.examples.curriculumcourse.domain.Course;

/**
 * Calculated during initialization, not modified during score calculation.
 */
public class CourseConflict implements Serializable, Comparable<CourseConflict> {

    private Course leftCourse;
    private Course rightCourse;

    public CourseConflict(Course leftCourse, Course rightCourse) {
        this.leftCourse = leftCourse;
        this.rightCourse = rightCourse;
    }

    public Course getLeftCourse() {
        return leftCourse;
    }

    public void setLeftCourse(Course leftCourse) {
        this.leftCourse = leftCourse;
    }

    public Course getRightCourse() {
        return rightCourse;
    }

    public void setRightCourse(Course rightCourse) {
        this.rightCourse = rightCourse;
    }

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

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftCourse)
                .append(rightCourse)
                .toHashCode();
    }

    public int compareTo(CourseConflict other) {
        return new CompareToBuilder()
                .append(leftCourse, other.leftCourse)
                .append(rightCourse, other.rightCourse)
                .toComparison();
    }

    @Override
    public String toString() {
        return leftCourse + " & " + rightCourse;
    }

}
