/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.cdi.impl.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.runtime.manager.impl.ManagedAuditEventBuilderImpl;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.util.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

@RunWith(Arquillian.class)
public class MultipleRuntimeManagerTest extends AbstractKieServicesBaseTest {
    
    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "jbpm-runtime-manager.jar")
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
                .addPackage("org.jbpm.services.task.audit.service")

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
                .addPackage("org.jbpm.shared.services.impl.tx")
                
                .addPackage("org.jbpm.kie.services.api")
                .addPackage("org.jbpm.kie.services.impl")                
                .addPackage("org.jbpm.kie.services.api.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.event.listeners")
                .addPackage("org.jbpm.kie.services.impl.audit")
                .addPackage("org.jbpm.kie.services.impl.form")
                .addPackage("org.jbpm.kie.services.impl.form.provider")
                .addPackage("org.jbpm.kie.services.impl.query")  
                .addPackage("org.jbpm.kie.services.impl.query.mapper")  
                .addPackage("org.jbpm.kie.services.impl.query.persistence")  
                .addPackage("org.jbpm.kie.services.impl.query.preprocessor")  
                
                .addPackage("org.jbpm.services.cdi")
                .addPackage("org.jbpm.services.cdi.impl")
                .addPackage("org.jbpm.services.cdi.impl.form")
                .addPackage("org.jbpm.services.cdi.impl.manager")
                .addPackage("org.jbpm.services.cdi.producer")
                .addPackage("org.jbpm.services.cdi.impl.security")
                .addPackage("org.jbpm.services.cdi.impl.query")
                
                .addPackage("org.jbpm.kie.services.test")
                .addPackage("org.jbpm.services.cdi.test") // Identity Provider Test Impl here
                .addClass("org.jbpm.services.cdi.test.util.CDITestHelperNoTaskService") 
                .addClass("org.jbpm.services.cdi.test.util.CountDownDeploymentListenerCDIImpl")
                .addClass("org.jbpm.kie.services.test.objects.CoundDownDeploymentListener")
                .addAsResource("jndi.properties","jndi.properties")
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

    }
    @BeforeClass
    public static void setup() {
        TestUtil.cleanupSingletonSessionId();
        Properties props = new Properties();
        props.setProperty("john", "user");
        
    }
    
    @Override
	protected void close() {
		// do nothing here and let CDI close resources
	}

	@Override
	protected void configureServices() {
		// do nothing here and let CDI configure services 
	}
    /*
     * end of initialization code, tests start here
     */

    @Inject
    private RuntimeManagerFactory managerFactory;
    
    @Inject
    private EntityManagerFactory emf;
    
    @Inject
    private BeanManager beanManager;
    
    @Test
    public void testAllManagersManager() {
        assertNotNull(managerFactory);
        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, new ManagedAuditEventBuilderImpl()))
                .get();
        
        RuntimeManager manager = managerFactory.newSingletonRuntimeManager(environment);
        testProcessStartOnManager(manager, EmptyContext.get());
        manager.close();
        
        environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, new ManagedAuditEventBuilderImpl()))
                .get();
        
        manager = managerFactory.newPerRequestRuntimeManager(environment);
        testProcessStartOnManager(manager, EmptyContext.get());
        manager.close();
        
        environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, new ManagedAuditEventBuilderImpl()))
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
        
        AuditService logService = runtime.getAuditService();
        
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("UserTask");
        assertNotNull(logs);      
        assertEquals(1, logs.size());
        
        String externalId = logs.get(0).getExternalId();
        assertEquals(manager.getIdentifier(), externalId);	
        
        runtime.getTaskService().start(tasks.get(0).getId(), "john");
        
        runtime.getTaskService().complete(tasks.get(0).getId(), "john", null);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
}
