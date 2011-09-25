package org.drools.timer.integrationtests;

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
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class TimerAndCalendarTest {
    private PoolingDataSource    ds1;
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
        ds1 = new PoolingDataSource();
        ds1.setUniqueName( "jdbc/testDS1" );
        ds1.setClassName( "org.h2.jdbcx.JdbcDataSource" );
        ds1.setMaxPoolSize( 3 );
        ds1.setAllowLocalTransactions( true );
        ds1.getDriverProperties().put( "user",
                                       "sa" );
        ds1.getDriverProperties().put( "password",
                                       "sasa" );
        ds1.getDriverProperties().put( "URL",
                                       "jdbc:h2:mem:mydb" );
        ds1.init();
        emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
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
                System.err.println( "Error: " + error.getMessage() );
            }
            Assert.fail( "KnowledgeBase did not build" );
        }
        Collection<KnowledgePackage> packages = kbuilder.getKnowledgePackages();
        return packages;
    }

}