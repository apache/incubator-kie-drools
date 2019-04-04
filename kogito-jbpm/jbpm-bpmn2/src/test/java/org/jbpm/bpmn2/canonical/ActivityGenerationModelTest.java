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
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.core.impl.InternalKnowledgeBase;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityGenerationModelTest extends JbpmBpmn2TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityGenerationModelTest.class);
    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.getInstance().loadCompiler( JavaDialectConfiguration.CompilerType.NATIVE, "1.8" );
    
    private KieSession ksession;
    
    @After
    public void dispose() {
        if (ksession != null) {   
            ksession.dispose();
            ksession = null;
        }
    }
    
    @Test
    public void testMinimalProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MinimalProcess.bpmn2");
                
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) kbase.getProcess("Minimal"));        
        String content = metaData.getGeneratedClassModel();
        assertThat(content).isNotNull();
        log(content);
        
        Map<String, String> classData = new HashMap<>();
        classData.put("com.sample.MinimalProcess", content);
        
        ksession = createKnowledgeSession(createKieBaseForProcesses(classData));
        ProcessInstance processInstance = ksession.startProcess("Minimal");
        assertProcessInstanceCompleted(processInstance);
    }
    
    @Test
    public void testUserTaskProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTask.bpmn2");
                
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) kbase.getProcess("UserTask"));        
        String content = metaData.getGeneratedClassModel();
        assertThat(content).isNotNull();
        log(content);
        
        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.UserTaskProcess", content);
        
        ksession = createKnowledgeSession(createKieBaseForProcesses(classData));
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test
    public void testUserTaskWithParamProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithParametrizedInput.bpmn2");
                
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) kbase.getProcess("UserTask"));        
        String content = metaData.getGeneratedClassModel();
        assertThat(content).isNotNull();
        log(content);
        
        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.UserTaskProcess", content);
        
        ksession = createKnowledgeSession(createKieBaseForProcesses(classData));
        
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("Executing task of process instance " + processInstance.getId() + " as work item with Hello",
                workItem.getParameter("Description").toString().trim());
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test
    public void testScriptMultilineExprProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-CallActivitySubProcess.bpmn2");
                
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) kbase.getProcess("SubProcess"));        
        String content = metaData.getGeneratedClassModel();
        assertThat(content).isNotNull();
        log(content);
        
        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.SubProcessProcess", content);
        
        ksession = createKnowledgeSession(createKieBaseForProcesses(classData));
        ProcessInstance processInstance = ksession.startProcess("SubProcess");
        assertProcessInstanceCompleted(processInstance);
    }
    
    @Test
    public void testExclusiveSplit() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplit.bpmn2");
        
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) kbase.getProcess("com.sample.test"));        
        String content = metaData.getGeneratedClassModel();
        assertThat(content).isNotNull();
        log(content);
        
        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.TestProcess", content);
        
        ksession = createKnowledgeSession(createKieBaseForProcesses(classData));
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "First");
        params.put("y", "Second");
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertProcessInstanceCompleted(processInstance);

    }
    
    @Test
    public void testInclusiveSplit() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-InclusiveSplit.bpmn2");
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) kbase.getProcess("com.sample.test"));        
        String content = metaData.getGeneratedClassModel();
        assertThat(content).isNotNull();
        log(content);
        
        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.TestProcess", content);
        
        ksession = createKnowledgeSession(createKieBaseForProcesses(classData));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 15);
        ProcessInstance processInstance = ksession.startProcess(
                "com.sample.test", params);
        assertProcessInstanceCompleted(processInstance);

    }
    
    @Test
    public void testInclusiveSplitDefaultConnection() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-InclusiveGatewayWithDefault.bpmn2");
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) kbase.getProcess("InclusiveGatewayWithDefault"));        
        String content = metaData.getGeneratedClassModel();
        assertThat(content).isNotNull();
        log(content);
        
        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.InclusiveGatewayWithDefaultProcess", content);
        
        ksession = createKnowledgeSession(createKieBaseForProcesses(classData));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("test", "c");
        ProcessInstance processInstance = ksession.startProcess("InclusiveGatewayWithDefault", params);
        assertProcessInstanceCompleted(processInstance);

    }
    
    @Test
    public void testParallelGateway() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ParallelSplit.bpmn2");
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) kbase.getProcess("com.sample.test"));        
        String content = metaData.getGeneratedClassModel();
        assertThat(content).isNotNull();
        log(content);
        
        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.TestProcess", content);
        
        ksession = createKnowledgeSession(createKieBaseForProcesses(classData));
        ProcessInstance processInstance = ksession.startProcess("com.sample.test");
        assertProcessInstanceCompleted(processInstance);
        
    }
    
    @Test
    public void testInclusiveSplitAndJoinNested() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-InclusiveSplitAndJoinNested.bpmn2");
        ProcessMetaData metaData = ProcessToExecModelGenerator.INSTANCE.generate((WorkflowProcess) kbase.getProcess("com.sample.test"));        
        String content = metaData.getGeneratedClassModel();
        assertThat(content).isNotNull();
        log(content);
        
        Map<String, String> classData = new HashMap<>();
        classData.put("org.drools.bpmn2.TestProcess", content);
        
        ksession = createKnowledgeSession(createKieBaseForProcesses(classData));
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 15);
        ProcessInstance processInstance = ksession.startProcess(
                "com.sample.test", params);

        List<WorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(2, activeWorkItems.size());
        
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        for (WorkItem wi : activeWorkItems) {
            ksession.getWorkItemManager().completeWorkItem(wi.getId(), null);
        }

        activeWorkItems = workItemHandler.getWorkItems();
        assertEquals(2, activeWorkItems.size());
        
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        for (WorkItem wi : activeWorkItems) {
            ksession.getWorkItemManager().completeWorkItem(wi.getId(), null);
        }
        assertProcessInstanceFinished(processInstance, ksession);

    }
    
    /*
     * Helper methods
     */
    
    protected void log(String content) {
        LOG.info(content);
    }
    
    protected KieBase createEmptyKnowledgeBase() throws Exception {

        KieServices ks = KieServices.Factory.get();  
        KieRepository kr = ks.getRepository();
        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        KieBase kbase = kContainer.getKieBase();
        
        for (Process p : kbase.getProcesses()) {
            ((InternalKnowledgeBase) kbase).removeProcess(p.getId());
        }
        
        return kbase;
    }
    
    
    protected KieBase createKieBaseForProcesses(Map<String, String> classData) throws Exception {
        KieBase testKbase = createEmptyKnowledgeBase();
        
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
        
        TestClassLoader cl = new TestClassLoader(this.getClass().getClassLoader(), trgMfs.getMap());
        
        for (String className : classData.keySet()) {
            Class<?> processClass = Class.forName(className, true, cl);
            
            Method processMethod = processClass.getMethod("process");
            Object process = processMethod.invoke(null);
            assertThat(process).isNotNull();
            
            
            ((InternalKnowledgeBase) testKbase).addProcess((Process) process);
        }
        
        return testKbase;
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
