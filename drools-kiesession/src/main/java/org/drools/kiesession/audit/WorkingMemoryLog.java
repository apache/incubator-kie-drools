package org.drools.kiesession.audit;

import java.util.List;

public class WorkingMemoryLog {

    private String version = "6.1";
    private List<LogEvent> events;

    public WorkingMemoryLog() { }

    public WorkingMemoryLog(List<LogEvent> events) {
        this.events = events;
    }

    public String getVersion() {
        return version;
    }

    public List<LogEvent> getEvents() {
        return events;
    }
}
