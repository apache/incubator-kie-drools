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
package org.jbpm.persistence.timer;

import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.DefaultJobHandle;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.drools.persistence.jpa.JDKCallableJobCommand;
import org.drools.persistence.jpa.JpaTimerJobInstance;
import org.jbpm.persistence.jta.ContainerManagedTransactionManager;
import org.jbpm.process.core.async.AsyncExecutionMarker;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.process.core.timer.impl.GlobalTimerService.DisposableCommandService;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.ExecutableRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension to the regular <code>JpaTimerJobInstance</code> that makes use of
 * GlobalTimerService to allow auto reactivate session.
 * 
 * Important to note is that when timer service created session this job instance
 * will dispose that session to leave it in the same state it was before job was executed
 * to avoid concurrent usage of the same session by different threads
 *
 */
public class GlobalJpaTimerJobInstance extends JpaTimerJobInstance {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalJpaTimerJobInstance.class);

    private static final long serialVersionUID = -5383556604449217342L;
    private String timerServiceId;

    public GlobalJpaTimerJobInstance(Job job, JobContext ctx, Trigger trigger,
            JobHandle handle, InternalSchedulerService scheduler) {
        super(job, ctx, trigger, handle, scheduler);
        timerServiceId = ((GlobalTimerService) scheduler).getTimerServiceId();
    }

    @Override
    public Void call() throws Exception {
        AsyncExecutionMarker.markAsync();
        ExecutableRunner runner = null;
        TransactionManager jtaTm = null;
        boolean success = false;
        try { 
            JDKCallableJobCommand command = new JDKCallableJobCommand( this );
            if (scheduler == null) {
                scheduler = (InternalSchedulerService) TimerServiceRegistry.getInstance().get(timerServiceId);
            }
            if (scheduler == null) {
            	throw new RuntimeException("No scheduler found for " + timerServiceId);
            }
            jtaTm = startTxIfNeeded(((GlobalTimerService) scheduler).getRuntimeManager().getEnvironment().getEnvironment());

			runner = ((GlobalTimerService) scheduler).getRunner( getJobContext() );

			runner.execute( command );
            GlobalJPATimerJobFactoryManager timerService = ((GlobalJPATimerJobFactoryManager)((GlobalTimerService) scheduler).getTimerJobFactoryManager());
            timerService.removeTimerJobInstance(((DefaultJobHandle)getJobHandle()).getTimerJobInstance());
            success = true;
            return null;
        } catch( Exception e ) { 
        	e.printStackTrace();
        	success = false;
            throw e;
        } finally {
            AsyncExecutionMarker.reset();
            if (runner != null && runner instanceof DisposableCommandService) {
            	if (allowedToDispose(((DisposableCommandService) runner).getEnvironment())) {
            		logger.debug("Allowed to dispose command service from global timer job instance");
            		((DisposableCommandService) runner).dispose();
            	}
            }
            closeTansactionIfNeeded(jtaTm, success);
        }
    }
    
    @Override
	public String toString() {
		return "GlobalJpaTimerJobInstance [timerServiceId=" + timerServiceId
				+ ", getJobHandle()=" + getJobHandle() + "]";
	}

	protected boolean allowedToDispose(Environment environment) {
    	if (hasEnvironmentEntry(environment, "IS_JTA_TRANSACTION", false)) {
    		return true;
    	}
    	TransactionManager transactionManager = null;
    	Object txm = environment.get(EnvironmentName.TRANSACTION_MANAGER);
    	if (txm != null && txm instanceof TransactionManager) {
    		transactionManager = (TransactionManager) txm;
    	} else {    	
    		transactionManager = TransactionManagerFactory.get().newTransactionManager();
    	}
    	int status = transactionManager.getStatus();

    	if (status != TransactionManager.STATUS_NO_TRANSACTION
                && status != TransactionManager.STATUS_ROLLEDBACK
                && status != TransactionManager.STATUS_COMMITTED) {
    		return false;
    	}
    	
    	return true;
    }
    
    protected boolean hasEnvironmentEntry(Environment environment, String name, Object value) {
    	Object envEntry = environment.get(name);
    	if (value == null) {
    		return envEntry == null;
    	}
    	return value.equals(envEntry);
    }
    
    protected TransactionManager startTxIfNeeded(Environment environment) {

    	try {	    	
	    	if (hasEnvironmentEntry(environment, "IS_TIMER_CMT", true)) {
        		return null;
        	}
    		if (environment.get(EnvironmentName.TRANSACTION_MANAGER) instanceof ContainerManagedTransactionManager) {
    			TransactionManager tm = TransactionManagerFactory.get().newTransactionManager();
    			
    			if (tm.begin()) {    			
    				return tm;
    			}
    		}
	    	
    	} catch (Exception e) {
    		logger.debug("Unable to optionally start transaction due to {}", e.getMessage(), e);
    	}
    	
    	return null;
    }
    
    protected void closeTansactionIfNeeded(TransactionManager jtaTm, boolean commit) {
    	if (jtaTm != null) {
    		if (commit) {
    			jtaTm.commit(true);
    		} else {
    			jtaTm.rollback(true);
    		}
    	}
    }

}
