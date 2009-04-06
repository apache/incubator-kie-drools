package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.FactHandle;

public class InsertObjectCommand implements Command<FactHandle> {
	
	private Object object;
	
    private String outIdentifier;
	
	public InsertObjectCommand(Object object) {
		this.object = object;
	}
	
	public FactHandle execute(ReteooWorkingMemory session) {
	    if ( outIdentifier != null ) {
	        session.getExecutionResult().getResults().put( this.outIdentifier, object );
	    }
		return session.insert(object);
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

    public String toString() {
		return "session.insert(" + object + ");";
	}

}
