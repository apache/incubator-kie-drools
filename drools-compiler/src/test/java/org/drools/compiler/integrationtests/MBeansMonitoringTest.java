/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.kie.builder.impl.InternalKieContainer;
import org.drools.core.ClockType;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.management.DroolsManagementAgent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.MBeansOption;
import org.kie.api.management.KieContainerMonitorMXBean;
import org.kie.api.management.GenericKieSessionMonitoringMBean;
import org.kie.api.management.KieSessionMonitoringMBean;
import org.kie.api.management.StatelessKieSessionMonitoringMBean;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class MBeansMonitoringTest extends CommonTestMethodBase {
    public static final Logger LOG = LoggerFactory.getLogger(MBeansMonitoringTest.class);
    
    public static final String KSESSION1 = "KSession1";
	public static final String KBASE1 = "KBase1";
    private static final String KBASE2 = "KBase2";
    private static final String KSESSION2 = "KSession2";
    private String mbeansprop;

    @Before
    public void setUp() throws Exception {
        mbeansprop = System.getProperty( MBeansOption.PROPERTY_NAME );
        System.setProperty( MBeansOption.PROPERTY_NAME, "enabled" );    
    }

    @After
    public void tearDown() throws Exception {
    	if (mbeansprop != null) {
    		System.setProperty( MBeansOption.PROPERTY_NAME, mbeansprop );
    	} else {
    		System.setProperty( MBeansOption.PROPERTY_NAME, MBeansOption.DISABLED.toString() );
    	}
    }

    @Test
    public void testEventOffset() throws Exception {
    	String drl = "package org.drools.compiler.test\n" +
    			"import org.drools.compiler.StockTick\n" +
    			"declare StockTick\n" +
    			"    @role(event)\n" +
    			"    @expires(10s)\n" +
    			"end\n" +
    			"rule X\n" +
    			"when\n" +
    			"    StockTick()\n" +
    			"then\n" +
    			"end";

    	KieServices ks = KieServices.Factory.get();

    	KieModuleModel kproj = ks.newKieModuleModel();

    	KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( KBASE1 ).setDefault( true )
    			.setEventProcessingMode( EventProcessingOption.STREAM );
    	KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel( KSESSION1 ).setDefault( true )
    			.setType( KieSessionModel.KieSessionType.STATEFUL )
    			.setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

    	ReleaseId releaseId1 = ks.newReleaseId( "org.kie.test", "mbeans", "1.0.0" );
    	createKJar( ks, kproj, releaseId1, null, drl );

    	KieContainer kc = ks.newKieContainer( releaseId1 );

    	KieBase kiebase = kc.getKieBase( KBASE1 );
    	MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();

    	ObjectName kbOn = DroolsManagementAgent.createObjectNameFor((InternalKnowledgeBase) kiebase);
    	mbserver.invoke( kbOn, "startInternalMBeans", new Object[0], new String[0] );

    	Object expOffset = mbserver.getAttribute( new ObjectName( kbOn + ",group=EntryPoints,EntryPoint=DEFAULT,ObjectType=org.drools.compiler.StockTick" ), "ExpirationOffset" );
    	Assert.assertEquals( 10001, ((Number) expOffset).longValue() );
    }
    
    @Test
    public void testContainerMBeans() throws Exception {
    	String drl = "package org.drools.compiler.test\n" +
    			"import org.drools.compiler.StockTick\n" +
    			"declare StockTick\n" +
    			"    @role(event)\n" +
    			"    @expires(10s)\n" +
    			"end\n" +
    			"rule X\n" +
    			"when\n" +
    			"    StockTick()\n" +
    			"then\n" +
    			"end";

    	KieServices ks = KieServices.Factory.get();

    	KieModuleModel kproj = ks.newKieModuleModel();

    	KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( KBASE1 ).setDefault( true )
    			.setEventProcessingMode( EventProcessingOption.STREAM );
    	KieSessionModel ksessionModel1 = kieBaseModel1.newKieSessionModel( KSESSION1 ).setDefault( true )
    			.setType( KieSessionModel.KieSessionType.STATEFUL )
    			.setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

    	ReleaseId releaseId1 = ks.newReleaseId( "org.kie.test", "mbeans", "1.0.0" );
    	createKJar( ks, kproj, releaseId1, null, drl );
    	
    	KieContainer kc = ks.newKieContainer( releaseId1 );
    	KieBase kiebase = kc.getKieBase( KBASE1 );
    	kc.newKieSession(KSESSION1);
    	kiebase.newKieSession();
    	
    	String kc1ID = ((InternalKieContainer) kc).getContainerId();
    	MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
    	LOG.debug("{}", mbserver.queryNames(new ObjectName("org.kie:kcontainerId="+ObjectName.quote(kc1ID)+",*"), null) );
    	
    	ReleaseId verRelease = ks.newReleaseId("org.kie.test", "mbeans", "RELEASE" );
        KieContainer kc2 = ks.newKieContainer( "Matteo", verRelease );
    	kc2.newKieSession(KSESSION1);
    	
    	
    	KieContainerMonitorMXBean c1Monitor = JMX.newMXBeanProxy(
    			mbserver,
    			DroolsManagementAgent.createObjectNameBy(kc1ID),
    	        KieContainerMonitorMXBean.class);
    	assertEquals(releaseId1.toExternalForm(), c1Monitor.getConfiguredReleaseIdStr());
    	assertEquals(releaseId1.toExternalForm(), c1Monitor.getResolvedReleaseIdStr());
    	
    	assertTrue(c1Monitor.getConfiguredReleaseId().sameGAVof(releaseId1));
        assertTrue(c1Monitor.getResolvedReleaseId().sameGAVof(releaseId1));
        assertEquals(releaseId1.getVersion(), c1Monitor.getConfiguredReleaseId().getVersion());
        assertEquals(releaseId1.getVersion(), c1Monitor.getResolvedReleaseId().getVersion());
    	
    	KieContainerMonitorMXBean c2Monitor = JMX.newMXBeanProxy(
    			mbserver,
    			DroolsManagementAgent.createObjectNameBy("Matteo"),
    	        KieContainerMonitorMXBean.class);
    	assertEquals(verRelease.toExternalForm(), c2Monitor.getConfiguredReleaseIdStr());
    	assertEquals(releaseId1.toExternalForm(), c2Monitor.getResolvedReleaseIdStr());
    	
        assertTrue(c2Monitor.getConfiguredReleaseId().sameGAVof(verRelease));
        assertTrue(c2Monitor.getResolvedReleaseId().sameGAVof(releaseId1));
        assertEquals(verRelease.getVersion(), c2Monitor.getConfiguredReleaseId().getVersion());
        assertEquals(releaseId1.getVersion(), c2Monitor.getResolvedReleaseId().getVersion());
        
        // MBean are supported only via KieContainer public API.
        assertEquals(3, mbserver.queryNames(new ObjectName("org.kie:kcontainerId="+ObjectName.quote(kc1ID)+",*"), null).size());
        ((InternalKieContainer) kc).dispose();
        assertEquals(0, mbserver.queryNames(new ObjectName("org.kie:kcontainerId="+ObjectName.quote(kc1ID)+",*"), null).size());
        assertEquals(3, mbserver.queryNames(new ObjectName("org.kie:kcontainerId="+ObjectName.quote("Matteo")+",*"), null).size());
    }
    
    @Test
    public void testAggregatedAndDispose() throws Exception {
        String drl = "package org.drools.compiler.integrationtests\n" +
    
                "rule ND\n" +
                "when\n" +
                "    String() \n" +
                "    not ( Double() ) \n" +
                "then\n" +
                "    // do nothing. \n" +
                "end\n"+
                
                "rule ND2\n" +
                "salience 1\n" +
                "when\n" +
                "    $s : String() \n" +
                "    not ( Double() ) \n" +
                "then\n" +
                "    try { Thread.sleep(10); } catch (Exception e) { }\n" + 
                "    insert( new Double(0) );\n" +
                "    retract( $s );\n" +
                "end\n"
                
                ;

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( KBASE1 ).setDefault( true )
                .setEventProcessingMode( EventProcessingOption.STREAM );
        KieSessionModel ksessionModel1 = kieBaseModel1.newKieSessionModel( KSESSION1 ).setDefault( true )
                .setType( KieSessionModel.KieSessionType.STATEFUL )
                .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        
        KieBaseModel kieBaseModel2 = kproj.newKieBaseModel( KBASE2 ).setDefault( false );
        KieSessionModel ksessionModel2 = kieBaseModel2.newKieSessionModel( KSESSION2 ).setDefault( true )
                .setType( KieSessionModel.KieSessionType.STATELESS );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie.test", "mbeans", "1.0.0" );
        createKJar( ks, kproj, releaseId1, null, drl );
        
        String containerId = "myContainerId";
        KieContainer kc = ks.newKieContainer( containerId, releaseId1 );
        KieBase kiebase = kc.getKieBase( KBASE1 );
        KieSessionConfiguration sessionConf = ks.newKieSessionConfiguration();
        sessionConf.setOption(ForceEagerActivationOption.YES);
        KieSession ksession = kc.newKieSession(KSESSION1, sessionConf);
        
        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        
        KieSessionMonitoringMBean aggrMonitor = JMX.newMXBeanProxy(
                mbserver,
                DroolsManagementAgent.createObjectNameBy(containerId, KBASE1, KieSessionType.STATEFUL, KSESSION1),
                KieSessionMonitoringMBean.class);
        
        long tft = 0; 
        
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 0, 0, 0);
        checkTotalFactCount(aggrMonitor, 0);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, tft);
        checkTotalSessions(aggrMonitor, 1);

        ksession.insert("Ciao");
        ksession.fireAllRules();
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 2, 1, 1);
        checkTotalFactCount(aggrMonitor, 1);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, tft + 1);
        checkTotalSessions(aggrMonitor, 1);

        ksession.fireAllRules();
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 2, 1, 1);
        checkTotalFactCount(aggrMonitor, 1);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, tft);
        checkTotalSessions(aggrMonitor, 1);
        
        LOG.debug("---");
        
        KieSession ksession2 = kc.newKieSession(KSESSION1, sessionConf);
        ksession2.insert("Ciao");
        ksession2.fireAllRules();
        
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 4, 2, 2);
        checkTotalFactCount(aggrMonitor, 2);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, tft + 1);
        checkTotalSessions(aggrMonitor, 2);
        
        ksession2.dispose();
        checkTotalSessions(aggrMonitor, 1);
        checkTotalFactCount(aggrMonitor, 1);
        ksession.dispose();
        checkTotalSessions(aggrMonitor, 0);
        checkTotalFactCount(aggrMonitor, 0);
        
        LOG.debug("--- NOW for the STATELESS ---");
        
        tft = 0;
        
        StatelessKieSession stateless = kc.newStatelessKieSession(KSESSION2, sessionConf);
        
        StatelessKieSessionMonitoringMBean aggrMonitor2 = JMX.newMXBeanProxy(
                mbserver,
                DroolsManagementAgent.createObjectNameBy(containerId, KBASE2, KieSessionType.STATELESS, KSESSION2),
                StatelessKieSessionMonitoringMBean.class);
        
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 0, 0, 0);
        checkRuleRuntimeTotals(aggrMonitor2, 0, 0);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, tft);
        checkTotalSessions(aggrMonitor2, 0);

        stateless.execute("Ciao");
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 2, 1, 1);
        checkRuleRuntimeTotals(aggrMonitor2, 2, 1);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, tft + 1);
        checkTotalSessions(aggrMonitor2, 1);
        
        stateless.execute("Ciao");
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 4, 2, 2);
        checkRuleRuntimeTotals(aggrMonitor2, 4, 2);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, tft + 1);
        checkTotalSessions(aggrMonitor2, 2);
        
        StatelessKieSession stateless2 = kc.newStatelessKieSession(KSESSION2, sessionConf);
        LOG.debug(stateless + " " + stateless2);
        checkTotalSessions(aggrMonitor2, 2);
        
        stateless2.execute("Ciao");
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 6, 3, 3);
        checkRuleRuntimeTotals(aggrMonitor2, 6, 3);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, tft + 1);
        checkTotalSessions(aggrMonitor2, 3);
        
        KieContainer kc2 = ks.newKieContainer( "kc2", releaseId1 );
        assertEquals(5, mbserver.queryNames(new ObjectName("org.kie:kcontainerId="+ObjectName.quote(containerId)+",*"), null).size());
        ((InternalKieContainer) kc).dispose();
        assertEquals(0, mbserver.queryNames(new ObjectName("org.kie:kcontainerId="+ObjectName.quote(containerId)+",*"), null).size());
        assertEquals(1, mbserver.queryNames(new ObjectName("org.kie:kcontainerId="+ObjectName.quote("kc2")+",*"), null).size());
    }
    
    private void print(GenericKieSessionMonitoringMBean mb) {
        LOG.debug("total match created  : {}",mb.getTotalMatchesCreated());
        LOG.debug("total match cancelled: {}",mb.getTotalMatchesCancelled());
        LOG.debug("total match fired    : {}",mb.getTotalMatchesFired());
        if (mb instanceof StatelessKieSessionMonitoringMBean) {
            StatelessKieSessionMonitoringMBean c = (StatelessKieSessionMonitoringMBean) mb;
            LOG.debug("inserted and deleted : +{} -{}",c.getTotalObjectsInserted(),c.getTotalObjectsDeleted());
        } else if (mb instanceof KieSessionMonitoringMBean) {
            KieSessionMonitoringMBean c = (KieSessionMonitoringMBean) mb;
            LOG.debug("total tact count     : {}",c.getTotalFactCount());
        }
        LOG.debug("{} ms .", mb.getTotalFiringTime());
    }
    
    private long checkTotalFiringTimeGEQ(GenericKieSessionMonitoringMBean mb, long amount) {
        assertTrue(mb.getTotalFiringTime() >= amount);
        return mb.getTotalFiringTime();
    }
    
    private void checkTotalSessions(GenericKieSessionMonitoringMBean mb, int totalSessions) {
        assertEquals(totalSessions, mb.getTotalSessions()         );
    }
    
    private void checkTotalFactCount(KieSessionMonitoringMBean mb, int factCount) {
        assertEquals(factCount     , mb.getTotalFactCount()       );
    }
    
    private void checkRuleRuntimeTotals(StatelessKieSessionMonitoringMBean mb, int inserted, int deleted) {
        assertEquals(inserted     , mb.getTotalObjectsInserted()  );
        assertEquals(deleted      , mb.getTotalObjectsDeleted()   );
    }
    
    private void checkAgendaTotals(GenericKieSessionMonitoringMBean mb, long mCreated, long mCancelled, long mFired) {
        assertEquals(mCreated     , mb.getTotalMatchesCreated()   );
        assertEquals(mCancelled   , mb.getTotalMatchesCancelled() );
        assertEquals(mFired       , mb.getTotalMatchesFired()     );
    }
}
