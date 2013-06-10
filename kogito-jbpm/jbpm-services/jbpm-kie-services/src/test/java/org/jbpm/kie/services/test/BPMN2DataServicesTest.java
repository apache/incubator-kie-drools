/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.kie.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.api.Vfs;
import org.jbpm.kie.services.api.bpmn2.BPMN2DataService;
import org.jbpm.kie.services.impl.VFSDeploymentUnit;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.services.task.impl.model.TaskDefImpl;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author salaboy
 */
@RunWith(Arquillian.class)
public class BPMN2DataServicesTest {

    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "domain-services.jar")
                .addPackage("org.jboss.seam.persistence") //seam-persistence
                .addPackage("org.jboss.seam.transaction") //seam-persistence
                .addPackage("org.jbpm.services.task")
                .addPackage("org.jbpm.services.task.wih") // work items org.jbpm.services.task.wih
                .addPackage("org.jbpm.services.task.annotations")
                .addPackage("org.jbpm.services.task.api")
                .addPackage("org.jbpm.services.task.impl")
                .addPackage("org.jbpm.services.task.events")
                .addPackage("org.jbpm.services.task.exception")
                .addPackage("org.jbpm.services.task.identity")
                .addPackage("org.jbpm.services.task.factories")
                .addPackage("org.jbpm.services.task.internals")
                .addPackage("org.jbpm.services.task.internals.lifecycle")
                .addPackage("org.jbpm.services.task.lifecycle.listeners")
                .addPackage("org.jbpm.services.task.query")
                .addPackage("org.jbpm.services.task.util")
                .addPackage("org.jbpm.services.task.commands") // This should not be required here
                .addPackage("org.jbpm.services.task.deadlines") // deadlines
                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.services.task.subtask")
                .addPackage("org.jbpm.services.task.rule")
                .addPackage("org.jbpm.services.task.rule.impl")

                .addPackage("org.kie.internal.runtime.manager")
                .addPackage("org.kie.internal.runtime.manager.context")
                .addPackage("org.kie.internal.runtime.manager.cdi.qualifier")
                
                .addPackage("org.jbpm.runtime.manager.impl")
                .addPackage("org.jbpm.runtime.manager.impl.cdi")                               
                .addPackage("org.jbpm.runtime.manager.impl.factory")
                .addPackage("org.jbpm.runtime.manager.impl.jpa")
                .addPackage("org.jbpm.runtime.manager.impl.manager")
                .addPackage("org.jbpm.runtime.manager.impl.task")
                .addPackage("org.jbpm.runtime.manager.impl.tx")
                
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                
                .addPackage("org.jbpm.kie.services.api")
                .addPackage("org.jbpm.kie.services.impl")
                .addPackage("org.jbpm.kie.services.api.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.event.listeners")
                .addPackage("org.jbpm.kie.services.impl.audit")
                .addPackage("org.jbpm.kie.services.impl.util")
                
                .addPackage("org.jbpm.kie.services.impl.vfs")
                
                .addPackage("org.jbpm.kie.services.impl.example")
                .addPackage("org.kie.commons.java.nio.fs.jgit")
                .addPackage("org.jbpm.kie.services.test") // Identity Provider Test Impl here
                .addAsResource("jndi.properties", "jndi.properties")
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

    }
    @Inject
    @Vfs
    private DeploymentService deploymentService;
    @Inject
    private BPMN2DataService bpmn2Service;

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    @After
    public void cleanup() {
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
    }

    @Test
    public void testHumanTaskProcess() throws IOException {
      
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new VFSDeploymentUnit("general", "", "processes/general");
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
      
        String theString = "org.jbpm.writedocument";
        

        Collection<TaskDefImpl> processTasks = bpmn2Service.getAllTasksDef(theString);
        
        assertEquals(3, processTasks.size());
        Map<String, String> processData = bpmn2Service.getProcessData(theString);
        
        assertEquals(3, processData.keySet().size());
        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(theString, "Write a Document" );
        
        assertEquals(3, taskInputMappings.keySet().size());
        
        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(theString, "Write a Document" );
        
        assertEquals(1, taskOutputMappings.keySet().size());
        
    }
    
    @Test
    public void testFindReusableSubProcesses() {
      
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new VFSDeploymentUnit("general", "", "processes/general");
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        String theString = "ParentProcess";
        
        assertNotNull(theString);
        Collection<String> reusableProcesses = bpmn2Service.getReusableSubProcesses(theString);
        assertNotNull(reusableProcesses);
        assertEquals(1, reusableProcesses.size());
        
        assertEquals("signal", reusableProcesses.iterator().next());
    }
}
