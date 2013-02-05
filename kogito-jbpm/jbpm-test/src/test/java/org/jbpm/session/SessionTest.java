package org.jbpm.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.transaction.Synchronization;
import javax.transaction.UserTransaction;

import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.SingleSessionCommandService;
import org.drools.persistence.jpa.JpaJDKTimerService;
import org.drools.persistence.jpa.processinstance.JPAWorkItemManagerFactory;
import org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory;
import org.jbpm.persistence.processinstance.JPASignalManagerFactory;
import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.PermissionDeniedException;
import org.jbpm.test.JbpmJUnitTestCase;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkflowProcessInstance;

import bitronix.tm.TransactionManagerServices;

public class SessionTest extends JbpmJUnitTestCase {
	
	private int nbThreadsProcess = 10;
	private int nbThreadsTask = 10;
	private int nbInvocations = 10;
	private transient int completedStart = 0;
	private transient int completedTask = 0;
	
	public SessionTest() {
		super(true);
	}
	
	@Test
	public void testDummy() {
	}
	
	public void testSingletonSessionMemory() throws Exception {
		for (int i = 0; i < 1000; i++) {
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
			KnowledgeBase kbase = kbuilder.newKnowledgeBase(); 
			SessionManagerFactory factory = new SingletonSessionManagerFactory(kbase);
			SessionManager sessionManager = factory.getSessionManager();
			sessionManager.dispose();
			factory.dispose();
			System.gc();
			Thread.sleep(100);
			System.gc();
			System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		}
	}
	
	public void testSingletonSession() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase(); 
		SessionManagerFactory factory = new SingletonSessionManagerFactory(kbase);
		completedStart = 0;
		for (int i=0; i<nbThreadsProcess; i++) {
			new Thread(new StartProcessRunnable(factory, i)).start();
		}
		completedTask = 0;
		for (int i=0; i<nbThreadsTask; i++) {
			new Thread(new CompleteTaskRunnable(factory, i)).start();
		}
		while (completedStart < nbThreadsProcess || completedTask < nbThreadsTask) {
			Thread.sleep(100);
		}
		Thread.sleep(1000);
		factory.dispose();
		System.out.println("Done");
	}
	
	public void testNewSession() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase(); 
		SessionManagerFactory factory = new NewSessionSessionManagerFactory(kbase);
		completedStart = 0;
		for (int i=0; i<nbThreadsProcess; i++) {
			new StartProcessRunnable(factory, i).run();
		}
		completedTask = 0;
		for (int i=0; i<nbThreadsTask; i++) {
			new Thread(new CompleteTaskRunnable(factory, i)).start();
		}
		while (completedStart < nbThreadsProcess || completedTask < nbThreadsTask) {
			Thread.sleep(100);
		}
		factory.dispose();
		System.out.println("Done");
	}
	
	public void testNewSessionFail() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase(); 
		SessionManagerFactory factory = new NewSessionSessionManagerFactory(kbase);
		SessionManager sessionManager = factory.getSessionManager();
		UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		ut.begin();
		ProcessInstance processInstance = sessionManager.getKnowledgeSession().startProcess("com.sample.bpmn.hello", null);
		System.out.println("Started process instance " + processInstance.getId());
		long workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
		long taskId = sessionManager.getTaskService().getTaskByWorkItemId(workItemId).getId();
		sessionManager.getTaskService().claim(taskId, "mary");
		ut.rollback();
		System.out.println("Rolled back");
		// TODO: whenever transaction fails, do we need to dispose? can we?
		// sessionManager.dispose();

		List<Status> statusses = new ArrayList<Status>();
		statusses.add(Status.Reserved);

		sessionManager = factory.getSessionManager();
		assertNull(sessionManager.getKnowledgeSession().getProcessInstance(processInstance.getId()));
		List<TaskSummary> tasks = sessionManager.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(0, tasks.size());
		
		ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		ut.begin();
		processInstance = sessionManager.getKnowledgeSession().startProcess("com.sample.bpmn.hello", null);
		workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
		taskId = sessionManager.getTaskService().getTaskByWorkItemId(workItemId).getId();
		sessionManager.getTaskService().claim(taskId, "mary");
		System.out.println("Started process instance " + processInstance.getId());
		ut.commit();

		assertNotNull(sessionManager.getKnowledgeSession().getProcessInstance(processInstance.getId()));
		tasks = sessionManager.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());

		taskId = tasks.get(0).getId();
		ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		ut.begin();
		sessionManager.getTaskService().start(taskId, "mary");
		sessionManager.getTaskService().complete(taskId, "mary", null);
		ut.rollback();
		sessionManager.dispose();
		
		sessionManager = factory.getSessionManager();
		assertNotNull(sessionManager.getKnowledgeSession().getProcessInstance(processInstance.getId()));
		tasks = sessionManager.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());

		taskId = tasks.get(0).getId();
		ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		ut.begin();
		sessionManager.getTaskService().start(taskId, "mary");
		sessionManager.getTaskService().complete(taskId, "mary", null);
		ut.commit();
		
		assertNull(sessionManager.getKnowledgeSession().getProcessInstance(processInstance.getId()));
		tasks = sessionManager.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(0, tasks.size());
		sessionManager.dispose();
		
		factory.dispose();
	}
	
	public void testNewSessionDispose() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase(); 
		SessionManagerFactory factory = new NewSessionSessionManagerFactory(kbase);
		final SessionManager sessionManager = factory.getSessionManager();
		UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		ut.begin();
		sessionManager.getKnowledgeSession().startProcess("com.sample.bpmn.hello", null);
		TransactionManagerServices.getTransactionManager().getTransaction().registerSynchronization(new Synchronization() {
			public void beforeCompletion() {
			}
			public void afterCompletion(int status) {
				try {
					sessionManager.dispose();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		ut.commit();
		factory.dispose();
	}
	
	public void testNewSessionFailBefore() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("sampleFailBefore.bpmn"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase(); 
		SessionManagerFactory factory = new NewSessionSessionManagerFactory(kbase);
		SessionManager sessionManager = factory.getSessionManager();

		try{
			ProcessInstance processInstance = sessionManager.getKnowledgeSession().startProcess("com.sample.bpmn.hello", null);
			fail("Started process instance " + processInstance.getId());
		} catch (RuntimeException e) {
			// do nothing
		}

		// TODO: whenever transaction fails, do we need to dispose? can we?
		// sessionManager.dispose();

		List<Status> statusses = new ArrayList<Status>();
		statusses.add(Status.Reserved);

		sessionManager = factory.getSessionManager();
		List<TaskSummary> tasks = sessionManager.getTaskService().getTasksAssignedAsPotentialOwner("mary", "en-UK");
		assertEquals(0, tasks.size());
		
		sessionManager.dispose();
		factory.dispose();
	}
	
	public void testNewSessionFailAfter() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("sampleFailAfter.bpmn"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase(); 
		SessionManagerFactory factory = new NewSessionSessionManagerFactory(kbase);
		SessionManager sessionManager = factory.getSessionManager();

		ProcessInstance processInstance = sessionManager.getKnowledgeSession().startProcess("com.sample.bpmn.hello", null);
		long workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
		long taskId = sessionManager.getTaskService().getTaskByWorkItemId(workItemId).getId();
		sessionManager.getTaskService().claim(taskId, "mary");

		List<Status> statusses = new ArrayList<Status>();
		statusses.add(Status.Reserved);

		List<TaskSummary> tasks = sessionManager.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());

		taskId = tasks.get(0).getId();
		UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		try {
			ut.begin();
			sessionManager.getTaskService().start(taskId, "mary");
			sessionManager.getTaskService().complete(taskId, "mary", null);
			fail("Task completed");
		} catch (RuntimeException e) {
			// do nothing
		}
		ut.rollback();

		// TODO: whenever transaction fails, do we need to dispose? can we?
		// sessionManager.dispose();

		sessionManager = factory.getSessionManager();
		tasks = sessionManager.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());
		
		sessionManager.dispose();
		factory.dispose();
	}
	
	public void testNewSessionFailAfter2() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("sampleFailAfter.bpmn"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase(); 
		SessionManagerFactory factory = new NewSessionSessionManagerFactory(kbase);
		SessionManager sessionManager = factory.getSessionManager();

		ProcessInstance processInstance = sessionManager.getKnowledgeSession().startProcess("com.sample.bpmn.hello", null);
		long workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
		long taskId = sessionManager.getTaskService().getTaskByWorkItemId(workItemId).getId();
		sessionManager.getTaskService().claim(taskId, "mary");
		sessionManager.getTaskService().start(taskId, "mary");

		List<Status> statusses = new ArrayList<Status>();
		statusses.add(Status.InProgress);

		List<TaskSummary> tasks = sessionManager.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());

		taskId = tasks.get(0).getId();
		try {
			sessionManager.getTaskService().complete(taskId, "mary", null);
			fail("Task completed");
		} catch (RuntimeException e) {
			// do nothing
		}

		// TODO: whenever transaction fails, do we need to dispose? can we?
		// sessionManager.dispose();

		sessionManager = factory.getSessionManager();
		tasks = sessionManager.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());
		
		sessionManager.dispose();
		factory.dispose();
	}
	
	private void testStartProcess(SessionManagerFactory factory) throws Exception {
		SessionManager sessionManager = factory.getSessionManager();
		long taskId; 
		synchronized((SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) sessionManager.getKnowledgeSession()).getCommandService()) {
			UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
			ut.begin();
			System.out.println("Starting process on ksession " + sessionManager.getKnowledgeSession().getId());
			ProcessInstance processInstance = sessionManager.getKnowledgeSession().startProcess("com.sample.bpmn.hello", null);
			System.out.println("Started process instance " + processInstance.getId() + " on ksession " + sessionManager.getKnowledgeSession().getId());
			long workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
			taskId = sessionManager.getTaskService().getTaskByWorkItemId(workItemId).getId();
			System.out.println("Created task " + taskId);
			ut.commit();
		}
		sessionManager.getTaskService().claim(taskId, "mary");
		sessionManager.dispose();
	}
	
	public class StartProcessRunnable implements Runnable {
		private SessionManagerFactory factory;
		private int counter;
		public StartProcessRunnable(SessionManagerFactory factory, int counter) {
			this.factory = factory;
			this.counter = counter;
		}
		public void run() {
			try {
				for (int i=0; i<nbInvocations; i++) {
//					System.out.println("Thread " + counter + " doing call " + i);
					testStartProcess(factory);
				}
//				System.out.println("Process thread " + counter + " completed");
				completedStart++;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private boolean testCompleteTask(SessionManagerFactory factory) throws InterruptedException, Exception {
		boolean result = false;
		List<Status> statusses = new ArrayList<Status>();
		statusses.add(Status.Reserved);
		SessionManager sessionManager = factory.getSessionManager();
		List<TaskSummary> tasks = null;
		tasks = sessionManager.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		if (tasks.isEmpty()) {
			System.out.println("Task thread found no tasks");
			Thread.sleep(1000);
		} else {
			long taskId = tasks.get(0).getId();
			System.out.println("Completing task " + taskId);
			boolean success = false;
			try {
				sessionManager.getTaskService().start(taskId, "mary");
				success = true;
			} catch (PermissionDeniedException e) {
				// TODO can we avoid these by doing it all in one transaction?
				System.out.println("Task thread was too late for starting task " + taskId);
			} catch (RuntimeException e) {
				if (e.getCause() instanceof OptimisticLockException) {
					System.out.println("Task thread got in conflict when starting task " + taskId);
				} else {
					throw e;
				}
			}
			if (success) {
				sessionManager.getTaskService().complete(taskId, "mary", null);
				System.out.println("Completed task " + taskId);
				result = true;
			}
		}
		sessionManager.dispose();
		return result;
	}

	public class CompleteTaskRunnable implements Runnable {
		private SessionManagerFactory factory;
		private int counter;
		public CompleteTaskRunnable(SessionManagerFactory factory, int counter) {
			this.factory = factory;
			this.counter = counter;
		}
		public void run() {
			try {
				int i = 0;
				while (i < nbInvocations) {
					boolean success = testCompleteTask(factory);
					if (success) {
						i++;
					}
				}
				completedTask++;
//				System.out.println("Task thread " + counter + " completed");
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
