/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.persistence.timer;

import org.drools.core.command.CommandService;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.SelfRemovalJob;
import org.drools.core.time.SelfRemovalJobContext;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.CommandServiceTimerJobFactoryManager;
import org.drools.core.time.impl.TimerJobInstance;
import org.jbpm.process.instance.timer.TimerManager.ProcessJobContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalJPATimerJobFactoryManager implements CommandServiceTimerJobFactoryManager {

    private Map<Long, TimerJobInstance> emptyStore = new HashMap<Long,TimerJobInstance>();
    private CommandService commandService;
    private Map<Long, Map<Long, TimerJobInstance>> timerInstances;
    private Map<Long, TimerJobInstance> singleTimerInstances;
    
    public void setCommandService(CommandService commandService) {
        this.commandService = commandService;
    }
    
    public GlobalJPATimerJobFactoryManager() {
        timerInstances = new ConcurrentHashMap<Long, Map<Long, TimerJobInstance>>();
        singleTimerInstances = new ConcurrentHashMap<Long, TimerJobInstance>();
        
    }
    
    public TimerJobInstance createTimerJobInstance(Job job,
                                                   JobContext ctx,
                                                   Trigger trigger,
                                                   JobHandle handle,
                                                   InternalSchedulerService scheduler) {
    	long sessionId = -1;
    	if (ctx instanceof ProcessJobContext) {
            sessionId = ((ProcessJobContext) ctx).getSessionId();
            Map<Long, TimerJobInstance> instances = timerInstances.get(sessionId);
            if (instances == null) {
                instances = new ConcurrentHashMap<Long, TimerJobInstance>();
                timerInstances.put(sessionId, instances);
            }
        }        
        ctx.setJobHandle( handle );
        GlobalJpaTimerJobInstance jobInstance = new GlobalJpaTimerJobInstance( new SelfRemovalJob( job ),
                                                                   new SelfRemovalJobContext( ctx,
                                                                		   emptyStore ),
                                                                   trigger,
                                                                   handle,
                                                                   scheduler);
    
        return jobInstance;
    }
    
    public void addTimerJobInstance(TimerJobInstance instance) {
    
        JobContext ctx = instance.getJobContext();
        if (ctx instanceof SelfRemovalJobContext) {
            ctx = ((SelfRemovalJobContext) ctx).getJobContext();
        }
        Map<Long, TimerJobInstance> instances = null;
        if (ctx instanceof ProcessJobContext) {
            long sessionId = ((ProcessJobContext)ctx).getSessionId();
            instances = timerInstances.get(sessionId);
            if (instances == null) {
                instances = new ConcurrentHashMap<Long, TimerJobInstance>();
                timerInstances.put(sessionId, instances);
            }
        } else {
            instances = singleTimerInstances;
        }
        instances.put( instance.getJobHandle().getId(),
                                 instance );        
    }
    
    public void removeTimerJobInstance(TimerJobInstance instance) {
        JobContext ctx = instance.getJobContext();
        if (ctx instanceof SelfRemovalJobContext) {
            ctx = ((SelfRemovalJobContext) ctx).getJobContext();
        }
        Map<Long, TimerJobInstance> instances = null;
        if (ctx instanceof ProcessJobContext) {
            long sessionId = ((ProcessJobContext)ctx).getSessionId();
            instances = timerInstances.get(sessionId);
            if (instances == null) {
                instances = new ConcurrentHashMap<Long, TimerJobInstance>();
                timerInstances.put(sessionId, instances);
            }
        } else {
            instances = singleTimerInstances;
        }
        instances.remove( instance.getJobHandle().getId() );        
    }
    
    
    public Collection<TimerJobInstance> getTimerJobInstances() {
        return singleTimerInstances.values();
    }
    
    public Collection<TimerJobInstance> getTimerJobInstances(Long sessionId) {
        Map<Long, TimerJobInstance> sessionTimerJobs = timerInstances.get(sessionId);
        if (sessionTimerJobs == null) {
            return Collections.EMPTY_LIST;
        }
        return sessionTimerJobs.values();
    }
    
    public CommandService getCommandService() {
        return this.commandService;
    }
    
}
