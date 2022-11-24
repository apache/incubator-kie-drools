package org.optaplanner.examples.examination.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.examples.examination.domain.solver.PeriodUpdatingVariableListener;

@PlanningEntity
public class FollowingExam extends Exam {

    protected LeadingExam leadingExam;

    // Shadow variables
    protected Period period;

    public LeadingExam getLeadingExam() {
        return leadingExam;
    }

    public void setLeadingExam(LeadingExam leadingExam) {
        this.leadingExam = leadingExam;
    }

    @Override
    @ShadowVariable(variableListenerClass = PeriodUpdatingVariableListener.class,
            sourceEntityClass = LeadingExam.class, sourceVariableName = "period")
    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public FollowingExam withId(long id) {
        this.setId(id);
        return this;
    }

    public FollowingExam withTopic(Topic topic) {
        this.setTopic(topic);
        return this;
    }

    public FollowingExam withRoom(Room room) {
        this.setRoom(room);
        return this;
    }

    public FollowingExam withPeriod(Period period) {
        this.setPeriod(period);
        return this;
    }

    public FollowingExam withLeadingExam(LeadingExam leadingExam) {
        this.setLeadingExam(leadingExam);
        return this;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
