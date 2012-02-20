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

import static org.drools.marshalling.util.MarshallingDBUtil.*;
import static org.drools.persistence.util.PersistenceUtil.*;
import static org.drools.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.StringUtils;
import org.drools.impl.EnvironmentFactory;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.InputMarshaller;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.process.instance.WorkItem;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.time.impl.DefaultTimerJobInstance;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.TransactionManagerServices;


public class MarshallingTestUtil {

    private static Logger logger = LoggerFactory.getLogger(MarshallingTestUtil.class);
  
    protected static boolean STORE_KNOWLEDGE_BASE = false;
    
    protected final static String PROCESS_INSTANCE_INFO_CLASS_NAME = "org.jbpm.persistence.processinstance.ProcessInstanceInfo";
    private final static String PROCESS_INSTANCE_MARSHALL_UTIL_CLASS_NAME = "org.jbpm.marshalling.util.MarshallingTestUtil";
    private final static String PROCESS_INSTANCE_RESOLVER_STRATEGY = "org.jbpm.marshalling.impl.ProcessInstanceResolverStrategy";

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

    /**
     * 
     * @param testClass The class that this method is being called from. Because this class might be located within 
     * a jar when this method is called, it's important to be able to access a "local" ClassLoader in order to retrieve
     * the correct path to the test and base marshalling data db. 
     * @param persistenceUnitName The name of the persistence unit being used. 
     */
    public static void compareMarshallingDataFromTest(Class<?> testClass, String persistenceUnitName) { 
        
        // DO NOT RUN if we aren't testing marshalling!
        if( ! testMarshalling() ) { 
            return;
        }
        
        Object makeBaseDb = getDatasourceProperties().getProperty("makeBaseDb"); 

        boolean baseDBCreationOngoing = false;
        if( "true".equals(makeBaseDb) ) { 
            baseDBCreationOngoing = true;
        }
        
        HashMap<String, Object> testContext = initializeMarshalledDataEMF(persistenceUnitName, testClass, baseDBCreationOngoing);
        
        if( baseDBCreationOngoing ) { 
            checkMarshalledSnapshots(testContext);
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
            compareTestAndBaseMarshallingData(testClass, testDataList, baseDataList, baseDbVersions[v]);
        }
    }

    private static void checkMarshalledSnapshots(HashMap<String, Object> testContext) {
        logger.trace( "Checking MarshalledData objects saved in base db." );
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
                logger.debug( "Unmarshalling snapshot: " + marshalledData.getTestMethodAndSnapshotNum() );
                unmarshallObject(marshalledData);
            } catch( Exception e ) { 
//                e.printStackTrace();
                logger.error( e.getClass().getSimpleName() + " thrown while unmarshalling [" 
                        + marshalledData.getTestMethodAndSnapshotNum() + "] data stored in base database" );
            }
            
        }
        logger.trace( "MarshalledData objects saved in base db:" );
        for( MarshalledData marshalledData : baseDataList ) { 
           logger.trace( "- " + marshalledData); 
        }
    }
    
    public static ArrayList<MarshalledData> retrieveMarshallingData(EntityManagerFactory emf) { 
        ArrayList<MarshalledData> marshalledDataList = new ArrayList<MarshalledData>();
       
        TransactionManager txm = null;
        try { 
            txm = TransactionManagerServices.getTransactionManager();
            txm.begin();
        }
        catch( Exception e ) { 
            logger.warn("Unable to retrieve marshalled snapshots from marshalling database.");
            e.printStackTrace();
            return marshalledDataList;
        }
    
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
        
        try {
            txm.commit();
        } catch (Exception e) {
            logger.warn(e.getClass().getSimpleName() + " thrown when retrieving marshalled snapshots.");
            e.printStackTrace();
        } 
        
        return marshalledDataList;
    }

    /**
     * We do the following in this method: <ul>
     * <li>First, we organize the data in order to do a sanity check on the data
     *   <ul><li>see {@link#sanityCheckMarshalledData(Class, HashMap, HashMap)}</li></ul></li>
     * <li>Then, for every test method <i>snapshot</i> that has passed the sanity check:
     *   <ol><li>Retrieve the marshalled data that was created during the <i>base</i> run</li>
     *   <li>Unmarshall the base marshalled data</li>
     *   <li>Retrieve the marshalled data that was created during the <i>test</i> run</li>
     *   <li>Unmarshall the test marshalled data</li>
     *   <li>Lastly, compare the base unmarshalled object to the test unmarshalled object</li>
     *   </ol>
     * </li>
     * </ul>
     * 
     * @param testClass The class of the test that this is being done in (in order to get the local marshalling db path)
     * @param testData A list of the marshalled data created during this test
     * @param baseData A list of the marshalled data created during the base run (or whichever version of the project)
     */
    private static void compareTestAndBaseMarshallingData(Class<?> testClass, List<MarshalledData> testData, 
            List<MarshalledData> baseData, String baseDbVersion ) { 
   
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

        List<String> errors = new ArrayList<String>();
        for( String testMethodVer : testMarshalledDataSnapshotMap.keySet() ) { 
            logger.info("Comparing marshalled info for " + testMethodVer);
            Object baseObject = null;
            // Base 
            MarshalledData baseMarshalledData = baseMarshalledDataSnapshotMap.get(testMethodVer);
            try { 
                baseObject = unmarshallObject(baseMarshalledData);
            }
            catch( Exception e) {
                logger.info("Unable to unmarshall " + baseDbVersion + " data [" + testMethodVer +  "]: " 
                        + e.getClass().getSimpleName() + ": " + e.getMessage() + "]");
                continue;
            }
           
            Object testObject = null;
            // Test
            MarshalledData testMarshalledData = testMarshalledDataSnapshotMap.get(testMethodVer);
            try { 
                testObject = unmarshallObject(testMarshalledData);
            }
            catch( Exception e) {
                fail("Unable to unmarshall " + baseDbVersion + " data: [" + e.getClass().getSimpleName() + ": " + e.getMessage() + "]");
            }
            
            assertNotNull("Unmarshalled test data resulted in null object!", testObject);
            assertNotNull("Unmarshalled base data resulted in null object!", baseObject);
            
            if( ! CompareViaReflectionUtil.compareInstances(baseObject, testObject) ) { 
                String errorMsg =  "Unmarshalled " + baseObject.getClass().getSimpleName() 
                    + " object from " + baseDbVersion + " data is not equal to test unmarshalled object [" 
                    + baseMarshalledData.getTestMethodAndSnapshotNum() + "]";
                errors.add(errorMsg);
            }
        }
        
        if( errors.size() > 0 ) { 
            int i = errors.size()-1;
            for( ; i > 0; --i ) { 
                logger.warn(errors.get(i));
            }
            fail(errors.get(1));
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
               int numBaseSnapshotsForTestMethod = baseSnapshotsPerTestMap.get(baseTestMethod).size(); 
               int numTestSnapshotsForTestMethod = testSnapshotsPerTestMap.get(baseTestMethod).size();
               if( numBaseSnapshotsForTestMethod != numTestSnapshotsForTestMethod ) { 
                   logger.error("Has test changed? Unequal number [" + baseSnapshotsPerTestMap.get(baseTestMethod).size() + "/" 
                           + testSnapshotsPerTestMap.get(baseTestMethod).size() + "] of for test " + baseTestMethod );
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
     * Unmarshall the marshalled data saved during a test.
     * @param marshalledData 
     * @return The object that the MarshalledData object refers to.
     * @throws Exception When something goes wrong.
     */
    public static Object unmarshallObject(MarshalledData marshalledData) throws Exception { 
        if( SessionInfo.class.getName().equals(marshalledData.marshalledObjectClassName) ) { 
            return unmarshallSession(marshalledData);
        }
        else if( WorkItemInfo.class.getName().equals(marshalledData.marshalledObjectClassName) ) { 
            return unmarshallWorkItem(marshalledData.byteArray);
        }
        else if( PROCESS_INSTANCE_INFO_CLASS_NAME.equals(marshalledData.marshalledObjectClassName) ) { 
            return unmarshallProcessInstance(marshalledData.byteArray);
        }
        else { 
            throw new UnsupportedOperationException("Unable to unmarshall object of type \"" 
                    + marshalledData.marshalledObjectClassName + "\"");
        }
    }
    
    protected static StatefulKnowledgeSession unmarshallSession(MarshalledData marshalledData) throws Exception { 
        // Setup marshaller
        KnowledgeBase kbase;
        if( STORE_KNOWLEDGE_BASE ) { 
            kbase = (KnowledgeBase) DroolsStreamUtils.streamIn(marshalledData.serializedKnowledgeBase);
        }
        else { 
            kbase = KnowledgeBaseFactory.newKnowledgeBase();
        }
        ObjectMarshallingStrategy [] strategies 
            = new ObjectMarshallingStrategy[] { MarshallerFactory.newSerializeMarshallingStrategy() };
        strategies = addProcessInstanceResolverStrategyIfAvailable(strategies);
        Marshaller marshaller = MarshallerFactory.newMarshaller( kbase, strategies );
    
        // Prepare input for marshaller
        ByteArrayInputStream bais = new ByteArrayInputStream( marshalledData.byteArray );
        SessionConfiguration conf = SessionConfiguration.getDefaultInstance();
        Environment env = EnvironmentFactory.newEnvironment();
    
        // Unmarshall
        StatefulKnowledgeSession ksession = marshaller.unmarshall( bais, conf, env );
        
        return ksession;
    }

    private static WorkItem unmarshallWorkItem(byte [] marshalledSessionByteArray) throws Exception { 
        // Setup env/context/stream
        Environment env = EnvironmentFactory.newEnvironment();
        ByteArrayInputStream bais = new ByteArrayInputStream(marshalledSessionByteArray);
        MarshallerReaderContext context = new MarshallerReaderContext(bais, null, null, null, env);
       
        // Unmarshall
        WorkItem unmarshalledWorkItem =  InputMarshaller.readWorkItem(context);
        
        context.close();
        
        return unmarshalledWorkItem;
    }

    private static Object unmarshallProcessInstance(byte [] marshalledSessionByteArray) throws Exception { 
        // Get class/method..
        Class<?> processInstanceMarshallerClass = Class.forName(PROCESS_INSTANCE_MARSHALL_UTIL_CLASS_NAME);
        Method unmarshallMethod = processInstanceMarshallerClass.getMethod("unmarshallProcessInstances", 
                marshalledSessionByteArray.getClass());
        
        // Unmarshall
        Object unmarshalledProcessInstance = unmarshallMethod.invoke(null, marshalledSessionByteArray);
        
        return unmarshalledProcessInstance;
    }
    
    protected static byte [] getProcessInstanceInfoByteArray(Object processInstanceInfo) { 
        byte [] byteArray = null;
        Class<?> processInstanceInfoClass = processInstanceInfo.getClass();
        try {
            Method getByteArrayMethod = processInstanceInfoClass.getMethod("getProcessInstanceByteArray", (Class []) null);
            Object byteArrayObject = getByteArrayMethod.invoke(processInstanceInfo, ((Object []) null));
            byteArray = (byte []) byteArrayObject;
        } catch (Exception e) {
            fail( e.getClass().getSimpleName() + ": unable to retrieve byte array from " + processInstanceInfoClass.getSimpleName() );
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
    
    protected static byte [] getWorkItemByteArray(WorkItemInfo workItemInfo) { 
        Method byteArrayMethod = null;
        byte [] byteArray = null;
        try {
            byteArrayMethod = WorkItemInfo.class.getMethod("getWorkItemByteArray", (Class []) null);
        } catch (Exception e) {
            // do nothing..
        } 
        if( byteArrayMethod != null ) { 
            byteArray = workItemInfo.getWorkItemByteArray();
        }
        else {
            try { 
                Field byteArrayField = WorkItemInfo.class.getDeclaredField("workItemByteArray");
                byteArrayField.setAccessible(true);
                byteArray = (byte []) byteArrayField.get(workItemInfo);
            }
            catch( Exception e ) { 
                e.printStackTrace();
                Assert.fail("Unable to retrieve byte array from WorkItemInfo object.");
            }
        }
        return byteArray;
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

    private static ObjectMarshallingStrategy [] addProcessInstanceResolverStrategyIfAvailable(
            ObjectMarshallingStrategy [] strategies ) { 
     
        ObjectMarshallingStrategy processInstanceResolverStrategyObject = null;
        try {
            Class<?> strategyClass = Class.forName(PROCESS_INSTANCE_RESOLVER_STRATEGY);
            Constructor<?> constructor = strategyClass.getConstructors()[0];
            
            processInstanceResolverStrategyObject = (ObjectMarshallingStrategy) constructor.newInstance(new Object [0]);
        }
        catch( Throwable t ) { 
            // do nothing, strategy class could not be 
        }
       
        ObjectMarshallingStrategy [] newStrategies = new ObjectMarshallingStrategy[strategies.length+1];
        if( processInstanceResolverStrategyObject != null ) { 
           for( int i = 0; i < strategies.length; ++i ) { 
              newStrategies[i] = strategies[i]; 
           }
           newStrategies[strategies.length] = processInstanceResolverStrategyObject;
           strategies = newStrategies;
        }
        
        return strategies;
    }
}
