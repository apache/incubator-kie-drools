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
package org.drools.persistence.jta;

import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionManagerFactory;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;

/**
 * Implementation of {@link org.drools.persistence.TransactionManager} that creates
 * {@link JtaTransactionManager} instances.
 * 
 */
public class JtaTransactionManagerFactory extends TransactionManagerFactory {

    @Override
    public TransactionManager newTransactionManager() {
        return new JtaTransactionManager(null, null, null);
    }

    /**
     * Creates a {@link JtaTransactionManager} instance using any of the {@link javax.transaction.UserTransaction},
     * {@link javax.transaction.TransactionSynchronizationRegistry}, and {@link javax.transaction.TransactionManager}
     * present in {@code env}.
     * 
     * @param env
     */
    @Override
    public TransactionManager newTransactionManager(Environment env) {
        return new JtaTransactionManager(
            env.get(EnvironmentName.TRANSACTION),
            env.get(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY),
            env.get(EnvironmentName.TRANSACTION_MANAGER ));
    }    
}
