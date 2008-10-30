/**
 * 
 */
package org.drools.concurrent;

import org.drools.WorkingMemory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AssertObjects
    implements
    Command,
    Future {
    private Object        object;
    private volatile List results;
    private Exception     e;

    public AssertObjects() {
    }

    public AssertObjects(final Object object) {
        this.object = object;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        object  = in.readObject();
        results = (List)in.readObject();
        e       = (Exception)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(object);
        out.writeObject(results);
        out.writeObject(e);
    }

    public void execute(final WorkingMemory workingMemory) {
        try {
            if ( this.object instanceof Object[] ) {
                final Object[] objects = (Object[]) this.object;
                this.results = new ArrayList( objects.length );
                for ( int i = 0; i < objects.length; i++ ) {
                    this.results.add( workingMemory.insert( objects[i] ) );
                }
            } else if ( this.object instanceof Iterable<?> ) {
                final List list = (List) this.object;
                this.results = new ArrayList( list.size() );
                for ( final Iterator it = list.iterator(); it.hasNext(); ) {
                    this.results.add( workingMemory.insert( it.next() ) );
                }
            }
        } catch ( Exception e ) {
            this.e = e;
        }        
    }

    public Object getObject() {
        return this.results;
    }

    public boolean isDone() {
        return this.results != null;
    }
    
    public boolean exceptionThrown() {
        return e != null;
    }
    
    public Exception getException() {
        return this.e;
    }    
}