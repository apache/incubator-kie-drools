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

import org.drools.persistence.jta.JtaTransactionManagerFactory;
import org.kie.api.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract factory for creating {@link org.drools.persistence.TransactionManager} instances..
 * 
 * The implementation class can be specified using the {@code org.kie.txm.factory.class} system property.
 * 
 */
public abstract class TransactionManagerFactory {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManagerFactory.class);
    private static TransactionManagerFactory INSTANCE;

    static {
       setInstance();
    }

    private static void setInstance() {
        String factoryClassName = System.getProperty("org.kie.txm.factory.class", JtaTransactionManagerFactory.class.getName());
        try {
            TransactionManagerFactory factory = Class.forName(factoryClassName).asSubclass(TransactionManagerFactory.class).newInstance();
            INSTANCE = factory;
            logger.info("Using "+factory);
        } catch (Exception e) {
            logger.error("Unable to instantiate "+factoryClassName, e);
        }
    }

    public static void resetInstance() {
        setInstance();
    }
    
    /**
     * Retrieves the factory for creating {@link TransactionManager}s.
     * 
     * @return
     */
    public static final TransactionManagerFactory get() {
        return INSTANCE;
    }

    public abstract TransactionManager newTransactionManager();
 
    public abstract TransactionManager newTransactionManager(Environment environment);
    
}