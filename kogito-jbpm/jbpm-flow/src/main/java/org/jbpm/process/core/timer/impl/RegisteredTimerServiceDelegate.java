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
package org.jbpm.process.core.timer.impl;

import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.core.time.impl.TimerJobInstance;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.kie.api.time.SessionClock;

import java.util.Collection;

/**
 * Simple delegate for timer service that fetches the real instance of timer service from
 * TimerServiceRegistry under "default" key.
 * That requires TimerService to be registered prior to using this delegate, which usually 
 * means before any session is created.
 * 
 * This delegate should be configured in session configuration so when initializing it will use
 * right TimerService implementation:
 * <code>
 *      Properties conf = new Properties();
 *      conf.setProperty("drools.timerService", "org.jbpm.process.core.timer.impl.RegisteredTimerServiceDelegate");        
 *      KieSessionConfiguration sessionConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(conf);
 * </code>
 *
 */
public class RegisteredTimerServiceDelegate implements TimerService, InternalSchedulerService, SessionClock {
    
    private TimerService timerService;
    
    public RegisteredTimerServiceDelegate() {
        this("default");
    }
    
    public RegisteredTimerServiceDelegate(String timerServiceKey) {        
        timerService = TimerServiceRegistry.getInstance().get(timerServiceKey);
        if (timerService == null) {
            throw new IllegalStateException("TimerService with key " + timerServiceKey + " was not found in the registry");
        }
    }

    @Override
    public JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger) {
        return timerService.scheduleJob(job, ctx, trigger);
    }

    @Override
    public boolean removeJob(JobHandle jobHandle) {
        
        return timerService.removeJob(jobHandle);
    }

    @Override
    public void setTimerJobFactoryManager(
            TimerJobFactoryManager timerJobFactoryManager) {
        timerService.setTimerJobFactoryManager(timerJobFactoryManager);
    }

    @Override
    public TimerJobFactoryManager getTimerJobFactoryManager() {
        return timerService.getTimerJobFactoryManager();
    }

    @Override
    public void internalSchedule(TimerJobInstance timerJobInstance) {
        ((InternalSchedulerService)timerService).internalSchedule(timerJobInstance);
    }

    @Override
    public long getCurrentTime() {        
        return timerService.getCurrentTime();
    }

    @Override
    public void shutdown() {
        timerService.shutdown();
    }

    @Override
    public long getTimeToNextJob() {        
        return timerService.getTimeToNextJob();
    }

    @Override
    public Collection<TimerJobInstance> getTimerJobInstances(long id) {        
        return timerService.getTimerJobInstances(id);
    }

}
