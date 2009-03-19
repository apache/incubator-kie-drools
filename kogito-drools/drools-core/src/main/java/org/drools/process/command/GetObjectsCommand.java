package org.drools.process.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.impl.StatefulKnowledgeSessionImpl.ObjectStoreWrapper;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.ObjectFilter;

public class GetObjectsCommand
    implements
    Command<Collection> {

    public String getOutIdentifier() {
		return outIdentifier;
	}

	public void setOutIdentifier(String outIdentifier) {
		this.outIdentifier = outIdentifier;
	}

	private ObjectFilter filter = null;
    
    private String outIdentifier;

    public GetObjectsCommand() {
    }

    public GetObjectsCommand(ObjectFilter filter) {
        this.filter = filter;
    }

    public Collection execute(ReteooWorkingMemory session) {        
        Collection col = null;
        
        if ( filter != null ) {
            col =  getObjects( session, filter );
        } else {
            col =  getObjects(session);
        }
        
        if ( this.outIdentifier != null ) {
            List objects = new ArrayList( col );
            
            session.getBatchExecutionResult().getResults().put( this.outIdentifier, objects );
        }
        
        return col;
    }
    
    public Collection< ? extends Object > getObjects(ReteooWorkingMemory session) {
        return new ObjectStoreWrapper( session.getObjectStore(),
                                       null,
                                       ObjectStoreWrapper.OBJECT );
    }

    public Collection< ? extends Object > getObjects(ReteooWorkingMemory session, org.drools.runtime.ObjectFilter filter) {
        return new ObjectStoreWrapper( session.getObjectStore(),
                                       filter,
                                       ObjectStoreWrapper.OBJECT );
    }    

    public String toString() {
        if ( filter != null ) {
            return "session.iterateObjects( " + filter + " );";
        } else {
            return "session.iterateObjects();";
        }
    }

}
