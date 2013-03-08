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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import org.drools.core.util.FileManager;
import org.drools.impl.EnvironmentFactory;

import org.jbpm.shared.services.api.Domain;
import org.jbpm.shared.services.api.ServicesSessionManager;
import org.droolsjbpm.services.api.WorkItemHandlerProducer;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.event.listeners.BAM;
import org.droolsjbpm.services.impl.event.listeners.CDIBAMProcessEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIProcessEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIRuleAwareProcessEventListener;
import org.droolsjbpm.services.impl.helpers.KieSessionDelegate;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.jbpm.task.wih.CDIHTWorkItemHandler;
import org.kie.KieBase;
import org.kie.KieServices;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieScanner;
import org.kie.builder.Message;
import org.kie.builder.ReleaseId;
import org.kie.builder.impl.InternalKieModule;
import org.kie.builder.model.KieBaseModel;
import org.kie.builder.model.KieModuleModel;
import org.kie.builder.model.KieSessionModel;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.conf.EqualityBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.KieContainer;
import org.kie.runtime.KieSession;
import org.kie.runtime.conf.ClockTypeOption;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.MavenRepository;
import static org.kie.scanner.MavenRepository.getMavenRepository;
import org.sonatype.aether.repository.RemoteRepository;


/**
 * @author salaboy
 */
@ApplicationScoped
public class SessionManagerImpl implements ServicesSessionManager {

    @Inject
    private JbpmServicesPersistenceManager pm; 

    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject 
    private CDIHTWorkItemHandler htWorkItemHandler;
    @Inject
    private CDIProcessEventListener processListener;
    @Inject
    @BAM
    private CDIBAMProcessEventListener bamProcessListener;
    @Inject
    private CDIRuleAwareProcessEventListener processFactsListener;
 
    @Inject
    private WorkItemHandlerProducer workItemHandlerProducer;
    
    @Inject
    private FileService fs;
    
    protected FileManager fileManager;
    
    @Inject
    private IOService ioService;
    
    private Domain domain;
    
    
    
    // Ksession Name  / sessionId , Ksession
    private Map<String, Map<Integer, KieSession>> ksessions = new HashMap<String, Map<Integer, KieSession>>();
    // Ksession Name, Ksession Id
    private Map<String, List<Integer>> ksessionIds = new HashMap<String, List<Integer>>();
    // Ksession Id / Process Instance Id 
    private Map<Integer, List<Long>> processInstanceIdKsession = new HashMap<Integer, List<Long>>();
    // Process Path / Process Id - String 
    private Map<String, List<String>> processDefinitionNamesBySession = new HashMap<String, List<String>>();
    // Ksession Name / List of handlers
    private Map<String, Map<String, WorkItemHandler>> ksessionHandlers = new HashMap<String, Map<String, WorkItemHandler>>();

    
    public SessionManagerImpl() {
    }

    
    @PostConstruct
    public void init(){
      fileManager = new FileManager();
       fileManager.setUp();
    }

    public SessionManagerImpl(Domain domain) {
        this.domain = domain;
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public void setFs(FileService fs) {
        this.fs = fs;
    }

    public void setIoService(IOService ioService) {
        this.ioService = ioService;
    }
    

    public void setBamProcessListener(CDIBAMProcessEventListener bamProcessListener) {
        this.bamProcessListener = bamProcessListener;
    }

    public void setBpmn2Service(BPMN2DataService bpmn2Service) {
        this.bpmn2Service = bpmn2Service;
    }

    public void setHTWorkItemHandler(CDIHTWorkItemHandler htWorkItemHandler) {
        this.htWorkItemHandler = htWorkItemHandler;
    }

    public void setProcessListener(CDIProcessEventListener processListener) {
        this.processListener = processListener;
    }

    public void setProcessFactsListener(CDIRuleAwareProcessEventListener processFactsListener) {
        this.processFactsListener = processFactsListener;
    }

    public void setWorkItemHandlerProducer(WorkItemHandlerProducer workItemHandlerProducer) {
        this.workItemHandlerProducer = workItemHandlerProducer;
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
    public Map<Integer, List<Long>> getProcessInstanceIdKsession() {
        return processInstanceIdKsession;
    }

   
    @Override
    public void addProcessInstanceIdKsession(Integer ksessionId,
            Long processInstanceId) {
        List<Long> piIds = this.processInstanceIdKsession.get(ksessionId);
        if (piIds == null) {
            piIds = new CopyOnWriteArrayList<Long>();
        }
        piIds.add(processInstanceId);
        this.processInstanceIdKsession.put(ksessionId, piIds);
    }

    @Override
    public Map<Integer, KieSession> getKsessionsByName(String ksessionName) {
        return ksessions.get(ksessionName);
    }
    
    public void addKsession(String sessionName, KieSession session) {
        if(ksessions.get(sessionName) == null){
            ksessions.put(sessionName, new HashMap<Integer, KieSession>());
          }
          ksessions.get(sessionName).put(session.getId(), session);
    }
    
    @Override
    public KieSession getKsessionById(int ksessionId) {
    Collection<Map<Integer, KieSession>> values = ksessions.values();
      for(Map<Integer, KieSession> value : values){
        KieSession session = value.get(ksessionId);
        if(session != null){
          return session;
        }
      }
      return null;
    }
    

    @Override
    public int getSessionForProcessInstanceId(Long processInstanceId) {
        for (int sessionId : processInstanceIdKsession.keySet()) {
            List<Long> piIds = processInstanceIdKsession.get(sessionId);
            if (piIds != null && piIds.contains(processInstanceId)) {
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

 
  
  public int newKieSession(String groupId, String artifactId, String version, String kbaseName, String sessionName){
          Environment env = EnvironmentFactory.newEnvironment();
          UserTransaction ut = setupEnvironment(env);
          EntityManager entityManager = getEntityManager();

          
          MavenRepository repository = getMavenRepository();
          repository.addExtraRepository(getGuvnorM2Repository());
          KieServices ks = KieServices.Factory.get();
          ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);
          KieContainer kieContainer = ks.newKieContainer(releaseId);
          KieScanner scanner = ks.newKieScanner(kieContainer);
          scanner.scanNow();
          
          KieModuleMetaData newKieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(releaseId);
          Map<String, String> processes = newKieModuleMetaData.getProcesses();
          
          
          
          KieBase kieBase = kieContainer.getKieBase(kbaseName);
          

          for(String path : processes.keySet()){   
               String processString = processes.get(path);
              ProcessDesc process = bpmn2Service.findProcessId(processString);
              //getDomain().addAsset(p.getId(), "/"+sessionName +"/"+ path.getFileName().toString());
              //getDomain().addProcessDefinitionToKsession(sessionName, path);
              getDomain().addProcessBPMN2ContentToKsession(sessionName, process.getId(), processString); 
              addProcessDefinitionToSession(sessionName, process.getId());
          }
            
         
          KieSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kieBase, null, env);

          for(String processId : processDefinitionNamesBySession.get(sessionName)){
                ProcessDesc processDesc = bpmn2Service.getProcessDesc(processId);
                processDesc.setSessionId(ksession.getId());
                processDesc.setSessionName(sessionName);
                processDesc.setDomainName(domain.getName());

                entityManager.persist(processDesc);
          }

          ksession.addEventListener(processListener);

          ksession.addEventListener(bamProcessListener);

          //KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

          htWorkItemHandler.addSession(ksession);

          // Register the same handler for all the ksessions
          ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htWorkItemHandler);
          // Register the configured handlers
          Map<String, Object> params = new HashMap<String, Object>();
          params.put("ksession", ksession);
          Map<String, WorkItemHandler> handlers = workItemHandlerProducer.getWorkItemHandlers(getDomain().getKsessionRepositoryRoot().get(sessionName), params);
          KieSessionDelegate kieSessionDelegate = new KieSessionDelegate(sessionName, ksession, this);

          for (Map.Entry<String, WorkItemHandler> wihandler : handlers.entrySet()) {
              ksession.getWorkItemManager().registerWorkItemHandler(wihandler.getKey(), wihandler.getValue());
          }

          if(ksessions.get(sessionName) == null){
            ksessions.put(sessionName, new HashMap<Integer, KieSession>());
          }
          ksessions.get(sessionName).put(ksession.getId(), kieSessionDelegate);

          if(ksessionIds.get(sessionName) == null){
            ksessionIds.put(sessionName, new ArrayList<Integer>());
          }
          ksessionIds.get(sessionName).add(ksession.getId());
          completeOperation(ut, entityManager);
          return ksession.getId();
    
  }
  
  
    
  @Override
  public int buildSession(String sessionName, String path, boolean streamMode) {
        
        //This should be done by guvnor ng
        getDomain().addKsessionRepositoryRoot(sessionName, path);
        
        Collection<ProcessDesc> existingProcesses = getProcessesBySessionName(sessionName);
        Collection<ProcessDesc> loadedProcesses = new ArrayList<ProcessDesc>();
        Iterable<Path> loadProcessFiles = null;
        Iterable<Path> loadRulesFiles = null;
        try {
            loadProcessFiles = fs.loadFilesByType(path, ".+bpmn[2]?$");
            loadRulesFiles = fs.loadFilesByType(path, "drl");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        //THIS IS REDUNDANT AND NEEDS TO BE REMOVED 
          for (Path p : loadProcessFiles) {
            String processString = "";
            try {
               processString = new String(fs.loadFile(p));
            } catch (FileException ex) {
               Logger.getLogger(SessionManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            ProcessDesc process = bpmn2Service.findProcessId(processString);
            if(process != null){
              loadedProcesses.add(process);
            }
          }
        // ENDS REDUNDANT  
        loadedProcesses.removeAll(existingProcesses);
        if (!loadedProcesses.isEmpty()) {  
        
          processListener.setDomainName(getDomain().getName());
          processListener.setSessionManager(this);


          KieServices ks = KieServices.Factory.get();
          ReleaseId releaseId = ks.newReleaseId("org.jbpm", sessionName, "1.0-SNAPSHOT");
          File kPom = createKPom(releaseId);


          InternalKieModule kJar1 = createKieJar(ks, releaseId, sessionName, loadProcessFiles, loadRulesFiles);

          MavenRepository repository = getMavenRepository();
          repository.deployArtifact(releaseId, kJar1, kPom);
          // end guvnor 


          KieContainer kieContainer = ks.newKieContainer(releaseId);
          KieScanner scanner = ks.newKieScanner(kieContainer);

          scanner.scanNow();

          Environment env = EnvironmentFactory.newEnvironment();
          UserTransaction ut = setupEnvironment(env);
          EntityManager entityManager = getEntityManager();


          KieBase kieBase = kieContainer.getKieBase("KBase-"+sessionName);
          KieSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kieBase, null, env);

          for(String processId : processDefinitionNamesBySession.get(sessionName)){
                ProcessDesc processDesc = bpmn2Service.getProcessDesc(processId);
                processDesc.setSessionId(ksession.getId());
                processDesc.setSessionName(sessionName);
                processDesc.setDomainName(domain.getName());

                entityManager.persist(processDesc);
          }

          ksession.addEventListener(processListener);

          ksession.addEventListener(bamProcessListener);

          //KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

          htWorkItemHandler.addSession(ksession);

          // Register the same handler for all the ksessions
          ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htWorkItemHandler);
          // Register the configured handlers
          Map<String, Object> params = new HashMap<String, Object>();
          params.put("ksession", ksession);
          Map<String, WorkItemHandler> handlers = workItemHandlerProducer.getWorkItemHandlers(getDomain().getKsessionRepositoryRoot().get(sessionName), params);
          KieSessionDelegate statefulKnowledgeSessionDelegate = new KieSessionDelegate(sessionName, ksession, this);

          for (Map.Entry<String, WorkItemHandler> wihandler : handlers.entrySet()) {
              ksession.getWorkItemManager().registerWorkItemHandler(wihandler.getKey(), wihandler.getValue());
          }

          if(ksessions.get(sessionName) == null){
            ksessions.put(sessionName, new HashMap<Integer, KieSession>());
          }
          ksessions.get(sessionName).put(ksession.getId(), statefulKnowledgeSessionDelegate);

          if(ksessionIds.get(sessionName) == null){
            ksessionIds.put(sessionName, new ArrayList<Integer>());
          }
          ksessionIds.get(sessionName).add(ksession.getId());
          completeOperation(ut, entityManager);
          return ksession.getId();
        }else{
           // processes are not changed return already existing session
            return existingProcesses.iterator().next().getSessionId();
        }
  }

  protected Collection<ProcessDesc> getProcessesBySessionName(String sessionName) {
      List<ProcessDesc> processes = getEntityManager().createQuery("select pd from ProcessDesc pd where pd.sessionName=:sessionName GROUP BY pd.id ORDER BY pd.dataTimeStamp DESC")
              .setParameter("sessionName", sessionName).getResultList();
      return processes;
  }



/*
   * following are supporting methods to allow execution on application startup
   * as at that time RequestScoped entity manager cannot be used so instead
   * use EntityMnagerFactory and manage transaction manually
   */
  protected EntityManager getEntityManager() {
      try {
          ((JbpmServicesPersistenceManagerImpl)this.pm).getEm().toString();          
          return ((JbpmServicesPersistenceManagerImpl)this.pm).getEm();
      } catch (ContextNotActiveException e) {
          EntityManager em = ((JbpmServicesPersistenceManagerImpl)this.pm).getEmf().createEntityManager();
          return em;
      }
  }
  
  protected UserTransaction setupEnvironment(Environment environment) {
      UserTransaction ut = null;
      try {
          this.pm.toString();
          environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, ((JbpmServicesPersistenceManagerImpl)this.pm).getEm().getEntityManagerFactory());
      } catch (ContextNotActiveException e) {
          try {
              ut = InitialContext.doLookup("java:comp/UserTransaction");
          } catch (Exception ex) {
              try {
                  ut = InitialContext.doLookup(System.getProperty("jbpm.ut.jndi.lookup", "java:jboss/UserTransaction"));
                  
              } catch (Exception e1) {
                  throw new RuntimeException("Cannot find UserTransaction", e1);
              }
          }
          try {
              ut.begin();
              environment.set(EnvironmentName.TRANSACTION, ut);
          } catch (Exception ex) {
              
          }
          environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, ((JbpmServicesPersistenceManagerImpl)this.pm).getEmf());
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
  
  //Remove this code, because this should be done by guvnor ng
   private File createKPom(ReleaseId releaseId)  {
    File pomFile = null;
            pomFile = fileManager.newFile("pom.xml");
        try {
            fileManager.write(pomFile, getPom(releaseId));
        } catch (Exception ex) {
            Logger.getLogger(SessionManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    return pomFile;
  }
   
   
   
   
   
   @PreDestroy
   public void destroy(){
     fileManager.tearDown();
   }
   
   protected String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                "  <version>" + releaseId.getVersion() + "</version>\n" +
                "\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += "  <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        return pom;
    }
   
   protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, String sessionName, 
                                              Iterable<Path> processPaths, Iterable<Path> rulesPaths ) {
     
        
        KieFileSystem kfs = createKieFileSystemWithKProject(sessionName, ks);
        kfs.writePomXML( getPom(releaseId) );

        for(Path path : rulesPaths){
          String rulesString = new String(ioService.readAllBytes(path));
          domain.addRulesDefinitionToKsession(sessionName, path);
          kfs.write("src/main/resources/KBase-"+sessionName+"/" + path.getFileName().toString(), rulesString);
        }
        
        
        for(Path path : processPaths){
          String processString = new String(ioService.readAllBytes(path));
          ProcessDesc process = bpmn2Service.findProcessId(processString);
            if(process != null){
              getDomain().addAsset(process.getId(), path.toString());
              getDomain().addProcessDefinitionToKsession(sessionName, path);
              getDomain().addProcessBPMN2ContentToKsession(sessionName, process.getId(), processString);
              
              addProcessDefinitionToSession(sessionName, process.getId());
              
            }
          kfs.write("src/main/resources/KBase-"+sessionName+"/" + path.getFileName().toString(), processString);
        }
        
          KieBuilder kieBuilder = ks.newKieBuilder(kfs);
          if(!kieBuilder.buildAll().getResults().getMessages().isEmpty()){
            for(Message message: kieBuilder.buildAll().getResults().getMessages()){
                System.out.println("Error Message: ("+message.getPath()+") "+message.getText());
            }
            throw new RuntimeException("There are errors builing the package, please check your knowledge assets!");
          }
        
        return ( InternalKieModule ) kieBuilder.getKieModule();
    }

    

    protected KieFileSystem createKieFileSystemWithKProject(String name, KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-"+name)
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession-"+name)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") );
       
        
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }

    private RemoteRepository getGuvnorM2Repository() {
        File m2RepoDir = new File( "repository" );
          if (!m2RepoDir.exists()) {
             return null;
           }
          try {
                String localRepositoryUrl = m2RepoDir.toURI().toURL().toExternalForm();
              return new RemoteRepository( "guvnor-m2-repo", "default", localRepositoryUrl );
          } catch (MalformedURLException e) { }
          return null;
      }



}
