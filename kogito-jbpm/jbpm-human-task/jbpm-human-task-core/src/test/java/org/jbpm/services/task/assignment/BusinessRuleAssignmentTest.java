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

package org.jbpm.services.task.assignment;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.assignment.impl.strategy.BusinessRuleAssignmentStrategy;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.test.util.PoolingDataSource;
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
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class BusinessRuleAssignmentTest extends AbstractAssignmentTest {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessRuleAssignmentTest.class); 

    private static final String GROUP_ID = "org.jbpm.task";
    private static final String ARTIFACT_ID = "assignment-rules";    
    private static final String VERSION = "1.0";
    
	private PoolingDataSource pds;
	private EntityManagerFactory emf;	

	@Before
	public void setup() {
	    System.setProperty("org.jbpm.task.assignment.enabled", "true");
	    System.setProperty("org.jbpm.task.assignment.strategy", "BusinessRule");
	    System.setProperty("org.jbpm.task.assignment.rules.releaseId", GROUP_ID + ":" + ARTIFACT_ID +":" + VERSION);

	    buildKJar();
	    
		pds = setupPoolingDataSource();
		emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );
		
		AssignmentServiceProvider.override(new BusinessRuleAssignmentStrategy());
		
	
		this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
												.entityManagerFactory(emf)
												.getTaskService();
	
	}
	
	@After
	public void clean() {
	    System.clearProperty("org.jbpm.task.assignment.enabled");
	    System.clearProperty("org.jbpm.task.assignment.rules.releaseId");
	    System.clearProperty("org.jbpm.task.assignment.rules.scan");
	    System.clearProperty("org.jbpm.task.assignment.rules.query");
	    
	    AssignmentServiceProvider.clear();
		if (emf != null) {
			emf.close();
		}
		if (pds != null) {
			pds.close();
		}		
	}
	
    @Test
    public void testAssignmentAlwaysAssignToBobbaFet() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')  ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'Bobbas tasks' })";
        
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage");        
        // another task
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
        // yet another task
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
    }
    
    @Test
    public void testAssignmentPriorityBased() {
        String str = "(with (new Task()) { priority = 4, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')  ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'Low priority' })";
        
        createAndAssertTask(str, "Luke Cage", 3, "Bobba Fet", "Darth Vader", "Luke Cage");        
        // another task
        createAndAssertTask(str, "Luke Cage", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
        
        
        String str2 = "(with (new Task()) { priority = 10, taskData = (with( new TaskData()) { } ), ";
        str2 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')  ],businessAdministrators = [ new User('Administrator') ],}),";
        str2 += "name =  'High priority' })";
        // yet another task
        createAndAssertTask(str2, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage");
        
        createAndAssertTask(str2, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage");
    }
    
    @Test
    public void testAssignmentAlwaysAssignToBobbaFetRetrieveAssignmentsViaQuery() {
        // here two rules will match but we select only those for Darth Vader via query
        System.setProperty("org.jbpm.task.assignment.rules.query", "loadDarthAssignments");
        String str = "(with (new Task()) { priority = 9, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')  ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'Bobbas tasks' })";
        
        createAndAssertTask(str, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage");        
        // another task
        createAndAssertTask(str, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
        // yet another task
        createAndAssertTask(str, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
    }

    @Test
    public void testAssignmentAlwaysToBobbaFetWithPriorityBasedForDarthVader() throws Exception {
	    // Use priority that would match "High priority to Darth Vader" rule aswell
        String str = "(with (new Task()) { priority = 10, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')  ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'Bobbas tasks' })";

        // Should be "Bobba Fet" since we didn't specify a query to filter applied assignments and rule for Bobba fired first
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage");
        System.setProperty("org.jbpm.task.assignment.rules.query", "loadDarthAssignments");
        createAndAssertTask(str, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage");
        System.clearProperty("org.jbpm.task.assignment.rules.query");
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage");
    }

    @Test
    public void testRetrieveAssignmentsViaNonExistingQuery() throws Exception {
        System.setProperty("org.jbpm.task.assignment.rules.query", "loadDarthAssignments-howaboutno?");
        String str = "(with (new Task()) { priority = 10, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')  ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'Bobbas tasks' })";

        try {
            createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage");
            Assertions.fail("Should have thrown an exception");
        } catch (RuntimeException ex) {
            Assertions.assertThat(ex.getMessage()).contains("loadDarthAssignments-howaboutno?");
        }

    }

    @Test
    public void testAssignmentsWhenNoRuleWasFired() throws Exception {
        String str = "(with (new Task()) { priority = 51, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Ben Dover') ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'Not Bobbas tasks' })";

        Task task = TaskFactory.evalTask(new StringReader(str));
        assertPotentialOwners(task, 1);

        long taskId = taskService.addTask(task, Collections.emptyMap());

        task = taskService.getTaskById(taskId);
        assertPotentialOwners(task, 0);
        User actualOwner = task.getTaskData().getActualOwner();
        Assertions.assertThat(actualOwner).isNull();
    }

    protected void buildKJar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        
        List<String> resources = new ArrayList<>();
        resources.add("assignments/assignment-rules.drl");
        
        createKieJar(ks, releaseId, resources);
    }

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
            kfs.write("src/main/resources/rules/" + resource, ResourceFactory.newClassPathResource(resource));
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

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("defaultKieBase")
                .setDefault(true)
                .addPackage("*")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

    
        kieBaseModel1.newKieSessionModel("defaultKieSession")
                .setDefault(true)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") );
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }
}
