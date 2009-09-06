package org.drools.vsm;

import java.io.Serializable;
import java.util.Map;

import org.drools.command.Command;

public class Message
    implements
    Serializable {
    private int                  sessionId;
    private int                  responseId;
    private boolean              async;
    private Object               payload;

    public Message(int sessionId,
                   int responseId,
                   boolean async,
                   Map<String, Integer> contextVars,                   
                   Object payload) {
        this.sessionId = sessionId;
        this.async = async;
        this.responseId = responseId;
        this.payload = payload;
    }

    public Message(int sessionId,
                   Map<String, Integer> contextVars,
                   Object payload) {
        this.sessionId = sessionId;
        this.responseId = -1;
        this.payload = payload;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getResponseId() {
        return responseId;
    }
    
    

    public boolean isAsync() {
        return async;
    }

    public Object getPayload() {
        return payload;
    }

    public String toString() {
        return "sessionId=" + this.sessionId + " responseId=" + responseId + " async=" + this.async + " payload=" + this.payload;
    }

}
