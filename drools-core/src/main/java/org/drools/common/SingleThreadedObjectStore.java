/**
 * 
 */
package org.drools.common;

import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;

import org.drools.ObjectFilter;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.base.ShadowProxy;
import org.drools.util.JavaIteratorAdapter;
import org.drools.util.ObjectHashMap;
import org.drools.util.AbstractHashTable.HashTableIterator;

public class  SingleThreadedObjectStore implements Serializable, ObjectStore {
    /** Object-to-handle mapping. */
    private ObjectHashMap                          assertMap;
    private ObjectHashMap                          identityMap;
    private AssertBehaviour                        behaviour;
    private Lock                                   lock;
    
    public SingleThreadedObjectStore(RuleBaseConfiguration conf, Lock lock) {
        this.behaviour = conf.getAssertBehaviour();
        this.lock = lock;
        
        this.assertMap = new ObjectHashMap();            

        if ( this.behaviour == AssertBehaviour.IDENTITY ) {
            this.assertMap.setComparator( new IdentityAssertMapComparator() );
            this.identityMap = assertMap;
        } else {
            this.assertMap.setComparator( new EqualityAssertMapComparator() );
            this.identityMap = new ObjectHashMap();
            this.identityMap.setComparator( new IdentityAssertMapComparator() );
        }            
    }
    
    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#size()
     */
    public int size() {
        return this.assertMap.size();
    }
    
    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#isEmpty()
     */
    public boolean isEmpty() {
        return this.assertMap != null;
    }
    
    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#getObjectForHandle(org.drools.common.InternalFactHandle)
     */
    public Object getObjectForHandle(InternalFactHandle handle) {
        try {
            this.lock.lock();

            // Make sure the FactHandle is from this WorkingMemory
            final InternalFactHandle internalHandle = (InternalFactHandle) this.assertMap.get( handle );
            if ( internalHandle == null ) {
                return null;
            }

            Object object = internalHandle.getObject();

            if ( object != null && internalHandle.isShadowFact() ) {
                object = ((ShadowProxy) object).getShadowedObject();
            }

            return object;
        } finally {
            this.lock.unlock();
        }            
    }
            
    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#getHandleForObject(java.lang.Object)
     */
    public InternalFactHandle getHandleForObject(Object object){
        return (InternalFactHandle) this.assertMap.get( object );
    }
    
    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#getHandleForObject(java.lang.Object)
     */
    public InternalFactHandle getHandleForObjectIdentity(Object object) {
        return (InternalFactHandle) this.identityMap.get( object );
    }
    
    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#updateHandle(org.drools.common.InternalFactHandle, java.lang.Object)
     */
    public void updateHandle(InternalFactHandle handle, Object object){
        this.assertMap.remove( handle );
        Object oldObject = handle.getObject();
        if ( oldObject instanceof ShadowProxy ) {
            ((ShadowProxy) oldObject).setShadowedObject( object );
        } else {
            handle.setObject( object );
        }
        this.assertMap.put( handle,
                            handle,
                            false );
    }
    
    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#addHandle(org.drools.common.InternalFactHandle, java.lang.Object)
     */
    public void addHandle(InternalFactHandle handle, Object object) {
        this.assertMap.put( handle,
                            handle,
                            false );
        if ( this.behaviour == AssertBehaviour.EQUALITY ) {
            this.identityMap.put( handle,
                                  handle,
                                  false );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#removeHandle(org.drools.common.InternalFactHandle)
     */
    public void removeHandle(final InternalFactHandle handle) {
        this.assertMap.remove( handle );
        if ( this.behaviour == AssertBehaviour.EQUALITY ) {
            this.identityMap.remove( handle );
        }
    }      
    
    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#iterateObjects()
     */
    public Iterator iterateObjects() {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.OBJECT );
    }

    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#iterateObjects(org.drools.ObjectFilter)
     */
    public Iterator iterateObjects(ObjectFilter filter) {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.OBJECT,
                                        filter );
    }

    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#iterateFactHandles()
     */
    public Iterator iterateFactHandles() {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.FACT_HANDLE );
    }

    /* (non-Javadoc)
     * @see org.drools.common.ObjectStore#iterateFactHandles(org.drools.ObjectFilter)
     */
    public Iterator iterateFactHandles(ObjectFilter filter) {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.FACT_HANDLE,
                                        filter );
    }        
    
}