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
package org.drools.persistence.jta;

import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionSynchronization;

public class JtaTransactionSynchronizationAdapter
    implements
    Synchronization {
    TransactionSynchronization ts;

    public JtaTransactionSynchronizationAdapter(TransactionSynchronization ts) {
        super();
        this.ts = ts;
    }

    public void afterCompletion(int status) {
        switch ( status ) {
            case Status.STATUS_COMMITTED :
                this.ts.afterCompletion( TransactionManager.STATUS_COMMITTED );
                break;
            case Status.STATUS_ROLLEDBACK :
                this.ts.afterCompletion( TransactionManager.STATUS_ROLLEDBACK );
                break;
            case Status.STATUS_NO_TRANSACTION :
                this.ts.afterCompletion(  TransactionManager.STATUS_NO_TRANSACTION );
                break;
            case Status.STATUS_ACTIVE :
                this.ts.afterCompletion( TransactionManager.STATUS_ACTIVE );
                break;
            default :
                this.ts.afterCompletion( TransactionManager.STATUS_UNKNOWN );
        }
    }

    public void beforeCompletion() {
        this.ts.beforeCompletion();
    }
}
