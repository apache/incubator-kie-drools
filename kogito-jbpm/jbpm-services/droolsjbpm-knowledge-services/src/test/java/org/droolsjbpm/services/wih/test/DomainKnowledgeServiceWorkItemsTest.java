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
package org.droolsjbpm.services.wih.test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.droolsjbpm.services.api.Domain;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.ServicesSessionManager;
import org.droolsjbpm.services.impl.KnowledgeDomainServiceImpl;
import org.droolsjbpm.services.impl.SimpleDomainImpl;
import org.droolsjbpm.services.impl.example.MoveFileWorkItemHandler;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.commons.java.nio.file.Path;
import org.kie.runtime.process.WorkflowProcessInstance;

public abstract class DomainKnowledgeServiceWorkItemsTest {

    @Inject
    protected TaskServiceEntryPoint taskService;
    @Inject
    protected KnowledgeDataService dataService;
    @Inject
    protected KnowledgeAdminDataService adminDataService;
    @Inject
    private FileService fs;
    @Inject
    private ServicesSessionManager sessionManager;
    
    @Inject
    private MoveFileWorkItemHandler moveFileWorkItemHandler;

    String releasePath;
    String sourceDir;
    String targetDir;
    
    @Before
    public void setUp() throws IOException, FileException{
        
        releasePath = "processes/releaseDir";
        sourceDir = "origin";
        targetDir = "stage-"+UUID.randomUUID().toString();
        
        this.cleanUp();
        fs.createDirectory(releasePath+"/"+sourceDir);
        fs.createDirectory(releasePath+"/"+targetDir);
        
        OutputStream os = fs.openFile(releasePath+"/"+sourceDir+"/file1.txt");
        IOUtils.write("Hello World", os); 
        os.close();
        
        os = fs.openFile(releasePath+"/"+sourceDir+"/file2.txt");
        IOUtils.write("Bye World", os); 
        os.close();
        
        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);

//        Iterable<Path> loadFilesByType = null;
//        try {
//            loadFilesByType = fs.loadFilesByType("processes/release/", "bpmn");
//        } catch (FileException ex) {
//            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        for (Path p : loadFilesByType) {
//            myDomain.addProcessDefinitionToKsession("myKsession", p);
//        }
        
        sessionManager.buildSession("myKsession", "processes/release/", false);

        sessionManager.addKsessionHandler("myKsession", "MoveFile", moveFileWorkItemHandler);

        sessionManager.registerHandlersForSession("myKsession", 1);
    }
    
    @After
    public void cleanUp(){
        fs.deleteIfExists(releasePath+"/"+sourceDir+"/file1.txt");
        fs.deleteIfExists(releasePath+"/"+sourceDir+"/file2.txt");
        fs.deleteIfExists(releasePath+"/"+targetDir+"/file1.txt");
        fs.deleteIfExists(releasePath+"/"+targetDir+"/file2.txt");
        fs.deleteIfExists(releasePath+"/"+targetDir);
        fs.deleteIfExists(releasePath+"/"+sourceDir);
        fs.deleteIfExists(releasePath);
    }
    
    @Test
    public void testMoveFile() throws FileException {
        
        String files = "file1.txt, file2.txt";
        
        //Check that the files don't exist before the process
        Iterable<Path> targetFiles = fs.loadFilesByType(releasePath+"/"+targetDir, "txt");
        List<String> existingFiles = this.pathIteratorAsStringList(targetFiles.iterator());
        Assert.assertFalse(existingFiles.contains("file1.txt"));
        Assert.assertFalse(existingFiles.contains("file2.txt"));

        //Start the process
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("source", sourceDir);
        params.put("target", targetDir);
        params.put("release", releasePath);
        params.put("files", files);
        
        
        WorkflowProcessInstance pI = (WorkflowProcessInstance) sessionManager.getKsessionsByName("myKsession").get(1).startProcess("MoveFileWorkItemHandlerTest", params);
        
        //The files should be there now
        targetFiles = fs.loadFilesByType(releasePath+"/"+targetDir, "txt");
        existingFiles = this.pathIteratorAsStringList(targetFiles.iterator());
        Assert.assertTrue(existingFiles.contains("file1.txt"));
        Assert.assertTrue(existingFiles.contains("file2.txt"));
        
        //no errors
        Assert.assertEquals("", pI.getVariable("errors"));
        
    }

    
    private List<String> pathIteratorAsStringList(Iterator<Path> iterator){
        List<String> results = new ArrayList();
        
        if (iterator != null && iterator.hasNext()){
            while (iterator.hasNext()) {
                results.add(iterator.next().getFileName().toString());
            }
        }
        
        return results;
    }
}
