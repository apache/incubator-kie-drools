package org.drools.testcoverage.common.model;

public interface Event {

    long getTimeValue();

    long getDuration();

    void setTimeValue(long timeValue);

    void setDuration(long duration);
}
