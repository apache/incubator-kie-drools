/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
 package org.drools.persistence.api;


public interface TransactionManager {
    /** Completion status in case of proper commit */
    int STATUS_COMMITTED      = 0;

    /** Completion status in case of proper rollback */
    int STATUS_ROLLEDBACK     = 1;

    /** Completion status in case of heuristic mixed completion or system errors */
    int STATUS_UNKNOWN        = 2;

    /** No existing transaction is associated with this threat */
    int STATUS_NO_TRANSACTION = 3;

    /** Transaction is Active */
    int STATUS_ACTIVE         = 4;

    int getStatus();

    boolean begin();

    void commit(boolean transactionOwner);

    void rollback(boolean transactionOwner);

    void registerTransactionSynchronization(TransactionSynchronization ts);

    void putResource(Object key, Object resource);

    Object getResource(Object key);

}
