package org.jbpm.bpmn2.persistence;
import static junit.framework.Assert.*;
import static org.drools.runtime.EnvironmentName.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.bpmn2.concurrency.MultipleProcessesPerThreadTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class UnmarshallingOverdueTimersTest {

    private static Logger logger = LoggerFactory.getLogger(MultipleProcessesPerThreadTest.class);

    private static EntityManagerFactory emf;
    private static PoolingDataSource pds;

    @Before
    public void setup() {
        pds = new PoolingDataSource();

        // The name must match what's in the persistence.xml!
        pds.setUniqueName("jdbc/testDS1");

        pds.setMaxPoolSize(16);
        pds.setAllowLocalTransactions(true);

        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.getDriverProperties().put("user", "sa");
        pds.getDriverProperties().put("password", "sasa");
        pds.getDriverProperties().put("url", "jdbc:h2:file:jbpm-test");
        pds.getDriverProperties().put("driverClassName", "org.h2.Driver" );

        pds.init();

        emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        assertNotNull("EntityManagerFactory is null.", emf);
    }

    @After
    public void tearDown() throws Exception {
        BitronixTransactionManager txm = TransactionManagerServices.getTransactionManager();
        if (txm != null) {
            txm.shutdown();
        }

        try {
            emf.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        try {
            pds.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private static KnowledgeBase loadKnowledgeBase(String bpmn2FileName) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(bpmn2FileName, UnmarshallingOverdueTimersTest.class), ResourceType.BPMN2);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    private static Environment createEnvironment() { 
        Environment env = EnvironmentFactory.newEnvironment();

        env.set(ENTITY_MANAGER_FACTORY, emf);
        env.set(TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());
        env.set(GLOBALS, new MapGlobalResolver());
        
        return env;
    }
    
    private static StatefulKnowledgeSession createStatefulKnowledgeSession(KnowledgeBase kbase) {
        Environment env = createEnvironment();
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
    }

    private static int knowledgeSessionDispose(StatefulKnowledgeSession ksession) {
        int ksessionId = ksession.getId();
        logger.debug("disposing of ksesssion");
        ksession.dispose();
        return ksessionId;
    }

    private static StatefulKnowledgeSession reloadStatefulKnowledgeSession(String bpmn2FileName, int ksessionId) {
        KnowledgeBase kbase = loadKnowledgeBase(bpmn2FileName);

        logger.debug(". reloading ksession " + ksessionId);
        Environment env = null;
        env = createEnvironment();

        return JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, env);
    }

    private static long seconds = 10;
    private static String timeUnit = "s";
    private static String bpmn2FileName = "BPMN2-TimerInterrupted.bpmn2";

    private static boolean debug = true;
    
    @Test
    public void startDisposeAndReloadTimerProcess() throws Exception {
        if( debug ) { 
            String shellVar = "TEST";
            String shellVarVal = System.getenv(shellVar);
            if( shellVarVal != null ) { 
                debug = false;
            }
        }
        
        String sessionPropName = "KSESSION_ID";
        String sessionPropVal = System.getenv(sessionPropName);
        String processPropName = "PROCESS_ID";
        String processPropVal = System.getenv(sessionPropName);
        
        if (sessionPropVal == null || debug ) {
            KnowledgeBase kbase = loadKnowledgeBase(bpmn2FileName);
            StatefulKnowledgeSession ksession = createStatefulKnowledgeSession(kbase);

            // setup parameters
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("time", seconds + timeUnit);

            // note process start time
            Calendar cal = GregorianCalendar.getInstance();
            
            // start process
            ProcessInstance processInstance = ksession.startProcess("interruptedTimer", params);
            long processId = processInstance.getId();
            // print info for next test
            if( debug ) { 
                processPropVal = Long.toString(processId);
            }
            else { 
                logger.info("export " + processPropName + "=" + processId );
            }

            // dispose of session 
            KnowledgeSessionConfiguration config = ksession.getSessionConfiguration();
            int ksessionId = knowledgeSessionDispose(ksession);
            
            // print info for next test
            if( debug ) { 
                sessionPropVal = Integer.toString(ksessionId);
            }
            else { 
                logger.info("export " + sessionPropName + "=" + ksessionId );
                
            }
            
            if( !debug ) { 
                cal.add(Calendar.SECOND, (int) seconds);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
                logger.info("\nPlease wait at least " + seconds + timeUnit + " [" +  sdf.format(cal.getTime()) + "]\n" );
            }
        } 
        
        if( debug ) { 
            long wait = (long) ((double) seconds * 1000d * 1.1);
            logger.debug("sleeping " + wait + " seconds" );
            Thread.sleep(seconds * 1000 );
        }
        
        if( sessionPropVal != null || debug ) {
            // reload session
            int ksessionId = Integer.parseInt(sessionPropVal);
            StatefulKnowledgeSession ksession = reloadStatefulKnowledgeSession(bpmn2FileName, ksessionId);
            long processInstanceId = Integer.parseInt(processPropVal);

            logger.debug("! waiting 5 seconds for timer to fire");
            Thread.sleep(5 * 1000);
            
            ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
            if( processInstance != null ) { 
                assertTrue("Process has not terminated.", processInstance.getState() == ProcessInstance.STATE_COMPLETED );
            }
        }
    }
}
