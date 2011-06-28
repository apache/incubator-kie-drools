package org.drools.planner.examples.curriculumcourse.solver.solution.initializer;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.curriculumcourse.domain.Course;
import org.drools.planner.examples.curriculumcourse.domain.Lecture;

public class LectureDifficultyWeight implements Comparable<LectureDifficultyWeight> {

    private final Lecture lecture;
    private final int unavailablePeriodConstraintCount;

    public LectureDifficultyWeight(Lecture lecture, int unavailablePeriodConstraintCount) {
        this.lecture = lecture;
        this.unavailablePeriodConstraintCount = unavailablePeriodConstraintCount;
    }

    public int compareTo(LectureDifficultyWeight other) {
        Course course = lecture.getCourse();
        Course otherCourse = other.lecture.getCourse();
        return new CompareToBuilder()
                .append(otherCourse.getCurriculumList().size(), course.getCurriculumList().size()) // Descending
                .append(other.unavailablePeriodConstraintCount, unavailablePeriodConstraintCount) // Descending
                .append(otherCourse.getLectureSize(), course.getLectureSize()) // Descending
                .append(otherCourse.getStudentSize(), course.getStudentSize()) // Descending
                .append(otherCourse.getMinWorkingDaySize(), course.getMinWorkingDaySize()) // Descending
                .append(lecture.getId(), other.lecture.getId()) // Ascending
                .toComparison();
    }

}
