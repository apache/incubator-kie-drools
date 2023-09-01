package org.drools.persistence.map;

import org.drools.persistence.api.PersistenceContextManager;
import org.drools.persistence.api.TransactionManager;

public interface EnvironmentBuilder {

    PersistenceContextManager getPersistenceContextManager();

    TransactionManager getTransactionManager();

}
