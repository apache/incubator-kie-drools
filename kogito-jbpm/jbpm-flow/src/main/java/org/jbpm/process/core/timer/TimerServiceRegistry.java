/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.core.timer;

import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.impl.GlobalTimerService;

/**
 * Simple TimerService registry used for single point registration of <code>TimerService</code>
 * instances used by sessions.
 * Registry is intended to be used at the application startup to register all required TimerServices 
 * so it can start operate as soon as it is active even if the session are not yet active. 
 *
 */
public class TimerServiceRegistry {

    public static final String TIMER_SERVICE_SUFFIX = "-timerServiceId";
    private ConcurrentHashMap<String, TimerService> registeredServices = new ConcurrentHashMap<String, TimerService>();
    
    private static TimerServiceRegistry instance = new TimerServiceRegistry();
    
    public static TimerServiceRegistry getInstance() {

        return instance;
    }
    
    /**
     * Registers timerServie under given id. In case timer service is already registered  
     * with this id it will be overridden.
     * @param id key used to get hold of the timer service instance
     * @param timerService fully initialized TimerService instance
     */
    public void registerTimerService(String id, TimerService timerService) {   
        if (timerService instanceof GlobalTimerService) {
            ((GlobalTimerService) timerService).setTimerServiceId(id);
        }
        this.registeredServices.put(id, timerService);
    }
    
    /**
     * Returns TimerService instance registered under given key
     * @param id timer service identifier
     * @return returns timer service instance or null of there was none registered with given id
     */
    public TimerService get(String id) {
        if (id == null) {
            return null;
        }
        return this.registeredServices.get(id);
    }
    
    /**
     * Removes TimerService from the registry.
     * @param id timer service identifier
     * @return returns TimerService instance returned from the registry for cleanup tasks
     */
    public TimerService remove(String id) {
        return this.registeredServices.remove(id);
    }
}
