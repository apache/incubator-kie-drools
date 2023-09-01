package org.drools.persistence.api;

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
        String factoryClassName = System.getProperty("org.kie.txm.factory.class", "org.drools.persistence.jta.JtaTransactionManagerFactory");
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
