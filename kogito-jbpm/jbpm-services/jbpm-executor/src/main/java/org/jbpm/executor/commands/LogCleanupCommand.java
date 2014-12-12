package org.jbpm.executor.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManagerFactory;

import org.jbpm.executor.impl.jpa.ExecutorJPAAuditService;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.internal.executor.api.Reoccurring;
import org.kie.internal.executor.api.STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			Date oldetThanDate = new Date(System.currentTimeMillis() - olderThanDuration);
			
			olderThan = DATE_FORMAT.format(oldetThanDate);
		}
		if (!skipProcessLog) {
		// process tables
			long piLogsRemoved = 0l;		
			piLogsRemoved = auditLogService.processInstanceLogDelete()
			.processId(forProcess)
			.status(ProcessInstance.STATE_COMPLETED, ProcessInstance.STATE_ABORTED)
			.endDateRangeEnd(olderThan==null?null:DATE_FORMAT.parse(olderThan))
			.externalId(forDeployment)
			.build()
			.execute();
			logger.info("ProcessInstanceLogRemoved {}", piLogsRemoved);
			executionResults.setData("ProcessInstanceLogRemoved", piLogsRemoved);
			
			long niLogsRemoved = 0l;
			niLogsRemoved = auditLogService.nodeInstanceLogDelete()
			.processId(forProcess)
			.dateRangeEnd(olderThan==null?null:DATE_FORMAT.parse(olderThan))
			.externalId(forDeployment)
			.build()
			.execute();
			logger.info("NodeInstanceLogRemoved {}", niLogsRemoved);
			executionResults.setData("NodeInstanceLogRemoved", niLogsRemoved);
			
			long viLogsRemoved = 0l;
			viLogsRemoved = auditLogService.variableInstanceLogDelete()
			.processId(forProcess)
			.dateRangeEnd(olderThan==null?null:DATE_FORMAT.parse(olderThan))
			.externalId(forDeployment)
			.build()
			.execute();
			logger.info("VariableInstanceLogRemoved {}", viLogsRemoved);
			executionResults.setData("VariableInstanceLogRemoved", viLogsRemoved);
		}
		
		if (!skipTaskLog) {
			// task tables
			long taLogsRemoved = 0l;
			taLogsRemoved = auditLogService.auditTaskInstanceLogDelete()
			.processId(forProcess)		
			.dateRangeEnd(olderThan==null?null:DATE_FORMAT.parse(olderThan))
			.deploymentId(forDeployment)
			.build()
			.execute();
			logger.info("TaskAuditLogRemoved {}", taLogsRemoved);
			executionResults.setData("TaskAuditLogRemoved", taLogsRemoved);
			
			long teLogsRemoved = 0l;
			teLogsRemoved = auditLogService.taskEventInstanceLogDelete()
			.dateRangeEnd(olderThan==null?null:DATE_FORMAT.parse(olderThan))		
			.build()
			.execute();
			logger.info("TaskEventLogRemoved {}", teLogsRemoved);
			executionResults.setData("TaskEventLogRemoved", teLogsRemoved);
		}
		
		if (!skipExecutorLog) {
			// executor tables	
			long errorInfoLogsRemoved = 0l;
			errorInfoLogsRemoved = auditLogService.errorInfoLogDeleteBuilder()		
			.dateRangeEnd(olderThan==null?null:DATE_FORMAT.parse(olderThan))
			.build()
			.execute();
			logger.info("ErrorInfoLogsRemoved {}", errorInfoLogsRemoved);
			executionResults.setData("ErrorInfoLogsRemoved", errorInfoLogsRemoved);
			
			long requestInfoLogsRemoved = 0l;
			requestInfoLogsRemoved = auditLogService.requestInfoLogDeleteBuilder()
			.dateRangeEnd(olderThan==null?null:DATE_FORMAT.parse(olderThan))
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
