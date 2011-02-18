package org.drools.integrationtests.eventgenerator;

import org.drools.WorkingMemory;


public class SimpleEventListener extends AbstractEventListener {

    public SimpleEventListener(WorkingMemory wm) {
        super(wm);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.event.AbstractEventListener#generatedEventSent(com.event.Event)
     */
    @Override
    public void generatedEventSent(Event e) {
        addEventToWM(e);
    }

}
