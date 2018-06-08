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

package org.jbpm.runtime.manager.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.test.util.PoolingDataSource;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class KjarRuntimeEnvironmentTest extends AbstractBaseTest {

    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;
    private RuntimeManager manager; 
    
    private static final String ARTIFACT_ID = "kjar-module";
    private static final String GROUP_ID = "org.jbpm.test";
    private static final String VERSION = "1.0.0";
    
    @Before
    public void setup() {
    	TestUtil.cleanupSingletonSessionId();
    	KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("BPMN2-ScriptTask.bpmn2");
        processes.add("BPMN2-UserTask.bpmn2");
        processes.add("BPMN2-CustomTask.bpmn2");
        
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
            
        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, pom);
        
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        pds = TestUtil.setupPoolingDataSource();
    }
    
    @After
    public void teardown() {
    	if (manager != null) {
    		manager.close();    		
    	}
        pds.close();
    }
    
    @Test
    public void testCustomTaskFromKjar() {
    	KieServices ks = KieServices.Factory.get();
          
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder(ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION))
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
    }
    

    @Test
    public void testScriptTaskFromKjar() {
    	KieServices ks = KieServices.Factory.get();
          
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder(ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION))
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = engine.getKieSession().startProcess("ScriptTask", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
    }
    
    @Test
    public void testScriptTaskFromKjarUsingNamedKbaseKsession() {
    	KieServices ks = KieServices.Factory.get();
          
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder(ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION), "defaultKieBase", "defaultKieSession")
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = engine.getKieSession().startProcess("ScriptTask", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
    }
    
    @Test
    public void testUserTaskFromKjar() {
    	KieServices ks = KieServices.Factory.get();
          
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder(ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION))
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = engine.getKieSession().startProcess("UserTask", params);
        
        List<TaskSummary> tasks = engine.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        engine.getTaskService().start(taskId, "john");
        engine.getTaskService().complete(taskId, "john", null);
        
        processInstance = engine.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        manager.disposeRuntimeEngine(engine);
        
    }
    
    @Test
    public void testScriptTaskFromKjarByName() {
    	
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder(GROUP_ID, ARTIFACT_ID, VERSION)
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = engine.getKieSession().startProcess("ScriptTask", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
    }
    
    @Test
    public void testScriptTaskFromKjarByNameNamedKbaseKsession() {
    	
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder(GROUP_ID, ARTIFACT_ID, VERSION, "defaultKieBase", "defaultKieSession")
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = engine.getKieSession().startProcess("ScriptTask", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
    }
    
    @Test
    public void testScriptTaskFromClasspathContainer() {

    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newClasspathKmoduleDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = engine.getKieSession().startProcess("ScriptTask", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
    }
    
    @Test
    public void testScriptTaskFromClasspathContainerNamedKbaseKsession() {

    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newClasspathKmoduleDefaultBuilder("defaultKieBase", "defaultKieSession")
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = engine.getKieSession().startProcess("ScriptTask", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
    }
    
    @Test
    public void testUserTaskFromKjarPPI() {
    	KieServices ks = KieServices.Factory.get();
          
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder(ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION))
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = engine.getKieSession().startProcess("UserTask", params);
        
        manager.disposeRuntimeEngine(engine);
        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));        
        
        List<TaskSummary> tasks = engine.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        manager.disposeRuntimeEngine(engine);
        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        
        engine.getTaskService().start(taskId, "john");
        
        manager.disposeRuntimeEngine(engine);
        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        
        engine.getTaskService().complete(taskId, "john", null);
        
        manager.disposeRuntimeEngine(engine);
        try {
	        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
	        
	        processInstance = engine.getKieSession().getProcessInstance(processInstance.getId());
	        assertNull(processInstance);
	        fail("Should fail as process instance is already completed");
        } catch (Exception e) {
        	
        }
        manager.disposeRuntimeEngine(engine);
        
    }
    
    @Test
    public void testXStreamUnmarshalCustomObject() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, "xstream-test", VERSION);
        File kjar = new File("src/test/resources/kjar/jbpm-module.jar");
        File pom = new File("src/test/resources/kjar/pom.xml");
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kjar, pom);
        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder(releaseId)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("Rest", new WorkItemHandler() {
                            
                            @Override
                            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                                Map<String, Object> results = new HashMap<String, Object>();
                                results.put("Result", "<pv207.finepayment.Client><exists>true</exists><name>Pavel</name></pv207.finepayment.Client>");
                                
                                manager.completeWorkItem(workItem.getId(), results);
                            }
                            
                            @Override
                            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {                                
                            }
                        });
                        
                        return handlers;
                    }
                    
                })
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = engine.getKieSession().startProcess("RESTTask.RestProcess", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
        Object processClient = ((WorkflowProcessInstance)processInstance).getVariable("processClient");
        assertNotNull(processClient);
        assertEquals("pv207.finepayment.Client", processClient.getClass().getName());
        assertEquals("Pavel", valueOf(processClient, "name"));
        assertEquals("true", valueOf(processClient, "exists"));
    }
    
    // helper methods
    protected String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                "  <version>" + releaseId.getVersion() + "</version>\n" +
                "\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += "  <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        return pom;
    }
   
   protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources ) {
     
        
        KieFileSystem kfs = createKieFileSystemWithKProject(ks);
        kfs.writePomXML( getPom(releaseId) );

        
        for (String resource : resources) {
            kfs.write("src/main/resources/KBase-test/" + resource, ResourceFactory.newClassPathResource(resource));
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        if (!kieBuilder.buildAll().getResults().getMessages().isEmpty()) {
            for (Message message : kieBuilder.buildAll().getResults().getMessages()) {
                logger.error("Error Message: ({}) {}", message.getPath(), message.getText());
            }
            throw new RuntimeException(
                    "There are errors builing the package, please check your knowledge assets!");
        }
        
        return ( InternalKieModule ) kieBuilder.getKieModule();
    }

    

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("defaultKieBase").setDefault(true).addPackage("*")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

    
        kieBaseModel1.newKieSessionModel("defaultKieSession").setDefault(true)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") )
                .newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }
    
    protected Object valueOf(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }
}
