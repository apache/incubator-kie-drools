package org.jbpm.runtime.manager.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.UserGroupCallback;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class ConcurrentOperationsTest extends AbstractBaseTest {
    
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager manager;
    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
    }
    
    @After
    public void teardown() {
        if (manager != null) {
            manager.close();
        }
        pds.close();
    }

  
    
    @Test
    public void testExecuteProcessWithAsyncHandler() throws Exception {
    	
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addEnvironmentEntry("TRANSACTION_LOCK_ENABLED", true)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

					@Override
					public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
						Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
						handlers.put("Log", new AsyncWorkItemHandler(runtime.getKieSession()));
						return handlers;
					}
                	
                })
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CustomTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        ProcessInstance processInstance = ksession.startProcess("customtask");
        System.out.println("Started process, committing...");
        ut.commit();
        
        Thread.sleep(2000);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
               
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    @Test
    public void testExecuteHumanTaskWithAsyncHandler() throws Exception {
    	
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback) 
                .addEnvironmentEntry("TRANSACTION_LOCK_ENABLED", true)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

					@Override
					public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
						Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
						handlers.put("Log", new AsyncWorkItemHandler(runtime.getKieSession()));
						return handlers;
					}
                	
                })
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CustomAndHumanTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        
        ProcessInstance processInstance = ksession.startProcess("customtask");
        System.out.println("Started process, committing...");
        
        TaskService taskService = runtime.getTaskService();
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "john");
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();        
        taskService.complete(taskId, "john", null);
        System.out.println("Task completed, committing...");
        ut.commit();
        Thread.sleep(2000);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
               
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    private static class AsyncWorkItemHandler implements WorkItemHandler {
    	
    	private KieSession ksession;
    	
    	AsyncWorkItemHandler(KieSession ksession) {
    		this.ksession = ksession;
    	}

		@Override
		public void executeWorkItem(final WorkItem workItem, WorkItemManager manager) {
			
			new Thread() {

				@Override
				public void run() {
					System.out.println("staring a thread....");
					ksession.insert("doing it async");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						
					}
					System.out.println("Completing the work item");
					ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
				}
				
			}.start();
			
		}

		@Override
		public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

		}
    	
    }
}
