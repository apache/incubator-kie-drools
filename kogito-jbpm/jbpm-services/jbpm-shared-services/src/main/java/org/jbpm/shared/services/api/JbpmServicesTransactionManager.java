/**
 * Copyright 2012 JBoss Inc
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
package org.jbpm.shared.services.api;

import javax.persistence.EntityManager;

import org.drools.persistence.TransactionSynchronization;

public interface JbpmServicesTransactionManager {
    
    public boolean begin(EntityManager em);

    public void commit(EntityManager em, boolean txOwner);

    /**
     * It is the responsibility of this method
     * to check that the status of the transaction
     * is appropriate before rolling back the transaction.
     * 
     * @param em The persistence context (aka, the entity manager)
     * @param txOwner Whether or not the calling clause is owner of this transaction.
     */
    public void rollback(EntityManager em, boolean txOwner);

    public int getStatus(EntityManager em);
    
    public void attachPersistenceContext(EntityManager em);
    
    public void dispose();
    
    public void registerTXSynchronization(TransactionSynchronization txSync);
}
