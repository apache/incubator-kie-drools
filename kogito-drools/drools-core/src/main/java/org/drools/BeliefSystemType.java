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

package org.drools;

import org.drools.beliefsystem.BeliefSystem;
import org.drools.beliefsystem.jtms.JTMSBeliefSet;
import org.drools.beliefsystem.jtms.JTMSBeliefSystem;
import org.drools.beliefsystem.simple.SimpleBeliefSystem;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NamedEntryPoint;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.time.SessionClock;
import org.drools.time.impl.JDKTimerService;
import org.drools.time.impl.PseudoClockScheduler;

/**
 * This enum represents all engine supported clocks
 */
public enum BeliefSystemType {

    SIMPLE("simple") {
        public BeliefSystem createInstance(NamedEntryPoint ep,
                                           TruthMaintenanceSystem tms) {
            return new SimpleBeliefSystem(ep, tms);
        }
    },

    /**
     * A Pseudo clock is a clock that is completely controlled by the
     * client application. It is usually used during simulations or tests
     */
    JTMS("jtms") {
        public BeliefSystem createInstance(NamedEntryPoint ep,
                                           TruthMaintenanceSystem tms) {
            return new JTMSBeliefSystem( ep, tms );
        }
    };

    public abstract BeliefSystem createInstance(NamedEntryPoint ep,
                                                TruthMaintenanceSystem tms);
    
    private String string;
    BeliefSystemType( String string ) {
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
    
    public static BeliefSystemType resolveBeliefSystemType(String id) {
        if( SIMPLE.getId().equalsIgnoreCase( id ) ) {
            return SIMPLE;
        } else if( JTMS.getId().equalsIgnoreCase( id ) ) {
            return JTMS;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + id + "' for BeliefSystem" );
    }

}
