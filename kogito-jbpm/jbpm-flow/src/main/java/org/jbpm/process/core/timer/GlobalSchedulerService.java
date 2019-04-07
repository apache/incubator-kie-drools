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

import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.JobHandle;
import org.drools.core.time.SchedulerService;
import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.impl.GlobalTimerService.GlobalJobHandle;

/**
 * Implementations of these interface are responsible for scheduled jobs in global manner,
 * meaning not knowledge session scoped but global accessible for all the sessions that will
 * be configured to use this <code>GlobalSchedulerService</code>
 *
 */
public interface GlobalSchedulerService extends SchedulerService, InternalSchedulerService {

    /**
     * Provides handle to inject timerService that owns this scheduler service and initialize it
     * @param timerService owner of this scheduler service
     */
    void initScheduler(TimerService timerService);
    
    /**
     * Allows to shutdown the scheduler service
     */
    void shutdown();
    
    /**
     * Builds JobHandle based on NamedJobContext
     * @param ctx
     * @return
     */
    JobHandle buildJobHandleForContext(NamedJobContext ctx);
    
    /**
     * Indicates if given implementation of scheduler service supports 
     * transactional timer store.
     * @return
     */
    boolean isTransactional();
    
    /**
     * Indicates if given implementation of scheduler service supports
     * retries of failed timer jobs
     * @return
     */
    boolean retryEnabled();
    
    /**
     * Allows to set interceptor that will be used to schedule timer instances
     * @param interceptor
     */
    void setInterceptor(SchedulerServiceInterceptor interceptor);
    
    /**
     * Verifies if given job is still valid or it was already executed/vetoed.
     * Especially important in multi node environments - cluster. 
     * @param jobHandle
     * @return
     */
    boolean isValid(GlobalJobHandle jobHandle);
}
