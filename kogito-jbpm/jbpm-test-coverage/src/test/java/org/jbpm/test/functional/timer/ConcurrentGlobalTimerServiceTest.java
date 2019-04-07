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

package org.jbpm.test.functional.timer;

import org.drools.core.command.SingleSessionCommandService;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.hibernate.StaleObjectStateException;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.impl.ThreadPoolSchedulerService;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

public class ConcurrentGlobalTimerServiceTest extends TimerBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentGlobalTimerServiceTest.class);
    
    private long maxWaitTime = 60*1000; // max wait to complete operation is set to 60 seconds to avoid build hangs
	
	private int nbThreadsProcess = 10;
	private int nbThreadsTask = 10;
	private transient int completedStart = 0;
	private transient int completedTask = 0;
	private int wait = 2;
	
	private UserGroupCallback userGroupCallback;
	
	private GlobalSchedulerService globalScheduler;

	private RuntimeManager manager;
	
	private EntityManagerFactory emf;
    
    @Before
    public void setup() {
        
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        
        globalScheduler = new ThreadPoolSchedulerService(1);
        
        emf = Persistence.createEntityManagerFactory("org.jbpm.test.persistence");
    }
    
    @After
    public void teardown() {       
        globalScheduler.shutdown();
        if (manager != null) {
            manager.close();
        }
        emf.close();
    }
	
    @Test
    public void testSessionPerProcessInstance() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
    			.entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/IntermediateCatchEventTimerCycleWithHT.bpmn2"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .get();

        long startTimeStamp = System.currentTimeMillis();
        long maxEndTime = startTimeStamp + maxWaitTime;
        
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        // prepare task service with users and groups
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = engine.getTaskService();
        
        Group grouphr = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) grouphr).setId("HR");
        
        User mary = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) mary).setId("mary");
        User john = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) john).setId("john");
        
        ((InternalTaskService)taskService).addGroup(grouphr);
        ((InternalTaskService)taskService).addUser(mary);
        ((InternalTaskService)taskService).addUser(john);
        
        manager.disposeRuntimeEngine(engine);
 
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
        engine = manager.getRuntimeEngine(EmptyContext.get());
        AuditService logService = engine.getAuditService();
        //active
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("IntermediateCatchEvent");
        assertNotNull(logs);
        for (ProcessInstanceLog log : logs) {
            logger.debug("Left over {}", log.getProcessInstanceId());
        }
        assertEquals(0, logs.size());
        
        // completed
        logs = logService.findProcessInstances("IntermediateCatchEvent");
        assertNotNull(logs);
        assertEquals(nbThreadsProcess, logs.size());
        manager.disposeRuntimeEngine(engine);
        
        logger.debug("Done");
    }
    
	
	private void testStartProcess(RuntimeEngine runtime) throws Exception {
		
		synchronized((SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) runtime.getKieSession()).getRunner()) {
			UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
            try {
                ut.begin();
                logger.debug("Starting process on ksession {}", runtime.getKieSession().getIdentifier());
                Map<String, Object> params = new HashMap<String, Object>();

                params.put("x", "R2/PT1S");
                ProcessInstance processInstance = runtime.getKieSession().startProcess("IntermediateCatchEvent", params);
                logger.debug("Started process instance {} on ksession {}", processInstance.getId(), runtime.getKieSession().getIdentifier());
                ut.commit();
            } catch (Exception ex) {
                ut.rollback();
                throw ex;
            }
		}
		
		
	}

	
	private boolean testCompleteTaskByProcessInstance(RuntimeManager manager, RuntimeEngine runtime, long piId) throws InterruptedException, Exception {
        boolean result = false;
        List<Status> statusses = new ArrayList<Status>();
        statusses.add(Status.Reserved);
        
        List<TaskSummary> tasks = null;
        tasks = runtime.getTaskService().getTasksByStatusByProcessInstanceId(piId, statusses, "en-UK");
        if (tasks.isEmpty()) {
            logger.debug("Task thread found no tasks for piId {}" + piId);
            Thread.sleep(1000);
        } else {
            long taskId = tasks.get(0).getId();
            logger.debug("Completing task {} piId {}", taskId, piId);
            boolean success = false;
            try {
                runtime.getTaskService().start(taskId, "john");
                success = true;
                
                if (success) {
                    runtime.getTaskService().complete(taskId, "john", null);
                    logger.debug("Completed task {} piID {}", taskId, piId);
                    result = true;
       
                }
            } catch (PermissionDeniedException e) {
                // TODO can we avoid these by doing it all in one transaction?
                logger.debug("Task thread was too late for starting task {} piId {}", taskId, piId);
            } catch (Throwable e) {     
                throw new RuntimeException(e);
      
            }

        }
        
        return result;
    }
	
	   private boolean testRetryCompleteTaskByProcessInstance(RuntimeManager manager, RuntimeEngine runtime, long piId) throws InterruptedException, Exception {
	        boolean result = false;
	        List<Status> statusses = new ArrayList<Status>();
	        statusses.add(Status.InProgress);
	        
	        List<TaskSummary> tasks = null;
	        tasks = runtime.getTaskService().getTasksByStatusByProcessInstanceId(piId, statusses, "en-UK");
	        if (tasks.isEmpty()) {
	            logger.debug("Retry : Task thread found no tasks for piId {}", piId);
	            Thread.sleep(1000);
	        } else {
	            long taskId = tasks.get(0).getId();
	            logger.debug("Retry : Completing task {} piId {}", taskId, piId);
	            try {
	                
                    runtime.getTaskService().complete(taskId, "john", null);
                    logger.debug("Retry : Completed task {} piId {}", taskId, piId);
                    result = true;
       
	                
	            } catch (PermissionDeniedException e) {
	                // TODO can we avoid these by doing it all in one transaction?
	                logger.debug("Task thread was too late for starting task {} piId {}", taskId, piId);
	            } catch (Exception e) {
	                throw e;
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
                RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
                testStartProcess(runtime);                    
                manager.disposeRuntimeEngine(runtime);                    
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
                

                for (int y = 0; y<wait; y++) {
                	RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
                	try {
                        testCompleteTaskByProcessInstance(manager, runtime, processInstanceId);
                    } catch (Throwable e) {
                        if (checkOptimiticLockException(e)) {
                            logger.debug("{} retrying for process instance {}", counter, processInstanceId);
                            manager.disposeRuntimeEngine(runtime);
                            runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
                            testRetryCompleteTaskByProcessInstance(manager, runtime, processInstanceId);
                        } else {
                            throw e;
                        }
                    }
                	manager.disposeRuntimeEngine(runtime);
                }
                

                completedTask++;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
   
   public static boolean checkOptimiticLockException(Throwable e) {
       Throwable rootCause = e.getCause();
       while (rootCause != null) {
           if ((rootCause instanceof OptimisticLockException || rootCause instanceof StaleObjectStateException) ){               
               return true;
           }
           
           rootCause = rootCause.getCause();
       }
       
       if (e instanceof InvocationTargetException) {
           return checkOptimiticLockException(((InvocationTargetException) e).getTargetException());
       }
       
       return false;
   }
}
