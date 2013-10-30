/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.runtime.manager.impl.tx;

import org.drools.core.time.impl.TimerJobInstance;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionSynchronization;
import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.impl.DelegateSchedulerServiceInterceptor;
import org.kie.api.runtime.manager.RuntimeEnvironment;

/**
 * Transaction aware scheduler service interceptor that will delay actual scheduling of the
 * timer job instance to the afterCompletion phase of JTA transaction. Scheduling will only
 * take place when transaction was successfully committed. That will make the timers
 * transactional to avoid any issues with having timer registered even though transaction was rolled
 * back. <br/>
 * NOTE:This interceptor should not be used for <code>GlobalSchedulerService</code> that are by nature
 * transactional e.g. Quartz with Data Base job store.
 *
 */
public class TransactionAwareSchedulerServiceInterceptor extends DelegateSchedulerServiceInterceptor {

    public TransactionAwareSchedulerServiceInterceptor(RuntimeEnvironment environment, GlobalSchedulerService schedulerService) {
        super(schedulerService);
    }

    @Override
    public final void internalSchedule(final TimerJobInstance timerJobInstance) {
        JtaTransactionManager tm = new ExtendedJTATransactionManager(null, null, null);
        if (tm.getStatus() != JtaTransactionManager.STATUS_NO_TRANSACTION
                && tm.getStatus() != JtaTransactionManager.STATUS_ROLLEDBACK
                && tm.getStatus() != JtaTransactionManager.STATUS_COMMITTED) {
            tm.registerTransactionSynchronization(new ScheduleTimerTransactionSynchronization(timerJobInstance, delegate));
            
            return;
        }
        super.internalSchedule(timerJobInstance);
    }

    private class ScheduleTimerTransactionSynchronization implements TransactionSynchronization {
        
        private GlobalSchedulerService schedulerService;
        private TimerJobInstance timerJobInstance;
        
        ScheduleTimerTransactionSynchronization(TimerJobInstance timerJobInstance, GlobalSchedulerService schedulerService) {
            this.timerJobInstance = timerJobInstance;
            this.schedulerService = schedulerService;
        }
        
        @Override
        public void beforeCompletion() {                            
        }
        
        @Override
        public void afterCompletion(int status) {
            if ( status == TransactionManager.STATUS_COMMITTED ) {
                this.schedulerService.internalSchedule(timerJobInstance);
            }
            
        }
    }
}
