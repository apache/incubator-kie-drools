/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.droolsjbpm.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.drools.impl.EnvironmentFactory;
import org.droolsjbpm.services.api.Domain;
import org.droolsjbpm.services.api.SessionManager;
import org.droolsjbpm.services.api.WorkItemHandlerProducer;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.event.listeners.BAM;
import org.droolsjbpm.services.impl.event.listeners.CDIBAMProcessEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIProcessEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIRuleAwareProcessEventListener;
import org.droolsjbpm.services.impl.helpers.StatefulKnowledgeSessionDelegate;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.wih.CDIHTWorkItemHandler;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderErrors;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.conf.EventProcessingOption;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItemHandler;

/**
 * @author salaboy
 */
@Transactional
@ApplicationScoped
public class CDISessionManager implements SessionManager {

    @Inject
    private EntityManager em; 
    
    @Inject
    private EntityManagerFactory emf;
    
    @Inject
    private TaskServiceEntryPoint taskService;
    @Inject 
    private CDIHTWorkItemHandler handler;
    @Inject
    private CDIProcessEventListener processListener;
    @Inject
    @BAM
    private CDIBAMProcessEventListener bamProcessListener;
    @Inject
    private CDIRuleAwareProcessEventListener processFactsListener;
    
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    private WorkItemHandlerProducer workItemHandlerProducer;
    
    @Inject
    private FileService fs;
    
    @Inject
    @Named("fileServiceIOStrategy")
    private IOService ioService;
    private Domain domain;
    // Ksession Name  / sessionId , Ksession
    private Map<String, Map<Integer, StatefulKnowledgeSession>> ksessions = new HashMap<String, Map<Integer, StatefulKnowledgeSession>>();
    // Ksession Name, Ksession Id
    private Map<String, List<Integer>> ksessionIds = new HashMap<String, List<Integer>>();
    // Ksession Id / Process Instance Id 
    private Map<Integer, Long> processInstanceIdKsession = new HashMap<Integer, Long>();
    // Process Path / Process Id - String 
    private Map<String, List<String>> processDefinitionNamesBySession = new HashMap<String, List<String>>();
    // Ksession Name / List of handlers
    private Map<String, Map<String, WorkItemHandler>> ksessionHandlers = new HashMap<String, Map<String, WorkItemHandler>>();

    
    public CDISessionManager() {
    }
    
   

    public CDISessionManager(Domain domain) {
        this.domain = domain;
    }

    @Override
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }

    @Override
    public void addKsessionHandler(String ksessionName, String handlerName, WorkItemHandler handler) {
        if (ksessionHandlers.get(ksessionName) == null) {
            ksessionHandlers.put(ksessionName, new HashMap<String, WorkItemHandler>());
        }
        ksessionHandlers.get(ksessionName).put(handlerName, handler);
    }

    @Override
    public void registerHandlersForSession(String ksessionName, int version) {
        Map<String, WorkItemHandler> handlers = ksessionHandlers.get(ksessionName);
        if (handlers != null) {
            for (String key : handlers.keySet()) {
                ksessions.get(ksessionName).get(version).getWorkItemManager().registerWorkItemHandler(key, handlers.get(key));
            }
        } else {
            // Log NONE Handler Registered
        }
    }

    @Override
    public void registerRuleListenerForSession(String ksessionName, int version) {
        ksessions.get(ksessionName).get(version).addEventListener(processFactsListener);
    }

    @Override
    public void buildSessions(boolean streamMode) {
        processListener.setDomainName(getDomain().getName());
        processListener.setSessionManager(this);

        Map<String, List<Path>> ksessionProcessDefinitions = getDomain().getProcessDefinitionFromKsession();
        Map<String, List<Path>> ksessionRulesDefinitions = getDomain().getRulesDefinitionFromKsession();
        for (String sessionName : ksessionProcessDefinitions.keySet()) {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            if (ksessionProcessDefinitions.get(sessionName) != null) {
                for (Path path : ksessionProcessDefinitions.get(sessionName)) {
                    String processString = new String(ioService.readAllBytes(path));
                    String processId = bpmn2Service.findProcessId(processString);
                    if(!processId.equals("")){
                      addProcessDefinitionToSession(sessionName, processId);
                      System.out.println(">>>>>>>>>> Adding Process to KBase - > " + path.toString());
                      kbuilder.add(ResourceFactory.newByteArrayResource(processString.getBytes()), ResourceType.BPMN2);
                    }else{
                      System.out.println("EEEEEEEEE> Path - > " + path.toString()+" was not added!");
                    }
                }
            }
            if (ksessionRulesDefinitions.get(sessionName) != null) {
                for (Path path : ksessionRulesDefinitions.get(sessionName)) {
                    String rules = new String(ioService.readAllBytes(path));
                    System.out.println(">>>>>>>>>> Adding Rules to KBase - > " + path.toString());
                    kbuilder.add(ResourceFactory.newByteArrayResource(rules.getBytes()), ResourceType.DRL);
                }
            }

            if (!kbuilder.getErrors().isEmpty()) {
                KnowledgeBuilderErrors errors = kbuilder.getErrors();
                Iterator<KnowledgeBuilderError> iterator = errors.iterator();
                while (iterator.hasNext()) {
                    System.out.println("Error: " + iterator.next().getMessage());
                }
                continue;
            }
            KnowledgeBase kbase = null;
            if (streamMode) {
                KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
                config.setOption(EventProcessingOption.STREAM);
                kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
            } else {
                kbase = KnowledgeBaseFactory.newKnowledgeBase();
            }
            
            
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
            Environment env = EnvironmentFactory.newEnvironment();
            UserTransaction ut = setupEnvironment(env);
            StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);            

            ksession.addEventListener(processListener);
            
            ksession.addEventListener(bamProcessListener);

            KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

            handler.addSession(ksession);
            
            // Register the same handler for all the ksessions
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
            // Register the configured handlers
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ksession", ksession);
            Map<String, WorkItemHandler> handlers = workItemHandlerProducer.getWorkItemHandlers(getDomain().getKsessionRepositoryRoot().get(sessionName), params);
            StatefulKnowledgeSessionDelegate statefulKnowledgeSessionDelegate = new StatefulKnowledgeSessionDelegate(sessionName, ksession, this);
            
            for (Map.Entry<String, WorkItemHandler> wihandler : handlers.entrySet()) {
                ksession.getWorkItemManager().registerWorkItemHandler(wihandler.getKey(), wihandler.getValue());
            }

          if(ksessions.get(sessionName) == null){
            ksessions.put(sessionName, new HashMap<Integer, StatefulKnowledgeSession>());
          }
          ksessions.get(sessionName).put(ksession.getId(), statefulKnowledgeSessionDelegate);

          if(ksessionIds.get(sessionName) == null){
            ksessionIds.put(sessionName, new ArrayList<Integer>());
          }
          ksessionIds.get(sessionName).add(ksession.getId());
          completeOperation(ut, null);
       }
    }

   

    

    @Override
    public Map<Integer, Long> getProcessInstanceIdKsession() {
        return processInstanceIdKsession;
    }

   
    @Override
    public void addProcessInstanceIdKsession(Integer ksessionId,
            Long processInstanceId) {
        this.processInstanceIdKsession.put(ksessionId, processInstanceId);
    }

    @Override
    public Map<Integer, StatefulKnowledgeSession> getKsessionsByName(String ksessionName) {
        return ksessions.get(ksessionName);
    }
    
    @Override
    public StatefulKnowledgeSession getKsessionById(int ksessionId) {
    Collection<Map<Integer, StatefulKnowledgeSession>> values = ksessions.values();
      for(Map<Integer, StatefulKnowledgeSession> value : values){
        StatefulKnowledgeSession session = value.get(ksessionId);
        if(session != null){
          return session;
        }
      }
      return null;
    }
    

    @Override
    public int getSessionForProcessInstanceId(Long processInstanceId) {
        for (int sessionId : processInstanceIdKsession.keySet()) {
            if (processInstanceIdKsession.get(sessionId) == processInstanceId) {
                return sessionId;
            }
        }
        return -1;
    }

    @Override
    public List<Integer> getSessionIdsByName(String ksessionName) {
        return ksessionIds.get(ksessionName);
    }

    @Override
    public Collection<String> getAllSessionsNames() {
        return ksessions.keySet();
    }

    public Map<String, List<String>> getProcessDefinitionNamesBySession() {
        return processDefinitionNamesBySession;
    }

    @Override
    public void addProcessDefinitionToSession(String sessionName,
            String processId) {
        if (processDefinitionNamesBySession.get(sessionName) == null) {
            processDefinitionNamesBySession.put(sessionName, new ArrayList<String>());
        }
        processDefinitionNamesBySession.get(sessionName).add(processId);
    }

    @Override
    public void removeProcessDefinitionFromSession(String sessionName,
            String processId) {
        if (processDefinitionNamesBySession.get(sessionName) != null) {
            processDefinitionNamesBySession.get(sessionName).remove(processId);
        }
    }

    @Override
    public Collection<String> getProcessesInSession(String sessionName) {
        return processDefinitionNamesBySession.get(sessionName);
    }

    public String getProcessInSessionByName(String processDefId) {
        for (String sessionName : processDefinitionNamesBySession.keySet()) {
            for (String processDef : processDefinitionNamesBySession.get(sessionName)) {
                if (processDef.equals(processDefId)) {
                    return sessionName;
                }
            }
        }
        return "";
    }

    @Override
    public void clear() {
      this.ksessions.clear();
      this.ksessionIds.clear();
      this.processInstanceIdKsession.clear();
      this.processDefinitionNamesBySession.clear();
      this.ksessionHandlers.clear();
    }

  @Override
  public int buildSession(String sessionName, String path, boolean streamMode) {
        getDomain().addKsessionRepositoryRoot(sessionName, path);
       
        Iterable<Path> loadProcessFiles = null;
        Iterable<Path> loadRulesFiles = null;
        try {
            loadProcessFiles = fs.loadFilesByType(path, "bpmn");
            loadRulesFiles = fs.loadFilesByType(path, "drl");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        for (Path p : loadProcessFiles) {
            String processString = "";
            try {
              processString = new String(fs.loadFile(p));
            } catch (FileException ex) {
              Logger.getLogger(CDISessionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            String processId = bpmn2Service.findProcessId(processString);
            if(!processId.equals("")){
              getDomain().addAsset(processId, "/"+path +"/"+ p.getFileName().toString());
              getDomain().addProcessDefinitionToKsession(sessionName, p);
              getDomain().addProcessBPMN2ContentToKsession(sessionName, processId, processString);
            }
        }
        
         for (Path p : loadRulesFiles) {            
            System.out.println(" >>> Adding Path to Session- > "+p.toString());
            // TODO automate this in another service
            domain.addRulesDefinitionToKsession(sessionName, p);
        }
        
        processListener.setDomainName(getDomain().getName());
        processListener.setSessionManager(this);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            if (getDomain().getProcessDefinitionFromKsession().get(sessionName) != null) {
                for (Path processPath : getDomain().getProcessDefinitionFromKsession().get(sessionName)) {
                    String processString = new String(ioService.readAllBytes(processPath));
                    String processId = bpmn2Service.findProcessId(processString);
                    
                    if(!processId.equals("")){
                      addProcessDefinitionToSession(sessionName, processId);
                      System.out.println(">>>>>>>>>> Adding Process to KBase - > " + processPath.toString());
                      kbuilder.add(ResourceFactory.newByteArrayResource(processString.getBytes()), ResourceType.BPMN2);
                    }else{
                      System.out.println("EEEEEEEEE> Path - > " + processPath.toString()+" was not added!");
                    }
                }
            }
            if (getDomain().getRulesDefinitionFromKsession().get(sessionName) != null) {
                for (Path rulesPath : getDomain().getRulesDefinitionFromKsession().get(sessionName)) {
                    String rules = new String(ioService.readAllBytes(rulesPath));
                    System.out.println(">>>>>>>>>> Adding Rules to KBase - > " + rulesPath.toString());
                    kbuilder.add(ResourceFactory.newByteArrayResource(rules.getBytes()), ResourceType.DRL);
                }
            }

            if (!kbuilder.getErrors().isEmpty()) {
                KnowledgeBuilderErrors errors = kbuilder.getErrors();
                Iterator<KnowledgeBuilderError> iterator = errors.iterator();
                while (iterator.hasNext()) {
                    System.out.println("Error: " + iterator.next().getMessage());
                }
                return -1;
            }
            KnowledgeBase kbase = null;
            if (streamMode) {
                KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
                config.setOption(EventProcessingOption.STREAM);
                kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
            } else {
                kbase = KnowledgeBaseFactory.newKnowledgeBase();
            }
            
            //kbase.addEventListener(kbaseEventListener);
            
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
            Environment env = EnvironmentFactory.newEnvironment();
            UserTransaction ut = setupEnvironment(env);
            StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);            
            
            EntityManager entityManager = getEntityManager();
            
            for(String processId : processDefinitionNamesBySession.get(sessionName)){
              ProcessDesc processDesc = bpmn2Service.getProcessDesc(processId);
              processDesc.setSessionId(ksession.getId());
              processDesc.setDomainName(domain.getName());
              
              entityManager.persist(processDesc);
            }
            
            ksession.addEventListener(processListener);
            
            ksession.addEventListener(bamProcessListener);

            KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

            handler.addSession(ksession);
            
            // Register the same handler for all the ksessions
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
            // Register the configured handlers
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ksession", ksession);
            Map<String, WorkItemHandler> handlers = workItemHandlerProducer.getWorkItemHandlers(getDomain().getKsessionRepositoryRoot().get(sessionName), params);
            StatefulKnowledgeSessionDelegate statefulKnowledgeSessionDelegate = new StatefulKnowledgeSessionDelegate(sessionName, ksession, this);
            
            for (Map.Entry<String, WorkItemHandler> wihandler : handlers.entrySet()) {
                ksession.getWorkItemManager().registerWorkItemHandler(wihandler.getKey(), wihandler.getValue());
            }

            if(ksessions.get(sessionName) == null){
              ksessions.put(sessionName, new HashMap<Integer, StatefulKnowledgeSession>());
            }
            ksessions.get(sessionName).put(ksession.getId(), statefulKnowledgeSessionDelegate);
            
            if(ksessionIds.get(sessionName) == null){
              ksessionIds.put(sessionName, new ArrayList<Integer>());
            }
            ksessionIds.get(sessionName).add(ksession.getId());
            completeOperation(ut, entityManager);
            return ksession.getId();
        
  }

  /*
   * following are supporting methods to allow execution on application startup
   * as at that time RequestScoped entity manager cannot be used so instead
   * use EntityMnagerFactory and manage transaction manually
   */
  protected EntityManager getEntityManager() {
      try {
          this.em.toString();          
          return this.em;
      } catch (ContextNotActiveException e) {
          EntityManager em = this.emf.createEntityManager();
          return em;
      }
  }
  
  protected UserTransaction setupEnvironment(Environment environment) {
      UserTransaction ut = null;
      try {
          this.em.toString();
          environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, this.em.getEntityManagerFactory());
      } catch (ContextNotActiveException e) {
          try {
              ut = InitialContext.doLookup("java:comp/UserTransaction");
          } catch (Exception ex) {
              try {
                  ut = InitialContext.doLookup(System.getProperty("jbpm.ut.jndi.lookup", "java:jboss/UserTransaction"));
                  ut.begin();
                  environment.set(EnvironmentName.TRANSACTION, InitialContext.doLookup("java:jboss/UserTransaction"));
              } catch (Exception e1) {
                  throw new RuntimeException("Cannot find UserTransaction", e1);
              }
          }
          environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, this.emf);
      }
      
      return ut;
  }
  protected void completeOperation(UserTransaction ut, EntityManager entityManager) {
      if (ut != null) {
          try {
              ut.commit();
              if (entityManager != null) {
                  entityManager.clear();
                  entityManager.close();
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  }

}
