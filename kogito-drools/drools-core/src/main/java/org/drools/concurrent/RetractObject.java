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

package org.drools.concurrent;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.WorkingMemory;
import org.drools.FactHandle;

public class RetractObject
    implements
    Command,
    Future {
    private FactHandle       factHandle;
    private volatile boolean done;
    private Exception     e;

    public RetractObject() {
    }

    public RetractObject(final FactHandle factHandle) {
        this.factHandle = factHandle;
    }

    public void execute(final WorkingMemory workingMemory) {
        workingMemory.retract( this.factHandle );
        this.done = true;

    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factHandle  = (FactHandle)in.readObject();
        done        = in.readBoolean();
        e           = (Exception)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factHandle);
        out.writeBoolean(done);
        out.writeObject(e);
    }

    public Object getObject() {
        return null;
    }

    public boolean isDone() {
        return this.done;
    }

    public boolean exceptionThrown() {
        return e != null;
    }

    public Exception getException() {
        return this.e;
    }
}
