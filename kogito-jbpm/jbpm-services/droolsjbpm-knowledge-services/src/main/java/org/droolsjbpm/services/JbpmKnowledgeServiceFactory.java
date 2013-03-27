///*
// * Copyright 2013 JBoss Inc
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.droolsjbpm.services;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//
//import org.droolsjbpm.services.api.IdentityProvider;
//import org.droolsjbpm.services.api.KnowledgeAdminDataService;
//import org.droolsjbpm.services.api.RuntimeDataService;
//import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
//import org.droolsjbpm.services.impl.KnowledgeAdminDataServiceImpl;
//import org.droolsjbpm.services.impl.RuntimeDataServiceImpl;
//import org.droolsjbpm.services.impl.SessionManagerImpl;
//import org.droolsjbpm.services.impl.bpmn2.BPMN2DataServiceImpl;
//import org.droolsjbpm.services.impl.bpmn2.BPMN2DataServiceSemanticModule;
//import org.droolsjbpm.services.impl.bpmn2.GetReusableSubProcessesHandler;
//import org.droolsjbpm.services.impl.bpmn2.HumanTaskGetInformationHandler;
//import org.droolsjbpm.services.impl.bpmn2.ProcessDescriptionRepository;
//import org.droolsjbpm.services.impl.bpmn2.ProcessGetInformationHandler;
//import org.droolsjbpm.services.impl.bpmn2.ProcessGetInputHandler;
//import org.droolsjbpm.services.impl.event.listeners.CDIRuleAwareProcessEventListener;
//import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
//import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
//import org.jbpm.shared.services.api.ServicesSessionManager;
//import org.jbpm.shared.services.impl.JbpmLocalTransactionManager;
//import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
//import org.jbpm.services.task.wih.LocalHTWorkItemHandler;
//
///**
// *
// * @author salaboy
// */
//public class JbpmKnowledgeServiceFactory {
//    
////    private static ServicesSessionManager service = new SessionManagerImpl();
//    
//    private static JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
//    
//    private static EntityManagerFactory emf;
//    
//    private static JbpmServicesTransactionManager jbpmTransactionManager = new JbpmLocalTransactionManager();
//    
//    private static BPMN2DataService bpmn2DataService = new BPMN2DataServiceImpl();
//    
//    
//    private static LocalHTWorkItemHandler htWorkItemHandler;
//    
//    
//    private static IdentityProvider identityProvider;
//    
//    private static RuntimeDataService dataService = new RuntimeDataServiceImpl();
//        
//    private static KnowledgeAdminDataService adminDataService = new KnowledgeAdminDataServiceImpl();
//        
//    
//    public static ServicesSessionManager newServicesSessionManager(){
//        configure();
//        return service;
//    }
//    
//    public static BPMN2DataService newBPMN2DataService(){
//        configurePersistenceManager();
//        configureBpmn2DataService();
//        return bpmn2DataService;
//    }
//    
//    public static RuntimeDataService newKnowledgeDataService(){
//        configurePersistenceManager();
//        configureKnowledgeDataService();
//        return dataService;
//    }
//    
//    public static KnowledgeAdminDataService newKnowledgeAdminDataService(){
//        configureKnowledgeAdminDataService();
//        return adminDataService;
//    }
//    
//
//    private static void configure() {
//        
//        configurePersistenceManager();
//        
//        ((SessionManagerImpl)service).setPm(pm);
//        
//        configureBpmn2DataService();
//        
//        ((SessionManagerImpl)service).setBpmn2Service(bpmn2DataService);           
//        
//        CDIRuleAwareProcessEventListener ruleAwareEventListener = new CDIRuleAwareProcessEventListener();
//        ((SessionManagerImpl)service).setProcessFactsListener(ruleAwareEventListener);
//
//    }
//    
//   
//    
//    public static void configurePersistenceManager(){
//        EntityManager em = emf.createEntityManager();
//        // Persistence and Transactions
//        ((JbpmServicesPersistenceManagerImpl)pm).setEm(em);
//        ((JbpmServicesPersistenceManagerImpl)pm).setTransactionManager(jbpmTransactionManager);
//        
//    }
//     
//    public static void configureBpmn2DataService(){
//        ProcessDescriptionRepository repo = new ProcessDescriptionRepository();
//        ((BPMN2DataServiceImpl)bpmn2DataService).setRepository(repo);
//        BPMN2DataServiceSemanticModule semanticModule = new BPMN2DataServiceSemanticModule();
//        
//        ProcessGetInformationHandler processHandler = new ProcessGetInformationHandler();
//        processHandler.setRepository(repo);
//        semanticModule.setProcessHandler(processHandler);
//        
//        ProcessGetInputHandler inputHandler = new ProcessGetInputHandler();
//        inputHandler.setRepository(repo);
//        semanticModule.setProcessInputHandler(inputHandler);
//        
//        GetReusableSubProcessesHandler subProcessHandler = new GetReusableSubProcessesHandler();
//        subProcessHandler.setRepository(repo);
//        semanticModule.setReusableSubprocessHandler(subProcessHandler);
//        
//        HumanTaskGetInformationHandler taskHandler = new HumanTaskGetInformationHandler();
//        taskHandler.setRepository(repo);
//        semanticModule.setTaskHandler(taskHandler);
//        
//        semanticModule.init();
//        
//        ((BPMN2DataServiceImpl)bpmn2DataService).setSemanticModule(semanticModule);
//        ((BPMN2DataServiceImpl)bpmn2DataService).init();
//    
//    }
//    
//    public static void setIdentityProvider(IdentityProvider identityProvider) {
//        JbpmKnowledgeServiceFactory.identityProvider = identityProvider;
//    }
//
//    public static void setHtWorkItemHandler(LocalHTWorkItemHandler htWorkItemHandler) {
//        JbpmKnowledgeServiceFactory.htWorkItemHandler = htWorkItemHandler;
//    }
//
//    public static void setJbpmServicesTransactionManager(JbpmServicesTransactionManager txmgr){
//        JbpmKnowledgeServiceFactory.jbpmTransactionManager = txmgr;
//    }
//    
//    
//    public static void setEntityManagerFactory(EntityManagerFactory emf){
//        JbpmKnowledgeServiceFactory.emf = emf;
//    }
//    
//    public static EntityManagerFactory getEntityManagerFactory(){
//        return JbpmKnowledgeServiceFactory.emf;
//    }
//
//    public static BPMN2DataService getBpmn2DataService() {
//        return bpmn2DataService;
//    }
//
//    private static void configureKnowledgeDataService() {
//        ((RuntimeDataServiceImpl)dataService).setPm(pm);
//    }
//
//    private static void configureKnowledgeAdminDataService() {
//        ((KnowledgeAdminDataServiceImpl)adminDataService).setPm(pm);
//    }
//    
//    
//    
//    
//     
//    
//}
