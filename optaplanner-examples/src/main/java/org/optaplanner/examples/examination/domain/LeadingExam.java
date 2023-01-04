package org.optaplanner.examples.examination.domain;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class LeadingExam extends Exam {

    protected List<FollowingExam> followingExamList;

    // Planning variables: changes during planning, between score calculations.
    protected Period period;

    public List<FollowingExam> getFollowingExamList() {
        return followingExamList;
    }

    public void setFollowingExamList(List<FollowingExam> followingExamList) {
        this.followingExamList = followingExamList;
    }

    @Override
    @PlanningVariable
    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public LeadingExam withId(long id) {
        this.setId(id);
        return this;
    }

    public LeadingExam withTopic(Topic topic) {
        this.setTopic(topic);
        return this;
    }

    public LeadingExam withRoom(Room room) {
        this.setRoom(room);
        return this;
    }

    public LeadingExam withPeriod(Period period) {
        this.setPeriod(period);
        return this;
    }

    public LeadingExam withFollowingExamList(List<FollowingExam> followingExamList) {
        this.setFollowingExamList(followingExamList);
        return this;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
