package org.drools.vsm.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.drools.eventmessaging.EventTriggerTransport;
import org.drools.eventmessaging.Payload;
import org.drools.task.service.Command;
import org.drools.task.service.CommandName;
import org.drools.vsm.GenericIoWriter;
import org.drools.vsm.Message;

public class GenericEventTransport implements EventTriggerTransport {
    private String uuid;
    private Map<String, GenericIoWriter> sessions;
    private int responseId;
    private boolean remove;
    
    GenericEventTransport(String uuid, int responseId, Map<String, GenericIoWriter> sessions, boolean remove) {
        this.uuid = uuid;
        this.responseId = responseId;
        this.sessions = sessions;
        this.remove = remove;
    }

    public void trigger(Payload payload) {        
        GenericIoWriter session = sessions.get( uuid );
        List args = new ArrayList( 1 );
        args.add( payload );
        Command resultsCmnd = new Command( responseId, CommandName.EventTriggerResponse, args);
        session.write(new Message(Integer.parseInt(uuid),
                                        responseId,
                                        false,
                                        resultsCmnd ), null);
    }
    
    public boolean isRemove() {
        return this.remove;
    }
}
