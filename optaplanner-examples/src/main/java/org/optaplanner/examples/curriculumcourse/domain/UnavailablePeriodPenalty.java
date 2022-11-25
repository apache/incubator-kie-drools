package org.optaplanner.examples.curriculumcourse.domain;

import static java.util.Objects.requireNonNull;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class UnavailablePeriodPenalty extends AbstractPersistable {

    private Course course;
    private Period period;

    public UnavailablePeriodPenalty() {
    }

    public UnavailablePeriodPenalty(long id, Course course, Period period) {
        super(id);
        this.course = requireNonNull(course);
        this.period = requireNonNull(period);
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return course + "@" + period;
    }

}
