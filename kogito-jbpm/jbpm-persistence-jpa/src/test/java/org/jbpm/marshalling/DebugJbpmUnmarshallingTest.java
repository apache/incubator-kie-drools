package org.jbpm.marshalling;

import static org.kie.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static org.jbpm.marshalling.util.MarshallingDBUtil.initializeMarshalledDataEMF;
import static org.jbpm.marshalling.util.MarshallingTestUtil.retrieveMarshallingData;
import static org.jbpm.persistence.util.PersistenceUtil.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javax.persistence.EntityManagerFactory;

import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.jbpm.marshalling.util.*;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugJbpmUnmarshallingTest {

    private static Logger logger = LoggerFactory.getLogger(DebugJbpmUnmarshallingTest.class);
    
    @Test
    @Ignore
    public void testWriteReadUTF() throws IOException { 
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       ObjectOutputStream stream = new ObjectOutputStream(baos);
      
       stream.writeUTF("org.kie.test.TestProcess");
       byte [] resultBytes = baos.toByteArray();
       stream.close();
       
       resultBytes = baos.toByteArray();
       assertTrue( resultBytes != null && resultBytes.length > 0);
      
       ByteArrayInputStream bais = new ByteArrayInputStream(resultBytes);
       ObjectInputStream inStream = new ObjectInputStream(bais);

       String result = null;
       try { 
           result = inStream.readUTF();
       } catch( Exception e ) { 
           e.printStackTrace();
           fail( e.getClass().getSimpleName() + " thrown: " + e.getMessage());
       }
       
       System.out.println(": \"" + result + "\"");
    }

    @Test
    @Ignore
    public void debugUnmarshallingSpecificMarshalledData() { 
        HashSet<String> testMethodAndSnapNumSet = new HashSet<String>();
        testMethodAndSnapNumSet.add("org.jbpm.persistence.session.PersistentStatefulSessionTest.testPersistenceWorkItems2:2");
        testMethodAndSnapNumSet.add("org.jbpm.persistence.session.PersistentStatefulSessionTest.testPersistenceWorkItems3:5");
        
        HashMap<String, Object> testContext
            = initializeMarshalledDataEMF(JBPM_PERSISTENCE_UNIT_NAME, this.getClass(), true);
        EntityManagerFactory emf = (EntityManagerFactory) testContext.get(ENTITY_MANAGER_FACTORY);
        List<MarshalledData> marshalledDataList = retrieveMarshallingData(emf);
        Set<MarshalledData> marshalledDataSet = new HashSet<MarshalledData>();
        for( MarshalledData marshalledDataElement : marshalledDataList ) { 
           if( testMethodAndSnapNumSet.contains(marshalledDataElement.getTestMethodAndSnapshotNum()) ) { 
               marshalledDataSet.add(marshalledDataElement);
               break;
           }
        }
    
        assertTrue("No marshalled data retrieved.", ! marshalledDataSet.isEmpty());
       
        String logMsg = null;
        try { 
            Object unmarshalledObject = null;
            for( MarshalledData marshalledData : marshalledDataSet ) { 
                logMsg = 
                    marshalledData.marshalledObjectClassName.substring(marshalledData.marshalledObjectClassName.lastIndexOf('.')+1)
                    + ": " + marshalledData.getTestMethodAndSnapshotNum();
                unmarshalledObject = MarshallingTestUtil.unmarshallObject(marshalledData);
                assertNotNull(unmarshalledObject);
                logger.info( ".: " + logMsg);
            } 
        }
        catch( Exception e ) { 
            logger.info( "X: " + logMsg);
            e.printStackTrace();
            fail( "[" + e.getClass().getSimpleName() + "]: " + e.getMessage() );
        }
        finally {  
            cleanUp(testContext);
        }
        
    }

    @Test
    @Ignore
    public void compareUnmarshalledObjects() { 
        HashSet<String> testMethodAndSnapNumSet = new HashSet<String>();
        testMethodAndSnapNumSet.add("org.jbpm.persistence.session.PersistentStatefulSessionTest.testPersistenceWorkItems2:2");
        // testMethodAndSnapNumSet.add("org.jbpm.persistence.session.PersistentStatefulSessionTest.testPersistenceWorkItems3:5");
        
        boolean [] dbType = { true, false };
        Object [] objects = new Object [2];
        
        for( int i = 0; i < objects.length; ++i ) { 
            HashMap<String, Object> testContext
                = initializeMarshalledDataEMF(JBPM_PERSISTENCE_UNIT_NAME, this.getClass(), dbType[i], "5.2.0");
            EntityManagerFactory emf = (EntityManagerFactory) testContext.get(ENTITY_MANAGER_FACTORY);

            List<MarshalledData> marshalledDataList = retrieveMarshallingData(emf);
            Set<MarshalledData> marshalledDataSet = new HashSet<MarshalledData>();
            
            for( MarshalledData marshalledDataElement : marshalledDataList ) { 
                if( testMethodAndSnapNumSet.contains(marshalledDataElement.getTestMethodAndSnapshotNum()) ) { 
                    marshalledDataSet.add(marshalledDataElement);
                    break;
                }
            }

            assertFalse("No marshalled data retrieved [" + i + "]", marshalledDataSet.isEmpty());
            assertTrue("Not all marshalled data was retrieved.", marshalledDataSet.size() == testMethodAndSnapNumSet.size() );
            assertTrue("Too much marshalled data was retrieved [" + marshalledDataSet.size() + "]", 
                    marshalledDataSet.size() == 1 );

            Object unmarshalledObject = null;
            try { 
                for( MarshalledData marshalledData : marshalledDataSet ) { 
                    unmarshalledObject = MarshallingTestUtil.unmarshallObject(marshalledData);
                    assertNotNull(unmarshalledObject);
                } 
            }
            catch( Exception e ) { 
                e.printStackTrace();
                fail( "[" + e.getClass().getSimpleName() + "]: " + e.getMessage() );
            }
            finally {  
                cleanUp(testContext);
            }

            objects[i] = unmarshalledObject;
        }

        assertTrue("Not the same", CompareViaReflectionUtil.compareInstances(objects[0], objects[1]));
    }
    
    @Test
    @Ignore
    public void checkMarshalledProcessInstanceObjects() { 
        List<MarshalledData> marshalledDataList = retrieveMarshallingDataForTest(true);
        unmarshallAllObjectsOfClass(marshalledDataList, ProcessInstanceInfo.class.getName());
    }

    @Test
    @Ignore
    public void checkMarshalledWorkItemObjects() throws IOException { 
        List<MarshalledData> marshalledDataList = retrieveMarshallingDataForTest(false);
        unmarshallAllObjectsOfClass(marshalledDataList, WorkItemInfo.class.getName());
    }
   
    @Test
    @Ignore
    public void checkMarshalledSessionInfoObjects() throws IOException { 
        List<MarshalledData> marshalledDataList = retrieveMarshallingDataForTest(true);
        unmarshallAllObjectsOfClass(marshalledDataList, SessionInfo.class.getName());
    }
  
    private List<MarshalledData> retrieveMarshallingDataForTest(boolean fromBase) { 
        HashMap<String, Object> testContext = null;
        List<MarshalledData> marshalledDataList = new ArrayList<MarshalledData>();
        try { 
          testContext = initializeMarshalledDataEMF(JBPM_PERSISTENCE_UNIT_NAME, this.getClass(), fromBase);
          EntityManagerFactory emf = (EntityManagerFactory) testContext.get(ENTITY_MANAGER_FACTORY);
          marshalledDataList = retrieveMarshallingData(emf);
        }
        finally { 
            cleanUp(testContext);
        }
        return marshalledDataList;
    }

    private void unmarshallAllObjectsOfClass(List<MarshalledData> marshalledDataList, String className) { 
        for( MarshalledData marshalledData: marshalledDataList ) { 
            if( ! marshalledData.marshalledObjectClassName.equals(className) ) { 
                continue; 
            }
            String logMsg = 
                marshalledData.marshalledObjectClassName.substring(marshalledData.marshalledObjectClassName.lastIndexOf('.')+1)
                + ": " + marshalledData.getTestMethodAndSnapshotNum();
            try { 
                MarshallingTestUtil.unmarshallObject(marshalledData);
                logger.info( ".: " + logMsg );
            } 
            catch( Exception e ) { 
                logger.info( "X: " + logMsg );
                StackTraceElement [] ste = e.getStackTrace();
                for( int i = 0; i < 1; ++i ) { 
                    StringBuilder elemMsg = new StringBuilder();
                    elemMsg.append("  " + ste[i].getClassName() + ":");
                    elemMsg.append(ste[i].getMethodName() + " [");
                    elemMsg.append(ste[i].getLineNumber() + "]");
                    System.out.println(elemMsg);
                }
            }
        }
    }
}
