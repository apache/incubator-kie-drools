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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ClassPathResource;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.impl.event.listeners.CDIKbaseEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIProcessEventListener;
import org.jbpm.task.wih.CDIHTWorkItemHandler;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
public class KnowledgeDomainServiceImpl implements KnowledgeDomainService{

    private Map<String, StatefulKnowledgeSession> ksessions = new HashMap<String, StatefulKnowledgeSession>();
    
    @Inject 
    private CDIHTWorkItemHandler handler;
    
    @Inject 
    private CDIProcessEventListener processListener;
    
    @Inject
    private CDIKbaseEventListener kbaseEventListener;
    // This must be replaced by the VFS
    private Map<String, String> availableProcesses = new HashMap<String, String>();
    
    private long   id;
    private String domainName;
    private long   parentId;

    public KnowledgeDomainServiceImpl() {
        this.id = 0;
        this.domainName = "My Business Unit";
    }
    
    
    
    @PostConstruct
    public void init(){
        kbaseEventListener.setDomainName(domainName);
        processListener.setDomainName(domainName);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        String processLocation = "example/humanTask.bpmn";
        String processId = "org.jbpm.writedocument";
        
        
        ClassPathResource classPathResource = new ClassPathResource(processLocation);
        
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(classPathResource.getInputStream(), writer, "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        String processString = writer.toString();
        availableProcesses.put(processId, processString);
        
        kbuilder.add(new ByteArrayResource(processString.getBytes()), ResourceType.BPMN2);
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addEventListener(kbaseEventListener);
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        ksession.addEventListener(processListener);
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
        
        handler.setSession(ksession);
        handler.init();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        ksessions.put("default", ksession);
        
        
        
    }
    
    public void registerSession(String businessKey, StatefulKnowledgeSession ksession) {
        ksessions.put(businessKey, ksession);
    }

    public StatefulKnowledgeSession getSession(long sessionId) {
        throw new NotImplementedException();
    }

    public StatefulKnowledgeSession getSessionByBusinessKey(String businessKey) {
        return ksessions.get(businessKey);
    }
 
    public Collection<StatefulKnowledgeSession> getSessions() {
        return ksessions.values();
    }

    public Collection<String> getSessionsNames() {
        return ksessions.keySet();
    }
    
    public int getAmountOfSessions() {
        return ksessions.size();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Map<String, String> getAvailableProcesses() {
        return availableProcesses;
    }

}
