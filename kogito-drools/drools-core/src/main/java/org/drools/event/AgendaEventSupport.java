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
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public class AgendaEventSupport
    implements
    Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 400L;
    private final List        listeners        = Collections.synchronizedList( new ArrayList() );

    public AgendaEventSupport() {
    }

    public void addEventListener(final AgendaEventListener listener) {
        if ( !this.listeners.contains( listener ) ) {
            this.listeners.add( listener );
        }
    }

    public void removeEventListener(final AgendaEventListener listener) {
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

    public void fireActivationCreated(final Activation activation,
                                      final WorkingMemory workingMemory) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final ActivationCreatedEvent event = new ActivationCreatedEvent( activation );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).activationCreated( event,
                                                                               workingMemory );
        }
    }

    public void fireActivationCancelled(final Activation activation,
                                        final WorkingMemory workingMemory) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final ActivationCancelledEvent event = new ActivationCancelledEvent( activation );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).activationCancelled( event,
                                                                                 workingMemory );
        }
    }

    public void fireBeforeActivationFired(final Activation activation,
                                          final WorkingMemory workingMemory) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforeActivationFiredEvent event = new BeforeActivationFiredEvent( activation );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).beforeActivationFired( event,
                                                                                   workingMemory );
        }
    }

    public void fireAfterActivationFired(final Activation activation,
                                         final InternalWorkingMemory workingMemory) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterActivationFiredEvent event = new AfterActivationFiredEvent( activation );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).afterActivationFired( event,
                                                                                  workingMemory );
        }
    }

    public void fireAgendaGroupPopped(final AgendaGroup agendaGroup,
                                      final InternalWorkingMemory workingMemory) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AgendaGroupPoppedEvent event = new AgendaGroupPoppedEvent( agendaGroup );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).agendaGroupPopped( event,
                                                                               workingMemory );
        }
    }

    public void fireAgendaGroupPushed(final AgendaGroup agendaGroup,
                                      final InternalWorkingMemory workingMemory) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AgendaGroupPushedEvent event = new AgendaGroupPushedEvent( agendaGroup );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).agendaGroupPushed( event,
                                                                               workingMemory );
        }
    }

    public void reset() {
        this.listeners.clear();
    }
}