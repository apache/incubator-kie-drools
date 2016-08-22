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
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.MBeansOption;
import org.kie.api.management.KieContainerMonitorMXBean;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.lang.management.ManagementFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class MBeansMonitoringTest extends CommonTestMethodBase {
    public static final String KSESSION1 = "KSession1";
	public static final String KBASE1 = "KBase1";
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
    	KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel( KSESSION1 ).setDefault( true )
    			.setType( KieSessionModel.KieSessionType.STATEFUL )
    			.setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

    	ReleaseId releaseId1 = ks.newReleaseId( "org.kie.test", "mbeans", "1.0.0" );
    	createKJar( ks, kproj, releaseId1, null, drl );
    	
    	KieContainer kc = ks.newKieContainer( releaseId1 );
    	KieBase kiebase = kc.getKieBase( KBASE1 );
    	kc.newKieSession(KSESSION1);
    	kiebase.newKieSession();
    	
    	String kc1ID = ((InternalKieContainer) kc).getContainerId();
    	
    	ReleaseId verRelease = ks.newReleaseId("org.kie.test", "mbeans", "RELEASE" );
        KieContainer kc2 = ks.newKieContainer( "Matteo", verRelease );
    	kc2.newKieSession(KSESSION1);
    	
    	MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
    	
    	KieContainerMonitorMXBean c1Monitor = JMX.newMXBeanProxy(
    			mbserver,
    			DroolsManagementAgent.createObjectNameByContainerId(kc1ID),
    	        KieContainerMonitorMXBean.class);
    	assertEquals(releaseId1.toExternalForm(), c1Monitor.getConfiguredReleaseIdStr());
    	assertEquals(releaseId1.toExternalForm(), c1Monitor.getResolvedReleaseIdStr());
    	
    	assertTrue(c1Monitor.getConfiguredReleaseId().sameGAVof(releaseId1));
        assertTrue(c1Monitor.getResolvedReleaseId().sameGAVof(releaseId1));
        assertEquals(releaseId1.getVersion(), c1Monitor.getConfiguredReleaseId().getVersion());
        assertEquals(releaseId1.getVersion(), c1Monitor.getResolvedReleaseId().getVersion());
    	
    	KieContainerMonitorMXBean c2Monitor = JMX.newMXBeanProxy(
    			mbserver,
    			DroolsManagementAgent.createObjectNameByContainerId("Matteo"),
    	        KieContainerMonitorMXBean.class);
    	assertEquals(verRelease.toExternalForm(), c2Monitor.getConfiguredReleaseIdStr());
    	assertEquals(releaseId1.toExternalForm(), c2Monitor.getResolvedReleaseIdStr());
    	
        assertTrue(c2Monitor.getConfiguredReleaseId().sameGAVof(verRelease));
        assertTrue(c2Monitor.getResolvedReleaseId().sameGAVof(releaseId1));
        assertEquals(verRelease.getVersion(), c2Monitor.getConfiguredReleaseId().getVersion());
        assertEquals(releaseId1.getVersion(), c2Monitor.getResolvedReleaseId().getVersion());
    }
}
