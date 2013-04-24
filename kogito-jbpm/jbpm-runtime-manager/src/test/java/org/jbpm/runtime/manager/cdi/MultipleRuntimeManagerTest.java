package org.jbpm.runtime.manager.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

@RunWith(Arquillian.class)
public class MultipleRuntimeManagerTest {
    
    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "jbpm-runtime-manager.jar")

                .addPackage("org.jbpm.task")
                .addPackage("org.jbpm.task.wih") // work items org.jbpm.task.wih
                .addPackage("org.jbpm.task.annotations")
                .addPackage("org.jbpm.task.api")
//                .addPackage("org.jbpm.task.impl")
                .addPackage("org.jbpm.task.events")
                .addPackage("org.jbpm.task.exception")
//                .addPackage("org.jbpm.task.identity")
                .addPackage("org.jbpm.task.factories")
                .addPackage("org.jbpm.task.internals")
//                .addPackage("org.jbpm.task.internals.lifecycle")
                .addPackage("org.jbpm.task.lifecycle.listeners")
                .addPackage("org.jbpm.task.query")
                .addPackage("org.jbpm.task.util")
//                .addPackage("org.jbpm.task.commands") // This should not be required here
                .addPackage("org.jbpm.task.deadlines") // deadlines
                .addPackage("org.jbpm.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.task.subtask")
                .addPackage("org.jbpm.shared.services.impl")
                .addPackage("org.jbpm.runtime.manager")
                .addPackage("org.jbpm.runtime.manager.impl")
                .addPackage("org.jbpm.runtime.manager.impl.cdi.qualifier")
                .addPackage("org.jbpm.runtime.manager.impl.context")
                .addPackage("org.jbpm.runtime.manager.impl.factory")
                .addPackage("org.jbpm.runtime.manager.impl.jpa")
                .addPackage("org.jbpm.runtime.manager.impl.manager")
                .addPackage("org.jbpm.runtime.manager.mapper")
                .addPackage("org.jbpm.runtime.manager.impl.task")
                .addPackage("org.jbpm.runtime.manager.impl.tx") 
                .addPackage("org.jbpm.runtime.manager.util") // test utilities
                .addAsResource("jndi.properties","jndi.properties")
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
        props.setProperty("john", "user");
        
    }
    
    @AfterClass
    public static void teardown() {
        pds.close();
    }
    /*
     * end of initialization code, tests start here
     */

    @Inject
    private RuntimeManagerFactory managerFactory;
    
    @Inject
    private EntityManagerFactory emf;
    
    @Test
    public void testAllManagersManager() {
        assertNotNull(managerFactory);
        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        RuntimeManager manager = managerFactory.newSingletonRuntimeManager(environment);
        testProcessStartOnManager(manager, EmptyContext.get());
        manager.close();
        
        environment = RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = managerFactory.newPerRequestRuntimeManager(environment);
        testProcessStartOnManager(manager, EmptyContext.get());
        manager.close();
        
        environment = RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = managerFactory.newPerProcessInstanceRuntimeManager(environment);
        testProcessStartOnManager(manager, ProcessInstanceIdContext.get());
        manager.close();
    }    
    
    
    private void testProcessStartOnManager(RuntimeManager manager, Context<?> context) {
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(context);
        assertNotNull(runtime);
        
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertNotNull(processInstance);
        
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = runtime.getTaskService().getTasksOwnedByStatus("john", statuses, "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        runtime.getTaskService().start(tasks.get(0).getId(), "john");
        
        runtime.getTaskService().complete(tasks.get(0).getId(), "john", null);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
}
