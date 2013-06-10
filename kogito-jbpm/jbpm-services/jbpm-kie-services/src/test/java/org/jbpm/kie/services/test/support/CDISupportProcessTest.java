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
package org.jbpm.kie.services.test.support;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.runner.RunWith;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.InternalTaskService;

@RunWith(Arquillian.class)
public class CDISupportProcessTest extends SupportProcessBaseTest {

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
    private TaskService taskService;
    

    @After
    public void tearDown() throws Exception {
        int removedTasks = ((InternalTaskService) taskService).removeAllTasks();
        int removedLogs = adminDataService.removeAllData();
        System.out.println(" --> Removed Tasks = "+removedTasks + " - ");
        System.out.println(" --> Removed Logs = "+removedLogs + " - ");
       
    }
}
