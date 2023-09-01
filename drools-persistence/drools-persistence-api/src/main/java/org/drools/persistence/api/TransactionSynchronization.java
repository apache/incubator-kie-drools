package org.drools.persistence.api;

public interface TransactionSynchronization {
    
    void beforeCompletion();
    
    void afterCompletion(int status);
}
