/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.drools.persistence.jta;

import org.drools.core.command.impl.AbstractInterceptor;
import org.drools.persistence.api.OrderedTransactionSynchronization;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerHelper;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Status;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ExecutableInterceptor that will lock underlying <code>Runner</code> until transaction completion.
 * In case there is no transaction active lock is released directly.
 *
 * By default, interceptor is disabled and needs to be explicitly enabled in one of two ways:
 * <ul>
 *  <li>using system property named <code>org.kie.tx.lock.enabled</code> that must be set to true</li>
 *  <li>using environment entry named <code>TRANSACTION_LOCK_ENABLED</code> that must be set to true</li>
 * </ul>
 */
public class TransactionLockInterceptor extends AbstractInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionLockInterceptor.class);
    private boolean active = false;
    private ReentrantLock lock = new ReentrantLock();
    private Environment environment;

    private String releaseTxKey;

    private Set<Long> forceUnlock = new CopyOnWriteArraySet<Long>();

    public TransactionLockInterceptor(Environment environment) {
        this(environment, "tx-unlock");
    }

    public TransactionLockInterceptor(Environment environment, String releaseTxKey) {
        this.environment = environment;
        this.releaseTxKey = releaseTxKey;
        this.active = Boolean.getBoolean("org.kie.tx.lock.enabled");
        if (environment.get("TRANSACTION_LOCK_ENABLED") != null) {
            this.active = Boolean.parseBoolean(environment.get("TRANSACTION_LOCK_ENABLED").toString());
        }
        logger.debug("Transaction lock interceptor enabled " + this.active);
    }

    @Override
    public RequestContext execute( Executable executable, RequestContext ctx ) {
        if (!active) {
            executeNext(executable, ctx);
            return ctx;
        }
        // release before entering in case it failed previously to avoid deadlock
        releaseAfterFailure();
        // proceed only when explicitly activated
        boolean locked = false;
        if (!lock.isHeldByCurrentThread()) {
            logger.debug("About to get a lock on command service by {}", Thread.currentThread().getName());
            lock.lock();
            locked = true;
            logger.debug("Lock taken by {}", Thread.currentThread().getName());
        }
        try {
            executeNext(executable, ctx);
        } finally {
            if (locked) {
                logger.debug("About to register lock release handler by {}", Thread.currentThread().getName());
                release((TransactionManager) environment.get(EnvironmentName.TRANSACTION_MANAGER));
            }
            releaseAfterFailure();
        }
        return ctx;
    }

    protected void release(TransactionManager txm) {
        try {
            TransactionManagerHelper.registerTransactionSyncInContainer(txm, new ReleaseLockTransactionSynchronization(Thread.currentThread().getId(), 100, releaseTxKey));
        } catch (Throwable e) {
            logger.debug("Error happened releasing directly by {} due to {}", Thread.currentThread().getName(), e.getMessage());
            doRelease();
        }
    }

    protected void releaseAfterFailure() {
        if (forceUnlock.remove(Thread.currentThread().getId())) {
            logger.debug("Forcibly unlocking as it was requested by a reaper thread (transaction timeout)");
            doRelease();
        }
    }

    protected void doRelease() {
        logger.debug("Releasing on transaction completion by {}", Thread.currentThread().getName());
        lock.unlock();
        logger.debug("Successfully released lock by {}", Thread.currentThread().getName());
    }

    private class ReleaseLockTransactionSynchronization extends OrderedTransactionSynchronization {

        private volatile long registrationThreadId;

        public ReleaseLockTransactionSynchronization(long threadId, Integer order, String identifier) {
            super(order, identifier);
            this.registrationThreadId = threadId;
        }

        @Override
        public void beforeCompletion() {

        }

        @Override
        public void afterCompletion(int status) {

            if (isRollback(status)) {
                final long currentThreadId = Thread.currentThread().getId();
                final boolean isRegistrationThread = currentThreadId == registrationThreadId;
                if ( ! isRegistrationThread ) {
                    logger.debug("Attempt to unlock from different thread {} while owner is {}, requesting force unlock", currentThreadId, registrationThreadId );
                    forceUnlock.add(registrationThreadId);
                    return;
                }
            }
            doRelease();
        }

        boolean isRollback(int status) {
            return status == Status.STATUS_MARKED_ROLLBACK ||
                    status == Status.STATUS_ROLLING_BACK ||
                    status == Status.STATUS_ROLLEDBACK;
        }
    }

}
