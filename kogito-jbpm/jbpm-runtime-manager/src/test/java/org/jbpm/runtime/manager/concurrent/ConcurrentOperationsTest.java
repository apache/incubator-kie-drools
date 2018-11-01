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

package org.jbpm.runtime.manager.concurrent;

import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.event.process.ProcessEventListener;
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

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

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

  
    
    @Test(timeout=10000)
    public void testExecuteProcessWithAsyncHandler() throws Exception {
    	final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Log", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addEnvironmentEntry("TRANSACTION_LOCK_ENABLED", true)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

					@Override
					public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
						Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
						handlers.put("Log", new AsyncWorkItemHandler(((RuntimeEngineImpl)runtime).getManager()));
						return handlers;
					}

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {

                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                	
                })
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CustomTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        long sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        ProcessInstance processInstance = ksession.startProcess("customtask");
        logger.debug("Started process, committing...");
        ut.commit();
        
        countDownListener.waitTillCompleted();
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
               
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    @Ignore("unstable")
    @Test(timeout=10000)
    public void testExecuteHumanTaskWithAsyncHandler() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Log", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback) 
                .addEnvironmentEntry("TRANSACTION_LOCK_ENABLED", true)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

					@Override
					public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
						Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
						handlers.put("Log", new AsyncWorkItemHandler(((RuntimeEngineImpl)runtime).getManager()));
						return handlers;
					}
                	
					@Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {

                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CustomAndHumanTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        long sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        
        ProcessInstance processInstance = ksession.startProcess("customandhumantask");
        logger.debug("Started process, committing...");
        
        TaskService taskService = runtime.getTaskService();
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "john");
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();        
        taskService.complete(taskId, "john", null);
        logger.debug("Task completed, committing...");
        ut.commit();
        ksession.fireAllRules();
        countDownListener.waitTillCompleted();
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
               
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    private class AsyncWorkItemHandler implements WorkItemHandler {
    	
    	private RuntimeManager runtimeManager;
    	
    	AsyncWorkItemHandler(RuntimeManager runtimeManager) {
    		this.runtimeManager = runtimeManager;
    	}

		@Override
		public void executeWorkItem(final WorkItem workItem, WorkItemManager manager) {
			
			new Thread() {

				@Override
				public void run() {
				    
					try {
					    Thread.sleep(1000);
					    
    					RuntimeEngine engine = runtimeManager.getRuntimeEngine(EmptyContext.get());// only for singleton
                        logger.debug("staring a thread....");
                        engine.getKieSession().insert("doing it async");
    					logger.debug("Completing the work item");
    					
    					engine.getKieSession().getWorkItemManager().completeWorkItem(workItem.getId(), null);
    					runtimeManager.disposeRuntimeEngine(engine);
					} catch (Exception e) {
					    logger.error("Error when executing async operation", e);
					}
				}
				
			}.start();
			
		}

		@Override
		public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

		}
    	
    }
}
