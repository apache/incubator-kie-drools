package org.drools.persistence;

import org.drools.persistence.info.SessionInfo;

public interface PersistenceContext {

    void persist(Object entity);

    public <T> T find(Class<T> entityClass, 
                      Object primaryKey);

    boolean isOpen();

    void joinTransaction();

    void close();

}
