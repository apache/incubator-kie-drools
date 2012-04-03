package org.jbpm.task.service.persistence;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TaskJTATransactionManager extends TaskTransactionManager {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private UserTransaction ut;
    private javax.transaction.TransactionManager tm;
    private Object tsr;

    TaskJTATransactionManager() {
        this.ut = findUserTransaction();
        this.tm = findTransactionManager(this.ut);
        this.tsr = findTransactionSynchronizationRegistry(this.ut, this.tm);
    }

    boolean ownsTransaction(EntityManager em) { 
       return (getStatus(em) == TransactionStatus.NO_TRANSACTION);
    }
    
    void attachPersistenceContext(EntityManager em) { 
        em.joinTransaction();           
    }
    
    void begin(EntityManager em) {
        try {
            this.ut.begin();
        } catch (Exception e) {
            logger.warn("Unable to begin transaction", e);
            throw new RuntimeException("Unable to begin transaction", e);
        }
    }

    void commit(EntityManager em) {
        try {
            this.ut.commit();
        } catch (Exception e) {
            logger.warn("Unable to commit transaction", e);
            throw new RuntimeException("Unable to commit transaction", e);
        }
    }

    void rollback(EntityManager em, boolean txOwner) {
        try {
            if( txOwner ) { 
                this.ut.rollback();
            }
            else { 
                this.ut.setRollbackOnly();
            }
        } catch (Exception e) {
            logger.warn("Unable to rollback transaction", e);
            throw new RuntimeException("Unable to rollback transaction", e);
        }
    }

    TransactionStatus getStatus(EntityManager em) {
        int status;
        try {
            status = this.ut.getStatus();
        } catch ( SystemException e ) {
            throw new RuntimeException( "Unable to get status for transaction", e );
        }

        switch ( status ) {
            case javax.transaction.Status.STATUS_ACTIVE :
                return TransactionStatus.ACTIVE;
            case javax.transaction.Status.STATUS_COMMITTED :
                return TransactionStatus.COMMITTED;
            case javax.transaction.Status.STATUS_ROLLEDBACK :
                return TransactionStatus.ROLLEDBACK;
            case javax.transaction.Status.STATUS_MARKED_ROLLBACK :
                return TransactionStatus.MARKED_ROLLBACK;
            case javax.transaction.Status.STATUS_NO_TRANSACTION :
                return TransactionStatus.NO_TRANSACTION;
            default :
                return TransactionStatus.UNKNOWN;
        }
    }
    
    void registerTransactionSynchronization(TransactionSynchronization ts) {
        // DBG Auto-generated method stub

    }

    /**
     * JTA specific information 
     */
    
    private static final String DEFAULT_USER_TRANSACTION_NAME = "java:comp/UserTransaction";

    private static final String[] FALLBACK_TRANSACTION_MANAGER_NAMES = new String[] { 
    "java:comp/TransactionManager",
    "java:appserver/TransactionManager", 
    "java:pm/TransactionManager", 
    "java:/TransactionManager" };

    /**
     * Standard Java EE 5 JNDI location for the JTA
     * TransactionSynchronizationRegistry. Autodetected when available.
     */
    private static final String DEFAULT_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME = "java:comp/TransactionSynchronizationRegistry";

    private static final String TRANSACTION_SYNCHRONIZATION_REGISTRY_CLASS_NAME = "javax.transaction.TransactionSynchronizationRegistry";

    private static Class<?> transactionSynchronizationRegistryClass;
    static {
        ClassLoader cl = TaskJTATransactionManager.class.getClassLoader();
        try {
            transactionSynchronizationRegistryClass = cl.loadClass(TRANSACTION_SYNCHRONIZATION_REGISTRY_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            // JTA 1.1 API not available... simply proceed the JTA 1.0 way.
        }
    }

    private UserTransaction findUserTransaction() {
        try {
            InitialContext context = new InitialContext();
            return (UserTransaction) context.lookup(DEFAULT_USER_TRANSACTION_NAME);
        } catch (NamingException ex) {
            logger.debug("No UserTransaction found at JNDI location [{}]", DEFAULT_USER_TRANSACTION_NAME, ex);
            return null;
        }
    }

    private TransactionManager findTransactionManager(UserTransaction ut) {
        if (ut instanceof TransactionManager) {
            logger.debug("JTA UserTransaction object [{}] implements TransactionManager", ut);
            return (TransactionManager) ut;
        }
    
        InitialContext context = null;
    
        try {
            context = new InitialContext();
        } catch (NamingException ex) {
            logger.debug("Could not initialise JNDI InitialContext", ex);
            return null;
        }
    
        // Check fallback JNDI locations.
        for (String jndiName : FALLBACK_TRANSACTION_MANAGER_NAMES) {
            try {
                TransactionManager tm = (TransactionManager) context.lookup(jndiName);
                logger.debug("JTA TransactionManager found at fallback JNDI location [{}]", jndiName);
                return tm;
            } catch (NamingException ex) {
                logger.debug("No JTA TransactionManager found at fallback JNDI location [{}]", jndiName, ex);
            }
        }
    
        // OK, so no JTA TransactionManager is available...
        return null;
    }

    private Object findTransactionSynchronizationRegistry(UserTransaction ut, TransactionManager tm) {
        if (transactionSynchronizationRegistryClass == null) {
            // JTA 1.1 API not present - skip. new InitialContext().lookup(
            // "java:comp/UserTransaction" );
            logger.debug("JTA 1.1 [{}] API not available", TRANSACTION_SYNCHRONIZATION_REGISTRY_CLASS_NAME);
            return null;
        }
    
        String jndiName = DEFAULT_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME;
        try {
            InitialContext context = new InitialContext();
            context.lookup(jndiName);
            logger.debug("JTA TransactionSynchronizationRegistry found at default JNDI location [{}]", jndiName);
            return tsr;
        } catch (NamingException ex) {
            logger.debug("No JTA TransactionSynchronizationRegistry found at default JNDI location [{}]", jndiName, ex);
        }
        // Check whether the UserTransaction or TransactionManager implements
        // it...
        if (transactionSynchronizationRegistryClass.isInstance(ut)) {
            return ut;
        }
        if (transactionSynchronizationRegistryClass.isInstance(tm)) {
            return tm;
        }
        // OK, so no JTA 1.1 TransactionSynchronizationRegistry is available,
        // despite the API being present...
        return null;
    }

}
