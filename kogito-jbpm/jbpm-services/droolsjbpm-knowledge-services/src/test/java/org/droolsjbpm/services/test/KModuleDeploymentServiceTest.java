package org.droolsjbpm.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.droolsjbpm.services.api.DeployedUnit;
import org.droolsjbpm.services.api.DeploymentService;
import org.droolsjbpm.services.api.DeploymentUnit;
import org.droolsjbpm.services.api.RuntimeDataService;
import org.droolsjbpm.services.impl.KModuleDeploymentUnit;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.runtime.manager.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.commons.java.nio.file.Path;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.scanner.MavenRepository;

@RunWith(Arquillian.class)
public class KModuleDeploymentServiceTest {
    
    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "domain-services.jar")
                .addPackage("org.jboss.seam.persistence") //seam-persistence
                .addPackage("org.jboss.seam.transaction") //seam-persistence
                .addPackage("org.jbpm.services.task")
                .addPackage("org.jbpm.services.task.wih") // work items org.jbpm.services.task.wih
                .addPackage("org.jbpm.services.task.annotations")
                .addPackage("org.jbpm.services.task.api")
                .addPackage("org.jbpm.services.task.impl")
                .addPackage("org.jbpm.services.task.events")
                .addPackage("org.jbpm.services.task.exception")
                .addPackage("org.jbpm.services.task.identity")
                .addPackage("org.jbpm.services.task.factories")
                .addPackage("org.jbpm.services.task.internals")
                .addPackage("org.jbpm.services.task.internals.lifecycle")
                .addPackage("org.jbpm.services.task.lifecycle.listeners")
                .addPackage("org.jbpm.services.task.query")
                .addPackage("org.jbpm.services.task.util")
                .addPackage("org.jbpm.services.task.commands") // This should not be required here
                .addPackage("org.jbpm.services.task.deadlines") // deadlines
                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.services.task.subtask")

                .addPackage("org.kie.api.runtime.manager")
                .addPackage("org.kie.internal.runtime.manager")
                .addPackage("org.kie.internal.runtime.manager.context")
                .addPackage("org.kie.internal.runtime.manager.cdi.qualifier")
                
                .addPackage("org.jbpm.runtime.manager.impl")
                .addPackage("org.jbpm.runtime.manager.impl.cdi")                               
                .addPackage("org.jbpm.runtime.manager.impl.factory")
                .addPackage("org.jbpm.runtime.manager.impl.jpa")
                .addPackage("org.jbpm.runtime.manager.impl.manager")
                .addPackage("org.jbpm.runtime.manager.impl.task")
                .addPackage("org.jbpm.runtime.manager.impl.tx")
                
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                
                .addPackage("org.droolsjbpm.services.api")
                .addPackage("org.droolsjbpm.services.impl")
                .addPackage("org.droolsjbpm.services.api.bpmn2")
                .addPackage("org.droolsjbpm.services.impl.bpmn2")
                .addPackage("org.droolsjbpm.services.impl.event.listeners")
                .addPackage("org.droolsjbpm.services.impl.audit")
                .addPackage("org.droolsjbpm.services.impl.util")
                
                .addPackage("org.droolsjbpm.services.impl.vfs")
                
                .addPackage("org.droolsjbpm.services.impl.example")
                .addPackage("org.kie.commons.java.nio.fs.jgit")
                .addPackage("org.droolsjbpm.services.test") // Identity Provider Test Impl here
                .addAsResource("jndi.properties", "jndi.properties")
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/kmodule-beans.xml", ArchivePaths.create("beans.xml"));

    }
    
    @Inject
    private DeploymentService deploymentService;
    
    @Inject
    private RuntimeDataService runtimeDataService;
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    private static final String ARTIFACT_ID = "test-module";
    private static final String GROUP_ID = "org.jbpm.test";
    private static final String VERSION = "1.0.0-SNAPSHOT";
    
    @Before
    public void prepare() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId, null).getBytes());
            fs.close();
        } catch (Exception e) {
            
        }
        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
    }
    
    @After
    public void cleanup() {
        TestUtil.cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
    }
    
    @Test
    public void testDeploymentOfProcesses() {
        
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertNotNull(deployed.getDeployedAssetLocation("customtask"));
        assertTrue(deployed.getDeployedAssetLocation("customtask").endsWith("repo/processes/general/customtask.bpmn"));
        
        assertNotNull(runtimeDataService);
        Collection<ProcessDesc> processes = runtimeDataService.getProcesses();
        assertNotNull(processes);
        assertEquals(4, processes.size());
        
        processes = runtimeDataService.getProcessesByFilter("custom");
        assertNotNull(processes);
        assertEquals(1, processes.size());
        
        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier());
        assertNotNull(processes);
        assertEquals(4, processes.size());
        
        ProcessDesc process = runtimeDataService.getProcessById("customtask");
        
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
    }
//    
//    
//    @Test
//    public void testDeploymentOfAllProcesses() {
//        
//        assertNotNull(deploymentService);
//        // deploy first unit
//        DeploymentUnit deploymentUnitGeneral = new VFSDeploymentUnit("general", "", "processes/general");        
//        deploymentService.deploy(deploymentUnitGeneral);
//        units.add(deploymentUnitGeneral);
//        
//        RuntimeManager managerGeneral = deploymentService.getRuntimeManager(deploymentUnitGeneral.getIdentifier());
//        assertNotNull(managerGeneral);
//        
//        // deploy second unit
//        DeploymentUnit deploymentUnitSupport = new VFSDeploymentUnit("support", "", "processes/support");        
//        deploymentService.deploy(deploymentUnitSupport);
//        units.add(deploymentUnitSupport);
//        
//        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnitGeneral.getIdentifier());
//        assertNotNull(deployedGeneral);
//        assertNotNull(deployedGeneral.getDeploymentUnit());
//        assertNotNull(deployedGeneral.getRuntimeManager());
//        assertNotNull(deployedGeneral.getDeployedAssetLocation("customtask"));
//        assertTrue(deployedGeneral.getDeployedAssetLocation("customtask").endsWith("repo/processes/general/customtask.bpmn"));
//        
//        RuntimeManager managerSupport = deploymentService.getRuntimeManager(deploymentUnitSupport.getIdentifier());
//        assertNotNull(managerSupport);
//        
//        DeployedUnit deployedSupport = deploymentService.getDeployedUnit(deploymentUnitSupport.getIdentifier());
//        assertNotNull(deployedSupport);
//        assertNotNull(deployedSupport.getDeploymentUnit());
//        assertNotNull(deployedSupport.getRuntimeManager());
//        assertNotNull(deployedSupport.getDeployedAssetLocation("support.process"));
//        assertTrue(deployedSupport.getDeployedAssetLocation("support.process").endsWith("repo/processes/support/support.bpmn"));
//        
//        // execute process that is bundled in first deployment unit
//        RuntimeEngine engine = managerGeneral.getRuntimeEngine(EmptyContext.get());
//        assertNotNull(engine);
//        
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("id", "test");
//        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);
//        
//        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
//        
//        // execute process that is in second deployment unit
//        RuntimeEngine engineSupport = managerSupport.getRuntimeEngine(EmptyContext.get());
//        assertNotNull(engineSupport);
//        
//        ProcessInstance supportPI = engineSupport.getKieSession().startProcess("support.process");
//        assertEquals(ProcessInstance.STATE_ACTIVE, supportPI.getState());
//        
//        List<TaskSummary> tasks = engineSupport.getTaskService().getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
//        assertNotNull(tasks);
//        assertEquals(1, tasks.size());
//        
//        engineSupport.getKieSession().abortProcessInstance(supportPI.getId());
//        assertNull(engineSupport.getKieSession().getProcessInstance(supportPI.getState()));
//    }
//    
//    @Test(expected=IllegalStateException.class)
//    public void testDuplicatedDeployment() {
//            
//        assertNotNull(deploymentService);
//        
//        DeploymentUnit deploymentUnit = new VFSDeploymentUnit("general", "", "processes/general");        
//        deploymentService.deploy(deploymentUnit);
//        units.add(deploymentUnit);
//        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
//        assertNotNull(deployedGeneral);
//        assertNotNull(deployedGeneral.getDeploymentUnit());
//        assertNotNull(deployedGeneral.getRuntimeManager());
//        // duplicated deployment of the same deployment unit should fail
//        deploymentService.deploy(deploymentUnit);
//    }   
    
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
          if(!kieBuilder.buildAll().getResults().getMessages().isEmpty()){
            for(Message message: kieBuilder.buildAll().getResults().getMessages()){
                System.out.println("Error Message: ("+message.getPath()+") "+message.getText());
            }
            throw new RuntimeException("There are errors builing the package, please check your knowledge assets!");
          }
        
        return ( InternalKieModule ) kieBuilder.getKieModule();
    }

    

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

    
        
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }
}
