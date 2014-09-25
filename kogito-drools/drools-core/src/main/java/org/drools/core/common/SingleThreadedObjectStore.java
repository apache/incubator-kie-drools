/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.util.JavaIteratorAdapter;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.HashTableIterator;
import org.drools.core.RuleBaseConfiguration.AssertBehaviour;
import org.kie.api.runtime.rule.FactHandle;

public class  SingleThreadedObjectStore implements Externalizable, ObjectStore {
    /** Object-to-handle mapping. */
    private ObjectHashMap                          assertMap;
    private ObjectHashMap                          negAssertMap;
    private ObjectHashMap                          identityMap;
    private AssertBehaviour                        behaviour;
    private Lock                                   lock;

    public SingleThreadedObjectStore() {

    }

    public SingleThreadedObjectStore(RuleBaseConfiguration conf, Lock lock) {
        this.behaviour = conf.getAssertBehaviour();
        this.lock = lock;

        this.assertMap = new ObjectHashMap();
        this.negAssertMap = new ObjectHashMap();

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
     * @see org.kie.common.ObjectStore#size()
     */
    public int size() {
        return this.assertMap.size();
    }

    /* (non-Javadoc)
     * @see org.kie.common.ObjectStore#isEmpty()
     */
    public boolean isEmpty() {
        return this.assertMap.size() == 0;
    }
    
    public void clear() {
        this.assertMap.clear();
        this.identityMap.clear();
    }

    /* (non-Javadoc)
     * @see org.kie.common.ObjectStore#getObjectForHandle(org.kie.common.InternalFactHandle)
     */
    public Object getObjectForHandle(FactHandle handle) {
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
     * @see org.kie.common.ObjectStore#getHandleForObject(java.lang.Object)
     */
    public InternalFactHandle getHandleForObject(Object object){
        if ( object == null ) {
            return null;
        } else {
            return (InternalFactHandle) this.assertMap.get( object );
        }
    }
    
    public InternalFactHandle reconnect(InternalFactHandle handle) {
        if ( handle.isNegated() ) {
            return (InternalFactHandle) this.negAssertMap.get( handle );
        }   else {
            return (InternalFactHandle) this.assertMap.get( handle );
        }

    }

    /* (non-Javadoc)
     * @see org.kie.common.ObjectStore#getHandleForObject(java.lang.Object)
     */
    public InternalFactHandle getHandleForObjectIdentity(Object object) {
        return (InternalFactHandle) this.identityMap.get( object );
    }

    /* (non-Javadoc)
     * @see org.kie.common.ObjectStore#updateHandle(org.kie.common.InternalFactHandle, java.lang.Object)
     */
    public void updateHandle(InternalFactHandle handle, Object object){
        removeHandle( handle );

        handle.setObject( object );

        addHandle( handle, object );
    }

    /* (non-Javadoc)
     * @see org.kie.common.ObjectStore#addHandle(org.kie.common.InternalFactHandle, java.lang.Object)
     */
    public void addHandle(InternalFactHandle handle, Object object) {
        if ( handle.isNegated() ) {
            negAssertMap.put(handle,
                             handle,
                             false);
        } else {
            this.assertMap.put(handle,
                               handle,
                               false);
        }

        if ( AssertBehaviour.EQUALITY.equals(this.behaviour) ) {
            this.identityMap.put( handle,
                                  handle,
                                  false );
        }
    }

    /* (non-Javadoc)
     * @see org.kie.common.ObjectStore#removeHandle(org.kie.common.InternalFactHandle)
     */
    public void removeHandle(final InternalFactHandle handle) {
        if ( handle.isNegated() ) {
            negAssertMap.remove(handle);
        } else {
            this.assertMap.remove(handle);
        }

        if ( AssertBehaviour.EQUALITY.equals(this.behaviour) ) {
            this.identityMap.remove( handle );
        }
    }

    /* (non-Javadoc)
     * @see org.kie.common.ObjectStore#iterateObjects()
     */
    public Iterator iterateObjects() {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.OBJECT );
    }

    /* (non-Javadoc)
     * @see org.kie.common.ObjectStore#iterateObjects(org.kie.ObjectFilter)
     */
    public Iterator iterateObjects(org.kie.api.runtime.ObjectFilter filter) {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.OBJECT,
                                        filter );
    }

    /* (non-Javadoc)
     * @see org.kie.common.ObjectStore#iterateFactHandles()
     */
    public Iterator iterateFactHandles() {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.FACT_HANDLE );
    }

    /* (non-Javadoc)
     * @see org.kie.common.ObjectStore#iterateFactHandles(org.kie.ObjectFilter)
     */
    public Iterator iterateFactHandles(org.kie.api.runtime.ObjectFilter filter) {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.FACT_HANDLE,
                                        filter );
    }


    public Iterator iterateNegObjects(org.kie.api.runtime.ObjectFilter filter) {
        HashTableIterator iterator = new HashTableIterator( this.negAssertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.OBJECT,
                                        filter );
    }

    public Iterator iterateNegFactHandles(org.kie.api.runtime.ObjectFilter filter) {
        HashTableIterator iterator = new HashTableIterator( this.negAssertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.FACT_HANDLE,
                                        filter );
    }

}
