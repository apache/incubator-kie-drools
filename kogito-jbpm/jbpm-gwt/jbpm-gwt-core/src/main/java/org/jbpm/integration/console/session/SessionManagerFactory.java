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

import java.lang.reflect.Constructor;

import org.kie.KnowledgeBase;

/**
 * Factory for providing <code>SessionManager</code> instances
 */
public class SessionManagerFactory {

    /**
     * Returns new instance of <code>SessionManager</code> interface that is either default implementation
     * or custom one that is defined by system property: <code>jbpm.session.manager</code><br/>
     * Example:<br/>
     * -Djbpm.session.manager=com.company.jbpm.CustomSessionManager
     * <br/>
     * Custom implementation must implement <code>org.jbpm.integration.console.session.SessionManager</code> interface
     * or extend <code>org.jbpm.integration.console.session.AbstractSessionManager</code><br/>
     * Moreover such custom implementation must provide a constructor that accepts single parameter 
     * of a <code>KnowledgeBase</code> type 
     * 
     * Default implementation is <code>org.jbpm.integration.console.session.MVELSingleSessionManager</code>
     * that builds stateful session based on session template given in either:
     * <ul>
     *  <li>default.session.templates - template that is bundled with jbpm as defaults - read only</li>
     *  <li>session.templates - custom configuration of jbpm console session</li>
     * </ul>
     * @param kbase knowledge base to be used to create session
     * @return new instance of <code>KnowledgeBaseManager</code>
     */
    @SuppressWarnings("unchecked")
    public static SessionManager newSessionManager(KnowledgeBase kbase) {
        String sessionManager = System.getProperty("jbpm.session.manager");
        if (sessionManager == null) {
            return new MVELSingleSessionManager(kbase);
        }
        
        SessionManager sessionManagerInstance = null;
        try {
            // build session manager based on given class
            Class<SessionManager> sessionManagerClass = (Class<SessionManager>) Class.forName(sessionManager);
            Constructor<SessionManager> c = sessionManagerClass.getConstructor(KnowledgeBase.class);
            sessionManagerInstance = c.newInstance(kbase);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create SessionManager from class " + sessionManager, e);
        }
        
        return sessionManagerInstance;
    }
}
