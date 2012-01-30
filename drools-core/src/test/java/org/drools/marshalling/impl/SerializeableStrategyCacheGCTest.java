package org.drools.marshalling.impl;

import static junit.framework.Assert.assertTrue;
import static org.drools.marshalling.impl.SerializablePlaceholderResolverStrategyTest.*;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Putting this test into debug will cause it to fail.
 */
public class SerializeableStrategyCacheGCTest implements Serializable { 

    /** Generated serial version UID */
    private static final long serialVersionUID = 5112894963562141093L;
    private transient Logger logger = LoggerFactory.getLogger(SerializeableStrategyCacheGCTest.class);

    private transient SerializablePlaceholderResolverStrategy strategy;
        
    @Test
    public void testCacheAndGarbageCollectionAfterRead() throws Exception { 
        // setup
        strategy = new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);
        Object obj = new SerializeableStrategyCacheGCTest();
        byte [] data = serializeObjectWithoutStrategy(obj);
        
        int originalCacheSize = SerializablePlaceholderResolverStrategy.objectCache.size();
        logger.debug("Cache size: " + originalCacheSize);
       
        // insert object (via read) into cache
        deserializeObjectWithStrategy(strategy, data);
        int cacheSize = SerializablePlaceholderResolverStrategy.objectCache.size(); 
        assertTrue( "Cache size " + cacheSize, cacheSize == originalCacheSize + 1);
        logger.debug("Cache size after read: " + cacheSize);
        
        serializeObjectWithStrategy(strategy, obj);
        cacheSize = SerializablePlaceholderResolverStrategy.objectCache.size(); 
        assertTrue( "Cache size " + cacheSize, cacheSize == originalCacheSize + 1);
        logger.debug("Cache size after write: " + cacheSize);
      
        serializeObjectWithStrategy(strategy, obj);
        cacheSize = SerializablePlaceholderResolverStrategy.objectCache.size(); 
        assertTrue( "Cache size " + cacheSize, cacheSize == originalCacheSize + 1);
        logger.debug("Cache size after second write: " + cacheSize);
        
        deserializeObjectWithStrategy(strategy, data);
        cacheSize = SerializablePlaceholderResolverStrategy.objectCache.size(); 
        assertTrue( "Cache size " + cacheSize, cacheSize == originalCacheSize + 1);
        logger.debug("Cache size after second read: " + cacheSize);
     
        // clear references
        obj = null;
        
        logger.debug("Running garbage collection.");
        System.gc();
        logger.debug("Waiting for garbage collection to finish.");
        Object propVal = null;
        while(propVal == null ) { 
            propVal = System.getProperty(FINALIZE_PROPERTY_NAME);
            Thread.sleep(1000);
        }
        logger.debug("GC done, cleaning cache of size " + SerializablePlaceholderResolverStrategy.objectCache.size() );
        SerializablePlaceholderResolverStrategy.cleanupCache();
        cacheSize = SerializablePlaceholderResolverStrategy.objectCache.size(); 
        assertTrue("Original object not garbage collected or clean up has failed: " + cacheSize + " > " + originalCacheSize, 
                cacheSize <= originalCacheSize);
        verifyThatCacheLocksAreCleared();
    }

    @Test
    public void testCacheAndGarbageCollectionAfterWrite() throws Exception { 
        // setup
        strategy = new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);
        Object obj = new SerializeableStrategyCacheGCTest();
        
        int originalCacheSize = SerializablePlaceholderResolverStrategy.objectCache.size();
        logger.debug("Cache size: " + originalCacheSize);
       
        // Insert object (via write) into cache
        byte [] data = serializeObjectWithStrategy(strategy, obj);
        int cacheSize = SerializablePlaceholderResolverStrategy.objectCache.size(); 
        assertTrue( "Cache size " + cacheSize, cacheSize == originalCacheSize + 1);
        logger.debug("Cache size after write: " + cacheSize);
      
        deserializeObjectWithStrategy(strategy, data);
        cacheSize = SerializablePlaceholderResolverStrategy.objectCache.size(); 
        assertTrue( "Cache size " + cacheSize, cacheSize == originalCacheSize + 1);
        logger.debug("Cache size after read: " + cacheSize);
        
        serializeObjectWithStrategy(strategy, obj);
        cacheSize = SerializablePlaceholderResolverStrategy.objectCache.size(); 
        assertTrue( "Cache size " + cacheSize, cacheSize == originalCacheSize + 1);
        logger.debug("Cache size after second write: " + cacheSize);

        deserializeObjectWithStrategy(strategy, data);
        cacheSize = SerializablePlaceholderResolverStrategy.objectCache.size(); 
        assertTrue( "Cache size " + cacheSize, cacheSize == originalCacheSize + 1);
        logger.debug("Cache size after second read: " + cacheSize);
        
        // clear references
        obj = null;
        
        logger.debug("Running garbage collection.");
        System.gc();
        logger.debug("Waiting for garbage collection to finish.");
        Object propVal = null;
        while(propVal == null ) { 
            propVal = System.getProperty(FINALIZE_PROPERTY_NAME);
            Thread.sleep(1000);
        }
        logger.debug("GC done, cleaning cache of size " + SerializablePlaceholderResolverStrategy.objectCache.size() );
        SerializablePlaceholderResolverStrategy.cleanupCache();
        cacheSize = SerializablePlaceholderResolverStrategy.objectCache.size(); 
        assertTrue("Original object not garbage collected or clean up has failed: " + cacheSize + " > " + originalCacheSize, 
                cacheSize <= originalCacheSize);
        verifyThatCacheLocksAreCleared();
    }
    
    /**
     * The test object
     */
   
    private final static String FINALIZE_PROPERTY_NAME = "finalized";
    protected void finalize() throws Throwable {
        System.setProperty(FINALIZE_PROPERTY_NAME, Boolean.TRUE.toString());
        super.finalize();
    }

    /**
     * ============
     * Locks: helper method
     * ============
     */
    
    private void verifyThatCacheLocksAreCleared() {
        ReentrantReadWriteLock cacheLock = null;
        if( SerializablePlaceholderResolverStrategy.cacheLock instanceof ReentrantReadWriteLock ) { 
            cacheLock = (ReentrantReadWriteLock) SerializablePlaceholderResolverStrategy.cacheLock;
        }
        else { 
            throw new RuntimeException("Unexpected lock type: " 
                    + SerializablePlaceholderResolverStrategy.cacheLock.getClass().getSimpleName() );
        }
        
        // check locks
        int stateInt = cacheLock.getReadLockCount();
        assertTrue("There are still " + stateInt + " read locks!", stateInt == 0);
        stateInt = cacheLock.getWriteHoldCount();
        assertTrue("There are still " + stateInt + " write holds!", stateInt == 0);
        assertTrue("There is still a write lock!", cacheLock.isWriteLocked() == false);
        stateInt = cacheLock.getQueueLength(); 
        assertTrue(stateInt + " threads are still waiting on a lock!", stateInt == 0);
    }
    
}