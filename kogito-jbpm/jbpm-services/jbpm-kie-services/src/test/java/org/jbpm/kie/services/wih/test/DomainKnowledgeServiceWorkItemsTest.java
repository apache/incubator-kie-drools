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
package org.jbpm.kie.services.wih.test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.impl.VFSDeploymentUnit;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.commons.java.nio.file.Path;
import org.kie.internal.runtime.manager.context.EmptyContext;


public abstract class DomainKnowledgeServiceWorkItemsTest {

    @Inject
    private FileService fs;
    
    String releasePath;
    String sourceDir;
    String targetDir;
    
    
    @Inject
    private DeploymentService deploymentService;

    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    
    @Before
    public void setUp() throws IOException, FileException{
        
        releasePath = "processes/releaseDir";
        sourceDir = "origin";
        targetDir = "stage-"+UUID.randomUUID().toString();
        
        this.cleanUp();
        fs.createDirectory(fs.getPath(releasePath+"/"+sourceDir));
        fs.createDirectory(fs.getPath(releasePath+"/"+targetDir));
        
        OutputStream os = fs.openFile(fs.getPath(releasePath+"/"+sourceDir+"/file1.txt"));
        IOUtils.write("Hello World", os); 
        os.close();
        
        os = fs.openFile(fs.getPath(releasePath+"/"+sourceDir+"/file2.txt"));
        IOUtils.write("Bye World", os); 
        os.close();

    }
    
    @After
    public void cleanUp(){
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
        
        fs.deleteIfExists(fs.getPath(releasePath+"/"+sourceDir+"/file1.txt"));
        fs.deleteIfExists(fs.getPath(releasePath+"/"+sourceDir+"/file2.txt"));
        fs.deleteIfExists(fs.getPath(releasePath+"/"+targetDir+"/file1.txt"));
        fs.deleteIfExists(fs.getPath(releasePath+"/"+targetDir+"/file2.txt"));
        fs.deleteIfExists(fs.getPath(releasePath+"/"+targetDir));
        fs.deleteIfExists(fs.getPath(releasePath+"/"+sourceDir));
        fs.deleteIfExists(fs.getPath(releasePath));
    }
    
    @Test
    public void testMoveFile() throws FileException {
                
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new VFSDeploymentUnit("release", "", "processes/release");        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        String files = "file1.txt, file2.txt";
        
        //Check that the files don't exist before the process
        Iterable<Path> targetFiles = fs.loadFilesByType(fs.getPath(releasePath+"/"+targetDir), "txt");
        List<String> existingFiles = this.pathIteratorAsStringList(targetFiles.iterator());
        Assert.assertFalse(existingFiles.contains("file1.txt"));
        Assert.assertFalse(existingFiles.contains("file2.txt"));

        //Start the process
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("source", sourceDir);
        params.put("target", targetDir);
        params.put("release", releasePath);
        params.put("files", files);
        
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        WorkflowProcessInstance pI = (WorkflowProcessInstance) engine.getKieSession().startProcess("MoveFileWorkItemHandlerTest", params);
        
        //The files should be there now
        targetFiles = fs.loadFilesByType(fs.getPath(releasePath+"/"+targetDir), "txt");
        existingFiles = this.pathIteratorAsStringList(targetFiles.iterator());
        Assert.assertTrue(existingFiles.contains("file1.txt"));
        Assert.assertTrue(existingFiles.contains("file2.txt"));
        
        //no errors
        Assert.assertEquals("", pI.getVariable("errors"));
        
    }

    
    private List<String> pathIteratorAsStringList(Iterator<Path> iterator){
        List<String> results = new ArrayList<String>();
        
        if (iterator != null && iterator.hasNext()){
            while (iterator.hasNext()) {
                results.add(iterator.next().getFileName().toString());
            }
        }
        
        return results;
    }
}
