package org.jbpm.process.audit.strategy;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TransactionRequiredException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy is used by instances that are<ul>
 * <li>used outside the {@link KieSession}</li>
 * <li>use their own {@link EntityManager} instance per operation</li>
 * </ul>
 */
public class StandaloneJtaStrategy implements PersistenceStrategy {

    private static final Logger logger = LoggerFactory.getLogger(JPAWorkingMemoryDbLogger.class);

    private static final String[] KNOWN_UT_JNDI_KEYS = new String[] {"UserTransaction", "java:jboss/UserTransaction", System.getProperty("jbpm.ut.jndi.lookup")};

    protected EntityManagerFactory emf;
    
    public StandaloneJtaStrategy(EntityManagerFactory emf) { 
        this.emf = emf;
    }
    
    @Override
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Object joinTransaction(EntityManager em) {
        boolean newTx = false;
        UserTransaction ut = findUserTransaction();
        try {
            if( ut != null && ut.getStatus() == Status.STATUS_NO_TRANSACTION ) { 
                ut.begin();
                newTx = true;
                // since new transaction was started em must join it
            } 
        } catch(Exception ex) {
            throw new IllegalStateException("Unable to find or open a transaction: " + ex.getMessage(), ex);
        }

        try { 
            em.joinTransaction();
        } catch( Exception e) { 
            throw new IllegalStateException("Unable to join EntityManager to transaction: " + e.getMessage(), e);
        }

        if( newTx ) { 
            return ut;
        }
        return null;
    }

    protected static UserTransaction findUserTransaction() {
        InitialContext context = null;
        try {
            context = new InitialContext();
            return (UserTransaction) context.lookup( "java:comp/UserTransaction" );
        } catch ( NamingException ex ) {
            for (String utLookup : KNOWN_UT_JNDI_KEYS) {
                if (utLookup != null) {
                    try {
                        UserTransaction ut = (UserTransaction) context.lookup(utLookup);
                        return ut;
                    } catch (NamingException e) {
                        logger.debug("User Transaction not found in JNDI under {}", utLookup);
                        
                    }
                }
            }
            logger.warn("No user transaction found under known names");
            return null;
        }
    }

    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        commitTransaction(transaction);
        
        em.clear();
        em.close();
    }

    protected void commitTransaction(Object transaction) {
        UserTransaction ut = null;
        if( ! (transaction instanceof UserTransaction) ) { 
           throw new IllegalStateException("This persistence strategy only deals with UserTransaction instances!" );
        } else if( transaction != null ){ 
           ut = (UserTransaction) transaction;
        }
        
        try { 
            if( ut != null ) { 
                // There's a tx running, close it.
                ut.commit();
            }
        } catch(Exception e) { 
            logger.error("Unable to commit transaction: ", e);
        }
    }

    @Override
    public void dispose() {
        // NEVER close the emf, you don't know what it is also being used for!
        emf = null;
    }
}
