package org.drools.event;
/*
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





import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.PropagationContext;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public class WorkingMemoryEventSupport
    implements
    Serializable {
    private final List          listeners = new ArrayList();
    private final WorkingMemory workingMemory;

    public WorkingMemoryEventSupport(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        if ( !this.listeners.contains( listener ) ) {
            this.listeners.add( listener );
        }
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        this.listeners.remove( listener );
    }

    public List getEventListeners() {
        return Collections.unmodifiableList( this.listeners );
    }

    public int size() {
        return this.listeners.size();
    }

    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    public void fireObjectAsserted(PropagationContext propagationContext,
                                   FactHandle handle,
                                   Object object) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        ObjectAssertedEvent event = new ObjectAssertedEvent( this.workingMemory,
                                                             propagationContext,
                                                             handle,
                                                             object );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((WorkingMemoryEventListener) this.listeners.get( i )).objectAsserted( event );
        }
    }

    public void fireObjectModified(PropagationContext propagationContext,
                                   FactHandle handle,
                                   Object oldObject,
                                   Object object) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        ObjectModifiedEvent event = new ObjectModifiedEvent( this.workingMemory,
                                                             propagationContext,
                                                             handle,
                                                             oldObject,
                                                             object );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((WorkingMemoryEventListener) this.listeners.get( i )).objectModified( event );
        }
    }

    public void fireObjectRetracted(PropagationContext propagationContext,
                                    FactHandle handle,
                                    Object oldObject) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        ObjectRetractedEvent event = new ObjectRetractedEvent( this.workingMemory,
                                                               propagationContext,
                                                               handle,
                                                               oldObject );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((WorkingMemoryEventListener) this.listeners.get( i )).objectRetracted( event );
        }
    }

}