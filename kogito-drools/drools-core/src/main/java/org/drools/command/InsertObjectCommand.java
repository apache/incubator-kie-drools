package org.drools.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.InternalFactHandle;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class InsertObjectCommand
    implements
    GenericCommand<FactHandle> {

    private Object  object;

    private String  outIdentifier;

    private boolean returnObject = true;

    public InsertObjectCommand(Object object) {
        this.object = object;
    }

    public FactHandle execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        FactHandle factHandle = ksession.insert( object );
        
        ReteooWorkingMemory session = ((StatefulKnowledgeSessionImpl)ksession).session;

        if ( outIdentifier != null ) {
            if ( this.returnObject ) {
                session.getExecutionResult().getResults().put( this.outIdentifier,
                                                               object );
            }
            session.getExecutionResult().getFactHandles().put( this.outIdentifier,
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
