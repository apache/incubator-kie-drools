package org.drools.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class GetObjectCommand
    implements
    GenericCommand<Object> {

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

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        Object object = ksession.getObject( factHandle );
        
        ((StatefulKnowledgeSessionImpl)ksession).session.getExecutionResult().getResults().put( this.outIdentifier,
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
