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

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.drools.core.impl.EnvironmentFactory;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.persistence.jta.JtaTransactionManagerFactory;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.Environment;

import com.google.common.collect.Lists;

public class TransactionManagerFactoryTest {

    @Before
    public void clearFactoryClassProperty() {
        System.clearProperty("org.kie.txm.factory.class");
    }
    
    @Test
    public void defaultsToJtaTransactionManagerFactory() throws Exception {
        assertEquals(JtaTransactionManagerFactory.class.getName(), loadNewTransactionManagerFactory().getClass().getName());
    }

    @Test
    public void createsSystemPropertySpecifiedFactory() throws Exception {
        System.setProperty("org.kie.txm.factory.class", TestTransactionManagerFactory.class.getName());
        assertEquals(TestTransactionManagerFactory.class.getName(), loadNewTransactionManagerFactory().getClass().getName());

    }
    
    @Test
    public void createsJtaTransactionManager() throws Exception {
        assertEquals(JtaTransactionManager.class, getTransactionManagerFromNewFactory().getClass());
    }
    
    @Test
    public void createsJtaTransactionManagerWithEnvironment() throws Exception {
        Environment env = EnvironmentFactory.newEnvironment();
        assertEquals(JtaTransactionManager.class, getTransactionManagerFromNewFactory(env).getClass());
    }

    /**
     * Gets the TransactionManager returned by a TransactionManagerFactory loaded from a new ClassLoader.
     * 
     * @param cl
     * @return
     * @throws Exception
     */
    private TransactionManager getTransactionManagerFromNewFactory() throws Exception {
        Object factory = loadNewTransactionManagerFactory();
        return (TransactionManager) factory.getClass().getMethod("newTransactionManager").invoke(factory);
    }

    /**
     * Gets the TransactionManager returned by a TransactionManagerFactory loaded from a new ClassLoader.
     * 
     * @return
     * @throws Exception
     */
    private TransactionManager getTransactionManagerFromNewFactory(Environment env) throws Exception {
        Object factory = loadNewTransactionManagerFactory();
        return (TransactionManager) factory.getClass().getMethod("newTransactionManager", Environment.class).invoke(factory, env);
    }

    /**
     * Loads the TransactionManagerFactory using a new ClassLoader.
     * 
     * @param cl
     * @return
     */
    private Object loadNewTransactionManagerFactory() throws Exception {
        Class<?> factoryClass = createFactoryClassLoader().loadClass("org.drools.persistence.TransactionManagerFactory");
        return factoryClass.getMethod("get").invoke(null);
    }
    
    /**
     * Creates a ClassLoader that will directly load the TransactionManagerFactory without
     * first delegating to the parent ClassLoader.  This is to allow the static block within
     * TransactionManagerFactory to be executed multiple times within the VM.
     * 
     * @return
     */
    private ClassLoader createFactoryClassLoader() {
        ArrayList<URL> urls = Lists.newArrayList(((URLClassLoader) getClass().getClassLoader()).getURLs());
        return new URLClassLoader(urls.toArray(new URL[0]), getClass().getClassLoader()) {
            public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                // Loads TransactionManagerFactory and subclasses without delegating to parent.
                if (name.endsWith("TransactionManagerFactory")) {
                    Class<?> c = findLoadedClass(name);
                    if (c == null) {
                        c = findClass(name);
                    }
                    if (resolve) {
                        resolveClass(c);
                    }
                    return c;
                } else {
                    return super.loadClass(name, resolve);
                }
            }
        };
    }
    
    /**
     * 
     */
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
}
