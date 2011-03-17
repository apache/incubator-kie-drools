package org.drools.integrationtests.sequential;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.Message;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.conf.Option;
import org.drools.conf.SequentialOption;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.integrationtests.DynamicRulesTest;
import org.drools.integrationtests.SerializationHelper;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.WorkingMemory;

public class SequentialTest {
    //  FIXME lots of XXX tests here, need to find out why.
    
    @Test
    public void testBasicOperation() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "simpleSequential.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( SequentialOption.YES );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase    = SerializationHelper.serializeObject( kbase );
        final StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();

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

        ksession.execute( Arrays.asList( new Object[]{p1, stilton, p2, cheddar, p3} ) );

        assertEquals( 3,
                      list.size() );
    }
    
    @Test
    public void testSalience() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "simpleSalience.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( SequentialOption.YES );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase    = SerializationHelper.serializeObject( kbase );
        final StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();

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
        str += "package org.test\n";
        str +="import org.drools.Message\n";
        str +="rule \"Hello World\"\n";
        str +="when\n";
        str +="    Message( )\n";
        str +="then\n";
        str +="    System.out.println( drools.getKnowledgeRuntime() );\n";
        str +="end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes()), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( SequentialOption.YES );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase    = SerializationHelper.serializeObject( kbase );
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        
        ksession.execute( new Message( "help" ) );
    }
    
    @Test
    public void testEvents() throws Exception {
        String str = "";
        str += "package org.test\n";
        str +="import org.drools.Message\n";
        str +="rule \"Hello World\"\n";
        str +="when\n";
        str +="    Message( )\n";
        str +="then\n";
        str +="    System.out.println( drools.getKnowledgeRuntime() );\n";
        str +="end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes()), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( SequentialOption.YES );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase    = SerializationHelper.serializeObject( kbase );
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        
        final List list = new ArrayList();
        
        ksession.addEventListener( new AgendaEventListener() {

            public void activationCancelled(ActivationCancelledEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                list.add( event );
            }

            public void activationCreated(ActivationCreatedEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                list.add( event );
            }

            public void afterActivationFired(AfterActivationFiredEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                list.add( event );
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                list.add( event );
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                list.add( event );
            }

            public void beforeActivationFired(BeforeActivationFiredEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                list.add( event );
            }

        });
        
        ksession.addEventListener( new WorkingMemoryEventListener() {

            public void objectInserted(ObjectInsertedEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                list.add( event );
            }

            public void objectRetracted(ObjectRetractedEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                list.add( event );
            }

            public void objectUpdated(ObjectUpdatedEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                list.add( event );
            }
            
        });
        
        ksession.execute( new Message( "help" ) );
        
        assertEquals( 4, list.size() );
    }
    

    // JBRULES-1567 - ArrayIndexOutOfBoundsException in sequential execution after calling RuleBase.addPackage(..)
    @Test
    public void testSequentialWithRulebaseUpdate() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "simpleSalience.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( SequentialOption.YES );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase    = SerializationHelper.serializeObject( kbase );
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                           list );

        ksession.execute(new Person("pob"));

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_Dynamic3.drl", DynamicRulesTest.class ), ResourceType.DRL );

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        ksession = kbase.newStatelessKnowledgeSession();
        ksession.setGlobal( "list",
                           list );
        Person  person  = new Person("bop");
        ksession.execute(person);

        assertEquals( 7,
                      list.size() );

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

        runTestProfileManyRulesAndFacts( true,
                                         "Sequential mode",
                                         0, "sequentialProfile.drl"  );
        runTestProfileManyRulesAndFacts( true,
                                         "Sequential mode",
                                         0, "sequentialProfile.drl"  );

        System.gc();
        Thread.sleep( 100 );
    }

    @Test
    public void testProfileRETE() throws Exception {
        runTestProfileManyRulesAndFacts( false,
                                         "Normal RETE mode",
                                         0, "sequentialProfile.drl"  );
        runTestProfileManyRulesAndFacts( false,
                                         "Normal RETE mode",
                                         0, "sequentialProfile.drl"  );

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
                                                 int timetoMeasureIterations, String file) throws DroolsParserException,
                                                                             IOException,
                                                                             Exception {
        // postponed while I sort out KnowledgeHelperFixer
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( file ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        Properties properties = new Properties();
        properties.setProperty( "drools.shadowProxyExcludes",
                                "org.drools.*" );

        RuleBaseConfiguration conf = new RuleBaseConfiguration( properties );
        conf.setSequential( sequentialMode );

        RuleBase ruleBase = getRuleBase( conf );
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final StatelessSession session = ruleBase.newStatelessSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
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
            session.execute( data );
            System.out.println( "Time for " + message + ":" + (System.currentTimeMillis() - start) );
            assertTrue( list.size() > 0 );

        } else {
            //lots of shots
            //test throughput
            long start = System.currentTimeMillis();
            long end = start + timetoMeasureIterations;
            int count = 0;
            while ( System.currentTimeMillis() < end ) {
                StatelessSession sess2 = ruleBase.newStatelessSession();
                List list2 = new ArrayList();
                sess2.setGlobal( "list",
                                 list2 );

                sess2.execute( data );
                //session.execute( data );
                count++;
            }
            System.out.println( "Iterations in for " + message + " : " + count );

        }

    }

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
}
