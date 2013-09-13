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
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaPersistenceContextManager
    implements
    PersistenceContextManager {
    
    protected final Environment                 env;
    protected final ThreadLocal<EntityManager>  threadLocalCmdScopedEM;
    private final boolean useSessionLocking;

    private final EntityManagerFactory          emf;

    private volatile EntityManager              appScopedEntityManager;
    protected volatile EntityManager            cmdScopedEntityManager;

    private volatile boolean                    internalAppScopedEntityManager;
    private volatile boolean                    internalCmdScopedEntityManager;

    private static Logger        logger = LoggerFactory.getLogger( JpaPersistenceContextManager.class );
    
    public JpaPersistenceContextManager(Environment env) {
        this.env = env;
        Object sessionLockingObj =  this.env.get(EnvironmentName.USE_SESSION_LOCKING);
        if( sessionLockingObj != null ) { 
            if( sessionLockingObj instanceof Boolean ) { 
                useSessionLocking = true;
            } else if( sessionLockingObj instanceof String ) { 
                useSessionLocking = Boolean.parseBoolean((String) sessionLockingObj);
            } else { 
                useSessionLocking = false;
            }
        } else { 
            useSessionLocking = false;
        }
        if( useSessionLocking ) { 
           threadLocalCmdScopedEM = new ThreadLocal<EntityManager>(); 
        } else { 
            threadLocalCmdScopedEM = null;
        }
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
                internalAppScopedEntityManager = true;
                this.appScopedEntityManager = this.emf.createEntityManager();

                this.env.set( EnvironmentName.APP_SCOPED_ENTITY_MANAGER,
                              this.appScopedEntityManager );
            } else {
                internalAppScopedEntityManager = false;
            }
        }
        return new JpaPersistenceContext( appScopedEntityManager );
    }

    public PersistenceContext getCommandScopedPersistenceContext() {
        return new JpaPersistenceContext( this.cmdScopedEntityManager );
    }

    public void beginCommandScopedEntityManager() {
        EntityManager cmdScopedEntityManager = getCommandScopedEntityManager();
        if ( cmdScopedEntityManager == null || 
           ( this.cmdScopedEntityManager != null && !this.cmdScopedEntityManager.isOpen() )) {
            internalCmdScopedEntityManager = true;
            this.cmdScopedEntityManager = this.emf.createEntityManager(); 
            logger.debug("Created command scoped entity manager [id: " + System.identityHashCode(this.cmdScopedEntityManager) + "]");
            this.cmdScopedEntityManager.setFlushMode(FlushModeType.COMMIT);
            setCommandScopedEntityManager(this.cmdScopedEntityManager); 
            cmdScopedEntityManager = this.cmdScopedEntityManager;
        } else {
            logger.debug("Using existing command scoped entity manager [id: " + System.identityHashCode(cmdScopedEntityManager) + "]");
            internalCmdScopedEntityManager = false;
        }
        cmdScopedEntityManager.joinTransaction();
        appScopedEntityManager.joinTransaction();
    }

    public void endCommandScopedEntityManager() {
        if ( this.internalCmdScopedEntityManager ) {
            setCommandScopedEntityManager(null);
            logger.debug("Setting command scoped entity manager to null [id: " + System.identityHashCode(cmdScopedEntityManager) + "]");
        } else {
            logger.debug("Not disposing of non-internal command scoped entity manager [id: " + System.identityHashCode(cmdScopedEntityManager) + "]");
        }
    }

    public void dispose() {
        if ( this.internalAppScopedEntityManager ) {
            if (  this.appScopedEntityManager != null && this.appScopedEntityManager.isOpen() ) {
                this.appScopedEntityManager.close();
            }
            this.internalAppScopedEntityManager = false;
            this.env.set( EnvironmentName.APP_SCOPED_ENTITY_MANAGER, null );
            this.appScopedEntityManager = null;
        }
        
        if ( this.internalCmdScopedEntityManager ) {
            if (  this.cmdScopedEntityManager != null && this.cmdScopedEntityManager.isOpen() ) {
                this.cmdScopedEntityManager.close();
            }
            this.internalCmdScopedEntityManager = false;
            setCommandScopedEntityManager(null);
            this.cmdScopedEntityManager = null;
        }
    }

    public void clearPersistenceContext() {
        if (this.cmdScopedEntityManager != null && this.cmdScopedEntityManager.isOpen()) {
            this.cmdScopedEntityManager.clear();
        }
        
    }

    protected EntityManager getCommandScopedEntityManager() { 
        if( useSessionLocking ) { 
            return threadLocalCmdScopedEM.get();
        } else { 
            return (EntityManager) this.env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
        }
    }
    
    protected void setCommandScopedEntityManager(EntityManager entityManager) { 
        if( useSessionLocking ) { 
            threadLocalCmdScopedEM.set(entityManager);
        } else { 
            this.env.set(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, entityManager);
        }
    }
    
}
