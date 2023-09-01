package org.drools.core.event;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;

public class DefaultProcessEventListener implements ProcessEventListener {

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
    }

    public void beforeProcessStarted(ProcessStartedEvent event) {
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
    }

}
