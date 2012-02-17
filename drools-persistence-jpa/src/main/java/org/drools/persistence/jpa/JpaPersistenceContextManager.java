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
import org.drools.persistence.TransactionManager;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;

public class JpaPersistenceContextManager
    implements
    PersistenceContextManager {
    Environment                  env;

    private EntityManagerFactory emf;
    private TransactionManager txm;

    private EntityManager        appScopedEntityManager;
    protected EntityManager      cmdScopedEntityManager;

    private boolean              internalAppScopedEntityManager;
    private boolean              internalCmdScopedEntityManager;

    public JpaPersistenceContextManager(Environment env) {
        this.env = env;
        this.emf = ( EntityManagerFactory ) env.get( EnvironmentName.ENTITY_MANAGER_FACTORY );
    }
    
    public void setTransactionManager(TransactionManager txm) { 
        this.txm = txm;
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
        EntityManager cmdScopedEntityManager = (EntityManager) env.get( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER );
        if ( cmdScopedEntityManager == null || 
           ( this.cmdScopedEntityManager != null && !this.cmdScopedEntityManager.isOpen() )) {
            internalCmdScopedEntityManager = true;
            // no need to call joinTransaction as it will do so if one already exists
            this.cmdScopedEntityManager = this.emf.createEntityManager(); 
            this.cmdScopedEntityManager.setFlushMode(FlushModeType.COMMIT);
            this.env.set( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER,
                          this.cmdScopedEntityManager );
            cmdScopedEntityManager = this.cmdScopedEntityManager;
        } else {
            internalCmdScopedEntityManager = false;
        }
        txm.attachPersistenceContext(cmdScopedEntityManager);
        txm.attachPersistenceContext(appScopedEntityManager);
    }

    public void endCommandScopedEntityManager() {
        if ( this.internalCmdScopedEntityManager ) {
            this.env.set( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, 
                          null );
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
            this.env.set( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, null );
            this.cmdScopedEntityManager = null;
        }
    }


}
