/**
 * Copyright 2011 JBoss Inc
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
package org.jbpm.integration.console;

import org.kie.runtime.StatefulKnowledgeSession;
import org.jbpm.integration.console.kbase.KnowledgeBaseManager;
import org.jbpm.integration.console.kbase.KnowledgeBaseManagerFactory;
import org.jbpm.integration.console.session.SessionManager;
import org.jbpm.integration.console.session.SessionManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This takes care of the (stateful knowledge) session initialization and holder logic 
 * for the {@link CommandDelegate} class. 
 * </p>
 * The class is designed to work as a static instance.
 * </p>
 * Lastly, parts of the drools/jbpm infrastructure need a Session instance to exist in order for certain
 * things, like timer job events, to be able to occur. (This may/hopefully will change in the future). This is
 * why we keep one static instance of a ksession open and available all the time. 
 */
public class StatefulKnowledgeSessionUtil {

    private static final Logger logger = LoggerFactory.getLogger(StatefulKnowledgeSessionUtil.class);
    
    private static KnowledgeBaseManager kbaseManager;
    private static SessionManager sessionManager;
   
    public static KnowledgeBaseManager getKnowledgeBaseManager() {
        if (kbaseManager == null) {
            kbaseManager = KnowledgeBaseManagerFactory.newKnowledgeBaseManager();
        }
        return kbaseManager;
    }

    protected StatefulKnowledgeSessionUtil() {
    }

    public static void dispose() {
        
        if (sessionManager != null) {
            logger.debug("Disposing session manager");
            sessionManager.disposeSession(getStatefulKnowledgeSession());
            SessionHolder.statefulKnowledgeSession = null;
            sessionManager = null;
        }
        
        if (kbaseManager != null) {
            logger.debug("Disposing knowledge base manager");
            kbaseManager.dispose();
            kbaseManager = null;
        }


    }
   
    /**
     * The following two methods illustrate the "Value Holder" design pattern, also known
     * as the "Lazy Initialization Holder" class idiom. <br\>
     * See http://en.wikipedia.org/wiki/Lazy_loading#Value_holder
     * </p> 
     * In the post jdk 1.4 world, this is the correct way to implement lazy initialization in 
     * a multi-threaded environment. 
     * </p>
     * Double-Checked-Locking is an antipattern!
     * </p>
     * See pp. 346-349 of "Java Concurrency in Practice" (B. Goetz) for more info. 
     */
    private static class SessionHolder { 
        public static StatefulKnowledgeSession statefulKnowledgeSession = initializeStatefulKnowledgeSession();
    }
    
    public static StatefulKnowledgeSession getStatefulKnowledgeSession() { 
        if (SessionHolder.statefulKnowledgeSession == null) {
            throw new RuntimeException("Session was not initialized, check previous errors in log");
        }
        return SessionHolder.statefulKnowledgeSession;
    }
    
    /**
     * This method is meant to run within <b>1</b> thread (as is all logic in this class). 
     * </p>
     * @return
     */
    protected static StatefulKnowledgeSession initializeStatefulKnowledgeSession() {
        try {
            // Create knowledge session
            sessionManager = SessionManagerFactory.newSessionManager(getKnowledgeBaseManager().getKnowledgeBase());

            return sessionManager.getSession();
        } catch (Throwable t) {
            logger.error("Could not initialize stateful knowledge session: " + t.getMessage(), t);
            return null;
        }
    }
    
    
    

}
