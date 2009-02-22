package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.CopyIdentifiersGlobalExporter;
import org.drools.base.MapGlobalResolver;
import org.drools.base.ReferenceOriginalGlobalExporter;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.impl.ParametersImpl;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.runtime.Parameters;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSessionResults;
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
        StatelessKnowledgeSession session = getSession2( "literal_rule_test.drl" );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.executeObject( stilton );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    public void testArrayObjectAssert() throws Exception {
        StatelessKnowledgeSession session = getSession2( "literal_rule_test.drl" );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.executeIterable( Arrays.asList( new Object[]{stilton} ) );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    public void testCollectionObjectAssert() throws Exception {
        StatelessKnowledgeSession session = getSession2( "literal_rule_test.drl" );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        List collection = new ArrayList();
        collection.add( stilton );
        session.executeIterable( collection );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    public void testSingleObjectAssertWithResults() throws Exception {
        StatelessKnowledgeSession session = getSession2( "literal_rule_test.drl" );

        // notice I don't export Cheessery
        Parameters parameters = session.newParameters();
        parameters.getGlobalParams().setOut( Arrays.asList(  new String[]{"list"} ) );        

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        StatelessKnowledgeSessionResults results = session.executeObjectWithParameters( stilton,
                                                                             parameters );
        
        assertEquals( 1, results.getIdentifiers().size() );
        assertTrue( results.getIdentifiers().contains( "list" ));

        assertEquals( "stilton",
                      ((List) results.getValue( "list" )).get( 0 ) );

        // cheesery should be null
        assertNull( results.getValue( "cheesery" ) );
    }

    public void testArrayObjectAssertWithResults() throws Exception {
        StatelessKnowledgeSession session = getSession2( "literal_rule_test.drl" );

        // notice I don't export Cheessery
        Parameters parameters = session.newParameters();
        parameters.getGlobalParams().setOut( Arrays.asList(  new String[]{"list"} ) ); 

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        StatelessKnowledgeSessionResults results = session.executeIterableWithParameters( Arrays.asList( new Object[]{stilton} ),
                                                                             parameters );

        assertEquals( 1, results.getIdentifiers().size() );
        assertTrue( results.getIdentifiers().contains( "list" ));

        assertEquals( "stilton",
                      ((List) results.getValue( "list" )).get( 0 ) );

        // cheesery should be null
        assertNull( results.getValue( "cheesery" ) );
    }

    public void testCollectionObjectAssertWithResults() throws Exception {
        StatelessKnowledgeSession session = getSession2( "literal_rule_test.drl" );

        // notice I don't export Cheessery
        Parameters parameters = session.newParameters();
        parameters.getGlobalParams().setOut( Arrays.asList(  new String[]{"list"} ) ); 

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        List collection = new ArrayList();
        collection.add( stilton );        
        StatelessKnowledgeSessionResults results = session.executeIterableWithParameters( collection,
                                                                                          parameters );        

        assertEquals( 1, results.getIdentifiers().size() );
        assertTrue( results.getIdentifiers().contains( "list" ));

        assertEquals( "stilton",
                      ((List) results.getValue( "list" )).get( 0 ) );

        // cheesery should be null
        assertNull( results.getValue( "cheesery" ) );
    }
    
    public void testInAndOutParams() throws Exception {
        StatelessKnowledgeSession session = getSession2( "testStatelessKnowledgeSessionInOutParams.drl" );


        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        
        final Cheese cheddar = new Cheese( "cheddar",
                                           25 );   
        
        // notice I don't export Cheessery
        Parameters parameters = session.newParameters();
        Map<String, Object> globalsIn = new HashMap<String, Object>();
        globalsIn.put( "inString", "string" );
        parameters.getGlobalParams().setIn( globalsIn );        
        parameters.getGlobalParams().setOut( Arrays.asList(  new String[]{"list"} ) ); 
        
        Map<String, Object> factIn = new HashMap<String, Object>();
        factIn.put( "inCheese", cheddar );
        parameters.getFactParams().setIn( factIn );
        parameters.getFactParams().setOut( Arrays.asList(  new String[]{ "outCheese"} ) );         
 
        StatelessKnowledgeSessionResults results = session.executeObjectWithParameters( stilton,
                                                                                        parameters );        

        assertEquals( 2, results.getIdentifiers().size() );
        assertTrue( results.getIdentifiers().contains( "list" ));
        assertTrue( results.getIdentifiers().contains( "outCheese" ));
        
        assertEquals( new Cheese( "brie", 50), results.getValue( "outCheese" ) );

        assertEquals( "rule1 cheddar",
                      ((List) results.getValue( "list" )).get( 0 ) );
        
        assertEquals( "rule2 stilton",
                      ((List) results.getValue( "list" )).get( 1 ) );      
        
        assertEquals( "rule3 brie",
                      ((List) results.getValue( "list" )).get( 2 ) );         
        
        assertEquals( "rule4 string",
                      ((List) results.getValue( "list" )).get( 3 ) );          

        // cheesery should be null
        assertNull( results.getValue( "cheesery" ) );        
        
    }
    
    public void testInOutAndOutParams() throws Exception {
        StatelessKnowledgeSession session = getSession2( "testStatelessKnowledgeSessionInOutParams.drl" );


        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        
        final Cheese cheddar = new Cheese( "cheddar",
                                           25 );   
        
        // notice I don't export Cheessery
        Parameters parameters = session.newParameters();
        Map<String, Object> globalsInOut = new HashMap<String, Object>();
        globalsInOut.put( "inString", "string" );
        parameters.getGlobalParams().setInOut( globalsInOut );        
        parameters.getGlobalParams().setOut( Arrays.asList(  new String[]{"list"} ) ); 
        
        Map<String, Object> factInOut = new HashMap<String, Object>();
        factInOut.put( "inCheese", cheddar );
        parameters.getFactParams().setInOut( factInOut );
        parameters.getFactParams().setOut( Arrays.asList(  new String[]{ "outCheese"} ) );         
 
        StatelessKnowledgeSessionResults results = session.executeObjectWithParameters( stilton,
                                                                                        parameters );        

        assertEquals( 4, results.getIdentifiers().size() );
        assertTrue( results.getIdentifiers().contains( "list" ));
        assertTrue( results.getIdentifiers().contains( "inString" ));
        assertTrue( results.getIdentifiers().contains( "inCheese" ));
        assertTrue( results.getIdentifiers().contains( "outCheese" ));
        
        assertEquals( new Cheese( "brie", 50), results.getValue( "outCheese" ) );

        assertEquals( "rule1 cheddar",
                      ((List) results.getValue( "list" )).get( 0 ) );
        
        assertEquals( "rule2 stilton",
                      ((List) results.getValue( "list" )).get( 1 ) );      
        
        assertEquals( "rule3 brie",
                      ((List) results.getValue( "list" )).get( 2 ) );         
        
        assertEquals( "rule4 string",
                      ((List) results.getValue( "list" )).get( 3 ) );          

        // cheesery should be null
        assertNull( results.getValue( "cheesery" ) );        
        
    }    
    
    public void testAsynSingleOjbectcAssert() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.asyncExecute( stilton );

        Thread.sleep( 300 );

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
    
    private StatelessKnowledgeSession getSession2(String fileName) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(  ResourceFactory.newClassPathResource( fileName, getClass() ), ResourceType.DRL );
        
        if (kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
        }
        
        assertFalse( kbuilder.hasErrors() );
        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        
       
        kbase.addKnowledgePackages( pkgs );
        kbase    = SerializationHelper.serializeObject( kbase );
        StatelessKnowledgeSession session = kbase.newStatelessKnowledgeSession();

        session.setGlobal( "list",
                           this.list );
        session.setGlobal( "cheesery",
                           this.cheesery );
        return session;
    }    
}
