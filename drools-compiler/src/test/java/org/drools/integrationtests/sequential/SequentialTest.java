package org.drools.integrationtests.sequential;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

public class SequentialTest extends TestCase {
    public void testBasicOperation() throws Exception {

        // postponed while I sort out KnowledgeHelperFixer
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "simpleSequential.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setSequential( true );
        final RuleBase ruleBase = getRuleBase( conf );
        ruleBase.addPackage( pkg );
        final StatelessSession session = ruleBase.newStatelessSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
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

        session.execute( new Object[]{p1, stilton, p2, cheddar, p3} );

        assertEquals( 3,
                      list.size() );

    }

    public void XXtestProfileSequential() throws Exception {

        runTestProfileManyRulesAndFacts( true,
                                         "Sequential mode",
                                         0 );
        runTestProfileManyRulesAndFacts( true,
                                         "Sequential mode",
                                         0 );

        System.gc();
        Thread.sleep( 100 );
    }

    public void XXtestProfileRETE() throws Exception {
        runTestProfileManyRulesAndFacts( false,
                                         "Normal RETE mode",
                                         0 );
        runTestProfileManyRulesAndFacts( false,
                                         "Normal RETE mode",
                                         0 );

        System.gc();
        Thread.sleep( 100 );
    }

    public void testNumberofIterationsSeq() throws Exception {
        //test throughput
        runTestProfileManyRulesAndFacts( true,
                                         "SEQUENTIAL",
                                         2000 );
    }

    public void testNumberofIterationsRETE() throws Exception {
        //test throughput
        runTestProfileManyRulesAndFacts( false,
                                         "RETE",
                                         2000 );

    }

    private void runTestProfileManyRulesAndFacts(boolean sequentialMode,
                                                 String message,
                                                 int timetoMeasureIterations) throws DroolsParserException,
                                                                             IOException,
                                                                             Exception {
        // postponed while I sort out KnowledgeHelperFixer
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "sequentialProfile.drl" ) ) );
        final Package pkg = builder.getPackage();

        Properties properties = new Properties();
        properties.setProperty( "drools.shadowProxyExcludes",
                                "org.drools.*" );

        RuleBaseConfiguration conf = new RuleBaseConfiguration( properties );
        conf.setSequential( sequentialMode );

        final RuleBase ruleBase = getRuleBase( conf );
        ruleBase.addPackage( pkg );
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
