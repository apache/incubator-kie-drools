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

package org.jbpm.services.ejb.client;

import static org.junit.Assert.assertNotNull;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractBaseTest;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.ejb.api.DefinitionServiceEJBRemote;
import org.jbpm.services.ejb.api.DeploymentServiceEJBRemote;
import org.jbpm.services.ejb.api.ProcessServiceEJBRemote;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBRemote;
import org.jbpm.services.ejb.api.UserTaskServiceEJBRemote;
import org.jbpm.services.ejb.client.helper.DeploymentServiceWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.MavenRepository;

public class ClientDeployEJBTest extends AbstractBaseTest {
	
	private static final String application = "ejb-app";
	
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    private static final String ARTIFACT_ID = "custom-data-project";
    private static final String GROUP_ID = "org.jbpm.test";
    private static final String VERSION = "1.0";
    
    private ClassLoader customClassLoader;
    private ClassLoader originClassLoader;
    
    private Long processInstanceId;
    
    @Before
    public void prepare() throws MalformedURLException {
    	originClassLoader = Thread.currentThread().getContextClassLoader();
    	configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        File kjar = new File("src/test/resources/kjar/custom-data-project-1.0.jar");
        File pom = new File("src/test/resources/kjar/pom.xml");
        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kjar, pom);
        
        URL[] urls = new URL[]{kjar.toURI().toURL()};
        customClassLoader = new URLClassLoader(urls, this.getClass().getClassLoader());
        Thread.currentThread().setContextClassLoader(customClassLoader);
    }
    
    @After
    public void cleanup() {
        close();
    }

	@Override
	protected void close() {
		// do nothing
	}

	@Override
	protected void configureServices() {
		try {
			ClientServiceFactory factory = ServiceFactoryProvider.getProvider("JBoss");
			DeploymentServiceEJBRemote deploymentService = factory.getService(application, DeploymentServiceEJBRemote.class);
			ProcessServiceEJBRemote processService = factory.getService(application, ProcessServiceEJBRemote.class);
			RuntimeDataServiceEJBRemote runtimeDataService = factory.getService(application, RuntimeDataServiceEJBRemote.class);
			DefinitionServiceEJBRemote definitionService = factory.getService(application, DefinitionServiceEJBRemote.class);
			UserTaskServiceEJBRemote userTaskService = factory.getService(application, UserTaskServiceEJBRemote.class);
			
			setBpmn2Service(definitionService);
			setProcessService(processService);
			setRuntimeDataService(runtimeDataService);
			setUserTaskService(userTaskService);
			setDeploymentService(new DeploymentServiceWrapper(deploymentService));
		} catch (Exception e) {
			throw new RuntimeException("Unable to configure services", e);
		}
	}
	
	@Test
	public void testDeploy() {

        assertNotNull(deploymentService);
        
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        
     
	}
	
	@Test
	public void testUndeploy() {

        assertNotNull(deploymentService);
        
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.undeploy(deploymentUnit);
        
     
	}
	
	
	@Test
	public void testGetProcesses() {
		
		Collection<ProcessDefinition> defs = runtimeDataService.getProcesses();
		for (ProcessDefinition def : defs) {
			System.out.println("Process " + def.getName() + " with id " + def.getId());
		}
	}
}
