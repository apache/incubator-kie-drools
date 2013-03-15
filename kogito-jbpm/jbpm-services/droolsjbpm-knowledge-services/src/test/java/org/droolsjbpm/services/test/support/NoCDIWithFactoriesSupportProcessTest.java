/*
 * Copyright 2013 JBoss Inc
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
package org.droolsjbpm.services.test.support;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.droolsjbpm.services.JbpmKnowledgeServiceFactory;
import org.droolsjbpm.services.impl.MVELWorkItemHandlerProducer;
import org.droolsjbpm.services.impl.SessionManagerImpl;
import org.droolsjbpm.services.impl.audit.IdentityAwareAuditEventBuilder;
import org.droolsjbpm.services.test.TestIdentityProvider;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.jbpm.shared.services.impl.TestVFSFileServiceImpl;
import org.jbpm.task.HumanTaskServiceFactory;
import org.jbpm.task.wih.LocalHTWorkItemHandler;
import org.jbpm.task.wih.HTWorkItemHandlerFactory;
import org.junit.After;
import org.junit.Before;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceNio2WrapperImpl;


public class NoCDIWithFactoriesSupportProcessTest extends SupportProcessBaseTest {

    
    private PoolingDataSource ds;
    private EntityManagerFactory emf;
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
        emf = Persistence.createEntityManagerFactory("org.jbpm.domain");
        
        Logger logger = LogManager.getLogManager().getLogger("");
        
        // Task Service Start up
        HumanTaskServiceFactory.setEntityManagerFactory(emf);
        JbpmServicesTransactionManager jbpmJTATransactionManager = new JbpmJTATransactionManager();
        HumanTaskServiceFactory.setJbpmServicesTransactionManager(jbpmJTATransactionManager);
        taskService = HumanTaskServiceFactory.newTaskService();
        
        // Session Manager Start up
        JbpmKnowledgeServiceFactory.setEntityManagerFactory(emf);
        JbpmKnowledgeServiceFactory.setIdentityProvider(new TestIdentityProvider());
        JbpmKnowledgeServiceFactory.setJbpmServicesTransactionManager(jbpmJTATransactionManager);
        sessionManager = JbpmKnowledgeServiceFactory.newServicesSessionManager();
        
        this.fs = new TestVFSFileServiceImpl();
        fs.init();
        
        ((SessionManagerImpl)sessionManager).setFs(fs);
        ((SessionManagerImpl)sessionManager).setIoService(ioService);
        
        LocalHTWorkItemHandler htWorkItemHandler = HTWorkItemHandlerFactory.newHandler(sessionManager, taskService);
        
        ((SessionManagerImpl)sessionManager).setHTWorkItemHandler(htWorkItemHandler);
        
        MVELWorkItemHandlerProducer workItemProducer = new MVELWorkItemHandlerProducer();
        workItemProducer.setFs(fs);
        
        TestIdentityProvider identityProvider = new TestIdentityProvider();
        IdentityAwareAuditEventBuilder auditEventBuilder = new IdentityAwareAuditEventBuilder();
        auditEventBuilder.setIdentityProvider(identityProvider);
        
        ((SessionManagerImpl)sessionManager).setAuditEventBuilder(auditEventBuilder);
        
        ((SessionManagerImpl)sessionManager).setWorkItemHandlerProducer(workItemProducer);
        
        ((SessionManagerImpl)sessionManager).init();

        dataService = JbpmKnowledgeServiceFactory.newKnowledgeDataService();
        
        adminDataService = JbpmKnowledgeServiceFactory.newKnowledgeAdminDataService();
        
        bpmn2Service = JbpmKnowledgeServiceFactory.getBpmn2DataService();
      
    }

    @After
    public void tearDown() throws Exception {
        int removedTasks = taskService.removeAllTasks();
        int removedLogs = adminDataService.removeAllData();
        emf.close();
        System.out.println(" --> Removed Tasks = "+removedTasks + " - ");
        System.out.println(" --> Removed Logs = "+removedLogs + " - ");
       
    }
}
