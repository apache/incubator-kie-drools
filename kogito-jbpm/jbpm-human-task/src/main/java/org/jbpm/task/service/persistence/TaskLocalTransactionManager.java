package org.jbpm.task.service.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TaskLocalTransactionManager extends TaskTransactionManager {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    TaskLocalTransactionManager() { 
        // empty constructor
    }
    
    boolean ownsTransaction(EntityManager em) { 
        TransactionStatus status = getStatus(em);
       return (status != TransactionStatus.ACTIVE
               && status != TransactionStatus.MARKED_ROLLBACK);     
    }
    
    void attachPersistenceContext(EntityManager em) { 
        // no-op for entity transactions
    }
    
    synchronized void begin(EntityManager em) {
        try {
            em.getTransaction().begin();
        } catch (Exception e) {
            logger.warn("Unable to begin transaction", e);
            throw new RuntimeException("Unable to begin transaction", e);
        }
    }

    void commit(EntityManager em) {
        try { 
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.warn("Unable to begin transaction", e);
            throw new RuntimeException("Unable to commit transaction", e);
        }
    }

    void rollback(EntityManager em, boolean txOwner) {
        if( ! em.getTransaction().isActive() ) { 
            return;
        }
        
        try {
            if( txOwner ) { 
                em.getTransaction().rollback();
            }
            else { 
                em.getTransaction().setRollbackOnly();
            }
        } catch (Exception e) {
            logger.warn("Unable to rollback transaction", e);
            throw new RuntimeException("Unable to rollback transaction", e);
        }
    }

    TransactionStatus getStatus(EntityManager em) { 
        EntityTransaction tx = em.getTransaction();
        if( tx.isActive() ) { 
            if( tx.getRollbackOnly() ) { 
                return TransactionStatus.MARKED_ROLLBACK;
            }
            return TransactionStatus.ACTIVE;
        }
        else { 
            return TransactionStatus.COMMITTED;
        }
    }
    
    void registerTransactionSynchronization(TransactionSynchronization ts) {
        // DBG Auto-generated method stub

    }

}
