package org.jbpm.runtime.manager.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.OptimisticLockException;
import javax.transaction.UserTransaction;

import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.SingleSessionCommandService;
import org.hibernate.StaleObjectStateException;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.task.Status;
import org.jbpm.task.TaskService;
import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.PermissionDeniedException;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.manager.RuntimeManager;
import org.kie.runtime.manager.RuntimeManagerFactory;
import org.kie.runtime.manager.context.EmptyContext;
import org.kie.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkflowProcessInstance;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class SessionTest {
    
    private long maxWaitTime = 60*1000; // max wait to complete operation is set to 60 seconds to avoid build hangs
	
	private int nbThreadsProcess = 10;
	private int nbThreadsTask = 10;
	private int nbInvocations = 10;
	private transient int completedStart = 0;
	private transient int completedTask = 0;
	
	private PoolingDataSource pds;
	
    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        Properties props = new Properties();
        props.setProperty("mary", "HR");
        
        UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl(props));
        
        pds = TestUtil.setupPoolingDataSource();
    }
    
    @After
    public void teardown() {
        UserGroupCallbackManager.resetCallback(); 
        
        pds.close();
    }

	@Test
	public void testDummy() {
	}
	

	
	@Test
	@Ignore
	public void testSingletonSessionMemory() throws Exception {
		for (int i = 0; i < 1000; i++) {
		    SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
	        environment.addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
	        
	        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);  
	        org.kie.runtime.manager.Runtime runtime = manager.getRuntime(EmptyContext.get());
	        manager.disposeRuntime(runtime);
			manager.close();
			System.gc();
			Thread.sleep(100);
			System.gc();
			System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		}
	}
	
	@Test
	public void testSingletonSession() throws Exception {
	    SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
        long startTimeStamp = System.currentTimeMillis();
        long maxEndTime = startTimeStamp + maxWaitTime;
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);  
		completedStart = 0;
		for (int i=0; i<nbThreadsProcess; i++) {
			new Thread(new StartProcessRunnable(manager, i)).start();
		}
		completedTask = 0;
		for (int i=0; i<nbThreadsTask; i++) {
			new Thread(new CompleteTaskRunnable(manager, i)).start();
		}
		while (completedStart < nbThreadsProcess || completedTask < nbThreadsTask) {
			Thread.sleep(100);
			if (System.currentTimeMillis() > maxEndTime) {
			    fail("Failure, did not finish in time most likely hanging");
			}
		}
		Thread.sleep(1000);
	      //make sure all process instance were completed
        JPAProcessInstanceDbLog.setEnvironment(environment.getEnvironment());
        //active
        List<ProcessInstanceLog> logs = JPAProcessInstanceDbLog.findActiveProcessInstances("com.sample.bpmn.hello");
        assertNotNull(logs);
        assertEquals(0, logs.size());
        
        // completed
        logs = JPAProcessInstanceDbLog.findProcessInstances("com.sample.bpmn.hello");
        assertNotNull(logs);
        assertEquals(nbThreadsProcess*nbInvocations, logs.size());
        manager.close();
		System.out.println("Done");
	}
	
	@Test
	public void testNewSession() throws Exception {
	    SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
        long startTimeStamp = System.currentTimeMillis();
        long maxEndTime = startTimeStamp + maxWaitTime;
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
		completedStart = 0;
		for (int i=0; i<nbThreadsProcess; i++) {
			new StartProcessRunnable(manager, i).run();
		}
		completedTask = 0;
		for (int i=0; i<nbThreadsTask; i++) {
			new Thread(new CompleteTaskRunnable(manager, i)).start();
		}
		while (completedStart < nbThreadsProcess || completedTask < nbThreadsTask) {
			Thread.sleep(100);
	         if (System.currentTimeMillis() > maxEndTime) {
                fail("Failure, did not finish in time most likely hanging");
             }
		}
		//make sure all process instance were completed
		JPAProcessInstanceDbLog.setEnvironment(environment.getEnvironment());
		//active
		List<ProcessInstanceLog> logs = JPAProcessInstanceDbLog.findActiveProcessInstances("com.sample.bpmn.hello");
		assertNotNull(logs);
		assertEquals(0, logs.size());
		
		// completed
		logs = JPAProcessInstanceDbLog.findProcessInstances("com.sample.bpmn.hello");
        assertNotNull(logs);
        assertEquals(nbThreadsProcess*nbInvocations, logs.size());
		manager.close();
		System.out.println("Done");
	}
	
    @Test
    public void testSessionPerProcessInstance() throws Exception {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
        long startTimeStamp = System.currentTimeMillis();
        long maxEndTime = startTimeStamp + maxWaitTime;
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        completedStart = 0;
        for (int i=0; i<nbThreadsProcess; i++) {
            new StartProcessPerProcessInstanceRunnable(manager, i).run();
        }
        completedTask = 0;
        for (int i=0; i<nbThreadsTask; i++) {
            new Thread(new CompleteTaskPerProcessInstanceRunnable(manager, i)).start();
        }
        while (completedStart < nbThreadsProcess || completedTask < nbThreadsTask) {
            Thread.sleep(100);
            if (System.currentTimeMillis() > maxEndTime) {
                fail("Failure, did not finish in time most likely hanging");
            }
        }
        //make sure all process instance were completed
        JPAProcessInstanceDbLog.setEnvironment(environment.getEnvironment());
        //active
        List<ProcessInstanceLog> logs = JPAProcessInstanceDbLog.findActiveProcessInstances("com.sample.bpmn.hello");
        assertNotNull(logs);
        assertEquals(0, logs.size());
        
        // completed
        logs = JPAProcessInstanceDbLog.findProcessInstances("com.sample.bpmn.hello");
        assertNotNull(logs);
        assertEquals(nbThreadsProcess*nbInvocations, logs.size());
        
        manager.close();
        System.out.println("Done");
    }	
	
	@Test
	public void testNewSessionFail() throws Exception {
	    SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
		UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		ut.begin();
		org.kie.runtime.manager.Runtime<TaskService> runtime = manager.getRuntime(EmptyContext.get());
		ProcessInstance processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello", null);
		System.out.println("Started process instance " + processInstance.getId());
		long workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
		long taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
		runtime.getTaskService().claim(taskId, "mary");
		ut.rollback();
		System.out.println("Rolled back");
		// TODO: whenever transaction fails, do we need to dispose? can we?
		// sessionManager.dispose();

		List<Status> statusses = new ArrayList<Status>();
		statusses.add(Status.Reserved);

		runtime = manager.getRuntime(EmptyContext.get());
		assertNull(runtime.getKieSession().getProcessInstance(processInstance.getId()));
		List<TaskSummary> tasks = runtime.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(0, tasks.size());
		
		ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		ut.begin();
		processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello", null);
		workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
		taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
		runtime.getTaskService().claim(taskId, "mary");
		System.out.println("Started process instance " + processInstance.getId());
		ut.commit();

		assertNotNull(runtime.getKieSession().getProcessInstance(processInstance.getId()));
		tasks = runtime.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());

		taskId = tasks.get(0).getId();
		ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		ut.begin();
		runtime.getTaskService().start(taskId, "mary");
		runtime.getTaskService().complete(taskId, "mary", null);
		ut.rollback();
		manager.disposeRuntime(runtime);
		
		runtime = manager.getRuntime(EmptyContext.get());
		assertNotNull(runtime.getKieSession().getProcessInstance(processInstance.getId()));
		tasks = runtime.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());

		taskId = tasks.get(0).getId();
		ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		ut.begin();
		runtime.getTaskService().start(taskId, "mary");
		runtime.getTaskService().complete(taskId, "mary", null);
		ut.commit();
		
		assertNull(runtime.getKieSession().getProcessInstance(processInstance.getId()));
		tasks = runtime.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(0, tasks.size());
		manager.disposeRuntime(runtime);
		
		manager.close();
	}
	
	@Test
	public void testNewSessionFailBefore() throws Exception {
		SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.addAsset(ResourceFactory.newClassPathResource("sampleFailBefore.bpmn"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        org.kie.runtime.manager.Runtime<TaskService> runtime = manager.getRuntime(EmptyContext.get());
		try{
			ProcessInstance processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello", null);
			fail("Started process instance " + processInstance.getId());
		} catch (RuntimeException e) {
			// do nothing
		}

		// TODO: whenever transaction fails, do we need to dispose? can we?
		// sessionManager.dispose();
		manager.disposeRuntime(runtime);

		List<Status> statusses = new ArrayList<Status>();
		statusses.add(Status.Reserved);

		runtime = manager.getRuntime(EmptyContext.get());
		List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("mary", "en-UK");
		assertEquals(0, tasks.size());
		
		manager.disposeRuntime(runtime);
		manager.close();
	}
	
	@Test
	public void testNewSessionFailAfter() throws Exception {
	    SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.addAsset(ResourceFactory.newClassPathResource("sampleFailAfter.bpmn"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        org.kie.runtime.manager.Runtime<TaskService> runtime = manager.getRuntime(EmptyContext.get());

		ProcessInstance processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello", null);
		long workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
		long taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
		runtime.getTaskService().claim(taskId, "mary");

		List<Status> statusses = new ArrayList<Status>();
		statusses.add(Status.Reserved);

		List<TaskSummary> tasks = runtime.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());

		taskId = tasks.get(0).getId();
		UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		try {
			ut.begin();
			runtime.getTaskService().start(taskId, "mary");
			runtime.getTaskService().complete(taskId, "mary", null);
			fail("Task completed");
		} catch (RuntimeException e) {
			// do nothing
		    e.printStackTrace();
		}
		try {
		ut.rollback();
		} catch(Exception e) {
		    
		}
		// TODO: whenever transaction fails, do we need to dispose? can we?
		// sessionManager.dispose();
		manager.disposeRuntime(runtime);

		runtime = manager.getRuntime(EmptyContext.get());
		tasks = runtime.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());
		
		manager.disposeRuntime(runtime);
		manager.close();
	}
	
	@Test
	public void testNewSessionFailAfter2() throws Exception {
	    SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.addAsset(ResourceFactory.newClassPathResource("sampleFailAfter.bpmn"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        org.kie.runtime.manager.Runtime<TaskService> runtime = manager.getRuntime(EmptyContext.get());

		ProcessInstance processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello", null);
		long workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
		long taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
		runtime.getTaskService().claim(taskId, "mary");
		runtime.getTaskService().start(taskId, "mary");

		List<Status> statusses = new ArrayList<Status>();
		statusses.add(Status.InProgress);

		List<TaskSummary> tasks = runtime.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());

		taskId = tasks.get(0).getId();
		try {
		    runtime.getTaskService().complete(taskId, "mary", null);
			fail("Task completed");
		} catch (RuntimeException e) {
			// do nothing
		}

		// TODO: whenever transaction fails, do we need to dispose? can we?
		// sessionManager.dispose();
		manager.disposeRuntime(runtime);

		runtime = manager.getRuntime(EmptyContext.get());
		tasks = runtime.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		assertEquals(1, tasks.size());
		
		manager.disposeRuntime(runtime);
        manager.close();
	}
	
	private void testStartProcess(org.kie.runtime.manager.Runtime<TaskService> runtime) throws Exception {
		
		long taskId; 
		synchronized((SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) runtime.getKieSession()).getCommandService()) {
			UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
			ut.begin();
			System.out.println("Starting process on ksession " + runtime.getKieSession().getId());
			ProcessInstance processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello", null);
			System.out.println("Started process instance " + processInstance.getId() + " on ksession " + runtime.getKieSession().getId());
			long workItemId = ((HumanTaskNodeInstance) ((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next()).getWorkItemId();
			taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
			System.out.println("Created task " + taskId);
			ut.commit();
		}
		runtime.getTaskService().claim(taskId, "mary");
		
	}
	
	public class StartProcessRunnable implements Runnable {
		private RuntimeManager manager;
		private int counter;
		public StartProcessRunnable(RuntimeManager manager, int counter) {
			this.manager = manager;
			this.counter = counter;
		}
		public void run() {
			try {
				for (int i=0; i<nbInvocations; i++) {
				    org.kie.runtime.manager.Runtime runtime = manager.getRuntime(EmptyContext.get());
//					System.out.println("Thread " + counter + " doing call " + i);
					testStartProcess(runtime);
					manager.disposeRuntime(runtime);
				}
//				System.out.println("Process thread " + counter + " completed");
				completedStart++;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	private boolean testCompleteTask(org.kie.runtime.manager.Runtime<TaskService> runtime) throws InterruptedException, Exception {
		boolean result = false;
		List<Status> statusses = new ArrayList<Status>();
		statusses.add(Status.Reserved);
		
		List<TaskSummary> tasks = null;
		tasks = runtime.getTaskService().getTasksOwned("mary", statusses, "en-UK");
		if (tasks.isEmpty()) {
			System.out.println("Task thread found no tasks");
			Thread.sleep(1000);
		} else {
			long taskId = tasks.get(0).getId();
			System.out.println("Completing task " + taskId);
			boolean success = false;
			try {
			    runtime.getTaskService().start(taskId, "mary");
				success = true;
			} catch (PermissionDeniedException e) {
				// TODO can we avoid these by doing it all in one transaction?
				System.out.println("Task thread was too late for starting task " + taskId);
			} catch (RuntimeException e) {
				if (e.getCause() instanceof OptimisticLockException || e.getCause() instanceof StaleObjectStateException) {
					System.out.println("Task thread got in conflict when starting task " + taskId);
				} else {
					throw e;
				}
			}
			if (success) {
			    runtime.getTaskService().complete(taskId, "mary", null);
				System.out.println("Completed task " + taskId);
				result = true;
			}
		}
		
		return result;
	}
	
	private boolean testCompleteTaskByProcessInstance(org.kie.runtime.manager.Runtime<TaskService> runtime, long piId) throws InterruptedException, Exception {
        boolean result = false;
        List<Status> statusses = new ArrayList<Status>();
        statusses.add(Status.Reserved);
        
        List<TaskSummary> tasks = null;
        tasks = runtime.getTaskService().getTasksByStatusByProcessId(piId, statusses, "en-UK");
        if (tasks.isEmpty()) {
            System.out.println("Task thread found no tasks");
            Thread.sleep(1000);
        } else {
            long taskId = tasks.get(0).getId();
            System.out.println("Completing task " + taskId);
            boolean success = false;
            try {
                runtime.getTaskService().start(taskId, "mary");
                success = true;
            } catch (PermissionDeniedException e) {
                // TODO can we avoid these by doing it all in one transaction?
                System.out.println("Task thread was too late for starting task " + taskId);
            } catch (RuntimeException e) {
                if (e.getCause() instanceof OptimisticLockException || e.getCause() instanceof StaleObjectStateException) {
                    System.out.println("Task thread got in conflict when starting task " + taskId);
                } else {
                    throw e;
                }
            }
            if (success) {
                runtime.getTaskService().complete(taskId, "mary", null);
                System.out.println("Completed task " + taskId);
                result = true;
   
            }
        }
        
        return result;
    }

	public class CompleteTaskRunnable implements Runnable {
		private RuntimeManager manager;
		private int counter;
		public CompleteTaskRunnable(RuntimeManager manager, int counter) {
			this.manager = manager;
			this.counter = counter;
		}
		public void run() {
			try {
				int i = 0;
				while (i < nbInvocations) {
				    org.kie.runtime.manager.Runtime runtime = manager.getRuntime(EmptyContext.get());
					boolean success = testCompleteTask(runtime);
					manager.disposeRuntime(runtime);
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
	
    public class StartProcessPerProcessInstanceRunnable implements Runnable {
        private RuntimeManager manager;
        private int counter;
        public StartProcessPerProcessInstanceRunnable(RuntimeManager manager, int counter) {
            this.manager = manager;
            this.counter = counter;
        }
        public void run() {
            try {
                for (int i=0; i<nbInvocations; i++) {
                    org.kie.runtime.manager.Runtime runtime = manager.getRuntime(ProcessInstanceIdContext.get());
//                  System.out.println("Thread " + counter + " doing call " + i);
                    testStartProcess(runtime);
                    manager.disposeRuntime(runtime);
                }
//              System.out.println("Process thread " + counter + " completed");
                completedStart++;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
	
   public class CompleteTaskPerProcessInstanceRunnable implements Runnable {
        private RuntimeManager manager;
        private int counter;
        public CompleteTaskPerProcessInstanceRunnable(RuntimeManager manager, int counter) {
            this.manager = manager;
            this.counter = counter;
        }
        public void run() {
            try {
                int i = 0;
                while (i < nbInvocations) {

                    long processInstanceId = (nbInvocations *counter)+1 + i;
//                    System.out.println("pi id " + processInstanceId + " counter " + counter);
                    org.kie.runtime.manager.Runtime runtime = manager.getRuntime(ProcessInstanceIdContext.get(processInstanceId));
                    boolean success = false;
                    
                    success = testCompleteTaskByProcessInstance(runtime, processInstanceId);
                    
                    manager.disposeRuntime(runtime);
                    if (success) {
                        i++;
                    }
                }
                completedTask++;
//	              System.out.println("Task thread " + counter + " completed");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
