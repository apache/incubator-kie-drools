package org.drools.mvel.integrationtests.facts;

import java.io.Serializable;
import java.util.Date;

public class BasicEvent implements Serializable {

    private static final long serialVersionUID = 2172618811749631685L;

    private Date eventTimestamp;
    private Long eventDuration;
    private String name;

    public BasicEvent(final Date eventTimestamp, final Long eventDuration, final String name) {
        this.eventTimestamp = eventTimestamp;
        this.eventDuration = eventDuration;
        this.name = name;
    }

    public BasicEvent() {
    }

    public Date getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(final Date eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public Long getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(final Long eventDuration) {
        this.eventDuration = eventDuration;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
