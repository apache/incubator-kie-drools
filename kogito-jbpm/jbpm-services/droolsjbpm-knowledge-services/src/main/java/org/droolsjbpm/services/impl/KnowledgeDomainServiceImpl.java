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

import org.droolsjbpm.services.api.FileException;
import org.droolsjbpm.services.api.FileService;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.event.listeners.CDIKbaseEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIProcessEventListener;
import org.jbpm.task.wih.CDIHTWorkItemHandler;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.io.ResourceFactory;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    private Map<String, String> availableProcesses = new HashMap<String, String>();

    private long   id;
    private String domainName;
    private long   parentId;

    public KnowledgeDomainServiceImpl() {
        this.id = 0;
        this.domainName = "My Business Unit";
    }

    @PostConstruct
    public void createDomain() {

        kbaseEventListener.setDomainName( domainName );
        processListener.setDomainName( domainName );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Iterable<Path> loadFilesByType = null;
        try {
            loadFilesByType = fs.loadFilesByType( "examples/general/", "bpmn" );
        } catch ( FileException ex ) {
            Logger.getLogger( KnowledgeDomainServiceImpl.class.getName() ).log( Level.SEVERE, null, ex );
        }
        for ( Path p : loadFilesByType ) {
            System.out.println( " >>>>>>>>>>>>>>>>>>>>>>>>>>> Loading -> " + p.toString() );
            String processString = new String( ioService.readAllBytes( p ) );
            availableProcesses.put( bpmn2Service.findProcessId( processString ), processString );
            kbuilder.add( ResourceFactory.newByteArrayResource( ioService.readAllBytes( p ) ), ResourceType.BPMN2 );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addEventListener( kbaseEventListener );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.addEventListener( processListener );
        KnowledgeRuntimeLoggerFactory.newConsoleLogger( ksession );

        handler.setSession( ksession );
        handler.init();
        ksession.getWorkItemManager().registerWorkItemHandler( "Human Task", handler );

        ksessions.put( "default", ksession );

    }

    public void registerSession( String businessKey,
                                 StatefulKnowledgeSession ksession ) {
        ksessions.put( businessKey, ksession );
    }

    public StatefulKnowledgeSession getSession( long sessionId ) {
        throw new NotImplementedException();
    }

    public StatefulKnowledgeSession getSessionByBusinessKey( String businessKey ) {
        return ksessions.get( businessKey );
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

    public void setId( Long id ) {
        this.id = id;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName( String domainName ) {
        this.domainName = domainName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId( Long parentId ) {
        this.parentId = parentId;
    }

    public Map<String, String> getAvailableProcesses() {
        return availableProcesses;
    }

}
