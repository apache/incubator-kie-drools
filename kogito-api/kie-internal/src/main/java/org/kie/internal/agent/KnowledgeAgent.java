/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.agent;

import org.kie.internal.ChangeSet;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.SystemEventListener;
import org.kie.internal.event.knowledgeagent.KnowledgeAgentEventListener;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSessionConfiguration;

/**
 * The KnowledgeAgentFactory provides detailed information on how to create and use the KnowledgeAgent.
 * 
 * @see org.kie.internal.agent.KnowledgeAgentFactory
 * @see org.kie.internal.agent.KnowledgeAgentConfiguration
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
    StatelessKnowledgeSession newStatelessKnowledgeSession(KieSessionConfiguration conf);

    void monitorResourceChangeEvents(boolean monitor);

    void applyChangeSet(Resource resource);

    void applyChangeSet(ChangeSet changeSet);

    void setSystemEventListener(SystemEventListener listener);

    void dispose();
}
