package org.drools.marshalling.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import junit.framework.TestCase;

import org.drools.persistence.info.SessionInfo;
import org.junit.Test;

public class MarshallingEntityManager implements EntityManager {

    private EntityManager em;
    public MarshallingEntityManager(EntityManager em) { 
        this.em = em;
    }
    
    /**
     * persist.. magic!
     */
    public void persist(Object entity) {
        em.persist(entity);
        if( entity instanceof SessionInfo ) { 
           MarshalledData marshalledData = new MarshalledData((SessionInfo) entity);
           marshalledData.testMethodName = getTestMethodName();
           em.persist(marshalledData);
           marshalledData.toString();
        }
    }

    /**
     * merge.. magic!
     */
    public <T> T merge(T entity) {
        T updatedEntity = em.merge(entity);
        if( entity instanceof SessionInfo ) { 
           MarshalledData marshalledData = new MarshalledData((SessionInfo) entity);
        }
        return updatedEntity;
    }

    /**
     * Retrieve the name of the actual method running the test, via reflection magic. 
     * @return The method of the (Junit) test running at this moment.
     */
    private static String getTestMethodName() { 
        String testMethodName = null;
        
        StackTraceElement [] ste = Thread.currentThread().getStackTrace();
        // 0: getStackTrace
        // 1: getTestMethodName (this method)
        // 2: this.persist() or this.merge().. etc.
        FINDTESTMETHOD: for( int i = 3; i < ste.length; ++i ) { 
            Class testClass = getSTEClass(ste[i]);
            if( testClass == null ) { 
                RuntimeException re = new RuntimeException("Unable to determine test method name");
                re.setStackTrace(Thread.currentThread().getStackTrace());
                throw re;
            }
            
            Method [] classMethods = testClass.getMethods();
            String methodName = ste[i].getMethodName();
            for( int m = 0; m < classMethods.length; ++m ) { 
                if( classMethods[m].getName().equals(methodName) ) { 
                   Annotation [] annos = classMethods[m].getAnnotations(); 
                   for( int a = 0; a < annos.length; ++a ) { 
                       if( annos[a] instanceof Test ) { 
                           testMethodName = testClass.getName() + "." + methodName;
                           break FINDTESTMETHOD;
                       }
                   }
                }
            }
        }
        
        for( int i = 0; testMethodName == null && i < ste.length; ++i ) { 
            Class steClass = getSTEClass(ste[i]);
            if( steClass.equals(TestCase.class) && ste[i].getMethodName().equals("runTest") ) { 
                StackTraceElement testMethodSTE = ste[i-5];
                testMethodName = getSTEClass(testMethodSTE).getName() + "." + testMethodSTE.getMethodName();
            }
        }
        
        return testMethodName;
    }

    private static Class getSTEClass(StackTraceElement ste) { 
        Class steClass = null;
        try { 
            steClass =  Class.forName(ste.getClassName());
        }
        catch( ClassNotFoundException cnfe ) { 
            // do nothing.. 
        }
            
        return steClass; 
    }

    /**
     * merge.. magic!
     */
    public void remove(Object entity) {
        em.remove(entity);
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

    // flush 
    
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
    public void joinTransaction() {
        em.joinTransaction();
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

     // clear, open, close..
    
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
