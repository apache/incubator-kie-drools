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

package org.jbpm.executor.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.UpdateStringCommand;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.Reoccurring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execution error clean up command that aims at doing house keeping of execution error table used in jBPM:
 * Command by default is auto configured to run once a day from the time it was initially scheduled though it can be reconfigured
 * in terms of frequency when it is executed and if it shall run multiple times at all.<br/>
 * Following is a complete list of accepted parameters:
 * <ul>
 * 	<li>DateFormat - date format for further date related params - if not given yyyy-MM-dd is used (pattern of SimpleDateFormat class)</li>
 * 	<li>EmfName - name of entity manager factory to be used for queries (valid persistence unit name)</li>
 * 	<li>SingleRun - indicates if execution should be single run only (true|false)</li>
 * 	<li>NextRun - provides next execution time (valid time expression e.g. 1d, 5h, etc)</li>
 * 	<li>OlderThan - indicates what errors should be deleted - older than given date</li>
 * 	<li>OlderThanPeriod - indicated what errors should be deleted older than given time expression (valid time expression e.g. 1d, 5h, etc)</li>
 * 	<li>ForProcess - indicates errors to be deleted only for given process definition</li>
 *  <li>ForProcessInstance - indicates errors to be deleted only for given process instance</li>
 * 	<li>ForDeployment - indicates errors to be deleted that are from given deployment id</li>
 * </ul>
 */
public class ExecutionErrorCleanupCommand implements Command, Reoccurring {
	
	private static final Logger logger = LoggerFactory.getLogger(ExecutionErrorCleanupCommand.class);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
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
		SimpleDateFormat formatToUse = DATE_FORMAT;
		
		String dataFormat = (String) ctx.getData("DateFormat");
		if (dataFormat != null) {
			formatToUse = new SimpleDateFormat(dataFormat);
		}
		
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

		// collect parameters
		String olderThan = (String)ctx.getData("OlderThan");
		String olderThanPeriod = (String)ctx.getData("OlderThanPeriod");
		String forProcess = (String)ctx.getData("ForProcess");
		String forProcessInstance = (String)ctx.getData("ForProcessInstance");
		String forDeployment = (String)ctx.getData("ForDeployment");
		
		if (olderThanPeriod != null) {
			long olderThanDuration = DateTimeUtils.parseDateAsDuration(olderThanPeriod);
			Date olderThanDate = new Date(System.currentTimeMillis() - olderThanDuration);
			
			olderThan = formatToUse.format(olderThanDate);
		}
		Map<String, Object> parameters = new HashMap<>();
        StringBuilder cleanUpErrorsQuery = new StringBuilder();
        
        cleanUpErrorsQuery.append("delete from ExecutionErrorInfo where processInstanceId in "
                + "(select processInstanceId from ProcessInstanceLog where status in (2,3))");
        if (olderThan != null && !olderThan.isEmpty()) {            
            cleanUpErrorsQuery.append(" and errorDate < :olderThan");
            parameters.put("olderThan", formatToUse.parse(olderThan));
        }
        if (forProcess != null && !forProcess.isEmpty()) {            
            cleanUpErrorsQuery.append(" and processId = :forProcess");
            parameters.put("forProcess", forProcess);
        }
        if (forProcessInstance != null && !forProcessInstance.isEmpty()) {            
            cleanUpErrorsQuery.append(" and processInstanceId = :forProcessInstance");
            parameters.put("forProcessInstance", Long.parseLong(forProcessInstance));
        }
        if (forDeployment != null && !forDeployment.isEmpty()) {            
            cleanUpErrorsQuery.append(" and deploymentId = :forDeployment");
            parameters.put("forDeployment", forDeployment);
        }
        
        TransactionalCommandService commandService = new TransactionalCommandService(emf);
        
        int deletedErrors = commandService.execute(new UpdateStringCommand(cleanUpErrorsQuery.toString(), parameters));
		logger.debug("Number of Execution errors deleted {}", deletedErrors);
		
		executionResults.setData("ErrorsDeleted", deletedErrors);
        return executionResults;
	}

}
