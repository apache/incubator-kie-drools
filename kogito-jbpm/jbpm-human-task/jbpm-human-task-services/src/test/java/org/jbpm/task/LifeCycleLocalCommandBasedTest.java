package org.jbpm.task;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class LifeCycleLocalCommandBasedTest extends LifeCycleBaseTest {

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
                //This two packages are required for the command based implementation
                .addPackage("org.jbpm.task.commands") 
                .addPackage("org.jbpm.task.impl.command") 
                .addPackage("org.jbpm.task.deadlines") // deadlines
                .addPackage("org.jbpm.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.task.subtask")
                .addAsManifestResource("test-persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
                .addAsManifestResource("beans-commandbased.xml", ArchivePaths.create("beans.xml"));

    }
    
}
