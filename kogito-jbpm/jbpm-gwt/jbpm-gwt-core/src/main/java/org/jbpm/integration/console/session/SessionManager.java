/**
 * Copyright 2012 JBoss Inc
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
package org.jbpm.integration.console.session;

import org.kie.runtime.StatefulKnowledgeSession;

/**
 * Manager interface for maintaining <code>StatefulKnowledgeSession</code> that includes:
 * <ul>
 *  <li>create of new session</li>
 *  <li>load existing session from data base</li>
 *  <li>register work item handlers</li>
 *  <li>register event listeners</li>
 *  <li>dispose of existing session</li>
 * </ul>
 */
public interface SessionManager {

    /**
     * Returns default session for this manager
     * @return active default session 
     */
    StatefulKnowledgeSession getSession();
    
    /**
     * Returns active session looked up by business key managed by this manager<br/>
     * NOTE: Not every implementation must support this method as it is intended for multi session managers only
     * @param businessKey business key that identifies session
     * @return active session if found by given business key, otherwise null
     */
    StatefulKnowledgeSession getSession(String businessKey);
    
    /**
     * Returns active session looked up by sessionId key managed by this manager<br/>
     * NOTE: Not every implementation must support this method as it is intended for multi session managers only
     * @param sessionId internal key that identifies session
     * @return active session if found by given sessionId key, otherwise null
     */
    StatefulKnowledgeSession getSession(int sessionId);
    
    /**
     * Dispose given session and remove it from manager context
     * @param session session to be disposed
     */
    void disposeSession(StatefulKnowledgeSession session);
}
