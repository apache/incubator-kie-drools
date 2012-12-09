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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.droolsjbpm.services.api.Domain;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.SessionManager;
import org.droolsjbpm.services.impl.KnowledgeDomainServiceImpl;
import org.droolsjbpm.services.impl.SimpleDomainImpl;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.junit.Assert;
import org.junit.Test;

import org.kie.commons.java.nio.file.Path;
import org.kie.runtime.process.ProcessInstance;

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
    private SessionManager sessionManager;
    
    @Inject
    private MoveFileWorkItemHandler moveFileWorkItemHandler;

    @Test
    public void testMoveFile() throws FileException {
        
        String releasePath = "examples/releaseDir";
        String sourceDir = "origin";
        String targetDir = "stage-"+UUID.randomUUID().toString();

        fs.createDirectory(releasePath+"/"+targetDir);
        
        String files = "file1.txt, file2.txt";
        
        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);

        Iterable<Path> loadFilesByType = null;
        try {
            loadFilesByType = fs.loadFilesByType("examples/release/", "bpmn");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Path p : loadFilesByType) {
            myDomain.addKsessionAsset("myKsession", p);
        }
        
        Iterable<Path> targetFiles = fs.loadFilesByType(releasePath+"/"+targetDir, "txt");
        List<String> existingFiles = this.pathIteratorAsStringList(targetFiles.iterator());
        Assert.assertFalse(existingFiles.contains("file1.txt"));
        Assert.assertFalse(existingFiles.contains("file2.txt"));
        
        
        
        sessionManager.buildSessions();

        sessionManager.addKsessionHandler("myKsession", "MoveFile", moveFileWorkItemHandler);

        sessionManager.registerHandlersForSession("myKsession");
         
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("source", sourceDir);
        params.put("target", targetDir);
        params.put("release", releasePath);
        params.put("files", files);
        
        
        ProcessInstance pI = sessionManager.getKsessionByName("myKsession").startProcess("MoveFileWorkItemHandlerTest", params);
        
        
        targetFiles = fs.loadFilesByType(releasePath+"/"+targetDir, "txt");
        existingFiles = this.pathIteratorAsStringList(targetFiles.iterator());
        Assert.assertTrue(existingFiles.contains("file1.txt"));
        Assert.assertTrue(existingFiles.contains("file2.txt"));
        
        
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
