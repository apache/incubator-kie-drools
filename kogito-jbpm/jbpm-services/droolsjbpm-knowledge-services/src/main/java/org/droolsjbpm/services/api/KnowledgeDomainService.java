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

/**
 *
 * @author salaboy
 */
public interface KnowledgeDomainService {

    void registerSession(String businessKey, StatefulKnowledgeSession ksession);

    StatefulKnowledgeSession getSession(long sessionId);

    StatefulKnowledgeSession getSessionByBusinessKey(String businessKey);

    Collection<StatefulKnowledgeSession> getSessions();

    int getAmountOfSessions();

    Collection<String> getSessionsNames();
    
    Long getId();

    void setId(Long id);

    String getDomainName();

    void setDomainName(String domainName);

    Long getParentId();

    void setParentId(Long parentId);
    
    Map<String, String> getAvailableProcesses();
}
