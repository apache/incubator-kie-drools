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

package org.drools.marshalling.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Joe Walnes included as author because of the use of idea's and code found here: 
 * - http://svn.codehaus.org/xstream/trunk/xstream/src/java/com/thoughtworks/xstream/core/util/ObjectIdDictionary.java 
 * 
 * @author Joe Walnes
 * @author Drools/jBPM
 */
public class SerializablePlaceholderResolverStrategy
    implements
    ObjectMarshallingStrategy {

    
    private ObjectMarshallingStrategyAcceptor acceptor;

    private Logger logger = LoggerFactory.getLogger(SerializablePlaceholderResolverStrategy.class);
    
    // We use the concurrentHashMap as a "ConcurrentHashSet". 
    protected static HashMap<Object, Object> objectCache = new HashMap<Object, Object>();
    protected static HashSet<String> writtenObjectHashCodeSet = new HashSet<String>();
    // The Integer and Long constant pools mostly only cover [-127,128] -- so we use the String Constant Pool
    protected static HashMap<String, Integer> objectIdHashCodeMap = new HashMap<String, Integer>();
    
    // Not final so that we can do test things
    // The lock is static because the locked _objects_ (above) are also static!
    protected static ReadWriteLock cacheLock = new ReentrantReadWriteLock(true);
    
    public SerializablePlaceholderResolverStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        this.acceptor = acceptor;
    }
    
    /**
     * There are two situations that we must take into account here.</p>
     * We are reading a data stream that was written:<ol>
     * <li>before this JVM started
     * <ul><li>which means that the retrieved object id references an identity hashcode that doesn't map to this JVM</li></ul></li>
     * <li><i>by this</i> JVM and thus references an identity hashcode of an object in <i>this</i> JVM
     *     </br>&nbsp;</li>
     * </ol>
     * 
     * </p>
     * The following applies to the description of the algorithm below: <ul>
     * <li>The algorithm is optimized to try read locks first before resorting to write locks</li>
     * <li>"Mapping" refers to a mapping in the objectIdHashCode map between an object id -> (object system identiy) hash code</li>
     * </ul>
     * 
     * </p>
     * 
     * <b>Algorithm</b>
     * </p>
     * Check first with a read lock:</p>
     * <ol>
     * <li>If there is a mapping (object Id -> hashCode), retrieve the wrapped object from the cache
     * <ol><li>If the wrapped object is not null, return the object wrapped</li>
     *     <li>Otherwise, retry this with a write lock (checkWithWriteLock = true)</li></ol></li>
     * <li>If there is no mapping, but 1. the written object hashcode set contains the object id and
     *     2. the object cache contains a wrapped object for the objectId 
     * <ol><li>then use the wrapped object
     *     </p>[<i>this</i> JVM has written the object]</li></ol>
     * </li>
     * </ol>
     * If neither of the above clauses succeeded, release the read lock and acquire a write lock:</p>
     * <ol>
     * <li>If there is a mapping (object Id -> hashCode),  retrieve the wrapped object from the cache
     * <ol><li>If the wrapped object is not null, return the object wrapped</li>
     *     <li>Otherwise, insert the read fact object into the cache and return the read fact object</li></ol></li>
     * <li>If there is no mapping
     * <ol><li>add the mapping and retrieve the (expectedly null) wrapped object to the cache</li>
     *     <li>If the object does not exist in the cache as expected, add the object to the cache</li>
     *     <li>If the object already (somehow?) exists in the cache, warn and use the read object</li></ol></li>
     * </ol>
     */
    public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException {
        Object firstObject = os.readObject();
        if( firstObject instanceof ObjectId ) { 
            // read the next, and real object
            Object factObject = os.readObject();
            ObjectId objectId = (ObjectId) firstObject;

            // Operations that can be done outside of the lock
            ObjectWrapper emptyWrappedObject = new ObjectWrapper();
            String objectIdString = String.valueOf(objectId.id).intern();
            boolean checkWithWriteLock = true;
            
            // Try checking the cache with a read lock first..
            cacheLock.readLock().lock();
            Integer mappedHashCode = objectIdHashCodeMap.get(objectIdString);
            if( mappedHashCode != null ) { 
            // read lock: Mapping found for the object, retrieve object from cache
                checkWithWriteLock = false;
                emptyWrappedObject.setHashCode(mappedHashCode);
                WeakObjectWrapper mappedWrappedObject = (WeakObjectWrapper) objectCache.get(emptyWrappedObject);
                cacheLock.readLock().unlock();
                if( mappedWrappedObject != null ) { 
                    // Double check that garbage collection hasn't done something weird
                    factObject = (mappedWrappedObject.get() != null ? mappedWrappedObject.get() : factObject );
                }
                else { 
                    logger.error("IMPOSSIBLE situation occurred: object not present in cache " +
                            "for object Id [{}] -> hashcode [{}] mapping of class " + factObject.getClass().getName(),
                            objectIdString, mappedHashCode );
                    checkWithWriteLock = true;
                    // use the factObject read from the stream
                }
            }
            else { 
            // read lock: no mapping found
                boolean objectWrittenByThisJVM = writtenObjectHashCodeSet.contains(objectIdString); 
                emptyWrappedObject.setHashCode(objectId.id); 
                WeakObjectWrapper objectIdWrappedObject = (WeakObjectWrapper) objectCache.get(emptyWrappedObject);
                cacheLock.readLock().unlock();
                if( objectWrittenByThisJVM && objectIdWrappedObject != null ) { 
                // read lock: the object was written by this JVM and exists in the cache 
                    factObject = (objectIdWrappedObject.get() != null ? objectIdWrappedObject.get() : factObject );
                    checkWithWriteLock = false;
                }
            }
            
            if( checkWithWriteLock ) { 
            // recheck with write lock and if still not there, proceed
                cacheLock.writeLock().lock(); 
                mappedHashCode = objectIdHashCodeMap.get(objectIdString);
                if( mappedHashCode != null ) { 
                // write lock: Between read.unlock() and write.lock(), another thread read the same object    
                    emptyWrappedObject.setHashCode(mappedHashCode);
                    WeakObjectWrapper mappedWrappedObject = (WeakObjectWrapper) objectCache.get(emptyWrappedObject);
                    if( mappedWrappedObject != null ) { 
                        cacheLock.writeLock().unlock();
                        // Double check that garbage collection hasn't done something weird
                        factObject = (mappedWrappedObject.get() != null ? mappedWrappedObject.get() : factObject );
                    }
                    else { 
                        WeakObjectWrapper wrappedObject = new WeakObjectWrapper(factObject);
                        objectCache.put(wrappedObject, wrappedObject);
                        cacheLock.writeLock().unlock();
                        logger.error("IMPOSSIBLE situation occurred: object not present in cache " +
                                "for object Id [{}] -> hashcode [{}] mapping of class " + factObject.getClass().getName(),
                                objectIdString, mappedHashCode );
                        // use the factObject read from the stream
                    }
                }
                else { 
                // write lock: no mapping present for object -> The object was written by a _different_ JVM 
                    // --> this thread is the first to insert the mapping
                    WeakObjectWrapper wrappedObject = new WeakObjectWrapper(factObject);
                    objectIdHashCodeMap.put(objectIdString, wrappedObject.hashCode());
                    WeakObjectWrapper cachedWrappedObject = (WeakObjectWrapper) objectCache.put(wrappedObject, wrappedObject);
                    cacheLock.writeLock().unlock();
                    if( cachedWrappedObject != null ) { 
                        /** 
                         * If the above is true, then the object inserted DID overwrite mapping, which is weird: 
                         * -> If this happens, it means that _another_ thread inserted an object into the cache
                         *    with the _SAME_ (system identity) hashcode as the object that we have _just_ created!
                         *    This is the "hashcode not unique" situation.
                         *    
                         * In this case, we do NOT use the cache object, but use the object read.
                         * We can trust the content (but not integrity) of the object read,
                         * but we can _not_ trust the content or integrity of the cached object.
                         * 
                         * Lastly, the cache has been updated with the new object, 
                         * so this should not occur again for this hashcode
                         */
                        logger.error("IMPOSSIBLE situation occurred: object present in cache WITHOUT mapping " +
                                     "for object Id [{}] -> hashcode [{}] mapping of class " + factObject.getClass().getName(),
                                     objectIdString, mappedHashCode );
                        // use the factObject read from the stream
                    } // if objectCache.put (no mapping) returns an object
                } // else check mapping with write lock had no results
            } // else check mapping with readlock had no results

            return factObject;
        }
        else { 
            // backwards compatibility (if this replaces the serializable placeholder resolver)
            return firstObject;
        }
    }

    /**
     * This method does 2 things: <ul>
     * <li>It serializes the object and inserts it into the stream (via .writeObject(...)).</li>
     * <li>It inserts the object into the cache maintained by this strategy</li>
     * </ul>
     * 
     * @param os The stream with which to write the object and associated information.
     * @param object The (fact or parameter) object to serialize
     */
    public void write(ObjectOutputStream os, Object object) throws IOException {
        WeakObjectWrapper wrappedObject = new WeakObjectWrapper(object);
        cacheLock.writeLock().lock();
        // If the hashcode has already been added, then one of two things has happened: 
        // 1. another thread has inserted the same object (wrapped) into the cache
        // 2. a (system identity) hashcode clash has occurred: the chances of this are _VERY_ small and we can't do anything about it.
        if( ! objectIdHashCodeMap.containsKey(String.valueOf(wrappedObject.hashCode()).intern()) ) { 
            // not a read object
            if(  writtenObjectHashCodeSet.add(String.valueOf(wrappedObject.hashCode).intern()) ) { 
                objectCache.put(wrappedObject, wrappedObject);
            }
        }
        cacheLock.writeLock().unlock();
       
        os.writeObject(new ObjectId(wrappedObject.hashCode()));
        os.writeObject(object);
    }

    public boolean accept(Object object) {
        return acceptor.accept( object );
    }

    /**
     * This method ensures that (weak) references to garbage collected objects are removed 
     *  once the objects have been garbage collected
     */
    public static void cleanupCache() {
        HashMap<Integer, String> hashCodeObjectIdMap = new HashMap<Integer, String>();
        WeakObjectWrapper wrappedObject;
        cacheLock.writeLock().lock();
        for( String id : objectIdHashCodeMap.keySet() ) { 
            hashCodeObjectIdMap.put(objectIdHashCodeMap.get(id), id);
        }
        while ((wrappedObject = (WeakObjectWrapper) queue.poll()) != null) {
            writtenObjectHashCodeSet.remove(wrappedObject.hashCode());
            objectIdHashCodeMap.remove( hashCodeObjectIdMap.get(wrappedObject.hashCode()) );
            objectCache.remove(wrappedObject);
        }
        cacheLock.writeLock().unlock();
    }

    /**
     * Helper classes: serialization
     */
    @SuppressWarnings("serial")
    protected static class ObjectId implements Serializable { 
        protected int id;
        
        protected ObjectId(int idValue) { 
            this.id = idValue;
        }
    }

    
    /**
     * Helper classes: caching
     */
    @SuppressWarnings("rawtypes")
    private static final ReferenceQueue queue = new ReferenceQueue();
    
    protected static interface Wrapper {
        int hashCode();
        boolean equals(Object obj);
        String toString();
        Object get();
    }

   
    @SuppressWarnings("rawtypes")
    protected static class WeakObjectWrapper extends WeakReference implements Wrapper {

        private final int hashCode;

        @SuppressWarnings("unchecked")
        public WeakObjectWrapper(Object obj) {
            super(obj, queue);
            hashCode = System.identityHashCode(obj);
        }

        public int hashCode() {
            return hashCode;
        }

        public boolean equals(Object other) {
            return this.hashCode == ((Wrapper)other).hashCode();
        }

        public String toString() {
            Object obj = get();
            return obj == null ? "(null)" : obj.toString();
        }

        public Object get() {
            return super.get();
        }
    }
    
    protected static class ObjectWrapper implements Wrapper {

        private final Object obj;
        private int hashCode;

        public ObjectWrapper() { 
            obj = null;
            hashCode = -1;
        }
        
        @SuppressWarnings({ "unused" })
        public ObjectWrapper(Object obj) {
            hashCode = System.identityHashCode(obj);
            this.obj = obj;
        }

        public void setHashCode(int hashCode) {
            this.hashCode = hashCode;
        }

        public int hashCode() {
            return hashCode;
        }

        public boolean equals(Object other) {
            return this.hashCode == ((Wrapper)other).hashCode();
        }

        public String toString() {
            Object obj = get();
            return obj == null ? "(null)" : obj.toString();
        }

        public Object get() {
            return obj;
        }
    }
    
}
