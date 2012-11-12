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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.kie.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract manager that provides some helpful methods used by concrete <code>SessionManager</code> implementations
 *
 */
public abstract class AbstractSessionManager implements SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSessionManager.class);
    
    /**
     * Retrieves session id from serialized file named jbpmSessionId.ser from given location.
     * @param location directory where jbpmSessionId.ser file should be
     * @return sessionId if file was found otherwise 0
     */
    protected int getPersistedSessionId(String location) {
        File sessionIdStore = new File(location + File.separator + "jbpmSessionId.ser");
        if (sessionIdStore.exists()) {
            Integer knownSessionId = null; 
            FileInputStream fis = null;
            ObjectInputStream in = null;
            try {
                fis = new FileInputStream(sessionIdStore);
                in = new ObjectInputStream(fis);
                
                knownSessionId = (Integer) in.readObject();
                
                return knownSessionId.intValue();
                
            } catch (Exception e) {
                return 0;
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
            
        } else {
            return 0;
        }
    }
    
    /**
     * Stores gives ksessionId in a serialized file in given location under jbpmSessionId.ser file name
     * @param location directory where serialized file should be stored
     * @param ksessionId value of ksessionId to be stored
     */
    protected void persistSessionId(String location, int ksessionId) {
        if (location == null) {
            return;
        }
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(location + File.separator + "jbpmSessionId.ser");
            out = new ObjectOutputStream(fos);
            out.writeObject(Integer.valueOf(ksessionId));
            out.close();
        } catch (IOException ex) {
            logger.warn("Error when persisting known session id", ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    /**
     * Looks up session from JNDI by given business key
     * @param businessKey JNDI name to look up
     * @return session instance if found in JNDI otherwise null
     */
    protected StatefulKnowledgeSession lookUpInJNDI(String businessKey) {
        try {
            InitialContext ctx = new InitialContext();
        
        
            return (StatefulKnowledgeSession) ctx.lookup(businessKey);
        } catch (NamingException e) {
            logger.warn("Error when looking up session in JNDI", e);
            return null;
        }
    }
    
    /**
     * Removes object bound to given business key from JNDI
     * @param businessKey JNDI name to be unbound
     */
    protected void removeFromJNDI(String businessKey) {
        try {
            InitialContext ctx = new InitialContext();
            ctx.unbind(businessKey);
        } catch (Exception e) {
            logger.error("Error when removing session from JNDI", e);
        }
    }
    
    /**
     * Bind given session into JNDI under given JNDI name as business key
     * @param businessKey JNDI name to bind the session
     * @param session session to be bind into JNDI
     */
    protected void bindToJNDI(String businessKey, StatefulKnowledgeSession session) {
        try {
            Context ctx = new InitialContext();
            Name name = ctx.getNameParser("").parse(businessKey);
            
            int size = name.size();
            String atom = name.get(size - 1);
            Context parentCtx = createSubcontext(ctx, name.getPrefix(size - 1));
            parentCtx.bind(atom, session);
            
        } catch (Exception e) {
            logger.error("Error when binding session to JNDI under key " + businessKey, e);
        }
    }
    
    /**
     * Utility method to create subcontext in JNDI if needed
     * @param ctx parent context
     * @param name JNDI name including subcontexts
     * @return returns last sub context for given name
     * @throws NamingException
     */
    protected Context createSubcontext(Context ctx, Name name)
            throws NamingException {
        Context subctx = ctx;
        for (int pos = 0; pos < name.size(); pos++) {
            String ctxName = name.get(pos);
            try {
                subctx = (Context) ctx.lookup(ctxName);
            } catch (NameNotFoundException e) {
                subctx = ctx.createSubcontext(ctxName);
            }
            ctx = subctx;
        }
        return subctx;
    }

}
