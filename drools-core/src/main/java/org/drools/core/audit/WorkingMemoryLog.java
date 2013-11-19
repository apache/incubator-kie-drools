package org.drools.core.audit;

import org.drools.core.audit.event.LogEvent;

import java.util.List;

public class WorkingMemoryLog {

    private String version = "6.1";
    private List<LogEvent> events;
    private String engine;

    public WorkingMemoryLog() { }

    public WorkingMemoryLog(List<LogEvent> events, String engine) {
        this.events = events;
        this.engine = engine;
    }

    public String getVersion() {
        return version;
    }

    public List<LogEvent> getEvents() {
        return events;
    }

    public String getEngine() {
        return engine;
    }
}
