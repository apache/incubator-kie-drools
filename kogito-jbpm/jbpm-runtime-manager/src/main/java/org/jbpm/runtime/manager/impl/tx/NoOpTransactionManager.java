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

package org.jbpm.runtime.manager.impl.tx;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionSynchronization;


public class NoOpTransactionManager implements TransactionManager {

    @Override
    public void rollback(boolean transactionOwner) {                
    }
    
    @Override
    public void registerTransactionSynchronization(TransactionSynchronization ts) {                
    }
    
    @Override
    public void putResource(Object key, Object resource) {                
    }
    
    @Override
    public int getStatus() {
        return STATUS_NO_TRANSACTION;
    }
    
    @Override
    public Object getResource(Object key) {
        return null;
    }
    
    @Override
    public void commit(boolean transactionOwner) {
        
    }
    
    @Override
    public boolean begin() {
        return false;
    }

}
