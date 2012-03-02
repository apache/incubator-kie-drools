package org.jbpm.task.service.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public abstract class TaskTransactionManager {
    
    enum TransactionStatus { 
        ACTIVE, COMMITTED, 
        MARKED_ROLLBACK, ROLLEDBACK, 
        NO_TRANSACTION, UNKNOWN; 
    }

    abstract void begin(EntityManager em);

    abstract void commit(EntityManager em);

    abstract void rollback(EntityManager em, boolean txOwner);

    abstract void registerTransactionSynchronization(TransactionSynchronization ts);
    
    abstract TransactionStatus getStatus(EntityManager em);
    
    abstract boolean ownsTransaction(EntityManager em);
    
    abstract void attachPersistenceContext(EntityManager em);
    
    interface TransactionSynchronization {
        void beforeCompletion();
        void afterCompletion(int status);
    }
    
    static TaskTransactionManager getInstance(EntityManagerFactory emf) {
        TaskTransactionManager ttxm = null;
        EntityManager em = emf.createEntityManager();
        
        boolean useResourceLocalTxm = false;
        boolean useJTATxm = false;
        try { 
            em.getTransaction();
            useResourceLocalTxm = true;
        } catch(Exception e) { 
            boolean illegalStateExceptionThrown = false;
            Throwable cause = e;
            while( cause != null && ! illegalStateExceptionThrown ) { 
                illegalStateExceptionThrown = (cause instanceof IllegalStateException);
                cause = cause.getCause();
            }
            if( illegalStateExceptionThrown ) { 
                useJTATxm = true;
            }
            else { 
                // this resource is not JTA
                throw new RuntimeException("Unable to determine persistence-unit type (JTA/Local)", e);
            }
        }
            
        if( useJTATxm ) { 
            ttxm = new TaskJTATransactionManager();
        } else if( useResourceLocalTxm ) { 
            ttxm = new TaskLocalTransactionManager();
        } else { 
            throw new RuntimeException("Unknown resource type");
        }
        return ttxm;
    }
}
