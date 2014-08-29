/*
 * Copyright 2014 JBoss Inc
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

import java.util.concurrent.locks.ReentrantLock;

import org.drools.core.command.impl.AbstractInterceptor;
import org.drools.persistence.OrderedTransactionSynchronization;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionManagerHelper;
import org.kie.api.command.Command;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor that will lock underlying <code>CommandService</code> until transaction completion.
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

    public TransactionLockInterceptor(Environment environment) {
        this.environment = environment;
        this.active = Boolean.getBoolean("org.kie.tx.lock.enabled");
        if (environment.get("TRANSACTION_LOCK_ENABLED") != null) {
            this.active = Boolean.parseBoolean(environment.get("TRANSACTION_LOCK_ENABLED").toString());
        }
        logger.debug("Transaction lock interceptor enabled " + this.active);
    }

    @Override
    public <T> T execute(Command<T> command) {
        if (!active) {
            return executeNext(command);
        }

        // proceed only when explicitly activated
        boolean locked = false;
        if (!lock.isHeldByCurrentThread()) {
            logger.debug("About to get a lock on command service by {}", Thread.currentThread().getName());
            lock.lock();
            locked = true;
            logger.debug("Lock taken by {}", Thread.currentThread().getName());
        }
        try {
            return executeNext(command);
        } finally {
            if (locked) {
                logger.debug("About to register lock release handler by {}", Thread.currentThread().getName());
                release((TransactionManager) environment.get(EnvironmentName.TRANSACTION_MANAGER));
            }
        }
    }

    protected void release(TransactionManager txm) {
        try {
            TransactionManagerHelper.registerTransactionSyncInContainer(txm, new OrderedTransactionSynchronization(100, "tx-unlock") {

                @Override
                public void beforeCompletion() {
                }

                @Override
                public void afterCompletion(int status) {
                    logger.debug("Releasing on transaction completion by {}", Thread.currentThread().getName());
                    lock.unlock();
                    logger.debug("Successfully released lock by {}", Thread.currentThread().getName());
                }
            });
        } catch (Throwable e) {
            logger.debug("Error happened releasing directly by {} due to {}", Thread.currentThread().getName(), e.getMessage());
            lock.unlock();
        }
    }

}
