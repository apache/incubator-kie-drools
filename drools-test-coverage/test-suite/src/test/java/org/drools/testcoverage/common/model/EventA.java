package org.drools.testcoverage.common.model;

public class EventA implements Event, Comparable<EventA> {

    private long timeValue;
    private long duration;

    public EventA() {
        //
    }

    @Override
    public long getTimeValue() {
        return timeValue;
    }

    @Override
    public void setTimeValue(final long timeValue) {
        this.timeValue = timeValue;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(final long duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(final EventA o) {
        if (timeValue > o.getTimeValue()) return 1;
        if (timeValue < o.getTimeValue()) return -1;
        // time of insertion is same for both events, so does not matter which is first
        return 1;
    }

    @Override
    public String toString() {
        return "EventA{" +
                "timeValue=" + timeValue +
                ", duration=" + duration +
                '}';
    }
}
