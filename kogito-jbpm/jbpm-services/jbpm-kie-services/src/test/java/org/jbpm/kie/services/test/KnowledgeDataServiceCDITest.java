package org.jbpm.kie.services.test;

//package org.jbpm.kie.services.test;
//
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.Archive;
//import org.jboss.shrinkwrap.api.ArchivePaths;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.junit.After;
//import org.junit.runner.RunWith;
//
//@RunWith(Arquillian.class)
//public class KnowledgeDataServiceCDITest extends KnowledgeDataServiceBaseTest {
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
//                .addPackage("org.jbpm.kie.services.api")
//                .addPackage("org.jbpm.kie.services.api.bpmn2")
//                .addPackage("org.jbpm.kie.services.impl")
//                .addPackage("org.jbpm.kie.services.impl.bpmn2")
//                .addPackage("org.jbpm.shared.services.api")
//                .addPackage("org.jbpm.shared.services.impl")
//                .addPackage("org.jbpm.kie.services.impl.vfs")
//                .addPackage("org.kie.commons.java.nio.fs.jgit")
//                .addPackage("org.jbpm.kie.services.test")
//                .addPackage("org.jbpm.kie.services.impl.event.listeners")
//                .addPackage("org.jbpm.kie.services.impl.example") 
//                .addPackage("org.jbpm.kie.services.impl.audit")
//                .addPackage("org.jbpm.kie.services.impl.util") 
//                
//                .addPackage("org.kie.internal.runtime")
//                .addPackage("org.kie.internal.runtime.manager")
//                .addPackage("org.kie.internal.runtime.manager.cdi.qualifier")
//                .addPackage("org.jbpm.runtime.manager")
//                .addPackage("org.jbpm.runtime.manager.impl")
//                .addPackage("org.jbpm.runtime.manager.impl.cdi.qualifier")
//                .addPackage("org.jbpm.runtime.manager.impl.context")
//                .addPackage("org.jbpm.runtime.manager.impl.factory")
//                .addPackage("org.jbpm.runtime.manager.impl.jpa")
//                .addPackage("org.jbpm.runtime.manager.impl.manager")
//                .addPackage("org.jbpm.runtime.manager.mapper")
//                .addPackage("org.jbpm.runtime.manager.impl.task")
//                .addPackage("org.jbpm.runtime.manager.impl.tx")
//                
//                
//                .addAsResource("jndi.properties","jndi.properties")
//                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
//                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
//                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"))
//                .addAsManifestResource("META-INF/services/org.kie.commons.java.nio.file.spi.FileSystemProvider", ArchivePaths.create("org.kie.commons.java.nio.file.spi.FileSystemProvider"));
//
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        super.teardown();
//        int removedTasks = taskService.removeAllTasks();
//        int removedLogs = adminDataService.removeAllData();
//        System.out.println(" --> Removed Tasks = "+removedTasks + " - ");
//        System.out.println(" --> Removed Logs = "+removedLogs + " - ");
//        
//    }
//}
