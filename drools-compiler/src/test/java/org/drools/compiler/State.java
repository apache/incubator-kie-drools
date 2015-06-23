/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class State implements Serializable {
    private final PropertyChangeSupport changes = new PropertyChangeSupport( this );

    private String                      state;
    private boolean                     flag;

    public State() {

    }

    public State(final String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setState(final String newState) {
        final String oldState = this.state;
        this.state = newState;
        this.changes.firePropertyChange( "state",
                                         oldState,
                                         newState );
    }

    public void addPropertyChangeListener(final PropertyChangeListener l) {
        this.changes.addPropertyChangeListener( l );
    }

    public void removePropertyChangeListener(final PropertyChangeListener l) {
        this.changes.removePropertyChangeListener( l );
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return this.changes.getPropertyChangeListeners();
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setFlag(final boolean flag) {
        final boolean old = this.flag;
        this.flag = flag;
        this.changes.firePropertyChange( "flag",
                                         old,
                                         flag );
    }
}
