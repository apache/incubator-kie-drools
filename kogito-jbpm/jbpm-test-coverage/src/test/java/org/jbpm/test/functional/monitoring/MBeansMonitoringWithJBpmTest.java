/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.functional.monitoring;

import static org.junit.Assert.*;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.ClockType;
import org.drools.core.management.DroolsManagementAgent;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.MBeansOption;
import org.kie.api.management.GenericKieSessionMonitoringMXBean;
import org.kie.api.management.KieSessionMonitoringMXBean;
import org.kie.api.management.StatelessKieSessionMonitoringMXBean;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.JMX;
import javax.management.MBeanServer;

public class MBeansMonitoringWithJBpmTest {
    
    public static final Logger LOG = LoggerFactory.getLogger(MBeansMonitoringWithJBpmTest.class);
    public static final String KCONTAINER1 = "myContainerId";
    public static final String KSESSION1 = "KSession1";
	public static final String KBASE1 = "KBase1";
    private static final String KSESSION2 = "KSession2_stateless";
    private String mbeansprop;

    @Before
    public void setUp() throws Exception {
        mbeansprop = System.getProperty( MBeansOption.PROPERTY_NAME );
        System.setProperty( MBeansOption.PROPERTY_NAME, "enabled" );
    }

    @After
    public void tearDown()
            throws Exception {
    	if (mbeansprop != null) {
    		System.setProperty( MBeansOption.PROPERTY_NAME, mbeansprop );
    	} else {
    		System.setProperty( MBeansOption.PROPERTY_NAME, MBeansOption.DISABLED.toString() );
    	}
    }
    
    @Test
    public void testRulesAndProcesses() throws Exception {
        String drl = "package org.drools.droolsjbpm_integration_testmgt\n" +
                "rule S\n" +
                "when\n" +
                "    $s: String() \n" +
                "then\n" +
                "    try { Thread.sleep(10); } catch (Exception e) { }\n" + 
                "    System.out.println($s); \n" +
                "end\n"
                ;
        
        String process = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" + 
                "<definitions id=\"Definition\"\n" + 
                "             targetNamespace=\"http://www.example.org/MinimalExample\"\n" + 
                "             typeLanguage=\"http://www.java.com/javaTypes\"\n" + 
                "             expressionLanguage=\"http://www.mvel.org/2.0\"\n" + 
                "             xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" + 
                "             xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
                "             xs:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\"\n" + 
                "             xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n" + 
                "             xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n" + 
                "             xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n" + 
                "             xmlns:tns=\"http://www.jboss.org/drools\">\n" + 
                "\n" + 
                "  <process processType=\"Private\" isExecutable=\"true\" id=\"com.sample.HelloWorld\" name=\"Hello World\" >\n" + 
                "\n" + 
                "    <!-- nodes -->\n" + 
                "    <startEvent id=\"_a1\" name=\"StartProcess\" />\n" + 
                "    <scriptTask id=\"_a2\" name=\"Hello\" >\n" + 
                "      <script>System.out.println(\"Hello World\");</script>\n" + 
                "    </scriptTask>\n" + 
                "    <endEvent id=\"_a3\" name=\"EndProcess\" >\n" + 
                "        <terminateEventDefinition/>\n" + 
                "    </endEvent>\n" + 
                "\n" + 
                "    <!-- connections -->\n" + 
                "    <sequenceFlow id=\"_a1-_a2\" sourceRef=\"_a1\" targetRef=\"_a2\" />\n" + 
                "    <sequenceFlow id=\"_a2-_a3\" sourceRef=\"_a2\" targetRef=\"_a3\" />\n" + 
                "\n" + 
                "  </process>"+
                "  <process processType=\"Private\" isExecutable=\"true\" id=\"com.sample.CiaoWorld\" name=\"Ciao World\" >\n" + 
                "\n" + 
                "    <!-- nodes -->\n" + 
                "    <startEvent id=\"_b1\" name=\"StartProcess\" />\n" + 
                "    <scriptTask id=\"_b2\" name=\"Ciao\" >\n" + 
                "      <script>System.out.println(\"Ciao World\");</script>\n" + 
                "    </scriptTask>\n" + 
                "    <endEvent id=\"_b3\" name=\"EndProcess\" >\n" + 
                "        <terminateEventDefinition/>\n" + 
                "    </endEvent>\n" + 
                "\n" + 
                "    <!-- connections -->\n" + 
                "    <sequenceFlow id=\"_b1-_b2\" sourceRef=\"_b1\" targetRef=\"_b2\" />\n" + 
                "    <sequenceFlow id=\"_b2-_b3\" sourceRef=\"_b2\" targetRef=\"_b3\" />\n" + 
                "\n" + 
                "  </process>"+
                "</definitions>";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( KBASE1 ).setDefault( true )
                .setEventProcessingMode( EventProcessingOption.STREAM );
        KieSessionModel ksessionModel1 = kieBaseModel1.newKieSessionModel( KSESSION1 ).setDefault( true )
                .setType( KieSessionModel.KieSessionType.STATEFUL )
                .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        
        KieSessionModel ksessionModel2 = kieBaseModel1.newKieSessionModel( KSESSION2 ).setDefault( false )
                .setType( KieSessionModel.KieSessionType.STATELESS );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie.test", "mbeans", "1.0.0" );
        
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        kfs.generateAndWritePomXML(releaseId1);
        kfs.write("src/main/resources/r" + 1 + ".drl", drl);
        kfs.write("src/main/resources/p" + 1 + ".bpmn2", process);
        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        if( kb.getResults().hasMessages( org.kie.api.builder.Message.Level.ERROR ) ) {
            for( org.kie.api.builder.Message result : kb.getResults().getMessages() ) {
                LOG.error(result.getText());
            }
        }
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository().getKieModule(releaseId1);
        
        KieContainer kc = ks.newKieContainer( KCONTAINER1, releaseId1 );
        KieBase kiebase = kc.getKieBase( KBASE1 );
        Properties ksessionConfigProps = new Properties();
        ksessionConfigProps.setProperty("drools.processSignalManagerFactory"   , DefaultSignalManagerFactory.class.getName());           
        ksessionConfigProps.setProperty("drools.processInstanceManagerFactory" , DefaultProcessInstanceManagerFactory.class.getName());  
        KieSessionConfiguration sessionConf = ks.newKieSessionConfiguration(ksessionConfigProps);
        sessionConf.setOption(ForceEagerActivationOption.YES);
        KieSession ksession = kc.newKieSession(KSESSION1, sessionConf);
        
        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        
        KieSessionMonitoringMXBean aggrMonitor = JMX.newMXBeanProxy(
                mbserver,
                DroolsManagementAgent.createObjectNameBy(KCONTAINER1, KBASE1, KieSessionType.STATEFUL, KSESSION1),
                KieSessionMonitoringMXBean.class);
        
        long tft = 0; 
        
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 0, 0, 0);
        checkTotalFactCount(aggrMonitor, 0);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, tft);
        checkTotalSessions(aggrMonitor, 1);
        assertNull(  aggrMonitor.getStatsForProcess("com.sample.HelloWorld") );
        assertNull(  aggrMonitor.getStatsForProcess("com.sample.CiaoWorld") );
        assertFalse( aggrMonitor.getStatsByProcess().containsKey("com.sample.HelloWorld") );
        assertFalse( aggrMonitor.getStatsByProcess().containsKey("com.sample.CiaoWorld")  );

        ksession.insert("Ciao");
        ksession.startProcess("com.sample.HelloWorld");
        ksession.startProcess("com.sample.HelloWorld");
        ksession.startProcess("com.sample.CiaoWorld");
        ksession.fireAllRules();
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 1, 0, 1);
        checkTotalFactCount(aggrMonitor, 1);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, tft + 1);
        checkTotalSessions(aggrMonitor, 1);
        checkStatsForProcess(aggrMonitor, "com.sample.HelloWorld" ,2,2,6);
        checkStatsForProcess(aggrMonitor, "com.sample.CiaoWorld"  ,1,1,3);

        ksession.fireAllRules();
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 1, 0, 1);
        checkTotalFactCount(aggrMonitor, 1);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, tft);
        checkTotalSessions(aggrMonitor, 1);
        checkStatsForProcess(aggrMonitor, "com.sample.HelloWorld" ,2,2,6);
        checkStatsForProcess(aggrMonitor, "com.sample.CiaoWorld"  ,1,1,3);
        
        LOG.debug("---");
        
        KieSession ksession2 = kc.newKieSession(KSESSION1, sessionConf);
        ksession2.insert("Ciao");
        ksession.startProcess("com.sample.HelloWorld");
        ksession.startProcess("com.sample.HelloWorld");
        ksession.startProcess("com.sample.CiaoWorld");
        ksession2.fireAllRules();
        
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 2, 0, 2);
        checkTotalFactCount(aggrMonitor, 2);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, tft + 1);
        checkTotalSessions(aggrMonitor, 2);
        checkStatsForProcess(aggrMonitor, "com.sample.HelloWorld" ,4,4,12);
        checkStatsForProcess(aggrMonitor, "com.sample.CiaoWorld"  ,2,2,6);

        
        LOG.debug("--- NOW for the STATELESS ---");
        
        tft = 0;
        
        StatelessKieSession stateless = kc.newStatelessKieSession(KSESSION2, sessionConf);
        
        StatelessKieSessionMonitoringMXBean aggrMonitor2 = JMX.newMXBeanProxy(
                mbserver,
                DroolsManagementAgent.createObjectNameBy(KCONTAINER1, KBASE1, KieSessionType.STATELESS, KSESSION2),
                StatelessKieSessionMonitoringMXBean.class);
        
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 0, 0, 0);
        checkRuleRuntimeTotals(aggrMonitor2, 0, 0);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, tft);
        checkTotalSessions(aggrMonitor2, 0);
        
        List<Command> cmds = new ArrayList<Command>(); 
        cmds.add(CommandFactory.newInsert("Ciao"));
        cmds.add(CommandFactory.newStartProcess("com.sample.HelloWorld") );
        cmds.add(CommandFactory.newStartProcess("com.sample.HelloWorld") );
        cmds.add(CommandFactory.newStartProcess("com.sample.CiaoWorld")  );
        BatchExecutionCommand batch = CommandFactory.newBatchExecution(cmds);
        stateless.execute(batch); 
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 1, 0, 1);
        checkRuleRuntimeTotals(aggrMonitor2, 1, 0);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, tft + 1);
        checkTotalSessions(aggrMonitor2, 1);
        checkStatsForProcess(aggrMonitor2, "com.sample.HelloWorld" ,2,2,6);
        checkStatsForProcess(aggrMonitor2, "com.sample.CiaoWorld"  ,1,1,3);
        
        stateless.execute(batch);
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 2, 0, 2);
        checkRuleRuntimeTotals(aggrMonitor2, 2, 0);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, tft + 1);
        checkTotalSessions(aggrMonitor2, 2);
        checkStatsForProcess(aggrMonitor2, "com.sample.HelloWorld" ,4,4,12);
        checkStatsForProcess(aggrMonitor2, "com.sample.CiaoWorld"  ,2,2,6);
        
        StatelessKieSession stateless2 = kc.newStatelessKieSession(KSESSION2, sessionConf);
        LOG.debug(stateless + " " + stateless2);
        checkTotalSessions(aggrMonitor2, 2);
        
        stateless2.execute(batch);
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 3, 0, 3);
        checkRuleRuntimeTotals(aggrMonitor2, 3, 0);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, tft + 1);
        checkTotalSessions(aggrMonitor2, 3);
        checkStatsForProcess(aggrMonitor2, "com.sample.HelloWorld" ,6,6,18);
        checkStatsForProcess(aggrMonitor2, "com.sample.CiaoWorld"  ,3,3,9);
    }
    
    private void checkStatsForProcess(GenericKieSessionMonitoringMXBean mb, String ruleName, long iStarted, long iCompleted, long nodesTriggered) {
        assertEquals(iStarted      , mb.getStatsForProcess(ruleName).getProcessInstancesStarted()   );
        assertEquals(iCompleted    , mb.getStatsForProcess(ruleName).getProcessInstancesCompleted() );
        assertEquals(nodesTriggered, mb.getStatsForProcess(ruleName).getProcessNodesTriggered()     );
    }

    /**
     * Utility method to add locally in test methods so to deliberately block on system.in, allowing time to use jvisualvm to inspecti the JMX manually.
     */
    private static void blockOnSystemINforENTER() throws Exception {
        Runnable task2 = new Runnable() {
            @Override
            public void run() { System.out.println("Press ENTER to continue: "); Scanner sin = new Scanner(System.in); sin.nextLine(); }
        };
        ExecutorService es = Executors.newCachedThreadPool();
        es.submit(task2).get();
    }
    
    private void checkStatsByRule(GenericKieSessionMonitoringMXBean mb, String ruleName, long mCreated, long mCancelled, long mFired) {
        assertEquals(mCreated     , mb.getStatsByRule().get(ruleName).getMatchesCreated()   );
        assertEquals(mCancelled   , mb.getStatsByRule().get(ruleName).getMatchesCancelled() );
        assertEquals(mFired       , mb.getStatsByRule().get(ruleName).getMatchesFired()     );
    }

    private void checkStatsForRule(GenericKieSessionMonitoringMXBean mb, String ruleName, long mCreated, long mCancelled, long mFired) {
        assertEquals(mCreated     , mb.getStatsForRule(ruleName).getMatchesCreated()   );
        assertEquals(mCancelled   , mb.getStatsForRule(ruleName).getMatchesCancelled() );
        assertEquals(mFired       , mb.getStatsForRule(ruleName).getMatchesFired()     );
    }

    private void print(GenericKieSessionMonitoringMXBean mb) {
        LOG.debug("total match created  : {}",mb.getTotalMatchesCreated());
        LOG.debug("total match cancelled: {}",mb.getTotalMatchesCancelled());
        LOG.debug("total match fired    : {}",mb.getTotalMatchesFired());
        if (mb instanceof StatelessKieSessionMonitoringMXBean) {
            StatelessKieSessionMonitoringMXBean c = (StatelessKieSessionMonitoringMXBean) mb;
            LOG.debug("inserted and deleted : +{} -{}",c.getTotalObjectsInserted(),c.getTotalObjectsDeleted());
        } else if (mb instanceof KieSessionMonitoringMXBean) {
            KieSessionMonitoringMXBean c = (KieSessionMonitoringMXBean) mb;
            LOG.debug("total tact count     : {}",c.getTotalFactCount());
        }
        LOG.debug("{} ms .", mb.getTotalFiringTime());
    }
    
    private long checkTotalFiringTimeGEQ(GenericKieSessionMonitoringMXBean mb, long amount) {
        assertTrue(mb.getTotalFiringTime() >= amount);
        return mb.getTotalFiringTime();
    }
    
    private void checkTotalSessions(GenericKieSessionMonitoringMXBean mb, int totalSessions) {
        assertEquals(totalSessions, mb.getTotalSessions()         );
    }
    
    private void checkTotalFactCount(KieSessionMonitoringMXBean mb, int factCount) {
        assertEquals(factCount     , mb.getTotalFactCount()       );
    }
    
    private void checkRuleRuntimeTotals(StatelessKieSessionMonitoringMXBean mb, int inserted, int deleted) {
        assertEquals(inserted     , mb.getTotalObjectsInserted()  );
        assertEquals(deleted      , mb.getTotalObjectsDeleted()   );
    }
    
    private void checkAgendaTotals(GenericKieSessionMonitoringMXBean mb, long mCreated, long mCancelled, long mFired) {
        assertEquals(mCreated     , mb.getTotalMatchesCreated()   );
        assertEquals(mCancelled   , mb.getTotalMatchesCancelled() );
        assertEquals(mFired       , mb.getTotalMatchesFired()     );
    }
}
