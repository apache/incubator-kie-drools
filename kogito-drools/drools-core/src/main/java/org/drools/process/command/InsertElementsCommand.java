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
    public Iterable objects;

    private String  outIdentifier;

    private boolean returnObject = true;

    public InsertElementsCommand() {
        this.objects = new ArrayList();
    }

    public InsertElementsCommand(Iterable objects) {
        this.objects = objects;
    }

    public Iterable getObjects() {
        return this.objects;
    }

    public void setObjects(Iterable objects) {
        this.objects = objects;
    }

    public Collection<FactHandle> execute(ReteooWorkingMemory session) {
        List<FactHandle> handles = new ArrayList<FactHandle>();
        for ( Object object : objects ) {
            handles.add( session.insert( object ) );
        }

        if ( outIdentifier != null ) {
            if ( this.returnObject ) {
                session.getExecutionResult().getResults().put( this.outIdentifier,
                                                               objects );
            }
            session.getExecutionResult().getFactHandles().put( this.outIdentifier,
                                                               handles );
        }
        return handles;
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
        List<Object> list = new ArrayList<Object>();
        for ( Object object : objects ) {
            list.add( object );
        }
        return "insert " + list;
    }

}
