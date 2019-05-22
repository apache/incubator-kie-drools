/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.bpmn2.canonical;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.core.event.DebugProcessEventListener;
import org.drools.core.io.impl.ClassPathResource;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.jbpm.compiler.canonical.UserTaskModelMetaData;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.Test;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.submarine.process.ProcessConfig;
import org.kie.submarine.process.ProcessInstance;
import org.kie.submarine.process.bpmn2.BpmnProcess;
import org.kie.submarine.process.bpmn2.BpmnVariables;
import org.kie.submarine.process.impl.CachedWorkItemHandlerConfig;
import org.kie.submarine.process.impl.DefaultProcessEventListenerConfig;
import org.kie.submarine.process.impl.StaticProcessConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityGenerationModelTest extends JbpmBpmn2TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityGenerationModelTest.class);
    @SuppressWarnings("deprecation")
    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.getInstance().loadCompiler( JavaDialectConfiguration.CompilerType.NATIVE, "1.8" );
    
    @Test
    public void testMinimalProcess() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-MinimalProcess.bpmn2")).get(0);        
                
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
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
    public void testUserTaskProcessWithTaskModels() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);

        List<UserTaskModelMetaData> models = ProcessToExecModelGenerator.INSTANCE.generateUserTaskModel((WorkflowProcess) process.legacyProcess());

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
             
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
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
        
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        processInstance.completeWorkItem(workItem.getId(), null);
        assertEquals(STATE_COMPLETED, processInstance.status());
    }
    
    @Test
    public void testUserTaskWithParamProcess() throws Exception {        
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTaskWithParametrizedInput.bpmn2")).get(0);
                
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
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
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("Executing task of process instance " + processInstance.id() + " as work item with Hello",
                workItem.getParameter("Description").toString().trim());
        processInstance.completeWorkItem(workItem.getId(), null);
        assertEquals(STATE_COMPLETED, processInstance.status());
    }
    
    @Test
    public void testScriptMultilineExprProcess() throws Exception {        
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-CallActivitySubProcess.bpmn2")).get(0);
                
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
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
        
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
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
    public void testInclusiveSplit() throws Exception {
        
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-InclusiveSplit.bpmn2")).get(0);
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
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
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
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
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
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
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
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

        List<WorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(2, activeWorkItems.size());
        
    
        for (WorkItem wi : activeWorkItems) {
            processInstance.completeWorkItem(wi.getId(), null);
        }

        activeWorkItems = workItemHandler.getWorkItems();
        assertEquals(2, activeWorkItems.size());

        for (WorkItem wi : activeWorkItems) {
            processInstance.completeWorkItem(wi.getId(), null);
        }
        assertEquals(STATE_COMPLETED, processInstance.status());

    }
    
    @Test
    public void testWorkItemProcessWithVariableMapping() throws Exception {        
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-ServiceProcess.bpmn2")).get(0);
                
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);
        
        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.ServiceProcessProcess", content);
 
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        Map<String, BpmnProcess> processes = createProcesses(classData, Collections.singletonMap("Service Task", workItemHandler));                
        ProcessInstance<BpmnVariables> processInstance = processes.get("ServiceProcess").createInstance(BpmnVariables.create(params));
        
        processInstance.start();
        assertEquals(STATE_ACTIVE, processInstance.status());
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        
        assertEquals("john", workItem.getParameter("Parameter"));
        
        processInstance.completeWorkItem(workItem.getId(), Collections.singletonMap("Result", "john doe"));
        
        assertEquals(STATE_COMPLETED, processInstance.status());
    }
    
    @Test
    public void testBusinessRuleTaskProcess() throws Exception {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-BusinessRuleTask.bpmn2")).get(0);        
                
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) process.legacyProcess());        
        String content = metaData.getGeneratedClassModel().toString();
        assertThat(content).isNotNull();
        log(content);
    }
    
    /*
     * Helper methods
     */
    
    protected void log(String content) {
        LOG.info(content);
    }
   
    protected Map<String, BpmnProcess> createProcesses(Map<String, String> classData, Map<String, WorkItemHandler> handlers) throws Exception {
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
        for (Entry<String, WorkItemHandler> entry : handlers.entrySet()) {
            wiConfig.register(entry.getKey(), entry.getValue());
        }
        
        ProcessConfig config = new StaticProcessConfig(wiConfig, new DefaultProcessEventListenerConfig(new DebugProcessEventListener()));
        
        TestClassLoader cl = new TestClassLoader(this.getClass().getClassLoader(), trgMfs.getMap());
        Map<String, BpmnProcess> processes = new HashMap<>();
        for (String className : classData.keySet()) {
            Class<?> processClass = Class.forName(className, true, cl);
            
            Method processMethod = processClass.getMethod("process");
            Process process = (Process) processMethod.invoke(null);
            assertThat(process).isNotNull();
            
            
            processes.put(process.getId(), new BpmnProcess(process, config));
        }
        
        return processes;
    }
    
    private static class TestClassLoader extends URLClassLoader {
        private final Map<String, byte[]> extraClassDefs;

        public TestClassLoader(ClassLoader parent, Map<String, byte[]> extraClassDefs) {
          super(new URL[0], parent);
          this.extraClassDefs = new HashMap<String, byte[]>();
          
          for (Entry<String, byte[]> entry : extraClassDefs.entrySet()) {
              this.extraClassDefs.put(entry.getKey().replaceAll("/", ".").replaceFirst("\\.class", ""), entry.getValue());
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
}
