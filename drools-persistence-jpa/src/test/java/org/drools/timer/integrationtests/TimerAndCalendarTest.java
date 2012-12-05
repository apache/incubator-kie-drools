package org.drools.timer.integrationtests;

import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;
import static org.drools.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.drools.persistence.util.PersistenceUtil.tearDown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;

import org.drools.ClockType;
import org.drools.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderErrors;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.conf.EventProcessingOption;
import org.kie.definition.KnowledgePackage;
import org.kie.definition.type.FactType;
import org.kie.io.Resource;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.conf.ClockTypeOption;
import org.kie.time.SessionClock;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class TimerAndCalendarTest {
    private PoolingDataSource    ds1;
    private EntityManagerFactory emf;

    @Test
    public void testTimerRuleAfterIntReloadSession() throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = createSession( kbase );

        // must advance time or it won't save.
        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        // if we do not call 'ksession.fireAllRules()', this test will run successfully.
        ksession.fireAllRules();

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
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

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );
        ksession.fireAllRules();

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );

        List<Integer> list = Collections.synchronizedList( new ArrayList<Integer>() );
        ksession.setGlobal( "list",
                            list );
        Assert.assertEquals( 0,
                             list.size() );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 1700,
                           TimeUnit.MILLISECONDS );
        Assert.assertEquals( 2,
                             list.size() );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );
        ksession.setGlobal( "list",
                            list );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
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
        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        // if we do not call 'ksession.fireAllRules()', this test will run successfully.
        ksession.fireAllRules();

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 300,
                           TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
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

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );
        ksession.setGlobal( "list",
                            list );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 10,
                           TimeUnit.MILLISECONDS );

        Assert.assertEquals( 1,
                             list.size() );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 3,
                           TimeUnit.SECONDS );
        Assert.assertEquals( 4,
                             list.size() );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );
        ksession.setGlobal( "list",
                            list );

        clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
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

    private HashMap<String, Object> context;
    
    @Before
    public void before() throws Exception {
        context = setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
    }

    @After
    public void after() throws Exception {
        tearDown(context);
    }

    private StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
        final KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase,
                                                                                             conf,
                                                                                             createEnvironment(context) );
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
                                                                                                 createEnvironment(context) );
        return newksession;
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
