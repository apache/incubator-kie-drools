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
package org.jbpm.task;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;

/**
 *
 *
 */
@RunWith(Arquillian.class)
public class TaskServiceLifeCycleLocalTest extends TaskServiceLifeCycleBaseTest {
    

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
                .addPackage("org.jbpm.task.commands") // This should not be required here 
                .addAsResource("org/jbpm/task/LoadUsers.mvel", "org/jbpm/task/LoadUsers.mvel")
                .addAsResource("org/jbpm/task/LoadGroups.mvel", "org/jbpm/task/LoadGroups.mvel")
                .addAsManifestResource("test-persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

    }
    
  
    
   
}
