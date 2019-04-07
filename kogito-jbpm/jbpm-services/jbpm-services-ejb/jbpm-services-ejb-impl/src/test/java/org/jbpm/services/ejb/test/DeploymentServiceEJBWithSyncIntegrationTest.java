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

package org.jbpm.services.ejb.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.services.test.objects.CoundDownDeploymentListener;
import org.jbpm.services.api.ListenerSupport;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;


@RunWith(Arquillian.class)
public class DeploymentServiceEJBWithSyncIntegrationTest extends AbstractTestSupport {
   
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
        
    
    @After
    public void cleanup() {

    	cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
    }
	
	@Deployment
	public static WebArchive createDeployment() {
		File archive = new File("target/sample-war-ejb-app.war");
		if (!archive.exists()) {
			throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
		}
		WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
		war.addPackage("org.jbpm.services.ejb.test");
		war.addClass("org.jbpm.kie.services.test.objects.CoundDownDeploymentListener");// test cases

		// deploy test kjar
		deployKjar();
		
		return war;
	}
	
	protected static void deployKjar() {
		KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("processes/customtask.bpmn");
        processes.add("processes/humanTask.bpmn");
        processes.add("processes/signal.bpmn");
        processes.add("processes/import.bpmn");
        processes.add("processes/callactivity.bpmn");
        
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
        
        ReleaseId releaseIdSupport = ks.newReleaseId(GROUP_ID, "support", VERSION);
        List<String> processesSupport = new ArrayList<String>();
        processesSupport.add("processes/support.bpmn");
        
        InternalKieModule kJar2 = createKieJar(ks, releaseIdSupport, processesSupport);
        File pom2 = new File("target/kmodule2", "pom.xml");
        pom2.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom2);
            fs.write(getPom(releaseIdSupport).getBytes());
            fs.close();
        } catch (Exception e) {
            
        }

        repository.installArtifact(releaseIdSupport, kJar2, pom2);
	}
	
    protected CoundDownDeploymentListener configureListener(int threads) {
        CoundDownDeploymentListener countDownListener = new CoundDownDeploymentListener(threads);
        ((ListenerSupport)deploymentService).addListener(countDownListener);
        
        return countDownListener;
    }
    
    @EJB
	private DeploymentServiceEJBLocal deploymentService;
    
    @EJB(beanInterface=TransactionalCommandServiceEJBImpl.class)
	private TransactionalCommandService commandService;
    
    @Test
    public void testDeploymentOfProcessesBySync() throws Exception {
        
        CoundDownDeploymentListener countDownListener = configureListener(1);
        
    	DeploymentStore store = new DeploymentStore();
		store.setCommandService(commandService);
    	Collection<DeployedUnit> deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(0, deployed.size());
    	
    	KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);    		
		store.enableDeploymentUnit(unit);
		units.add(unit);
		
		countDownListener.waitTillCompleted(10000);
		
		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(1, deployed.size());
       
    }
    
    @Test
    public void testUndeploymentOfProcessesBySync() throws Exception {
        CoundDownDeploymentListener countDownListener = configureListener(2);
        
    	DeploymentStore store = new DeploymentStore();
		store.setCommandService(commandService);
    	Collection<DeployedUnit> deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(0, deployed.size());
    	
    	KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);    		
		deploymentService.deploy(unit);
		units.add(unit);

		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(1, deployed.size());
    	
    	countDownListener.waitTillCompleted(1000);
        
        store.disableDeploymentUnit(unit);

        countDownListener.waitTillCompleted(10000);
		
		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(0, deployed.size());
    }
    
    @Test
    public void testDeactivateAndActivateOfProcessesBySync() throws Exception {
        CoundDownDeploymentListener countDownListener = configureListener(2);
        
    	DeploymentStore store = new DeploymentStore();
		store.setCommandService(commandService);
		
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
    	
    	store.deactivateDeploymentUnit(unit);

    	countDownListener.waitTillCompleted(10000);
		
		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(1, deployed.size());
    	assertFalse(deployed.iterator().next().isActive());
    	
    	store.activateDeploymentUnit(unit);

    	countDownListener.reset(1);
        countDownListener.waitTillCompleted(10000);
		
		deployed = deploymentService.getDeployedUnits();
    	assertNotNull(deployed);
    	assertEquals(1, deployed.size());
    	assertTrue(deployed.iterator().next().isActive());
    }
   
}
