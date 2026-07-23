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
package org.kie.kogito.app.jobs.spi;

/**
 * Interface for marking the current transaction for rollback.
 * This is used when a job execution fails and needs to be retried, ensuring that
 * any changes made during the failed execution are rolled back before the retry attempt.
 */
public interface TransactionRollbackMarker {

    /**
     * Marks the current transaction for rollback.
     * If no transaction is active, this method should do nothing.
     */
    void markForRollback();

    /**
     * Checks if a transaction is currently active.
     * 
     * @return true if a transaction is active, false otherwise
     */
    boolean isTransactionActive();
}
