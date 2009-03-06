package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.FactHandle;

public class UpdateCommand
    implements
    Command<Object> {

    private FactHandle handle;
    private Object     object;

    public UpdateCommand(FactHandle handle,
                         Object object) {
        this.handle = handle;
        this.object = object;
    }

    public Object execute(ReteooWorkingMemory session) {
        session.update( handle,
                        object );
        return null;
    }

    public String toString() {
        return "session.update( " + handle + ", " + object + " );";
    }
}
