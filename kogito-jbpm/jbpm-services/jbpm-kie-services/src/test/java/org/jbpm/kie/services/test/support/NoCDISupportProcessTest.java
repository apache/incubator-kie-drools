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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.KnowledgeAdminDataServiceImpl;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.kie.services.impl.VFSDeploymentService;
import org.jbpm.kie.services.impl.VfsMVELWorkItemHandlerProducer;
import org.jbpm.kie.services.impl.audit.ServicesAwareAuditEventBuilder;
import org.jbpm.kie.services.impl.bpmn2.AbstractTaskGetInformationHandler;
import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceSemanticModule;
import org.jbpm.kie.services.impl.bpmn2.DataServiceItemDefinitionHandler;
import org.jbpm.kie.services.impl.bpmn2.GetReusableSubProcessesHandler;
import org.jbpm.kie.services.impl.bpmn2.HumanTaskGetInformationHandler;
import org.jbpm.kie.services.impl.bpmn2.ProcessDescriptionRepository;
import org.jbpm.kie.services.impl.bpmn2.ProcessGetInformationHandler;
import org.jbpm.kie.services.impl.bpmn2.ProcessGetInputHandler;
import org.jbpm.kie.services.test.TestIdentityProvider;
import org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.jbpm.shared.services.impl.TestVFSFileServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;

public class NoCDISupportProcessTest extends SupportProcessBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(NoCDISupportProcessTest.class);
    
    private TaskService taskService;
    private FileService fs;
    private PoolingDataSource ds;
    @Before
    public void setUp() throws Exception {
        
        ds = new PoolingDataSource();
        ds.setUniqueName("jdbc/testDS1");


        //NON XA CONFIGS
        ds.setClassName("org.h2.jdbcx.JdbcDataSource");
        ds.setMaxPoolSize(3);
        ds.setAllowLocalTransactions(true);
        ds.getDriverProperties().put("user", "sa");
        ds.getDriverProperties().put("password", "sasa");
        ds.getDriverProperties().put("URL", "jdbc:h2:mem:mydb");

        ds.init();
        
        IOService ioService = new IOServiceNio2WrapperImpl();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.domain");
        EntityManager em = emf.createEntityManager();
        
        JbpmServicesTransactionManager jbpmJTATransactionManager = new JbpmJTATransactionManager();
        JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
        ((JbpmServicesPersistenceManagerImpl)pm).setEm(em);
        ((JbpmServicesPersistenceManagerImpl)pm).setTransactionManager(jbpmJTATransactionManager); 
        
       
        this.fs = new TestVFSFileServiceImpl();
        fs.init();
               
        VfsMVELWorkItemHandlerProducer workItemProducer = new VfsMVELWorkItemHandlerProducer();
        workItemProducer.setFs(fs);

        
        TestIdentityProvider identityProvider = new TestIdentityProvider();
        ServicesAwareAuditEventBuilder auditEventBuilder = new ServicesAwareAuditEventBuilder();
        auditEventBuilder.setIdentityProvider(identityProvider);
        
        adminDataService = new KnowledgeAdminDataServiceImpl();
        ((KnowledgeAdminDataServiceImpl)adminDataService).setPm(pm);
        
        bpmn2Service = new BPMN2DataServiceImpl();
        ProcessDescriptionRepository repo = new ProcessDescriptionRepository();
        ((BPMN2DataServiceImpl) bpmn2Service).setRepository(repo);
        BPMN2DataServiceSemanticModule semanticModule = new BPMN2DataServiceSemanticModule();

        ProcessGetInformationHandler processHandler = new ProcessGetInformationHandler();
        processHandler.setRepository(repo);
        semanticModule.setProcessHandler(processHandler);

        ProcessGetInputHandler inputHandler = new ProcessGetInputHandler();
        inputHandler.setRepository(repo);
        semanticModule.setProcessInputHandler(inputHandler);

        GetReusableSubProcessesHandler subProcessHandler = new GetReusableSubProcessesHandler();
        subProcessHandler.setRepository(repo);
        semanticModule.setReusableSubprocessHandler(subProcessHandler);

        HumanTaskGetInformationHandler taskHandler = new HumanTaskGetInformationHandler();
        taskHandler.setRepository(repo);
        semanticModule.setTaskHandler(taskHandler);
      
        DataServiceItemDefinitionHandler itemDefHandler = new DataServiceItemDefinitionHandler();
        itemDefHandler.setRepository(repo);
        semanticModule.setItemDefinitionHandler(itemDefHandler);

        AbstractTaskGetInformationHandler abstractTaskGetInformationHandler = new AbstractTaskGetInformationHandler();
        abstractTaskGetInformationHandler.setRepository(repo);
        semanticModule.setAbstractTaskHandler(abstractTaskGetInformationHandler);
        
        semanticModule.init();

        ((BPMN2DataServiceImpl) bpmn2Service).setSemanticModule(semanticModule);
        ((BPMN2DataServiceImpl) bpmn2Service).init();
      
        HumanTaskServiceFactory.setEntityManagerFactory(emf);
      
        HumanTaskServiceFactory.setJbpmServicesTransactionManager(jbpmJTATransactionManager);
        taskService = HumanTaskServiceFactory.newTaskService();

        RuntimeDataService runtimeDataService = new RuntimeDataServiceImpl();
        ((RuntimeDataServiceImpl)runtimeDataService).setPm(pm);
        
        deploymentService = new VFSDeploymentService();
        ((VFSDeploymentService) deploymentService).setBpmn2Service(bpmn2Service);
        ((VFSDeploymentService) deploymentService).setEmf(emf);
        ((VFSDeploymentService) deploymentService).setFs(fs);
        ((VFSDeploymentService) deploymentService).setIdentityProvider(identityProvider);
        ((VFSDeploymentService) deploymentService).setManagerFactory(new RuntimeManagerFactoryImpl());
        ((VFSDeploymentService) deploymentService).setPm(pm);
        ((VFSDeploymentService) deploymentService).setRuntimeDataService(runtimeDataService);

    }

    @After
    public void tearDown() throws Exception {
        int removedTasks = ((InternalTaskService) taskService).removeAllTasks();
        int removedLogs = adminDataService.removeAllData();
        logger.debug(" --> Removed Tasks = {}", removedTasks);
        logger.debug(" --> Removed Logs = {}", removedLogs);
       
    }
}
