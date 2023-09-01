package org.drools.core.time.impl;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

/**
 * Base class for unit tests that wish to verify 
 * backwards compatibily of serialization with earlier versions
 * of Quartz.
 */
public abstract class SerializationTestSupport {

    /**
     * Get the object to serialize when generating serialized file for future
     * tests, and against which to validate deserialized object.
     */
    protected abstract Object getTargetObject() throws Exception;
    
    /**
     * Get the Quartz versions for which we should verify
     * serialization backwards compatibility.
     */
    protected abstract String[] getVersions();
    
    /**
     * Verify that the target object and the object we just deserialized 
     * match.
     */
    protected abstract void verifyMatch(Object target, Object deserialized);
    
    /**
     * Test that we can successfully deserialize our target
     * class for all of the given Quartz versions. 
     */
    @Test
    public void testSerialization() throws Exception {
        Object targetObject = getTargetObject();
        
        for (int i = 0; i < getVersions().length; i++) {
            String version = getVersions()[i];
            
            verifyMatch(
                targetObject,
                deserialize(version, targetObject.getClass()));
        }
    }
    
    /**
     * Deserialize the target object from disk.
     */
    protected Object deserialize(String version, Class clazz) throws Exception {
        InputStream is = getClass().getResourceAsStream(getSerializedFileName(version, clazz));
        
        ObjectInputStream ois = new ObjectInputStream(is);
        
        Object obj = ois.readObject();

        ois.close();
        is.close();

        return obj;
    }
    
    /**
     * Use this method in the future to generate other versions of
     * of the serialized object file.
     */
    public void writeJobDataFile(String version) throws Exception {
        Object obj = getTargetObject();
        
        FileOutputStream fos = new FileOutputStream(getSerializedFileName(version, obj.getClass()));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        
        oos.writeObject(obj);

        oos.flush();
        fos.close();
        oos.close();
    }
    
    /**
     * Generate the expected name of the serialized object file.
     */
    private String getSerializedFileName(String version, Class clazz) {
        String className = clazz.getName();
        int index = className.lastIndexOf(".");
        index = (index < 0) ? 0 : index + 1;
        
        return className.substring(index) + "-" + version + ".ser";
    }
}
