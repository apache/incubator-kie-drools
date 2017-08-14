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

package org.jbpm.kie.services.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.services.impl.store.DeploymentSyncInvoker;
import org.jbpm.kie.services.impl.store.DeploymentSynchronizer;
import org.jbpm.kie.services.test.objects.CoundDownDeploymentListener;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.ListenerSupport;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.UpdateStringCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

/*
 * IMPORTANT: we cannot rely on @Test(timeout=1000) within this test as it is 
 * extended by CDI tests and arquillian we use does not support it - as soon as
 * it will be upgraded to 1.1.4 that timeout from JUnit can be used
 *
 */
public class DeactivateDeploymentServiceWithSyncTest extends AbstractKieServicesBaseTest {
    
    static Logger logger = LoggerFactory.getLogger(DeactivateDeploymentServiceWithSyncTest.class);
   
	protected List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    protected DeploymentStore store;
    protected DeploymentSynchronizer sync;
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
        KieMavenRepository repository = getKieMavenRepository();
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
        
        sync = new DeploymentSynchronizer();
        sync.setDeploymentService(deploymentService);
        sync.setDeploymentStore(store);
        
        invoker = new DeploymentSyncInvoker(sync, 1L, 1L, TimeUnit.SECONDS);
        
    }
    
    @After
    public void cleanup() {
    	if (invoker != null) {
    		invoker.stop();
    	}
    	
        int deleted = 0;
        deleted += commandService.execute(new UpdateStringCommand("delete from  DeploymentStoreEntry dse"));

        logger.info("Deleted " + deleted);
        
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
    public void testDeactivateDeploymentBySync() throws Exception {
        
        Collection<DeployedUnit> deployed = deploymentService.getDeployedUnits();
        assertNotNull(deployed);
        assertEquals(0, deployed.size());
        
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);        
        deploymentService.deploy(unit);
        deploymentService.deactivate(unit.getIdentifier());
        
        ((KModuleDeploymentService) deploymentService).shutdown();  
        sync.clear();
        
        AtomicBoolean deploymentActive = new AtomicBoolean(true);

        CoundDownDeploymentListener countDownListener = new CoundDownDeploymentListener(1) {
            @Override
            public void onDeploy(DeploymentEvent event) {
                // This used to use a specific listener for setting the active state
                deploymentActive.set(event.getDeployedUnit().isActive());
                super.onDeploy(event);
            }
        };
        countDownListener.setDeploy(true);
        ((ListenerSupport)deploymentService).addListener(countDownListener);
        invoker.start();
        
        countDownListener.waitTillCompleted();
        
        
        assertFalse("Deployment should be deactivated", deploymentActive.get());
        deployed = deploymentService.getDeployedUnits();
        assertNotNull(deployed);
        assertEquals(1, deployed.size());
        
    }
}
