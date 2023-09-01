package org.drools.core;

import org.drools.core.time.TimerService;
import org.drools.core.time.impl.JDKTimerService;
import org.drools.core.time.impl.PseudoClockScheduler;

/**
 * This enum represents all engine supported clocks
 */
public enum ClockType {

    REALTIME_CLOCK("realtime") {
        public JDKTimerService createInstance() {
            return new JDKTimerService();
        }
    },

    /**
     * A Pseudo clock is a clock that is completely controlled by the
     * client application. It is usually used during simulations or tests
     */
    PSEUDO_CLOCK("pseudo") {
        public PseudoClockScheduler createInstance() {
            return new PseudoClockScheduler();
        }
    };

    public abstract TimerService createInstance();
    
    private String string;
    ClockType( String string ) {
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
    
    public static ClockType resolveClockType( String id ) {
        if( PSEUDO_CLOCK.getId().equalsIgnoreCase( id ) ) {
            return PSEUDO_CLOCK;
        } else if( REALTIME_CLOCK.getId().equalsIgnoreCase( id ) ) {
            return REALTIME_CLOCK;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + id + "' for ClockType" );
    }

}
