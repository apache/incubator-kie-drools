package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class StatelessSessionTest extends TestCase {
    final List list = new ArrayList();

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    public void testSingleObjectAssert() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.execute( stilton );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    public void testArrayObjectAssert() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.execute( new Object[]{stilton} );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    public void testCollectionObjectAssert() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        List collection = new ArrayList();
        collection.add( stilton );
        session.execute( collection );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    public void testSingleObjectAssertWithResults() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        StatelessSessionResult result = session.executeWithResults( stilton );

        assertSame( stilton,
                    result.iterateObjects().next() );
    }

    public void testArrayObjectAssertWithResults() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        StatelessSessionResult result = session.executeWithResults( new Object[]{stilton} );

        assertSame( stilton,
                    result.iterateObjects().next() );
    }

    public void testCollectionObjectAssertWithResults() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        List collection = new ArrayList();
        collection.add( stilton );
        StatelessSessionResult result = session.executeWithResults( collection );

        assertSame( stilton,
                    result.iterateObjects().next() );
    }

    public void testAsynSingleOjbectcAssert() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.asyncExecute( stilton );

        Thread.sleep( 100 );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    public void testAsynArrayOjbectcAssert() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.asyncExecute( new Object[]{stilton} );

        Thread.sleep( 100 );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    public void testAsynCollectionOjbectcAssert() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        List collection = new ArrayList();
        collection.add( stilton );
        session.execute( collection );

        Thread.sleep( 100 );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    private StatelessSession getSession() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final StatelessSession session = ruleBase.newStatelessSession();

        session.setGlobal( "list",
                           this.list );
        return session;
    }
}
