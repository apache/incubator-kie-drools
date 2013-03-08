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
package org.droolsjbpm.services.api;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.jbpm.shared.services.api.ServicesSessionManager;
import org.jbpm.shared.services.api.SessionManager;
import org.jbpm.shared.services.impl.JbpmLocalTransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.jbpm.task.wih.CDIHTWorkItemHandler;

/**
 *
 * @author salaboy
 */
public class SessionManagerModule {
    
    private static ServicesSessionManager service = new SessionManagerImpl();
    private static boolean configured = false;
    
    private static JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
    
    private static EntityManagerFactory emf;
    
    private static JbpmServicesTransactionManager jbpmTransactionManager = new JbpmLocalTransactionManager();
    
    private static BPMN2DataService bpmn2DataService = new BPMN2DataServiceImpl();
    
    private static CDIHTWorkItemHandler htWorkItemHandler;
    
    
    private static IdentityProvider identityProvider;
    
    public static ServicesSessionManager getService(){
        if(!configured){
            configure();
        }
        return service;
    }

    private static void configure() {
        
        configurePersistenceManager();
        
        ((SessionManagerImpl)service).setPm(pm);
        
        configureBpmn2DataService();
        
        ((SessionManagerImpl)service).setBpmn2Service(bpmn2DataService);
        
        
        CDIBAMProcessEventListener bamProcessEventListener = new CDIBAMProcessEventListener();
        bamProcessEventListener.setPm(pm);
        
        bamProcessEventListener.setIdentity(identityProvider);
        
        ((SessionManagerImpl)service).setBamProcessListener(bamProcessEventListener);
        
        
        ((SessionManagerImpl)service).setHTWorkItemHandler(htWorkItemHandler);
        
        CDIProcessEventListener processEventListener = new CDIProcessEventListener();
        processEventListener.setPm(pm);
        processEventListener.setIdentity(identityProvider);
        
        ((SessionManagerImpl)service).setProcessListener(processEventListener);
        
        CDIRuleAwareProcessEventListener ruleAwareEventListener = new CDIRuleAwareProcessEventListener();
        ((SessionManagerImpl)service).setProcessFactsListener(ruleAwareEventListener);
        
        configured = true;
    }
    
     public static void dispose(){
        SessionManagerModule.configured = false;
        
    }
    
    public static void configurePersistenceManager(){
        EntityManager em = emf.createEntityManager();
        // Persistence and Transactions
        ((JbpmServicesPersistenceManagerImpl)pm).setEm(em);
        ((JbpmServicesPersistenceManagerImpl)pm).setTransactionManager(jbpmTransactionManager);
        
    }
     
    public static void configureBpmn2DataService(){
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
    
    }
    
    public static void setIdentityProvider(IdentityProvider identityProvider) {
        SessionManagerModule.identityProvider = identityProvider;
    }

    public static void setJbpmServicesPersistenceManager(JbpmServicesPersistenceManager pm) {
        SessionManagerModule.pm = pm;
    }

    public static void setHtWorkItemHandler(CDIHTWorkItemHandler htWorkItemHandler) {
        SessionManagerModule.htWorkItemHandler = htWorkItemHandler;
    }

    public static void setJbpmServicesTransactionManager(JbpmServicesTransactionManager txmgr){
        SessionManagerModule.jbpmTransactionManager = txmgr;
    }
    
    
    public static void setEntityManagerFactory(EntityManagerFactory emf){
        SessionManagerModule.emf = emf;
    }
    
    public static EntityManagerFactory getEntityManagerFactory(){
        return SessionManagerModule.emf;
    }

    public static BPMN2DataService getBpmn2DataService() {
        return bpmn2DataService;
    }
    
    
    
    
     
    
}
