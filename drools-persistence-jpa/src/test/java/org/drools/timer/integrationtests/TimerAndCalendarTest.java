/*
 * Copyright 2011 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.timer.integrationtests;

import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.ClockType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.type.FactType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.util.MarshallingTestUtil;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.SessionPseudoClock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class TimerAndCalendarTest {
    private static Logger logger = LoggerFactory.getLogger(TimerAndCalendarTest.class);
    
    private EntityManagerFactory emf;

    @Test
    public void testTimerRuleAfterIntReloadSession() throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = createSession( kbase );

        // must advance time or it won't save.
        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        // if we do not call 'ksession.fireAllRules()', this test will run successfully.
        ksession.fireAllRules();

        clock = ksession.getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );

        clock = ksession.getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        // build timer rule, if the rule is fired, the list size will increase every 500ms
        String timerRule = "package org.drools.test\n" +
                           "global java.util.List list \n" +
                           "rule TimerRule \n" +
                           "   timer (int:1000 500) \n" +
                           "when \n" + "then \n" +
                           "        list.add(list.size()); \n" +
                           " end";
        Resource resource = ResourceFactory.newByteArrayResource( timerRule.getBytes() );
        Collection<KnowledgePackage> kpackages = buildKnowledgePackage( resource,
                                                                        ResourceType.DRL );
        kbase.addKnowledgePackages( kpackages );

        clock = ksession.getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );
        ksession.fireAllRules();

        clock = ksession.getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );

        clock = ksession.getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );

        List<Integer> list = Collections.synchronizedList( new ArrayList<Integer>() );
        ksession.setGlobal( "list",
                            list );
        Assert.assertEquals( 0,
                             list.size() );

        clock = ksession.getSessionClock();
        clock.advanceTime( 1700,
                           TimeUnit.MILLISECONDS );
        Assert.assertEquals( 2,
                             list.size() );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );
        ksession.setGlobal( "list",
                            list );

        clock = ksession.getSessionClock();
        clock.advanceTime( 1000,
                           TimeUnit.MILLISECONDS );

        // if the rule is fired, the list size will greater than one.
        Assert.assertEquals( 4,
                             list.size() );
    }

    @Test
    public void testTimerRuleAfterCronReloadSession() throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = createSession( kbase );

        // must advance time or it won't save.
        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        // if we do not call 'ksession.fireAllRules()', this test will run successfully.
        ksession.fireAllRules();

        clock = ksession.getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );

        clock = ksession.getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        // build timer rule, if the rule is fired, the list size will increase every 300ms
        String timerRule = "package org.drools.test\n" +
                           "global java.util.List list \n" +
                           "rule TimerRule \n" +
                           "   timer (cron: * * * * * ?) \n" +
                           "when \n" + "then \n" +
                           "        list.add(list.size()); \n" +
                           " end";
        Resource resource = ResourceFactory.newByteArrayResource( timerRule.getBytes() );
        Collection<KnowledgePackage> kpackages = buildKnowledgePackage( resource,
                                                                        ResourceType.DRL );
        kbase.addKnowledgePackages( kpackages );

        List<Integer> list = Collections.synchronizedList( new ArrayList<Integer>() );
        ksession.setGlobal( "list",
                            list );

        ksession.setGlobal( "list",
                            list );
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );
        ksession.fireAllRules();

        clock = ksession.getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );
        ksession.setGlobal( "list",
                            list );

        clock = ksession.getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );

        Assert.assertEquals( 1,
                             list.size() );

        clock = ksession.getSessionClock();
        clock.advanceTime( 3,
                           TimeUnit.SECONDS );
        Assert.assertEquals( 4,
                             list.size() );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );
        ksession.setGlobal( "list",
                            list );

        clock = ksession.getSessionClock();
        clock.advanceTime( 2,
                           TimeUnit.SECONDS );

        // if the rule is fired, the list size will greater than one.
        Assert.assertEquals( 6,
                             list.size() );
    }

    @Test
    public void testEventExpires() throws Exception {
        String timerRule = "package org.drools.test\n" +
                           "declare TestEvent \n" +
                           "    @role( event )\n" +
                           "    @expires( 10s )\n" +
                           "end\n" +
                           "" +
                           "rule TimerRule \n" +
                           "    when \n" +
                           "        TestEvent( ) from entry-point \"Test\"\n" +
                           "    then \n" +
                           "end";
        KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbconf );
        Resource resource = ResourceFactory.newByteArrayResource( timerRule.getBytes() );
        Collection<KnowledgePackage> kpackages = buildKnowledgePackage( resource,
                                                                        ResourceType.DRL );
        kbase.addKnowledgePackages( kpackages );
        StatefulKnowledgeSession ksession = createSession( kbase );

        FactType type = kbase.getFactType( "org.drools.test",
                                           "TestEvent" );
        Assert.assertNotNull( "could not get type",
                              type );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );
        ksession.getWorkingMemoryEntryPoint( "Test" ).insert( type.newInstance() );
        ksession.fireAllRules();
        ksession = disposeAndReloadSession( ksession,
                                            kbase );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );
    }

    @Before
    public void setUp() throws Exception {
        testContext = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME);
        emf = (EntityManagerFactory) testContext.get(PersistenceUtil.ENTITY_MANAGER_FACTORY);
    }

    @After
    public void tearDown() throws Exception {
        try {
            emf.close();
            ds1.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void compareMarshallingData() { 
        MarshallingTestUtil.compareMarshallingDataFromTest(DROOLS_PERSISTENCE_UNIT_NAME);
    }
    
    private StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
        final KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase,
                                                                                             conf,
                                                                                             createEnvironment() );
        return ksession;
    }

    private StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession ksession,
                                                             KnowledgeBase kbase) {
        int ksessionId = ksession.getId();
        ksession.dispose();

        final KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession newksession = JPAKnowledgeService.loadStatefulKnowledgeSession( ksessionId,
                                                                                                 kbase,
                                                                                                 conf,
                                                                                                 createEnvironment() );
        return newksession;
    }

    private Environment createEnvironment() {
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        // env.set(EnvironmentName.TRANSACTION_MANAGER,
        // TransactionManagerServices.getTransactionManager());
        env.set( EnvironmentName.GLOBALS,
                 new MapGlobalResolver() );
        return env;
    }

    private Collection<KnowledgePackage> buildKnowledgePackage(Resource resource,
                                                               ResourceType resourceType) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( resource,
                      resourceType );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors != null && errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                logger.error( "Error: " + error.getMessage() );
            }
            Assert.fail( "KnowledgeBase did not build" );
        }
        Collection<KnowledgePackage> packages = kbuilder.getKnowledgePackages();
        return packages;
    }

}
