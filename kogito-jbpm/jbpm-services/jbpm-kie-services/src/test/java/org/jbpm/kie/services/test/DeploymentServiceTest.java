package org.jbpm.kie.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jbpm.kie.services.api.DeployedUnit;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.api.Vfs;
import org.jbpm.kie.services.impl.VFSDeploymentUnit;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;

@RunWith(Arquillian.class)
public class DeploymentServiceTest extends AbstractBaseTest {
    
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
    @Vfs
    private DeploymentService deploymentService;
    
    @Inject
    private RuntimeDataService runtimeDataService;
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
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
        
        DeploymentUnit deploymentUnit = new VFSDeploymentUnit("general", "", "processes/general");
        
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
    
    
    @Test
    public void testDeploymentOfAllProcesses() {
        
        assertNotNull(deploymentService);
        // deploy first unit
        DeploymentUnit deploymentUnitGeneral = new VFSDeploymentUnit("general", "", "processes/general");        
        deploymentService.deploy(deploymentUnitGeneral);
        units.add(deploymentUnitGeneral);
        
        RuntimeManager managerGeneral = deploymentService.getRuntimeManager(deploymentUnitGeneral.getIdentifier());
        assertNotNull(managerGeneral);
        
        // deploy second unit
        DeploymentUnit deploymentUnitSupport = new VFSDeploymentUnit("support", "", "processes/support");        
        deploymentService.deploy(deploymentUnitSupport);
        units.add(deploymentUnitSupport);
        
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnitGeneral.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());
        assertNotNull(deployedGeneral.getDeployedAssetLocation("customtask"));
        assertTrue(deployedGeneral.getDeployedAssetLocation("customtask").endsWith("repo/processes/general/customtask.bpmn"));
        
        RuntimeManager managerSupport = deploymentService.getRuntimeManager(deploymentUnitSupport.getIdentifier());
        assertNotNull(managerSupport);
        
        DeployedUnit deployedSupport = deploymentService.getDeployedUnit(deploymentUnitSupport.getIdentifier());
        assertNotNull(deployedSupport);
        assertNotNull(deployedSupport.getDeploymentUnit());
        assertNotNull(deployedSupport.getRuntimeManager());
        assertNotNull(deployedSupport.getDeployedAssetLocation("support.process"));
        assertTrue(deployedSupport.getDeployedAssetLocation("support.process").endsWith("repo/processes/support/support.bpmn"));
        
        // execute process that is bundled in first deployment unit
        RuntimeEngine engine = managerGeneral.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
        // execute process that is in second deployment unit
        RuntimeEngine engineSupport = managerSupport.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engineSupport);
        
        ProcessInstance supportPI = engineSupport.getKieSession().startProcess("support.process");
        assertEquals(ProcessInstance.STATE_ACTIVE, supportPI.getState());
        
        List<TaskSummary> tasks = engineSupport.getTaskService().getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        engineSupport.getKieSession().abortProcessInstance(supportPI.getId());
        assertNull(engineSupport.getKieSession().getProcessInstance(supportPI.getState()));
    }
    
    @Test(expected=IllegalStateException.class)
    public void testDuplicatedDeployment() {
            
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new VFSDeploymentUnit("general", "", "processes/general");        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());
        // duplicated deployment of the same deployment unit should fail
        deploymentService.deploy(deploymentUnit);
    }
}
