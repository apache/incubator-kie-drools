package org.drools.command;

import org.drools.KnowledgeBase;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;

public class SetGlobalCommand
    implements
    GenericCommand<Void> {

    private String  identifier;
    private Object  object;

    private String  outIdentifier;

    private boolean out;

    public SetGlobalCommand(String identifier,
                            Object object) {
        this.identifier = identifier;
        this.object = object;
    }

    public Void execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();

        if ( this.out ) {
            ((StatefulKnowledgeSessionImpl) ksession).session.getExecutionResult().getResults().put( (this.outIdentifier != null) ? this.outIdentifier : this.identifier,
                                                                                                     object );
        }

        ksession.setGlobal( this.identifier,
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
