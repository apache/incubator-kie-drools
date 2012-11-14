package org.jbpm.bpmn2.persistence;

import static junit.framework.Assert.assertTrue;
import static org.jbpm.persistence.util.PersistenceUtil.*;

import java.text.SimpleDateFormat;
import java.util.*;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;

import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;

import org.kie.runtime.Environment;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.jbpm.bpmn2.concurrency.MultipleProcessesPerThreadTest;
import org.jbpm.persistence.util.PersistenceUtil;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnmarshallingOverdueTimersTest {

    private static Logger logger = LoggerFactory.getLogger(MultipleProcessesPerThreadTest.class);

    private HashMap<String, Object> context;

    @Before
    public void setup() {
         context = PersistenceUtil.setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    private static KnowledgeBase loadKnowledgeBase(String bpmn2FileName) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(bpmn2FileName, UnmarshallingOverdueTimersTest.class), ResourceType.BPMN2);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    private StatefulKnowledgeSession createStatefulKnowledgeSession(KnowledgeBase kbase) {
        Environment env = createEnvironment(context);
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
    }

    private static int knowledgeSessionDispose(StatefulKnowledgeSession ksession) {
        int ksessionId = ksession.getId();
        logger.debug("disposing of ksesssion");
        ksession.dispose();
        return ksessionId;
    }

    private StatefulKnowledgeSession reloadStatefulKnowledgeSession(String bpmn2FileName, int ksessionId) {
        KnowledgeBase kbase = loadKnowledgeBase(bpmn2FileName);

        logger.debug(". reloading ksession " + ksessionId);
        Environment env = null;
        env = createEnvironment(context);

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
