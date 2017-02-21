/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import org.drools.core.time.impl.DefaultTimerJobFactoryManager;
import org.drools.core.time.impl.ThreadSafeTrackableTimeJobFactoryManager;
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.core.time.impl.TrackableTimeJobFactoryManager;

public abstract class TimerJobFactoryType {

    public static final TimerJobFactoryType DEFAULT = new TimerJobFactoryType("default") {
        public TimerJobFactoryManager createInstance() {
            return DefaultTimerJobFactoryManager.instance;
        }
    };

    public static final TimerJobFactoryType TRACKABLE = new TimerJobFactoryType("trackable") {
        public TimerJobFactoryManager createInstance() {
            return new TrackableTimeJobFactoryManager();
        }
    };

    public static final TimerJobFactoryType THREAD_SAFE_TRACKABLE = new TimerJobFactoryType("thread_safe_trackable") {
        public TimerJobFactoryManager createInstance() {
            return new ThreadSafeTrackableTimeJobFactoryManager();
        }
    };

    public static final TimerJobFactoryType JPA = new TimerJobFactoryType("jpa") {
        public TimerJobFactoryManager createInstance() {
            try {
                return (TimerJobFactoryManager)Class.forName("org.drools.persistence.jpa.JpaTimeJobFactoryManager").newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    private static final Map<String, TimerJobFactoryType> registry = new HashMap<>();
    
    static {
    	register(DEFAULT);
    	register(TRACKABLE);
    	register(THREAD_SAFE_TRACKABLE);
    	register(JPA);
    }

    public static void register(TimerJobFactoryType type) {
    	if (type != null && type.getId() != null) {
    		registry.put(type.getId(), type);
    	}
    }
    
    public abstract TimerJobFactoryManager createInstance();

    private final String string;
    
    public TimerJobFactoryType( String string ) {
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
        TimerJobFactoryType type = registry.get(id);
        if (type != null) {
        	return type;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + id + "' for TimerJobFactoryType" );
    }
}
