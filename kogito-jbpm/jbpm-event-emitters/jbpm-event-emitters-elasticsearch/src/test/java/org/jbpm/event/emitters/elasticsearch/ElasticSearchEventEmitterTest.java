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

package org.jbpm.event.emitters.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.persistence.api.integration.InstanceView;
import org.jbpm.persistence.api.integration.model.CaseInstanceView;
import org.jbpm.persistence.api.integration.model.ProcessInstanceView;
import org.jbpm.persistence.api.integration.model.TaskInstanceView;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.CaseData;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTaskData;
import org.mockito.Mockito;

public class ElasticSearchEventEmitterTest {
    
    private static String dateFormatStr;
    
    private static Server server;
    
    private static List<String> responseCollector = new ArrayList<>();
    
    @BeforeClass
    public static void initialize() throws Exception {
        dateFormatStr = "yyyy-MM-dd";
        System.setProperty("org.jbpm.event.emitters.elasticsearch.date_format", dateFormatStr);
        FakeElasticSearchRESTApplication application = new FakeElasticSearchRESTApplication(responseCollector);
        RuntimeDelegate delegate = RuntimeDelegate.getInstance();

        JAXRSServerFactoryBean bean = delegate.createEndpoint(application,
                                                              JAXRSServerFactoryBean.class);        
        String url = "http://localhost:9998" + bean.getAddress();
        bean.setAddress(url);
        server = bean.create();
        server.start();
        
        System.setProperty("org.jbpm.event.emitters.elasticsearch.url", url);
    }

    @AfterClass
    public static void destroy() throws Exception {
        if (server != null) {
            server.stop();
            server.destroy();
        }
        
        System.clearProperty("org.jbpm.event.emitters.elasticsearch.url");
    }
    
    @Before
    public void setup() {
        responseCollector.clear();
    }
    
    @Test
    public void testProcessInstanceThroughEmitter() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
        // sample date must match one set in the expected result file
        Date sampleDate = sdf.parse("2018-10-23");
        
        String expectedResult = read(this.getClass().getResourceAsStream("/testProcessInstanceThroughEmitter.json"));
        
        WorkflowProcessInstanceImpl processInstance = Mockito.mock(WorkflowProcessInstanceImpl.class);
        WorkflowProcessImpl process = Mockito.mock(WorkflowProcessImpl.class);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", "john");
        variables.put("variable", 123);
        
        when(process.isDynamic()).thenReturn(false);
        when(process.getVersion()).thenReturn("1.0");
        
        when(processInstance.getProcess()).thenReturn(process);
        when(processInstance.getId()).thenReturn(99L);
        when(processInstance.getDeploymentId()).thenReturn("test");
        when(processInstance.getCorrelationKey()).thenReturn("key");
        when(processInstance.getParentProcessInstanceId()).thenReturn(-1L);
        when(processInstance.getProcessId()).thenReturn("myprocess");
        when(processInstance.getDescription()).thenReturn("");
        when(processInstance.getProcessName()).thenReturn("MyProcess");
        when(processInstance.getState()).thenReturn(1);
        when(processInstance.getVariables()).thenReturn(variables);
        
        List<InstanceView<?>> views = new ArrayList<>();
        
        ProcessInstanceView instanceView = new ProcessInstanceView(processInstance);
        instanceView.copyFromSource();
        // override date so it's reliable in comparison
        instanceView.setDate(sampleDate);
        
        views.add(instanceView);
        // use latch to wait for async processing of the emitter
        CountDownLatch latch = new CountDownLatch(1);
        ElasticSearchEventEmitter emitter = new ElasticSearchEventEmitter() {

            @Override
            protected ExecutorService buildExecutorService() {
                return createExecutor(latch);
            }
            
        };
        emitter.apply(views);        
        latch.await(5, TimeUnit.SECONDS);
        
        // always close emitter to clean resources
        emitter.close();
        
        assertThat(responseCollector).hasSize(1);
        assertThat(responseCollector.get(0)).isEqualTo(expectedResult);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testCaseInstanceThroughEmitter() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
        // sample date must match one set in the expected result file
        Date sampleDate = sdf.parse("2018-10-23");
        
        String expectedResult = read(this.getClass().getResourceAsStream("/testCaseInstanceThroughEmitter.json"));
        
        WorkflowProcessInstanceImpl processInstance = Mockito.mock(WorkflowProcessInstanceImpl.class);
        WorkflowProcessImpl process = Mockito.mock(WorkflowProcessImpl.class);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", "john");
        variables.put("variable", 123);
        
        when(process.isDynamic()).thenReturn(false);
        when(process.getVersion()).thenReturn("1.0");
        
        CaseData caseData = Mockito.mock(CaseData.class, Mockito.withSettings().extraInterfaces(CaseAssignment.class));
        Map<String, Object> caseVariables = new HashMap<>();
        caseVariables.put("caseDetail", "my test case");
        caseVariables.put("age", 55);
        when(caseData.getData()).thenReturn(caseVariables);
        
        Collection objects = new ArrayList<>();
        objects.add(caseData);
        
        InternalKnowledgeRuntime kruntime = Mockito.mock(InternalKnowledgeRuntime.class);
        when(kruntime.getObjects(any())).thenReturn(objects);
        
        when(processInstance.getProcess()).thenReturn(process);
        when(processInstance.getId()).thenReturn(99L);
        when(processInstance.getDeploymentId()).thenReturn("test");
        when(processInstance.getCorrelationKey()).thenReturn("key");
        when(processInstance.getParentProcessInstanceId()).thenReturn(-1L);
        when(processInstance.getProcessId()).thenReturn("myprocess");
        when(processInstance.getDescription()).thenReturn("");
        when(processInstance.getProcessName()).thenReturn("MyProcess");
        when(processInstance.getState()).thenReturn(1);
        when(processInstance.getVariables()).thenReturn(variables);
        when(processInstance.getKnowledgeRuntime()).thenReturn(kruntime);
        
        List<InstanceView<?>> views = new ArrayList<>();
        
        CaseInstanceView instanceView = new CaseInstanceView(processInstance);
        instanceView.copyFromSource();
        // override date so it's reliable in comparison
        instanceView.setDate(sampleDate);
        
        views.add(instanceView);
        // use latch to wait for async processing of the emitter
        CountDownLatch latch = new CountDownLatch(1);
        ElasticSearchEventEmitter emitter = new ElasticSearchEventEmitter() {

            @Override
            protected ExecutorService buildExecutorService() {
                return createExecutor(latch);
            }
            
        };
        emitter.apply(views);        
        latch.await(5, TimeUnit.SECONDS);
        
        // always close emitter to clean resources
        emitter.close();
        
        assertThat(responseCollector).hasSize(1);
        assertThat(responseCollector.get(0)).isEqualTo(expectedResult);
    }
    
    @Test
    public void testTaskInstanceThroughEmitter() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
        // sample date must match one set in the expected result file
        Date sampleDate = sdf.parse("2018-10-23");
        
        String expectedResult = read(this.getClass().getResourceAsStream("/testTaskInstanceThroughEmitter.json"));
        
        Task taskInstance = Mockito.mock(Task.class);
        InternalTaskData taskData = Mockito.mock(InternalTaskData.class);
        User user = Mockito.mock(User.class);
        InternalPeopleAssignments peopleAssignments = Mockito.mock(InternalPeopleAssignments.class);
        
        when(peopleAssignments.getBusinessAdministrators()).thenReturn(Collections.emptyList());
        when(peopleAssignments.getExcludedOwners()).thenReturn(Collections.emptyList());
        when(peopleAssignments.getPotentialOwners()).thenReturn(Collections.emptyList());
        
        when(user.getId()).thenReturn("john");
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", "john");
        variables.put("variable", 123);
        
        Map<String, Object> outputVariables = new HashMap<>();
        outputVariables.put("outcome", "good work");
        outputVariables.put("score", 55);
        
        when(taskData.getActivationTime()).thenReturn(sampleDate);
        when(taskData.getActualOwner()).thenReturn(user);
        when(taskData.getDeploymentId()).thenReturn("test");
        when(taskData.getCreatedBy()).thenReturn(user);
        when(taskData.getCreatedOn()).thenReturn(sampleDate);
        when(taskData.getExpirationTime()).thenReturn(sampleDate);
        when(taskData.getTaskInputVariables()).thenReturn(variables);
        when(taskData.getTaskOutputVariables()).thenReturn(outputVariables);
        when(taskData.getParentId()).thenReturn(-1L);        
        when(taskData.getProcessId()).thenReturn("process");
        when(taskData.getProcessInstanceId()).thenReturn(99L);
        when(taskData.isSkipable()).thenReturn(true);
        when(taskData.getStatus()).thenReturn(Status.Reserved);
        when(taskData.getWorkItemId()).thenReturn(100L);        
        
        when(taskInstance.getId()).thenReturn(44L);
        when(taskInstance.getPeopleAssignments()).thenReturn(peopleAssignments);
        when(taskInstance.getDescription()).thenReturn("simple task");
        when(taskInstance.getFormName()).thenReturn("simpletask");
        when(taskInstance.getName()).thenReturn("Simple Task");
        when(taskInstance.getSubject()).thenReturn("empty");
        when(taskInstance.getPriority()).thenReturn(5);
        when(taskInstance.getTaskType()).thenReturn("");        
        when(taskInstance.getTaskData()).thenReturn(taskData);
        
        List<InstanceView<?>> views = new ArrayList<>();
        
        TaskInstanceView instanceView = new TaskInstanceView(taskInstance);
        instanceView.copyFromSource();
        
        views.add(instanceView);
        // use latch to wait for async processing of the emitter
        CountDownLatch latch = new CountDownLatch(1);
        ElasticSearchEventEmitter emitter = new ElasticSearchEventEmitter() {

            @Override
            protected ExecutorService buildExecutorService() {
                return createExecutor(latch);
            }
            
        };
        emitter.apply(views);        
        latch.await(5, TimeUnit.SECONDS);
        
        // always close emitter to clean resources
        emitter.close();
        
        assertThat(responseCollector).hasSize(1);        
        assertThat(responseCollector.get(0)).isEqualTo(expectedResult);
    }
    
    protected String read(InputStream input) {
        String lineSeparator = System.getProperty("line.separator");

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")))) {
            return buffer.lines().collect(Collectors.joining(lineSeparator)) + lineSeparator;
        } catch (Exception e) {
            return null;
        }
    }
    
    protected NotifyingThreadPoolExecutor createExecutor(CountDownLatch latch) {
        NotifyingThreadPoolExecutor executor = new NotifyingThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        executor.setLatch(latch);
        return executor;
    }
}
