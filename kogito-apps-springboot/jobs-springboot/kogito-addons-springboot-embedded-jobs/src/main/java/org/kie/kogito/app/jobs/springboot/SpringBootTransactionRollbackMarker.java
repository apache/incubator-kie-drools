/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.app.jobs.springboot;

import org.kie.kogito.app.jobs.spi.TransactionRollbackMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class SpringBootTransactionRollbackMarker implements TransactionRollbackMarker {

    private static final Logger LOG = LoggerFactory.getLogger(SpringBootTransactionRollbackMarker.class);

    // Thread-local to store the current TransactionStatus for TransactionTemplate usage
    private static final ThreadLocal<TransactionStatus> CURRENT_TRANSACTION_STATUS = new ThreadLocal<>();

    public static void setCurrentTransactionStatus(TransactionStatus status) {
        CURRENT_TRANSACTION_STATUS.set(status);
    }

    public static void clearCurrentTransactionStatus() {
        CURRENT_TRANSACTION_STATUS.remove();
    }

    @Override
    public void markForRollback() {
        if (isTransactionActive()) {
            TransactionStatus status = CURRENT_TRANSACTION_STATUS.get();
            if (status != null) {
                status.setRollbackOnly();
                LOG.debug("Transaction marked for rollback using TransactionStatus");
            } else {
                LOG.warn("No TransactionStatus available to mark for rollback");
            }
        }
    }

    @Override
    public boolean isTransactionActive() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }
}
