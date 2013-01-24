/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.droolsjbpm.services.impl.util;



import bitronix.tm.BitronixTransaction;
import bitronix.tm.TransactionManagerServices;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.core.Veto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.jboss.seam.transaction.AbstractUserTransaction;
import org.jboss.seam.transaction.DefaultTransaction;
import org.jboss.seam.transaction.Synchronizations;

/**
 * Support for the JPA EntityTransaction API.
 * <p/>
 * Adapts JPA transaction management to a Seam UserTransaction interface.For use
 * in non-JTA-capable environments.
 *
 * @author Gavin King
 */
@ApplicationScoped
@DefaultTransaction
@Veto
public class DefaultBitronixTransaction extends AbstractUserTransaction {
    private static final Logger log = Logger.getLogger(DefaultBitronixTransaction.class);

   

    @Inject
    public void init(Synchronizations sync) {
        setSynchronizations(sync);
    }

    public DefaultBitronixTransaction() {
    }

    private UserTransaction getDelegate() {
        return TransactionManagerServices.getTransactionManager();
    }

    public void begin() throws NotSupportedException, SystemException {
        log.debug("beginning JPA resource-local transaction");
        // TODO: translate exceptions that occur into the correct JTA exception
        try {
            getDelegate().begin();
            getSynchronizations().afterTransactionBegin();
        } catch (RuntimeException re) {
            throw re;
        }
    }

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        log.debug("committing JPA resource-local transaction");
        UserTransaction delegate = getDelegate();
        boolean success = false;
        try {
//            if (delegate.getRollbackOnly()) {
//                delegate.rollback();
//                throw new RollbackException();
//            } else {
                getSynchronizations().beforeTransactionCommit();
                delegate.commit();
                success = true;
           // }
        } finally {
            getSynchronizations().afterTransactionCompletion(success);
        }
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        log.debug("rolling back JPA resource-local transaction");
        // TODO: translate exceptions that occur into the correct JTA exception
        UserTransaction delegate = getDelegate();
        try {
            delegate.rollback();
        } finally {
            getSynchronizations().afterTransactionCompletion(false);
        }
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        log.debug("marking JPA resource-local transaction for rollback");
        getDelegate().setRollbackOnly();
    }

    public int getStatus() throws SystemException {
        return getDelegate().getStatus();
    }

    public void setTransactionTimeout(int timeout) throws SystemException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerSynchronization(Synchronization sync) {
        if (log.isDebugEnabled()) {
            log.debug("registering synchronization: " + sync);
        }
        // try to register the synchronization directly with the
        // persistence provider
        getSynchronizations().registerSynchronization(sync);
    }

    @Override
    public boolean isConversationContextRequired() {
        return true;
    }

    @Override
    public void enlist(EntityManager entityManager) {
        // no-op
    }

}
