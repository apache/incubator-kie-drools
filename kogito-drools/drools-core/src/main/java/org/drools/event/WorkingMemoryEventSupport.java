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
    /**
     * 
     */
    private static final long   serialVersionUID = -7572714148615479288L;
    private final List          listeners        = Collections.synchronizedList( new ArrayList() );
    private final WorkingMemory workingMemory;

    public WorkingMemoryEventSupport(final WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public void addEventListener(final WorkingMemoryEventListener listener) {
        if ( !this.listeners.contains( listener ) ) {
            this.listeners.add( listener );
        }
    }

    public void removeEventListener(final WorkingMemoryEventListener listener) {
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

    public void fireObjectInserted(final PropagationContext propagationContext,
                                   final FactHandle handle,
                                   final Object object) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final ObjectInsertedEvent event = new ObjectInsertedEvent( this.workingMemory,
                                                                   propagationContext,
                                                                   handle,
                                                                   object );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((WorkingMemoryEventListener) this.listeners.get( i )).objectInserted( event );
        }
    }

    public void fireObjectUpdated(final PropagationContext propagationContext,
                                   final FactHandle handle,
                                   final Object oldObject,
                                   final Object object) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final ObjectUpdatedEvent event = new ObjectUpdatedEvent( this.workingMemory,
                                                                   propagationContext,
                                                                   handle,
                                                                   oldObject,
                                                                   object );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((WorkingMemoryEventListener) this.listeners.get( i )).objectUpdated( event );
        }
    }

    public void fireObjectRetracted(final PropagationContext propagationContext,
                                    final FactHandle handle,
                                    final Object oldObject) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final ObjectRetractedEvent event = new ObjectRetractedEvent( this.workingMemory,
                                                                     propagationContext,
                                                                     handle,
                                                                     oldObject );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((WorkingMemoryEventListener) this.listeners.get( i )).objectRetracted( event );
        }
    }

}