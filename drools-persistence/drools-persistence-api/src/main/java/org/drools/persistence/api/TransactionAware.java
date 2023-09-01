package org.drools.persistence.api;

public interface TransactionAware {

    void onStart(TransactionManager txm);

    void onEnd(TransactionManager txm);
}
