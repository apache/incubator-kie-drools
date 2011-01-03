package org.drools.persistence.map;

import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.TransactionManager;

public interface EnvironmentBuilder {

    PersistenceContextManager getPersistenceContextManager();

    TransactionManager getTransactionManager();

}