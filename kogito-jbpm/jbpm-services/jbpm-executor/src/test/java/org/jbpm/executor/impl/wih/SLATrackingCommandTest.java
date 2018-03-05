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

package org.jbpm.executor.impl.wih;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.jbpm.test.util.PoolingDataSource;
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
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;

public class SLATrackingCommandTest extends AbstractExecutorBaseTest {

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

    @Test
    public void testSLATrackingOnProcessInstance() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTaskWithSLA.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("SLATimerMode", "false")
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);  
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        JPAAuditLogService logService = new JPAAuditLogService(emf);
                
        assertProcessInstanceSLACompliance(logService, processInstance.getId(), ProcessInstance.SLA_PENDING);
        
        scheduleSLATracking(manager.getIdentifier());
        countDownListener.waitTillCompleted();
       
        assertProcessInstanceSLACompliance(logService, processInstance.getId(), ProcessInstance.SLA_PENDING);
        // wait for due date of SLA to pass
        Thread.sleep(3000);
        
        countDownListener.reset(1);
        scheduleSLATracking(manager.getIdentifier());
        countDownListener.waitTillCompleted();
       
        assertProcessInstanceSLACompliance(logService, processInstance.getId(), ProcessInstance.SLA_VIOLATED);
    }
    
    @Test
    public void testSLATrackingOnUserTask() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTaskWithSLAOnTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("SLATimerMode", "false")
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);  
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        JPAAuditLogService logService = new JPAAuditLogService(emf);
                
        assertNodeInstanceSLACompliance(logService, processInstance.getId(), "Hello", ProcessInstance.SLA_PENDING);
        
        scheduleSLATracking(manager.getIdentifier());
        countDownListener.waitTillCompleted();
       
        assertNodeInstanceSLACompliance(logService, processInstance.getId(), "Hello", ProcessInstance.SLA_PENDING);
        // wait for due date of SLA to pass
        Thread.sleep(3000);
        
        countDownListener.reset(1);
        scheduleSLATracking(manager.getIdentifier());
        countDownListener.waitTillCompleted();
        
        runtime.getTaskService().start(tasks.get(0).getId(), "john");
        runtime.getTaskService().complete(tasks.get(0).getId(), "john", null);
              
        assertNodeInstanceSLACompliance(logService, processInstance.getId(), "Hello", ProcessInstance.SLA_VIOLATED);
    }
    
    @Test
    public void testSLATrackingOnProcessInstanceSLAMet() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTaskWithSLA.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("SLATimerMode", "false")
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);  
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        JPAAuditLogService logService = new JPAAuditLogService(emf);
                
        assertProcessInstanceSLACompliance(logService, processInstance.getId(), ProcessInstance.SLA_PENDING);
        
        scheduleSLATracking(manager.getIdentifier());
        countDownListener.waitTillCompleted();
       
        assertProcessInstanceSLACompliance(logService, processInstance.getId(), ProcessInstance.SLA_PENDING);

        runtime.getTaskService().start(tasks.get(0).getId(), "john");
        runtime.getTaskService().complete(tasks.get(0).getId(), "john", null);
       
        assertProcessInstanceSLACompliance(logService, processInstance.getId(), ProcessInstance.SLA_MET);
    }
    
    @Test
    public void testSLATrackingOnUserTaskSLAMet() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTaskWithSLAOnTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("SLATimerMode", "false")
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);  
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        JPAAuditLogService logService = new JPAAuditLogService(emf);
                
        assertNodeInstanceSLACompliance(logService, processInstance.getId(), "Hello", ProcessInstance.SLA_PENDING);
        
        scheduleSLATracking(manager.getIdentifier());
        countDownListener.waitTillCompleted();
       
        assertNodeInstanceSLACompliance(logService, processInstance.getId(), "Hello", ProcessInstance.SLA_PENDING);        
        
        runtime.getTaskService().start(tasks.get(0).getId(), "john");
        runtime.getTaskService().complete(tasks.get(0).getId(), "john", null);
              
        assertNodeInstanceSLACompliance(logService, processInstance.getId(), "Hello", ProcessInstance.SLA_MET);
    }
   
    
    private ExecutorService buildExecutorService() {        
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.complete");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        
        executorService.init();
        
        return executorService;
    }
    
	private void scheduleSLATracking(String identifier) {
		CommandContext commandContext = new CommandContext();
		commandContext.setData("EmfName", "org.jbpm.persistence.complete");
		commandContext.setData("SingleRun", "true");
		commandContext.setData("ForDeployment", identifier);
		executorService.scheduleRequest("org.jbpm.executor.commands.SLATrackingCommand", commandContext);
	}

	private void assertProcessInstanceSLACompliance(JPAAuditLogService logService, Long processInstanceId, int slaCompliance) {
	    List<ProcessInstanceLog> logs = logService.processInstanceLogQuery()
	            .processInstanceId(processInstanceId)
	            .build()
	            .getResultList();
	            
        assertEquals(1, logs.size());
        ProcessInstanceLog log = logs.get(0);
        assertEquals(processInstanceId, log.getProcessInstanceId());
        assertEquals(slaCompliance, ((org.jbpm.process.audit.ProcessInstanceLog)log).getSlaCompliance().intValue());
	}
	
   private void assertNodeInstanceSLACompliance(JPAAuditLogService logService, Long processInstanceId, String name, int slaCompliance) {
        List<NodeInstanceLog> logs = logService.nodeInstanceLogQuery()
                .processInstanceId(processInstanceId).and()
                .nodeName(name)                
                .build()
                .getResultList();
                        
        NodeInstanceLog log = logs.get(logs.size() - 1);
        assertEquals(processInstanceId, log.getProcessInstanceId());
        assertEquals(slaCompliance, ((org.jbpm.process.audit.NodeInstanceLog)log).getSlaCompliance().intValue());
    }
}
