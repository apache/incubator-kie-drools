package org.drools.marshalling.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.drools.persistence.info.SessionInfo;

public class CacheEntityManager implements EntityManager {

    private HashMap<Long, MarshalledData> cache;
   
    private static String SESSIONINFO_CLASS = "org.drools.persistence.info.SessionInfo";
    private static HashSet<String> supportedClasses  = new HashSet<String>();
    static { 
       supportedClasses.add(SESSIONINFO_CLASS);
    }
    
    public CacheEntityManager(HashMap<Long, MarshalledData> cache) { 
      this.cache = cache;
    }

    
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        T entity = null;
        // OCRAM: processInstance.. and WorkIteminfo.. 
        if( entityClass.getName() != null && entityClass.getName().equals(SessionInfo.class.getName()) ) { 
            Integer id = (Integer) primaryKey;
            // OCRAM: npe check on marshalledData
            MarshalledData marshalledData = cache.get(new Long(id));
            SessionInfo sessionInfo = new SessionInfo();
            sessionInfo.setId(id);
            sessionInfo.setData(marshalledData.rulesByteArray);
            return (T) sessionInfo;
        }
        else { 
            throwUnsupportedOperationException();
        }
        return entity;
    }

    public void persist(Object entity) {
        throwUnsupportedOperationException();
    }

    public <T> T merge(T entity) {
       throwUnsupportedOperationException();
       return null;
    }

    public void remove(Object entity) {
       throwUnsupportedOperationException();
    }

    public void flush() {
       throwUnsupportedOperationException();
    }

    public void setFlushMode(FlushModeType flushMode) {
       throwUnsupportedOperationException();
    }

    public FlushModeType getFlushMode() {
        throwUnsupportedOperationException();
        return null;
    }

    // Queries
    
    // Transaction methods
    
    public void joinTransaction() {
        throwUnsupportedOperationException();
    }

    public EntityTransaction getTransaction() {
        throwUnsupportedOperationException();
        return null;
    }

    public Object getDelegate() {
        throwUnsupportedOperationException();
        return null;
    }

    // lock, find, getReference, refresh, contains
    
    public void lock(Object entity, LockModeType lockMode) {
        throwUnsupportedOperationException();
    }

    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        throwUnsupportedOperationException();
        return null;
    }

    public void refresh(Object entity) {
        throwUnsupportedOperationException();
    }

    public boolean contains(Object entity) {
        throwUnsupportedOperationException();
        return false;
    }

    public Query createQuery(String ejbqlString) {
        throwUnsupportedOperationException();
        return null;
    }

    public Query createNamedQuery(String name) {
        throwUnsupportedOperationException();
        return null;
    }

    public Query createNativeQuery(String sqlString) {
        throwUnsupportedOperationException();
        return null;
    }

    public Query createNativeQuery(String sqlString, Class resultClass) {
        throwUnsupportedOperationException();
        return null;
    }

    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        throwUnsupportedOperationException();
        return null;
    }

     // Transaction methods
    
    public void clear() {
        throwUnsupportedOperationException();
    }

    public void close() {
        throwUnsupportedOperationException();
    }

    public boolean isOpen() {
        throwUnsupportedOperationException();
        return false;
    }

    private void throwUnsupportedOperationException() { 
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        throw new UnsupportedOperationException(methodName + " operation is not supported.");
    }

}
