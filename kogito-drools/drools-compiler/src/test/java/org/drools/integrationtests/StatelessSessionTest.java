package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.CopyIdentifiersGlobalExporter;
import org.drools.base.MapGlobalResolver;
import org.drools.base.ReferenceOriginalGlobalExporter;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.drools.spi.GlobalResolver;

public class StatelessSessionTest extends TestCase {
    final List list = new ArrayList();
    final Cheesery cheesery = new Cheesery();
    final GlobalResolver globalResolver = new MapGlobalResolver();

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

        // notice I don't export Cheessery
        session.setGlobalExporter( new CopyIdentifiersGlobalExporter( new String[]{"list"} ) );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        StatelessSessionResult result = session.executeWithResults( stilton );

        assertSame( stilton,
                    result.iterateObjects().next() );

        assertEquals( "stilton",
                      ((List) result.getGlobal( "list" )).get( 0 ) );

        // cheesery should be null
        assertNull( result.getGlobal( "cheesery" ) );
    }

    public void testArrayObjectAssertWithResults() throws Exception {
        StatelessSession session = getSession();

        // notice I don't export Cheessery
        session.setGlobalExporter( new CopyIdentifiersGlobalExporter( new String[]{"list"} ) );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        StatelessSessionResult result = session.executeWithResults( new Object[]{stilton} );

        assertSame( stilton,
                    result.iterateObjects().next() );

        assertEquals( "stilton",
                      ((List) result.getGlobal( "list" )).get( 0 ) );

        // cheesery should be null
        assertNull( result.getGlobal( "cheesery" ) );
    }

    public void testCollectionObjectAssertWithResults() throws Exception {
        StatelessSession session = getSession();

        // notice I don't export Cheessery
        session.setGlobalExporter( new CopyIdentifiersGlobalExporter( new String[]{"list"} ) );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        List collection = new ArrayList();
        collection.add( stilton );
        StatelessSessionResult result = session.executeWithResults( collection );

        assertSame( stilton,
                    result.iterateObjects().next() );

        assertEquals( "stilton",
                      ((List) result.getGlobal( "list" )).get( 0 ) );

        // cheesery should be null
        assertNull( result.getGlobal( "cheesery" ) );
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
    
    public void testCopyIdentifierGlobalExporterOneValue() throws Exception {
        StatelessSession session = getSession();       

        // notice I don't export Cheessery
        session.setGlobalExporter( new CopyIdentifiersGlobalExporter( new String[]{"list"} ) );

        StatelessSessionResult result = session.executeWithResults( (Object) null );

        assertSame( this.list,
                    result.getGlobal( "list" ) );

        // cheesery should be null
        assertNull( result.getGlobal( "cheesery" ) );
        
        assertNotSame( this.globalResolver, result.getGlobalResolver() );
    }
    
    public void testCopyIdentifierGlobalExporterTwoValues() throws Exception {
        StatelessSession session = getSession();

        session.setGlobalExporter( new CopyIdentifiersGlobalExporter( new String[]{"list", "cheesery"} ) );

        StatelessSessionResult result = session.executeWithResults( (Object) null );

        assertSame( this.list,
                    result.getGlobal( "list" ) );

        // cheesery should be null
        assertSame( this.cheesery,
                    result.getGlobal( "cheesery" ) );
        
        assertNotSame( this.globalResolver, result.getGlobalResolver() );        
    }    
    
    public void testCopyIdentifierGlobalExporterAllValues() throws Exception {
        StatelessSession session = getSession();

        // I've not specified any identifiers, so it should do them alll
        session.setGlobalExporter( new CopyIdentifiersGlobalExporter() );

        StatelessSessionResult result = session.executeWithResults( (Object) null );

        assertSame( this.list,
                    result.getGlobal( "list" ) );

        // cheesery should be null
        assertSame( this.cheesery,
                    result.getGlobal( "cheesery" ) );
        
        assertNotSame( this.globalResolver, result.getGlobalResolver() );        
    }     
    
    public void testReferenceOriginalGlobalExporter() throws Exception {
        StatelessSession session = getSession();

        // I've not specified any identifiers, so it should do them alll
        session.setGlobalExporter( new ReferenceOriginalGlobalExporter() );

        StatelessSessionResult result = session.executeWithResults( (Object) null );

        assertSame( this.list,
                    result.getGlobal( "list" ) );

        // cheesery should be null
        assertSame( this.cheesery,
                    result.getGlobal( "cheesery" ) );
        
        assertSame( this.globalResolver, result.getGlobalResolver() );        
    }        

    private StatelessSession getSession() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        StatelessSession session = ruleBase.newStatelessSession();
        
        session    = SerializationHelper.serializeObject(session);
        session.setGlobalResolver( this.globalResolver );

        session.setGlobal( "list",
                           this.list );
        session.setGlobal( "cheesery",
                           this.cheesery );
        return session;
    }
}
