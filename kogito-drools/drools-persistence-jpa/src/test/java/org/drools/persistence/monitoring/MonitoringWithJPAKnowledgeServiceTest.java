/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.persistence.monitoring;

import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.MBeansOption;
import org.kie.api.management.KieContainerMonitorMXBean;
import org.kie.api.management.KieSessionMonitoringMXBean;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;

import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;
import static org.junit.Assert.assertEquals;

public class MonitoringWithJPAKnowledgeServiceTest {
    private static Logger LOG = LoggerFactory.getLogger(MonitoringWithJPAKnowledgeServiceTest.class);
    
    private Map<String, Object> context;
    private Environment env;
    
    public MonitoringWithJPAKnowledgeServiceTest() { 
    }
    
    private String mbeansprop;

    @Before
    public void setUp() throws Exception {
        ((KieServicesImpl) KieServices.Factory.get()).nullKieClasspathContainer();
        ((KieServicesImpl) KieServices.Factory.get()).nullAllContainerIds();
        mbeansprop = System.getProperty( MBeansOption.PROPERTY_NAME );
        System.setProperty( MBeansOption.PROPERTY_NAME, "enabled" );    
        
        context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
    }
        
    @After
    public void tearDown() throws Exception {
        ((KieServicesImpl) KieServices.Factory.get()).nullKieClasspathContainer();
        ((KieServicesImpl) KieServices.Factory.get()).nullAllContainerIds();
        if (mbeansprop != null) {
            System.setProperty( MBeansOption.PROPERTY_NAME, mbeansprop );
        } else {
            System.setProperty( MBeansOption.PROPERTY_NAME, MBeansOption.DISABLED.toString() );
        }
        
        DroolsPersistenceUtil.cleanUp(context);
    }


    @Test
    public void testBasic() throws MalformedObjectNameException {
        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        KieServices ks = KieServices.Factory.get();

        String containerId = "testcontainer-"+System.currentTimeMillis();
        KieContainer kc = ks.newKieClasspathContainer(containerId);
        
        KieContainerMonitorMXBean c1Monitor = JMX.newMXBeanProxy(
                mbserver,
                DroolsManagementAgent.createObjectNameBy(containerId),
                KieContainerMonitorMXBean.class);
        KieBase kb = kc.getKieBase("org.kie.monitoring.kbase1");

        // Use JPAKnowledgeService to create the KieSession 
        StatefulKnowledgeSession statefulKieSession = JPAKnowledgeService.newStatefulKnowledgeSession(kb, null, env);
        long sessionIdentifier = statefulKieSession.getIdentifier();
        
        statefulKieSession.insert("String1");
        statefulKieSession.fireAllRules();
        
        KieSessionMonitoringMXBean statefulKieSessionMonitor = JMX.newMXBeanProxy(
                mbserver,
                DroolsManagementAgent.createObjectNameBy(containerId, "org.kie.monitoring.kbase1", KieSessionType.STATEFUL, "persistent"),
                KieSessionMonitoringMXBean.class);
        assertEquals(1, statefulKieSessionMonitor.getTotalMatchesFired() );
        
        // There should be 3 mbeans for KieContainer, KieBase and KieSession.
        assertEquals(3, mbserver.queryNames(new ObjectName("org.kie:kcontainerId="+ObjectName.quote(containerId)+",*"), null).size());
        
        // needs to be done separately:
        statefulKieSession.dispose();
        
        StatefulKnowledgeSession deserialized = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionIdentifier, kb, null, createEnvironment(context));
        deserialized.insert("String2");
        deserialized.fireAllRules();
        
        // the mbean does not persist state, but in this case consolidate by grouping the fire of the former session and the deserialized one 
        assertEquals(2, statefulKieSessionMonitor.getTotalMatchesFired() );
        
        kc.dispose();
    }

}
