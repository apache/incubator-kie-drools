package org.drools.integrationtests.sequential;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.PersonInterface;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class SequentialTest extends TestCase {
    public void test1() throws Exception {

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
    
    public void FIXME_testProfile() throws Exception {

        // postponed while I sort out KnowledgeHelperFixer
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "sequentialProfile.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setSequential( true );
        final RuleBase ruleBase = getRuleBase( conf );
        ruleBase.addPackage( pkg );
        final StatelessSession session = ruleBase.newStatelessSession();

        
        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        
        Object[] data = new Object[50000];
        for ( int i = 0; i < data.length; i++ ) {
            
            if (i % 2 == 0) {
                final Person p = new Person( "p" + i,
                "stilton" );
                data[i] = p;
            } else {
                    data[i] = new Cheese( "cheddar",
                                i );
            }
        }
        
        long start = System.currentTimeMillis();
        session.execute( data );
        System.out.println("Time for seq:" + (System.currentTimeMillis() - start));
        assertTrue( 
                      list.size() > 0);

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
