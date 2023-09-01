package org.drools.testcoverage.common.model;

import java.util.Date;

public abstract class ParentEvent {

    public ParentEvent(Date eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    private Date eventTimestamp;

    public Date getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Date eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }
}
