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
package org.droolsjbpm.services.api;

import java.util.Collection;
import java.util.Map;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItemHandler;

/**
 *
 * @author salaboy
 */
public interface SessionManager {

    void setDomain(Domain domain);

    void buildSessions(boolean streamMode);

    Map<String, StatefulKnowledgeSession> getKsessions();

    void setKsessions(Map<String, StatefulKnowledgeSession> ksessions);

    void addKsession(String name, StatefulKnowledgeSession ksession);

    StatefulKnowledgeSession getKsessionByName(String ksessionName);

    Map<String, Long> getProcessInstanceIdKsession();

    void setProcessInstanceIdKsession(Map<String, Long> processInstanceIdKsession);

    void addProcessInstanceIdKsession(String ksessionName, Long processInstanceId);

    String getSessionForProcessInstanceId(Long processInstanceId);

    int getSessionIdByName(String ksessionName);

    Collection<String> getAllSessionsNames();

    void addProcessDefinitionToSession(String sessionName, String processId);

    void removeProcessDefinitionFromSession(String sessionName, String processId);

    Collection<String> getProcessesInSession(String sessionName);

    String getProcessInSessionByName(String processDefId);

    void addKsessionHandler(String ksessionName, String handlerName, WorkItemHandler handler);

    void registerHandlersForSession(String ksessionName);
    
    void registerRuleListenerForSession(String ksessionName);
    
}
