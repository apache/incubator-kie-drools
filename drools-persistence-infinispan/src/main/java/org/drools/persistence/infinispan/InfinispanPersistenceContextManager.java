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
 package org.drools.persistence.infinispan;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;

public class InfinispanPersistenceContextManager
    implements
    PersistenceContextManager {
    Environment                  env;

    private DefaultCacheManager cm;

    private Cache<String, Object> appScopedCache;
    protected Cache<String, Object> cmdScopedCache;

    private boolean              internalAppScopedCache;
    private boolean              internalCmdScopedCache;

    public InfinispanPersistenceContextManager(Environment env) {
        this.env = env;
        this.cm = ( DefaultCacheManager ) env.get( EnvironmentName.ENTITY_MANAGER_FACTORY );
    }
    
    @SuppressWarnings("unchecked")
    public PersistenceContext getApplicationScopedPersistenceContext() {
        if ( this.appScopedCache == null ) {
            // Use the App scoped EntityManager if the user has provided it, and it is open.
            this.appScopedCache = (Cache<String, Object>) this.env.get( EnvironmentName.APP_SCOPED_ENTITY_MANAGER );
            
            if ( this.appScopedCache == null ) {
                internalAppScopedCache = true;
                this.appScopedCache = this.cm.getCache("jbpm-configured-cache");
                this.env.set( EnvironmentName.APP_SCOPED_ENTITY_MANAGER, this.appScopedCache );
            } else {
                internalAppScopedCache = false;
            }
        }
        return new InfinispanPersistenceContext( appScopedCache );
    }

    public PersistenceContext getCommandScopedPersistenceContext() {
        return new InfinispanPersistenceContext( this.cmdScopedCache );
    }

    public void beginCommandScopedEntityManager() {
    	@SuppressWarnings("unchecked")
        Cache<String, Object> cmdScopedCache = (Cache<String, Object>) env.get( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER );
        if ( cmdScopedCache == null) {
            internalCmdScopedCache = true;
            this.cmdScopedCache = this.cm.getCache("jbpm-configured-cache");
            this.env.set( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, this.cmdScopedCache );
            cmdScopedCache = this.cmdScopedCache;
        } else {
            internalCmdScopedCache = false;
        }
        //cmdScopedCache.joinTransaction();?? TODO
        //appScopedCache.joinTransaction();?? TODO
    }

    public void endCommandScopedEntityManager() {
        if ( this.internalCmdScopedCache ) {
            this.env.set( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, null );
        }
    }

    public void dispose() {
        if ( this.internalAppScopedCache ) {
            this.internalAppScopedCache = false;
            this.env.set( EnvironmentName.APP_SCOPED_ENTITY_MANAGER, null );
            this.appScopedCache = null;
        }
        
        if ( this.internalCmdScopedCache ) {
            this.internalCmdScopedCache = false;
            this.env.set( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, null );
            this.cmdScopedCache = null;
        }
    }

    public void clearPersistenceContext() {
        /*if (this.cmdScopedCache != null) {
            this.cmdScopedCache.clear();
        }*/ //TODO do nothing for now
        
    }

}
