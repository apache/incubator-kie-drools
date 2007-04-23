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

    public AssertObjects(final Object object) {
        this.object = object;
    }

    public void execute(final WorkingMemory workingMemory) {
        if ( this.object instanceof Object[] ) {
            workingMemory.assertObject( this.object );
        } else if ( this.object instanceof List ) {
            final List list = (List) this.object;
            this.results = new ArrayList( list.size() );
            for ( final Iterator it = list.iterator(); it.hasNext(); ) {
                this.results.add( workingMemory.assertObject( it.next() ) );
            }
        } else {
            final Object[] objects = (Object[]) this.object;
            this.results = new ArrayList( objects.length );
            for ( int i = 0, length = objects.length; i < length; i++ ) {
                this.results.add( workingMemory.assertObject( objects[i] ) );
            }
        }
    }

    public Object getObject() {
        return this.results;
    }

    public boolean isDone() {
        return this.results != null;
    }
}