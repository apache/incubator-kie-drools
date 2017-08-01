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

package org.jbpm.test.container.webspherefix;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

/**
 * Workaroud for https://hibernate.atlassian.net/browse/HHH-11606 .
 */
public class WebSphereFixedJtaPlatform extends AbstractJtaPlatform {

    private static final long serialVersionUID = 1L;
    public static final String UT_NAME = "java:comp/UserTransaction";

    @Override
    protected boolean canCacheTransactionManager() {
        return true;
    }

    @Override
    protected TransactionManager locateTransactionManager() {
        try {
            return (TransactionManager) Class.forName("com.ibm.ws.Transaction.TransactionManagerFactory").getMethod("getTransactionManager").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction) jndiService().locate(UT_NAME);
    }

    @Override
    public Object getTransactionIdentifier(Transaction transaction) {
        // WebSphere, however, is not a sane JEE/JTA container...
        return Integer.valueOf(transaction.hashCode());
    }
}
