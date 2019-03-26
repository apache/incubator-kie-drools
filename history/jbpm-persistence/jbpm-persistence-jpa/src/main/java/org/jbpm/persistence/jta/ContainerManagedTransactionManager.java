/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.persistence.jta;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionSynchronizationRegistry;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionSynchronization;
import org.drools.persistence.api.TransactionSynchronizationRegistryHelper;
import org.drools.persistence.jta.JtaTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dedicated implementation of <code>TransactionManager</code> that should be used when:
 * <ul>
 *  <li>jBPM engine is embedded in Container Managed Transaction (CMT) environment like EJB container</li>
 *  <li>container that does not allow accessing UserTransaction (when running in CMT mode) 
 *  - e.g. WebSphere Appliation Server</li>
 * </ul>
 * Instance of this transaction manager must be set on Environment together with 
 * Persistence context manager instance to take effect.
 * <code>
 *      Environment env = EnvironmentFactory.newEnvironment();
 *      env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
 *      env.set(EnvironmentName.TRANSACTION_MANAGER, new ContainerManagedTransactionManager());
 *      env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new JpaProcessPersistenceContextManager(env));
 *      env.set(EnvironmentName.TASK_PERSISTENCE_CONTEXT_MANAGER, new JPATaskPersistenceContextManager(env));
 * </code>
 * Since it dedicated to be run in CMT begine/commit/rollback are no-op methods. <br/>
 * Status of the transaction is always Active.
 */
public class ContainerManagedTransactionManager implements TransactionManager {

    private static Logger logger = LoggerFactory.getLogger(ContainerManagedTransactionManager.class);
    protected TransactionSynchronizationRegistry txSyncRegistry;
    
    public ContainerManagedTransactionManager() {
        this.txSyncRegistry = (TransactionSynchronizationRegistry) findTransactionSynchronizationRegistry();
    }
  
    public boolean begin() {
        // do nothing
        return false;
    }

    public void commit(boolean arg0) {
        // do nothing

    }

    public int getStatus() {

        return TransactionManager.STATUS_ACTIVE;
    }

    public void registerTransactionSynchronization(TransactionSynchronization arg0) {
        
        if (txSyncRegistry != null) {
            TransactionSynchronizationRegistryHelper.registerTransactionSynchronization( txSyncRegistry, arg0 );
        }

    }

    public void rollback(boolean arg0) {
        // do nothing

    }

    protected Object findTransactionSynchronizationRegistry() {

        String jndiName = JtaTransactionManager.DEFAULT_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME;
        try {
            InitialContext context = new InitialContext();
            Object tsrObject = context.lookup(jndiName);

            return tsrObject;
        } catch (NamingException ex) {
            logger.warn("Error when getting TransactionSynchronizationRegistry from JNDI ", ex);
            String customJndiLocation = System.getProperty("jbpm.tsr.jndi.lookup", "java:jboss/TransactionSynchronizationRegistry");
            try {

                Object tsrObject =  InitialContext.doLookup(customJndiLocation);
                logger.debug( "JTA TransactionSynchronizationRegistry found at default JNDI location [{}]",
                        customJndiLocation );

                return tsrObject;
            } catch (Exception e1) {
                logger.debug( "No JTA TransactionSynchronizationRegistry found at default JNDI location [{}]",
                        customJndiLocation,
                        ex );
            }
        }

        return null;
    }

	@Override
	public void putResource(Object key, Object resource) {
		TransactionSynchronizationRegistryHelper.putResource(this.txSyncRegistry, key, resource);
	}

	@Override
	public Object getResource(Object key) {
		return TransactionSynchronizationRegistryHelper.getResource(this.txSyncRegistry, key);
	}

}
