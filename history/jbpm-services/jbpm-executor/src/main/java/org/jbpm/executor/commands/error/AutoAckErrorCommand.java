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

package org.jbpm.executor.commands.error;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.impl.jpa.ExecutionErrorInfo;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.Reoccurring;
import org.kie.internal.runtime.error.ExecutionErrorManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AutoAckErrorCommand implements Command, Reoccurring {
	
	private static final Logger logger = LoggerFactory.getLogger(AutoAckErrorCommand.class);	
	
	private long nextScheduleTimeAdd = 24 * 60 * 60 * 1000; // one day in milliseconds

	@Override
	public Date getScheduleTime() {
		if (nextScheduleTimeAdd < 0) {
			return null;
		}
		
		long current = System.currentTimeMillis();
		
		Date nextSchedule = new Date(current + nextScheduleTimeAdd);
		logger.debug("Next schedule for job {} is set to {}", this.getClass().getSimpleName(), nextSchedule);
		
		return nextSchedule;
	}

	
    @Override
	public ExecutionResults execute(CommandContext ctx) throws Exception {
			
		ExecutionResults executionResults = new ExecutionResults();
		String emfName = (String)ctx.getData("EmfName");
		if (emfName == null) {
			emfName = "org.jbpm.domain"; 
		}
		String singleRun = (String)ctx.getData("SingleRun");
		if ("true".equalsIgnoreCase(singleRun)) {
			// disable rescheduling
			this.nextScheduleTimeAdd = -1;
		}
		String nextRun = (String)ctx.getData("NextRun");
		if (nextRun != null) {
			nextScheduleTimeAdd = DateTimeUtils.parseDateAsDuration(nextRun);
		}
		
		// get hold of persistence and create instance of audit service
		EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate(emfName);
		
		
		EntityManager em = emf.createEntityManager();
		try {
		
		    List<ExecutionErrorInfo> errorsToAck = findErrorsToAck(em);
		    logger.debug("Found {} jobs that can be auto ack", errorsToAck.size());
		    
		    errorsToAck.forEach(error -> {
		        
		        AbstractRuntimeManager manager = (AbstractRuntimeManager) RuntimeManagerRegistry.get().getManager(error.getDeploymentId());
		        if (manager != null) {
		            ExecutionErrorManager errorManager = manager.getExecutionErrorManager();
		            
		            errorManager.getStorage().acknowledge("SYSTEM", error.getErrorId());
		            logger.debug("Error {} has been auto acknowledged by system based on {}", error.getErrorId(), getAckRule());
		        } else {
		            logger.warn("Unable to ack error {} due missing runtime manager for '{}'", error.getErrorId(), error.getDeploymentId());
		        }
		    });
		    
		} finally {
		    em.close();
		}
		
        return executionResults;
	}
	
    /**
     * Responsible to look up all errors that can be auto acknowledged.
     * @param em instance of entity manager to access db
     * @return returns always not null list of results
     */
	protected abstract List<ExecutionErrorInfo> findErrorsToAck(EntityManager em); 
	
	/**
	 * Returns description of why given jobs can be auto ack'ed
	 */
	protected abstract String getAckRule();

}
