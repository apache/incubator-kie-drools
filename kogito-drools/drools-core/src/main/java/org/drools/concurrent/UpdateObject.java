/*
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

import org.drools.FactHandle;
import org.drools.WorkingMemory;

public class UpdateObject
    implements
    Command,
    Future {
    private FactHandle       factHandle;
    private Object           object;
    private volatile boolean done;
    private Exception     e;

    public UpdateObject() {
    }

    public UpdateObject(final FactHandle factHandle,
                        final Object object) {
        this.factHandle = factHandle;
        this.object = object;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factHandle  = (FactHandle)in.readObject();
        object      = in.readObject();
        done        = in.readBoolean();
        e           = (Exception)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factHandle);
        out.writeObject(object);
        out.writeBoolean(done);
        out.writeObject(e);
    }

    public void execute(final WorkingMemory workingMemory) {
        workingMemory.update( this.factHandle,
                              this.object );
        this.done = true;
    }

    public Object getObject() {
        return null;
    }

    public boolean isDone() {
        return this.done == true;
    }

    public boolean exceptionThrown() {
        return e != null;
    }

    public Exception getException() {
        return this.e;
    }
}
