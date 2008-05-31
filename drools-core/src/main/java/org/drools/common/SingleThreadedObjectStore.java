/**
 *
 */
package org.drools.common;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;

import org.drools.ObjectFilter;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.base.ShadowProxy;
import org.drools.util.JavaIteratorAdapter;
import org.drools.util.ObjectHashMap;
import org.drools.util.AbstractHashTable.HashTableIterator;

public class  SingleThreadedObjectStore implements Externalizable, ObjectStore {
    /** Object-to-handle mapping. */
    private ObjectHashMap                          assertMap;
    private ObjectHashMap                          identityMap;
    private AssertBehaviour                        behaviour;
    private Lock                                   lock;

    public SingleThreadedObjectStore() {

    }

    public SingleThreadedObjectStore(RuleBaseConfiguration conf, Lock lock) {
        this.behaviour = conf.getAssertBehaviour();
        this.lock = lock;

        this.assertMap = new ObjectHashMap();

        if ( AssertBehaviour.IDENTITY.equals(this.behaviour) ) {
            this.assertMap.setComparator( new IdentityAssertMapComparator() );
            this.identityMap = assertMap;
        } else {
            this.assertMap.setComparator( new EqualityAssertMapComparator() );
            this.identityMap = new ObjectHashMap();
            this.identityMap.setComparator( new IdentityAssertMapComparator() );
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        assertMap   = (ObjectHashMap)in.readObject();
        identityMap   = (ObjectHashMap)in.readObject();
        behaviour   = (AssertBehaviour)in.readObject();
        lock   = (Lock)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(assertMap);
        out.writeObject(identityMap);
        out.writeObject(behaviour);
        out.writeObject(lock);
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
        handle.setObject( object );
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
        if ( AssertBehaviour.EQUALITY.equals(this.behaviour) ) {
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
        if ( AssertBehaviour.EQUALITY.equals(this.behaviour) ) {
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