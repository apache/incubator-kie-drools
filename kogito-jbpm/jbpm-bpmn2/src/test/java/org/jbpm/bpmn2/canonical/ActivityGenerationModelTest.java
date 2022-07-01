/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.bpmn2.canonical;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.util.PortablePath;
import org.drools.util.io.ClassPathResource;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.jbpm.compiler.canonical.UserTaskModelMetaData;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.StaticApplication;
import org.kie.kogito.StaticConfig;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnProcesses;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.impl.CachedWorkItemHandlerConfig;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.services.identity.StaticIdentityProvider;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ABORTED;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_COMPLETED;

public class ActivityGenerationModelTest extends JbpmBpmn2TestCase {

    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.loadCompiler(JavaConfiguration.CompilerType.NATIVE, "1.8");

    @Test
    public void testMinimalProcess() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-MinimalProcess.bpmn2")).get(0);

        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("com.sample.MinimalProcess", content);

        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.emptyMap());
        ProcessInstance<BpmnVariables> processInstance = processes.get("Minimal").createInstance();

        processInstance.start();

        assertEquals(STATE_COMPLETED, processInstance.status());
    }

    @Test
    public void testProcessEmptyScript() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-ProcessEmptyScript.bpmn2")).get(0);

        assertThrows(IllegalStateException.class, () -> ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get()));

    }

    @Test
    public void testUserTaskProcessWithTaskModels() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);

        List<UserTaskModelMetaData> models = ProcessToExecModelGenerator.INSTANCE.generateUserTaskModel((WorkflowProcess) process.get());

        for (UserTaskModelMetaData metaData : models) {
            String content = metaData.generateInput();
            assertThat(content).isNotNull();
            log(content);

            content = metaData.generateOutput();
            assertThat(content).isNotNull();
            log(content);
        }
    }

    @Test
    public void testUserTaskProcess() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);

        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.UserTaskProcess", content);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();

        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.singletonMap("Human Task", workItemHandler));
        ProcessInstance<BpmnVariables> processInstance = processes.get("UserTask").createInstance();

        processInstance.start();
        assertEquals(STATE_ACTIVE, processInstance.status());

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        processInstance.completeWorkItem(workItem.getStringId(), null, SecurityPolicy.of(new StaticIdentityProvider("john")));
        assertEquals(STATE_COMPLETED, processInstance.status());
    }

    @Test
    public void testUserTaskWithParamProcess() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTaskWithParametrizedInput.bpmn2")).get(0);

        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.UserTaskProcess", content);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();

        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.singletonMap("Human Task", workItemHandler));
        ProcessInstance<BpmnVariables> processInstance = processes.get("UserTask").createInstance();

        processInstance.start();
        assertEquals(STATE_ACTIVE, processInstance.status());
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("Executing task of process instance " + processInstance.id() + " as work item with Hello",
                workItem.getParameter("Description").toString().trim());
        processInstance.completeWorkItem(workItem.getStringId(), null, SecurityPolicy.of(new StaticIdentityProvider("john")));
        assertEquals(STATE_COMPLETED, processInstance.status());
    }

    @Test
    public void testScriptMultilineExprProcess() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-CallActivitySubProcess.bpmn2")).get(0);

        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.SubProcessProcess", content);

        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.emptyMap());
        ProcessInstance<BpmnVariables> processInstance = processes.get("SubProcess").createInstance();

        processInstance.start();

        assertEquals(STATE_COMPLETED, processInstance.status());
    }

    @Test
    public void testExclusiveSplit() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-ExclusiveSplit.bpmn2")).get(0);

        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.TestProcess", content);

        SystemOutWorkItemHandler workItemHandler = new SystemOutWorkItemHandler();

        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.singletonMap("Email", workItemHandler));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "First");
        params.put("y", "Second");
        ProcessInstance<BpmnVariables> processInstance = processes.get("com.sample.test").createInstance(BpmnVariables.create(params));

        processInstance.start();

        assertEquals(STATE_COMPLETED, processInstance.status());

    }

    @Test
    public void testExclusiveSplitRetriggerAfterError() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-ExclusiveSplit.bpmn2")).get(0);

        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.TestProcess", content);

        SystemOutWorkItemHandler workItemHandler = new SystemOutWorkItemHandler();

        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.singletonMap("Email", workItemHandler));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "First1");
        params.put("y", "Second1");
        ProcessInstance<BpmnVariables> processInstance = processes.get("com.sample.test").createInstance(BpmnVariables.create(params));

        processInstance.start();

        assertEquals(KogitoProcessInstance.STATE_ERROR, processInstance.status());

        Optional<ProcessError> errorOptional = processInstance.error();
        assertThat(errorOptional).isPresent();

        ProcessError error = errorOptional.get();
        assertThat(error.failedNodeId()).isEqualTo("_2");
        assertThat(error.errorMessage()).contains("XOR split could not find at least one valid outgoing connection for split Split");

        params.put("x", "First");
        processInstance.updateVariables(BpmnVariables.create(params));

        error.retrigger();
        assertEquals(STATE_COMPLETED, processInstance.status());
    }

    @Test
    public void testInclusiveSplit() throws Exception {

        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-InclusiveSplit.bpmn2")).get(0);
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.TestProcess", content);

        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.emptyMap());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 15);
        ProcessInstance<BpmnVariables> processInstance = processes.get("com.sample.test").createInstance(BpmnVariables.create(params));

        processInstance.start();

        assertEquals(STATE_COMPLETED, processInstance.status());

    }

    @Test
    public void testInclusiveSplitDefaultConnection() throws Exception {

        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-InclusiveGatewayWithDefault.bpmn2")).get(0);
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.InclusiveGatewayWithDefaultProcess", content);

        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.emptyMap());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("test", "c");
        ProcessInstance<BpmnVariables> processInstance = processes.get("InclusiveGatewayWithDefault").createInstance(BpmnVariables.create(params));

        processInstance.start();

        assertEquals(STATE_COMPLETED, processInstance.status());

    }

    @Test
    public void testParallelGateway() throws Exception {

        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-ParallelSplit.bpmn2")).get(0);
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.TestProcess", content);

        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.emptyMap());
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance<BpmnVariables> processInstance = processes.get("com.sample.test").createInstance(BpmnVariables.create(params));

        processInstance.start();

        assertEquals(STATE_COMPLETED, processInstance.status());

    }

    @Test
    public void testInclusiveSplitAndJoinNested() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-InclusiveSplitAndJoinNested.bpmn2")).get(0);
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.TestProcess", content);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 15);
        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.singletonMap("Human Task", workItemHandler));
        ProcessInstance<BpmnVariables> processInstance = processes.get("com.sample.test").createInstance(BpmnVariables.create(params));

        processInstance.start();
        assertEquals(STATE_ACTIVE, processInstance.status());

        List<KogitoWorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(2, activeWorkItems.size());

        for (KogitoWorkItem wi : activeWorkItems) {
            processInstance.completeWorkItem(wi.getStringId(), null);
        }

        activeWorkItems = workItemHandler.getWorkItems();
        assertEquals(2, activeWorkItems.size());

        for (KogitoWorkItem wi : activeWorkItems) {
            processInstance.completeWorkItem(wi.getStringId(), null);
        }
        assertEquals(STATE_COMPLETED, processInstance.status());

    }

    @Test
    public void testInclusiveSplitAndJoinNestedWithBusinessKey() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-InclusiveSplitAndJoinNested.bpmn2")).get(0);
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.TestProcess", content);

        String businessKey = "custom";

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 15);
        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.singletonMap("Human Task", workItemHandler));
        ProcessInstance<BpmnVariables> processInstance = processes.get("com.sample.test").createInstance(businessKey, BpmnVariables.create(params));

        processInstance.start();
        assertEquals(STATE_ACTIVE, processInstance.status());

        ProcessInstance<BpmnVariables> loadedProcessInstance = processes.get("com.sample.test").instances().findById(processInstance.id()).orElse(null);
        assertThat(loadedProcessInstance).isNotNull();
        assertThat(loadedProcessInstance.businessKey()).isEqualTo(businessKey);

        loadedProcessInstance.abort();

        assertEquals(STATE_ABORTED, processInstance.status());

    }

    @Test
    public void testWorkItemProcessWithVariableMapping() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-ServiceProcess.bpmn2")).get(0);

        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.ServiceProcessProcess", content);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.singletonMap("org.jbpm.bpmn2.objects.HelloService_hello_2_Handler", workItemHandler));
        ProcessInstance<BpmnVariables> processInstance = processes.get("ServiceProcess").createInstance(BpmnVariables.create(params));

        processInstance.start();
        assertEquals(STATE_ACTIVE, processInstance.status());

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);

        assertEquals("john", workItem.getParameter("Parameter"));

        processInstance.completeWorkItem(workItem.getStringId(), Collections.singletonMap("Result", "john doe"));

        assertEquals(STATE_COMPLETED, processInstance.status());
    }

    @Test
    public void testBusinessRuleTaskProcess() throws Exception {
        // This is a workaround to make it compile. A process that includes rules will never execute without a full Kogito context
        MockClassLoader classLoader = new MockClassLoader("org.kie.api.runtime.KieRuntimeBuilder");

        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-BusinessRuleTask.bpmn2")).get(0);

        ProcessMetaData metaData = new ProcessToExecModelGenerator(classLoader).generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);
    }

    @Test
    public void testServiceTaskProcess() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-ServiceProcess.bpmn2")).get(0);

        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        assertThat(metaData.getWorkItems())
                .hasSize(1)
                .contains("org.jbpm.bpmn2.objects.HelloService_hello_2_Handler");
    }

    @Test
    public void testCallActivityProcess() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("PrefixesProcessIdCallActivity.bpmn2")).get(0);

        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        assertThat(metaData.getSubProcesses())
                .hasSize(1)
                .containsKey("SubProcess")
                .containsValue("test.SubProcess");
    }

    @Test
    public void testAsyncExecution() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("async/AsyncProcess.bpmn")).get(0);
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.get());
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);

        Map<String, String> classData = Collections.singletonMap("com.example.AsyncProcessProcess", content);
        CountDownLatch latch = new CountDownLatch(1);
        String mainThread = Thread.currentThread().getName();
        AtomicReference<String> workItemThread = new AtomicReference<>();
        KogitoWorkItemHandler workItemHandler = new KogitoWorkItemHandler() {
            @Override
            public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                workItemThread.set(Thread.currentThread().getName());
                manager.completeWorkItem(workItem.getStringId(), Collections.singletonMap("response", "hello " + workItem.getParameter("name")));
                latch.countDown();
            }

            @Override
            public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                latch.countDown();
            }
        };

        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.singletonMap("org.jbpm.bpmn2.objects.HelloService_hello_3_Handler", workItemHandler));
        ProcessInstance i = UnitOfWorkExecutor.executeInUnitOfWork(process.getApplication().unitOfWorkManager(), () -> {
            ProcessInstance<BpmnVariables> processInstance = processes.get("AsyncProcess").createInstance(BpmnVariables.create(Collections.singletonMap("name", "Tiago")));
            processInstance.start();
            assertEquals(STATE_ACTIVE, processInstance.status());
            return processInstance;
        });

        //since the tasks as async, possibly executed in different threads.
        latch.await(5, TimeUnit.SECONDS);

        assertEquals(STATE_COMPLETED, i.status());
        BpmnVariables variables = (BpmnVariables) i.variables();
        assertEquals(variables.get("greeting"), "hello Tiago");
        assertNotEquals(mainThread, workItemThread.get());
    }

    /*
     * Helper methods
     */

    protected void log(String content) {
        logger.debug(content);
    }

    protected Map<String, BpmnProcess> createProcesses(Map<String, String> classData, Map<String, KogitoWorkItemHandler> handlers) throws Exception {
        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();

        String[] sources = new String[classData.size()];
        int index = 0;
        for (Entry<String, String> entry : classData.entrySet()) {
            String fileName = entry.getKey().replaceAll("\\.", "/") + ".java";
            sources[index++] = fileName;

            srcMfs.write(fileName, entry.getValue().getBytes());
        }

        CompilationResult result = JAVA_COMPILER.compile(sources, srcMfs, trgMfs, this.getClass().getClassLoader());
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).hasSize(0);

        CachedWorkItemHandlerConfig wiConfig = new CachedWorkItemHandlerConfig();
        for (Entry<String, KogitoWorkItemHandler> entry : handlers.entrySet()) {
            wiConfig.register(entry.getKey(), entry.getValue());
        }

        ProcessConfig config = new StaticProcessConfig(wiConfig, new DefaultProcessEventListenerConfig(), new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()));

        TestClassLoader cl = new TestClassLoader(this.getClass().getClassLoader(), trgMfs.getMap());
        Map<String, BpmnProcess> processes = new HashMap<>();
        BpmnProcesses bpmnProcesses = new BpmnProcesses();
        StaticApplication application = new StaticApplication(new StaticConfig(null, config), bpmnProcesses);

        for (String className : classData.keySet()) {
            Class<?> processClass = Class.forName(className, true, cl);

            Method processMethod = processClass.getMethod("process");
            Process process = (Process) processMethod.invoke(null);
            assertThat(process).isNotNull();

            processes.put(process.getId(), new BpmnProcess(process, config, application));
        }

        return processes;
    }

    private static class TestClassLoader extends URLClassLoader {
        private final Map<String, byte[]> extraClassDefs;

        public TestClassLoader(ClassLoader parent, Map<PortablePath, byte[]> extraClassDefs) {
            super(new URL[0], parent);
            this.extraClassDefs = new HashMap<>();

            for (Entry<PortablePath, byte[]> entry : extraClassDefs.entrySet()) {
                this.extraClassDefs.put(entry.getKey().asString().replace('/', '.').replaceFirst("\\.class", ""), entry.getValue());
            }
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            byte[] classBytes = this.extraClassDefs.remove(name);
            if (classBytes != null) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }

    }

    private static class MockClassLoader extends ClassLoader {

        private final Collection<String> mockedClass = new ArrayList<>();

        private MockClassLoader(String... mockedClass) {
            this.mockedClass.addAll(Arrays.asList(mockedClass));
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (mockedClass.contains(name)) {
                return Object.class;
            }
            return super.loadClass(name);
        }
    }
}
