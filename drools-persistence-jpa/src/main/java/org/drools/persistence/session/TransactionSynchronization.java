package org.drools.persistence.session;

public interface TransactionSynchronization {    
    
    void beforeCompletion();
    
    void afterCompletion(int status);
}
