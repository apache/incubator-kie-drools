/**
 * 
 */
package org.drools.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;

public class AssertObjects
    implements
    Command,
    Future {
    private Object        object;
    private volatile List results;
    private Exception     e;

    public AssertObjects(final Object object) {
        this.object = object;
    }

    public void execute(final WorkingMemory workingMemory) {
        try {
            if ( this.object instanceof Object[] ) {
                final Object[] objects = (Object[]) this.object;
                this.results = new ArrayList( objects.length );
                for ( int i = 0; i < objects.length; i++ ) {
                    this.results.add( workingMemory.insert( objects[i] ) );
                }
            } else if ( this.object instanceof List ) {
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