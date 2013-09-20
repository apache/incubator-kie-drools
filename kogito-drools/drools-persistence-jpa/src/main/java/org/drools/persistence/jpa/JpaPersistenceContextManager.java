/*
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
 package org.drools.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.SingleSessionCommandService;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages {@link JpaPersistenceContext} objects, and the underlying persistence context ({@link EntityManager}) 
 * instances for a persistent {@link KieSession} and other infrastructure classes that use persistence in KIE projects.
 * </p>
 * (For reference in the following documentation: the {@link EntityManager} is the class used to represent a persistence context)
 * </p>
 * There are 2 issues to take into account when looking at or modifying the code here: <ol>
 * <li>One of the features made available here is the ability for the user to supply their own (Command Scoped) persistence 
 *     context for use by the {@link KieSession}</li>
 * <li>However, significant race-conditions arise when a Command Scoped persistence context is used in one persistent
 * {@link KieSession} by multiple threads. In other words, when multiple threads call operations on a Singleton persistent 
 * {@link KieSession}.</li>
 * </ol>
 * 
 * This class uses {@link ThreadLocal} instances for two things:<ol>
 * <li>The internal Command Scoped {@link EntityManager} instance.</li>
 * <li></li>
 * </ol>
 */
public class JpaPersistenceContextManager
    implements
    PersistenceContextManager {
    
    protected final Environment                 env;

    private final EntityManagerFactory          emf;

    private volatile EntityManager              appScopedEntityManager;
    protected final ThreadLocal<EntityManager>  localInternalCmdScopedEntityManager = new ThreadLocal<EntityManager>();

    private volatile boolean                    internalAppScopedEntityManagerFlag;
    private volatile boolean                    internalCmdScopedEntityManagerFlag;

    public JpaPersistenceContextManager(Environment env) {
        this.env = env;
        this.emf = ( EntityManagerFactory ) env.get( EnvironmentName.ENTITY_MANAGER_FACTORY );
    }
    
    public PersistenceContext getApplicationScopedPersistenceContext() {
        if ( this.appScopedEntityManager == null ) {
            // Use the App scoped EntityManager if the user has provided it, and it is open.
            this.appScopedEntityManager = (EntityManager) this.env.get( EnvironmentName.APP_SCOPED_ENTITY_MANAGER );
            if ( this.appScopedEntityManager != null && !this.appScopedEntityManager.isOpen() ) {
                throw new RuntimeException("Provided APP_SCOPED_ENTITY_MANAGER is not open");
            }
            
            if ( this.appScopedEntityManager == null ) {
                internalAppScopedEntityManagerFlag = true;
                this.appScopedEntityManager = this.emf.createEntityManager();

                this.env.set( EnvironmentName.APP_SCOPED_ENTITY_MANAGER,
                              this.appScopedEntityManager );
            } else {
                internalAppScopedEntityManagerFlag = false;
            }
        }
        return new JpaPersistenceContext( appScopedEntityManager );
    }

    public PersistenceContext getCommandScopedPersistenceContext() {
        return new JpaPersistenceContext( getInternalCommandScopedEntityManager() );
    }

    public void beginCommandScopedEntityManager() {
        EntityManager externalCmdScopedEntityManager  = (EntityManager) this.env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
        EntityManager internalCmdScopedEntityManager = getInternalCommandScopedEntityManager();
        
        EntityManager cmdScopedEntityManager;
        /**
         * The following if() check is fairly important for KIE persistence. It should make sure
         * to correctly implement the following logic: 
         * 
         * 1. If there's already an internal Command Scoped EntityManager (CSEM) for this thread, 
         *    which is also open, then the existing CSEM should be used and *no* new CSEM should be created.
         * 2. If 1 is not true, AND there's an open, externally managed CSEM (supplied by the user via the Environment), 
         *    then the externally managed CSEM should be used (and *no* new CSEM should be created.)
         *    
         *  Notice that I'm specifying when a new CSEM should *not* be created, while the logic below does the 
         *  opposite of this: it creates a new CSEM in accordance with the logic described above. 
         */
        boolean openInternalCSEM = internalCmdScopedEntityManager != null && internalCmdScopedEntityManager.isOpen();
        if ( openInternalCSEM ||
             (externalCmdScopedEntityManager != null && externalCmdScopedEntityManager.isOpen()) ) {
            if( internalCmdScopedEntityManager != null ) { 
                cmdScopedEntityManager = internalCmdScopedEntityManager;
            } else { 
                internalCmdScopedEntityManagerFlag = false;
                cmdScopedEntityManager = externalCmdScopedEntityManager;
                setInternalCommandScopedEntityManager(externalCmdScopedEntityManager);
            }
        } else { 
            internalCmdScopedEntityManagerFlag = true;
           
            // Create a new cmd scoped em
            internalCmdScopedEntityManager = this.emf.createEntityManager();
            setInternalCommandScopedEntityManager(internalCmdScopedEntityManager); 
            internalCmdScopedEntityManager.setFlushMode(FlushModeType.COMMIT);

            cmdScopedEntityManager = internalCmdScopedEntityManager;
        }
        
        cmdScopedEntityManager.joinTransaction();
        appScopedEntityManager.joinTransaction();
    }

    public void endCommandScopedEntityManager() {
        EntityManager cmdScopedEntityManager = getInternalCommandScopedEntityManager();
        if ( this.internalCmdScopedEntityManagerFlag ) {
            if (cmdScopedEntityManager != null && cmdScopedEntityManager.isOpen()) {
                cmdScopedEntityManager.clear();
            }
        } 
    }

    public void dispose() {
        if ( this.internalAppScopedEntityManagerFlag ) {
            if (  this.appScopedEntityManager != null && this.appScopedEntityManager.isOpen() ) {
                this.appScopedEntityManager.close();
            }
            this.internalAppScopedEntityManagerFlag = false;
            this.env.set( EnvironmentName.APP_SCOPED_ENTITY_MANAGER, null );
            this.appScopedEntityManager = null;
        }
        
        if ( this.internalCmdScopedEntityManagerFlag ) {
            EntityManager cmdScopedEntityManager = getInternalCommandScopedEntityManager();
            if (  cmdScopedEntityManager != null && cmdScopedEntityManager.isOpen() ) {
                cmdScopedEntityManager.close();
            }
            this.internalCmdScopedEntityManagerFlag = false;
            setInternalCommandScopedEntityManager(null);
        }
    }

    /**
     * Getter / Setter methods for the Command Scoped {@link EntityManager}
     */

    protected EntityManager getInternalCommandScopedEntityManager() { 
        return this.localInternalCmdScopedEntityManager.get();
    }

    protected void setInternalCommandScopedEntityManager(EntityManager entityManager) { 
        this.localInternalCmdScopedEntityManager.set(entityManager);
    }

}
