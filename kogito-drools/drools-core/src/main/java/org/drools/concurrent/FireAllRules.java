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
import org.drools.spi.AgendaFilter;

public class FireAllRules
    implements
    Command,
    Future {
    private AgendaFilter     agendaFilter;
    private volatile boolean done;
    private Exception     e;

    public FireAllRules() {
    }

    public FireAllRules(final AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        agendaFilter    = (AgendaFilter)in.readObject();
        done            = in.readBoolean();
        e               = (Exception)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(agendaFilter);
        out.writeBoolean(done);
        out.writeObject(e);
    }

    public void execute(final WorkingMemory workingMemory) {
        try {
            workingMemory.fireAllRules( this.agendaFilter );
        } catch ( Exception e ) {
            this.e = e;
        }
        this.done = true;
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