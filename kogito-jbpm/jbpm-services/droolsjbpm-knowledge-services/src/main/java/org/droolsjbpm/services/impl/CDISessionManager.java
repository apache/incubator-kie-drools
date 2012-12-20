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
import javax.inject.Inject;
import javax.inject.Named;

import org.droolsjbpm.services.api.Domain;
import org.droolsjbpm.services.api.SessionManager;
import org.droolsjbpm.services.api.WorkItemHandlerProducer;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.event.listeners.BAM;
import org.droolsjbpm.services.impl.event.listeners.CDIBAMProcessEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIKbaseEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIProcessEventListener;
import org.droolsjbpm.services.impl.event.listeners.CDIRuleAwareProcessEventListener;
import org.droolsjbpm.services.impl.helpers.StatefulKnowledgeSessionDelegate;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.wih.CDIHTWorkItemHandler;
import org.kie.KnowledgeBase;
import org.kie.KieBaseConfiguration;
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
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItemHandler;

/**
 * @author salaboy
 */
public class CDISessionManager implements SessionManager {

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
    private CDIKbaseEventListener kbaseEventListener;
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    private WorkItemHandlerProducer workItemHandlerProducer;
    @Inject
    @Named("fileServiceIOStrategy")
    private IOService ioService;
    private Domain domain;
    // Ksession Name  / Ksession
    private Map<String, StatefulKnowledgeSession> ksessions = new HashMap<String, StatefulKnowledgeSession>();
    // Ksession Name, Ksession Id
    private Map<String, Integer> ksessionIds = new HashMap<String, Integer>();
    // Ksession Name / Process Instance Id 
    private Map<String, Long> processInstanceIdKsession = new HashMap<String, Long>();
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
    public void registerHandlersForSession(String ksessionName) {
        Map<String, WorkItemHandler> handlers = ksessionHandlers.get(ksessionName);
        if (handlers != null) {
            for (String key : handlers.keySet()) {
                ksessions.get(ksessionName).getWorkItemManager().registerWorkItemHandler(key, handlers.get(key));
            }
        } else {
            // Log NONE Handler Registered
        }
    }

    @Override
    public void registerRuleListenerForSession(String ksessionName) {
        ksessions.get(ksessionName).addEventListener(processFactsListener);
    }

    @Override
    public void buildSessions(boolean streamMode) {
        processListener.setDomainName(domain.getName());
        kbaseEventListener.setDomainName(domain.getName());
        processListener.setSessionManager(this);

        Map<String, List<Path>> ksessionProcessDefinitions = domain.getProcessDefinitionFromKsession();
        Map<String, List<Path>> ksessionRulesDefinitions = domain.getRulesDefinitionFromKsession();
        for (String session : ksessionProcessDefinitions.keySet()) {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            if (ksessionProcessDefinitions.get(session) != null) {
                for (Path path : ksessionProcessDefinitions.get(session)) {
                    String processString = new String(ioService.readAllBytes(path));
                    addProcessDefinitionToSession(session, bpmn2Service.findProcessId(processString));
                    System.out.println(">>>>>>>>>> Adding Process to KBase - > " + path.toString());
                    kbuilder.add(ResourceFactory.newByteArrayResource(processString.getBytes()), ResourceType.BPMN2);
                }
            }
            if (ksessionRulesDefinitions.get(session) != null) {
                for (Path path : ksessionRulesDefinitions.get(session)) {
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
            
            kbase.addEventListener(kbaseEventListener);
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();            

            ksession.addEventListener(processListener);
            
            ksession.addEventListener(bamProcessListener);

            KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

            handler.setSession(ksession);
            handler.init();
            // Register the same handler for all the ksessions
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
            // Register the configured handlers
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ksession", ksession);
            Map<String, WorkItemHandler> handlers = workItemHandlerProducer.getWorkItemHandlers(domain.getKsessionRepositoryRoot().get(session), params);
            StatefulKnowledgeSessionDelegate statefulKnowledgeSessionDelegate = new StatefulKnowledgeSessionDelegate(session, ksession, this);
            
            for (Map.Entry<String, WorkItemHandler> wihandler : handlers.entrySet()) {
                ksession.getWorkItemManager().registerWorkItemHandler(wihandler.getKey(), wihandler.getValue());
            }

            ksessions.put(session, statefulKnowledgeSessionDelegate);
            ksessionIds.put(session, ksession.getId());

        }
    }

    public Map<String, StatefulKnowledgeSession> getKsessions() {
        return ksessions;
    }

    @Override
    public void setKsessions(Map<String, StatefulKnowledgeSession> ksessions) {
        this.ksessions = ksessions;
    }

    @Override
    public void addKsession(String name,
            StatefulKnowledgeSession ksession) {
        this.ksessions.put(name, ksession);
    }

    @Override
    public Map<String, Long> getProcessInstanceIdKsession() {
        return processInstanceIdKsession;
    }

    @Override
    public void setProcessInstanceIdKsession(Map<String, Long> processInstanceIdKsession) {
        this.processInstanceIdKsession = processInstanceIdKsession;
    }

    @Override
    public void addProcessInstanceIdKsession(String ksessionName,
            Long processInstanceId) {
        this.processInstanceIdKsession.put(ksessionName, processInstanceId);
    }

    @Override
    public StatefulKnowledgeSession getKsessionByName(String ksessionName) {
        return ksessions.get(ksessionName);
    }

    @Override
    public String getSessionForProcessInstanceId(Long processInstanceId) {
        for (String sessionName : processInstanceIdKsession.keySet()) {
            if (processInstanceIdKsession.get(sessionName) == processInstanceId) {
                return sessionName;
            }
        }
        return null;
    }

    @Override
    public int getSessionIdByName(String ksessionName) {
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
}
