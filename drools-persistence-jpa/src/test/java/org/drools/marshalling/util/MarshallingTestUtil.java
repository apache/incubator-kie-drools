package org.drools.marshalling.util;

import static org.drools.persistence.util.PersistenceUtil.*;
import static org.drools.marshalling.util.MarshallingDBUtil.initializeMarshalledDataEMF;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.base.MapGlobalResolver;
import org.drools.core.util.StringUtils;
import org.drools.impl.EnvironmentFactory;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.TransactionManagerServices;


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

    public static void compareMarshallingDataFromTest(Class testClass, String persistenceUnitName) { 
        // Retrieve the test data
        List<MarshalledData> testDataList = null;
        HashMap<String, Object> testContext = initializeMarshalledDataEMF(persistenceUnitName, testClass, false);
        try { 
            EntityManagerFactory testEMF = (EntityManagerFactory) testContext.get(ENTITY_MANAGER_FACTORY);
            testDataList  = retrieveMarshallingData(testEMF);
        }
        finally { 
           tearDown(testContext); 
        }
        assertNotNull("Not marshalled data found for " + testClass.getSimpleName(), 
                testDataList != null && ! testDataList.isEmpty() );
    
        // Retrieve the base data
        HashMap<String, Object> baseContext = initializeMarshalledDataEMF(persistenceUnitName, testClass, true);
        List<MarshalledData> baseDataList =  null;
        try { 
            EntityManagerFactory baseEMF = (EntityManagerFactory) baseContext.get(ENTITY_MANAGER_FACTORY);
            baseDataList = retrieveMarshallingData(baseEMF);
        }
        finally {
            tearDown(baseContext);
        }
        assertTrue("Not base marshalled data found", baseDataList != null && ! baseDataList.isEmpty() );
        
        // Compare!
        compareTestAndBaseMarshallingData(testClass, testDataList, baseDataList);
    }

    protected static ArrayList<MarshalledData> retrieveMarshallingData(EntityManagerFactory emf) { 
        ArrayList<MarshalledData> marshalledDataList = new ArrayList<MarshalledData>();
        
        JtaTransactionManager txm = new JtaTransactionManager(null, null, null);
        boolean txOwner = txm.begin();
    
        EntityManager em = emf.createEntityManager();
        @SuppressWarnings("unchecked")
        List<Object> mdList = em.createQuery("SELECT m FROM MarshalledData m").getResultList();
        for( Object resultObject : mdList ) { 
            MarshalledData marshalledData = (MarshalledData) resultObject;
            if( StringUtils.isEmpty(marshalledData.testMethodName) || marshalledData.snapshotNumber == null ) {
               fail("MarshalledData object does not contain the proper identification information.");
            }
            marshalledDataList.add(marshalledData);
            // DBG delete when ready MarshallingTestUtil.retrieveMarshallingData() out.println
            logger.trace("> " + marshalledData);
        }
        
        txm.commit(txOwner);
        
        return marshalledDataList;
    }

    private static void compareTestAndBaseMarshallingData(Class<?> testClass, List<MarshalledData> testData, 
            List<MarshalledData> baseData ) { 
    
        sanityCheckMarshalledData(testClass, testData, baseData);
        
        HashMap<String, MarshalledData> testMarshalledDataSnapshotMap = new HashMap<String, MarshalledData>();
        HashMap<String, MarshalledData> baseMarshalledDataSnapshotMap = new HashMap<String, MarshalledData>();
    
        for( MarshalledData testMarshalledData : testData ) { 
           testMarshalledDataSnapshotMap.put(testMarshalledData.getTestMethodAndSnapshotNum(), testMarshalledData);
        }
        for( MarshalledData baseMarshalledData : baseData ) { 
            //OCRAM: limit to this test class
           baseMarshalledDataSnapshotMap.put(baseMarshalledData.getTestMethodAndSnapshotNum(), baseMarshalledData);
        }
        
        for( String testMethodVer : testMarshalledDataSnapshotMap.keySet() ) { 
            logger.info("Comparing marshalled info for " + testMethodVer);
            StatefulKnowledgeSession baseKSession = null;
            try { 
                // Base 
                MarshalledData baseMarshalledData = baseMarshalledDataSnapshotMap.get(testMethodVer);
                baseKSession = unmarshallSession(baseMarshalledData.rulesByteArray);
            }
            catch( Exception e) {
                fail("Unable to unmarshall base data [" + testMethodVer +  "]: " + e.getClass().getSimpleName() + ": " + e.getMessage() + "]");
            }
           
            StatefulKnowledgeSession testKSession = null;
            try { 
                // Test
                MarshalledData testMarshalledData = testMarshalledDataSnapshotMap.get(testMethodVer);
                testKSession = unmarshallSession(testMarshalledData.rulesByteArray);
            }
            catch( Exception e) {
                fail("Unable to unmarshall test data: [" + e.getClass().getSimpleName() + ": " + e.getMessage() + "]");
            }
            
            assertNotNull("Unmarshalled test data resulted in null object!", testKSession);
            assertNotNull("Unmarshalled base data resulted in null object!", baseKSession);
            
            assertTrue( "Unmarshalled " + baseKSession.getClass().getSimpleName() + " objects are not equal.", 
                    CompareViaReflectionUtil.compareInstances(baseKSession, testKSession) );
        }
        
        // x eerst, selecteer een test method
        // x pak alle snapshots from test voor die test method
        // x pak alle snapshots from base voor die test method
        // x bevestig dat je hetzelfde aantal heb
            // x zo niet.. waarschuw, of faal!
        // een voor een 
        // - pak de snapshot van _test
        //   + gebruik inputMarshaller om iets te maken
        //   + bewaar de staat (xml/dump tree-achtig??)
        //   + reflection, anders worden toekomstige veranderingen niet gezien
        // - pak de snapshot van base
        //   + gebruik inputMarshaller om iets te maken
        //   + bewaar de staat (xml/dump tree-achtig??)
        //   + reflection, anders worden toekomstige veranderingen niet gezien
        // ! verglijk de twee bewaarde staten!! 
        
    }

    private static void sanityCheckMarshalledData(Class<?> testClass, List<MarshalledData> testData, 
            List<MarshalledData> baseData) { 
        HashMap<String, List<MarshalledData>> testSnapshotsPerTestMap = extractSnapshotsPerTestMethodMap(testClass, testData);
        HashMap<String, List<MarshalledData>> baseSnapshotsPerTestMap = extractSnapshotsPerTestMethodMap(testClass, baseData);
    
        Set<String> baseTestMethods = baseSnapshotsPerTestMap.keySet(); 
        List<String> testTestMethods = new ArrayList<String>();
        testTestMethods.addAll(testSnapshotsPerTestMap.keySet());
        for( String baseTestMethod : baseTestMethods ) { 
            if( testSnapshotsPerTestMap.get(baseTestMethod) == null ) { 
                logger.error("Marshalled data snapshots for test " + baseTestMethod 
                        + " exist in the base db, but not in the test db generated by this test run!");
            }
            else { 
               Assert.assertNotNull("Empty list of marshalled data snapshots in base for " + baseTestMethod, 
                       baseSnapshotsPerTestMap.get(baseTestMethod));
               Assert.assertEquals("Unequal number of marshalled data snapshots for test " + baseTestMethod 
                       + ": unable to compare marshalled data compatibility for this test.", 
                       baseSnapshotsPerTestMap.get(baseTestMethod).size(), testSnapshotsPerTestMap.get(baseTestMethod).size());
               testTestMethods.remove(baseTestMethod);
            }
        }
        
        for( String testMethod : testTestMethods ) { 
            logger.error("Marshalled data snapshots for test " + testMethod + " have not been added to the base db yet.");
        }
    }

    private static HashMap<String, List<MarshalledData>> extractSnapshotsPerTestMethodMap(Class<?> testClass, List<MarshalledData> marshalledDataList) { 
        String testClassName = testClass.getName();
        HashMap<String, List<MarshalledData>> snapshotsPerTestMethod = new HashMap<String, List<MarshalledData>>();
        for( MarshalledData marshalledData : marshalledDataList ) {
            if( ! marshalledData.testMethodName.contains(testClassName) ) { 
                marshalledData.testMethodName.length();
                continue;
            }
            List<MarshalledData> testMethodMarshalledDataList = snapshotsPerTestMethod.get(marshalledData.testMethodName);
            if( testMethodMarshalledDataList == null ) { 
                testMethodMarshalledDataList = new ArrayList<MarshalledData>();
                snapshotsPerTestMethod.put(marshalledData.testMethodName, testMethodMarshalledDataList); 
            }
            testMethodMarshalledDataList.add(marshalledData);  
        }
        return snapshotsPerTestMethod;
    }
   
    public static StatefulKnowledgeSession unmarshallSession(byte [] marshalledSessionByteArray) throws Exception { 
    
        // Setup marshaller
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Marshaller marshaller = MarshallerFactory.newMarshaller( kbase, new ObjectMarshallingStrategy[] { MarshallerFactory.newSerializeMarshallingStrategy() } );
    
        // Prepare input for marshaller
        ByteArrayInputStream bais = new ByteArrayInputStream( marshalledSessionByteArray );
        SessionConfiguration conf = SessionConfiguration.getDefaultInstance();
        Environment env = EnvironmentFactory.newEnvironment();
    
        // Unmarshall
        StatefulKnowledgeSession ksession = marshaller.unmarshall( bais, conf, env );
        
        return ksession;
    }

    private static StatefulKnowledgeSession unmarshallSession(EntityManagerFactory emf, MarshalledData marshalledData) { 
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());
        env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());
        
        return JPAKnowledgeService.loadStatefulKnowledgeSession(marshalledData.marshalledObjectId.intValue(), kbase, null, env);
    }
}
