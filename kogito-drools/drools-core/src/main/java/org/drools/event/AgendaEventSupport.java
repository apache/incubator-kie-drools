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

import org.drools.WorkingMemory;
import org.drools.spi.Activation;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public class AgendaEventSupport
    implements
    Serializable {
    private final List          listeners = Collections.synchronizedList( new ArrayList() );
    private final WorkingMemory workingMemory;

    public AgendaEventSupport(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public void addEventListener(AgendaEventListener listener) {
        if ( !this.listeners.contains( listener ) ) {
            this.listeners.add( listener );
        }
    }

    public void removeEventListener(AgendaEventListener listener) {
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

    public void fireActivationCreated(Activation activation) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        ActivationCreatedEvent event = new ActivationCreatedEvent( activation );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).activationCreated( event );
        }
    }

    public void fireActivationCancelled(Activation activation) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        ActivationCancelledEvent event = new ActivationCancelledEvent( activation );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).activationCancelled( event );
        }
    }

    public void fireBeforeActivationFired(Activation activation) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        BeforeActivationFiredEvent event = new BeforeActivationFiredEvent( activation );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).beforeActivationFired( event );
        }
    }

    public void fireAfterActivationFired(Activation activation) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        AfterActivationFiredEvent event = new AfterActivationFiredEvent( activation );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).afterActivationFired( event );
        }
    }
}