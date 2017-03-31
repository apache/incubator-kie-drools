/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor.impl.wih;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionErrorStorage;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class CleanupExecutionErrorCommandWithProcessTest extends AbstractExecutorBaseTest {

    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager manager;
    private ExecutorService executorService;
    private EntityManagerFactory emf = null;
    @Before
    public void setup() {
        ExecutorTestUtil.cleanupSingletonSessionId();
        pds = ExecutorTestUtil.setupPoolingDataSource();
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        executorService = buildExecutorService();
    }
    
    @After
    public void teardown() {
        executorService.destroy();
        if (manager != null) {
            RuntimeManagerRegistry.get().remove(manager.getIdentifier());
            manager.close();
        }
        if (emf != null) {
        	emf.close();
        }
        pds.close();
    }
    
    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        
        return countDownListener;
    }

    @Test(timeout=30000)
    public void testRunProcessWithAsyncHandler() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.ThrowExceptionCommand"));
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        ExecutionErrorStorage errorStorage = ((AbstractRuntimeManager) manager).getExecutionErrorManager().getStorage();
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);  
        
        Date startDate = new Date();        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        countDownListener.waitTillCompleted();
        
        List<ExecutionError> errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        scheduleLogCleanup(startDate, "ScriptTask", String.valueOf(processInstanceId), "yyyy-MM-dd", manager.getIdentifier());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        System.out.println("Aborting process instance " + processInstance.getId());
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        // should not delete any errors as the process instance is still active
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        runtime.getKieSession().abortProcessInstance(processInstance.getId());
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        Thread.sleep(1000);
        
        scheduleLogCleanup(new Date(), "ScriptTask", String.valueOf(processInstanceId), "yyyy-MM-dd HH:mm:ss", manager.getIdentifier());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        
        errors = errorStorage.list(0, 10);
        assertEquals(0, errors.size());
 
    }     
    
    private ExecutorService buildExecutorService() {        
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.complete");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.setInterval(1);
        executorService.setRetries(0);
        executorService.init();
        
        return executorService;
    }
    
	private void scheduleLogCleanup(Date olderThan,
			String forProcess, String forProcessInstance, String dateFormat, String identifier) {
		CommandContext commandContext = new CommandContext();
		commandContext.setData("EmfName", "org.jbpm.persistence.complete");
		commandContext.setData("SingleRun", "true");
		if (olderThan != null) {
		    commandContext.setData("OlderThan", new SimpleDateFormat(dateFormat).format(olderThan));
		}
		commandContext.setData("DateFormat", dateFormat);
		commandContext.setData("ForDeployment", identifier);
		commandContext.setData("ForProcess", forProcess);
		commandContext.setData("ForProcessInstance", forProcessInstance);
		executorService.scheduleRequest("org.jbpm.executor.commands.ExecutionErrorCleanupCommand", commandContext);
	}
	
}
