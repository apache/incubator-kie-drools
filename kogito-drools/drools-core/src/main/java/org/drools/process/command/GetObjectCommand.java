package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.runtime.rule.FactHandle;

public class GetObjectCommand
    implements
    Command<Object> {

    private FactHandle factHandle;

    public GetObjectCommand(FactHandle factHandle) {
        this.factHandle = factHandle;
    }

    public Object execute(StatefulSession session) {
        return session.getObject( factHandle );
    }

    public String toString() {
        return "session.getObject( " + factHandle + " );";
    }

}
