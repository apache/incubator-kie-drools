package org.drools.testcoverage.common.model;

public class EventB extends EventA {

    @Override
    public String toString() {
        return "EventB{" +
                "timeValue=" + getTimeValue() +
                ", duration=" + getDuration() +
                '}';
    }
}
