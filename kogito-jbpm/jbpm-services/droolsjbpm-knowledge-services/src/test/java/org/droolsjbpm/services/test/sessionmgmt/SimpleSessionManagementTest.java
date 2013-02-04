/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.droolsjbpm.services.test.sessionmgmt;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.droolsjbpm.services.api.Domain;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.api.SessionManager;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.SimpleDomainImpl;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.query.TaskSummary;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.kie.runtime.process.WorkflowProcessInstance;

/**
 *
 * @author salaboy
 */

@RunWith(Arquillian.class)
public class SimpleSessionManagementTest {
  
  public SimpleSessionManagementTest() {
  }
  
  @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "droolsjbpm-knowledge-services.jar")
                .addPackage("org.jboss.seam.persistence") //seam-persistence
                .addPackage("org.jboss.seam.transaction") //seam-persistence
                .addPackage("org.jbpm.task")
                .addPackage("org.jbpm.task.wih") // work items org.jbpm.task.wih
                .addPackage("org.jbpm.task.annotations")
                .addPackage("org.jbpm.task.api")
                .addPackage("org.jbpm.task.impl")
                .addPackage("org.jbpm.task.events")
                .addPackage("org.jbpm.task.exception")
                .addPackage("org.jbpm.task.identity")
                .addPackage("org.jbpm.task.factories")
                .addPackage("org.jbpm.task.internals")
                .addPackage("org.jbpm.task.internals.lifecycle")
                .addPackage("org.jbpm.task.lifecycle.listeners")
                .addPackage("org.jbpm.task.query")
                .addPackage("org.jbpm.task.util")
                .addPackage("org.jbpm.task.commands") // This should not be required here
                .addPackage("org.jbpm.task.deadlines") // deadlines
                .addPackage("org.jbpm.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.task.subtask")
                .addPackage("org.droolsjbpm.services.api")
                .addPackage("org.droolsjbpm.services.api.bpmn2")
                .addPackage("org.droolsjbpm.services.impl")
                .addPackage("org.droolsjbpm.services.impl.bpmn2")
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                .addPackage("org.droolsjbpm.services.impl.vfs")
                .addPackage("org.kie.commons.java.nio.fs.jgit")
                .addPackage("org.droolsjbpm.services.test")
                .addPackage("org.droolsjbpm.services.impl.event.listeners")
                .addPackage("org.droolsjbpm.services.impl.example") 
                .addPackage("org.droolsjbpm.services.impl.util") 
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"))
                .addAsManifestResource("META-INF/services/org.kie.commons.java.nio.file.spi.FileSystemProvider", ArchivePaths.create("org.kie.commons.java.nio.file.spi.FileSystemProvider"));

    }
    
     private static PoolingDataSource ds = new PoolingDataSource();
    
    @BeforeClass
    public static void setUpClass(){
        ds.setUniqueName("jdbc/testDS1");


          //NON XA CONFIGS
          ds.setClassName("org.h2.jdbcx.JdbcDataSource");
          ds.setMaxPoolSize(3);
          ds.setAllowLocalTransactions(true);
          ds.getDriverProperties().put("user", "sa");
          ds.getDriverProperties().put("password", "sasa");
          ds.getDriverProperties().put("URL", "jdbc:h2:mem:mydb");

          ds.init();
        
    }
    
    @After
    public void tearDown() throws Exception {
        int removedTasks = taskService.removeAllTasks();
        int removedLogs = adminDataService.removeAllData();
        System.out.println(" --> Removed Tasks = "+removedTasks + " - ");
        System.out.println(" --> Removed Logs = "+removedLogs + " - ");
       
    }
    
    @AfterClass
    public static void tearDownClass(){
       ds.close();
    }

    @Inject
    protected TaskServiceEntryPoint taskService;
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    protected KnowledgeDataService dataService;
    @Inject
    protected KnowledgeAdminDataService adminDataService;

    @Inject
    private SessionManager sessionManager;
    
    
   @Test
   public void supportProcessSessionCreation() throws FileException {
        Domain myDomain = new SimpleDomainImpl("myDomain");
        
        sessionManager.setDomain(myDomain);
        int firstSessionId = sessionManager.buildSession("supportKsession","examples/support/", false);
        
        
        sessionManager.registerHandlersForSession("supportKsession",firstSessionId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", "Salaboy");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)sessionManager.getKsessionsByName("supportKsession").get(firstSessionId).startProcess("support.process", params);
        
        
        assertNotNull(processInstance);
        
        assertEquals(processInstance.getVariable("customer"), "Salaboy");
        List<Integer> supportSessionsIds = sessionManager.getSessionIdsByName("supportKsession");
        
        
        assertEquals(1, supportSessionsIds.size());
        
        int secondSessionId = sessionManager.buildSession("supportKsession","examples/support/", false);
        
        sessionManager.registerHandlersForSession("supportKsession", secondSessionId);
        
        supportSessionsIds = sessionManager.getSessionIdsByName("supportKsession");
        
        assertEquals(2, supportSessionsIds.size());
        
        
        List<TaskSummary> salaboysTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        
        assertEquals(1, salaboysTasks.size());
        long processInstanceId = salaboysTasks.get(0).getProcessInstanceId();
        long taskIdSessionOne = salaboysTasks.get(0).getId();
        assertEquals(processInstance.getId(), processInstanceId);
        int sessionForProcessInstanceId = sessionManager.getSessionForProcessInstanceId(processInstanceId);
        
        assertEquals(firstSessionId, sessionForProcessInstanceId);
        Collection<String> sessionsNames = sessionManager.getAllSessionsNames();
        
        assertEquals(1, sessionsNames.size());
        Collection<ProcessDesc> processes = dataService.getProcesses();
        
        assertEquals(2, processes.size());
        
        params = new HashMap<String, Object>();
        params.put("customer", "Salaboy2");
        processInstance = (WorkflowProcessInstance)sessionManager.getKsessionById(secondSessionId).startProcess("support.process", params);
        
        
        
        salaboysTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertEquals(2, salaboysTasks.size());
        
        processInstanceId = salaboysTasks.get(1).getProcessInstanceId();
        long taskIdSessionTwo = salaboysTasks.get(1).getId();
        assertEquals(processInstance.getId(), processInstanceId);
        
        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("customer", "Modified 2");
        
        Map<String, Object> params1 = new HashMap<String, Object>();
        params2.put("customer", "Modified 1");
        
        taskService.start(taskIdSessionTwo, "salaboy");
        taskService.complete(taskIdSessionTwo, "salaboy" , params2);
        taskService.start(taskIdSessionOne, "salaboy");
        taskService.complete(taskIdSessionOne, "salaboy" , params1);
        
        salaboysTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        
        assertEquals(2, salaboysTasks.size());
        
        for(TaskSummary t : salaboysTasks){
          System.out.println("Process Instance ID: "+ t.getProcessInstanceId());
          System.out.println("Process Session ID: "+ t.getProcessSessionId());
          System.out.println("Name: "+ t.getName());
        }
        
   }
   
   
}
