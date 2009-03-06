package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;

public class GetGlobalCommand
    implements
    Command<Object> {

    private String identifier;
    private String outIdentifier;

    public GetGlobalCommand(String identifier) {
        this.identifier = identifier;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Object execute(ReteooWorkingMemory session) {
        Object object = session.getGlobal( this.identifier );
        session.getBatchExecutionResult().getResults().put( (this.outIdentifier != null) ? this.outIdentifier : this.identifier,
                                                            object );
        return object;
    }

    public String toString() {
        return "session.getGlobal( " + identifier + " );";
    }
}
