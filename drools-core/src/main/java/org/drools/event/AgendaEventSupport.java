package org.drools.event;

/*
 * $Id$
 *
 * Copyright 2004 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.Tuple;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public class AgendaEventSupport
    implements
    Serializable {
    private final List          listeners = new ArrayList();
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

        ActivationCreatedEvent event = new ActivationCreatedEvent( activation);

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((AgendaEventListener) this.listeners.get( i )).activationCreated( event );
        }
    }

    public void fireActivationCancelled(Activation activation) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        ActivationCancelledEvent event = new ActivationCancelledEvent( activation);

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
