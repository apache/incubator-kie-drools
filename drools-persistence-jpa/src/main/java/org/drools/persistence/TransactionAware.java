package org.drools.persistence;

public interface TransactionAware {

    void onStart(TransactionManager txm);

    void onEnd(TransactionManager txm);
}
