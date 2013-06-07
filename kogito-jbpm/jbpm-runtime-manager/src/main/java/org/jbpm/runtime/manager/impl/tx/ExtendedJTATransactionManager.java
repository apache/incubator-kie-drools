/*
 * Copyright 2013 JBoss Inc
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
