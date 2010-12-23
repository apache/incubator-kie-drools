package org.drools.persistence;

public interface PersistenceContext {

    void persist(Object entity);

    public <T> T find(Class<T> entityClass, 
                      Object primaryKey);

    boolean isOpen();

    void joinTransaction();

    void close();

}
