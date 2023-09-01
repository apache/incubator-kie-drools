package org.kie.api.event.rule;

import java.io.PrintStream;

public class DebugRuleRuntimeEventListener
    implements
    RuleRuntimeEventListener {

    private PrintStream stream;

    public DebugRuleRuntimeEventListener() {
        this.stream =  System.err;
    }

    public DebugRuleRuntimeEventListener(PrintStream stream) {
        this.stream = stream;
    }

    public void objectInserted(ObjectInsertedEvent event) {
        stream.println( event );
    }

    public void objectDeleted(ObjectDeletedEvent event) {
        stream.println( event );
    }

    public void objectUpdated(ObjectUpdatedEvent event) {
        stream.println( event );
    }

}
