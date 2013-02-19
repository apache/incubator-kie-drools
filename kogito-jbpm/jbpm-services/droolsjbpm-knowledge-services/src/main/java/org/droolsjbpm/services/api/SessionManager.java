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
import java.util.List;
import java.util.Map;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItemHandler;

/**
 *
 * @author salaboy
 */
public interface SessionManager {

    void setDomain(Domain domain);
    
    Domain getDomain();
    
    int buildSession(String sessionName, String path, boolean streamMode);

    Map<Integer, KieSession> getKsessionsByName(String ksessionName);

    Map<Integer, Long> getProcessInstanceIdKsession();

    KieSession getKsessionById(int ksessionId);

    void addProcessInstanceIdKsession(Integer ksessionId, Long processInstanceId);

    int getSessionForProcessInstanceId(Long processInstanceId);

    List<Integer> getSessionIdsByName(String ksessionName);

    Collection<String> getAllSessionsNames();

    void addProcessDefinitionToSession(String sessionName, String processId);

    void removeProcessDefinitionFromSession(String sessionName, String processId);

    Collection<String> getProcessesInSession(String sessionName);

    String getProcessInSessionByName(String processDefId);

    void addKsessionHandler(String ksessionName, String handlerName, WorkItemHandler handler);

    void registerHandlersForSession(String ksessionName, int version);
    
    void registerRuleListenerForSession(String ksessionName, int version);
    
    void clear();
    
}
