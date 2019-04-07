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

import javax.persistence.EntityManagerFactory;

import org.jbpm.executor.impl.jpa.ExecutorJPAAuditService;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.Reoccurring;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log clean up command that aims at doing house keeping of audit/log tables used in jBPM:
 * <ul>
 * 	<li>process related audit logs (process instance, node instance, variables)</li>
 * 	<li>task related audit logs (audit task, task events)</li>
 * 	<li>executor related data (requests and errors)</li>
 * </ul>
 * Command by default is auto configured to run once a day from the time it was initially scheduled though it can be reconfigured
 * in terms of frequency when it is executed and if it shall run multiple times at all.<br/>
 * Following is a complete list of accepted parameters:
 * <ul>
 * 	<li>SkipProcessLog - indicates if clean up of process logs should be omitted (true|false)</li>
 * 	<li>SkipTaskLog - indicates if clean up of task logs should be omitted (true|false)</li>
 * 	<li>SkipExecutorLog - indicates if clean up of executor logs should be omitted (true|false)</li>
 * 	<li>DateFormat - date format for further date related params - if not given yyyy-MM-dd is used (pattern of SimpleDateFormat class)</li>
 * 	<li>EmfName - name of entity manager factory to be used for queries (valid persistence unit name)</li>
 * 	<li>SingleRun - indicates if execution should be single run only (true|false)</li>
 * 	<li>NextRun - provides next execution time (valid time expression e.g. 1d, 5h, etc)</li>
 * 	<li>OlderThan - indicates what logs should be deleted - older than given date</li>
 * 	<li>OlderThanPeriod - indicated what logs should be deleted older than given time expression (valid time expression e.g. 1d, 5h, etc)</li>
 * 	<li>ForProcess - indicates logs to be deleted only for given process definition</li>
 * 	<li>ForDeployment - indicates logs to be deleted that are from given deployment id</li>
 * </ul>
 */
public class LogCleanupCommand implements Command, Reoccurring {
	
	private static final Logger logger = LoggerFactory.getLogger(LogCleanupCommand.class);
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
		boolean skipProcessLog = ctx.getData().containsKey("SkipProcessLog")?Boolean.parseBoolean((String)ctx.getData("SkipProcessLog")):false;
		boolean skipTaskLog = ctx.getData().containsKey("SkipTaskLog")?Boolean.parseBoolean((String)ctx.getData("SkipTaskLog")):false;;
		boolean skipExecutorLog = ctx.getData().containsKey("SkipExecutorLog")?Boolean.parseBoolean((String)ctx.getData("SkipExecutorLog")):false;;
		
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
		ExecutorJPAAuditService auditLogService = new ExecutorJPAAuditService(emf);
		
		// collect parameters
		String olderThan = (String)ctx.getData("OlderThan");
		String olderThanPeriod = (String)ctx.getData("OlderThanPeriod");
		String forProcess = (String)ctx.getData("ForProcess");
		String forDeployment = (String)ctx.getData("ForDeployment");
		
		if (olderThanPeriod != null) {
			long olderThanDuration = DateTimeUtils.parseDateAsDuration(olderThanPeriod);
			Date olderThanDate = new Date(System.currentTimeMillis() - olderThanDuration);
			
			olderThan = formatToUse.format(olderThanDate);
		}
		
        
        if (!skipTaskLog) {
            // task tables
            long taLogsRemoved = 0l;
            taLogsRemoved = auditLogService.auditTaskDelete()
            .processId(forProcess)      
            .dateRangeEnd(olderThan==null?null:formatToUse.parse(olderThan))
            .deploymentId(forDeployment)
            .build()
            .execute();
            logger.info("TaskAuditLogRemoved {}", taLogsRemoved);
            executionResults.setData("TaskAuditLogRemoved", taLogsRemoved);
            
            long teLogsRemoved = 0l;
            teLogsRemoved = auditLogService.taskEventInstanceLogDelete()
            .dateRangeEnd(olderThan==null?null:formatToUse.parse(olderThan))        
            .build()
            .execute();
            logger.info("TaskEventLogRemoved {}", teLogsRemoved);
            executionResults.setData("TaskEventLogRemoved", teLogsRemoved);
            
            long tvLogsRemoved = 0l;
            tvLogsRemoved = auditLogService.taskVariableInstanceLogDelete()
            .dateRangeEnd(olderThan==null?null:formatToUse.parse(olderThan))        
            .build()
            .execute();
            logger.info("TaskVariableLogRemoved {}", tvLogsRemoved);
            executionResults.setData("TaskVariableLogRemoved", tvLogsRemoved);
        }		
		
		if (!skipProcessLog) {
		// process tables			
			long niLogsRemoved = 0l;
			niLogsRemoved = auditLogService.nodeInstanceLogDelete()
			.processId(forProcess)
			.dateRangeEnd(olderThan==null?null:formatToUse.parse(olderThan))
			.externalId(forDeployment)
			.build()
			.execute();
			logger.info("NodeInstanceLogRemoved {}", niLogsRemoved);
			executionResults.setData("NodeInstanceLogRemoved", niLogsRemoved);
			
			long viLogsRemoved = 0l;
			viLogsRemoved = auditLogService.variableInstanceLogDelete()
			.processId(forProcess)
			.dateRangeEnd(olderThan==null?null:formatToUse.parse(olderThan))
			.externalId(forDeployment)
			.build()
			.execute();
			logger.info("VariableInstanceLogRemoved {}", viLogsRemoved);
			executionResults.setData("VariableInstanceLogRemoved", viLogsRemoved);
			
			long piLogsRemoved = 0l;        
            piLogsRemoved = auditLogService.processInstanceLogDelete()
            .processId(forProcess)
            .status(ProcessInstance.STATE_COMPLETED, ProcessInstance.STATE_ABORTED)
            .endDateRangeEnd(olderThan==null?null:formatToUse.parse(olderThan))
            .externalId(forDeployment)
            .build()
            .execute();
            logger.info("ProcessInstanceLogRemoved {}", piLogsRemoved);
            executionResults.setData("ProcessInstanceLogRemoved", piLogsRemoved);
		}

		
		if (!skipExecutorLog) {
			// executor tables	
			long errorInfoLogsRemoved = 0l;
			errorInfoLogsRemoved = auditLogService.errorInfoLogDeleteBuilder()		
			.dateRangeEnd(olderThan==null?null:formatToUse.parse(olderThan))
			.build()
			.execute();
			logger.info("ErrorInfoLogsRemoved {}", errorInfoLogsRemoved);
			executionResults.setData("ErrorInfoLogsRemoved", errorInfoLogsRemoved);
			
			long requestInfoLogsRemoved = 0l;
			requestInfoLogsRemoved = auditLogService.requestInfoLogDeleteBuilder()
			.dateRangeEnd(olderThan==null?null:formatToUse.parse(olderThan))
			.status(STATUS.CANCELLED, STATUS.DONE, STATUS.ERROR)
			.build()
			.execute();
			logger.info("RequestInfoLogsRemoved {}", requestInfoLogsRemoved);
			executionResults.setData("RequestInfoLogsRemoved", requestInfoLogsRemoved);
		}
		
		// bam tables
		long bamLogsRemoved = 0l;
		executionResults.setData("BAMLogRemoved", bamLogsRemoved);
		
		
        return executionResults;
	}

}
