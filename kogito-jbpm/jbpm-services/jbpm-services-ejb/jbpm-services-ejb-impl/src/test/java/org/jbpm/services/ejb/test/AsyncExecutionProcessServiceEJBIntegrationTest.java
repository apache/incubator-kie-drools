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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.command.impl.RegistryContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

@RunWith(Arquillian.class)
public class AsyncExecutionProcessServiceEJBIntegrationTest extends AbstractTestSupport {

	@Deployment
	public static WebArchive createDeployment() {
		File archive = new File("target/sample-war-ejb-app.war");
		if (!archive.exists()) {
			throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
		}
		WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
		war.addPackage("org.jbpm.services.ejb.test"); // test cases
		war.addClass("org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener");
		// deploy test kjar
		deployKjar();
		
		return war;
	}
	
	protected static void deployKjar() {
		KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("processes/async-execution.bpmn2");
        
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
	}
	
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



	@EJB
	private DeploymentServiceEJBLocal deploymentService;
	
	@EJB
	private ProcessServiceEJBLocal processService;
	
	@EJB
	private RuntimeDataServiceEJBLocal runtimeDataService;
	
    
    @Test
    public void testStartProcessWithParms() throws Exception {
    	assertNotNull(deploymentService);
    	final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Task 1", 1);
        
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);
    	
    	assertNotNull(processService);
    	
        
        // register count down listener
        processService.execute(deploymentUnit.getIdentifier(), new ExecutableCommand<Void>() {

            private static final long serialVersionUID = -5416366832158798895L;

            @Override
            public Void execute(Context context) {
                KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
                ksession.addEventListener(countDownListener);
                return null;
            }
        });
    	
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("command", "org.jbpm.executor.commands.PrintOutCommand");
    	
    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "AsyncExecution", params);
    	assertNotNull(processInstanceId);
    	
    	// wait for the command to be executed
    	countDownListener.waitTillCompleted(10000);
    	
    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);    	
    	assertNull(pi);
    }
    
    
}
