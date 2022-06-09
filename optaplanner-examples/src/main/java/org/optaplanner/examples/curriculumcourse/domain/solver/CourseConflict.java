package org.optaplanner.examples.curriculumcourse.domain.solver;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.curriculumcourse.domain.Course;

/**
 * Calculated during initialization, not modified during score calculation.
 */
public class CourseConflict implements Comparable<CourseConflict> {

    private static final Comparator<Course> COURSE_COMPARATOR = Comparator.comparingLong(Course::getId);
    private static final Comparator<CourseConflict> COMPARATOR = Comparator
            .comparing(CourseConflict::getLeftCourse, COURSE_COMPARATOR)
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
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CourseConflict other = (CourseConflict) o;
        return Objects.equals(leftCourse, other.leftCourse) &&
                Objects.equals(rightCourse, other.rightCourse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftCourse, rightCourse);
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
