package org.jbpm.session;

import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.SingleSessionCommandService;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.local.LocalTaskService;
import org.kie.runtime.StatefulKnowledgeSession;

public class SessionEnvironment {
	private StatefulKnowledgeSession ksession;
	private TaskService taskService;
	private LocalHTWorkItemHandler humanTaskHandler;
	private JPAWorkingMemoryDbLogger historyLogger;
	
	public SessionEnvironment(StatefulKnowledgeSession ksession,
			                  LocalTaskService taskService,
			                  LocalHTWorkItemHandler humanTaskHandler,
			                  JPAWorkingMemoryDbLogger historyLogger) {
		this.ksession = ksession;
		// TODO: this is not necessary for new session, should we avoid this?
		this.taskService = new SynchronizedTaskService(
			(SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession).getCommandService(),
			taskService);
		this.humanTaskHandler = humanTaskHandler;
		this.historyLogger = historyLogger;
	}
	
	public StatefulKnowledgeSession getKnowledgeSession() {
		return ksession;
	}
	
	public TaskService getTaskService() {
		return taskService;
	}
	
	// TODO: this probably shouldn't throw Exception
	public void dispose() throws Exception {
		if (historyLogger != null) {
			historyLogger.dispose();
			historyLogger = null;
		}
		// TODO: make these debug statements
		System.out.println("Disposing ksession " + ksession.getId());
//		System.out.println("Disposing taskService");
		humanTaskHandler.dispose();
		humanTaskHandler = null;
		taskService.disconnect();
		// TODO: is disconnect enough, do we need taskService.dispose(); ?
		taskService = null;
		// TODO: (automatically?) support disposal of ksession through transaction listener callback
		ksession.dispose();
		// TODO: ksession.destroy();
		// TODO: Can we already set ksession to null, even though the ksession is not disposed yet?
		ksession = null;
	}
}