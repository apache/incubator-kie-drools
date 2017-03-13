/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.persistence;

import org.drools.core.impl.EnvironmentFactory;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.persistence.jta.JtaTransactionManagerFactory;
import org.junit.After;
import org.junit.Test;
import org.kie.api.runtime.Environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class TransactionManagerFactoryTest {

    TransactionManagerFactory transactionManagerFactory =
            getTransactionManagerFactory();

    @After
    public void cleanup() {
        System.clearProperty("org.kie.txm.factory.class");
        transactionManagerFactory.resetInstance();
    }

    @Test
    public void defaultsToJtaTransactionManagerFactory() throws Exception {

        assertTrue(transactionManagerFactory.get() instanceof JtaTransactionManagerFactory);
    }

    @Test
    public void createsSystemPropertySpecifiedFactory() throws Exception {
        System.setProperty("org.kie.txm.factory.class",
                           TestTransactionManagerFactory.class.getName());
        transactionManagerFactory.resetInstance();

        assertEquals(TestTransactionManagerFactory.class.getName(),
                     transactionManagerFactory.get().getClass().getName());
    }

    @Test
    public void createAndResetSystemPropertySpecifiedFactory() throws Exception {
        System.setProperty("org.kie.txm.factory.class",
                           TestTransactionManagerFactory.class.getName());

        transactionManagerFactory.resetInstance();

        assertEquals(TestTransactionManagerFactory.class.getName(),
                     transactionManagerFactory.get().getClass().getName());

        System.setProperty("org.kie.txm.factory.class",
                           TestTransactionManagerFactoryTwo.class.getName());

        transactionManagerFactory.resetInstance();

        assertEquals(TestTransactionManagerFactoryTwo.class.getName(),
                     transactionManagerFactory.get().getClass().getName());
        transactionManagerFactory.resetInstance();
    }

    @Test
    public void createsJtaTransactionManager() throws Exception {
        assertEquals(JtaTransactionManager.class.getName(),
                     transactionManagerFactory.newTransactionManager().getClass().getName());
    }

    @Test
    public void createsJtaTransactionManagerWithEnvironment() throws Exception {
        Environment env = EnvironmentFactory.newEnvironment();

        assertEquals(JtaTransactionManagerFactory.class.getName(),
                     transactionManagerFactory.get().getClass().getName());

        assertNotNull(transactionManagerFactory.newTransactionManager(env));

        assertEquals(JtaTransactionManager.class.getName(),
                     transactionManagerFactory.newTransactionManager(env).getClass().getName());
    }

    private TransactionManagerFactory getTransactionManagerFactory() {
        try {
            Class<?> factoryClazz = this.getClass().getClassLoader().loadClass("org.drools.persistence.api.TransactionManagerFactory");
            return (TransactionManagerFactory) factoryClazz.getMethod("get").invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static final class TestTransactionManagerFactory extends TransactionManagerFactory {

        @Override
        public TransactionManager newTransactionManager() {
            return null;
        }

        @Override
        public TransactionManager newTransactionManager(Environment environment) {
            return null;
        }
    }

    public static final class TestTransactionManagerFactoryTwo extends TransactionManagerFactory {

        @Override
        public TransactionManager newTransactionManager() {
            return null;
        }

        @Override
        public TransactionManager newTransactionManager(Environment environment) {
            return null;
        }
    }
}

