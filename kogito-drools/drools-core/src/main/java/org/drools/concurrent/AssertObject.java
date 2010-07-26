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

import org.drools.WorkingMemory;

public class AssertObject
    implements
    Command,
    Future {
    private Object          object;
    private volatile Object result;
    private Exception       e;

    public AssertObject() {
    }

    public AssertObject(final Object object) {
        this.object = object;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        object  = in.readObject();
        result  = in.readObject();
        e       = (Exception)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(object);
        out.writeObject(result);
        out.writeObject(e);
    }

    public void execute(final WorkingMemory workingMemory) {
        try {
            this.result = workingMemory.insert( this.object );
        } catch ( Exception e ) {
            this.e = e;
        }
    }

    public Object getObject() {
        return this.result;
    }

    public boolean isDone() {
        return this.result != null;
    }

    public boolean exceptionThrown() {
        return e != null;
    }

    public Exception getException() {
        return this.e;
    }
}