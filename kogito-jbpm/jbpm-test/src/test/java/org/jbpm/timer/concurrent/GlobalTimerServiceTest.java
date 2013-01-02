package org.jbpm.timer.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.OptimisticLockException;
import javax.transaction.UserTransaction;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.SingleSessionCommandService;
import org.hibernate.StaleObjectStateException;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.impl.ThreadPoolSchedulerService;
import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.task.exception.PermissionDeniedException;
import org.jbpm.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.timer.TimerBaseTest;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.TaskSummary;

public class GlobalTimerServiceTest extends TimerBaseTest {
    
    private long maxWaitTime = 60*1000; // max wait to complete operation is set to 60 seconds to avoid build hangs
	
	private int nbThreadsProcess = 10;
	private int nbThreadsTask = 10;
	private transient int completedStart = 0;
	private transient int completedTask = 0;
	private int wait = 2;
	
	private UserGroupCallback userGroupCallback;
	
	private GlobalSchedulerService globalScheduler;

    
    @Before
    public void setup() {
        
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        
        globalScheduler = new ThreadPoolSchedulerService(1);
    }
    
    @After
    public void teardown() {       
        globalScheduler.shutdown();
        
    }
	
    @Test
    public void testSessionPerProcessInstance() throws Exception {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycleWithHT.bpmn2"), ResourceType.BPMN2);
        environment.setSchedulerService(globalScheduler);
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
        List<ProcessInstanceLog> logs = JPAProcessInstanceDbLog.findActiveProcessInstances("IntermediateCatchEvent");
        assertNotNull(logs);
        assertEquals(0, logs.size());
        
        // completed
        logs = JPAProcessInstanceDbLog.findProcessInstances("IntermediateCatchEvent");
        assertNotNull(logs);
        assertEquals(nbThreadsProcess, logs.size());
        
        manager.close();
        System.out.println("Done");
    }
    
	
	private void testStartProcess(org.kie.internal.runtime.manager.Runtime runtime) throws Exception {
		
		synchronized((SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) runtime.getKieSession()).getCommandService()) {
			UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
			ut.begin();
			System.out.println("Starting process on ksession " + runtime.getKieSession().getId());
			Map<String, Object> params = new HashMap<String, Object>();
			DateTime now = new DateTime();
		    now.plus(1000);

			params.put("x", "R" + wait + "/PT1S");
			ProcessInstance processInstance = runtime.getKieSession().startProcess("IntermediateCatchEvent", params);
			System.out.println("Started process instance " + processInstance.getId() + " on ksession " + runtime.getKieSession().getId());			
			ut.commit();
		}
		
		
	}

	
	private boolean testCompleteTaskByProcessInstance(org.kie.internal.runtime.manager.Runtime runtime, long piId) throws InterruptedException, Exception {
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
                runtime.getTaskService().start(taskId, "john");
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
                runtime.getTaskService().complete(taskId, "john", null);
                System.out.println("Completed task " + taskId);
                result = true;
   
            }
        }
        
        return result;
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
                org.kie.internal.runtime.manager.Runtime runtime = manager.getRuntime(ProcessInstanceIdContext.get());
                testStartProcess(runtime);                    
                manager.disposeRuntime(runtime);                    
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
                // wait for amount of time timer expires and plus 1s initially
                Thread.sleep(wait * 1000 + 1000);
                long processInstanceId = counter+1;
                org.kie.internal.runtime.manager.Runtime runtime = manager.getRuntime(ProcessInstanceIdContext.get(processInstanceId));

                for (int y = 0; y<wait; y++) {
                    testCompleteTaskByProcessInstance(runtime, processInstanceId);
                }
                manager.disposeRuntime(runtime);

                
                
                completedTask++;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
