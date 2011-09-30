package org.drools.marshalling.util;

import static junit.framework.Assert.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Transient;

import junit.framework.TestCase;

import org.drools.core.util.StringUtils;
import org.drools.persistence.jta.JtaTransactionManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MarshallingTestUtil {

    private static Logger logger = LoggerFactory.getLogger(MarshallingTestUtil.class);
   
    private static MessageDigest algorithm = null;
    static { 
       if( algorithm == null ) { 
          try {
              algorithm = MessageDigest.getInstance("SHA-1");
          }
          catch(Exception e) { 
              e.printStackTrace();
          }
       }
    }
    
    public static void compareTestAndBaseMarshallingData(HashMap<String, MarshalledData> testData, 
            HashMap<String, MarshalledData> baseData ) { 
   
        // OCRAM: fill in
    }
    
    public static HashMap<String, MarshalledData> retrieveMarshallingData(EntityManager em) { 
        HashMap<String, MarshalledData> marshalledDataMap = new HashMap<String, MarshalledData>();
        
        JtaTransactionManager txm = new JtaTransactionManager(null, null, null);
        boolean txOwner = txm.begin();
        
        @SuppressWarnings("unchecked")
        List<Object> mdList = em.createQuery("SELECT m FROM MarshalledData m").getResultList();
        for( Object resultObject : mdList ) { 
            MarshalledData marshalledData = (MarshalledData) resultObject;
            if( StringUtils.isEmpty(marshalledData.testMethodName) || marshalledData.snapshotNumber == null ) {
               fail("MarshalledData object does not contain the proper identification information.");
            }
            marshalledDataMap.put(marshalledData.getTestMethodAndSnapshotNum(), marshalledData);
            // DBG delete when ready MarshallingTestUtil.retrieveMarshallingData() out.println
            System.out.println("->  " + marshalledData);
        }
        
        txm.commit(txOwner);
        
        return marshalledDataMap;
    }
    
    private static Class<?> getSTEClass(StackTraceElement ste) { 
        Class<?> steClass = null;
        try { 
            steClass =  Class.forName(ste.getClassName());
        }
        catch( ClassNotFoundException cnfe ) { 
            // do nothing.. 
        }
          
        return steClass; 
    }

    /**
     * Retrieve the name of the actual method running the test, via reflection magic. 
     * @return The method of the (Junit) test running at this moment.
     */
    public static String getTestMethodName() { 
        String testMethodName = null;
        
        StackTraceElement [] ste = Thread.currentThread().getStackTrace();
        // 0: getStackTrace
        // 1: getTestMethodName (this method)
        // 2: this.persist() or this.merge().. etc.
        FINDTESTMETHOD: for( int i = 3; i < ste.length; ++i ) { 
            Class<?> steClass = getSTEClass(ste[i]);
            if( steClass == null ) { 
                continue;
            }
            
            Method [] classMethods = steClass.getMethods();
            String methodName = ste[i].getMethodName();
            for( int m = 0; m < classMethods.length; ++m ) { 
                if( classMethods[m].getName().equals(methodName) ) { 
                   Annotation [] annos = classMethods[m].getAnnotations(); 
                   for( int a = 0; a < annos.length; ++a ) { 
                       if( annos[a] instanceof Test ) { 
                           testMethodName = steClass.getName() + "." + methodName;
                           break FINDTESTMETHOD;
                       }
                   }
                }
            }
        }
        
        for( int i = 0; testMethodName == null && i < ste.length; ++i ) { 
            Class<?> steClass = getSTEClass(ste[i]);
            if( steClass.equals(TestCase.class) && ste[i].getMethodName().equals("runTest") ) { 
                StackTraceElement testMethodSTE = ste[i-5];
                testMethodName = getSTEClass(testMethodSTE).getName() + "." + testMethodSTE.getMethodName();
            }
        }
        
        if( testMethodName == null ) { 
            RuntimeException re = new RuntimeException("Unable to determine test method name");
            re.setStackTrace(Thread.currentThread().getStackTrace());
            throw re;
        }
        return testMethodName;
    }
    
    public static String byteArrayHashCode(byte [] byteArray) { 
        StringBuffer hashCode = new StringBuffer();
        try {
            byte messageDigest[];
            synchronized (algorithm) {
                algorithm.reset();
                algorithm.update(byteArray);
                messageDigest = algorithm.digest();
            }

            for (int i=0;i<messageDigest.length;i++) {
                hashCode.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return hashCode.toString();
    }
}
