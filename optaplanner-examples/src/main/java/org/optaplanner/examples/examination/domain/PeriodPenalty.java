package org.optaplanner.examples.examination.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PeriodPenalty")
public class PeriodPenalty extends AbstractPersistable {

    public PeriodPenalty() {
    }

    public PeriodPenalty(Topic leftTopic, Topic rightTopic, PeriodPenaltyType periodPenaltyType) {
        this.leftTopic = leftTopic;
        this.rightTopic = rightTopic;
        this.periodPenaltyType = periodPenaltyType;
    }

    private PeriodPenaltyType periodPenaltyType;
    private Topic leftTopic;
    private Topic rightTopic;

    public PeriodPenaltyType getPeriodPenaltyType() {
        return periodPenaltyType;
    }

    public void setPeriodPenaltyType(PeriodPenaltyType periodPenaltyType) {
        this.periodPenaltyType = periodPenaltyType;
    }

    public Topic getLeftTopic() {
        return leftTopic;
    }

    public void setLeftTopic(Topic leftTopic) {
        this.leftTopic = leftTopic;
    }

    public Topic getRightTopic() {
        return rightTopic;
    }

    public void setRightTopic(Topic rightTopic) {
        this.rightTopic = rightTopic;
    }

    @Override
    public String toString() {
        return periodPenaltyType + "@" + leftTopic.getId() + "&" + rightTopic.getId();
    }

}
