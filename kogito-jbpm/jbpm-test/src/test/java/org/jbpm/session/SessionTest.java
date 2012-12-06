package org.jbpm.session;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OptimisticLockException;

import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.PermissionDeniedException;
import org.jbpm.test.JbpmJUnitTestCase;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkflowProcessInstance;

public class SessionTest extends JbpmJUnitTestCase {
	
	private int nbThreadsProcess = 10;
	private int nbThreadsTask = 10;
	private int nbInvocations = 10;
	
	public SessionTest() {
		super(true);
	}
	
	@Test
	public void testDummy() {
	}
	
	public void testSingletonSession() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase(); 
		SessionManagerFactory factory = new SingletonSessionManagerFactory(kbase);
		for (int i=0; i<nbThreadsProcess; i++) {
			new Thread(new StartProcessRunnable(factory, i)).start();
		}
		for (int i=0; i<nbThreadsTask; i++) {
			new Thread(new CompleteTaskRunnable(factory, i)).start();
		}
		Thread.sleep(20000);
		factory.dispose();
		// TODO: check memory gc()
		System.out.println("Done");
	}
	
	public void testNewSession() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase(); 
		SessionManagerFactory factory = new NewSessionSessionManagerFactory(kbase);
		for (int i=0; i<nbThreadsProcess; i++) {
			new StartProcessRunnable(factory, i).run();
		}
		for (int i=0; i<nbThreadsTask; i++) {
			new Thread(new CompleteTaskRunnable(factory, i)).start();
		}
		Thread.sleep(10000);
		factory.dispose();
		System.out.println("Done");
	}
	
	public void testStartProcess(SessionManagerFactory factory) throws Exception {
		SessionManager sessionManager = factory.getSessionManager();
		System.out.println("Starting process on ksession " + sessionManager.getKnowledgeSession().getId());
		ProcessInstance processInstance = sessionManager.getKnowledgeSession().startProcess("com.sample.bpmn.hello", null);
		System.out.println("Started process instance " + processInstance.getId() + " on ksession " + sessionManager.getKnowledgeSession().getId());
		long workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
		long taskId = sessionManager.getTaskService().getTaskByWorkItemId(workItemId).getId();
		System.out.println("Created task " + taskId);
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
					System.out.println("Thread " + counter + " doing call " + i);
					testStartProcess(factory);
				}
				System.out.println("Process thread " + counter + " completed");
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
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
					List<Status> statusses = new ArrayList<Status>();
					statusses.add(Status.Reserved);
					SessionManager sessionManager = factory.getSessionManager();
					List<TaskSummary> tasks = sessionManager.getTaskService().getTasksOwned("mary", statusses, "en-UK");
					if (tasks.isEmpty()) {
						System.out.println("Task thread " + counter + " found no tasks");
						Thread.sleep(1000);
					} else {
						long taskId = tasks.get(0).getId();
						System.out.println("Task thread " + counter + " is completing task " + taskId);
						boolean success = false;
						try {
							sessionManager.getTaskService().start(taskId, "mary");
							success = true;
						} catch (PermissionDeniedException e) {
							// TODO can we avoid these by doing it all in one transaction?
							System.out.println("Task thread " + counter + " was too late for starting task " + taskId);
						} catch (RuntimeException e) {
							if (e.getCause() instanceof OptimisticLockException) {
								System.out.println("Task thread " + counter + " got in conflict when starting task " + taskId);
							} else {
								throw e;
							}
						}
						if (success) {
							sessionManager.getTaskService().complete(taskId, "mary", null);
							i++;
						}
					}
					sessionManager.dispose();
				}
				System.out.println("Task thread " + counter + " completed");
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

}
