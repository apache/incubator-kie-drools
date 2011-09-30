package org.drools.marshalling.util;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.drools.persistence.info.SessionInfo;

public class MarshallingEntityManager implements EntityManager {

    private EntityManager em;
    private ConcurrentHashMap<SessionInfo, byte []> marshalledDataMap = new ConcurrentHashMap<SessionInfo, byte []>();
    private ConcurrentHashMap<String, byte[]> testMethodByteArrayMap = new ConcurrentHashMap<String, byte[]>();
    
    public MarshallingEntityManager(EntityManager em) { 
        this.em = em;
    }
    
    /**
     * persist.. magic!
     */
    public void persist(Object entity) {
        em.persist(entity);
        if( entity instanceof SessionInfo ) { 
           SessionInfo sessionInfo = (SessionInfo) entity;
           marshalledDataMap.put(sessionInfo, sessionInfo.getData().clone());
           MarshalledData marshalledData = new MarshalledData(sessionInfo);
           em.persist(marshalledData);
           System.out.println("-.-: " + marshalledData);
        }
    }

    /**
     * merge.. magic!
     */
    public <T> T merge(T entity) {
        T updatedEntity = em.merge(entity);
        if( entity instanceof SessionInfo ) { 
            // do stuff???
            // - if updatedEntity.rulesByteArray != entity.rulesByteArray
            // -> then.. ?
            //     ? save a new MarshalledData object
           MarshalledData marshalledData = new MarshalledData((SessionInfo) entity);
           System.out.println("- MERGE: " + marshalledData);
        }
        return updatedEntity;
    }



    /**
     * merge.. magic!
     */
    public void remove(Object entity) {
        em.remove(entity);
    }

    // OCRAM: MarshallingEntityManager comments: lock, find, getReference, refresh, contains
    
    /**
     * {@inheritDoc}
     */
    public void flush() {
        em.flush();
    }

    /**
     * {@inheritDoc}
     */
    public void setFlushMode(FlushModeType flushMode) {
        em.setFlushMode(flushMode);
    }

    /**
     * {@inheritDoc}
     */
    public FlushModeType getFlushMode() {
        return em.getFlushMode();
    }

    // Queries
    
    // Transaction methods
    
    /**
     * {@inheritDoc}
     */
    public void joinTransaction() {
        em.joinTransaction();
        for( SessionInfo sessionInfo : marshalledDataMap.keySet()) { 
           byte [] origMarshalledBytes = marshalledDataMap.get(sessionInfo); 
           boolean newMarshalledData = ! Arrays.equals(origMarshalledBytes, sessionInfo.getData());

           byte [] lastMarshalledData = testMethodByteArrayMap.get(MarshallingTestUtil.getTestMethodName());
           if( lastMarshalledData != null && Arrays.equals(lastMarshalledData, sessionInfo.getData()) ) {
              newMarshalledData = false; 
           }
           if( newMarshalledData ) { 
               MarshalledData marshalledData = new MarshalledData(sessionInfo);
               em.persist(marshalledData);
               testMethodByteArrayMap.put(marshalledData.testMethodName, marshalledData.rulesByteArray);
               System.out.println("-!-: " + marshalledData);
           }
        }
    }

    /**
     * {@inheritDoc}
     */
    public EntityTransaction getTransaction() {
        return em.getTransaction();
    }

    /**
     * {@inheritDoc}
     */
    public Object getDelegate() {
        return em.getDelegate();
    }

    // lock, find, getReference, refresh, contains
    
    /**
     * {@inheritDoc}
     */
    public void lock(Object entity, LockModeType lockMode) {
        em.lock(entity, lockMode);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return em.find(entityClass, primaryKey);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return em.getReference(entityClass, primaryKey);
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(Object entity) {
        em.refresh(entity);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Object entity) {
        return em.contains(entity);
    }

    /**
     * {@inheritDoc}
     */
    public Query createQuery(String ejbqlString) {
        return em.createQuery(ejbqlString);
    }

    /**
     * {@inheritDoc}
     */
    public Query createNamedQuery(String name) {
        return em.createNamedQuery(name);
    }

    /**
     * {@inheritDoc}
     */
    public Query createNativeQuery(String sqlString) {
        return em.createNativeQuery(sqlString);
    }

    /**
     * {@inheritDoc}
     */
    public Query createNativeQuery(String sqlString, Class resultClass) {
        return em.createNativeQuery(sqlString, resultClass);
    }

    /**
     * {@inheritDoc}
     */
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return em.createNativeQuery(sqlString, resultSetMapping);
    }

     // Transaction methods
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        em.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        em.close();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isOpen() {
        return em.isOpen();
    }

}
