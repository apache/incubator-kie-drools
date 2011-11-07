package org.jbpm.marshalling;

import static org.drools.marshalling.util.MarshallingDBUtil.initializeMarshalledDataEMF;
import static org.drools.marshalling.util.MarshallingTestUtil.retrieveMarshallingData;
import static org.drools.persistence.util.PersistenceUtil.*;
import static org.drools.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.drools.marshalling.util.MarshalledData;
import org.drools.marshalling.util.MarshallingTestUtil;
import org.drools.persistence.info.WorkItemInfo;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugJbpmUnmarshallingTest {

    private static Logger logger = LoggerFactory.getLogger(DebugJbpmUnmarshallingTest.class);
    
    @Test
    @Ignore
    public void checkMarshalledProcessInstanceObjects() { 
        HashMap<String, Object> testContext
            = initializeMarshalledDataEMF(JBPM_PERSISTENCE_UNIT_NAME, this.getClass(), true);
        EntityManagerFactory emf = (EntityManagerFactory) testContext.get(ENTITY_MANAGER_FACTORY);
        List<MarshalledData> marshalledDataList = retrieveMarshallingData(emf);
        for( MarshalledData marshalledData: marshalledDataList ) { 
            if( ! marshalledData.marshalledObjectClassName.equals(ProcessInstanceInfo.class.getName()) ) {
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
                fail( "Unable to unmarshall ProcessInstanceInfo object: " + logMsg);
            }
        }
    
        tearDown(testContext);
        
    }


    @Test
    @Ignore
    public void checkMarshalledWorkItemObjects() throws IOException { 
        HashMap<String, Object> testContext
            = initializeMarshalledDataEMF(JBPM_PERSISTENCE_UNIT_NAME, this.getClass(), true);
        EntityManagerFactory emf = (EntityManagerFactory) testContext.get(ENTITY_MANAGER_FACTORY);
        List<MarshalledData> marshalledDataList = retrieveMarshallingData(emf);
        for( MarshalledData marshalledData: marshalledDataList ) { 
            if( ! marshalledData.marshalledObjectClassName.equals(WorkItemInfo.class.getName()) ) {
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
                fail( "Unable to unmarshall WorkItem object: " + logMsg);
            }
            finally { 
                tearDown(testContext);
            }
        }
    
        
    }
    
    @Test
    @Ignore
    public void testWriteReadUTF() throws IOException { 
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       ObjectOutputStream stream = new ObjectOutputStream(baos);
      
       stream.writeUTF("org.drools.test.TestProcess");
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
        testMethodAndSnapNumSet.add("org.jbpm.persistence.session.VariablePersistenceStrategyTest.testWorkItemWithVariablePersistence:2");
        testMethodAndSnapNumSet.add("org.jbpm.persistence.session.PersistentStatefulSessionTest.testPersistenceSubProcess:2");
        
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
            tearDown(testContext);
        }
        
    }
}
