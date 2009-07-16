package org.drools.command.runtime;

import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class RegisterExitPointCommand
    implements
    GenericCommand<Object> {

    private String    name;
    private ExitPoint exitPoint;

    public RegisterExitPointCommand(String name,
                                    ExitPoint exitPoint) {
        this.name = name;
        this.exitPoint = exitPoint;
    }

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();

        ksession.registerExitPoint( name,
                                    exitPoint );

        return null;
    }

    public String toString() {
        return "reteooStatefulSession.registerExitPoint( " + name + ", " + exitPoint + " );";
    }
}
