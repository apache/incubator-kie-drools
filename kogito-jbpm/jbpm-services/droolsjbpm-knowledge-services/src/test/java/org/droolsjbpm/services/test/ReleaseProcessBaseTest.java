/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.droolsjbpm.services.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.droolsjbpm.services.api.Domain;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.SessionManager;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.KnowledgeDomainServiceImpl;
import org.droolsjbpm.services.impl.SimpleDomainImpl;
import org.droolsjbpm.services.impl.example.MoveFileWorkItemHandler;
import org.droolsjbpm.services.impl.example.NotificationWorkItemHandler;
import org.droolsjbpm.services.impl.example.TriggerTestsWorkItemHandler;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.query.TaskSummary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.commons.java.nio.file.Path;
import org.kie.runtime.process.ProcessInstance;

public abstract class ReleaseProcessBaseTest {

    @Inject
    protected TaskServiceEntryPoint taskService;
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    protected KnowledgeDataService dataService;
    @Inject
    protected KnowledgeAdminDataService adminDataService;
    @Inject
    private FileService fs;
    @Inject
    private SessionManager sessionManager;
    
    @Inject
    private MoveFileWorkItemHandler moveFilesWIHandler;
    
    @Inject
    private TriggerTestsWorkItemHandler triggerTestsWorkItemHandler;
    
    @Inject
    private NotificationWorkItemHandler notificationWorkItemHandler;
    

    String releasePath;
    String sourceDir;
    String stageDir;
    String productionDir;
    String testDir;
    private String goodbyeContent;
    private String helloContent;
    
    
    @Before
    public void setUp() throws IOException, FileException{
        
        releasePath = "examples/release/testdir";
        sourceDir = "origin";
        stageDir = "stage";
        productionDir = "production";
        testDir = "test";
        
        goodbyeContent = new String(fs.loadFile(releasePath+"/"+sourceDir+"/goodbye.txt"));
        helloContent = new String(fs.loadFile(releasePath+"/"+sourceDir+"/hello.txt"));
        
        this.cleanUp();
        
        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);

        Iterable<Path> loadFilesByType = null;
        try {
            loadFilesByType = fs.loadFilesByType("examples/release/", "bpmn");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Path p : loadFilesByType) {
            myDomain.addProcessDefinitionToKsession("myKsession", p);
        }
        
    }
    
    @After
    public void cleanUp() throws IOException{
        fs.deleteIfExists(releasePath+"/"+sourceDir+"/goodbye.txt");
        fs.deleteIfExists(releasePath+"/"+sourceDir+"/hello.txt");
        fs.deleteIfExists(releasePath+"/"+productionDir+"/goodbye.txt");
        fs.deleteIfExists(releasePath+"/"+productionDir+"/hello.txt");
        fs.deleteIfExists(releasePath+"/"+testDir+"/goodbye.txt");
        fs.deleteIfExists(releasePath+"/"+testDir+"/hello.txt");
        fs.deleteIfExists(releasePath+"/"+stageDir+"/goodbye.txt");
        fs.deleteIfExists(releasePath+"/"+stageDir+"/hello.txt");
        
        //recreate files
        IOUtils.write(goodbyeContent, fs.openFile(releasePath+"/"+sourceDir+"/goodbye.txt"));
        IOUtils.write(helloContent, fs.openFile(releasePath+"/"+sourceDir+"/hello.txt"));
        
    }
    
    
    
    @Test
    public void testReleaseProcess() throws FileException {
        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);

//        Iterable<Path> loadFilesByType = null;
//        try {
//            loadFilesByType = fs.loadFilesByType("examples/release/", "bpmn");
//        } catch (FileException ex) {
//            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        String kSessionName = "myKsession";
//        myDomain.addKsessionRepositoryRoot(kSessionName, "examples/release/");
//        for (Path p : loadFilesByType) {
//            
//            
//            String processString = new String( fs.loadFile(p) );
//            String processId = bpmn2Service.findProcessId( processString );
//            if(!processId.equals("")){
//              System.out.println(" >>> Loading Path -> "+p.toString());
//              myDomain.addProcessDefinitionToKsession("myKsession", p);
//              myDomain.addProcessBPMN2ContentToKsession(kSessionName, processId, processString );
//            }
//        }

        sessionManager.buildSession("myKsession", "examples/release/", false);

        sessionManager.addKsessionHandler("myKsession", "MoveToStagingArea", moveFilesWIHandler);
        sessionManager.addKsessionHandler("myKsession", "MoveToTest", moveFilesWIHandler);
        sessionManager.addKsessionHandler("myKsession", "TriggerTests", triggerTestsWorkItemHandler);
        sessionManager.addKsessionHandler("myKsession", "MoveBackToStaging", moveFilesWIHandler);
        sessionManager.addKsessionHandler("myKsession", "MoveToProduction", moveFilesWIHandler);
        sessionManager.addKsessionHandler("myKsession", "Email", notificationWorkItemHandler);

        sessionManager.registerHandlersForSession("myKsession", 1);
         
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("release_name", "first release ever");
        params.put("release_path", releasePath);
        
        ProcessInstance pI = sessionManager.getKsessionsByName("myKsession").get(1).startProcess("org.jbpm.release.process", params);
        
        // Configure Release
        List<TaskSummary> tasksAssignedByGroup = taskService.getTasksAssignedByGroup("Release Manager", "en-UK");

        assertEquals(1, tasksAssignedByGroup.size());
        TaskSummary configureReleaseTask = tasksAssignedByGroup.get(0);

        taskService.claim(configureReleaseTask.getId(), "salaboy");

        taskService.start(configureReleaseTask.getId(), "salaboy");
        
        Map<String, Object> taskContent = taskService.getTaskContent(configureReleaseTask.getId());

        assertEquals("first release ever", taskContent.get("release_name"));
        
        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings("org.jbpm.release.process", configureReleaseTask.getName());
        
        assertEquals(1, taskOutputMappings.size());
        assertEquals("files_output", taskOutputMappings.values().iterator().next());
            
        Map<String, Object> output = new HashMap<String, Object>();
        String files = "goodbye.txt, hello.txt";
        output.put("files_output", files);
        taskService.complete(configureReleaseTask.getId(), "salaboy", output);

        // Review and Confirm Release Setup 
        
        tasksAssignedByGroup = taskService.getTasksAssignedByGroup("Release Manager", "en-UK");
        assertEquals(1, tasksAssignedByGroup.size());
        TaskSummary confirmConfigurationTask = tasksAssignedByGroup.get(0);

        taskService.claim(confirmConfigurationTask.getId(), "salaboy");

        taskService.start(confirmConfigurationTask.getId(), "salaboy");
        
        taskContent = taskService.getTaskContent(confirmConfigurationTask.getId());
        
        
        
        assertEquals(2, ((String)taskContent.get("in_files")).split(",").length);
        
        params = new HashMap<String, Object>();
        params.put("out_selected_files", files);
        params.put("out_dueDate", new Date());
        params.put("out_confirmed", true);
        
        taskService.complete(confirmConfigurationTask.getId(), "salaboy", params);
        
        
        
        
    }
    
    
}
