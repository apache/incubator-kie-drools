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

package org.drools.core.time;

import java.util.Map;

import org.drools.core.time.impl.TimerJobInstance;

public class SelfRemovalJobContext implements JobContext {
    private JobContext jobContext;
    private Map<Long, TimerJobInstance> timerInstances;
    
    public SelfRemovalJobContext(JobContext jobContext,
                                 Map<Long, TimerJobInstance> timerInstances) {
        this.jobContext = jobContext;
        this.timerInstances = timerInstances;
    }

    public JobContext getJobContext() {
        return jobContext;
    }

    public void setJobHandle(JobHandle jobHandle) {
        jobContext.setJobHandle( jobHandle );
    }

    public JobHandle getJobHandle() {
        return jobContext.getJobHandle();
    }

    public void remove() {
        this.timerInstances.remove( jobContext.getJobHandle().getId() );
    }
  
    
}
