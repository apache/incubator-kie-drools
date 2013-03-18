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
package org.jbpm.shared.services.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;

/**
 * Extension of the main <code>SessionManager</code> that provides services specific
 * enhancements. 
 * @author salaboy
 */
public interface ServicesSessionManager extends org.jbpm.shared.services.api.SessionManager {

    void setDomain(Domain domain);
    
    Domain getDomain();
    
    int buildSession(String sessionName, String path, boolean streamMode);
    
    int newKieSession(String groupId, String artifactId, String version, String kbaseName, String sessionName);

    Map<Integer, KieSession> getKsessionsByName(String ksessionName);

    Map<Integer, List<Long>> getProcessInstanceIdKsession();
    
    KieSession getKsessionById(int ksessionId);
    
    void addKsession(String sessionName, KieSession session);

    void addProcessInstanceIdKsession(Integer ksessionId, Long processInstanceId);

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
