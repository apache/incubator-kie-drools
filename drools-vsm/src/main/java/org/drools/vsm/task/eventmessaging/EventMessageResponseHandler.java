package org.drools.vsm.task.eventmessaging;

import org.drools.eventmessaging.Payload;
import org.drools.vsm.MessageResponseHandler;


public interface EventMessageResponseHandler extends MessageResponseHandler {
    public void execute(Payload payload);
}
