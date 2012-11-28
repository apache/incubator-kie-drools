package org.jbpm.session;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.service.local.LocalTaskService;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.SystemEventListenerFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.StatefulKnowledgeSession;

public class StatefulKnowledgeSessionFactory {

	private EntityManagerFactory emf;
	private KnowledgeBase kbase;
	private boolean useHistoryLogger = true;
	
	public StatefulKnowledgeSessionFactory() {
		// TODO init configuration parameters from configuration files and/or
		// system properties etc.
	}
	
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
	}
	
	public void setKnowledgeBase(KnowledgeBase kbase) {
		this.kbase = kbase;
	}
	
	// TODO add support for injecting an EM instead of an EMF as well
	
	public void setUseHistoryLogger(boolean useHistoryLogger) {
		this.useHistoryLogger = useHistoryLogger;
	}
	
	public SessionEnvironment createStatefulKnowledgeSession() {
		Environment env = KnowledgeBaseFactory.newEnvironment();
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
		// TODO env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());
		
		StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
		JPAWorkingMemoryDbLogger historyLogger = null;
		if (useHistoryLogger) {
			historyLogger = new JPAWorkingMemoryDbLogger(ksession);
		}
		
		// TODO reuse internalTaskService (but deregister listeners)
		org.jbpm.task.service.TaskService internalTaskService = new org.jbpm.task.service.TaskService(
			emf, SystemEventListenerFactory.getSystemEventListener());
		// TODO UserGroupCallback

		// TODO support remote task service
		LocalTaskService taskService = new LocalTaskService(internalTaskService);
		LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler(taskService, ksession);
		humanTaskHandler.connect();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
		
		// TODO: make these debug statements
		System.out.println("Created ksession " + ksession.getId());
//		System.out.println("Created taskService");
//		((SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession).getCommandService()).addInterceptor(new AbstractInterceptor() {
//			public <T> T execute(Command<T> command) {
//				System.out.println("Starting command " + command.hashCode());
//				T result = executeNext(command);
//				System.out.println("Completed command " + command.hashCode());
//				return result;
//			}
//		});
		
		return new SessionEnvironment(ksession, taskService, humanTaskHandler, historyLogger);
	}
	
}
