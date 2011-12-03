/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.persistence.timer;

import static junit.framework.Assert.*;
import static org.jbpm.test.JBPMHelper.*;

import java.io.IOException;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.persistence.util.LoggingPrintStream;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.h2.tools.Server;
import org.jbpm.test.JBPMHelper;
import org.jbpm.test.KnowledgeSessionCleanup;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * See JBPM-3170/JBPM-3391
 */
public class DelayTimerEventProcessTest {

    // General setup
    private static Logger logger = LoggerFactory.getLogger(DelayTimerEventProcessTest.class);
    
    @Rule
    public KnowledgeSessionCleanup ksessionRule = new KnowledgeSessionCleanup();
   
    // Persistence/process
    private static PoolingDataSource pds = null;
    private static Server server = null;
    private final static String PROCESS_FILE_NAME = "delayTimerEventProcess.bpmn";
    private final static String PROCESS_NAME = "DelayTimerEventProcess";

    // Logging
    private StatefulKnowledgeSession ksession;
    
    // Test specific
    private static long processId = -1;

    
    @BeforeClass
    public static void beforeClass() {
        System.setOut(new LoggingPrintStream(System.out));
        pds = setupDataSource();
        server = startH2Server();
    }
   
    @Before
    public void before() throws IOException { 
        KnowledgeBase kbase = createKnowledgeBase();
        this.ksession = JBPMHelper.newStatefulKnowledgeSession(kbase);
        
        //Console log
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
    }

    /**
     * Creates a ksession from a kbase containing process definition
     * @return 
     */
    public KnowledgeBase createKnowledgeBase(){
        //Create the kbuilder
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    
        //Add simpleProcess.bpmn to kbuilder
        kbuilder.add(new ClassPathResource(PROCESS_FILE_NAME), ResourceType.BPMN2);
        logger.debug("Compiling resources");
        
        //Check for errors
        if (kbuilder.hasErrors()) {
            if (kbuilder.getErrors().size() > 0) {
                for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                    logger.error("Error building kbase: " + error.getMessage());
                }
            }
            throw new RuntimeException("Error building kbase!");
        }
    
        //Create a knowledge base and add the generated package
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    
        return kbase;
    }

    @AfterClass
    public static void afterClass() {
        if( pds != null ) { 
            pds.close();
        }
        if( server != null && server.isRunning(true) ) { 
            server.stop();
            server.shutdown();
        }
        assertTrue("H2 tcp server has NOT been stopped!", server == null || ! server.isRunning(true)); 
    }
    
    @Test
    public void startTimerProcess() throws InterruptedException{
        //Start the process using its id
        ProcessInstance process = ksession.startProcess(PROCESS_NAME);
        processId = process.getId();

        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());
       
    }
   
    @Test
    public void finishTimerProcess() throws InterruptedException {
        assertTrue("Process id has not been saved in previous test!", processId > 0);
        ProcessInstance process = ksession.getProcessInstance(processId);
       
        assertNotNull("Could not retrieve process " + processId, process);
        if( process.getState() == ProcessInstance.STATE_ACTIVE ) { 
            int sleep = 10000;
            logger.debug("Sleeping " + sleep/1000 + " seconds." );
            Thread.sleep(sleep);
            logger.debug("Awake!");
        }
        
        //The process continues until it reaches the end node
        int processState = process.getState();
        Assert.assertTrue("Expected process state " + processStateName[2] + " not " + processStateName[processState], 
                ProcessInstance.STATE_COMPLETED == process.getState());
        
        Long timerExecutionTime = (Long) ((WorkflowProcessInstance)process).getVariable("timerExecutionTime");
        Assert.assertTrue(timerExecutionTime >= 5000);
    }
  
}
