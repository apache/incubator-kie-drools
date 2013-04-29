package org.jbpm.runtime.manager.impl.tx;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.drools.persistence.TransactionSynchronization;
import org.drools.persistence.TransactionSynchronizationRegistryHelper;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.persistence.jta.JtaTransactionSynchronizationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension of default <code>JTATransactionManger</code> is provided 
 * to make sure transaction synchronizations are registered as regular ones
 * instead of interposed that will cause issues with other transaction synchronizations
 * mainly one registered by SingleSessionCommandService to clean up on transaction end.
 *
 */
public class ExtendedJTATransactionManager extends JtaTransactionManager {

    private  Logger logger = LoggerFactory.getLogger( getClass() );
    private TransactionManager tm;
    private UserTransaction ut;
    private TransactionSynchronizationRegistry tsr;
    public ExtendedJTATransactionManager(Object ut, Object tsr, Object tm) {
        super(ut, tsr, tm);
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
        this.tsr = (TransactionSynchronizationRegistry) ((tsr != null) ? tsr : findTransactionSynchronizationRegistry( this.ut,
                this.tm ));
    }

    @Override
    public void registerTransactionSynchronization(TransactionSynchronization ts) {
        // the only change here compared to it's super impl is to do not use TransactionSynchrnonizationTegistry
        if ( tm != null ) {
            try {
                this.tm.getTransaction().registerSynchronization( new JtaTransactionSynchronizationAdapter( ts ) );
            } catch ( Exception e ) {
                // No JTA TransactionManager available - log a warning.
                logger.warn( "Participating in existing JTA transaction, but no JTA TransactionManager or TransactionSychronizationRegistry available: ",
                             e );

            }
        } else if ( this.tsr != null ) {
            TransactionSynchronizationRegistryHelper.registerTransactionSynchronization( this.tsr,
                    ts );
        } else {
            // No JTA TransactionManager available - log a warning.
            logger.warn( "Participating in existing JTA transaction, but no JTA TransactionManager or TransactionSychronizationRegistry available: " );
        }
    }
    
    

}
