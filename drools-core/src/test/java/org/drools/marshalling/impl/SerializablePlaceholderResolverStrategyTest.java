package org.drools.marshalling.impl;

import static junit.framework.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy.ObjectWrapper;
import org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy.WeakObjectWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SerializablePlaceholderResolverStrategyTest { 

    private SerializablePlaceholderResolverStrategy strategy;
        
    @Before
    public void before() { 
        strategy = new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);
        SerializablePlaceholderResolverStrategy.objectCache = new HashMap<Object, Object>();
        SerializablePlaceholderResolverStrategy.objectIdHashCodeMap = new HashMap<String, Integer>();
    }
   
    @After
    public void after() { 
        verifyThatCacheLocksAreCleared();
        SerializablePlaceholderResolverStrategy.cacheLock = new ReentrantReadWriteLock();
        strategy = null;
    }
    
    @Test
    public void testRetrieveWithEmptyWrappedObject() { 
        Object factObject = new Object();
        WeakObjectWrapper wrappedObject = new WeakObjectWrapper(factObject);
        ObjectWrapper emptyWrappedObject = new ObjectWrapper();        
        emptyWrappedObject.setHashCode(wrappedObject.hashCode());
       
        HashMap<Object, Object> cache = new HashMap<Object, Object>();
        
        cache.put(wrappedObject, wrappedObject);
        WeakObjectWrapper retrievedWrappedObject = (WeakObjectWrapper) cache.get(emptyWrappedObject);
        Object retrievedFactObject = retrievedWrappedObject.get();
        
        assertTrue(retrievedFactObject == factObject);
    }
    
    @Test
    public void testReadWriteSameJVM() throws Exception {
        Object [] fieldVals = { "Spiderman", 23, true };
        SuperHero obj = new SuperHero(fieldVals);
      
        // write
        byte [] data = serializeObjectWithStrategy(strategy, obj);
       
        // read
        SuperHero objCopy = (SuperHero) deserializeObjectWithStrategy(strategy, data);
        assertTrue(objCopy == obj);
        verifyFieldValuesOfObject(fieldVals, objCopy);
        
        // write
        byte [] copyData = serializeObjectWithStrategy(strategy, objCopy);
        assertThatByteArraysAreEqual(data, copyData);
       
        // read again
        objCopy = (SuperHero) deserializeObjectWithStrategy(strategy, copyData);
        assertTrue(objCopy == obj);
        verifyFieldValuesOfObject(fieldVals, objCopy);
    }

    @Test
    public void testReadTwiceInSameJVM() throws Exception { 
        Object [] fieldVals = { "Superman", 24, true };
        SuperHero obj = new SuperHero(fieldVals);
      
        // setup
        byte [] data = serializeObjectWithoutStrategy(obj);
       
        // read
        SuperHero objCopyA = (SuperHero) deserializeObjectWithStrategy(strategy, data);
        assertTrue(objCopyA != obj); // new object created
        verifyFieldValuesOfObject(fieldVals, objCopyA);
       
        // read again
        SuperHero objCopyB = (SuperHero) deserializeObjectWithStrategy(strategy, data);
        assertTrue(objCopyB == objCopyA); // same object used
        verifyFieldValuesOfObject(fieldVals, objCopyB);
    }
    
    @Test
    public void testReadChangeAndReadAgain() throws Exception { 
        Object [] fieldVals = { "Superman", 24, true };
        SuperHero obj = new SuperHero(fieldVals);
      
        // setup
        byte [] data = serializeObjectWithoutStrategy(obj);
       
        // read
        SuperHero objCopyA = (SuperHero) deserializeObjectWithStrategy(strategy, data);
        assertTrue(objCopyA != obj); // new object created
        verifyFieldValuesOfObject(fieldVals, objCopyA);
   
        // Change
        objCopyA.name = "Bizarro";
        objCopyA.age = 42;
        objCopyA.good = false;
        fieldVals = getFieldValues(objCopyA);
        
        // read again
        SuperHero objCopyB = (SuperHero) deserializeObjectWithStrategy(strategy, data);
        assertTrue(objCopyB == objCopyA); // same object used
        verifyFieldValuesOfObject(fieldVals, objCopyB);
    }

    @Test
    public void testReadChangeWriteAndReadAgain() throws Exception { 
        Object [] fieldVals = { "Superman", 24, true };
        SuperHero obj = new SuperHero(fieldVals);
      
        // setup
        byte [] data = serializeObjectWithoutStrategy(obj);
       
        // read
        SuperHero objCopyA = (SuperHero) deserializeObjectWithStrategy(strategy, data);
        assertTrue(objCopyA != obj); // new object created
        verifyFieldValuesOfObject(fieldVals, objCopyA);
   
        // change
        objCopyA.name = "Bizarro";
        objCopyA.age = 42;
        objCopyA.good = false;
        fieldVals = getFieldValues(objCopyA);
      
        // write
        byte [] newData = serializeObjectWithStrategy(strategy, objCopyA);
        
        // read again
        SuperHero objCopyB = (SuperHero) deserializeObjectWithStrategy(strategy, newData);
        assertTrue(objCopyB == objCopyA); // same object used
        verifyFieldValuesOfObject(fieldVals, objCopyB);
    }
    
    
    @Test
    public void testAnotherThreadReadsBetweenReadAndWriteLockAfterRead() throws Exception { 
        // data setup
        Object [] fieldVals = { "Black Mask", 27, false };
        SuperHero obj = new SuperHero(fieldVals);
        byte [] data = serializeObjectWithoutStrategy(obj);
        
        SerializablePlaceholderResolverStrategy.cacheLock = (ReadWriteLock) WaitingReadWriteLockProxy.newInstance(new ReentrantReadWriteLock());
       
        WrappedObject wrappedObject = new WrappedObject();
        ReadAndWaitTestCase readAndWaitTestCase = new ReadAndWaitTestCase(data, obj, wrappedObject, fieldVals);
        NormalReadTestCase normalTestCase = new NormalReadTestCase(data, obj, wrappedObject, fieldVals);
       
        Thread readAndWaitThread = new Thread(readAndWaitTestCase);
        Thread normalThread = new Thread(normalTestCase);
       
        readAndWaitThread.start();
        // readAndWait is now waiting on the semaphore
        
        normalThread.start();
        // normal has now inserted the object into the shared WrappedObject
       
        int w = 0; // wait max 1 second
        while( normalThread.isAlive() && ++w < 100 ) { 
            Thread.sleep(10);
        }
        if( w == 100 ) { 
            assertTrue("NormalTest thread still alive!", ! normalThread.isAlive());
        }

        synchronized(WaitingReadWriteLockProxy.semaphore) { 
            WaitingReadWriteLockProxy.semaphore.notifyAll();
        }
        // readAndWait finishes it's work (and verifies results)
       
        w = 0; // wait max 1 second
        while( readAndWaitThread.isAlive() && ++w < 100 ) { 
            Thread.sleep(10);
        }
        if( w == 100 ) { 
            assertTrue("ReadAndWait thread still alive!", ! readAndWaitThread.isAlive());
        }
        
        assertTrue("Read and Wait test case failed: " + readAndWaitTestCase.failMessage, 
                readAndWaitTestCase.testFailed != true);
        assertTrue("Normal test case failed: " + normalTestCase.failMessage, normalTestCase.testFailed != true);
    }

    @Test
    public void testAnotherThreadReadsBetweenReadAndWriteLockAfterWrite() throws Exception { 
        // data setup
        Object [] fieldVals = { "Doctor Octopus", 34, false };
        SuperHero obj = new SuperHero(fieldVals);
        byte [] data = serializeObjectWithStrategy(strategy, obj);
        
        SerializablePlaceholderResolverStrategy.cacheLock = (ReadWriteLock) WaitingReadWriteLockProxy.newInstance(new ReentrantReadWriteLock());
        
        WrappedObject wrappedObject = new WrappedObject();
        // original Object == read object
        ReadAndWaitTestCase readAndWaitTestCase = new ReadAndWaitTestCase(data, null, wrappedObject, fieldVals);
        // original object == read object
        NormalReadTestCase normalTestCase = new NormalReadTestCase(data, null, wrappedObject, fieldVals);
       
        Thread readAndWaitThread = new Thread(readAndWaitTestCase);
        Thread normalThread = new Thread(normalTestCase);
      
        readAndWaitThread.start();
        // readAndWait has found object and is now waiting on the semaphore
        normalThread.start();
        // normal has also found object
       
        int w = 0; // wait max 1 second
        while( normalThread.isAlive() && ++w < 100 ) { 
            Thread.sleep(10);
        }
        if( w == 100 ) { 
            assertTrue("NormalTest thread still alive!", ! normalThread.isAlive());
        }
        
        synchronized(WaitingReadWriteLockProxy.semaphore) { 
            WaitingReadWriteLockProxy.semaphore.notifyAll();
        }
        // readAndWait finishes it's work (and verifies results)
       
        w = 0; // wait max 1 second
        while( readAndWaitThread.isAlive() && ++w < 100 ) { 
            Thread.sleep(10);
        }
        if( w == 100 ) { 
            assertTrue("ReadAndWait thread still alive!", ! readAndWaitThread.isAlive());
        }
        
        assertTrue("Read and Wait test case failed: " + readAndWaitTestCase.failMessage, 
                readAndWaitTestCase.testFailed != true);
        assertTrue("Normal test case failed: " + normalTestCase.failMessage, normalTestCase.testFailed != true);
    }

    /**
     * =============
     * Serialize and deserialize: helper methods
     * =============
     */
    
    protected static byte [] serializeObjectWithStrategy(ObjectMarshallingStrategy strategy, Object object) throws Exception { 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        
        strategy.write(oos, object);
        
        oos.close();
        byte [] result = baos.toByteArray();
        baos.close();
        return result;
    }

    protected static byte [] serializeObjectWithoutStrategy(Object object) throws Exception { 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        
        oos.writeObject(new SerializablePlaceholderResolverStrategy.ObjectId(System.identityHashCode(object)));
        oos.writeObject(object);
        
        oos.close();
        byte [] result = baos.toByteArray();
        baos.close();
        return result;
    }

    protected static Object deserializeObjectWithStrategy(ObjectMarshallingStrategy strategy, byte [] data) throws Exception { 
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        
        Object result = strategy.read(ois);
        
        ois.close();
        bais.close();
        return result;
    }

    /**
     * =============
     * Checking values: helper methods 
     * =============
     */
    
    private static void assertThatByteArraysAreEqual(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) {
            fail("byte [] length different: b1=" + b1.length + " b2=" + b2.length);
        }
    
        for (int i = 0, length = b1.length; i < length; i++) {
            if (b1[i] != b2[i]) {
                fail("byte [] content different at byte " + i + ": [" + b1[i] + "] != [" + b2[i] + "]");
            }
        }
    }

    private static void verifyFieldValuesOfObject(Object [] vals, SuperHero superHero) { 
        assertNotNull(superHero);
        assertTrue(vals[0].equals(superHero.name));
        assertTrue(vals[1].equals(superHero.age));
        assertTrue(vals[2].equals(superHero.good));
    }

    /**
     * =============
     * Thread tests: helper objects and methods
     * =============
     */
    
    private class WrappedObject { 
       Object obj; 
    }
    
    private abstract class TestCase implements Runnable { 
        
        public byte [] data;
        public Object originalObject;
        public WrappedObject wrappedCachedObject;
        public Object [] fieldVals;

        boolean testFailed = false;
        String failMessage;
        
        public TestCase(byte [] data, Object orig, WrappedObject wrappedObject, Object [] fieldVals ) { 
            this.data = data;
            this.originalObject = orig;
            this.wrappedCachedObject = wrappedObject;
            this.fieldVals = fieldVals;
        }
        
    }
    
    /**
     * This test class tries the read lock part of the strategy, and then waits.
     */
    private class ReadAndWaitTestCase extends TestCase {

        public ReadAndWaitTestCase(byte[] data, Object orig, WrappedObject wrappedObject, Object[] fieldVals) {
            super(data, orig, wrappedObject, fieldVals);
        }
        
        public void run() {
            try {
                SuperHero objCopy = (SuperHero) deserializeObjectWithStrategy(strategy, data);
                if( objCopy == originalObject ) { testFailed = true; failMessage = "object different from original"; }
                if( wrappedCachedObject.obj == null ) { 
                    // other thread has saved object
                    testFailed = true; 
                    failMessage = "wrapped cached object empty"; 
                 } 
                if(objCopy != wrappedCachedObject.obj) { 
                    // use object from other thread
                    testFailed = true; 
                    failMessage = "object from other thread not the same as retrieved object" ; 
                } 
                if( ! fieldValuesOfObjectAreCorrect(fieldVals, objCopy) ) { testFailed = true; failMessage = "object fields incorrect"; }
            } catch (Exception e) {
                e.printStackTrace();
                fail("Thread crashed due to " + e.getClass().getSimpleName() + " [" + e.getMessage() + "]");
            }
        }
    }
    
    private class NormalReadTestCase extends TestCase { 
       
        public NormalReadTestCase(byte[] data, Object orig, WrappedObject wrappedObject, Object[] fieldVals) {
            super(data, orig, wrappedObject, fieldVals);
        }

        public void run() {
            try {
                SuperHero objCopy = (SuperHero) deserializeObjectWithStrategy(strategy, data);
                if( objCopy == null ) { testFailed = true; failMessage = "null object retrieved";} 
                if( objCopy == originalObject ) { testFailed = true; failMessage = "object same than original"; } 
                if( ! fieldValuesOfObjectAreCorrect(fieldVals, objCopy) ) { testFailed = true; failMessage = "fields different"; }
                wrappedCachedObject.obj = objCopy; // save object for test in other thread
            } catch (Exception e) {
                e.printStackTrace();
                fail("Thread crashed due to " + e.getClass().getSimpleName() + " [" + e.getMessage() + "]");
            }
        }
    }
    
    private static boolean fieldValuesOfObjectAreCorrect(Object [] vals, SuperHero superHero) { 
        if(superHero == null) { return false; }
        if( ! vals[0].equals(superHero.name) ) { return false; }
        if( ! vals[1].equals(superHero.age) ) { return false; }
        if( ! vals[2].equals(superHero.good) ) { return false; }
        return true;
    }
    
    /**
     * The test object
     */
    
    private final static String SUPER_HERO_FINALIZED = "superhero.finalized";

    @SuppressWarnings("serial")
    private static class SuperHero implements Serializable { 
        public String name = "John";
        public int age = 23;
        public boolean good = true;
        
        public SuperHero(String name, Integer age, Boolean good)  { 
            this.name = name;
            this.age = age;
            this.good = good;
        }
        
        public SuperHero(Object [] fieldVals) { 
            this((String) fieldVals[0], (Integer) fieldVals[1], (Boolean) fieldVals[2]);
        }
        
        protected void finalize() throws Throwable { 
            System.setProperty(SUPER_HERO_FINALIZED, Boolean.TRUE.toString());
            super.finalize();
            synchronized(WaitingReadWriteLockProxy.semaphore) { 
                WaitingReadWriteLockProxy.semaphore.notifyAll();
            }
        }
        
    }

    private static Object [] getFieldValues(SuperHero superHero) { 
       Object [] fieldValues = new Object [3];
       fieldValues[0] = superHero.name;
       fieldValues[1] = superHero.age;
       fieldValues[2] = superHero.good;
       return fieldValues;
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
        else if( Proxy.isProxyClass(SerializablePlaceholderResolverStrategy.cacheLock.getClass()) ) { 
            WaitingReadWriteLockProxy handler = (WaitingReadWriteLockProxy) Proxy.getInvocationHandler(SerializablePlaceholderResolverStrategy.cacheLock);
            cacheLock = (ReentrantReadWriteLock) handler.getMasterLock();
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
