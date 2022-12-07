package org.optaplanner.examples.curriculumcourse.domain.solver;

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.comparingLong;

import java.util.Comparator;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.curriculumcourse.domain.Course;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;

public class LectureDifficultyWeightFactory implements SelectionSorterWeightFactory<CourseSchedule, Lecture> {

    @Override
    public LectureDifficultyWeight createSorterWeight(CourseSchedule schedule, Lecture lecture) {
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

        private static final Comparator<LectureDifficultyWeight> COMPARATOR = comparingInt(
                (LectureDifficultyWeight c) -> c.lecture.getCurriculumSet().size())
                .thenComparing(c -> c.unavailablePeriodPenaltyCount)
                .thenComparingInt(c -> c.lecture.getCourse().getLectureSize())
                .thenComparingInt(c -> c.lecture.getCourse().getStudentSize())
                .thenComparing(c -> c.lecture.getCourse().getMinWorkingDaySize())
                .thenComparing(c -> c.lecture, comparingLong(Lecture::getId));

        private final Lecture lecture;
        private final int unavailablePeriodPenaltyCount;

        public LectureDifficultyWeight(Lecture lecture, int unavailablePeriodPenaltyCount) {
            this.lecture = lecture;
            this.unavailablePeriodPenaltyCount = unavailablePeriodPenaltyCount;
        }

        @Override
        public int compareTo(LectureDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
