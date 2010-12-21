package org.drools.persistence;

public interface TransactionablePersistentContext {

    boolean isOpen();

    void joinTransaction();

    void close();
}
