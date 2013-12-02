package org.jbpm.kie.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.kie.services.api.Kjar;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
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
import org.kie.internal.deployment.DeployedUnit;
import org.kie.internal.deployment.DeploymentService;
import org.kie.internal.deployment.DeploymentUnit;
import org.kie.internal.io.ResourceFactory;
import org.kie.scanner.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
public class FilteredKModuleDeploymentServiceTest extends AbstractBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(FilteredKModuleDeploymentServiceTest.class);
    
    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "domain-services.jar")
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
                .addPackage("org.jbpm.services.task.rule")
                .addPackage("org.jbpm.services.task.rule.impl")

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
                .addPackage("org.jbpm.shared.services.impl.tx")
                
                .addPackage("org.jbpm.kie.services.api")
                .addPackage("org.jbpm.kie.services.impl")
                .addPackage("org.jbpm.kie.services.cdi.producer")
                .addPackage("org.jbpm.kie.services.api.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.event.listeners")
                .addPackage("org.jbpm.kie.services.impl.audit")
                
                .addPackage("org.jbpm.kie.services.impl.vfs")
                
                .addPackage("org.jbpm.kie.services.impl.example")
                .addPackage("org.kie.commons.java.nio.fs.jgit")
                .addPackage("org.jbpm.kie.services.test") // Identity Provider Test Impl here
                .addAsResource("jndi.properties", "jndi.properties")
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

    }
    
    @Inject
    @Kjar
    private DeploymentService deploymentService;
    
    @Inject
    private RuntimeDataService runtimeDataService;
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    
    
    
    public void prepare(String packages) {
    	logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        
        List<String> processes2 = new ArrayList<String>();
        processes2.add("repo/processes/general/import.bpmn");
        
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, processes2, packages);
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
    public void testDeploymentOfProcessesFromCustomerPackageDeafultKBase() {
        prepare("customer.repo.processes.general");
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION, 
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessAssetDesc> processes = runtimeDataService.getProcesses();
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        processes = runtimeDataService.getProcessesByFilter("custom");
        assertNotNull(processes);
        assertEquals(1, processes.size());
        
        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier());
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        ProcessAssetDesc process = runtimeDataService.getProcessById("customtask");
        assertNotNull(process);
 
    }
    
    @Test
    public void testDeploymentOfProcessesFromCustomerPackage() {
        prepare("customer.repo.processes.general");
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test");
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION+":"+"KBase-test"+":"+"ksession-test", 
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessAssetDesc> processes = runtimeDataService.getProcesses();
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        processes = runtimeDataService.getProcessesByFilter("custom");
        assertNotNull(processes);
        assertEquals(1, processes.size());
        
        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier());
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        ProcessAssetDesc process = runtimeDataService.getProcessById("customtask");
        assertNotNull(process);
 
    }
    
    @Test
    public void testDeploymentOfProcessesFromOrderPackage() {
        prepare("order.repo.processes.general");
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test");
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION+":"+"KBase-test"+":"+"ksession-test", 
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessAssetDesc> processes = runtimeDataService.getProcesses();
        assertNotNull(processes);
        assertEquals(1, processes.size());
        
        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier());
        assertNotNull(processes);
        assertEquals(1, processes.size());
        
        ProcessAssetDesc process = runtimeDataService.getProcessById("Import");
        assertNotNull(process);
 
    }
    
    @Test
    public void testDeploymentOfProcessesWildcardPackage() {
        prepare("customer.*");
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test");
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION+":"+"KBase-test"+":"+"ksession-test", 
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessAssetDesc> processes = runtimeDataService.getProcesses();
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        processes = runtimeDataService.getProcessesByFilter("custom");
        assertNotNull(processes);
        assertEquals(1, processes.size());
        
        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier());
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        ProcessAssetDesc process = runtimeDataService.getProcessById("customtask");
        assertNotNull(process);
 
    }
    
   protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources, List<String> resources2, String packages ) {
     
        
        KieFileSystem kfs = createKieFileSystemWithKProject(ks, packages);
        kfs.writePomXML( getPom(releaseId) );

        
        for (String resource : resources) {
            kfs.write("src/main/resources/customer/" + resource, ResourceFactory.newClassPathResource(resource));
        }
        for (String resource : resources2) {
            kfs.write("src/main/resources/order/" + resource, ResourceFactory.newClassPathResource(resource));
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

    

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks, String packages) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage(packages)
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

    
        kieBaseModel1.newKieSessionModel("ksession-test").setDefault(true)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") )
                .newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }
}
