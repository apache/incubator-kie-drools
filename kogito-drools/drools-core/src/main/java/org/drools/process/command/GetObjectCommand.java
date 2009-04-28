package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.FactHandle;

public class GetObjectCommand
    implements
    Command<Object> {

    private FactHandle factHandle;
    private String     outIdentifier;

    public GetObjectCommand(FactHandle factHandle) {
        this.factHandle = factHandle;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Object execute(ReteooWorkingMemory session) {
        Object object = session.getObject( factHandle );
        session.getExecutionResult().getResults().put( this.outIdentifier,
                                                       object );

        return object;
    }
    
    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    public String toString() {
        return "session.getObject( " + factHandle + " );";
    }

}
