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

package org.drools.core;

import org.drools.core.time.impl.DefaultTimerJobFactoryManager;
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.core.time.impl.TrackableTimeJobFactoryManager;

public enum TimerJobFactoryType {

    DEFUALT("default") {
        public TimerJobFactoryManager createInstance() {
            return DefaultTimerJobFactoryManager.instance;
        }
    },

    TRACKABLE("trackable") {
        public TimerJobFactoryManager createInstance() {
            return new TrackableTimeJobFactoryManager();
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
        } else if( DEFUALT.getId().equalsIgnoreCase( id ) ) {
            return DEFUALT;
        } else if( JPA.getId().equalsIgnoreCase( id ) ) {
            return JPA;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + id + "' for TimerJobFactoryType" );
    }
}
