package org.drools.compiler.integrationtests.eventgenerator;

import org.kie.api.runtime.KieSession;


public class SimpleEventListener extends AbstractEventListener {

    public SimpleEventListener(KieSession ksession) {
        super(ksession);
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
