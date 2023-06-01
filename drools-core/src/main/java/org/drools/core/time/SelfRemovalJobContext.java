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

package org.drools.core.time;

import java.util.Map;

import org.drools.base.time.JobHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.time.impl.TimerJobInstance;

public class SelfRemovalJobContext implements JobContext {

    private static final long serialVersionUID = 614425985040796356L;

    protected final JobContext jobContext;
    protected final Map<Long, TimerJobInstance> timerInstances;

    public SelfRemovalJobContext( JobContext jobContext,
                                  Map<Long, TimerJobInstance> timerInstances ) {
        this.jobContext = jobContext;
        this.timerInstances = timerInstances;
    }

    public JobContext getJobContext() {
        return jobContext;
    }

    @Override
    public void setJobHandle(JobHandle jobHandle) {
        jobContext.setJobHandle( jobHandle );
    }

    @Override
    public JobHandle getJobHandle() {
        return jobContext.getJobHandle();
    }

    @Override
    public ReteEvaluator getReteEvaluator() {
        return jobContext.getReteEvaluator();
    }

    public void remove() {
        this.timerInstances.remove( jobContext.getJobHandle().getId() );
    }
}
