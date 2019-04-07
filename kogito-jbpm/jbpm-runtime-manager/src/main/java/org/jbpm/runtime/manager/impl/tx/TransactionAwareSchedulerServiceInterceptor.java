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

package org.jbpm.runtime.manager.impl.tx;

import org.drools.core.time.JobContext;
import org.drools.core.time.SelfRemovalJobContext;
import org.drools.core.time.impl.TimerJobInstance;
import org.drools.persistence.api.OrderedTransactionSynchronization;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.drools.persistence.api.TransactionManagerHelper;
import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.NamedJobContext;
import org.jbpm.process.core.timer.impl.DelegateSchedulerServiceInterceptor;
import org.jbpm.process.instance.timer.TimerManager.ProcessJobContext;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

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

	private RuntimeEnvironment environment;
	private RuntimeManager manager;
	
    public TransactionAwareSchedulerServiceInterceptor(RuntimeEnvironment environment, RuntimeManager manager, GlobalSchedulerService schedulerService) {
        super(schedulerService);
        this.environment = environment;
        this.manager = manager;
    }

    @Override
    public final void internalSchedule(final TimerJobInstance timerJobInstance) {
    	if (hasEnvironmentEntry("IS_JTA_TRANSACTION", false)) {
    		super.internalSchedule(timerJobInstance);
    		return;
    	}
    	
        TransactionManager tm = getTransactionManager(timerJobInstance.getJobContext());
        if (tm.getStatus() != TransactionManager.STATUS_NO_TRANSACTION
                && tm.getStatus() != TransactionManager.STATUS_ROLLEDBACK
                && tm.getStatus() != TransactionManager.STATUS_COMMITTED) {
            TransactionManagerHelper.registerTransactionSyncInContainer(tm, 
            		new ScheduleTimerTransactionSynchronization(timerJobInstance, delegate));
            
            return;
        }
        super.internalSchedule(timerJobInstance);
    }

    private class ScheduleTimerTransactionSynchronization extends OrderedTransactionSynchronization {
        
        private GlobalSchedulerService schedulerService;
        private TimerJobInstance timerJobInstance;
        
        ScheduleTimerTransactionSynchronization(TimerJobInstance timerJobInstance, GlobalSchedulerService schedulerService) {
        	super(5, "TransactionAwareSchedulerServiceInterceptor");
            this.timerJobInstance = timerJobInstance;
            this.schedulerService = schedulerService;
        }
        
        @Override
        public void beforeCompletion() {                            
        }
        
        @Override
        public void afterCompletion(int status) {
            if ( status == TransactionManager.STATUS_COMMITTED && !timerJobInstance.getJobHandle().isCancel()) {
                this.schedulerService.internalSchedule(timerJobInstance);
            }
            
        }

		@Override
		public int compareTo(OrderedTransactionSynchronization o) {
			if (o instanceof ScheduleTimerTransactionSynchronization) {
				if (this.timerJobInstance.equals(((ScheduleTimerTransactionSynchronization) o).timerJobInstance)) {
					return 0;
				}
				return -1;
			}
			return super.compareTo(o);
		}
        
        
    }
    
    protected boolean hasEnvironmentEntry(String name, Object value) {
    	Object envEntry = environment.getEnvironment().get(name);
    	if (value == null) {
    		return envEntry == null;
    	}
    	return value.equals(envEntry);
    }
    
    protected TransactionManager getTransactionManager(JobContext jobContext) {
    	
    	Object txm = getEnvironment(jobContext).get(EnvironmentName.TRANSACTION_MANAGER);
    	if (txm != null && txm instanceof TransactionManager) {
    		return (TransactionManager) txm;
    	}
    	
    	return TransactionManagerFactory.get().newTransactionManager();
    }
    
    protected Environment getEnvironment(JobContext jobContext) {
    	JobContext ctxorig = jobContext;
        if (ctxorig instanceof SelfRemovalJobContext) {
            ctxorig = ((SelfRemovalJobContext) ctxorig).getJobContext();
        }
    	// first attempt to get knowledge runtime's environment if job context is a process one
    	if (ctxorig instanceof ProcessJobContext) {
    		return ((ProcessJobContext) ctxorig).getKnowledgeRuntime().getEnvironment();
    	} else {
    		// next if we have manager set use it to get ksession's environment of active RuntimeEngine
    		// while running this there must be an active RuntimeEngine present
	    	if (manager != null) {
	    		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(getProcessInstancId(ctxorig)));
	    		return engine.getKieSession().getEnvironment();
	    	} else {
	    		// last resort use the runtime environment's environment template
	    		return environment.getEnvironment();
	    	}
    	}
    }
    
    protected Long getProcessInstancId(JobContext jobContext) {
        
        if (jobContext instanceof ProcessJobContext) {
            return ((ProcessJobContext) jobContext).getProcessInstanceId();
        } else if(jobContext instanceof NamedJobContext){
        	return ((NamedJobContext)jobContext).getProcessInstanceId();
        } else {
            return null; 
        }
    }
}
