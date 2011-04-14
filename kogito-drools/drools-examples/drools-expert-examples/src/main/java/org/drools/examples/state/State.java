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

package org.drools.examples.state;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class State {
    public static final int       NOTRUN   = 0;
    public static final int       FINISHED = 1;

    private final PropertyChangeSupport changes  = new PropertyChangeSupport( this );

    private String                name;
    private int                   state;

    public State() {
        
    }
    
    public State(final String name) {
        this.name = name;
        this.state = State.NOTRUN;
    }

    public String getName() {
        return this.name;
    }

    public int getState() {
        return this.state;
    }

    public void setState(final int newState) {
        final int oldState = this.state;
        this.state = newState;
        this.changes.firePropertyChange( "state",
                                         oldState,
                                         newState );
    }

    public boolean inState(final String name,
                           final int state) {
        return this.name.equals( name ) && this.state == state;
    }

    public String toString() {
        switch ( this.state ) {
            case NOTRUN :
                return this.name + "[" + "NOTRUN" + "]";
            case FINISHED :
            default :
                return this.name + "[" + "FINISHED" + "]";
        }
    }

    public void addPropertyChangeListener(final PropertyChangeListener l) {
        this.changes.addPropertyChangeListener( l );
    }

    public void removePropertyChangeListener(final PropertyChangeListener l) {
        this.changes.removePropertyChangeListener( l );
    }
}
