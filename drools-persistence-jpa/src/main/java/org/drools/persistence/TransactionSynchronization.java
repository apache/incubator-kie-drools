package org.drools.persistence;

public interface TransactionSynchronization {    
    
    void beforeCompletion();
    
    void afterCompletion(int status);
}
