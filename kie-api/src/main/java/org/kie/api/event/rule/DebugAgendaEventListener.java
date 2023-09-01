package org.kie.api.event.rule;

import java.io.PrintStream;

public class DebugAgendaEventListener
    implements
    AgendaEventListener {

    private PrintStream stream;

    public DebugAgendaEventListener() {
        this.stream =  System.err;
    }

    public DebugAgendaEventListener(PrintStream stream) {
        this.stream = stream;
    }

    public void matchCancelled(MatchCancelledEvent event) {
        stream.println( event );
    }

    public void matchCreated(MatchCreatedEvent event) {
        stream.println( event );
    }

    public void afterMatchFired(AfterMatchFiredEvent event) {
        stream.println( event );
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        stream.println( event );
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        stream.println( event );
    }

    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        stream.println( event );
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        stream.println( event );
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        stream.println( event );
    }

    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        stream.println( event );
    }

    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        stream.println( event );
    }

}
