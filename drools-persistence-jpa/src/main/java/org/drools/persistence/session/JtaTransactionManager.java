package org.drools.persistence.session;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

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
    
    private boolean localTransaction;

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
            return null;
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
            context.lookup( jndiName );
            logger.debug( "JTA TransactionSynchronizationRegistry found at default JNDI location [{}]",
                          jndiName );
            return tsr;
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

    public void begin() {
        if ( getStatus() == TransactionManager.STATUS_NO_TRANSACTION ) {
            this.localTransaction = true;
            try {
                this.ut.begin();
            } catch ( Exception e ) {
                logger.warn( "Unable to begin transaction", e);
                throw new RuntimeException( "Unable to begin transaction",
                                            e );
            }
        } else {
            this.localTransaction = false;
        }
    }

    public void commit() {
        if ( this.localTransaction ) {
            try {
                this.ut.commit();
            } catch ( Exception e ) {
                logger.warn( "Unable to commit transaction", e);
                throw new RuntimeException( "Unable to commit transaction",
                                            e );
            }
        } else {
            localTransaction = false;   
        }
    }
    
    public void rollback() {
        localTransaction = false;
        try {
            this.ut.rollback();
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
