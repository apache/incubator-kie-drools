package org.drools.persistence;

import javax.transaction.xa.XAResource;

public interface Persister<T> {

    XAResource getXAResource();

    Transaction getTransaction();

    void save();

    void load();
    
    String getUniqueId();
    
    void setUniqueId(String id);
    
    T getObject();
    
}