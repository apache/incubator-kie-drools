/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.droolsjbpm.services.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.droolsjbpm.services.api.Domain;

import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.api.RulesNotificationService;
import org.droolsjbpm.services.api.SessionManager;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.event.listeners.CDIKbaseEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIProcessEventListener;
import org.droolsjbpm.services.impl.example.MoveFileWorkItemHandler;
import org.droolsjbpm.services.impl.example.NotificationWorkItemHandler;
import org.droolsjbpm.services.impl.example.TriggerTestsWorkItemHandler;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.wih.CDIHTWorkItemHandler;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

/**
 * @author salaboy
 */
@ApplicationScoped
public class KnowledgeDomainServiceImpl implements KnowledgeDomainService {

    private Map<String, StatefulKnowledgeSession> ksessions = new HashMap<String, StatefulKnowledgeSession>();
    @Inject
    private CDIHTWorkItemHandler handler;
    @Inject
    private CDIProcessEventListener processListener;
    @Inject
    private CDIKbaseEventListener kbaseEventListener;
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    private FileService fs;
    @Inject
    @Named("fileServiceIOStrategy")
    private IOService ioService;
    
    @Inject
    private TaskServiceEntryPoint taskService;
    
    @Inject
    private SessionManager sessionManager;
    
    @Inject
    private MoveFileWorkItemHandler moveFilesWIHandler;
    
    @Inject
    private TriggerTestsWorkItemHandler triggerTestsWorkItemHandler;
    
    @Inject
    private NotificationWorkItemHandler notificationWorkItemHandler;
    
    @Inject
    private RulesNotificationService rulesNotificationService;
    
    private Domain domain;
    

    public KnowledgeDomainServiceImpl() {
        domain = new SimpleDomainImpl("myDomain");

    }

    @PostConstruct
    public void createDomain() {
        // TODO: Do this based on configuration and use the new CDI approach
        sessionManager.setDomain(domain);

        Iterable<Path> releaseProcessesFiles = null;
        Iterable<Path> releaseRulesFiles = null;
        Iterable<Path> exampleProcessesFiles = null;
        try {
            releaseProcessesFiles = fs.loadFilesByType("examples/release/", "bpmn");
            releaseRulesFiles = fs.loadFilesByType("examples/release/", "drl");
            exampleProcessesFiles = fs.loadFilesByType("examples/general/", "bpmn");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        String kSessionName = "releaseSession";
        domain.addKsessionRepositoryRoot(kSessionName, "examples/release/");
        for (Path p : releaseProcessesFiles) {
            
            domain.addProcessDefinitionToKsession(kSessionName, p);
            System.out.println(" >>> Adding Path to ReleaseSession- > "+p.toString());
            // TODO automate this in another service
            String processString = new String( ioService.readAllBytes( p ) );
            domain.addProcessBPMN2ContentToKsession(kSessionName, bpmn2Service.findProcessId( processString ), processString );
        }
        kSessionName = "releaseSession";
        for (Path p : releaseRulesFiles) {            
            System.out.println(" >>> Adding Path to ReleaseSession- > "+p.toString());
            // TODO automate this in another service
            domain.addRulesDefinitionToKsession(kSessionName, p);
        }
        
        kSessionName = "generalSession";
        domain.addKsessionRepositoryRoot(kSessionName, "examples/general/");
        for (Path p : exampleProcessesFiles) {
            domain.addProcessDefinitionToKsession("generalSession", p);
            System.out.println(" >>> Adding Path to GeneralSession - > "+p.toString());
            // TODO automate this in another service
            String processString = new String( ioService.readAllBytes( p ) );
            domain.addProcessBPMN2ContentToKsession(kSessionName, bpmn2Service.findProcessId( processString ), processString );
        }
        
        

        sessionManager.buildSessions(true);
        
        

        sessionManager.addKsessionHandler("releaseSession", "MoveToStagingArea",moveFilesWIHandler);
        sessionManager.addKsessionHandler("releaseSession", "MoveToTest", moveFilesWIHandler);
        sessionManager.addKsessionHandler("releaseSession", "TriggerTests", triggerTestsWorkItemHandler);
        sessionManager.addKsessionHandler("releaseSession", "MoveBackToStaging", moveFilesWIHandler);
        sessionManager.addKsessionHandler("releaseSession", "MoveToProduction", moveFilesWIHandler);
        sessionManager.addKsessionHandler("releaseSession", "Email", notificationWorkItemHandler);

        sessionManager.registerHandlersForSession("releaseSession");
        
        sessionManager.registerRuleListenerForSession("releaseSession");
         
        sessionManager.getKsessionByName("releaseSession").setGlobal("rulesNotificationService", rulesNotificationService);
        
        sessionManager.getKsessionByName("releaseSession").setGlobal("taskService", taskService);
    }

    @Override
    public Collection<String> getSessionsNames() {
        return sessionManager.getAllSessionsNames();
    }

    @Override
    public int getAmountOfSessions() {
        return sessionManager.getAllSessionsNames().size();
    }

    @Override
    public Map<String, String> getAvailableProcesses() {
        return domain.getAllProcesses();
    }

    @Override
    public StatefulKnowledgeSession getSessionByName(String ksessionName) {
        return sessionManager.getKsessionByName(ksessionName);
        
    }

    public String getProcessInSessionByName(String processDefId){
        return sessionManager.getProcessInSessionByName(processDefId);
    }
    
    
    private class DoNothingWorkItemHandler implements WorkItemHandler {

        @Override
        public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
            for(String k : wi.getParameters().keySet()){
                System.out.println("Key = "+ k + " - value = "+wi.getParameter(k));
            }
            
            wim.completeWorkItem(wi.getId(), null);
        }

        @Override
        public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
        }
    }
    
    
    
}
