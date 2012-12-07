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
import org.droolsjbpm.services.api.SessionManager;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.event.listeners.CDIKbaseEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIProcessEventListener;
import org.jbpm.task.wih.CDIHTWorkItemHandler;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.runtime.StatefulKnowledgeSession;

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
    @Named("ioStrategy")
    private IOService ioService;
    
    @Inject
    private SessionManager sessionManager;
    
    private Domain domain;
    

    public KnowledgeDomainServiceImpl() {
        domain = new SimpleDomainImpl("myDomain");

    }

    @PostConstruct
    public void createDomain() {
        sessionManager.setDomain(domain);

        Iterable<Path> releaseFiles = null;
        Iterable<Path> exampleFiles = null;
        try {
            releaseFiles = fs.loadFilesByType("examples/release/", "bpmn");
            exampleFiles = fs.loadFilesByType("examples/general/", "bpmn");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Path p : releaseFiles) {
            String kSessionName = "releaseSession";
            domain.addKsessionAsset(kSessionName, p);
            
            // TODO automate this in another service
            String processString = new String( ioService.readAllBytes( p ) );
            domain.addProcessToKsession(kSessionName, bpmn2Service.findProcessId( processString ), processString );
        }
        for (Path p : exampleFiles) {
            String kSessionName = "generalSession";
            domain.addKsessionAsset("generalSession", p);
            // TODO automate this in another service
            String processString = new String( ioService.readAllBytes( p ) );
            domain.addProcessToKsession(kSessionName, bpmn2Service.findProcessId( processString ), processString );
        }

        sessionManager.buildSessions();

//        sessionManager.addKsessionHandler("myKsession", "MoveToStagingArea", new DoNothingWorkItemHandler());
//        sessionManager.addKsessionHandler("myKsession", "MoveToTest", new DoNothingWorkItemHandler());
//        sessionManager.addKsessionHandler("myKsession", "TriggerTests", new DoNothingWorkItemHandler());
//        sessionManager.addKsessionHandler("myKsession", "MoveBackToStaging", new DoNothingWorkItemHandler());
//        sessionManager.addKsessionHandler("myKsession", "MoveToProduction", new DoNothingWorkItemHandler());
//        sessionManager.addKsessionHandler("myKsession", "ApplyChangestoRuntimes", new DoNothingWorkItemHandler());
//        sessionManager.addKsessionHandler("myKsession", "Email", new DoNothingWorkItemHandler());

        sessionManager.registerHandlersForSession("releaseSession");
         

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
    
}
