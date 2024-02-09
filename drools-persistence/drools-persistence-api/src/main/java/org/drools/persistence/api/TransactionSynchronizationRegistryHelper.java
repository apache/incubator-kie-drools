/**
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
package org.drools.persistence.api;

import jakarta.transaction.TransactionSynchronizationRegistry;

import org.drools.persistence.jta.JtaTransactionSynchronizationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionSynchronizationRegistryHelper {

    private static final Logger logger = LoggerFactory.getLogger(TransactionSynchronizationRegistryHelper.class);
    public static void registerTransactionSynchronization(final Object tsro, final TransactionSynchronization ts) {
        TransactionSynchronizationRegistry tsr = ( TransactionSynchronizationRegistry ) tsro;
        tsr.registerInterposedSynchronization( new JtaTransactionSynchronizationAdapter( ts ) );
    }

    public static void putResource(final Object tsro, final Object key, final Object resource) {
        TransactionSynchronizationRegistry tsr = ( TransactionSynchronizationRegistry ) tsro;
        try {
            tsr.putResource(key, resource);
        } catch (IllegalStateException e) {
            // IllegalStateException is thrown when no transaction is active
            // so report only when there is actually resource to be inserted
            if (resource != null) {
                logger.warn("Unable to put resource {} value {} due to {}", key, resource, e.getMessage());
            }
        } catch (Exception e) {
            logger.warn("Unable to put resource {} value {} due to {}", key, resource, e.getMessage());
        }
    }

    public static Object getResource(final Object tsro, final Object key) {
        TransactionSynchronizationRegistry tsr = ( TransactionSynchronizationRegistry ) tsro;
        try {
            return tsr.getResource(key);
        } catch (Exception e) {
            return null;
        }
    }
}
