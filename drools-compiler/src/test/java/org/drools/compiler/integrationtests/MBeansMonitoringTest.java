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
import org.drools.core.ClockType;
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
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.conf.ClockTypeOption;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class MBeansMonitoringTest
        extends CommonTestMethodBase {

    public static final String KBASE1 = "KBase1";
    private KieContainer kc;
    private String mbeansprop;

    @Before
    public void setUp()
            throws Exception {
        mbeansprop = System.getProperty( MBeansOption.PROPERTY_NAME );
        System.setProperty( MBeansOption.PROPERTY_NAME, "enabled" );
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
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel( "KSession1" ).setDefault( true )
                .setType( KieSessionModel.KieSessionType.STATEFUL )
                .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie.test", "mbeans", "1.0.0" );
        createKJar( ks, kproj, releaseId1, null, drl );

        kc = ks.newKieContainer( releaseId1 );
    }

    @After
    public void tearDown()
            throws Exception {
        System.setProperty( MBeansOption.PROPERTY_NAME, mbeansprop );
    }

    @Test
    public void testEventOffset()
            throws InterruptedException,
                   AttributeNotFoundException,
                   InstanceNotFoundException,
                   MalformedObjectNameException,
                   MBeanException,
                   ReflectionException,
                   NullPointerException {

        KieBase kiebase = kc.getKieBase( KBASE1 );
        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        ObjectName kbOn = new ObjectName( "org.drools.kbases:type=" + KBASE1 );
        mbserver.invoke( kbOn, "startInternalMBeans", new Object[0], new String[0] );

        Object expOffset = mbserver.getAttribute( new ObjectName( "org.drools.kbases:type=" + KBASE1 + ",group=EntryPoints,EntryPoint=DEFAULT,ObjectType=org.drools.compiler.StockTick" ), "ExpirationOffset" );
        Assert.assertEquals( 10001, ((Number) expOffset).longValue() );
    }
}
