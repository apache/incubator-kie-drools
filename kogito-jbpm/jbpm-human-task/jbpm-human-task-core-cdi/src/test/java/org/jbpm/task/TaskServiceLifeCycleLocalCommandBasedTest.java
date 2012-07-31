package org.jbpm.task;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class TaskServiceLifeCycleLocalCommandBasedTest extends TaskServiceLifeCycleBaseTest {

    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "jbpm-human-task-cdi.jar")
                .addPackage("org.jboss.seam.persistence") //seam-persistence
                .addPackage("org.jboss.seam.transaction") //seam-persistence
                .addPackage("org.jbpm.task") 
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
                .addPackage("org.jbpm.task.commands") 
                .addAsResource("org/jbpm/task/LoadUsers.mvel", "org/jbpm/task/LoadUsers.mvel")
                .addAsResource("org/jbpm/task/LoadGroups.mvel", "org/jbpm/task/LoadGroups.mvel")
                .addAsManifestResource("test-persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
                .addAsManifestResource("META-INF/beans-commandbased.xml", ArchivePaths.create("beans.xml"));

    }
    
}
