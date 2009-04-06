package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;

public class SetGlobalCommand
    implements
    Command<Void> {

    private String identifier;
    private Object object;
    
    private String outIdentifier;
    
    private boolean out;

    public SetGlobalCommand(String identifier,
                            Object object) {
        this.identifier = identifier;
        this.object = object;
    }

    public Void execute(ReteooWorkingMemory session) {
        if ( this.out ) {
            session.getExecutionResult().getResults().put( ( this.outIdentifier != null ) ? this.outIdentifier : this.identifier, 
                                                                object );
        }
        
        session.setGlobal( this.identifier,
                           this.object );
        return null;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Object getObject() {
        return this.object;
    }

    public String getOutIdentifier() {
        return this.outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
        this.out = true;
    }

    public boolean isOut() {
        return this.out;
    }

    public void setOut(boolean out) {
        this.out = out;
    }    
    
    public String toString() {
        return "session.setGlobal(" + this.identifier + ", " + this.object + ");";
    }

}
