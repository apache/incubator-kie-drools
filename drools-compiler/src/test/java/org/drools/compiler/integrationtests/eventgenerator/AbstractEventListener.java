package org.drools.compiler.integrationtests.eventgenerator;

import org.kie.api.runtime.KieSession;


public abstract class AbstractEventListener {

    KieSession ksession;

    public AbstractEventListener(KieSession ksession) {
        this.ksession = ksession;
    }

    public void addEventToWM (Event ev){
        ksession.insert(ev);
        ksession.fireAllRules();
    }

    // send generated event and execute corresponding actions
    public abstract void generatedEventSent(Event e);

}
