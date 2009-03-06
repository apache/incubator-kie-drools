package org.drools.process.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.FactHandle;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class InsertElementsCommand
    implements
    Command<Collection<FactHandle>> {
    public Collection objects;

    public InsertElementsCommand() {
        this.objects = new ArrayList();
    }

    public InsertElementsCommand(Collection objects) {
        this.objects = objects;
    }
    
    public Collection getObjects() {
        return this.objects;
    }

    public void setObjects(Collection objects) {
        this.objects = objects;
    }

    public Collection<FactHandle> execute(ReteooWorkingMemory session) {
        List<FactHandle> handles = new ArrayList<FactHandle>( objects.size() );
        for ( Object object : objects ) {
            handles.add( session.insert( object ) );
        }
        return handles;
    }

    public String toString() {
        return "insert " + objects;
    }

}
