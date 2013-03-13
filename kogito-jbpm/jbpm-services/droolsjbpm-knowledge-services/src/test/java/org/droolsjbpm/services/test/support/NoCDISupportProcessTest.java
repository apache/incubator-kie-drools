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
package org.droolsjbpm.services.test.support;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.droolsjbpm.services.impl.KnowledgeAdminDataServiceImpl;
import org.droolsjbpm.services.impl.KnowledgeDataServiceImpl;
import org.droolsjbpm.services.impl.MVELWorkItemHandlerProducer;
import org.droolsjbpm.services.impl.SessionManagerImpl;
import org.droolsjbpm.services.impl.audit.IdentityAwareAuditEventBuilder;
import org.droolsjbpm.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.droolsjbpm.services.impl.bpmn2.BPMN2DataServiceSemanticModule;
import org.droolsjbpm.services.impl.bpmn2.GetReusableSubProcessesHandler;
import org.droolsjbpm.services.impl.bpmn2.HumanTaskGetInformationHandler;
import org.droolsjbpm.services.impl.bpmn2.ProcessDescriptionRepository;
import org.droolsjbpm.services.impl.bpmn2.ProcessGetInformationHandler;
import org.droolsjbpm.services.impl.bpmn2.ProcessGetInputHandler;
import org.droolsjbpm.services.impl.event.listeners.CDIRuleAwareProcessEventListener;
import org.droolsjbpm.services.test.TestIdentityProvider;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.jbpm.shared.services.impl.TestVFSFileServiceImpl;
import org.jbpm.task.HumanTaskServiceFactory;
import org.jbpm.task.wih.ExternalTaskEventListener;
import org.jbpm.task.wih.LocalHTWorkItemHandler;
import org.junit.After;
import org.junit.Before;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceNio2WrapperImpl;

import bitronix.tm.resource.jdbc.PoolingDataSource;


public class NoCDISupportProcessTest extends SupportProcessBaseTest {

    
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
        // Persistence Manager Start Up
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.domain");
        EntityManager em = emf.createEntityManager();
        
        Logger logger = LogManager.getLogManager().getLogger("");
        JbpmServicesTransactionManager jbpmJTATransactionManager = new JbpmJTATransactionManager();
        JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
        ((JbpmServicesPersistenceManagerImpl)pm).setEm(em);
        ((JbpmServicesPersistenceManagerImpl)pm).setTransactionManager(jbpmJTATransactionManager); 
        
        
          // Task Service Start up
        
        HumanTaskServiceFactory.setEntityManagerFactory(emf);
        
        HumanTaskServiceFactory.setJbpmServicesTransactionManager(jbpmJTATransactionManager);
        taskService = HumanTaskServiceFactory.newTaskService();

        ExternalTaskEventListener externalTaskEventListener = new ExternalTaskEventListener();
        externalTaskEventListener.setTaskService(taskService);
        
        
        
        // Session Manager Start up
 
        sessionManager = new SessionManagerImpl();
        ((SessionManagerImpl)sessionManager).setPm(pm);
        
        bpmn2Service = new BPMN2DataServiceImpl();
        ProcessDescriptionRepository repo = new ProcessDescriptionRepository();
        ((BPMN2DataServiceImpl)bpmn2Service).setRepository(repo);
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
        
        semanticModule.init();
        
        ((BPMN2DataServiceImpl)bpmn2Service).setSemanticModule(semanticModule);
        ((BPMN2DataServiceImpl)bpmn2Service).init();
        
        ((SessionManagerImpl)sessionManager).setBpmn2Service(bpmn2Service);
        
        
        CDIRuleAwareProcessEventListener ruleAwareEventListener = new CDIRuleAwareProcessEventListener();
        ((SessionManagerImpl)sessionManager).setProcessFactsListener(ruleAwareEventListener);
        
        this.fs = new TestVFSFileServiceImpl();
        fs.init();
        
        ((SessionManagerImpl)sessionManager).setFs(fs);
        ((SessionManagerImpl)sessionManager).setIoService(ioService);
        
        
        LocalHTWorkItemHandler htWorkItemHandler = new LocalHTWorkItemHandler();
        htWorkItemHandler.setSessionManager(sessionManager);
        htWorkItemHandler.setTaskService(taskService);
        htWorkItemHandler.setTaskEventListener(externalTaskEventListener);
        
        ((SessionManagerImpl)sessionManager).setHTWorkItemHandler(htWorkItemHandler);
        
        MVELWorkItemHandlerProducer workItemProducer = new MVELWorkItemHandlerProducer();
        workItemProducer.setFs(fs);
        
        TestIdentityProvider identityProvider = new TestIdentityProvider();
        IdentityAwareAuditEventBuilder auditEventBuilder = new IdentityAwareAuditEventBuilder();
        auditEventBuilder.setIdentityProvider(identityProvider);
        
        ((SessionManagerImpl)sessionManager).setAuditEventBuilder(auditEventBuilder);
        
        ((SessionManagerImpl)sessionManager).setWorkItemHandlerProducer(workItemProducer);
        
        ((SessionManagerImpl)sessionManager).init();
        
        
        
        dataService = new KnowledgeDataServiceImpl();
        ((KnowledgeDataServiceImpl)dataService).setPm(pm);
        
        adminDataService = new KnowledgeAdminDataServiceImpl();
        ((KnowledgeAdminDataServiceImpl)adminDataService).setPm(pm);
        
       
      
    }

    @After
    public void tearDown() throws Exception {
        int removedTasks = taskService.removeAllTasks();
        int removedLogs = adminDataService.removeAllData();
        System.out.println(" --> Removed Tasks = "+removedTasks + " - ");
        System.out.println(" --> Removed Logs = "+removedLogs + " - ");
       
    }
}
