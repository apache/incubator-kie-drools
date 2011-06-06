/*
 * Copyright 2010 JBoss Inc
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

package org.drools.agent;

import org.drools.ChangeSet;
import org.drools.KnowledgeBase;
import org.drools.SystemEventListener;
import org.drools.event.knowledgeagent.KnowledgeAgentEventListener;
import org.drools.io.Resource;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatelessKnowledgeSession;

/**
 * The KnowledgeAgentFactory provides detailed information on how to create and use the KnowledgeAgent.
 * 
 * @see org.drools.agent.KnowledgeAgentFactory
 * @see org.drools.agent.KnowledgeAgentConfiguration
 */
public interface KnowledgeAgent {

    void addEventListener(KnowledgeAgentEventListener listener);
    
    void removeEventListener(KnowledgeAgentEventListener listener);


    public enum ResourceStatus{
        RESOURCE_ADDED,
        RESOURCE_MODIFIED,
        RESOURCE_REMOVED;
    }

    /**
     * 
     * @return
     *    The name
     */
    String getName();

    /**
     * Returns the cached KnowledgeBase
     * @return
     *     The KnowledgeBase
     */
    KnowledgeBase getKnowledgeBase();
    
    
    /**
     * StatelessKnowledgeSession created from here will always have the execute() method called against the latest built KnowledgeBase
     * @return
     */
    StatelessKnowledgeSession newStatelessKnowledgeSession();
    
    /**
     * StatelessKnowledgeSession created from here will always have the execute() method called against the latest built KnowledgeBase
     * @return
     */    
    StatelessKnowledgeSession newStatelessKnowledgeSession(KnowledgeSessionConfiguration conf);

    void monitorResourceChangeEvents(boolean monitor);

    void applyChangeSet(Resource resource);

    void applyChangeSet(ChangeSet changeSet);

    void setSystemEventListener(SystemEventListener listener);

    void dispose();
}
