package org.optaplanner.examples.curriculumcourse.score;

import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.examples.curriculumcourse.domain.Course;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Curriculum;
import org.optaplanner.examples.curriculumcourse.domain.Day;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.Period;
import org.optaplanner.examples.curriculumcourse.domain.Room;
import org.optaplanner.examples.curriculumcourse.domain.Teacher;
import org.optaplanner.examples.curriculumcourse.domain.Timeslot;
import org.optaplanner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;
import org.optaplanner.examples.curriculumcourse.domain.solver.CourseConflict;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class CurriculumCourseConstraintProviderTest
        extends AbstractConstraintProviderTest<CurriculumCourseConstraintProvider, CourseSchedule> {

    private static final Curriculum CURRICULUM_1 = new Curriculum(1, "Curriculum1");
    private static final Curriculum CURRICULUM_2 = new Curriculum(2, "Curriculum2");
    private static final Teacher TEACHER_1 = new Teacher(1, "Teacher1");
    private static final Teacher TEACHER_2 = new Teacher(2, "Teacher2");
    private static final Course COURSE_1 = new Course(1, "Course1", TEACHER_1, 10, 20, 3, CURRICULUM_1);
    private static final Course COURSE_2 = new Course(2, "Course2", TEACHER_2, 10, 10, 2, CURRICULUM_2);
    private static final Course COURSE_3 = new Course(3, "Course3", TEACHER_1, 10, 5, 1, CURRICULUM_2);
    private static final Room ROOM_1 = new Room(1, "Room1", 10);
    private static final Room ROOM_2 = new Room(2, "Room2", 20);
    private static final Timeslot FIRST_TIMESLOT = new Timeslot(0);
    private static final Timeslot SECOND_TIMESLOT = new Timeslot(1);
    private static final Day MONDAY = new Day(0);
    private static final Day TUESDAY = new Day(1);
    private static final Period PERIOD_1_MONDAY = new Period(0, MONDAY, FIRST_TIMESLOT);
    private static final Period PERIOD_2_MONDAY = new Period(1, MONDAY, SECOND_TIMESLOT);
    private static final Period PERIOD_1_TUESDAY = new Period(2, TUESDAY, FIRST_TIMESLOT);

    @ConstraintProviderTest
    void conflictingLecturesDifferentCourseInSamePeriod(
            ConstraintVerifier<CurriculumCourseConstraintProvider, CourseSchedule> constraintVerifier) {
        int conflictCount = 2;
        CourseConflict courseConflict = new CourseConflict(COURSE_1, COURSE_2, conflictCount);

        // Make sure that unassigned lectures are ignored.
        Lecture unassignedLecture1 = new Lecture(0, COURSE_1, null, null);
        Lecture unassignedLecture2 = new Lecture(1, COURSE_1, null, null);
        // Make sure that different rooms are irrelevant.
        Lecture assignedLecture1 = new Lecture(2, COURSE_1, PERIOD_1_MONDAY, ROOM_1);
        Lecture assignedLecture2 = new Lecture(3, COURSE_2, PERIOD_1_MONDAY, ROOM_2);
        constraintVerifier.verifyThat(CurriculumCourseConstraintProvider::conflictingLecturesDifferentCourseInSamePeriod)
                .given(courseConflict, unassignedLecture1, unassignedLecture2, assignedLecture1, assignedLecture2)
                .penalizesBy(conflictCount);
    }

    @ConstraintProviderTest
    void conflictingLecturesSameCourseInSamePeriod(
            ConstraintVerifier<CurriculumCourseConstraintProvider, CourseSchedule> constraintVerifier) {
        // Make sure that unassigned lectures are ignored.
        Lecture unassignedLecture1 = new Lecture(0, COURSE_1, null, null);
        Lecture unassignedLecture2 = new Lecture(1, COURSE_1, null, null);
        // Make sure that different rooms are irrelevant.
        Lecture assignedLecture1 = new Lecture(2, COURSE_1, PERIOD_1_MONDAY, ROOM_1);
        Lecture assignedLecture2 = new Lecture(3, COURSE_1, PERIOD_1_MONDAY, ROOM_2);
        // Make sure that only pairs with the same course and same period are counted.
        Lecture assignedLecture3 = new Lecture(4, COURSE_2, PERIOD_1_MONDAY, ROOM_1);
        constraintVerifier.verifyThat(CurriculumCourseConstraintProvider::conflictingLecturesSameCourseInSamePeriod)
                .given(unassignedLecture1, unassignedLecture2, assignedLecture1, assignedLecture2, assignedLecture3)
                .penalizesBy(2);
    }

    @ConstraintProviderTest
    void roomOccupancy(ConstraintVerifier<CurriculumCourseConstraintProvider, CourseSchedule> constraintVerifier) {
        // Make sure that unassigned lectures are ignored.
        Lecture unassignedLecture = new Lecture(0, COURSE_1, null, null);
        // Make sure only unique pairs are counted.
        Lecture assignedLecture1 = new Lecture(2, COURSE_1, PERIOD_1_MONDAY, ROOM_1);
        Lecture assignedLecture2 = new Lecture(3, COURSE_2, PERIOD_1_MONDAY, ROOM_1);
        Lecture assignedLecture3 = new Lecture(4, COURSE_3, PERIOD_1_MONDAY, ROOM_1);
        // Make sure that different rooms are irrelevant.
        Lecture assignedLecture4 = new Lecture(5, COURSE_1, PERIOD_1_MONDAY, ROOM_2);
        constraintVerifier.verifyThat(CurriculumCourseConstraintProvider::roomOccupancy)
                .given(unassignedLecture, assignedLecture1, assignedLecture2, assignedLecture3, assignedLecture4)
                .penalizesBy(3);
    }

    @ConstraintProviderTest
    void unavailablePeriodPenalty(ConstraintVerifier<CurriculumCourseConstraintProvider, CourseSchedule> constraintVerifier) {
        UnavailablePeriodPenalty unavailablePeriodPenalty = new UnavailablePeriodPenalty(0, COURSE_1, PERIOD_1_MONDAY);
        Lecture matchingLecture = new Lecture(0, COURSE_1, PERIOD_1_MONDAY, ROOM_1);
        Lecture wrongCourseLecture = new Lecture(1, COURSE_2, PERIOD_1_MONDAY, ROOM_2);
        Lecture wrongPeriodLecture = new Lecture(2, COURSE_1, PERIOD_2_MONDAY, ROOM_1);
        constraintVerifier.verifyThat(CurriculumCourseConstraintProvider::unavailablePeriodPenalty)
                .given(unavailablePeriodPenalty, matchingLecture, wrongCourseLecture, wrongPeriodLecture)
                .penalizesBy(1);
    }

    @ConstraintProviderTest
    void roomCapacity(ConstraintVerifier<CurriculumCourseConstraintProvider, CourseSchedule> constraintVerifier) {
        Lecture overbookedLecture = new Lecture(0, COURSE_1, PERIOD_1_MONDAY, ROOM_1);
        Lecture packedLecture = new Lecture(1, COURSE_2, PERIOD_2_MONDAY, ROOM_1);
        Lecture nearlyEmptyLecture = new Lecture(2, COURSE_3, PERIOD_1_TUESDAY, ROOM_2);
        constraintVerifier.verifyThat(CurriculumCourseConstraintProvider::roomCapacity)
                .given(overbookedLecture, packedLecture, nearlyEmptyLecture)
                .penalizesBy(10); // Only penalizes the overbooked lecture.
    }

    @ConstraintProviderTest
    void minimumWorkingDays(ConstraintVerifier<CurriculumCourseConstraintProvider, CourseSchedule> constraintVerifier) {
        Lecture meetsMinimum = new Lecture(0, COURSE_3, PERIOD_1_MONDAY, ROOM_1);
        Lecture doesNotMeetMinimumBy1 = new Lecture(1, COURSE_2, PERIOD_1_MONDAY, ROOM_2);
        Lecture doesNotMeetMinimumBy2 = new Lecture(2, COURSE_1, PERIOD_2_MONDAY, ROOM_1);
        constraintVerifier.verifyThat(CurriculumCourseConstraintProvider::minimumWorkingDays)
                .given(meetsMinimum, doesNotMeetMinimumBy1, doesNotMeetMinimumBy2)
                .penalizesBy(3);
    }

    @ConstraintProviderTest
    void curriculumCompactness(ConstraintVerifier<CurriculumCourseConstraintProvider, CourseSchedule> constraintVerifier) {
        Lecture lectureInCurriculumWithoutOthers1 = new Lecture(0, COURSE_1, PERIOD_1_MONDAY, ROOM_1);
        Lecture lectureInCurriculumWithoutOthers2 = new Lecture(1, COURSE_2, PERIOD_1_MONDAY, ROOM_1);
        constraintVerifier.verifyThat(CurriculumCourseConstraintProvider::curriculumCompactness)
                .given(CURRICULUM_1, CURRICULUM_2, lectureInCurriculumWithoutOthers1, lectureInCurriculumWithoutOthers2)
                .penalizesBy(2);
    }

    @ConstraintProviderTest
    void roomStability(ConstraintVerifier<CurriculumCourseConstraintProvider, CourseSchedule> constraintVerifier) {
        Lecture lectureOfSameCourse1 = new Lecture(0, COURSE_1, PERIOD_1_MONDAY, ROOM_1);
        Lecture lectureOfSameCourse2 = new Lecture(1, COURSE_1, PERIOD_1_MONDAY, ROOM_2);
        Lecture lectureOfSameCourse3 = new Lecture(2, COURSE_1, PERIOD_2_MONDAY, ROOM_1);
        Lecture lectureOfDifferentCourse = new Lecture(3, COURSE_2, PERIOD_2_MONDAY, ROOM_1);
        constraintVerifier.verifyThat(CurriculumCourseConstraintProvider::roomStability)
                .given(lectureOfSameCourse1, lectureOfSameCourse2, lectureOfSameCourse3, lectureOfDifferentCourse)
                .penalizesBy(1); // lectureOfSameCourse2 is penalized
    }

    @Override
    protected ConstraintVerifier<CurriculumCourseConstraintProvider, CourseSchedule> createConstraintVerifier() {
        return ConstraintVerifier.build(new CurriculumCourseConstraintProvider(), CourseSchedule.class, Lecture.class);
    }
}
