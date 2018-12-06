/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.executor.impl.wih;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.test.util.db.PoolingDataSourceWrapper;

@RunWith(Parameterized.class)
public class ErrorHandlingTest extends AbstractExecutorBaseTest {

  
    private PoolingDataSourceWrapper pds;
    private UserGroupCallback userGroupCallback;
    private RuntimeManager manager;
    private ExecutorService executorService;
    private EntityManagerFactory emf = null;

    @Parameters(name = "AsyncMode : {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 {"true"}, 
                 {"false"}
           });
    }
    
    private String asyncMode;
    
    public ErrorHandlingTest(String asyncMode) {
        this.asyncMode = asyncMode;
    }
    
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

   
    @Test(timeout=10000)
    public void testAsyncModeWithScriptTask() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Catch exception", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("ErrorHandlerOfFailingScript.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", asyncMode)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();

        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);

        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("exceptionType", "uncheckedException");
        parameters.put("executeAct3", true);
        ProcessInstance processInstance = ksession.startProcess("SCRA_process", parameters);        
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        waitForAllJobsToComplete();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);
        

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        
        Set<String> uniqueNodeNames = logs.stream().map(l -> l.getNodeName()).collect(Collectors.toSet());
        assertFalse("Node 'Path Alternative' shouldn't be invoked", uniqueNodeNames.contains("Path Alternative"));
    }


    private boolean waitForAllJobsToComplete() throws Exception {
        int attempts = 10;
        do {
            List<RequestInfo> running = executorService.getRunningRequests(new QueryContext());
            attempts--;
            
            if (running.isEmpty()) {
                return true;
            }
            
            Thread.sleep(500);
            
        } while (attempts > 0);
        
        return false;
    }

    private ExecutorService buildExecutorService() {
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.init();

        return executorService;
    }
}
