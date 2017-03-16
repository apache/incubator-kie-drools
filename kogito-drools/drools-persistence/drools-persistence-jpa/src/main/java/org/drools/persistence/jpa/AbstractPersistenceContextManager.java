/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import org.drools.persistence.api.TransactionManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;

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
public abstract class AbstractPersistenceContextManager {

    protected final Environment                 env;
    protected final EntityManagerFactory          emf;
    protected final TransactionManager          txm;

    protected volatile EntityManager              appScopedEntityManager;
    protected volatile EntityManager  cmdScopedEntityManager;

    protected volatile boolean                    internalAppScopedEntityManagerFlag;
    protected volatile boolean                    internalCmdScopedEntityManagerFlag;

    public AbstractPersistenceContextManager(Environment env) {
        this.env = env;
        this.emf = ( EntityManagerFactory ) env.get( EnvironmentName.ENTITY_MANAGER_FACTORY );
        this.txm = ( TransactionManager ) env.get(EnvironmentName.TRANSACTION_MANAGER);
    }
    
    public EntityManager getApplicationScopedEntityManager() {
        if ( this.appScopedEntityManager == null ) {

            // Use the App scoped EntityManager if the user has provided it, and it is open.
            this.appScopedEntityManager = (EntityManager) this.env.get( EnvironmentName.APP_SCOPED_ENTITY_MANAGER );

            if ( this.appScopedEntityManager != null && !this.appScopedEntityManager.isOpen() ) {
                throw new RuntimeException("Provided APP_SCOPED_ENTITY_MANAGER is not open");
            }
            
            if ( this.appScopedEntityManager == null ) {
                internalAppScopedEntityManagerFlag = true;
                this.appScopedEntityManager = this.emf.createEntityManager();

                this.env.set( EnvironmentName.APP_SCOPED_ENTITY_MANAGER, this.appScopedEntityManager );
                if (txm.getStatus() == TransactionManager.STATUS_ACTIVE) {
                    this.txm.putResource(EnvironmentName.APP_SCOPED_ENTITY_MANAGER, this.appScopedEntityManager );
                }
            } else {
                internalAppScopedEntityManagerFlag = false;
            }
        }
        return appScopedEntityManager;
    }

    public EntityManager getCommandScopedEntityManager() {
        // first check if there is already cmd scoped entity manager as transactional resource
        EntityManager cmdScopedEntityManager = getInternalCommandScopedEntityManager();
        if ( cmdScopedEntityManager == null) {
            cmdScopedEntityManager = (EntityManager) env.get( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER );
            if (cmdScopedEntityManager != null) {
                this.txm.putResource(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, cmdScopedEntityManager );
            }

            if ( cmdScopedEntityManager == null) {
                internalCmdScopedEntityManagerFlag = true;
                cmdScopedEntityManager = this.emf.createEntityManager(); // no need to call joinTransaction as it will do so if one already exists
                this.env.set( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, cmdScopedEntityManager );
                this.txm.putResource(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, cmdScopedEntityManager );

            } else {
                internalCmdScopedEntityManagerFlag = false;
            }
        }
        cmdScopedEntityManager.joinTransaction();
        if (this.appScopedEntityManager != null) {
            this.appScopedEntityManager.joinTransaction();
        }
        return cmdScopedEntityManager;
    }

    public void endCommandScopedEntityManager() {
        EntityManager cmdScopedEntityManager = getInternalCommandScopedEntityManager();
        if ( this.internalCmdScopedEntityManagerFlag ) {
            if (cmdScopedEntityManager != null && cmdScopedEntityManager.isOpen()) {
                cmdScopedEntityManager.clear();
            }
            this.env.set( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, null );
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
            this.env.set( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, null );
            this.txm.putResource(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, null );
            this.internalCmdScopedEntityManagerFlag = false;
        }
    }

    protected EntityManager getInternalCommandScopedEntityManager() { 
        return (EntityManager) txm.getResource(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
    }

}
