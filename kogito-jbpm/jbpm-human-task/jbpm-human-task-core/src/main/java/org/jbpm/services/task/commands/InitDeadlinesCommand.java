package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.query.DeadlineSummaryImpl;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name="init-deadlines-command")
@XmlAccessorType(XmlAccessType.NONE)
public class InitDeadlinesCommand extends TaskCommand<Void> {
	
	private static final long serialVersionUID = -8095766991770311489L;
	private static final Logger logger = LoggerFactory.getLogger(InitDeadlinesCommand.class);

	public InitDeadlinesCommand() {		
	}

	@Override
	public Void execute(Context context) {
		TaskContext ctx = (TaskContext) context;
		
		TaskPersistenceContext persistenceContext = ctx.getPersistenceContext();
		TaskDeadlinesService deadlineService = ctx.getTaskDeadlinesService();
		
        try {
	        long now = System.currentTimeMillis();
	        List<DeadlineSummaryImpl> resultList = persistenceContext.queryInTransaction("UnescalatedStartDeadlines",
	        										ClassUtil.<List<DeadlineSummaryImpl>>castClass(List.class));
	        for (DeadlineSummaryImpl summary : resultList) {
	            long delay = summary.getDate().getTime() - now;
	            deadlineService.schedule(summary.getTaskId(), summary.getDeadlineId(), delay, DeadlineType.START);
	
	        }
	        
	        resultList = persistenceContext.queryInTransaction("UnescalatedEndDeadlines",
	        		ClassUtil.<List<DeadlineSummaryImpl>>castClass(List.class));
	        for (DeadlineSummaryImpl summary : resultList) {
	            long delay = summary.getDate().getTime() - now;
	            deadlineService.schedule(summary.getTaskId(), summary.getDeadlineId(), delay, DeadlineType.END);
	        }
        } catch (Exception e) {

        	logger.error("Error when executing deadlines", e);
        }
		return null;
	}

}
