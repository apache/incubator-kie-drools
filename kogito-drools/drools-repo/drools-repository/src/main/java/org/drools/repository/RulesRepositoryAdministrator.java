/**
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

package org.drools.repository;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is for administering the rules repo.
 * Any "sensitive" actions can happen in here.
 * 
 * @author Michael Neale
 *
 */
public class RulesRepositoryAdministrator {

    private static final Logger log = LoggerFactory.getLogger(RulesRepositoryAdministrator.class);
    
    private final Session session;

    /**
     * Pass in a session that is capable of doing admin-ey type stuff.
     */
    public RulesRepositoryAdministrator(Session session) {
        this.session = session;
    }
    
    static boolean isNamespaceRegistered(Session session) throws RepositoryException {
        Workspace ws = session.getWorkspace();
        //no need to set it up again, skip it if it has.
        String uris[] = ws.getNamespaceRegistry().getURIs();            
        for ( int i = 0; i < uris.length; i++ ) {
            if (RulesRepository.DROOLS_URI.equals( uris[i]) ) {
                return true;
            }
        }        
        return false;
    }
    
    /**
     * This will tell you if the repository currently connected to is initialized.
     * This includes the basic data/folders, as well as the name space registered.
     * The name space registration is JCR implementation dependent (jackrabbit is the default).
     */
    public boolean isRepositoryInitialized() {
        try {
            return isNamespaceRegistered( session ) && 
            session.getRootNode().hasNode( RulesRepository.RULES_REPOSITORY_NAME );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }
    
    /**
     * Clears out the entire tree below the rules repository node of the JCR repository.
     * IMPORTANT: after calling this, RepositoryConfigurator.setupRulesRepository() should
     * be called to set up the minimal data for a "blank" setup. If importing other data, however, this is probably not needed.
     */
    public void clearRulesRepository() {
        log.debug( "Clearing repository database. UserId=" + session.getUserID() );
        try {
            
            if (session.getRootNode().hasNode( RulesRepository.RULES_REPOSITORY_NAME )) {
                System.out.println("Clearing rules repository");
                Node node = session.getRootNode().getNode(RulesRepository.RULES_REPOSITORY_NAME);
                node.remove();
                session.save();
            } else {
                System.out.println("Repo not setup, ergo not clearing it !");
            }
        }
        catch(PathNotFoundException e) {                
            log.error( "Unable to clear rules repository.", e );
        }          
        catch(RepositoryException e) {
            log.error( "Unable to clear rules repository.", e );
        }
    }  
    
    
}
