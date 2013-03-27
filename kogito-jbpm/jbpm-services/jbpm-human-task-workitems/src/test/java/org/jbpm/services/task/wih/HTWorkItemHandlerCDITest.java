package org.jbpm.services.task.wih;
///**
// * Copyright 2010 JBoss Inc
// *
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License. You may obtain a copy of
// * the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// * License for the specific language governing permissions and limitations under
// * the License.
// */
//package org.jbpm.services.task.wih;
//
//import javax.inject.Inject;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.Archive;
//import org.jboss.shrinkwrap.api.ArchivePaths;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.jbpm.services.task.test.TestStatefulKnowledgeSession;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.runner.RunWith;
//import org.kie.internal.runtime.manager.RuntimeManagerFactory;
//import org.kie.internal.runtime.manager.context.EmptyContext;
//
//@RunWith(Arquillian.class)
//public class HTWorkItemHandlerCDITest extends HTWorkItemHandlerBaseTest {
//
//    @Deployment()
//    public static Archive<?> createDeployment() {
//        return ShrinkWrap.create(JavaArchive.class, "jbpm-human-task-wih.jar")
//                .addPackage("org.jboss.seam.persistence") //seam-persistence
//                .addPackage("org.jboss.seam.transaction") //seam-persistence
//                .addPackage("org.jbpm.shared.services.api")
//                .addPackage("org.jbpm.shared.services.impl")
//                .addPackage("org.jbpm.services.task")
//                .addPackage("org.jbpm.services.task.wih") // work items
//                .addPackage("org.jbpm.services.task.annotations")
//                .addPackage("org.jbpm.services.task.api")
//                .addPackage("org.jbpm.services.task.impl")
//                .addPackage("org.jbpm.services.task.impl.model")
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
//                .addPackage("org.droolsjbpm.services.impl.vfs")
//                .addPackage("org.kie.commons.java.nio.fs.jgit")
//                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
//                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
//                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
//
//    }
//    @Inject
//    private LocalHTWorkItemHandler htWorkItemHandler;
//    
//    @Inject
//    private RuntimeManagerFactory runtimeManagerFactory;
//    
//    @Before
//    public void setUp() throws Exception {
//        runtimeManagerFactory.newSingletonRuntimeManager(null).getRuntime(EmptyContext.get());
//        ksession = (TestStatefulKnowledgeSession) sessionManager.getKsessionById(TestStatefulKnowledgeSession.testSessionId);
//        listenr.setThrowException(false);
//        htWorkItemHandler.setRuntimeManager(sessionManager);
//        setHandler(htWorkItemHandler);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        int removeAllTasks = taskService.removeAllTasks();
//
//    }
//}
