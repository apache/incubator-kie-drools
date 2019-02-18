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

package org.drools.core.time.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.time.EnqueuedSelfRemovalJobContext;
import org.drools.core.time.SelfRemovalJob;
import org.kie.services.time.InternalSchedulerService;
import org.kie.services.time.Job;
import org.kie.services.time.JobContext;
import org.kie.services.time.JobHandle;
import org.kie.services.time.Trigger;
import org.kie.services.time.impl.DefaultTimerJobInstance;
import org.kie.services.time.impl.TimerJobFactoryManager;
import org.kie.services.time.impl.TimerJobInstance;

public class TrackableTimeJobFactoryManager
    implements
    TimerJobFactoryManager {

    protected final Map<Long, TimerJobInstance> timerInstances;

    public TrackableTimeJobFactoryManager() {
        this(new HashMap<Long, TimerJobInstance>());
    }

    protected TrackableTimeJobFactoryManager(Map<Long, TimerJobInstance> timerInstances) {
        this.timerInstances = timerInstances;
    }

    public TimerJobInstance createTimerJobInstance(Job job,
                                                   JobContext ctx,
                                                   Trigger trigger,
                                                   JobHandle handle,
                                                   InternalSchedulerService scheduler) {
        ctx.setJobHandle( handle );

        return new DefaultTimerJobInstance(new SelfRemovalJob(job ),
                                           createJobContext( ctx ),
                                           trigger,
                                           handle,
                                           scheduler );
    }

    protected JobContext createJobContext( JobContext ctx ) {
        return new EnqueuedSelfRemovalJobContext( ctx, timerInstances );
    }

    public void addTimerJobInstance(TimerJobInstance instance) {

        this.timerInstances.put( instance.getJobHandle().getId(),
                                 instance );
    }

    public void removeTimerJobInstance(TimerJobInstance instance) {

        this.timerInstances.remove( instance.getJobHandle().getId() );
    }

    public Collection<TimerJobInstance> getTimerJobInstances() {
        return timerInstances.values();
    }
}
