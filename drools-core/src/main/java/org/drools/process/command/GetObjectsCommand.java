package org.drools.process.command;

import java.util.Iterator;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.ObjectFilter;

public class GetObjectsCommand
    implements
    Command<Iterator< ? >> {

    private ObjectFilter filter = null;

    public GetObjectsCommand() {
    }

    public GetObjectsCommand(ObjectFilter filter) {
        this.filter = filter;
    }

    public Iterator< ? > execute(ReteooWorkingMemory session) {
        if ( filter != null ) {
            return session.iterateObjects( filter );
        } else {
            return session.iterateObjects();
        }
    }

    public String toString() {
        if ( filter != null ) {
            return "session.iterateObjects( " + filter + " );";
        } else {
            return "session.iterateObjects();";
        }
    }

}
