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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.jbpm.services.ejb.api.DefinitionServiceEJBLocal;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

@RunWith(Arquillian.class)
public class DefinitionServiceEJBIntegrationTest extends AbstractTestSupport {

	@Deployment
	public static WebArchive createDeployment() {

		File archive = new File("target/sample-war-ejb-app.war");
		if (!archive.exists()) {
			throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
		}
		WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
		war.addPackage("org.jbpm.services.ejb.test"); // test cases
		// deploy test kjar
		deployKjar();
		
		return war;
	}
	
	protected static void deployKjar() {
		KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("processes/hiring.bpmn2");
        processes.add("processes/customtask.bpmn");
        processes.add("processes/humanTask.bpmn");
        processes.add("processes/signal.bpmn");
        processes.add("processes/import.bpmn");
        processes.add("processes/callactivity.bpmn");
        processes.add("processes/itemrefissue.bpmn");
        
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
	private DefinitionServiceEJBLocal bpmn2Service;
	
	@EJB
	private DeploymentServiceEJBLocal deploymentService;
	
    @Test
    public void testHumanTaskProcess() throws IOException {
      
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
      
        String processId = "org.jbpm.writedocument";
        

        Collection<UserTaskDefinition> processTasks = bpmn2Service.getTasksDefinitions(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(3, processTasks.size());
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(3, processData.keySet().size());
        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(deploymentUnit.getIdentifier(), processId, "Write a Document" );
        
        assertEquals(3, taskInputMappings.keySet().size());
        
        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentUnit.getIdentifier(), processId, "Write a Document" );
        
        assertEquals(1, taskOutputMappings.keySet().size());
        
        Map<String, Collection<String>> associatedEntities = bpmn2Service.getAssociatedEntities(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(3, associatedEntities.keySet().size());
        
        
    }
    
    @Test
    public void testHiringProcessData() throws IOException {
      
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
      
        String processId = "hiring";
        

        Collection<UserTaskDefinition> processTasks = bpmn2Service.getTasksDefinitions(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(4, processTasks.size());
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(9, processData.keySet().size());
        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(deploymentUnit.getIdentifier(), processId, "HR Interview" );
        
        assertEquals(4, taskInputMappings.keySet().size());
        assertEquals("java.lang.String", taskInputMappings.get("TaskName"));
        assertEquals("Object", taskInputMappings.get("GroupId"));
        assertEquals("Object", taskInputMappings.get("Comment"));
        assertEquals("String", taskInputMappings.get("in_name"));
        
        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentUnit.getIdentifier(), processId, "HR Interview" );
        
        assertEquals(4, taskOutputMappings.keySet().size());
        assertEquals("String", taskOutputMappings.get("out_name"));
        assertEquals("Integer", taskOutputMappings.get("out_age"));
        assertEquals("String", taskOutputMappings.get("out_mail"));
        assertEquals("Integer", taskOutputMappings.get("out_score"));
        
        Map<String, Collection<String>> associatedEntities = bpmn2Service.getAssociatedEntities(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(4, associatedEntities.keySet().size());
        
        Map<String, String> allServiceTasks = bpmn2Service.getServiceTasks(deploymentUnit.getIdentifier(), processId);
        assertEquals(2, allServiceTasks.keySet().size());
        
        
    }
    
    @Test
    public void testFindReusableSubProcesses() {
      
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        String theString = "ParentProcess";
        
        assertNotNull(theString);
        Collection<String> reusableProcesses = bpmn2Service.getReusableSubProcesses(deploymentUnit.getIdentifier(), theString);
        assertNotNull(reusableProcesses);
        assertEquals(1, reusableProcesses.size());
        
        assertEquals("signal", reusableProcesses.iterator().next());
    }
    
    @Test
    public void itemRefIssue(){
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        String processId = "itemrefissue";
        

        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        assertNotNull(processData);
        
    }
    
    @Test
    public void testHumanTaskProcessBeforeAndAfterUndeploy() throws IOException {
      
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
      
        String processId = "org.jbpm.writedocument";
        
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), processId);
        assertNotNull(procDef);
        
        assertEquals(procDef.getId(), "org.jbpm.writedocument");
        assertEquals(procDef.getName(), "humanTaskSample");
        assertEquals(procDef.getKnowledgeType(), "PROCESS");
        assertEquals(procDef.getPackageName(), "defaultPackage");
        assertEquals(procDef.getType(), "RuleFlow");
        assertEquals(procDef.getVersion(), "1");
        
        // now let's undeploy the unit
        deploymentService.undeploy(deploymentUnit);
        
        procDef = bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), processId);
        assertNull(procDef);
    }
}
