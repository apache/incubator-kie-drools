/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core;

import org.drools.core.time.impl.JDKTimerService;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.kie.api.time.SessionClock;

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

    public abstract SessionClock createInstance();
    
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
