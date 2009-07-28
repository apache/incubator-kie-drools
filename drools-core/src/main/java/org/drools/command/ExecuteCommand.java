package org.drools.command;

import org.drools.command.Command;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.InternalFactHandle;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class ExecuteCommand
    implements
    GenericCommand<ExecutionResults> {

    private Command  command;

    public ExecuteCommand(Command  command) {
        this.command = command;
    }

    public ExecutionResults execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
      
        return ksession.execute( this.command );

    }

    public Command getCommand() {
        return this.command;
    }

//    public String getOutIdentifier() {
//        return this.outIdentifier;
//    }
//
//    public void setOutIdentifier(String outIdentifier) {
//        this.outIdentifier = outIdentifier;
//    }

//    public boolean isReturnObject() {
//        return returnObject;
//    }
//
//    public void setReturnObject(boolean returnObject) {
//        this.returnObject = returnObject;
//    }

    public String toString() {
        return "session.execute(" + this.command + ");";
    }

}
