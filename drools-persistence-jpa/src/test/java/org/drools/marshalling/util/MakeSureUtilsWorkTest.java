package org.drools.marshalling.util;

import static java.lang.System.*;
import static org.junit.Assert.*;
import static org.drools.persistence.util.PersistenceUtil.*;
import static org.drools.marshalling.util.MarshallingDBUtil.*;
import static org.drools.marshalling.util.MarshallingTestUtil.*;
import static org.drools.marshalling.util.CompareViaReflectionUtil.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.conf.EventProcessingOption;
import org.drools.event.knowledgebase.AfterFunctionRemovedEvent;
import org.drools.event.knowledgebase.AfterKnowledgeBaseLockedEvent;
import org.drools.event.knowledgebase.AfterKnowledgeBaseUnlockedEvent;
import org.drools.event.knowledgebase.AfterKnowledgePackageAddedEvent;
import org.drools.event.knowledgebase.AfterKnowledgePackageRemovedEvent;
import org.drools.event.knowledgebase.AfterProcessAddedEvent;
import org.drools.event.knowledgebase.AfterProcessRemovedEvent;
import org.drools.event.knowledgebase.AfterRuleAddedEvent;
import org.drools.event.knowledgebase.AfterRuleRemovedEvent;
import org.drools.event.knowledgebase.BeforeFunctionRemovedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseLockedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseUnlockedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgePackageAddedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgePackageRemovedEvent;
import org.drools.event.knowledgebase.BeforeProcessAddedEvent;
import org.drools.event.knowledgebase.BeforeProcessRemovedEvent;
import org.drools.event.knowledgebase.BeforeRuleAddedEvent;
import org.drools.event.knowledgebase.BeforeRuleRemovedEvent;
import org.drools.event.knowledgebase.KnowledgeBaseEventListener;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.KnowledgeBaseImpl.KnowledgeBaseEventListenerWrapper;
import org.drools.marshalling.util.CompareViaReflectionUtil;
import org.drools.marshalling.util.MarshalledData;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.impl.TrackableTimeJobFactoryManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.configuration.GlobalConfiguration;

public class MakeSureUtilsWorkTest {

    
    @Test
    public void testUnmarshallingSpecificMarshalledData() { 
        String testMethodAndSnapNum 
            = "org.drools.timer.integrationtests.TimerAndCalendarTest.testTimerRuleAfterIntReloadSession:1";
        HashMap<String, Object> testContext
            = initializeMarshalledDataEMF(DROOLS_PERSISTENCE_UNIT_NAME, this.getClass(), true);
        EntityManagerFactory emf = (EntityManagerFactory) testContext.get(ENTITY_MANAGER_FACTORY);
        List<MarshalledData> marshalledDataList = retrieveMarshallingData(emf);
        MarshalledData marshalledData = null;
        for( MarshalledData marshalledDataElement : marshalledDataList ) { 
           if( testMethodAndSnapNum.equals(marshalledDataElement.getTestMethodAndSnapshotNum()) ) { 
               marshalledData = marshalledDataElement;
           }
        }
   
        try { 
            StatefulKnowledgeSession ksession = unmarshallSession(marshalledData.rulesByteArray); 
            assertNotNull(ksession);
        } 
        catch( Exception e ) { 
            e.printStackTrace();
            fail( "[" + e.getClass().getSimpleName() + "]: " + e.getMessage() );
        }
        finally { 
            tearDown(testContext);
        }
    }

    @Test
    @Ignore
    public void testCompareArrays() { 
       
        int [] testA = { 1, 3 };
        int [] testB = { 1, 3 };
        
        boolean same = compareArrays(testA, testB);
        printResult(same, testA, testB);
        
        // setup for test
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeBase [] testArrA = { kbase };
        KnowledgeBase [] testArrB = { kbase, null };
        
        same = compareArrays(testArrA, testArrB);
        printResult(same, testArrA, testArrB);
       
        Environment [] testEnvA = { EnvironmentFactory.newEnvironment(), EnvironmentFactory.newEnvironment() };
        Environment [] testEnvB = { EnvironmentFactory.newEnvironment(), EnvironmentFactory.newEnvironment() };
       
        testEnvA[0].set(DROOLS_PERSISTENCE_UNIT_NAME, DROOLS_PERSISTENCE_UNIT_NAME);
        
        same = compareArrays(testEnvA, testEnvB);
        printResult(same, testEnvA, testEnvB);
    }
    
    private static void printResult(boolean same, Object objA, Object objB) { 
        out.println( "Same: " + same );
        String outLine =  "a: {";
        for( int i = 0; i < Array.getLength(objA); ++i ) { 
            outLine += Array.get(objA, i) + ",";
        }
        outLine = outLine.substring(0, outLine.lastIndexOf(",")) + "}";
        out.println(outLine);
        outLine = "b: {";
        for( int i = 0; i < Array.getLength(objB); ++i ) { 
            outLine += Array.get(objB, i) + ",";
        }
        outLine = outLine.substring(0, outLine.lastIndexOf(",")) + "}";
        out.println(outLine); 
    }
    
    @Test
    @Ignore
    public void testCompareInstances() throws Exception { 

        StatefulKnowledgeSession ksessionA = null;
        {
            KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            config.setOption( EventProcessingOption.STREAM );
            KnowledgeBase knowledgeBaseA = KnowledgeBaseFactory.newKnowledgeBase( config );
            KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
            ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
            ((SessionConfiguration) ksconf).setTimerJobFactoryManager( new TrackableTimeJobFactoryManager( ) );
            ksessionA = knowledgeBaseA.newStatefulKnowledgeSession(ksconf, null);
        }

        StatefulKnowledgeSession ksessionB = null;
        {
            KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(); 
            config.setOption( EventProcessingOption.STREAM );
            KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( config );
            KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
            ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
            ((SessionConfiguration) ksconf).setTimerJobFactoryManager( new TrackableTimeJobFactoryManager( ) );
            ksessionB = knowledgeBase.newStatefulKnowledgeSession(ksconf, null);
        }


        Assert.assertTrue(CompareViaReflectionUtil.class.getSimpleName() + " is broken!", 
                compareInstances(ksessionA, ksessionB) );
    }
}
