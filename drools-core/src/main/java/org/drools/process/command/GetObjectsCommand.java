package org.drools.process.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl.ObjectStoreWrapper;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;

public class GetObjectsCommand
    implements
    GenericCommand<Collection> {

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

    public Collection execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        Collection col = null;
        
        if ( filter != null ) {
            
            col =  ksession.getObjects( this.filter );
        } else {
            col =  ksession.getObjects( );
        }
        
        if ( this.outIdentifier != null ) {
            List objects = new ArrayList( col );
            
            ((StatefulKnowledgeSessionImpl)ksession).session.getExecutionResult().getResults().put( this.outIdentifier, objects );
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
