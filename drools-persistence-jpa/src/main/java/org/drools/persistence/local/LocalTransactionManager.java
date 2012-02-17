package org.drools.persistence.local;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionSynchronization;
import org.drools.persistence.jpa.JpaPersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a transaction manager for local resources. It's a bit of a hack
 * because there's no easy or elegant way to get a synchronization point. </p>
 * This class relies on the fact that when it's used in the
 * SingleSessionCommandService, the following operations are always executed in
 * the following order:
 * 
 * <pre>
 * {@code
 * try {
 *   transactionOwner = txm.begin();
 *   registerRollbackSync();
 *   txm.attachPersistenceContext(persistenceContext);
 *            
 *   // Do stuff
 * 
 *   txm.commit(transactionOwner);
 * }
 * </pre>
 * 
 * Specifically, we rely on the fact that
 * {@link #attachPersistenceContext(Object)} is called after both the start of
 * the transaction and the transaction synchronization registration. </p> What
 * happens is the following:
 * <ol>
 * <li>{@link #begin()} just returns if transaction has <i>not</i> begun.
 * <ul>
 * <li>If a transaction is already active, then the caller may not own the
 * transaction</li>
 * </ul>
 * </li>
 * <li> {@link #registerTransactionSynchronization(TransactionSynchronization)}
 * also just temporarily saves the passed {@link TransactionSynchronization}
 * implementation for use in the next step.</li>
 * <p/>
 * <li>All of the magic happens in the {@link #attachPersistenceContext(Object)}
 * method.
 * <ul>
 * <li>First we add the the entity manager (in the persistence context) to the
 * list of entity managers (if it hasn't already been added).</li>
 * <li>If the entity manager does not have a running transaction, we start one.</li>
 * <li>Lastly, we put the {@link EntityTransaction} in in a {@link WeakHashMap}
 * with the {@link TransactionSynchronization} implementation (from step 2
 * above) as a value.</li>
 * <li>Also, and this is probably the biggest hack, as a side-effect of how
 * which code runs when, a {@link TransactionSynchronization} implementation
 * is <b>only</b> registered for the app-scoped entity manager.</li>
 * </ul>
 * </li>
 * <li>Lastly, when the {@link #commit(boolean)} or {@link #rollback(boolean)}
 * is called (by the tx owner), they do the following:
 * </p>
 * <ul>
 * For all entity managers, in the LIFO order in which they were attached 
 * to the transaction manager:
 * <li>If the tx is active, then commit or rollback, depending on which
 * operation is appropriate</li>
 * <li>Once commit has succeeded, retrieve the tx sync impl for that tx and call
 * afterCompletion() with the correct status.</li>
 * </ul>
 * </li>
 * </ol>
 * 
 * There are a number of safety-checks in the code to make sure that nothing
 * unexpected happens.
 */
public class LocalTransactionManager implements TransactionManager {

    Logger logger = LoggerFactory.getLogger(getClass());

    private List<EntityManager> entityManagers = new LinkedList<EntityManager>();

    private TransactionSynchronization transactionSynchronization = null;
    // Using a WeakHashMap here just means very easy bookkeeping..
    private Map<EntityTransaction, TransactionSynchronization> transactionSynchronizations = new WeakHashMap<EntityTransaction, TransactionSynchronization>();

    /**
     * If any entity managers have an active transaction, then the status is
     * ACTIVE. Otherwise, the status is NO_TRANSACTION.
     */
    public int getStatus() {
        int status = -1;

        Iterator<EntityManager> iter = entityManagers.iterator();
        while (iter.hasNext()) {
            EntityManager em = iter.next();
            EntityTransaction transaction = em.getTransaction();
            if (transaction.isActive()) {
                status = TransactionManager.STATUS_ACTIVE;
            }
        }

        if (status == -1) {
            status = STATUS_NO_TRANSACTION;
        }

        return status;
    }

    /**
     * Return whether or not there is an active transaction.
     */
    public boolean begin() {
        if (getStatus() == TransactionManager.STATUS_NO_TRANSACTION) {
            return true;
        }
        return false;
    }

    /**
     * Commit the transaction.
     */
    public void commit(boolean transactionOwner) {
        if (transactionOwner) {
            List<TransactionSynchronization> synchronizationsToApply = new ArrayList<TransactionSynchronization>(2);
            // iteration (commit/sync "outer" entityManagers first)
            Iterator<EntityManager> iter = entityManagers.iterator();
            while (iter.hasNext()) {
                EntityManager em = iter.next();
                if (em.getTransaction().isActive()) {
                    EntityTransaction tx = em.getTransaction();
                    tx.commit();
                    TransactionSynchronization txSync = transactionSynchronizations.get(tx);
                    if (txSync != null) {
                        synchronizationsToApply.add(txSync);
                    }
                }
            }
            for (TransactionSynchronization txSync : synchronizationsToApply) {
                txSync.afterCompletion(TransactionManager.STATUS_COMMITTED);
            }
        }
    }

    /**
     * Roll back the transaction.
     */
    public void rollback(boolean transactionOwner) {
        if (transactionOwner) {
            List<TransactionSynchronization> synchronizationsToApply = new ArrayList<TransactionSynchronization>(2);
            // normal iteration (commit/sync "outer" entityManagers first)
            Iterator<EntityManager> iter = entityManagers.iterator();
            while (iter.hasNext()) {
                EntityManager em = iter.next();
                if (em.getTransaction().isActive()) {
                    EntityTransaction tx = em.getTransaction();
                    tx.rollback();
                    TransactionSynchronization txSync = transactionSynchronizations.get(tx);
                    if (txSync != null) {
                        synchronizationsToApply.add(txSync);
                    }
                }
            }
            for (TransactionSynchronization txSync : synchronizationsToApply) {
                txSync.afterCompletion(TransactionManager.STATUS_ROLLEDBACK);
            }
        } else {
            // normal iteration (commit/sync "outer" entityManagers first)
            Iterator<EntityManager> iter = entityManagers.iterator();
            while (iter.hasNext()) {
                EntityManager em = iter.next();
                if (em.getTransaction().isActive()) {
                    em.getTransaction().setRollbackOnly();
                }
            }
        }
    }

    /**
     * Register the {@link TransactionSynchronization} object with the ongoing
     * transaction(s).
     */
    public void registerTransactionSynchronization(TransactionSynchronization ts) {
        if( transactionSynchronization != null ) {
            throw new RuntimeException("Only one transactionSynchronization allowed per transaction");
        }
        this.transactionSynchronization = ts;
    }

    /**
     * Attach the persistence context to the transaction.
     * 
     * In this case, we do a lot more than just that: see the class javadoc for
     * more info.
     */
    public void attachPersistenceContext(Object persistenceContextObject) {
        EntityManager em = null;
        if (persistenceContextObject instanceof EntityManager) {
            em = (EntityManager) persistenceContextObject;
        } else if (persistenceContextObject instanceof JpaPersistenceContext) {
            em = ((JpaPersistenceContext) persistenceContextObject).getEntityManager();
        }
        if (em != null) {
            // Attach EM and clean up list, if neccessary
            if (!entityManagers.contains(em)) {
                entityManagers.add(0, em);
                /**
                 * If entityManagers.size() > 2, then the list looks like this:
                 *  0. new cmd scoped persistence context 
                 *  1. old cmd scoped persistence context 
                 *  2. app scoped persistence context 
                 * So, obviously, we delete 1: 
                 * reason 1: there is never more than one cmd-scoped persistence context per ksession active 
                 * reason 2: there is never more than 1 cmd-scoped persistence context 
                 *            and 1 app-scoped persistence context per ksession active
                 * 
                 * By doing this _here_, as opposed to in every loop, means that
                 * we keep the entityManagers list small. This means that 
                 *  a. methods referring to this list stay fast 
                 *  b. garbage collection doesn't slow down the method that discovers that
                 *     there are a bunch of empty weak ref's that need to be deleted
                 *     from the entity manager list.
                 */
                if (entityManagers.size() > 2) {
                    entityManagers.remove(1);
                }
            }

            // Start tx and register tx sync impl
            EntityTransaction tx = em.getTransaction();
            if (!tx.isActive()) {
                tx.begin();
                if (transactionSynchronization != null) {
                    transactionSynchronizations.put(tx, transactionSynchronization);
                    transactionSynchronization = null;
                }
            } else {
                logger.trace("active entity manager attached: " + em.getTransaction().isActive());
            }
        }
    }

    /**
     * These getters are used for testing. Note the scope of the methods..
     */

    List<EntityManager> getEntityManagers() {
        return entityManagers;
    }

    Map<EntityTransaction, TransactionSynchronization> getTransactionSynchronizations() {
        return transactionSynchronizations;
    }

}
