package org.drools.core;

import org.drools.core.time.impl.DefaultTimerJobFactoryManager;
import org.drools.core.time.impl.ThreadSafeTrackableTimeJobFactoryManager;
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.core.time.impl.TrackableTimeJobFactoryManager;

public enum TimerJobFactoryType {

    DEFAULT("default") {
        public TimerJobFactoryManager createInstance() {
            return DefaultTimerJobFactoryManager.instance;
        }
    },

    TRACKABLE("trackable") {
        public TimerJobFactoryManager createInstance() {
            return new TrackableTimeJobFactoryManager();
        }
    },

    THREAD_SAFE_TRACKABLE("thread_safe_trackable") {
        public TimerJobFactoryManager createInstance() {
            return new ThreadSafeTrackableTimeJobFactoryManager();
        }
    },

    JPA("jpa") {
        public TimerJobFactoryManager createInstance() {
            try {
                return (TimerJobFactoryManager)Class.forName("org.drools.persistence.jpa.JpaTimeJobFactoryManager").newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    public abstract TimerJobFactoryManager createInstance();

    private final String string;
    TimerJobFactoryType( String string ) {
        this.string = string;
    }

    public String toExternalForm() {
        return this.string;
    }

    public String toString() {
        return this.string;
    }

    public String getId() {
        return this.string;
    }

    public static TimerJobFactoryType resolveTimerJobFactoryType( String id ) {
        if( TRACKABLE.getId().equalsIgnoreCase( id ) ) {
            return TRACKABLE;
        } else if( DEFAULT.getId().equalsIgnoreCase( id ) ) {
            return DEFAULT;
        } else if( JPA.getId().equalsIgnoreCase( id ) ) {
            return JPA;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + id + "' for TimerJobFactoryType" );
    }
}
