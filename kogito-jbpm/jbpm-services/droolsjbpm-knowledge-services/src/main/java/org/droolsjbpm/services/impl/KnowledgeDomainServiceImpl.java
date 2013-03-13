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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.api.RulesNotificationService;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.example.MoveFileWorkItemHandler;
import org.droolsjbpm.services.impl.example.NotificationWorkItemHandler;
import org.droolsjbpm.services.impl.example.TriggerTestsWorkItemHandler;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.shared.services.api.Domain;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.shared.services.api.ServicesSessionManager;
import org.jbpm.shared.services.cdi.Startup;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.wih.LocalHTWorkItemHandler;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatefulKnowledgeSession;

/**
 * @author salaboy
 */
@ApplicationScoped
@Transactional
@Startup
public class KnowledgeDomainServiceImpl implements KnowledgeDomainService {


  private Map<String, StatefulKnowledgeSession> ksessions = new HashMap<String, StatefulKnowledgeSession>();
  @Inject
  private LocalHTWorkItemHandler handler;
  @Inject
  private BPMN2DataService bpmn2Service;
  @Inject
  private FileService fs;
  @Inject
  private IOService ioService;
  @Inject
  private TaskServiceEntryPoint taskService;
  @Inject
  private ServicesSessionManager sessionManager;
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

    sessionManager.setDomain(domain);
    Iterable<Path> availableDirectories = fs.listDirectories("processes/");

    for (Path p : availableDirectories) {

      sessionManager.buildSession(p.getFileName().toString(), "processes/" + p.getFileName().toString(), true);

    }


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
  public Map<String, String> getAvailableProcessesPaths() {
    return domain.getAssetsDefs();
  }

  @Override
  public Map<Integer, KieSession> getSessionsByName(String ksessionName) {
    return sessionManager.getKsessionsByName(ksessionName);

  }

  public String getProcessInSessionByName(String processDefId) {
    return sessionManager.getProcessInSessionByName(processDefId);
  }

  public int getSessionForProcessInstanceId(long processInstanceId) {
    return sessionManager.getSessionForProcessInstanceId(processInstanceId);
  }

  @Override
  public KieSession getSessionById(int sessionId) {
    return sessionManager.getKsessionById(sessionId);
  }

  @Override
  public int newKieSession(String groupId, String artifactId, String version, String kbaseName, String sessionName) {
    return sessionManager.newKieSession(groupId, artifactId, version, kbaseName, sessionName);
  }

  @Override
  public String getProcessAssetPath(String processId) {
    return sessionManager.getDomain().getAssetsDefs().get(processId);
  }
}
