/*
 * Copyright 2011 Red Hat Inc.
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
package org.drools.marshalling.util;

import static org.drools.runtime.EnvironmentName.*;
import static org.drools.persistence.util.PersistenceUtil.*;
import static org.drools.marshalling.util.MarshallingDBUtil.*;
import static org.drools.marshalling.util.MarshallingTestUtil.PROCESS_INSTANCE_INFO_CLASS_NAME;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.drools.time.impl.DefaultTimerJobInstance;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.TransactionManagerServices;


public class MarshallingTestUtil {

    private static Logger logger = LoggerFactory.getLogger(MarshallingTestUtil.class);
  
    protected static boolean DO_NOT_COMPARE_MAKING_BASE_DB = false;
    
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
        StringBuilder hashCode = new StringBuilder();
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
    protected static String getTestMethodName() { 
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
            if( "runTest".equals(ste[i].getMethodName()) ) { 
                do { 
                    if( TestCase.class.equals(steClass) ) { 
                        StackTraceElement testMethodSTE = ste[i-5];
                        testMethodName = getSTEClass(testMethodSTE).getName() + "." + testMethodSTE.getMethodName();
                    }
                    steClass = steClass.getSuperclass();
                } while( testMethodName == null && steClass != null );
            }
        }
        
        if( testMethodName == null ) { 
            for( int i = 0; testMethodName == null && i < ste.length; ++i ) { 
                Class<?> steClass = getSTEClass(ste[i]);
                if( "call".equals(ste[i].getMethodName()) ) { 
                    do { 
                        if( DefaultTimerJobInstance.class.equals(steClass) ) { 
                            StackTraceElement testMethodSTE = ste[i-5];
                            testMethodName = getSTEClass(testMethodSTE).getName() + "." + testMethodSTE.getMethodName();
                        }
                    } while(true);
                }
            }
        }

        /**
        if( testMethodName == null ) {
            RuntimeException re = new RuntimeException("Unable to determine test method name");
            re.setStackTrace(Thread.currentThread().getStackTrace());
            throw re;
        }
        **/
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

    public static void compareMarshallingDataFromTest(String persistenceUnitName) { 
        Class<?> testClass = null;
        try {
            testClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
        }
        catch(Exception e){ 
            fail("Unable to retrieve class of test running: [" + e.getClass().getSimpleName() + "] " + e.getMessage() );
        }
        compareMarshallingDataFromTest(testClass, persistenceUnitName);
    }
    
    public static void compareMarshallingDataFromTest(Class<?> testClass, String persistenceUnitName) { 
        HashMap<String, Object> testContext = initializeMarshalledDataEMF(persistenceUnitName, testClass, DO_NOT_COMPARE_MAKING_BASE_DB);
        if( DO_NOT_COMPARE_MAKING_BASE_DB ) { 
            List<MarshalledData> baseDataList = null;
            try { 
                EntityManagerFactory testEMF = (EntityManagerFactory) testContext.get(ENTITY_MANAGER_FACTORY);
                baseDataList  = retrieveMarshallingData(testEMF);
            }
            finally { 
                tearDown(testContext); 
            }
            
            assertNotNull("Could not rerieve list of MarshalledData from base db.", baseDataList);
            assertTrue("List of MarshalledData from base db is empty.", ! baseDataList.isEmpty() );
            
            for( MarshalledData marshalledData : baseDataList ) { 
                try { 
                    logger.info( "Unmarshalling snapshot: " + marshalledData.getTestMethodAndSnapshotNum() );
                    unmarshallSession(marshalledData.rulesByteArray);
                } catch( Exception e ) { 
                    e.printStackTrace();
                    fail( "Exception thrown while unmarshalling [" + marshalledData.getTestMethodAndSnapshotNum() + "] data stored in base database" );
                }
                
            }
            logger.info( "MarshalledData objects saved in base db:" );
            for( MarshalledData marshalledData : baseDataList ) { 
               logger.info( "- " + marshalledData); 
            }
            return;
        }
        
        // Retrieve the test data
        List<MarshalledData> testDataList = null;
        try { 
            EntityManagerFactory testEMF = (EntityManagerFactory) testContext.get(ENTITY_MANAGER_FACTORY);
            testDataList  = retrieveMarshallingData(testEMF);
        }
        finally { 
           tearDown(testContext); 
        }
        assertNotNull("Not marshalled data found for " + testClass.getSimpleName(), 
                testDataList != null && ! testDataList.isEmpty() );
    
        String [] baseDbVersions = getListOfBaseDbVers(testClass);

        for( int v = 0; v < baseDbVersions.length; ++v ) { 
            logger.info("Loading marshalled data from base DB version: [" + baseDbVersions[v] + "]");
            // Retrieve the base data
            HashMap<String, Object> baseContext = initializeMarshalledDataEMF(persistenceUnitName, testClass, true, baseDbVersions[v]);
            List<MarshalledData> baseDataList =  null;
            try { 
                EntityManagerFactory baseEMF = (EntityManagerFactory) baseContext.get(ENTITY_MANAGER_FACTORY);
                baseDataList = retrieveMarshallingData(baseEMF);
            }
            finally {
                tearDown(baseContext);
            }
            assertTrue("No base marshalled data found", baseDataList != null && ! baseDataList.isEmpty() );

            // Compare!
            compareTestAndBaseMarshallingData(testClass, testDataList, baseDataList);
        }
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
            logger.trace("> " + marshalledData);
        }
        
        txm.commit(txOwner);
        
        return marshalledDataList;
    }

    /**
     * 
     *   x eerst, selecteer een test method
     *   x pak alle snapshots from test voor die test method
     *   x pak alle snapshots from base voor die test method
     *   x bevestig dat je hetzelfde aantal heb
     *       x zo niet.. waarschuw, of faal!
     *   een voor een 
     *   - pak de snapshot van _test
     *     + gebruik inputMarshaller om iets te maken
     *     + bewaar de staat (xml/dump tree-achtig??)
     *     + reflection, anders worden toekomstige veranderingen niet gezien
     *   - pak de snapshot van base
     *     + gebruik inputMarshaller om iets te maken
     *     + bewaar de staat (xml/dump tree-achtig??)
     *     + reflection, anders worden toekomstige veranderingen niet gezien
     *   ! verglijk de twee bewaarde staten!! 
     */
    private static void compareTestAndBaseMarshallingData(Class<?> testClass, List<MarshalledData> testData, 
            List<MarshalledData> baseData ) { 
   
        // Extract the marshalled data info for all methods from THIS test (testClass)
        HashMap<String, List<MarshalledData>> testSnapshotsPerTestMap = extractSnapshotsPerTestMethodMap(testClass, testData);
        HashMap<String, List<MarshalledData>> baseSnapshotsPerTestMap = extractSnapshotsPerTestMethodMap(testClass, baseData);
       
        // Check that the tests for which the marshalled data has been retrieved
        //  haven't changed (diff numbers of 
        sanityCheckMarshalledData(testClass, testSnapshotsPerTestMap, baseSnapshotsPerTestMap);
       
        HashMap<String, MarshalledData> testMarshalledDataSnapshotMap = new HashMap<String, MarshalledData>();
        HashMap<String, MarshalledData> baseMarshalledDataSnapshotMap = new HashMap<String, MarshalledData>();
    
        for( String testMethod : testSnapshotsPerTestMap.keySet() ) { 
            for( MarshalledData testMarshalledData : testSnapshotsPerTestMap.get(testMethod) ) { 
                testMarshalledDataSnapshotMap.put(testMarshalledData.getTestMethodAndSnapshotNum(), testMarshalledData);
            }
         }
        for( String testMethod : baseSnapshotsPerTestMap.keySet() ) { 
            for( MarshalledData baseMarshalledData : baseSnapshotsPerTestMap.get(testMethod) ) { 
                baseMarshalledDataSnapshotMap.put(baseMarshalledData.getTestMethodAndSnapshotNum(), baseMarshalledData);
            }
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
                e.printStackTrace();
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
        
        
    }

    /**
     * We check three things: <ol>
     * <li>Do the snapshots for a test method from this (test) class exist 
     *     in the data saved from this test run?</li>
     * <li>Are there the same number of snapshots for this test method in the data from this test run 
     *     AND the data from the base being used? </li>
     * <li>Do the snapshots for a test method from this (test) class exist
     *     in the data from the base data being used?</li>
     * </ul>
     * <p/>
     * When these checks fail, it will mean the following:<ol>
     * <li>The test method existed when the base was made (in the past), but not in the current version of the code.</li>
     * <li>The test method exists now and existed when the base was made -- but the test has changed in between 
     *     such that there are more snapshots being made during the test.
     *     <ul><li>If the test has changed, we can't trust the information anymore (or can't know that), 
     *             so we don't do that.</li></ul>
     *     </li> 
     * <li>The test method did not exist when this base was made (in the past). If we're using the "current" version
     *     of the base info, this means that we probably should recreate the base.</li>
     * </ol>
     * @param testClass The class of the test for which marshalled data is being compared.
     * @param testSnapshotsPerTestMap The list of MarshalledData snapshots per test (method) from this test run.
     * @param baseSnapshotsPerTestMap The list of MarshalledData snapshots per test (method) from the base db being used.
     */
    private static void sanityCheckMarshalledData(Class<?> testClass, HashMap<String, List<MarshalledData>> testSnapshotsPerTestMap,
        HashMap<String, List<MarshalledData>> baseSnapshotsPerTestMap ) {     
            
        Set<String> testTestMethods = new HashSet<String>(testSnapshotsPerTestMap.keySet());

        List<String> untestableTestMethods = new ArrayList<String>();
        for( String baseTestMethod : baseSnapshotsPerTestMap.keySet() ) { 
            // 1. In this base db, but NOT in this test run!!
            if( ! testTestMethods.contains(baseTestMethod) ) { 
                logger.error("Marshalled data snapshots for test " + baseTestMethod 
                        + " exist in the base db, but not in the test db generated by this test run!");
                untestableTestMethods.add(baseTestMethod);
            }
            else { 
                // This is just to make sure we can do the next check.
                // If this fails, something really crazy is going on in the code.. (retrieved from the database.. but didn't ??)
               Assert.assertNotNull("Empty list of marshalled data snapshots in base for " + baseTestMethod, 
                       baseSnapshotsPerTestMap.get(baseTestMethod));
               
               // 2. This means that the test has changed somehow (between when the base db was made and this test run).
               if( baseSnapshotsPerTestMap.get(baseTestMethod).size() != testSnapshotsPerTestMap.get(baseTestMethod).size() ) { 
                   logger.error("Test has changed: unequal number of marshalled data snapshots for test " + baseTestMethod );
                   if( testSnapshotsPerTestMap.remove(baseTestMethod) != null ) { 
                       logger.warn( "Removing data and NOT comparing data for test " + baseTestMethod );
                       untestableTestMethods.add(baseTestMethod);
                   }
               }
              
               testTestMethods.remove(baseTestMethod);
            }
        }
        for( String badTestMethod : untestableTestMethods ) { 
            baseSnapshotsPerTestMap.remove(badTestMethod);
        }
        
        // 3. In this test run, but NOT in the base db!!
        for( String testMethod : testTestMethods ) { 
            logger.info("Marshalled data snapshots for test " + testMethod + " do not exist in this base db." );
            testSnapshotsPerTestMap.keySet().remove(testMethod);
        }
    }
    
    /**
     * This class extracts the following data structure: 
     * - For every test method in the given test class: 
     *   - make a list of the MarshalledData snapshots saved in that test method (testMethodMarshalledDataList). 
     * @param testClass The testClass that we're comparing marshalled data for. 
     * @param marshalledDataList A list of MarshalledData objects retrieved (from the test or a base (version) database).
     * @return A HashMap<String (testMethod), List<MarshalledData>> (testMethodMarshalledDataList)> object, described above. 
     */
    private static HashMap<String, List<MarshalledData>> extractSnapshotsPerTestMethodMap(Class<?> testClass, List<MarshalledData> marshalledDataList) { 
        String testClassName = testClass.getName();
        HashMap<String, List<MarshalledData>> snapshotsPerTestMethod = new HashMap<String, List<MarshalledData>>();
        for( MarshalledData marshalledData : marshalledDataList ) {
            if( ! marshalledData.testMethodName.startsWith(testClassName) ) { 
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

        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());
        env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());
        
        return JPAKnowledgeService.loadStatefulKnowledgeSession(marshalledData.marshalledObjectId.intValue(), kbase, null, env);
    }

    protected final static String PROCESS_INSTANCE_INFO_CLASS_NAME = "org.jbpm.persistence.processinstance.ProcessInstanceInfo";
    
    protected static byte [] getProcessInstanceInfoByteArray(Object processInstanceInfo) { 
        byte [] byteArray = null;
        Class<?> processInstanceInfoClass = processInstanceInfo.getClass();
        try {
            Method getByteArrayMethod = processInstanceInfoClass.getMethod("getProcessInstanceByteArray", (Class []) null);
            Object byteArrayObject = getByteArrayMethod.invoke(processInstanceInfo, ((Object []) null));
            byteArray = (byte []) byteArrayObject;
        } catch (Exception e) {
            fail( "Unable to retrieve byte array from " + processInstanceInfoClass.getSimpleName() + " object." );
        }
        
        return byteArray;
    }

    protected static Long getProcessInstanceInfoId(Object obj) { 
        Long id = null;
        try {
            Method getIdMethod = Class.forName(PROCESS_INSTANCE_INFO_CLASS_NAME).getMethod("getId", (Class []) null);
            Object idObj = getIdMethod.invoke(obj, (Object []) null);
            id = (Long) idObj;
        } catch (Exception e) {
            fail("Unable to retrieve id of ProcessInstanceInfo: [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        } 
        return id;
    }
    
}
