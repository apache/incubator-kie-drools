package org.drools.persistence.local;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Status;
import javax.transaction.Synchronization;

import org.drools.persistence.*;
import org.drools.persistence.jpa.JpaPersistenceContext;
import org.drools.persistence.jta.JtaTransactionSynchronizationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a transaction manager for local resources. It's a bit of a hack
 * because there's no easy or elegant way to get a synchronization point. </p>
 * 
 * While going through this code, it's important to remember the following:<ul>
 * <li>There will be multiple entity managers (persistence contexts) per transaction (app-scoped
 * and cmd-scoped).</li>
 * <li>There will occasionally be nested transactions, from a functional standpoint. This means
 * that the code using this manager will occassionally "nest" transactions although technically, there
 * will only ever be one transaction running at a time.</li>
 * </ul>
 * This class relies on the fact that when it's used in the
 * {@link SingleSessionCommandService}, the following operations are always executed in
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
 * <li>{@link #begin()} just returns (no-op) if transaction has <i>not</i> begun.
 * <ul>
 * <li>If a transaction is already active, then the caller may not own the
 * transaction</li>
 * </ul>
 * </li>
 * <li> {@link SingleSessionCommandService#registerRollbackSync()} (which calls
 * {@link #registerTransactionSynchronization(TransactionSynchronization)})
 * also just temporarily saves the passed {@link TransactionSynchronization}
 * implementation for use in the next step.</li>
 * <p/>
 * <li>All of the magic happens in the {@link #attachPersistenceContext(Object)}
 * method.
 * <ul>
 * 
 * <li>First we add the the entity manager (in the persistence context) to the
 * list of entity managers (if it hasn't already been added).</li>
 * 
 * <li>If the entity manager does not have a running transaction, we start one.</li>
 * 
 * <li>Lastly, we add a mapping in a {@link WeakHashMap} from the {@link EntityManager} 
 * to the {@link TransactionSynchronization} implementation (from step 2 above). 
 * See note [1] below for a little more explanation. 
 * 
 * <li>Also, and this is probably the biggest hack, as a side-effect of all of this,
 * a {@code TransactionSynchronization} implementation is <b>only</b> registered 
 * for the app-scoped entity manager. This happens because we link the 
 * {@code TransactionSynchronization} passed to this class in step 2
 * ( {@link registerTransactionSynchronization(TransactionSynchronization)} ) <i>once</i>
 * to any {@code EntityManager}. Fortunately, the app-scoped {@code EntityManager} is 
 * always attached before the cmd-scoped, which means it's attached to the app-scoped
 * {@code EntityManager}.</li>
 * 
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
 * [<u>1</u>]: Ideally, it would be better to use the {@link EntityTransaction} instead 
 * of the {@link EntityManager} for the mapping to the {@link TransactionSynchronization} 
 * but JSR 317 (Persistence) gives no guarantee that {@link EntityManager#getTransaction()} 
 * will consistently represent the same transaction by using the same instance of an 
 * {@code EntityTransaction} class. There's also no guarantee that two different instances 
 * of {@code EntityManager} can not represent the same persistence context, but the semantics 
 * and usage of {@code EntityManager} fortunately do make it almost impossible for that to happen,
 * especially considering the way we use {@code EntityManager} here.<ep>
 * 
 * There are a number of safety-checks in the code to make sure that nothing
 * unexpected happens. </p> 
 * 
 * There's one last oddity that should be explained: the <code>transactionSynchronizations</code>
 * map is a <code><u>WeakHashMap</u></code>.</p>
 *  The problem is the following:<ol>
 *  <li>Every time we call {@link #attachPersistenceContext(Object)}, we're potentially adding a 
 *  new {@link TransactionSynchronization}. I say <i>potentially</i> because there are cases
 *  in which we have a "nested" local transaction (batched ksession commands, for example) and the 
 *  {@code TransactionSynchronization} has already been added</li>
 *  <li>When we call {@link #commit(boolean)} or {@link #rollback(boolean)}, the {@code TransactionSynchronization}
 *  for that particular {@code EntityTransaction} is retrieved and the appropriate synchronization method is 
 *  executed.</li>
 *  <li>However, 
 */
public class LocalTransactionManager implements TransactionManager {

    Logger logger = LoggerFactory.getLogger(getClass());

    private LinkedHashSet<EntityManager> entityManagers = new LinkedHashSet<EntityManager>();

    private TransactionSynchronization transactionSynchronization = null;
    // TODO: weak hash map or not? 
    private Map<EntityManager, Synchronization> transactionSynchronizations = new HashMap<EntityManager, Synchronization>();

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
            EntityManager [] em = entityManagers.toArray(new EntityManager[entityManagers.size()]);
            
            // At most, we expect 2 synchronizations to be registered: one for the cmd-scoped EM and one for the app-scoped EM
            List<Synchronization> synchronizationsToApply = new ArrayList<Synchronization>(2);
            
            // Fill synchronizationsToApply with Synchronization objects that apply to this tx/entity manager
            int c = 0; //DBG
            for( int i = em.length-1; i >= 0; --i ) { 
                if (em[i].getTransaction().isActive()) {
                    Synchronization txSync = transactionSynchronizations.remove(em[i]);
                    if (txSync != null) {
                        synchronizationsToApply.add(txSync);
                    }
                }
            }
            
            for (Synchronization txSync : synchronizationsToApply) {
                txSync.beforeCompletion();
            }
            
            // Normal iteration (commit/sync "inner" entityManagers first)
            for( int i = em.length-1; i >= 0; --i ) { 
                if (em[i].getTransaction().isActive()) {
                    em[i].getTransaction().commit();
                }
            }
            
            for (Synchronization txSync : synchronizationsToApply) {
                txSync.afterCompletion(Status.STATUS_COMMITTED);
            }
        }
    }

    /**
     * Roll back the transaction.
     */
    public void rollback(boolean transactionOwner) {
        EntityManager [] em = entityManagers.toArray(new EntityManager[entityManagers.size()]);
        if (transactionOwner) {
            // At most, we expect 2 synchronizations to be registered: cmd-scoped EM and app-scoped EM
            List<Synchronization> synchronizationsToApply = new ArrayList<Synchronization>(2);

            // Fill synchronizationsToApply with Synchronization objects that apply to this tx/entity manager
            for( int i = em.length-1; i >= 0; --i ) { 
                if (em[i].getTransaction().isActive()) {
                    Synchronization txSync = transactionSynchronizations.remove(em[i]);
                    if (txSync != null) {
                        synchronizationsToApply.add(txSync);
                    }
                }
            }
            
            for (Synchronization txSync : synchronizationsToApply) {
                txSync.beforeCompletion();
            }
            
            // Normal iteration (commit/sync "inner" entityManagers first)
            for( int i = em.length-1; i >= 0; --i ) { 
                if (em[i].getTransaction().isActive()) {
                    em[i].getTransaction().rollback();
                }
            }
            
            for (Synchronization txSync : synchronizationsToApply) {
                txSync.afterCompletion(Status.STATUS_ROLLEDBACK);
            }
        } else {
            // normal iteration (commit/sync "inner" entityManagers first)
            for( int i = em.length-1; i >= 0; --i ) { 
                if (em[i].getTransaction().isActive()) {
                    em[i].getTransaction().setRollbackOnly();
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
            if (entityManagers.add(em)) { 
                /**
                 * If entityManagers.size() > 2, then the list looks like this:
                 *  0. app scoped persistence context 
                 *  1. old cmd scoped persistence context 
                 *  2. new cmd scoped persistence context 
                 * So, obviously, we delete 1: 
                 * reason 1: there is never more than one cmd-scoped persistence context 
                 *           per ksession active 
                 * reason 2: there is also never more than 1 cmd-scoped persistence context 
                 *            and 1 app-scoped persistence context per ksession active
                 * 
                 * By doing this _here_, as opposed to in every loop, means that
                 * we keep the entityManagers list small. This means that 
                 *  a. methods referring to this list stay fast 
                 *  b. garbage collection doesn't slow down the jvm when it's discovered that
                 *     there are a bunch of empty weak ref's that need to be deleted
                 *     from the entity manager list (if we were using a weak-ref based collection).
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
                    Synchronization txSync = new JtaTransactionSynchronizationAdapter(transactionSynchronization);
                    transactionSynchronizations.put(em, txSync);
                    transactionSynchronization = null;
                }
            } else {
                logger.trace("active entity manager attached: " + em.getTransaction().isActive());
            }
        }
    }

    public void setApplicationScopedEntityManager(EntityManager em) { 
        this.entityManagers.add(em);
    }
    
    /**
     * These getters are used for testing. Note the scope of the methods..
     */

    List<EntityManager> getEntityManagers() {
        return new ArrayList<EntityManager>(entityManagers);
    }

    /**
     * Mostly for testing purposes
     * @return
     */
    List<Synchronization> getTransactionSynchronizations() {
        List<Synchronization> realTxSyncs = new ArrayList<Synchronization>();
        for( EntityManager em : transactionSynchronizations.keySet() ) { 
           realTxSyncs.add(transactionSynchronizations.get(em));
        }
        return realTxSyncs;
    }

}
