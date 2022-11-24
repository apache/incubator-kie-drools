package org.optaplanner.examples.examination.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

public class PeriodPenalty extends AbstractPersistableJackson {

    public PeriodPenalty() { // For Jackson.
    }

    public PeriodPenalty(long id, Topic leftTopic, Topic rightTopic, PeriodPenaltyType periodPenaltyType) {
        super(id);
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
