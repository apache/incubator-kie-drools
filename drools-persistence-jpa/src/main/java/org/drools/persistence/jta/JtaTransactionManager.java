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
package org.drools.persistence.jta;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionSynchronization;
import org.drools.persistence.TransactionSynchronizationRegistryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JtaTransactionManager
    implements
    TransactionManager {

    Logger                               logger                                            = LoggerFactory.getLogger( getClass() );

    public static final String           DEFAULT_USER_TRANSACTION_NAME                     = "java:comp/UserTransaction";

    public static final String[]         FALLBACK_TRANSACTION_MANAGER_NAMES                = new String[]{"java:comp/TransactionManager", "java:appserver/TransactionManager", "java:pm/TransactionManager", "java:/TransactionManager"};

    /**
     * Standard Java EE 5 JNDI location for the JTA TransactionSynchronizationRegistry.
     * Autodetected when available.
     */
    public static final String           DEFAULT_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME = "java:comp/TransactionSynchronizationRegistry";

    private static final String          TRANSACTION_SYNCHRONIZATION_REGISTRY_CLASS_NAME   = "javax.transaction.TransactionSynchronizationRegistry";

    private static Class< ? >            transactionSynchronizationRegistryClass;

    static {
        ClassLoader cl = JtaTransactionManager.class.getClassLoader();
        try {
            transactionSynchronizationRegistryClass = cl.loadClass( TRANSACTION_SYNCHRONIZATION_REGISTRY_CLASS_NAME );
        } catch ( ClassNotFoundException e ) {
            // JTA 1.1 API not available... simply proceed the JTA 1.0 way.
        }
    }

    UserTransaction                      ut;
    Object                               tsr;
    javax.transaction.TransactionManager tm;
    
    public JtaTransactionManager(Object ut,
                                 Object tsr,
                                 Object tm) {
        if ( ut instanceof UserTransaction ) {
            this.ut = ( UserTransaction ) ut;
        } else {
            this.ut = ( UserTransaction ) ( (ut != null) ? ut : findUserTransaction() );
        }
        
        if ( tm instanceof javax.transaction.TransactionManager ) {
            this.tm = ( javax.transaction.TransactionManager ) tm;
        } else {
            this.tm = ( javax.transaction.TransactionManager ) ( (tm != null) ? tm : findTransactionManager( this.ut ) );
        }
        this.tsr = (tsr != null) ? tsr : findTransactionSynchronizationRegistry( this.ut,
                                                                                 this.tm );
    }

    protected javax.transaction.TransactionManager findTransactionManager(UserTransaction ut) {
        if ( ut instanceof TransactionManager ) {
            logger.debug( "JTA UserTransaction object [{}] implements TransactionManager",
                          ut );
            return (javax.transaction.TransactionManager) ut;
        }

        InitialContext context = null;

        try {
            context = new InitialContext();
        } catch ( NamingException ex ) {
            logger.debug( "Could not initialise JNDI InitialContext",
                          ex );
            return null;
        }

        // Check fallback JNDI locations.
        for ( String jndiName : FALLBACK_TRANSACTION_MANAGER_NAMES ) {
            try {
                javax.transaction.TransactionManager tm = (javax.transaction.TransactionManager) context.lookup( jndiName );
                logger.debug( "JTA TransactionManager found at fallback JNDI location [{}]",
                              jndiName );
                return tm;
            } catch ( NamingException ex ) {
                logger.debug( "No JTA TransactionManager found at fallback JNDI location [{}]",
                              jndiName,
                              ex );
            }
        }

        // OK, so no JTA TransactionManager is available...
        return null;
    }

    protected UserTransaction findUserTransaction() {
        try {
            InitialContext context = new InitialContext();
            return (UserTransaction) context.lookup( DEFAULT_USER_TRANSACTION_NAME );
        } catch ( NamingException ex ) {
            logger.debug( "No UserTransaction found at JNDI location [{}]",
                          DEFAULT_USER_TRANSACTION_NAME,
                          ex );
            throw new IllegalStateException("Unable to find transaction: " + ex.getMessage(), ex);
        }
    }

    protected Object findTransactionSynchronizationRegistry(UserTransaction ut,
                                                            javax.transaction.TransactionManager tm) {

        if ( transactionSynchronizationRegistryClass == null ) {
            // JTA 1.1 API not present - skip.
            logger.debug( "JTA 1.1 [{}] API not available",
                          TRANSACTION_SYNCHRONIZATION_REGISTRY_CLASS_NAME );
            return null;
        }

        String jndiName = DEFAULT_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME;
        try {
            InitialContext context = new InitialContext();
            Object tsrObject = context.lookup( jndiName );
            logger.debug( "JTA TransactionSynchronizationRegistry found at default JNDI location [{}]",
                          jndiName );
            return tsrObject;
        } catch ( NamingException ex ) {
            logger.debug( "No JTA TransactionSynchronizationRegistry found at default JNDI location [{}]",
                          jndiName,
                          ex );
        }
        // Check whether the UserTransaction or TransactionManager implements it...
        if ( transactionSynchronizationRegistryClass.isInstance( ut ) ) {
            return ut;
        }
        if ( transactionSynchronizationRegistryClass.isInstance( tm ) ) {
            return tm;
        }
        // OK, so no JTA 1.1 TransactionSynchronizationRegistry is available,
        // despite the API being present...
        return null;
    }

    public boolean begin() {
        if ( getStatus() == TransactionManager.STATUS_NO_TRANSACTION ) {
            try {
                this.ut.begin();
                return true;
            } catch ( Exception e ) {
                logger.warn( "Unable to begin transaction", e);
                throw new RuntimeException( "Unable to begin transaction",
                                            e );
            }
        } 
        return false;
    }

    public void commit(boolean transactionOwner) {
        if ( transactionOwner ) {
            try {
                this.ut.commit();
            } catch ( Exception e ) {
                logger.warn( "Unable to commit transaction", e);
                throw new RuntimeException( "Unable to commit transaction",
                                            e );
            }
        } 
    }
    
    public void rollback(boolean transactionOwner) {
        try {
        	if (transactionOwner) {
        		this.ut.rollback();
        	} else {
        		this.ut.setRollbackOnly();
        	}
        } catch ( Exception e ) {
            logger.warn( "Unable to rollback transaction", e);
            throw new RuntimeException( "Unable to rollback transaction",
                                        e );
        }
    }

    public int getStatus() {
        int s;
        try {
            s = this.ut.getStatus();
        } catch ( SystemException e ) {
            throw new RuntimeException( "Unable to get status for transaction",
                                        e );
        }

        switch ( s ) {
            case Status.STATUS_COMMITTED :
                return TransactionManager.STATUS_COMMITTED;
            case Status.STATUS_ROLLEDBACK :
                return TransactionManager.STATUS_ROLLEDBACK;
            case Status.STATUS_NO_TRANSACTION :
                return TransactionManager.STATUS_NO_TRANSACTION;
            default :
                return TransactionManager.STATUS_UNKNOWN;
        }
    }

    public void registerTransactionSynchronization(final TransactionSynchronization ts) {
        if ( this.tsr != null ) {
            TransactionSynchronizationRegistryHelper.registerTransactionSynchronization( this.tsr,
                                                                                         ts );
        } else if ( this.tm != null ) {
            try {
                this.tm.getTransaction().registerSynchronization( new JtaTransactionSynchronizationAdapter( ts ) );
            } catch ( Exception e ) {
                // No JTA TransactionManager available - log a warning.
                logger.warn( "Participating in existing JTA transaction, but no JTA TransactionManager or TransactionSychronizationRegistry available: ",
                             e );

            }
        } else {
            // No JTA TransactionManager available - log a warning.
            logger.warn( "Participating in existing JTA transaction, but no JTA TransactionManager or TransactionSychronizationRegistry available: " );
        }
    }

}
