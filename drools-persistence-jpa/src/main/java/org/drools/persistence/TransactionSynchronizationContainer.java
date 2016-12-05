/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

public class TransactionSynchronizationContainer implements TransactionSynchronization {

    private static Logger logger = LoggerFactory.getLogger( TransactionSynchronizationContainer.class );
    public static final String RESOURCE_KEY = "org.drools.persistence.txsync.container";

    private Set<TransactionSynchronization> synchronizations = new TreeSet<TransactionSynchronization>();

    @Override
    public void beforeCompletion() {
        TransactionSynchronization[] txSyncArray = synchronizations.toArray(new TransactionSynchronization[synchronizations.size()]);

        for (TransactionSynchronization txSync : txSyncArray) {

            txSync.beforeCompletion();
        }
    }

    @Override
    public void afterCompletion(int status) {
        TransactionSynchronization[] txSyncArray = synchronizations.toArray(new TransactionSynchronization[synchronizations.size()]);

        for (TransactionSynchronization txSync : txSyncArray) {

            txSync.afterCompletion(status);
        }
    }

    public void addTransactionSynchronization(TransactionSynchronization txSync) {

        this.synchronizations.add(txSync);
        logger.debug("Adding sync {} total syncs ", txSync, synchronizations.size());
    }
}
