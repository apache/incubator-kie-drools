package org.drools.transaction;

public interface TransactionManager {
    public void start();
    public void end();
    public void rollback();
    
}
