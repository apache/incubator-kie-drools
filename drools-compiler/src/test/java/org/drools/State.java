package org.drools;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class State {
    private PropertyChangeSupport changes = new PropertyChangeSupport( this );

    private String                state;
    private boolean               flag;
    
    public State(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String newState) {
        String oldState = this.state;
        this.state = newState;
        this.changes.firePropertyChange( "state",
                                         oldState,
                                         newState );
    }
    
    

    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener( l );
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener( l );
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        boolean old = this.flag;
        this.flag = flag;
        this.changes.firePropertyChange( "flag", old, flag );
    }
}
