/**
 * Copyright 2005 JBoss Inc
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

package org.drools.event;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.PropagationContext;

public class ObjectInsertedEvent extends WorkingMemoryEvent {
    /**
     * 
     */
    private static final long serialVersionUID = 400L;

    private final FactHandle  handle;

    private final Object      object;

    public ObjectInsertedEvent(final WorkingMemory workingMemory,
                               final PropagationContext propagationContext,
                               final FactHandle handle,
                               final Object object) {
        super( workingMemory,
               propagationContext );
        this.handle = handle;
        this.object = object;
    }

    public FactHandle getFactHandle() {
        return this.handle;
    }

    public Object getObject() {
        return this.object;
    }

    public String toString() {
        return "[ObjectInserted: handle=" + this.handle + "; object=" + this.object + "]";
    }
}
