package org.drools.process.command;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.FactHandle;

public class InsertObjectCommand
    implements
    Command<FactHandle> {

    private Object  object;

    private String  outIdentifier;

    private boolean returnObject = true;

    public InsertObjectCommand(Object object) {
        this.object = object;
    }

    public FactHandle execute(ReteooWorkingMemory session) {
        FactHandle factHandle = session.insert( object );

        if ( outIdentifier != null ) {
            if ( this.returnObject ) {
                session.getExecutionResult().getResults().put( this.outIdentifier,
                                                               object );
            }
            session.getExecutionResult().getFacts().put( this.outIdentifier,
                                                         factHandle );
        }

        return factHandle;
    }

    public Object getObject() {
        return this.object;
    }

    public String getOutIdentifier() {
        return this.outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public boolean isReturnObject() {
        return returnObject;
    }

    public void setReturnObject(boolean returnObject) {
        this.returnObject = returnObject;
    }

    public String toString() {
        return "session.insert(" + object + ");";
    }

}
