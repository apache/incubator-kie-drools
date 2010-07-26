/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */
package org.drools.concurrent;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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