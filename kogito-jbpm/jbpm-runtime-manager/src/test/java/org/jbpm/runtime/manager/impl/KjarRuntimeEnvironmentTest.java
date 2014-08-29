package org.jbpm.runtime.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractBaseTest;
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
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.scanner.MavenRepository;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class KjarRuntimeEnvironmentTest extends AbstractBaseTest {
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;
    private RuntimeManager manager; 
    
    private static final String ARTIFACT_ID = "kjar-module";
    private static final String GROUP_ID = "org.jbpm.test";
    private static final String VERSION = "1.0.0-SNAPSHOT";
    
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
        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
        
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
}
