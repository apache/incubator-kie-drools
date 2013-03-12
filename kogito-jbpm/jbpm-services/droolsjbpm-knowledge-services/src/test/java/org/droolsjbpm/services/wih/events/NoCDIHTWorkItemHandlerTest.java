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
package org.droolsjbpm.services.wih.events;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.SessionManagerImpl;
import org.droolsjbpm.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.droolsjbpm.services.impl.bpmn2.BPMN2DataServiceSemanticModule;
import org.droolsjbpm.services.impl.bpmn2.GetReusableSubProcessesHandler;
import org.droolsjbpm.services.impl.bpmn2.HumanTaskGetInformationHandler;
import org.droolsjbpm.services.impl.bpmn2.ProcessDescriptionRepository;
import org.droolsjbpm.services.impl.bpmn2.ProcessGetInformationHandler;
import org.droolsjbpm.services.impl.bpmn2.ProcessGetInputHandler;
import org.droolsjbpm.services.impl.event.listeners.CDIBAMProcessEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIProcessEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIRuleAwareProcessEventListener;
import org.droolsjbpm.services.test.TestIdentityProvider;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.jbpm.task.HumanTaskServiceFactory;
import org.jbpm.task.wih.LocalHTWorkItemHandler;
import org.jbpm.task.wih.ExternalTaskEventListener;
import org.junit.After;
import org.junit.Before;



public class NoCDIHTWorkItemHandlerTest extends HTWorkItemHandlerBaseTest {

    
    @Inject
    private LocalHTWorkItemHandler htWorkItemHandler;
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
        
        
        // Persistence Manager Start Up
        
        emf = Persistence.createEntityManagerFactory("org.jbpm.domain");
        
        Logger logger = LogManager.getLogManager().getLogger("");
        JbpmServicesTransactionManager jbpmJTATransactionManager = new JbpmJTATransactionManager();
        JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
        
        // Task Service Start up
          
        HumanTaskServiceFactory.setEntityManagerFactory(emf);
        HumanTaskServiceFactory.setJbpmServicesPersistenceManager(pm);
        HumanTaskServiceFactory.setJbpmServicesTransactionManager(jbpmJTATransactionManager);
        taskService = HumanTaskServiceFactory.getService();

        ExternalTaskEventListener externalTaskEventListener = new ExternalTaskEventListener();
        externalTaskEventListener.setTaskService(taskService);
        
        HumanTaskServiceFactory.addTaskEventListener(externalTaskEventListener);
        
        
        // Session Manager Start up
 
        sessionManager = new SessionManagerImpl();
        ((SessionManagerImpl)sessionManager).setPm(pm);
        
        BPMN2DataService bpmn2DataService = new BPMN2DataServiceImpl();
        ProcessDescriptionRepository repo = new ProcessDescriptionRepository();
        ((BPMN2DataServiceImpl)bpmn2DataService).setRepository(repo);
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
        
        ((BPMN2DataServiceImpl)bpmn2DataService).setSemanticModule(semanticModule);
        ((BPMN2DataServiceImpl)bpmn2DataService).init();
        
        ((SessionManagerImpl)sessionManager).setBpmn2Service(bpmn2DataService);
        
        
        CDIBAMProcessEventListener bamProcessEventListener = new CDIBAMProcessEventListener();
        bamProcessEventListener.setPm(pm);
        TestIdentityProvider identityProvider = new TestIdentityProvider();
        bamProcessEventListener.setIdentity(identityProvider);
        
        ((SessionManagerImpl)sessionManager).setBamProcessListener(bamProcessEventListener);
        
        
        htWorkItemHandler = new LocalHTWorkItemHandler();
        htWorkItemHandler.setSessionManager(sessionManager);
        htWorkItemHandler.setTaskService(taskService);
        htWorkItemHandler.setTaskEventListener(externalTaskEventListener);
        htWorkItemHandler.addSession(ksession);
        
        ((SessionManagerImpl)sessionManager).setHTWorkItemHandler(htWorkItemHandler);
        
        CDIProcessEventListener processEventListener = new CDIProcessEventListener();
        processEventListener.setPm(pm);
        processEventListener.setIdentity(identityProvider);
        
        ((SessionManagerImpl)sessionManager).setProcessListener(processEventListener);
        
        CDIRuleAwareProcessEventListener ruleAwareEventListener = new CDIRuleAwareProcessEventListener();
        ((SessionManagerImpl)sessionManager).setProcessFactsListener(ruleAwareEventListener);
        
        ((SessionManagerImpl)sessionManager).init();
        
        // TEST Start up
        setTaskService(taskService);
        setSessionManager(sessionManager);
        setHandler(htWorkItemHandler);
    }

    @After
    public void tearDown() throws Exception {
        int removeAllTasks = taskService.removeAllTasks();
        HumanTaskServiceFactory.dispose();
        
        emf.close();
        ds.close();
    }
}
