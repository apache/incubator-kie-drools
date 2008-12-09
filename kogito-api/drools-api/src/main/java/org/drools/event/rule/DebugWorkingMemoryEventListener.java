package org.drools.event.rule;

public class DebugWorkingMemoryEventListener
    implements
    WorkingMemoryEventListener {

    public void objectInserted(ObjectInsertedEvent event) {
        System.err.println( event );
    }

    public void objectRetracted(ObjectRetractedEvent event) {
        System.err.println( event );
    }

    public void objectUpdated(ObjectUpdatedEvent event) {
        System.err.println( event );
    }

}
