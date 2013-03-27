///*
// * Copyright 2012 JBoss by Red Hat.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.droolsjbpm.services.test;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Map;
//
//import javax.inject.Inject;
//
//import org.droolsjbpm.services.api.KnowledgeDomainService;
//import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.Archive;
//import org.jboss.shrinkwrap.api.ArchivePaths;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.jbpm.services.task.impl.model.TaskDefImpl;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import bitronix.tm.resource.jdbc.PoolingDataSource;
//
///**
// *
// * @author salaboy
// */
//@RunWith(Arquillian.class)
//public class BPMN2DataServicesTest {
//
//    @Deployment()
//    public static Archive<?> createDeployment() {
//        return ShrinkWrap.create(JavaArchive.class, "droolsjbpm-knowledge-services.jar")
//                .addPackage("org.jboss.seam.persistence") //seam-persistence
//                .addPackage("org.jboss.seam.transaction") //seam-persistence
//                .addPackage("org.jbpm.services.task")
//                .addPackage("org.jbpm.services.task.wih") // work items org.jbpm.services.task.wih
//                .addPackage("org.jbpm.services.task.annotations")
//                .addPackage("org.jbpm.services.task.api")
//                .addPackage("org.jbpm.services.task.impl")
//                .addPackage("org.jbpm.services.task.events")
//                .addPackage("org.jbpm.services.task.exception")
//                .addPackage("org.jbpm.services.task.identity")
//                .addPackage("org.jbpm.services.task.factories")
//                .addPackage("org.jbpm.services.task.internals")
//                .addPackage("org.jbpm.services.task.internals.lifecycle")
//                .addPackage("org.jbpm.services.task.lifecycle.listeners")
//                .addPackage("org.jbpm.services.task.query")
//                .addPackage("org.jbpm.services.task.util")
//                .addPackage("org.jbpm.services.task.commands") // This should not be required here
//                .addPackage("org.jbpm.services.task.deadlines") // deadlines
//                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
//                .addPackage("org.jbpm.services.task.subtask")
//                .addPackage("org.droolsjbpm.services.api")
//                .addPackage("org.droolsjbpm.services.api.bpmn2")
//                .addPackage("org.droolsjbpm.services.impl")
//                .addPackage("org.droolsjbpm.services.impl.bpmn2")
//                .addPackage("org.droolsjbpm.services.impl.vfs")
//                .addPackage("org.jbpm.shared.services.api")
//                .addPackage("org.jbpm.shared.services.impl")
//                .addPackage("org.kie.commons.java.nio.fs.jgit")
//                .addPackage("org.droolsjbpm.services.test")
//                .addPackage("org.droolsjbpm.services.impl.event.listeners")
//                .addPackage("org.droolsjbpm.services.impl.example") 
//                .addPackage("org.droolsjbpm.services.impl.audit")
//                .addPackage("org.droolsjbpm.services.impl.util") 
//                
//                  .addPackage("org.kie.internal.runtime")
//                .addPackage("org.kie.internal.runtime.manager")
//                .addPackage("org.kie.internal.runtime.manager.cdi.qualifier")
//                
//                .addPackage("org.jbpm.runtime.manager")
//                .addPackage("org.jbpm.runtime.manager.impl")
//                .addPackage("org.jbpm.runtime.manager.impl.cdi.qualifier")
//                
//                .addPackage("org.jbpm.runtime.manager.impl.context")
//                .addPackage("org.jbpm.runtime.manager.impl.factory")
//                .addPackage("org.jbpm.runtime.manager.impl.jpa")
//                .addPackage("org.jbpm.runtime.manager.impl.manager")
//                .addPackage("org.jbpm.runtime.manager.mapper")
//                .addPackage("org.jbpm.runtime.manager.impl.task")
//                .addPackage("org.jbpm.runtime.manager.impl.tx")
//                
//                
//                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
//                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
//                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"))
//                .addAsManifestResource("META-INF/services/org.kie.commons.java.nio.file.spi.FileSystemProvider", ArchivePaths.create("org.kie.commons.java.nio.file.spi.FileSystemProvider"));
//
//    }
//    @Inject
//    private KnowledgeDomainService knolwedgeService;
//    @Inject
//    private BPMN2DataService bpmn2Service;
//
//       
//    public BPMN2DataServicesTest() {
//    }
//
//    @Test
//    public void hello() throws IOException {
//      
//        knolwedgeService.createDomain();
//      
//        String theString = "org.jbpm.writedocument";
//        
//
//        Collection<TaskDefImpl> processTasks = bpmn2Service.getAllTasksDef(theString);
//        
//        assertEquals(3, processTasks.size());
//        Map<String, String> processData = bpmn2Service.getProcessData(theString);
//        
//        assertEquals(3, processData.keySet().size());
//        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(theString, "Write a Document" );
//        
//        assertEquals(3, taskInputMappings.keySet().size());
//        
//        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(theString, "Write a Document" );
//        
//        assertEquals(1, taskOutputMappings.keySet().size());
//        
//    }
//    
//    @Test
//    public void testFindReusableSubProcesses() {
//      
//        knolwedgeService.createDomain();
//        String theString = "ParentProcess";
//        
//        assertNotNull(theString);
//        Collection<String> reusableProcesses = bpmn2Service.getReusableSubProcesses(theString);
//        assertNotNull(reusableProcesses);
//        assertEquals(1, reusableProcesses.size());
//        
//        assertEquals("signal", reusableProcesses.iterator().next());
//    }
//}
