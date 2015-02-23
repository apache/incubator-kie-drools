/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.services.impl.store.DeploymentSyncInvoker;
import org.jbpm.kie.services.impl.store.DeploymentSynchronizer;
import org.jbpm.kie.test.util.AbstractBaseTest;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.MavenRepository;

public class DeploymentServiceWithSyncTest extends AbstractBaseTest {
   
	protected List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    protected DeploymentStore store;
    protected DeploymentSyncInvoker invoker;
    
    protected TransactionalCommandService commandService;
    
    public void setCommandService(TransactionalCommandService commandService) {
    	this.commandService = commandService;
    }
    
    @Before
    public void prepare() {
    	configureServices();

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/signal.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        processes.add("repo/processes/general/callactivity.bpmn");
        
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
        
        ReleaseId releaseIdSupport = ks.newReleaseId(GROUP_ID, "support", VERSION);
        List<String> processesSupport = new ArrayList<String>();
        processesSupport.add("repo/processes/support/support.bpmn");
        
        InternalKieModule kJar2 = createKieJar(ks, releaseIdSupport, processesSupport);
        File pom2 = new File("target/kmodule2", "pom.xml");
        pom2.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom2);
            fs.write(getPom(releaseIdSupport).getBytes());
            fs.close();
        } catch (Exception e) {
            
        }

        repository.deployArtifact(releaseIdSupport, kJar2, pom2);
        
        configureDeploymentSync();
    }
    
    protected void configureDeploymentSync() {
        assertNotNull(deploymentService);
        
    	store = new DeploymentStore();
    	if (commandService == null) {
    		commandService = new TransactionalCommandService(emf);
    	}
		store.setCommandService(commandService);
        
        DeploymentSynchronizer sync = new DeploymentSynchronizer();
        sync.setDeploymentService(deploymentService);
        sync.setDeploymentStore(store);
        
        invoker = new DeploymentSyncInvoker(sync, 2L, 3L, TimeUnit.SECONDS);
        invoker.start();
    }
    
    @After
    public void cleanup() {
    	if (invoker != null) {
    		invoker.stop();
    	}
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
        close();
    }
    
    @Test
    public void testDeploymentOfProcessesBySync() throws Exception {

    	Collection<DeployedUnit> deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(0, deployed.size());
    	
    	KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);    		
    	Thread.sleep(3000);
    	store.enableDeploymentUnit(unit);
		units.add(unit);
		
		Thread.sleep(3000);
		
		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(1, deployed.size());
       
    }
    
    @Test
    public void testUndeploymentOfProcessesBySync() throws Exception {

    	Collection<DeployedUnit> deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(0, deployed.size());
    	
    	KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);    		
		deploymentService.deploy(unit);
		units.add(unit);

		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(1, deployed.size());
    	Thread.sleep(3000);
    	
    	store.disableDeploymentUnit(unit);

		Thread.sleep(3000);
		
		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(0, deployed.size());
    }
    
    @Test
    public void testDeactivateAndActivateOfProcessesBySync() throws Exception {

    	Collection<DeployedUnit> deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(0, deployed.size());
    	
    	KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);    		
		deploymentService.deploy(unit);
		units.add(unit);

		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(1, deployed.size());
    	assertTrue(deployed.iterator().next().isActive());
    	Thread.sleep(3000);
    	
    	store.deactivateDeploymentUnit(unit);

		Thread.sleep(3000);
		
		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(1, deployed.size());
    	assertFalse(deployed.iterator().next().isActive());
    	
    	store.activateDeploymentUnit(unit);

		Thread.sleep(3000);
		
		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(1, deployed.size());
    	assertTrue(deployed.iterator().next().isActive());
    }
    
    @Test
    public void testDeploymentOfProcessesBySyncWithDisabledAttribute() throws Exception {

    	Collection<DeployedUnit> deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(0, deployed.size());
    	
    	KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
    	unit.addAttribute("sync", "false");
    	Thread.sleep(3000);
    	store.enableDeploymentUnit(unit);
		units.add(unit);
		
		Thread.sleep(3000);
		
		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(0, deployed.size());
       
    }
   
}
