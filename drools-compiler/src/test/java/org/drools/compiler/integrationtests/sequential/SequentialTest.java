package org.drools.compiler.integrationtests.sequential;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.drools.compiler.Person;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.integrationtests.DynamicRulesTest;
import org.drools.core.util.IoUtils;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.conf.SequentialOption;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SequentialTest extends CommonTestMethodBase {

    private KieBaseConfiguration kconf;

    @Before
    public void setup() {
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( phreak );
        kconf.setOption( SequentialOption.YES );
    }

    @Test
    public void testBasicOperation() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase(kconf, "simpleSequential.drl");
        StatelessKnowledgeSession ksession = createStatelessKnowledgeSession( kbase );
        final List list = new ArrayList();
        ksession.setGlobal( "list",
                           list );

        final Person p1 = new Person( "p1",
                                      "stilton" );
        final Person p2 = new Person( "p2",
                                      "cheddar" );
        final Person p3 = new Person( "p3",
                                      "stilton" );

        final Cheese stilton = new Cheese( "stilton",
                                           15 );
        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );


        ksession.execute( CommandFactory.newInsertElements(Arrays.asList( new Object[]{p1, stilton, p2, cheddar, p3} )) );

        assertEquals( 3,
                      list.size() );
    }
    
    @Test
    public void testSalience() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase(kconf, "simpleSalience.drl");
        StatelessKnowledgeSession ksession = createStatelessKnowledgeSession( kbase );

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                           list );

        ksession.execute( new Person( "pob")  );

        assertEquals( 3,
                      list.size() );
        
        assertEquals( "rule 3", list.get( 0 ));
        assertEquals( "rule 2", list.get( 1 ));
        assertEquals( "rule 1", list.get( 2 ));
    }
    
    @Test
    public void testKnowledgeRuntimeAccess() throws Exception {
        String str = "";
        str += "package org.drools.compiler.test\n";
        str +="import org.drools.compiler.Message\n";
        str +="rule \"Hello World\"\n";
        str +="when\n";
        str +="    Message( )\n";
        str +="then\n";
        str +="    System.out.println( drools.getKieRuntime() );\n";
        str +="end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(kconf, str);
        StatelessKnowledgeSession ksession = createStatelessKnowledgeSession( kbase );
        
        ksession.execute( new Message( "help" ) );
    }
    
    @Test
    public void testEvents() throws Exception {
        String str = "";
        str += "package org.drools.compiler.test\n";
        str +="import org.drools.compiler.Message\n";
        str +="rule \"Hello World\"\n";
        str +="when\n";
        str +="    Message( )\n";
        str +="then\n";
        str +="    System.out.println( drools.getKieRuntime() );\n";
        str +="end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(kconf, str);
        StatelessKnowledgeSession ksession = createStatelessKnowledgeSession( kbase );
        
        final List list = new ArrayList();
        
        ksession.addEventListener( new AgendaEventListener() {

            public void matchCancelled(MatchCancelledEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );
            }

            public void matchCreated(MatchCreatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );
            }

            public void afterMatchFired(AfterMatchFiredEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );
            }

            public void beforeMatchFired(BeforeMatchFiredEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );
            }

            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );    
            }

            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );  
            }

            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event ); 
            }

            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );
            }

        });
        
        ksession.addEventListener( new RuleRuntimeEventListener() {

            public void objectInserted(ObjectInsertedEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );
            }

            public void objectDeleted(ObjectDeletedEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );
            }

            public void objectUpdated(ObjectUpdatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                list.add( event );
            }
            
        });
        
        ksession.execute( new Message( "help" ) );
        
        assertEquals( 4, list.size() );
    }
    

    // JBRULES-1567 - ArrayIndexOutOfBoundsException in sequential execution after calling RuleBase.addPackage(..)
    @Test
    public void testSequentialWithRulebaseUpdate() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase(kconf, "simpleSalience.drl");
        StatelessKnowledgeSession ksession = createStatelessKnowledgeSession( kbase );

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.execute(new Person("pob"));

        kbase.addKnowledgePackages(loadKnowledgePackagesFromString( new String( IoUtils.readBytesFromInputStream( DynamicRulesTest.class.getResource("test_Dynamic3.drl").openStream() ) ) ) );

        ksession = kbase.newStatelessKnowledgeSession();
        ksession.setGlobal( "list", list );
        Person person  = new Person("bop");
        ksession.execute(person);

        assertEquals( 7, list.size() );

        assertEquals( "rule 3", list.get( 0 ));
        assertEquals( "rule 2", list.get( 1 ));
        assertEquals( "rule 1", list.get( 2 ));
        assertEquals( "rule 3", list.get( 3 ));
        assertEquals( "rule 2", list.get( 4 ));
        assertEquals( "rule 1", list.get( 5 ));
        assertEquals( person, list.get( 6 ));
    }

    @Test
    public void testProfileSequential() throws Exception {

        runTestProfileManyRulesAndFacts( true, "Sequential mode", 0, "sequentialProfile.drl"  );
        runTestProfileManyRulesAndFacts( true, "Sequential mode", 0, "sequentialProfile.drl"  );

        System.gc();
        Thread.sleep( 100 );
    }

    @Test
    public void testProfileRETE() throws Exception {
        runTestProfileManyRulesAndFacts( false, "Normal RETE mode", 0, "sequentialProfile.drl"  );
        runTestProfileManyRulesAndFacts( false, "Normal RETE mode", 0, "sequentialProfile.drl"  );

        System.gc();
        Thread.sleep( 100 );
    }

    @Test
    public void testNumberofIterationsSeq() throws Exception {
        //test throughput
        runTestProfileManyRulesAndFacts( true,
                                         "SEQUENTIAL",
                                         2000, "sequentialProfile.drl"  );
    }

    @Test
    public void testNumberofIterationsRETE() throws Exception {
        //test throughput
        runTestProfileManyRulesAndFacts( false,
                                         "RETE",
                                         2000, "sequentialProfile.drl"  );

    }

    @Test
    public void testPerfJDT() throws Exception {
        runTestProfileManyRulesAndFacts( true,
                                         "JDT",
                                         2000, "sequentialProfile.drl"  );
        
    }

    @Test
    public void testPerfMVEL() throws Exception {
        runTestProfileManyRulesAndFacts( true,
                                         "MVEL",
                                         2000, "sequentialProfileMVEL.drl"  );
        
    }


    private void runTestProfileManyRulesAndFacts(boolean sequentialMode,
                                                 String message,
                                                 int timetoMeasureIterations,
                                                 String file) throws DroolsParserException, IOException, Exception {
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        if ( sequentialMode ) {
            kconf.setOption( RuleEngineOption.RETEOO );
            kconf.setOption( SequentialOption.YES );
        }   else {
            kconf.setOption( SequentialOption.NO );
        }

        KnowledgeBase kbase = loadKnowledgeBase(kconf, file);
        StatelessKnowledgeSession ksession = createStatelessKnowledgeSession( kbase );

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                           list );

        Object[] data = new Object[50000];
        for ( int i = 0; i < data.length; i++ ) {

            if ( i % 2 == 0 ) {
                final Person p = new Person( "p" + i,
                                             "stilton" );
                data[i] = p;
            } else {
                data[i] = new Cheese( "cheddar",
                                      i );
            }
        }

        if ( timetoMeasureIterations == 0 ) {
            //one shot measure
            long start = System.currentTimeMillis();
            ksession.execute( CommandFactory.newInsertElements(Arrays.asList(data)));
            System.out.println( "Time for " + message + ":" + (System.currentTimeMillis() - start) );
            assertTrue( list.size() > 0 );

        } else {
            //lots of shots
            //test throughput
            long start = System.currentTimeMillis();
            long end = start + timetoMeasureIterations;
            int count = 0;
            while ( System.currentTimeMillis() < end ) {
                StatelessKnowledgeSession sess2 = createStatelessKnowledgeSession( kbase );
                List list2 = new ArrayList();
                sess2.setGlobal( "list",
                                 list2 );

                sess2.execute( CommandFactory.newInsertElements(Arrays.asList(data)));
                //session.execute( data );
                count++;
            }
            System.out.println( "Iterations in for " + message + " : " + count );

        }

    }

}
