package org.jbpm.runtime.manager.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.drools.core.event.DebugProcessEventListener;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.runtime.manager.impl.PerProcessInstanceRuntimeManager;
import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.EventService;

import bitronix.tm.resource.jdbc.PoolingDataSource;

@RunWith(Arquillian.class)
public class SingleRuntimeManagerWithListenersTest extends AbstractBaseTest {
    
    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "jbpm-runtime-manager.jar")

                .addPackage("org.jbpm.services.task")
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
                .addPackage("org.jbpm.services.task.deadlines") // deadlines
                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.services.task.subtask")
                .addPackage("org.jbpm.services.task.rule")
                .addPackage("org.jbpm.services.task.rule.impl")
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
                .addPackage("org.jbpm.runtime.manager.cdi.producers")
                .addClass("org.jbpm.runtime.manager.util.CDITestHelperNoTaskService") // test utilities
                .addPackage("org.jbpm.services.task.wih")
                .addPackage("org.jbpm.kie.services.impl.util")
                .addAsResource("jndi.properties","jndi.properties")
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
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
    @After
    public void close() {
        singletonManager.close();
        perRequestManager.close();
        perProcessInstanceManager.close();
    }
    /*
     * end of initialization code, tests start here
     */

    @Inject
    @Singleton
    private RuntimeManager singletonManager;
    
    @Inject
    @PerRequest
    private RuntimeManager perRequestManager;
    
    @Inject
    @PerProcessInstance
    private RuntimeManager perProcessInstanceManager;
    
    @Test
    public void testSingleSingletonManager() {
        assertNotNull(singletonManager);
        
        RuntimeEngine runtime = singletonManager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(runtime);
        testProcessStartOnManager(runtime);
        
        singletonManager.disposeRuntimeEngine(runtime);     
    }
    
    @Test
    public void testSinglePerRequestManager() {
        assertNotNull(perRequestManager);
        
        RuntimeEngine runtime = perRequestManager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(runtime);
        testProcessStartOnManager(runtime);   
        perRequestManager.disposeRuntimeEngine(runtime);
    }
    
    @Test
    public void testSinglePerProcessInstanceManager() {
        assertNotNull(perProcessInstanceManager);
        
        RuntimeEngine runtime = perProcessInstanceManager.getRuntimeEngine(ProcessInstanceIdContext.get());
        assertNotNull(runtime);
        testProcessStartOnManager(runtime);  
        perProcessInstanceManager.disposeRuntimeEngine(runtime);
    }
    
    
    private void testProcessStartOnManager(RuntimeEngine runtime) {
        
        
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);
        
        Collection<ProcessEventListener> pListeners = ksession.getProcessEventListeners();
        assertNotNull(pListeners);
        
        
        // prepare listeners class names for assertion
        List<String> listenerCLassNames = new ArrayList<String>();
        for (Object o : pListeners) {
        	listenerCLassNames.add(o.getClass().getName());
        }
        
        // DebugProcessEventListener was added by custom producer
        assertTrue(listenerCLassNames.contains(DebugProcessEventListener.class.getName()));
        // JPAWorkingMemoryDbLogger one is always added to deal with user tasks
        assertTrue(listenerCLassNames.contains(JPAWorkingMemoryDbLogger.class.getName()));
        if (((RuntimeEngineImpl)runtime).getManager() instanceof PerProcessInstanceRuntimeManager) {
        	assertEquals(3, pListeners.size());	
        } else {
        	assertEquals(2, pListeners.size());
        }
        
        TaskService taskService = runtime.getTaskService();
        assertNotNull(taskService);
        
        List<?> listeners = ((EventService<?>) taskService).getTaskEventListeners();
        assertNotNull(listeners);
        assertEquals(2, listeners.size());
        // prepare listeners class names for assertion
        listenerCLassNames = new ArrayList<String>();
        for (Object o : listeners) {
        	listenerCLassNames.add(o.getClass().getName());
        }
        assertEquals(2, listenerCLassNames.size());
        // JPATaskLifeCycleEventListener was added by custom producer
        assertTrue(listenerCLassNames.contains(JPATaskLifeCycleEventListener.class.getName()));
        // external one is always added to deal with user tasks
        assertTrue(listenerCLassNames.contains(ExternalTaskEventListener.class.getName()));
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertNotNull(processInstance);
        
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = taskService.getTasksOwnedByStatus("john", statuses, "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        taskService.start(tasks.get(0).getId(), "john");
        
        taskService.complete(tasks.get(0).getId(), "john", null);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
}
