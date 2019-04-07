/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.listener.process;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.drools.persistence.api.TransactionSynchronization;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultCountDownProcessEventListener extends DefaultProcessEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCountDownProcessEventListener.class);

    protected CountDownLatch latch;

    public DefaultCountDownProcessEventListener() {

    }

    public DefaultCountDownProcessEventListener(int threads) {
        this.latch = new CountDownLatch(threads);
    }

    public void waitTillCompleted() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all triggers");
        }
    }

    public void waitTillCompleted(long timeOut) {
        try {
            latch.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all triggers");
        }
    }

    public void reset(int threads) {
        this.latch = new CountDownLatch(threads);
    }

    protected void countDown() {
        try {
            TransactionManager tm = TransactionManagerFactory.get().newTransactionManager();
            if (tm != null && tm.getStatus() != TransactionManager.STATUS_NO_TRANSACTION
                    && tm.getStatus() != TransactionManager.STATUS_ROLLEDBACK
                    && tm.getStatus() != TransactionManager.STATUS_COMMITTED) {
                tm.registerTransactionSynchronization(new TransactionSynchronization() {

                    @Override
                    public void beforeCompletion() {
                    }

                    @Override
                    public void afterCompletion(int status) {
                        latch.countDown();
                    }
                });
            } else {
                latch.countDown();
            }
        } catch (Exception e) {
            latch.countDown();
        }
    }
}
