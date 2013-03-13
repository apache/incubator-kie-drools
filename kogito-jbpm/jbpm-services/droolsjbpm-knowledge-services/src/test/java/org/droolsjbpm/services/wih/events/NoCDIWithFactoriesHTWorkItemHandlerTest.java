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
import org.droolsjbpm.services.api.SessionManagerModule;
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



public class NoCDIWithFactoriesHTWorkItemHandlerTest extends HTWorkItemHandlerBaseTest {

    
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
        
        JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
     
        // Task Service Start up
        
        HumanTaskServiceFactory.setEntityManagerFactory(emf);
        
        JbpmServicesTransactionManager jbpmJTATransactionManager = new JbpmJTATransactionManager();
        HumanTaskServiceFactory.setJbpmServicesTransactionManager(jbpmJTATransactionManager);
        taskService = HumanTaskServiceFactory.newTaskService();

        ExternalTaskEventListener externalTaskEventListener = new ExternalTaskEventListener();
        externalTaskEventListener.setTaskService(taskService);
        
        
        // Session Manager Start up
        SessionManagerModule.setEntityManagerFactory(emf);
        SessionManagerModule.setIdentityProvider(new TestIdentityProvider());
        SessionManagerModule.setJbpmServicesPersistenceManager(pm);
        SessionManagerModule.setJbpmServicesTransactionManager(jbpmJTATransactionManager);
        sessionManager = SessionManagerModule.getService();
        
        
        
        htWorkItemHandler = new LocalHTWorkItemHandler();
        htWorkItemHandler.setSessionManager(sessionManager);
        htWorkItemHandler.setTaskService(taskService);
        htWorkItemHandler.setTaskEventListener(externalTaskEventListener);
        htWorkItemHandler.addSession(ksession);
        
        
        
        // TEST Start up
        setTaskService(taskService);
        setSessionManager(sessionManager);
        setHandler(htWorkItemHandler);
    }

    @After
    public void tearDown() throws Exception {
        int removeAllTasks = taskService.removeAllTasks();
        SessionManagerModule.dispose();
        emf.close();
        ds.close();
    }
}
