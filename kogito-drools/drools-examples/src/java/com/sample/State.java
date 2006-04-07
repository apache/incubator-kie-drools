package com.sample;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class State {
    public static final int       NOTRUN   = 0;
    public static final int       FINISHED = 1;

    private PropertyChangeSupport changes  = new PropertyChangeSupport( this );

    private String                name;
    private int                   state;

    public State(String name) {
        this.name = name;
        this.state = NOTRUN;
    }

    public String getName() {
        return this.name;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int newState) {
        int oldState = this.state;
        this.state = newState;
        this.changes.firePropertyChange("state", oldState, newState);
    }

    public boolean inState(String name,
                           int state) {
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

    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener( l );
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener( l );
    }
}