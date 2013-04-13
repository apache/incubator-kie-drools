/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.droolsjbpm.services.test.hr;

import org.droolsjbpm.services.test.domain.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.droolsjbpm.services.api.DomainManagerService;
import org.droolsjbpm.services.domain.entities.Domain;
import org.droolsjbpm.services.domain.entities.Organization;
import org.droolsjbpm.services.domain.entities.RuntimeId;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.commons.java.nio.file.Path;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.Context;
import org.kie.internal.runtime.manager.RuntimeEngine;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.TaskService;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.TaskSummary;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.internal.task.api.model.Content;
import org.kie.internal.task.api.model.Task;

/**
 *
 * @author salaboy
 */
@RunWith(Arquillian.class)
public class HumanResourcesHiringTest {

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
                .addPackage("org.kie.internal.runtime")
                .addPackage("org.kie.internal.runtime.manager")
                .addPackage("org.kie.internal.runtime.manager.cdi.qualifier")
                .addPackage("org.jbpm.runtime.manager")
                .addPackage("org.jbpm.runtime.manager.impl")
                .addPackage("org.jbpm.runtime.manager.impl.cdi")
                .addPackage("org.jbpm.runtime.manager.impl.cdi.qualifier")
                .addPackage("org.jbpm.runtime.manager.impl.context")
                .addPackage("org.jbpm.runtime.manager.impl.factory")
                .addPackage("org.jbpm.runtime.manager.impl.jpa")
                .addPackage("org.jbpm.runtime.manager.impl.manager")
                .addPackage("org.jbpm.runtime.manager.mapper")
                .addPackage("org.jbpm.runtime.manager.impl.task")
                .addPackage("org.jbpm.runtime.manager.impl.tx")
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                .addPackage("org.droolsjbpm.services.api")
                .addPackage("org.droolsjbpm.services.impl")
                .addPackage("org.droolsjbpm.services.api.bpmn2")
                .addPackage("org.droolsjbpm.services.impl.bpmn2")
                .addPackage("org.droolsjbpm.services.impl.event.listeners")
                .addPackage("org.droolsjbpm.services.impl.audit")
                .addPackage("org.droolsjbpm.services.impl.util")
                .addPackage("org.droolsjbpm.services.impl.vfs")
                .addPackage("org.droolsjbpm.services.impl.example")
                .addPackage("org.kie.commons.java.nio.fs.jgit")
                .addPackage("org.droolsjbpm.services.test") // Identity Provider Test Impl here
                .addAsResource("jndi.properties", "jndi.properties")
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                //                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

    }
    private static PoolingDataSource pds;

    @BeforeClass
    public static void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        Properties props = new Properties();
        props.setProperty("salaboy", "user");

    }

    @AfterClass
    public static void teardown() {
        pds.close();
    }

    @After
    public void tearDownTest() {
        int removeAllTasks = taskService.removeAllTasks();
        System.out.println(">>> Removed Tasks > " + removeAllTasks);
    }
    /*
     * end of initialization code, tests start here
     */
    @Inject
    private RuntimeManagerFactory managerFactory;
    @Inject
    private EntityManagerFactory emf;
    @Inject
    private FileService fs;
    @Inject
    protected DomainManagerService domainService;
    @Inject
    protected TaskService taskService;
    @Inject
    private BeanManager beanManager;

    @Test
    public void simpleExecutionTest() {
        assertNotNull(managerFactory);
        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(emf)
                .registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, null));

        builder.addAsset(ResourceFactory.newClassPathResource("repo/processes/hr/hiring.bpmn2"), ResourceType.BPMN2);


        RuntimeManager manager = managerFactory.newSingletonRuntimeManager(builder.get());
        testProcessStartOnManager(manager, EmptyContext.get());

        manager.close();

    }

//    @Test
//    public void initDomainTestWithWorkItemHandler(){
//        Organization organization = new Organization();
//        organization.setName("JBoss");
//        Domain domain = new Domain();
//        domain.setName("general");
//        List<RuntimeId> runtimes = new ArrayList<RuntimeId>();
//        RuntimeId runtime1 = new RuntimeId();
//        runtime1.setReference("processes/general/");
//        runtime1.setDomain(domain);
//        runtimes.add(runtime1);
//        domain.setRuntimes(runtimes);
//        domain.setOrganization(organization);
//        List<Domain> domains = new ArrayList<Domain>();
//        domains.add(domain);
//        organization.setDomains(domains);
//
//        domainService.storeOrganization(organization);
//        
//        domainService.initDomain(domain.getId());
//        RuntimeManager runtimesByDomain = domainService.getRuntimesByDomain(domain.getName());
//        RuntimeEngine runtime = runtimesByDomain.getRuntimeEngine(ProcessInstanceIdContext.get());
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("id", "test");
//        ProcessInstance processInstance = runtime.getKieSession().startProcess("customtask", params);
//        
//        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
//        Collection<ProcessDesc> processesByDomainName = domainService.getProcessesByDomainName("general");
//        assertNotNull(processesByDomainName);
//        
//        assertEquals(4, processesByDomainName.size());
//    
//    }
    private void testProcessStartOnManager(RuntimeManager manager, Context context) {
        assertNotNull(manager);

        org.kie.internal.runtime.manager.RuntimeEngine runtime = manager.getRuntimeEngine(context);
        assertNotNull(runtime);

        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        ProcessInstance processInstance = ksession.startProcess("hiring");
        assertNotNull(processInstance);
        TaskService taskService = runtime.getTaskService();
        
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroup("HR", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        TaskSummary HRInterview = tasks.get(0);
        
        taskService.claim(HRInterview.getId(), "katy");
        
        taskService.start(HRInterview.getId(), "katy");
        
        Map<String, Object> hrOutput = new HashMap<String, Object>();
        hrOutput.put("out.name", "salaboy");
        hrOutput.put("out.age", 29);
        hrOutput.put("out.mail", "salaboy@gmail.com");
        hrOutput.put("out.score", 8);
        
        taskService.complete(HRInterview.getId(), "katy", hrOutput);
        
        tasks = taskService.getTasksAssignedByGroup("IT", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        TaskSummary techInterview = tasks.get(0);
        Task techInterviewTask = taskService.getTaskById(techInterview.getId());
        Content contentById = taskService.getContentById(techInterviewTask.getTaskData().getDocumentContentId());
        assertNotNull(contentById);
        
        Map<String, Object> taskContent = (Map<String, Object>) ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
        
        assertEquals(5, taskContent.size());
        
        assertEquals("salaboy@gmail.com", taskContent.get("in.mail"));
        assertEquals(29, taskContent.get("in.age"));
        assertEquals("salaboy", taskContent.get("in.name"));
        
        
    }
}
